/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Data;

import com.disposis.trading.instruments.Stock;
import com.disposis.trading.ticks.StockTick;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author blake
 */
public class StockTicks {
    
    Stock stock;
    
    TreeMap<LocalDate, StockTick> ticks = new TreeMap<>();
    
    public StockTicks( Stock stock )
    {
        this.stock = stock;
    }
    
    public StockTick getTick( LocalDate date )
    {
        return ticks.get(date);
    }
    
    public Collection<StockTick> getTicks()
    {
        return ticks.values();
    }
    
    public Map<LocalDate, StockTick> getDateMappedTicks()
    {
        return ticks;
    }
    
    public TreeMap<LocalDate, StockTick> getOrderedDateMappedTicks()
    {
        return ticks;
    }
    
    public void addTick( StockTick tick )
    {
        this.ticks.put( tick.getDate(), tick );
    }
    
    public void addTicks( List<StockTick> ticks )
    {
        for ( StockTick tick : ticks )
            addTick( tick );
    }
    
    public int size() {
        return ticks.values().size();
    }
    
    public StockTick getEarliestTick()
    {
        return ticks.firstEntry().getValue();
    }
    
    public StockTick getLatestTick()
    {
        return ticks.lastEntry().getValue();
    }
}
