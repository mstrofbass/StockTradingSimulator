package com.disposis.StockDataAnalysis.Data;

import com.disposis.StockDataAnalysis.Models.OptionTick;
import com.disposis.StockDataAnalysis.Util.Configuration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.disposis.util.Logger;
import com.disposis.trading.instruments.Stock;
import java.util.List;

public class OptionTicks {
	
	protected static OptionTicks instance = null;
	
	protected static Configuration conf = Configuration.getInstance();
	
	private final Logger logger = Logger.getInstance( conf );
	protected final Logger dataErrorLogger = Logger.getInstance( conf, "data-errors.log" );;
    
    protected Stock stock;
    protected int numTicks = 0;
	
	protected String currentSymbol;
	
	//HashMap<quoteDate, TreeMap<expirationDate, TreeMap<strike, HashMap<type, OptionTick>>>>
	TreeMap<LocalDate, TreeMap<LocalDate, TreeMap<Float, HashMap<Integer, OptionTick>>>> ticks = new TreeMap<>();

	public OptionTicks( Stock stock ) {
		
        this.stock = stock;

	}

	protected TreeMap<LocalDate, TreeMap<LocalDate, TreeMap<Float, HashMap<Integer, OptionTick>>>> getOptionTicks() 
	{
		return ticks;
	}
	
	protected TreeMap<LocalDate, TreeMap<Float, HashMap<Integer, OptionTick>>> getOptionTicks( LocalDate quoteDate )
	{
		return ticks.get(quoteDate);
	}
	
	public TreeMap<Float, HashMap<Integer, OptionTick>> getOptionTicks( LocalDate quoteDate, LocalDate expirationDate )
	{
		return ticks.get(quoteDate).get(expirationDate);
	}
	
	public ArrayList<OptionTick> getOptionTicks( LocalDate quoteDate, LocalDate expirationDate, int type )
	{
		ArrayList<OptionTick> optionTicks = new ArrayList<>();
		
		TreeMap<Float, HashMap<Integer, OptionTick>> ticksByQuoteDateAndExpirationDate = getOptionTicks(quoteDate, expirationDate);
		
		for ( Map.Entry<Float, HashMap<Integer, OptionTick>> optionTick : ticksByQuoteDateAndExpirationDate.entrySet() )
		{
			optionTicks.add( optionTick.getValue().get(type) );
		}
		
		return optionTicks;
	}
	
	public OptionTick getOptionTick( LocalDate quoteDate, int type, LocalDate expirationDate, float strike )
	{
		if ( ticks.get(quoteDate) == null
			|| ticks.get(quoteDate).get(expirationDate) == null
			|| ticks.get(quoteDate).get(expirationDate).get(strike) == null 
		) 
			return null;
		
		return ticks.get(quoteDate).get(expirationDate).get(strike).get(type);
	}
	
	public LocalDate getNextExpirationDate( LocalDate quoteDate, LocalDate earliestExpirationDate )
	{
		if ( !ticks.containsKey( quoteDate ) )
		{
			dataErrorLogger.error("No option quotes for quote date: " + quoteDate + " for option ticks for symbol " + currentSymbol);
			return null;
		}
		
		if ( ticks.get( quoteDate ).containsKey(earliestExpirationDate) )
			return earliestExpirationDate;
		
		return ticks.get(quoteDate).higherKey(earliestExpirationDate);
	}
	
	public LocalDate getLastQuoteDate()
	{
		return ticks.lastKey();
	}
	
	protected void addOptionTick( OptionTick optionTick )
	{
        TreeMap<LocalDate, TreeMap<Float, HashMap<Integer, OptionTick>>> expirationDateTreeMap = ticks.get( optionTick.getQuoteTimestamp() );

        if ( expirationDateTreeMap == null )
        {
            expirationDateTreeMap = new TreeMap<>();
            ticks.put( optionTick.getQuoteTimestamp(), expirationDateTreeMap);
        }

        TreeMap<Float, HashMap<Integer, OptionTick>> strikeTreeMap = ticks.get( optionTick.getQuoteTimestamp() ).get( optionTick.getExpirationDate() );

        if ( strikeTreeMap == null )
        {
            strikeTreeMap = new 	TreeMap<>();
            ticks.get( optionTick.getQuoteTimestamp()).put(optionTick.getExpirationDate(), strikeTreeMap);
        }

        HashMap<Integer, OptionTick> typeHashMap = ticks.get( optionTick.getQuoteTimestamp()).get(optionTick.getExpirationDate()).get( optionTick.getStrike() );

        if ( typeHashMap == null )
        {
            typeHashMap = new HashMap<>();
            ticks.get( optionTick.getQuoteTimestamp()).get( optionTick.getExpirationDate()).put(optionTick.getStrike(), typeHashMap);
        }

        if ( typeHashMap.containsKey(optionTick.getType()) )
        {
            //dataErrorLogger.error( "\tOptionTicksBySymbol::addOptionTick() - Multiple ticks for date. Symbol:  " + optionTick.getUnderlyingSymbol() + " and quote date " + optionTick.getQuoteTimestamp() + " and expiration date  " + optionTick.getExpirationDate() + " and strike " + optionTick.getStrike() + " and type " + optionTick.getType() );

            if ( !optionTick.getUnderlyingSymbol().equals( optionTick.getRoot() ) )
                return;
        }

        typeHashMap.put( 
            optionTick.getType(),
            optionTick
		);
        
        numTicks++;
	}
    
    public void addTick( OptionTick tick )
    {
        addOptionTick( tick );
    }
    
    public void addTicks( List<OptionTick> ticks )
    {
        for ( OptionTick ot : ticks )
            addTick( ot );
    }
    
    public int size() {
        return numTicks;
    }
    
    public LocalDate getEarliestTickDate()
    {
        return ticks.firstEntry().getKey();
    }
    
    public LocalDate getLatestTickDate()
    {
        return ticks.lastEntry().getKey();
    }
}
