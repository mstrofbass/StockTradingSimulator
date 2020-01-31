/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Util.FileProcessor;

import java.io.File;

/**
 *
 * @author blake
 */
public interface FileProcessorFunction {
    public void process( File inputFile, File outputFile );
}
