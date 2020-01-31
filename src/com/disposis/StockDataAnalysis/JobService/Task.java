/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.JobService;

import com.disposis.trading.instruments.Stock;
import com.disposis.util.DateRange;
import java.util.Set;

/**
 *
 * @author blake
 */
public class Task {
    public static final String STRATEGY_REBOUND = "rebound";
    public static final String STRATEGY_INIT_DAY = "initDay";
    
    protected Stock stock;
    protected Set<String> strategies;
    protected Set<DateRange> dateRanges;
    
    public Task( Stock stock, Set<String> strategies, Set<DateRange> dateRanges )
    {
        this.stock = stock;
        this.strategies = strategies;
        this.dateRanges = dateRanges;
    }

    public Stock getStock() {
        return stock;
    }

    public Set<String> getStrategies() {
        return strategies;
    }

    public Set<DateRange> getDateRanges() {
        return dateRanges;
    }
}
