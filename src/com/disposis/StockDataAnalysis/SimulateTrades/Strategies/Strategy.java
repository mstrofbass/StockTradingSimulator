/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.SimulateTrades.Strategies;

import com.disposis.StockDataAnalysis.SimulateTrades.DateSynchronized;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.StockDataAnalysis.Util.MarketInfo;
import com.disposis.trading.ticks.StockTick;
import com.disposis.util.Logger;

/**
 *
 * @author blake
 */
public abstract class Strategy extends DateSynchronized {
    
    protected Logger logger = Logger.getInstance( Configuration.getInstance() );
    
    private final String label;
    
    protected MarketInfo marketInfo = MarketInfo.getInstance();
    
    public Strategy( String label ) {
        this.label = label;
    }
    
    public abstract void feedTick( StockTick tick );
    
    public abstract void reset();

	public String getLabel() {
		return label;
	}

	public static float calculatePercentChange( float x, float y )
	{
		return ((y-x)/x) * 100;
	}
}
