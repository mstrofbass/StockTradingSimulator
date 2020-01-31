/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Data;

import com.disposis.trading.instruments.Stock;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author blake
 */
public class DataRepository {
    
    protected static DataRepository instance = null;
    
    protected Map<String, StockTicks> stockTicks = new HashMap<>();
    protected Map<String, OptionTicks> optionTicks = new HashMap<>();
    
    private DataRepository() {}
    
    public StockTicks getStockTicks( Stock stock )
    {
        return stockTicks.get( stock.getISIN() );
    }
    
    public void putStockTicks( Stock stock, StockTicks ticks )
    {
        stockTicks.put( stock.getISIN(), ticks);
    }
    
    public void unloadStockTicks( Stock stock )
    {
        stockTicks.remove( stock.getISIN() );
    }
    
    public OptionTicks getOptionTicks( Stock stock )
    {
        return optionTicks.get(stock.getISIN());
    }
    
    public void putOptionTicks( Stock stock, OptionTicks ticks )
    {
        optionTicks.put( stock.getISIN(), ticks );
    }
    
    public void unloadOptionTicks( Stock stock )
    {
        optionTicks.remove( stock.getISIN() );
    }
    
    public void unloadAllTicks( Stock stock )
    {
        unloadStockTicks(stock);
        unloadOptionTicks(stock);
    }
    
    public static DataRepository getInstance() {
        if ( instance == null )
            instance = new DataRepository();
        
        return instance;
    }
}
