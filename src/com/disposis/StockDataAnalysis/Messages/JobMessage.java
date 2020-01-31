/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Messages;

import com.disposis.StockDataAnalysis.JobService.Task;

/**
 *
 * @author blake
 */
abstract public class JobMessage extends Message {
    protected Task task;
    
    public JobMessage( Task task )
    {
        super();
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
