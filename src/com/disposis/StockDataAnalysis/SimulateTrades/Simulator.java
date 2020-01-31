/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.SimulateTrades;

import com.disposis.StockDataAnalysis.Data.DataRepository;
import com.disposis.StockDataAnalysis.JobService.Task;
import com.disposis.StockDataAnalysis.SimulateTrades.Strategies.Strategy;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.StockDataAnalysis.Util.Timeline;
import com.disposis.trading.instruments.Stock;
import com.disposis.trading.ticks.StockTick;
import com.disposis.util.DateRange;
import com.disposis.util.Logger;
import com.disposis.util.Timer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author blake
 * @param <T>
 */
public abstract class Simulator<T extends Strategy> {

    protected static Timeline timeline = Timeline.getInstance();
    
    protected Exception caughtException = null;
    
    protected Configuration conf = Configuration.getInstance();
    protected DataRepository dataRepo = DataRepository.getInstance();
    protected Logger logger = Logger.getInstance(Configuration.getInstance());
    
    protected Task task;
    
    protected int threadId = 0;
    
    protected Writer writer = null;
    
    protected List<T> strategies;
    
    public Simulator( int threadId, Task task, List<T> strategies )
    {
        this.threadId = threadId;
        this.task = task;
        this.strategies = strategies;
    }
    
    public void simulate() throws Exception, IOException
	{

		logger.console( "[Thread " + this.threadId + "] Simulator::simulate() - Entering simulate method." );

        Timer simTimer;
        Path outputPath = null;
        
        Stock stock = task.getStock();

        try {
            outputPath = getOutputPath();

            Files.deleteIfExists(outputPath);

            this.writer = new BufferedWriter( new FileWriter( outputPath.toFile() ) );
        }
        catch ( IOException e )
        {
            String outputPathString = outputPath == null ? "null" : outputPath.toString();
            logger.console("[Thread " + this.threadId + "] Simulator::simulate() - Exception caught while trying to open output buffer. \n\tPath: %s \n\tMsg: %s", outputPathString, e.toString() );
            
            throw new IOException( String.format( "[Thread " + this.threadId + "] Simulator::simulate() - Exception caught while trying to open output buffer. \n\tPath: %s \n\tMsg: %s", outputPathString, e.toString()), e);
        }

        try {
            simTimer = Timer.start("sim-" + stock.getISIN() );

            runSimulation( stock );

            simTimer.stop();
            timeline.addTimer(threadId, "sim", simTimer);
        }
        catch ( Exception e )
        {
            logger.console( "[Thread " + this.threadId + "] Simulator::simulate() - Exception generated while trying to run the simulation: " + e.getMessage() );

            throw new Exception( "[Thread " + this.threadId + "] Simulator::simulate() - Exception generated while trying to run the simulation: " + e.getMessage(), e );
        }
        finally 
        {
            try {
                this.writer.close();
            }
            catch ( IOException e )
            {
                logger.console("[Thread " + this.threadId + "] Simulator::simulate() - Exception caught while trying to close output buffer for symbol. \n\tMsg: %s", e.toString() );
                
                throw new IOException( "[Thread " + this.threadId + "] Simulator::simulate() - Exception caught while trying to close output buffer for symbol. \n\tMsg: " + e.toString(), e );
            }
        }
		
		try {
	    		this.writer.close();
	    }
	    catch ( IOException e )
	    {
	    		logger.console( "[Thread " + this.threadId + "] Simulator::simulate() - Exception generated while trying to close the option transaction log buffered writer." );
		    	
                throw new IOException( "[[Thread " + this.threadId + "] Simulator::simulate() - Exception generated while trying to close the option transaction log buffered writer.", e );
	    }
		
		logger.console( "[Thread " + this.threadId + "] Done cleaning up." );
	}
    
    protected void runSimulation( Stock stock ) throws Exception
	{
        String symbol = stock.getSymbol();
        String isin = stock.getISIN();
        
		logger.console( "[Thread " + this.threadId + "] --------------------------------------------------------------" );
		logger.console( "[Thread " + this.threadId + "] Simulator::runSimulation() - Preparing to run simulation for symbol " + symbol + " (" + isin + ")");
		
		logger.console( "[Thread " + this.threadId + "] Simulator::runSimulation() - Preparations complete." );
		logger.console( "[Thread " + this.threadId + "] Simulator::runSimulation() - Running simulation." );
		
        Timer timer = Timer.start("get-stock-ticks-" + stock.getISIN() );
        
		TreeMap<LocalDate, StockTick> ticks = dataRepo.getStockTicks(stock).getOrderedDateMappedTicks();
        
        timer.stop();
        timeline.addTimer(threadId, "get-stock-ticks", timer);
		
        StockTick currentTick;
        Set<DateRange> listedDates = task.getDateRanges();
        
        if ( ticks == null )
        {
            throw new Exception( String.format( "Ticks is null for %s (%s)", symbol, isin ) );
        }
        
        for ( DateRange dateRange : listedDates )
            for ( LocalDate currentDate : dateRange )
            {
                currentTick = ticks.get( currentDate );
                
                if ( currentTick == null )
                    continue;
                
                DateSynchronized.setCurrentDate( currentDate );
			
                feedTick(currentTick);
            }
		
		logger.console( "[Thread " + this.threadId + "] Simulator::runSimulation() - Simulation for symbol " + symbol + " complete. Cleaning up." );	
		
		for ( Strategy strategy : this.strategies )
			strategy.reset();
        
        logger.flush();
		
		logger.console( "[Thread " + this.threadId + "] Simulator::runSimulation() - Done cleaning." );
    }
    
    protected abstract void feedTick( StockTick tick );
    protected abstract Path getOutputPath();
}
