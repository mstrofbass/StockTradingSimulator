/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.JobManager;

import com.disposis.StockDataAnalysis.JobService.Task;
import com.disposis.StockDataAnalysis.Messages.Message;
import com.disposis.StockDataAnalysis.Messages.SimulationCompleteMessage;
import com.disposis.StockDataAnalysis.Messages.TaskShutdownMessage;
import com.disposis.StockDataAnalysis.SimulateTrades.TradingSimulator;
import com.disposis.StockDataAnalysis.SimulateTrades.Strategies.TradingStrategy;
import com.disposis.StockDataAnalysis.SimulateTrades.Strategies.StrategyGenerator;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.util.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 *
 * @author blake
 */
public class SimulationTaskRunner implements Callable<TaskShutdownMessage> {
        
        protected Logger logger = Logger.getInstance( Configuration.getInstance() );
        
        protected boolean run = true;
        
        protected Task task;
        protected BlockingQueue<Message> messageQueue;
        protected Map<String, List<TradingStrategy>> strategies;
        
        public SimulationTaskRunner( Task task, BlockingQueue<Message> messageQueue, Map<String, List<TradingStrategy>> strategies )
        {
            this.task = task;
            this.messageQueue = messageQueue;
            this.strategies = strategies;
        }
        
        @Override
        public TaskShutdownMessage call() throws Exception {

            try {

                System.out.println("SimulationTaskRunner::run() - Processing stock data for " + task.getStock().getSymbol() );

                long threadId = Thread.currentThread().getId();
                
                List<TradingStrategy> currentStrategies = new ArrayList<>();

                for ( String strategyKey : task.getStrategies() )
                {
                    if ( strategies.containsKey( strategyKey ) )
                        currentStrategies.addAll( StrategyGenerator.generateStrategies( strategyKey ) );
                }
                
                TradingSimulator sim = new TradingSimulator( (int) threadId, task, currentStrategies );
                sim.simulate();

                System.out.println("SimulationTaskRunner::run() - Done processing stock data for " + task.getStock().getSymbol() );

                this.messageQueue.add( new SimulationCompleteMessage( task ) );
            }
            catch ( InterruptedException ie )
            {
                if ( Thread.currentThread().isInterrupted() )
                    return new TaskShutdownMessage();

                this.logger.message( "SimulationTaskRunner::run() - Interrupted Exception caught and dropped." );
            }
            catch( Exception e )
            {
                this.logger.message( "SimulationTaskRunner::run() - Unexpected exception caught: " + e.toString() );
                
                throw e;
            }
            
            return new TaskShutdownMessage();
        }
        
        public void shutdown() {
            this.run = false;
        }
    }