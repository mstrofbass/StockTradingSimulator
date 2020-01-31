/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.JobService;

/**
 *
 * @author blake
 */
public class Job {
    protected String _id;
    
    public Job( String id )
    {
        _id = id;
    }

    public String getId() {
        return _id;
    }
}
