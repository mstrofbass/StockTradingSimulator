/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Data;

import com.disposis.StockDataAnalysis.Models.OptionTick;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.trading.instruments.Stock;
import com.disposis.util.DateRange;
import com.disposis.util.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author blake
 */
public class OptionTickLoader {
    protected Configuration conf = Configuration.getInstance();
    protected Logger logger = Logger.getInstance( conf );
    protected Logger dataErrorLogger = Logger.getInstance( conf, "data-errors.log" );
    
    protected Stock stock;
    
    public OptionTicks loadTicks( Stock stock, Set<DateRange> dateRanges ) throws IOException
    {
        this.stock = stock;
       
        OptionTicks optionTicks = new OptionTicks( stock );
        
        Predicate<String[]> openInterestPredicate = getOpenInterestPredicate();
        Predicate<String[]> validDatePredicate = getDateRangePredicate(dateRanges);
        
        Stream<String> stream = Files.lines( conf.getEODOptionTickFilePath(stock) );
        stream
            .map( l -> l.split(",") )
            .filter( openInterestPredicate ) // requires there to be some open interest; this eliminates a lot of the outliers
            .filter( validDatePredicate ) // ignores tick dates not in the specified date ranges
            .forEach( rawData -> {
                OptionTick optionTick = parseOptionTick( rawData );
                
                if ( optionTick != null) // hopefully whatever code gives us the option tick properly logs the error
                    optionTicks.addTick(optionTick);
                
            });
        
        logger.console( "OptionTIckLoader::loadTicks() - Num ticks loaded: " + optionTicks.size() );
        
        return optionTicks;
    }
    
    protected static Predicate<String[]> getOpenInterestPredicate()
    {
        return l -> Integer.parseInt( l[24] ) > 0; // open interest > 0
    }
    
    protected static Predicate<String[]> getDateRangePredicate( Set<DateRange> dateRanges )
    {
        return rawData -> {
            LocalDate tickDate = LocalDate.parse( rawData[1], DateTimeFormatter.ISO_DATE );

            for ( DateRange dateRange : dateRanges )
            {
                if ( tickDate.isAfter( dateRange.getBegin().minusDays(1) ) 
                        && tickDate.isBefore(dateRange.getEnd().plusDays(1) ) )
                    return true;
            }

            return false;
        };
    }
    
    protected LocalDate determineExpirationDate( LocalDate tickDate, LocalDate expirationDate )
    {
        LocalDate thirdFriday = LocalDate.of(expirationDate.getYear(), expirationDate.getMonthValue(), 1).with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.FRIDAY));

        if ( expirationDate.isEqual( thirdFriday.plusDays(1) ) ) // if it's the third saturday, make the third friday instead
        {
            expirationDate = expirationDate.minusDays(1);
        }
        else if ( !expirationDate.isEqual( thirdFriday ) && !expirationDate.isEqual( thirdFriday.minusDays(1) ) ) // if it's not the third friday or the third thursday, skip it
        {
            dataErrorLogger.message("OptionTickLoader::determineExpirationDate() - Expiration date is not the third Thursday or Friday, so it's probably a weekly option. \n\tSymbol: " + stock.getSymbol() + " \n\tTick date: " + tickDate + " \n\tExpiration Date: " + expirationDate);
            return null;
        }

        if ( expirationDate.getDayOfWeek() == DayOfWeek.SATURDAY )
            expirationDate = expirationDate.minusDays(1);
        else if ( expirationDate.getDayOfWeek() != DayOfWeek.FRIDAY )
        {
            dataErrorLogger.message("OptionTickLoader::determineExpirationDate() - Expiration date is not a Friday. \n\tSymbol: " + stock.getSymbol() + " \n\tTick date: " + tickDate + " \n\tExpiration Date: " + expirationDate);
            return null;
        }
        
        return expirationDate;
    }
    
    protected OptionTick parseOptionTick( String[] rawData )
    {
        LocalDate tickDate = LocalDate.parse( rawData[1], DateTimeFormatter.ISO_DATE );
        LocalDate expirationDate = LocalDate.parse( rawData[3], DateTimeFormatter.ISO_DATE );
        LocalDate correctedExpirationDate = determineExpirationDate( tickDate, expirationDate );

        if ( correctedExpirationDate == null )
        {
            return null;
        }

        return new OptionTick(
            rawData[0], 
            rawData[2],
            tickDate, 
            rawData[5].equalsIgnoreCase("C") ? OptionTick.TYPE_CALL : OptionTick.TYPE_PUT, 
            Float.parseFloat( rawData[4] ),
            correctedExpirationDate,
            Float.parseFloat( rawData[6] ), 
            Float.parseFloat( rawData[9] ), 
            Float.parseFloat( rawData[7] ),
            Float.parseFloat( rawData[8] ), 
            Long.parseLong( rawData[10] ),
            Float.parseFloat( rawData[12] ),
            Float.parseFloat( rawData[14] ),
            Double.parseDouble( rawData[23])
        );
    }
}
