package com.disposis.StockDataAnalysis;

import com.disposis.StockDataAnalysis.JobManager.JobManager;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.util.Logger;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class StockDataAnalysis {
    
    protected static Logger logger = Logger.getInstance( Configuration.getInstance() );
    
    protected static ExecutorService jobService = Executors.newSingleThreadExecutor();
    protected static CompletionService<Object> completionService = new ExecutorCompletionService<>(jobService);
	
	public static void main(String[] args) throws Exception {
        
        JobManager jm = new JobManager();
        jm.run();
        
//        try {
//            Object res = completionService.take().get();
//        }
//        catch ( InterruptedException | ExecutionException e )
//        {
//            logger.console( "StockDataAnalysis::main() - Exception caught from the job manager: " + e.getMessage() );
//            e.printStackTrace();
//        }
//        
//        jobService.shutdown();
	}
    
    protected static void initJobManager() {
        
        logger.console( "StockDataAnalysis::initJobManager() - Initializing the job manager." );
        
        completionService.submit( () -> {
            JobManager jm = new JobManager();
            jm.run();

            return new Object();
        });
        
        logger.console( "StockDataAnalysis::initJobManager() - Job manager running." );
    }
}
