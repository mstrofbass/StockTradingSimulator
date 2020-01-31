/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Util;

import com.disposis.exceptions.ConfigException;
import com.disposis.trading.instruments.Stock;
import com.disposis.util.BaseConfiguration;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author blake
 */
public class Configuration extends BaseConfiguration {
    
    private static Configuration instance;
    
    protected String version;
    
    protected RunMode runMode = RunMode.DEBUG;
    protected int maxThreads;
    
    protected String baseDataDir;
    
    protected String stockMetadataFile;
    
    protected String rawIntradayStockTickDataDir;
    protected String eodStockTickDataDir;
    
    protected String eodOptionTickDataDir;
    
    protected String jobServerURL;
    
    protected JobServerConfiguration jsConfig;
    
    protected Configuration()
    {
        super();
    }

    @Override
    protected void init() throws ConfigException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
        
        if ( !conf.hasPath("app.version") )
			throw new ConfigException("Version not set in config file.");
        
        if ( !conf.hasPath("app.runMode") )
			throw new ConfigException("runMode not set in config file.");
        
        if ( !conf.hasPath("app.maxThreads") )
			throw new ConfigException("maxThreads not set in config file.");
        
        if ( !conf.hasPath("app.baseDataDir") )
			throw new ConfigException("baseDataDir not set in config file.");
        
        if ( !conf.hasPath("jobServer.url") )
			throw new ConfigException("Job server URL not set in config file.");
        
        maxThreads = conf.getInt("app.maxThreads");
        
        
        version = conf.getString("app.version");
        
        switch ( conf.getString( "app.runMode" ) )
        {
            case "DEBUG":
                runMode = RunMode.DEBUG;
            break;
            
            case "TUNE":
                runMode = RunMode.TUNE;
            break;
            
            case "FULL":
                runMode = RunMode.FULL;
            break;
        }
        
        baseDataDir = conf.getString("app.baseDataDir");
        
        stockMetadataFile = baseDataDir + "/stock-data/metadata/stock-data.conf";
        rawIntradayStockTickDataDir = baseDataDir + "/stock-data/ticks/intraday/raw";
        eodStockTickDataDir = baseDataDir + "/stock-data/ticks/eod/raw";
        
        eodOptionTickDataDir = baseDataDir + "/option-data/ticks/eod/raw";
        
        jsConfig = JobServerConfiguration.getInstance();
    }

    public String getVersion() {
        return version;
    }

    public RunMode getRunMode() {
        return runMode;
    }

    public void setRunMode(RunMode runMode) {
        this.runMode = runMode;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public String getStockMetadataFile() {
        return stockMetadataFile;
    }

    public void setStockMetadataFile(String stockMetadataFile) {
        this.stockMetadataFile = stockMetadataFile;
    }
    
    public String getRawIntradayStockTickDataDir()
    {
        return rawIntradayStockTickDataDir;
    }
    
    public String getEODStockTickDataDir()
    {
        return eodStockTickDataDir;
    }
    
    public Path getEODStockTickFilePath( Stock stock )
    {
        return Paths.get( getEODStockTickDataDir(), stock.getSymbol() + "." + stock.getISIN() + ".csv" );
    }
    
    public String getEODOptionTickDataDir()
    {
        return eodOptionTickDataDir;
    }
    
    public Path getEODOptionTickFilePath( Stock stock )
    {
        return Paths.get( getEODOptionTickDataDir(), stock.getSymbol() + "." + stock.getISIN() + ".csv" );
    }
    
    public String getOutputDir()
    {
        return baseDataDir + "/logs";
    }
    
    public Path getOptionTransOutputFilePath( Stock stock )
    {
        return Paths.get(getOutputDir(), "option-trans." + stock.getSymbol() + "." + stock.getISIN() + ".csv");
    }
    
    public Path getWeightedTreeOutputFilePath( Stock stock )
    {
        return Paths.get(getOutputDir(), "nn." + stock.getSymbol() + "." + stock.getISIN() + ".csv");
    }
    
    public Path getCombinedWeightedTreeOutputFilePath()
    {
        return Paths.get(getOutputDir(), "nn.combined.csv");
    }
    
    public Path getTimelineOutputFilePath()
    {
        return Paths.get(getOutputDir(), "timeline.out");
    }
    
    public Path getProgressLogOutputFilePath()
    {
        return Paths.get(getOutputDir(), "progress.log");
    }

    public JobServerConfiguration getJobServerConfig() {
        return jsConfig;
    }
    
	public static Configuration getInstance()
	{
		if ( Configuration.instance == null )
		{
			try {
				Configuration.instance = new Configuration();
                Configuration.instance.init();
			}
			catch (ConfigException e) {
				System.out.println( String.format("Configuration::getInstance() - Exception thrown while trying to instantiate a new configuration instance. Msg: %s", e.getMessage()));
				return null;
			}
		}
		
		return Configuration.instance;
	}
    
}
