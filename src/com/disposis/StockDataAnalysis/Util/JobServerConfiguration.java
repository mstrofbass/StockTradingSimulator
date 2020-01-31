/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.Util;

import com.disposis.exceptions.ConfigException;
import com.disposis.util.BaseConfiguration;

/**
 *
 * @author blake
 */
public class JobServerConfiguration extends BaseConfiguration {
    
    private static JobServerConfiguration instance;
    
    protected String url;
    protected String clientId;
    protected String name;
    
    protected String clientsEndpoint;
    protected String registerEndpoint;
    protected String pingEndpoint;
    
    protected String getJobsEndpoint;
    protected String jobNotificationEndpoint;
    protected String getNextJobEndpoint;
    protected String getNextTaskEndpoint;
    
    protected JobServerConfiguration()
    {
        super();
    }

    @Override
    protected void init() throws ConfigException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
        
        if ( !conf.hasPath("jobServer.url") )
			throw new ConfigException("Job server URL not set in config file.");
        
        if ( !conf.hasPath("jobServer.clientId") )
			throw new ConfigException("Client id not set in config file.");
        
        if ( !conf.hasPath("jobServer.name") )
			throw new ConfigException("Name not set in config file.");
        
        url = conf.getString("jobServer.url");
        clientId = conf.getString("jobServer.clientId");
        name = conf.getString("jobServer.name");
        
        loadEndpoints();
    }
    
    protected void loadEndpoints() throws ConfigException {
        if ( !conf.hasPath("jobServer.endpoints.register") )
			throw new ConfigException("Register endpoint url not set in config file.");
        
        if ( !conf.hasPath("jobServer.endpoints.ping") )
			throw new ConfigException("Ping URL not set in config file.");
        
        if ( !conf.hasPath("jobServer.endpoints.clients") )
			throw new ConfigException("Clients URL not set in config file.");
        
        if ( !conf.hasPath("jobServer.endpoints.getJobs") )
			throw new ConfigException("Get jobs URL not set in config file.");
        
        if ( !conf.hasPath("jobServer.endpoints.jobNotification") )
			throw new ConfigException("Job notification URL not set in config file.");
        
        if ( !conf.hasPath("jobServer.endpoints.getNextJob") )
			throw new ConfigException("Get next job URL not set in config file.");
        
        if ( !conf.hasPath("jobServer.endpoints.getNextTask") )
			throw new ConfigException("Get next task URL not set in config file.");
        
        registerEndpoint = url + conf.getString("jobServer.endpoints.register");
        clientsEndpoint = url + conf.getString("jobServer.endpoints.clients");
        pingEndpoint = url + conf.getString("jobServer.endpoints.ping");
        getJobsEndpoint = url + conf.getString("jobServer.endpoints.getJobs");
        jobNotificationEndpoint = url + conf.getString("jobServer.endpoints.jobNotification");
        getNextJobEndpoint = url + conf.getString("jobServer.endpoints.getNextJob");
        getNextTaskEndpoint = url + conf.getString("jobServer.endpoints.getNextTask");
        
    }

    public String getClientsEndpoint() {
        return clientsEndpoint;
    }

    public String getURL() {
        return url;
    }
    
    public String getRegisterEndpoint() {
        return registerEndpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }
    
    public String getPingEndpoint() {
        return pingEndpoint;
    }

    public String getGetJobsEndpoint() {
        return getJobsEndpoint;
    }

    public String getJobNotificationEndpoint() {
        return jobNotificationEndpoint;
    }
    
    public String getGetNextJobEndpoint() {
        return getNextJobEndpoint;
    }

    public String getGetNextTaskEndpoint() {
        return getNextTaskEndpoint;
    }
    
	public static JobServerConfiguration getInstance()
	{
		if ( JobServerConfiguration.instance == null )
		{
			try {
				JobServerConfiguration.instance = new JobServerConfiguration();
                JobServerConfiguration.instance.init();
			}
			catch (ConfigException e) {
				System.out.println( String.format("Configuration::getInstance() - Exception thrown while trying to instantiate a new configuration instance. Msg: %s", e.getMessage()));
				return null;
			}
		}
		
		return JobServerConfiguration.instance;
	}
    
}
