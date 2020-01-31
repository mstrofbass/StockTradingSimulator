/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.JobManager;

import com.disposis.StockDataAnalysis.Data.DataRepository;
import com.disposis.StockDataAnalysis.JobService.Job;
import com.disposis.StockDataAnalysis.JobService.Task;
import com.disposis.StockDataAnalysis.JobService.JobServiceConnector;
import com.disposis.StockDataAnalysis.Messages.JobCompleteMessage;
import com.disposis.StockDataAnalysis.Messages.JobServerShutdownMessage;
import com.disposis.StockDataAnalysis.Messages.LoadCompleteMessage;
import com.disposis.StockDataAnalysis.Messages.Message;
import com.disposis.StockDataAnalysis.Messages.SimulationCompleteMessage;
import com.disposis.StockDataAnalysis.Messages.TaskShutdownMessage;
import com.disposis.StockDataAnalysis.SimulateTrades.Strategies.TradingStrategy;
import com.disposis.StockDataAnalysis.SimulateTrades.Strategies.StrategyGenerator;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.StockDataAnalysis.Util.Timeline;
import com.disposis.util.Logger;
import com.disposis.util.Timer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author blake
 */
public class JobManager {
    protected static final int DATA_LOADED = 0;
    protected static final int STOCK_PROCESSED = 1;
    
    protected Configuration conf = Configuration.getInstance();
	protected Logger logger = Logger.getInstance( Configuration.getInstance() );
    
    protected boolean run = true;
    protected boolean shutdown = false;
    protected boolean forceShutdown = false;
    
    protected int activeTasks = 0;
    protected int activeSimulationThreads = conf.getMaxThreads();
    
    Map<String, List<TradingStrategy>> strategies = new HashMap<>();
    
    BlockingQueue<Task> stocksToLoad = new LinkedBlockingQueue<>();
    BlockingQueue<Task> stocksToProcess = new LinkedBlockingQueue<>();
    BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    
    LoadDataTaskRunner ldr;
    Thread loaderThread;
    
    Set<SimulationTaskRunner> strs = new HashSet( conf.getMaxThreads() );
    
    ExecutorService executorService = Executors.newFixedThreadPool( conf.getMaxThreads() );
    CompletionService<TaskShutdownMessage> completionService = new ExecutorCompletionService<>(executorService);
    
    JobServiceConnector jobService = new JobServiceConnector( messageQueue );
    
    DataRepository dataRepo = DataRepository.getInstance();
    Job currentJob;
    
    public JobManager() {
        
	}
    
    public void run() throws Exception {
        
        try {
            this.jobService.init();
        }
        catch( Exception e )
        {
            logger.console("JobManager::run() - JobService Connector init() failed: " + e.getMessage());
            
            throw e;
        }
        
        this.init();
        
        logger.console("JobManager::run() - Waiting for job start message.");
        
        Message msg;
        
        while ( !shutdown )
        {
            logger.console("JobManager::run() - Checking for active job.");
            
            currentJob = jobService.getNextJob();
            
            Thread.sleep(5000);
//            if ( currentJob == null )
//            {
//                logger.console("JobManager::run() - No job, waiting for message.");
//                
//                msg = messageQueue.poll(10, TimeUnit.SECONDS);
//
//                if ( msg != null && msg instanceof JobServerShutdownMessage )
//                {
//                    logger.console("JobManager::run() - Job shutdown message received; shutting down.");
//
//                    run = false;
//                    shutdown = true;
//
//                    JobServerShutdownMessage jMsg = (JobServerShutdownMessage) msg;
//
//                    if ( jMsg.isForcedShutdown() )
//                        shutdownThreads( true );
//                    else
//                        shutdownThreads();
//                }
//            }
            
            if ( currentJob != null )
            {
                logger.console("JobManager::run() - Job assigned, running job.");
                
                runJob();

                logger.console("JobManager::run() - Job complete.");

                currentJob = null;
                shutdown = true;
            }
        }
        
        shutdownThreads();
        
        this.logger.console( "JobManager::run() - Shutting down service connector." );
        
        jobService.shutdown();
        
        this.logger.console( "JobManager::run() - Exiting method." );
    }
    
    protected void init() 
    {
        initThreads();
    }
    
    protected void initThreads()
    {
        this.ldr = new LoadDataTaskRunner( this.stocksToLoad, this.messageQueue );
        
        this.logger.console( "JobManager::initThreads() - Starting loader thread." );
        
        this.loaderThread = new Thread( this.ldr );
        this.loaderThread.start();
        
        this.logger.console( "JobManager::run() - Threads started." );
    }
    
    protected void runJob() throws Exception
    {
        logger.console( "JobManager::runJob() - Running job." );
        
        Message msg;
        LoadCompleteMessage lcm;
        SimulationCompleteMessage scm;
        
        Task currentTask = jobService.getNextTask(currentJob);
        
        while ( currentTask != null || activeTasks > 0 )
        {
            while ( currentTask != null && activeTasks <= conf.getMaxThreads() )
            {
                logger.console("JobManager::runJob() - Starting new task for symbol %s (%s)", currentTask.getStock().getSymbol(), currentTask.getStock().getISIN() );
                
                this.stocksToLoad.add(currentTask);
                activeTasks++;
                
                currentTask = jobService.getNextTask(currentJob);
            }
            
            logger.console("JobManager::runJob() - Waiting for new messages on the message queue.");
            msg = messageQueue.take();
            
            if ( msg instanceof LoadCompleteMessage )
            {
                lcm = (LoadCompleteMessage) msg;
                
                logger.console("JobManager::runJob() - LoadCompleteMessage received for %s (%s). Proceeding to simulate.", lcm.getTask().getStock().getSymbol(), lcm.getTask().getStock().getISIN() );
                
                try {
                    simulate( lcm.getTask() );
                }
                catch ( Exception e )
                {
                    activeTasks--;
                    logger.console("JobManager::runJob() - Exception caught trying to start simulation of task, resulting in a task failure. Msg: " + e.getMessage() );
                }
            }
            else if ( msg instanceof SimulationCompleteMessage )
            {
                scm = (SimulationCompleteMessage) msg;
                
                dataRepo.unloadAllTicks( scm.getTask().getStock() );
                jobService.notifyCompleted(currentJob, scm.getTask() );
                activeTasks--;
            }
        }

        writeTimeline();
        PostProcessor.simulationPostProcessing();
    }
    
    protected void simulate( Task task ) throws Exception
    {
        generateStrategies( task );
        
        try {
            completionService.submit(
                new SimulationTaskRunner(task, this.messageQueue, strategies)
            );
        }
        catch ( Exception e )
        {
            logger.console( "exception in simulate msg: " + e.getMessage() );
        }
    }
    
    protected void generateStrategies( Task task ) throws Exception {
        
        for ( String strategyKey : task.getStrategies() )
        {
            if ( strategies.containsKey( strategyKey ) )
                continue;
            
            try {
                strategies.put( strategyKey , StrategyGenerator.generateStrategies(strategyKey) );
            }
            catch ( Exception e )
            {
                logger.console( "JobManager::generateStrategies() - Exception caught while generating strategies for " + strategyKey );
                throw new Exception( "JobManager::generateStrategies() - Exception caught while generating strategies for " + strategyKey );
            }
        }
    }
    
    protected void shutdownThreads() {
        this.shutdownThreads( false );
    }
    
    protected void shutdownThreads( boolean forceShutdown )
    {
        this.logger.console( "JobManager::run() - Shutting threads down." );
        this.ldr.shutdown();
        
        for (SimulationTaskRunner str : this.strs )
            str.shutdown();
        
        if ( forceShutdown )
        {
            logger.console( "JobManager::run() - Force shutdown indicated; using shutdownNow()." );
            this.executorService.shutdownNow();
        }
        else
        {
            logger.console( "JobManager::run() - Force shutdown NOT indicated; using shutdown()." );
            this.executorService.shutdown();
        }
        
        
        this.logger.console( "JobManager::run() - Threads shutting down." );
    }
    
    protected void writeTimeline()
    {
        logger.console( "JobManager::writeTimeline() - Writing timeline." );
        
        Timeline timeline = Timeline.getInstance();
        Map<Integer, Map<String, List<Timer>>> timelineData = timeline.getTimelineData();
        
        File timelineFile = conf.getTimelineOutputFilePath().toFile();
        
        BufferedWriter br = null;
        
        try {
            br = new BufferedWriter( new FileWriter( timelineFile ) );
            
            br.write("{");
            br.newLine();
            
            List<Integer> threadIds = new ArrayList<>( timelineData.keySet() );
            int threadId;
            
            List<String> timerTypes;
            Map<String, List<Timer>> timersByType;
            String timerType;
            
            Timer timer;
            List<Timer> timerList;
            
            for ( int i = 0 ; i < threadIds.size(); i++ ) 
            {
                threadId = threadIds.get(i);
                
                br.write("\t\"" + threadId + "\": {");
                br.newLine();
                
                timersByType = timelineData.get(threadId);
                timerTypes = new ArrayList<>( timersByType.keySet() );
                
                for ( int j = 0 ; j < timerTypes.size(); j++ ) 
                {
                    timerType = timerTypes.get(j);
                    
                    br.write("\t\t\"" + timerType + "\": [");
                    br.newLine();
                    
                    timerList = timersByType.get(timerType);
                  
                    for ( int k = 0 ; k < timerList.size(); k++ ) 
                    {
                        timer = timerList.get(k);
                        
                        br.write("\t\t\t{"); br.newLine();
                        br.write("\t\t\t\t\"begin\": \"" + timer.getBeginTime().toEpochMilli() + "\","); br.newLine();
                        br.write("\t\t\t\t\"end\": \"" + timer.getEndTime().toEpochMilli() + "\""); br.newLine();
                        br.write("\t\t\t}"); 
                        
                        if ( k < timerList.size() - 1 )
                            br.write(",");
                        
                        br.newLine();
                    }
                    
                    br.write("\t\t]"); 
                    
                    if ( j < timerTypes.size() - 1 )
                        br.write(",");
                    
                    br.newLine();
                }
                
                br.write("\t}"); 
                
                if ( i < threadIds.size() - 1 )
                        br.write(",");
                
                br.newLine();
            }
            
            br.write("}");
        }
        catch ( IOException e )
        {
            logger.console("Simulator::writeTimeline() - IOException caught while trying to write timeline. %s", e.toString() );
            return;
        }
        finally
        {
            try {
                if ( br != null )
                    br.close();
            }
            catch ( IOException e )
            {
                logger.console("Simulator::writeTimeline() - IOException caught while trying to close buffered reader. %s", e.toString() );
                return;
            }
        }
        
        logger.console( "JobManager::writeTimeline() - Done writing timeline." );
    }
}