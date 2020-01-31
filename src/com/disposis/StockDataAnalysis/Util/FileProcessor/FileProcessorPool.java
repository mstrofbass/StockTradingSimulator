/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Util.FileProcessor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author blake
 */
public class FileProcessorPool {
    
    BlockingQueue<FileProcessor> queue = new LinkedBlockingQueue<>();
    Set<FileProcessor> fpPool = new HashSet<>();
    
    public FileProcessorPool( int count )
    {
        Thread thread;
        FileProcessor fileProcessor;
        
        for ( int i = 0 ; i < count ; i++ )
        {            
            fileProcessor = new FileProcessor( queue );
            
            fpPool.add(fileProcessor);
            queue.add(fileProcessor);
            
            thread = new Thread( fileProcessor );
            thread.start();
        }
    }
    
    public BlockingQueue<FileProcessor> get()
    {
        return queue;
    }
    
    public void shutdown()
    {
        for ( FileProcessor fp : fpPool )
        {
            fp.shutdown();
        }
    }
}
