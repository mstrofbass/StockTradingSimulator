/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disposis.StockDataAnalysis.JobService;

import com.disposis.StockDataAnalysis.Messages.JobServerShutdownMessage;
import com.disposis.StockDataAnalysis.Messages.Message;
import com.disposis.StockDataAnalysis.Messages.JobStartMessage;
import com.disposis.StockDataAnalysis.Util.Configuration;
import com.disposis.StockDataAnalysis.Util.JobServerConfiguration;
import com.disposis.trading.instruments.Stock;
import com.disposis.util.DateRange;
import com.disposis.util.Logger;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author blake
 */
public class JobServiceConnector implements JobServiceInterface {
    
    public static final String NOTIFICATION_TASK_COMPLETED = "taskCompleted";
    public static final String NOTIFICATION_TASK_FAILED = "taskFailed";
    
    public static final String STRATEGY_KEY_REBOUND = "rebound";
    public static final String STRATEGY_KEY_INIT_DAY = "initDay";
    
    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    Configuration conf = Configuration.getInstance();
    JobServerConfiguration jsConf = conf.getJobServerConfig();
    
    protected Logger logger = Logger.getInstance( Configuration.getInstance() );
    List<Stock> sampleStocks = new ArrayList<>();
    
    Request pingRequest;
    ScheduledExecutorService pingService = Executors.newSingleThreadScheduledExecutor();
    
    OkHttpClient client = new OkHttpClient();
    
    Queue<Message> messageQueue;
    
    protected String clientServerId;
    protected String jobId;

    public JobServiceConnector( Queue<Message> messageQueue ) {
        this.messageQueue = messageQueue;
    }
    
    @Override
    public void init() throws Exception
    {
        this.logger.message( "JobServiceConnector::init() - Initializing JobServiceConnector." );

        registerClient();
        initPingThread();
        
        this.logger.message( "JobServiceConnector::init() - JobServiceConnector initialized." );
    }
    
    protected void registerClient() throws Exception
    {
        logger.console( "JobServiceConnector::registerClient() - Registering client with job server." );
        
        String ipAddr = InetAddress.getLocalHost().toString();
        
        JSONObject obj = new JSONObject();

        obj.put("clientId", jsConf.getClientId() );
        obj.put("name", jsConf.getName() + "-" + ipAddr );
        obj.put("ip", ipAddr );
        obj.put("version", conf.getVersion() );
        
        JSONObject resObj;
        
        RequestBody body = RequestBody.create( JSON, obj.toString() );
        String bodyText;
        
        Request registerRequest = new Request.Builder()
            .url( jsConf.getRegisterEndpoint() )
            .post(body)
            .build();

        try (Response response = client.newCall(registerRequest).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            resObj = new JSONObject( response.body().string() );
            
            clientServerId = resObj.getJSONObject("client").getString("_id");
        }
    }
    
    protected void initPingThread() {
        logger.console( "JobServiceConnector::initPingThread() - Initializing ping thread." );
        
        JSONObject obj = new JSONObject();
        
        obj.put("_id", clientServerId );

        RequestBody body = RequestBody.create( JSON, obj.toString() );
        
        pingRequest = new Request.Builder()
                        .url( jsConf.getPingEndpoint() )
                        .post(body)
                        .build();
        
        pingService.scheduleAtFixedRate( new PingRunnable(), 0, 10, TimeUnit.SECONDS );
    }
    
    public void shutdown() {
        logger.console( "JobServiceConnector::shutdown() - Shutting down ping executor." );
        
        pingService.shutdownNow();
        
        logger.console( "JobServiceConnector::shutdown() - Executor shutdown signal sent...hopefully it'll die." );
    }

    @Override
    public Job getNextJob() throws Exception {
        JSONObject obj = new JSONObject();
        
        obj.put("clientId", jsConf.getClientId() );
        
        JSONObject resObj;
        
        RequestBody body = RequestBody.create( JSON, obj.toString() );

        Request registerRequest = new Request.Builder()
            .url( jsConf.getGetNextJobEndpoint())
            .post(body)
            .build();

        try (Response response = client.newCall(registerRequest).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response + " Body: " + response.body().string());

            String bodyString = response.body().string();
            
            resObj = new JSONObject( bodyString );
            
            if ( resObj.isNull( "job" ) )
                return null;
            
            return new Job( resObj.getJSONObject("job").getString( "_id" ) );
        }
    }
    
    @Override
    public Task getNextTask( Job job ) throws Exception {
        
        logger.console( "JobServiceConnector::getNextTask() - Getting next task." );
        
        JSONObject obj = new JSONObject();
        
        obj.put("_id", job.getId() );
        obj.put("clientId", jsConf.getClientId() );
        
        JSONObject resObj;
        
        RequestBody body = RequestBody.create( JSON, obj.toString() );
        
        Request getNextTask = new Request.Builder()
            .url( jsConf.getGetNextTaskEndpoint())
            .post(body)
            .build();

        try (Response response = client.newCall(getNextTask).execute()) 
        {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response + " Body: " + response.body().string() );

            String bodyString = response.body().string();
            
            resObj = new JSONObject( bodyString );
            
            if ( resObj.isNull( "stock" ) )
                return null;
            
            JSONObject stockObj = resObj.getJSONObject("stock");
          
            Stock stock = new Stock(
                stockObj.getString("_id"), 
                stockObj.getString("isin"), 
                stockObj.getString("symbol")
            );
            
            JSONArray jsonStrategies = resObj.getJSONArray("strategies");
            
            Set<String> strategies = new HashSet<>( jsonStrategies.length() );
            
            for ( int i = 0 ; i < jsonStrategies.length() ; i++ )
            {
                strategies.add( jsonStrategies.getString(i) );
            }
       
            JSONArray jsonDateRanges = resObj.getJSONArray("dateRanges");
            JSONObject jsonDateRange;
            
            LocalDate beginDate;
            LocalDate endDate;
            
            Set<DateRange> dateRanges = new HashSet<>( jsonDateRanges.length() );
           
            for ( int i = 0 ; i < jsonDateRanges.length() ; i++ )
            {
                jsonDateRange = jsonDateRanges.getJSONObject(i);
                
                beginDate = Instant.parse( jsonDateRange.getString( "begin" ) ).atZone( ZoneId.of("UTC") ).toLocalDate();
                endDate = !jsonDateRange.isNull("end") ? Instant.parse( jsonDateRange.getString( "end" ) ).atZone( ZoneId.of("UTC") ).toLocalDate() : null;
                
                dateRanges.add( new DateRange( beginDate, endDate ) );
            }

            return new Task( stock, strategies, dateRanges );
        }
        catch ( IOException ioe )
        {
            logger.console("JobServiceConnector::notifyCompleted() - IOException caught while trying to get next task. " + ioe.getMessage() );
        }
        
        return null;
    }
    
    public void sendStartJobMessage() {
        this.messageQueue.add(new JobStartMessage() );
    }
    
    public void sendShutdownMessage()
    {
        sendShutdownMessage( false );
    }
    
    public void sendShutdownMessage( boolean forceShutdown )
    {
        this.messageQueue.add( new JobServerShutdownMessage( forceShutdown ) );
    }

    @Override
    public void notifyCompleted( Job job, Task task ) {
        logger.console( "JobServiceConnector::notifyCompleted() - Notifying job server that task is completed." );
        
        sendNotification( job, task, NOTIFICATION_TASK_COMPLETED, task.getStock().getId() );
    }
    
    public void notifyFailed( Job job, Task task ) {
        logger.console( "JobServiceConnector::notifyCompleted() - Notifying job server that task is completed." );
        
        sendNotification( job, task, NOTIFICATION_TASK_FAILED, task.getStock().getId() );
    }
    
    protected void sendNotification( Job job, Task task, String type, String value )
    {
        JSONObject obj = new JSONObject();
        
        obj.put("_id", job.getId() );
        obj.put("clientId", jsConf.getClientId() );
        obj.put("type", type );
        obj.put("value", value );
        
        RequestBody body = RequestBody.create( JSON, obj.toString() );
        
        Request notifyRequest = new Request.Builder()
            .url( jsConf.getJobNotificationEndpoint() )
            .post(body)
            .build();

        try (Response response = client.newCall(notifyRequest).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response + " Body: " + response.body().string() );
        }
        catch ( IOException ioe )
        {
            logger.console("JobServiceConnector::notifyCompleted() - IOException caught while trying to notify job server that task was completed. " + ioe.getMessage() );
        }
    }
    
    class PingRunnable implements Runnable {
            
            @Override
            public void run() {
                logger.console("PingRunnable::run() - Entering ping thread.");
                try {
                    
                    logger.console("PingRunnable::run() - Pinging server.");

                    try (Response response = client.newCall(pingRequest).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response + " Response body: " + response.body().string());

                        JSONObject resObj = new JSONObject( response.body().string() );
            
                        boolean stayAlive = !resObj.isNull( "stayAlive" ) ? resObj.getBoolean("stayAlive") : true;
                        boolean forceShutdown = !resObj.isNull("forceShutdown") ? resObj.getBoolean("forceShutdown") : false;
                        
                        if ( !stayAlive  )
                            sendShutdownMessage( forceShutdown );
                    }
                    
                    logger.console("PingRunnable::run() - Pinging server completed.");
                }
                catch ( IOException ioe )
                {
                    logger.console( "JobServiceConnector::initPingThread() - Ping failed; may be temporary so will continue. Message: " + ioe.getMessage() );
                }
                catch ( Exception e )
                {
                    logger.console( "JobServiceConnector::initPingThread() - Ping failed; may be temporary so will continue. Message: " + e.toString() );
                }
            }
        }
}
