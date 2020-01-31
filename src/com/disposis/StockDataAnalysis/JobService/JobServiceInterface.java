/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.JobService;

import com.disposis.trading.instruments.Stock;

/**
 *
 * @author blake
 */
public interface JobServiceInterface {
    public void init() throws Exception;
    public Job getNextJob() throws Exception;
    public Task getNextTask( Job job ) throws Exception;
    public void notifyCompleted( Job job, Task task );
}
