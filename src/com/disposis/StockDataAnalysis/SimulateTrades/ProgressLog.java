/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.SimulateTrades;

import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.trading.instruments.Stock;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author blake
 */
public class ProgressLog implements java.io.Serializable {
    public Set<Stock> stocksProcessed = new HashSet<>();
    public Set<Stock> stocksRemaining = new HashSet<>();
    public Set<File> filesRemainingToCompress = null;
    
    public static ProgressLog load()
    {
        Configuration conf = Configuration.getInstance();
        Path logPath = conf.getProgressLogOutputFilePath();
        
        if ( !Files.exists( logPath ) )
        {
            return null;
        }
        
        try {
            FileInputStream fileIn = new FileInputStream( Paths.get( conf.getOutputDir(), "progress.log").toFile() );
            ObjectInputStream progressReader = new ObjectInputStream(fileIn);
            
            ProgressLog plog = (ProgressLog) progressReader.readObject();
            
            return plog;
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        } 
        
        return null;
    }
    
    public void save() throws Exception
    {
        Configuration conf = Configuration.getInstance();
        Path logPath = conf.getProgressLogOutputFilePath();
           
        Files.deleteIfExists(logPath);

        try (FileOutputStream fileOut = new FileOutputStream( logPath.toFile() ); ObjectOutputStream progressWriter = new ObjectOutputStream(fileOut)) {
            
            progressWriter.writeObject( this );
            
        }
    }
}
