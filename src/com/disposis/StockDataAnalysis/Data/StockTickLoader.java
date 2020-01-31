/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Data;

import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.trading.instruments.Stock;
import com.disposis.trading.ticks.StockTick;
import com.disposis.util.DateRange;
import com.disposis.util.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author blake
 */
public class StockTickLoader {
    
    protected Configuration conf = Configuration.getInstance();
    protected Logger logger = Logger.getInstance( conf );
    protected Logger dataErrorLogger = Logger.getInstance( conf, "data-errors.log" );
    
    protected Stock stock;
    
    public StockTicks loadTicks( Stock stock, Set<DateRange> dateRanges ) throws IOException
    {
        this.stock = stock;
       
        StockTicks stockTicks = new StockTicks( stock );
        
        Predicate<String[]> validDatePredicate = getDateRangePredicate(dateRanges);
        Consumer<String[]> stockParser = getParseStockDataFunction(stockTicks);
        
        Stream<String> stream = Files.lines( conf.getEODStockTickFilePath(stock) );
        stream
            .map( l -> l.split(",") )
            .filter( validDatePredicate )
            .forEach( stockParser );

        logger.console( "StockTickData::loadTicks() - Num ticks loaded: " + stockTicks.size() );
        logger.console( "StockTickData::loadTicks() - First stock tick: " + stockTicks.getEarliestTick().getDateTime() );
        logger.console( "StockTickData::loadTicks() - Last stock tick: " + stockTicks.getLatestTick().getDateTime() );
        logger.console( "StockTickData::loadTicks() - Done loading stock EOD tick data for ISIN %s.", stock.getISIN() );
        
        return stockTicks;
    }
    
    protected static Predicate<String[]> getDateRangePredicate( Set<DateRange> dateRanges )
    {
        return data -> {
            LocalDate tickDate = LocalDate.parse( data[0], DateTimeFormatter.BASIC_ISO_DATE );

                for ( DateRange dateRange : dateRanges )
                {
                    if ( tickDate.isAfter(dateRange.getBegin().minusDays(1)) && tickDate.isBefore( dateRange.getEnd().plusDays(1) ) )
                        return true;
                }
                
                return false;
        };
    }
    
    protected Consumer<String[]> getParseStockDataFunction( StockTicks stockTicks ) {
        return rawData -> {  

            LocalDate tickDate = LocalDate.parse( rawData[0], DateTimeFormatter.BASIC_ISO_DATE );
            LocalDateTime tickDateTime = tickDate.atTime(16, 0);

            stockTicks.addTick(
                new StockTick(
                    stock,
                    tickDateTime,
                    Float.parseFloat( rawData[2] ),
                    Float.parseFloat( rawData[3] ),
                    Float.parseFloat( rawData[4] ),
                    Float.parseFloat( rawData[5] ) * Float.parseFloat( rawData[7] ),
                    Float.parseFloat( rawData[5] )
                )
            );
        };
    }
}
