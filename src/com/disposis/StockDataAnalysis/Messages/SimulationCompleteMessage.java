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
public class SimulationCompleteMessage extends JobMessage {
    public SimulationCompleteMessage( Task task )
    {
        super(task);
    }
}
