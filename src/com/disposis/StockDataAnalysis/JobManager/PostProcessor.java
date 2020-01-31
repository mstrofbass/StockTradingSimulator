/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.JobManager;

import com.disposis.StockDataAnalysis.SimulateTrades.ProgressLog;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.StockDataAnalysis.Util.FileProcessor.FileProcessor;
import com.disposis.StockDataAnalysis.Util.FileProcessor.FileProcessorPool;
import com.disposis.util.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.utils.IOUtils;

/**
 *
 * @author blake
 */
public class PostProcessor {
    
    protected static void simulationPostProcessing() throws Exception
    {
        Configuration conf = Configuration.getInstance();
        Logger logger = Logger.getInstance( conf );
        
        File outputDir = Paths.get( conf.getOutputDir() ).toFile();
        
        File[] files = outputDir.listFiles(
            (File dir, String name) -> name.toLowerCase().startsWith("option-trans") && name.toLowerCase().endsWith(".csv") 
        );
        
        ProgressLog progressLog = getProgressLog();
        
        if ( progressLog.filesRemainingToCompress == null )
        {
            progressLog.filesRemainingToCompress = new HashSet<>();
            
            for ( int i = 0 ; i < files.length ; i++ )
            {
                progressLog.filesRemainingToCompress.add( files[i] );
            }
            
            progressLog.save();
        }
        
        Set<File> remaining = new HashSet<>(progressLog.filesRemainingToCompress);
        
        FileProcessorPool fileProcessors = new FileProcessorPool( conf.getMaxThreads() );
        BlockingQueue<FileProcessor> fpQueue = fileProcessors.get();
        
        FileProcessor fileProcessor;
        
        for ( File file : remaining )
        {
            File inFile = file;
            File outFile = new File( file.getAbsolutePath() + ".gz" );
            
            fileProcessor = fpQueue.take();
            
            fileProcessor.addFileToProcess(inFile, outFile, ( inputFile, outputFile ) -> {
                logger.console( "StockDataAnalysis::simulationPostProcessing() - Zipping file %s.", file.getName() );
                
                try ( FileOutputStream fileOutputStream = new FileOutputStream( outputFile ); GZIPOutputStream gzipOutputStream = new GZIPOutputStream( fileOutputStream ) ) {
                    FileInputStream fis = new FileInputStream( inputFile );

                    IOUtils.copy(fis, gzipOutputStream);

                    progressLog.filesRemainingToCompress.remove( file );
                    progressLog.save();

                    logger.console("StockDataAnalysis::simulationPostProcessing() - Done zipping file %s. Files remaining: %s", file.getName(), progressLog.filesRemainingToCompress.size() );
                }
                catch ( IOException e )
                {
                    logger.console("StockDataAnalysis::simulationPostProcessing() - Caught exception while trying to gzip output files. Msg: " + e.toString() );
                } catch (Exception e) {
                    logger.console("StockDataAnalysis::simulationPostProcessing() - Caught exception while trying to gzip output files. Msg: " + e.toString() );
                }
            });
        }
        
        fileProcessors.shutdown();
    }
    
    protected static ProgressLog getProgressLog() throws Exception
    {
        ProgressLog progressLog = ProgressLog.load();
        
        if ( progressLog == null )
        {
            progressLog = new ProgressLog();
            progressLog.save();
        }
        
        return progressLog;
    }
}
