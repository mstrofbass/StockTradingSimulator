package com.disposis.StockDataAnalysis.SimulateTrades;

import com.disposis.StockDataAnalysis.Data.DataRepository;
import com.disposis.StockDataAnalysis.Models.Option;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.disposis.StockDataAnalysis.Models.OptionTick;
import com.disposis.StockDataAnalysis.Models.OptionTransaction;
import com.disposis.StockDataAnalysis.SimulateTrades.Strategies.TradingStrategy;
import com.disposis.StockDataAnalysis.SimulateTrades.Strategies.TradingStrategy.Transactions;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.StockDataAnalysis.Util.MarketInfo;
import com.disposis.StockDataAnalysis.Util.Timeline;
import com.disposis.trading.instruments.Stock;
import com.disposis.util.Logger;
import com.disposis.trading.ticks.StockTick;
import java.io.Writer;
import java.util.List;

public class Trader extends DateSynchronized {
	
    protected static Timeline timeline = Timeline.getInstance();
	
	protected Logger logger = Logger.getInstance( Configuration.getInstance() );
	protected Logger dataErrorLogger = Logger.getInstance( Configuration.getInstance(), "data-errors.out" );;
    
    protected MarketInfo marketInfo = MarketInfo.getInstance();
    
    protected List<TradingStrategy> strategies;
	
	protected final Writer transWriter;
	protected DataRepository dataRepo = DataRepository.getInstance();
    
	public Trader( List<TradingStrategy> strategies, Writer transWriter ) {
		super();
		
        this.strategies = strategies;
        this.transWriter = transWriter;
	}
    
    public void feedStockTick( StockTick tick )
    {
        Transactions trans;

        for ( TradingStrategy strategy : strategies )
        {
            try {
                strategy.feedTick(tick);
                
                trans = strategy.getTransactions();

                if ( trans.sell != null )
                {
                    for ( OptionTransaction ot : trans.sell )
                        logOptionTransaction( strategy.getLabel(), ot );
                }
            }
            catch ( Exception e )
            {
                logger.error( "Trader::feedStockTick() - Exception thrown while strategy was processing tick. Msg: " + e.getMessage() );
            }     
        }
    }
    
    protected void logOptionTransaction( String strategyLabel, OptionTransaction optionTrans ) throws Exception
    {
	    	if ( !optionTrans.isSale() )
	    	{
	    		throw new IllegalArgumentException( "Option must have been sold to log an option transaction! Option data: " + optionTrans.toString() );
	    	}
	    	
	    	/**
	    	 * strategy
	    	 * ISIN
	    	 * expiration_date
	    	 * strike
	    	 * type
	    	 * buy_date
	    	 * buy_year
	    	 * quantity
	    	 * buy_price
	    	 * cost
	    	 * sell_date
	    	 * sell_year
	    	 * sell_price
	    	 * proceeds
	    	 * revenue
	    	 * realPrice?
	    	 * buyTriggerPct
	    	 * sellTriggerPct
	    	 * vwap
	    	 */
	    	
	    	String expirationDateText = "NULL";
            Option option = optionTrans.getOption();
            Stock stock = option.getStock();
	    	
	    	if ( option.getExpirationDate() != null )
	    		expirationDateText = option.getExpirationDate().format( DateTimeFormatter.ISO_LOCAL_DATE );
	    	
	    	int realPrice = optionTrans.isRealPrice() ? 1 : 0;
	    	
	    	String vwap = optionTrans.getOptionTick() == null || optionTrans.getOptionTick().getVWAP() == 0.0 ? "NULL" : optionTrans.getOptionTick().getVWAP() + "";
	    	
	    	String logEntry = strategyLabel + "," 
                        + stock.getISIN() + "," 
						+ expirationDateText + ","
						+ option.getStrike() + ","
						+ (option.getType() == OptionTick.TYPE_CALL ? "C" : "P") + ","
                        + optionTrans.getBuyDate().format( DateTimeFormatter.ISO_LOCAL_DATE ) + ","
                        + optionTrans.getBuyDate().format( DateTimeFormatter.ofPattern("yyyy") ) + ","
						+ optionTrans.getQuantity() + "," 
                        + Trader.round( optionTrans.getBuyPrice(), 2) + ","
						+ Trader.round( optionTrans.getCost(), 2) + ","
                        + optionTrans.getSellDate().format( DateTimeFormatter.ISO_LOCAL_DATE ) + "," 
                        + optionTrans.getSellDate().format( DateTimeFormatter.ofPattern("yyyy") ) + "," 
                        + Trader.round( optionTrans.getSellPrice(), 2) + ","
						+ Trader.round( optionTrans.getProceeds(), 2) + ","
                        + Trader.round(( optionTrans.getProceeds() - optionTrans.getCost() ), 2) + ","
                        + realPrice + ","
                        + Trader.round( optionTrans.getBuyTriggerPct(), 2) + ","
						+ Trader.round( optionTrans.getSellTriggerPct(), 2) + ","
						+ vwap
                        + "\n"; 
	    	
	    	try {
	    		
			synchronized( this.transWriter ) {
				this.transWriter.write(logEntry);
			}
	    		
	    	}
	    	catch ( IOException e )
	    	{
	    		logger.error("Failed to write option transaction to option transaction log.");
	    		throw e;
	    	}
    }
    
    protected static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
