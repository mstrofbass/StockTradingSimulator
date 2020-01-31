/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Util.FileProcessor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author blake
 */
public class FileProcessor implements Runnable 
{
    BlockingQueue<FileProcessor> threadPool;

    protected boolean run = true;
    
    protected final BlockingQueue<File> inputFilesToProcess = new LinkedBlockingQueue<>();
    protected final Map<File, File> outputFilesToProcess = new HashMap<>();
    protected final Map<File, FileProcessorFunction> funcsForProcessing = new HashMap<>();

    public FileProcessor( BlockingQueue<FileProcessor> threadPool )
    {
        this.threadPool = threadPool;
    }

    @Override
    public void run() {

        Map<File, FileProcessorFunction> fileProcessors;

        File inputFileToProcess;
        File outputFileToProcess;
        FileProcessorFunction func;
        
        while ( run )
        {
            try {
                
                inputFileToProcess = inputFilesToProcess.poll(500, TimeUnit.MILLISECONDS);

                if ( inputFileToProcess != null )
                {
                    synchronized( inputFilesToProcess )
                    {
                        outputFileToProcess = outputFilesToProcess.get(inputFileToProcess);
                        func = funcsForProcessing.get(inputFileToProcess);
                    }
                    
                    func.process(inputFileToProcess, outputFileToProcess);
                    
                    threadPool.add(this);
                }

            } catch (InterruptedException ex) {

            }
        }
    }
    
    public void addFileToProcess( File inputFile, File outputFile, FileProcessorFunction func )
    {
        boolean inserted = false;
       
        while ( !inserted ) 
        {
            try {
                synchronized ( inputFilesToProcess )
                {
                    inputFilesToProcess.put(inputFile);
                    outputFilesToProcess.put(inputFile, outputFile);
                    funcsForProcessing.put(inputFile, func);
                }
                
                inserted = true;
            } catch (InterruptedException ex) {
                
            }
        }
    }

    public void shutdown()
    {
        this.run = false;

        synchronized( inputFilesToProcess )
        {
            inputFilesToProcess.notifyAll();
        }
    }
}
