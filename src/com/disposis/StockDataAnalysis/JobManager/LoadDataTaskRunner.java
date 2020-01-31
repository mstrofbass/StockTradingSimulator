/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.JobManager;

import com.disposis.StockDataAnalysis.Data.DataRepository;
import com.disposis.StockDataAnalysis.Data.OptionTickLoader;
import com.disposis.StockDataAnalysis.Data.OptionTicks;
import com.disposis.StockDataAnalysis.Data.StockTickLoader;
import com.disposis.StockDataAnalysis.Data.StockTicks;
import com.disposis.StockDataAnalysis.JobService.Task;
import com.disposis.StockDataAnalysis.Messages.LoadCompleteMessage;
import com.disposis.StockDataAnalysis.Messages.Message;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.StockDataAnalysis.Util.Timeline;
import com.disposis.trading.instruments.metadata.StockMetadata;
import com.disposis.util.Logger;
import com.disposis.util.Timer;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author blake
 */
public class LoadDataTaskRunner implements Runnable {
        
        protected Logger logger = Logger.getInstance( Configuration.getInstance() );
        protected static Timeline timeline = Timeline.getInstance();
        
        protected boolean run = true;
        
        protected BlockingQueue<Task> loadQueue;
        protected BlockingQueue<Message> messageQueue;
        
        protected DataRepository dataRepo = DataRepository.getInstance();
        
        Map<String, StockMetadata> stockMetadata;
        
        public LoadDataTaskRunner( BlockingQueue<Task> loadQueue, BlockingQueue<Message> messageQueue )
        {
            this.loadQueue = loadQueue;
            this.messageQueue = messageQueue;
        }
        
        @Override
        public void run() {
            System.out.println("LoadData::run() - Waiting on data...");
            
            Task task;
            
            while ( this.run )
            {
                try {
                    task = this.loadQueue.poll(10, TimeUnit.SECONDS);
                    
                    if ( task != null )
                    {
                        loadData( task );

                        this.messageQueue.add(new LoadCompleteMessage( task ) );
                    }
                }
                catch( InterruptedException e )
                {
                    this.logger.message( "LoadData::run() - Interrupted Exception caught and dropped." );
                }
            }
        }
        
        protected void loadData( Task task ) {
            try {
                        logger.console("LoadDataTaskRunner::loadData() - Loading stock data for " + task.getStock().getSymbol() );
                        Timer timer = Timer.start("load-stock-ticks-" + task.getStock().getISIN() );
                                
                loadStockTickDate( task );
                
                        timer.stop();
                        timeline.addTimer(200, "load-stock-ticks", timer);

                        logger.console("LoadDataTaskRunner::loadData() - Done loading stock data for " + task.getStock().getSymbol() );
                        logger.console("LoadDataTaskRunner::loadData() - Loading option data for " + task.getStock().getSymbol() );

                        timer = Timer.start("load-option-ticks-" + task.getStock().getISIN() );

                loadOptionTickData( task );

                        timer.stop();
                        timeline.addTimer(200, "load-option-ticks", timer);

                        logger.console("LoadDataTaskRunner::loadData() - Done loading option data for " + task.getStock().getSymbol() );

            } catch (IOException e) {
                logger.console("LoadDataTaskRunner::loadData() - Exception caught while trying to load data for stock %s (%s): %s ", task.getStock().getSymbol(), task.getStock().getISIN(), e.toString() );
            }
        }
        
        protected void loadStockTickDate( Task task ) throws IOException
        {
            StockTickLoader stl = new StockTickLoader();
            StockTicks stockTicks = stl.loadTicks(task.getStock(), task.getDateRanges());
            dataRepo.putStockTicks(task.getStock(), stockTicks);
        }
        
        protected void loadOptionTickData( Task task ) throws IOException
        {
            OptionTickLoader otl = new OptionTickLoader();
            OptionTicks optionTicks = otl.loadTicks(task.getStock(), task.getDateRanges());
            dataRepo.putOptionTicks(task.getStock(), optionTicks);
        }
        
        public void shutdown() {
            this.run = false;
        }
    }