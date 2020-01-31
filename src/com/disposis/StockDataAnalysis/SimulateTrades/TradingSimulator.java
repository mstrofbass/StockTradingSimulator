package com.disposis.StockDataAnalysis.SimulateTrades;

import com.disposis.StockDataAnalysis.JobService.Task;

import com.disposis.StockDataAnalysis.SimulateTrades.Strategies.TradingStrategy;
import com.disposis.trading.instruments.Stock;
import com.disposis.trading.ticks.StockTick;
import java.nio.file.Path;
import java.util.List;
	
public class TradingSimulator extends Simulator
{	
	protected Trader trader;
	
	public TradingSimulator( int threadId, Task task, List<TradingStrategy> strategies )
	{
		super( threadId, task, strategies );
	}
    
    @Override
    protected void runSimulation( Stock stock ) throws Exception
    {
        trader = new Trader(strategies, writer);
        logger.console( "simulating" );
        super.runSimulation( stock );
    }

    @Override
    protected void feedTick(StockTick tick) {
        trader.feedStockTick(tick);
    }
    
    @Override
    protected Path getOutputPath() {
        return conf.getOptionTransOutputFilePath( task.getStock() );
    }
}
