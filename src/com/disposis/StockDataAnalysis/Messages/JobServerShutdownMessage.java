/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Messages;

/**
 *
 * @author blake
 */
public class JobServerShutdownMessage extends Message {
    protected boolean forceShutdown = false;
    
    public JobServerShutdownMessage( boolean forceShutdown )
    {
        this.forceShutdown = forceShutdown;
    }

    public boolean isForcedShutdown() {
        return forceShutdown;
    }
}
