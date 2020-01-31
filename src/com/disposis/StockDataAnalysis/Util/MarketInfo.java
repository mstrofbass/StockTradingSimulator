/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Util;

import com.disposis.util.Logger;
import java.time.LocalDate;
import java.util.TreeMap;

/**
 *
 * @author blake
 */
public class MarketInfo {
    protected static MarketInfo instance = null;
    
    protected TreeMap<LocalDate, LocalDate> tradingDays = new TreeMap<LocalDate, LocalDate>();
    
    private MarketInfo() {}
    
    public void addTradingDay( LocalDate tradingDay )
	{
		if ( tradingDay == null )
		{
			try {
				Logger.getInstance( Configuration.getInstance() ).error("Trading day is null!?");
			}
			catch ( Exception e )
			{
				System.err.println("Error occurred trying to log message \"Trading day is null!?\"");
			}
		}
		
		tradingDays.put( tradingDay, tradingDay );
	}
	
	public boolean isTradingDay( LocalDate tradingDay )
	{
		return tradingDays.containsKey( tradingDay );
	}
	
	public LocalDate getTradingDay( LocalDate tradingDay )
	{
		return tradingDays.get( tradingDay );
	}
	
	public LocalDate getTradingDay( LocalDate tradingDay, int offset )
	{
		if ( offset == 0 )
		{
			return getTradingDay( tradingDay );
		}
		else if ( offset > 0 )
		{
			LocalDate currentTradingDay = tradingDays.get( tradingDay );
			
			for ( int i = 0; i < offset ; i++ )
			{
				currentTradingDay = tradingDays.higherKey(currentTradingDay);
				
				if ( currentTradingDay == null )
				{
					return tradingDay.plusDays(offset + ( (int) Math.floor(offset/5) * 2));
				}
			}
			
			return currentTradingDay;
		}
		else
		{
			LocalDate currentTradingDay = tradingDays.get( tradingDay );
			
			for ( int i = offset; i < 0 ; i++ )
			{
				currentTradingDay = tradingDays.higherKey(currentTradingDay);
				
				if ( currentTradingDay == null )
					return tradingDay.plusDays(offset + ( (int) Math.floor(offset/5) * 2));
			}
			
			return currentTradingDay;
		}
	}
    
    public static MarketInfo getInstance()
    {
        if ( instance == null )
            instance = new MarketInfo();
        
        return instance;
    }
}
