/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Util;

import com.disposis.util.Timer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author blake
 */
public class Timeline {
    
    private static Timeline instance;
    
    protected Map<Integer, Map<String, List<Timer>>> timeline = new HashMap<>();
    
    public void addTimer( int threadId, String type, Timer timer )
    {
        if ( timer.isRunning() )
            throw new IllegalArgumentException("Timer cannot be running.");
        
        init( threadId, type );
        
        timeline.get( threadId ).get(type).add(timer);
    }
    
    public Map<Integer, Map<String, List<Timer>>> getTimelineData()
    {
        return timeline;
    }
    
    protected void init( int threadId, String type )
    {
        if ( timeline.get( threadId ) == null )
            timeline.put( threadId, new HashMap<>() );
        
        if ( timeline.get( threadId ).get( type ) == null )
            timeline.get( threadId ).put(type, new ArrayList<>() );
        
    }
    
    public static Timeline getInstance()
    {
        if ( instance == null )
            instance = new Timeline();
        
        return instance;
    }
}
