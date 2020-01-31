package com.disposis.StockDataAnalysis;

public class StockDataAnalysisTester {

	public static void main(String[] args) {
		
		if ( args.length < 1 )
		{
			System.err.println( "A command is required." );
			StockDataAnalysisTester.printUsage();
			return;
		}
		
		String command = args[0];
		
		System.out.println(command);
		
		try {
			
			switch ( command.toLowerCase() )
			{
				case "loadoptionticks":
					StockDataAnalysisTester.testLoadOptionTicks();
				break;
				
				case "optiontickqueries":
					StockDataAnalysisTester.testOptionTickQueries();
				break;
					
				case "optionticks":
				default:
					StockDataAnalysisTester.testOptionTicks();
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		return;

	}
	
	protected static void testOptionTicks() throws Exception
	{     
		System.out.println( "Testing option ticks." );
		
//		Path optionTickDataDirPath = conf.getOptionTickDataPath();
//		BufferedReader optionFileBufferedReader;
//		OptionTicks optionTicks = new OptionTicks();
//		
//		File dir = optionTickDataDirPath.toFile();
//		File[] directoryListing = dir.listFiles();
//		  
//		if (directoryListing != null) 
//		{
//		    for (File child : directoryListing) 
//		    {
//		    	
//		    		System.out.println("Reading from file: " + child.toString() );
//		    	
//		    		try {
//		    			optionFileBufferedReader = new BufferedReader( new FileReader( child ) );
//		    			
//		    			// get rid of the header row
//		    			optionFileBufferedReader.readLine();
//		    			
////		    			optionTicks.loadTicks(optionFileBufferedReader);
//		    			
//		    		}
//		    		catch (Exception e)
//		    		{
//		    			System.out.println("Exception was thrown: " + e.toString() );
//		    			System.out.println( Thread.currentThread().getStackTrace()[2].getLineNumber() );
//		    			return;
//		    		}
//		    }
//		} 
//		else 
//		{
//			System.out.println("Error occurred. Directory listing is null.");
//		}
//		
//		System.out.println(optionTicks);
	}
	
	protected static void testLoadOptionTicks() throws Exception
	{     
//		System.out.println( "Testing option tick loading." );
//		
//		EntityManagerFactory emfactory = Persistence.createEntityManagerFactory( "stock_data_test" );
//	      
//		EntityManager em = emfactory.createEntityManager( );
//		em.getTransaction( ).begin( );
//		
//		Query query = em.createQuery("DELETE FROM OptionTick ot");
//		int rowCount = query.executeUpdate();
//		
//		query = em.createQuery("DELETE FROM IntradayOptionTick ot");
//		rowCount = query.executeUpdate();
//		
//		ZonedDateTime quoteDateTime = ZonedDateTime.of( 2008, 1, 18, 0, 0, 0, 0, ZoneId.of("America/New_York") );
//		ZonedDateTime expirationDate = ZonedDateTime.of( 2008, 2, 16, 0, 0, 0, 0, ZoneId.of("America/New_York") );
//		
//		OptionTick optionTick = new OptionTick("AVP", quoteDateTime, OptionTick.TYPE_CALL, 35, expirationDate, 2.5f, 2.5f, 2.68f, 2.05f, 1001, 2.68f, 2.99f);
//		
//		em.persist( optionTick );
//		
//		quoteDateTime = ZonedDateTime.of( 2008, 1, 19, 0, 0, 0, 0, ZoneId.of("America/New_York") );
//		expirationDate = ZonedDateTime.of( 2008, 2, 16, 0, 0, 0, 0, ZoneId.of("America/New_York") );
//		
//		optionTick = new OptionTick("AVP", quoteDateTime, OptionTick.TYPE_CALL, 35, expirationDate, 2.9f, 2.9f, 3.08f, 2.33f, 1301, 3.08f, 3.99f);
//		
//		em.persist( optionTick );
//		
//		em.getTransaction( ).commit( );
//	    
//	    query = em.createQuery("Select ot FROM OptionTick ot ORDER BY ot.high DESC");
//	    List<OptionTick> result = query.getResultList();
//	    
//	    for ( OptionTick ot : result )
//	    {
//	    		System.out.println( "\t" + ot );
//	    }
//	    
//	    em.getTransaction( ).begin( );
//		
//		quoteDateTime = ZonedDateTime.of( 2008, 1, 18, 12, 52, 30, 0, ZoneId.of("America/New_York") );
//		expirationDate = ZonedDateTime.of( 2008, 2, 16, 0, 0, 0, 0, ZoneId.of("America/New_York") );
//		
//		optionTick = new IntradayOptionTick("AVP", quoteDateTime, OptionTick.TYPE_CALL, 35, expirationDate, 2.5f, 2.5f, 2.68f, 2.05f, 1001, 2.68f, 2.99f, 28.95f, 29.30f);
//		
//		em.persist( optionTick );
//		
//		quoteDateTime = ZonedDateTime.of( 2008, 1, 18, 14, 52, 00, 0, ZoneId.of("America/New_York") );
//		expirationDate = ZonedDateTime.of( 2008, 2, 16, 0, 0, 0, 0, ZoneId.of("America/New_York") );
//		
//		optionTick = new IntradayOptionTick("AVP", quoteDateTime, OptionTick.TYPE_CALL, 35, expirationDate, 2.9f, 2.9f, 3.08f, 2.33f, 1301, 3.08f, 3.99f, 30.95f, 31.30f);
//		
//		em.persist( optionTick );
//		
//		em.getTransaction( ).commit( );
//	    
//	    query = em.createQuery("Select ot FROM IntradayOptionTick ot ORDER BY ot.high DESC");
//	    List<IntradayOptionTick> iotResults = query.getResultList();
//	    
//	    for ( IntradayOptionTick ot : iotResults )
//	    {
//	    		System.out.println( "\t" + ot );
//	    }
//		
//		System.out.println( "Done testing option tick loading." );
	}

	protected static void testOptionTickQueries()
	{
//		System.out.println( "Testing option tick queries." );
//		
//		EntityManagerFactory emfactory = Persistence.createEntityManagerFactory( "stock_data" );
//		EntityManager em = emfactory.createEntityManager( );
//		
//		StockDataAnalysisTester.testOptionPossiblesQuery();
		
		
	}
	
	protected static void testOptionPossiblesQuery()
	{
//		System.out.println( "Testing option possibles query." );
//		
//		ZonedDateTime earliestExpirationDate = ZonedDateTime.of( 2008, 1, 19, 0, 0, 0, 0, ZoneId.of("America/New_York") );
//		
//		ZonedDateTime quoteTimestamp = ZonedDateTime.of( 2008, 1, 3, 15, 58, 30, 0, ZoneId.of("America/New_York") );
//		
//		String queryString = "SELECT " + 
//				"    oti " + 
//				"FROM " + 
//				"	IntradayOptionTick oti " + 
//				"WHERE " + 
//				"	oti.underlyingSymbol = :underlyingSymbol " + 
//				"		AND\n" + 
//				"	oti.quoteTimestamp = :quoteTimestamp " + 
//				"		AND " + 
//				"	oti.type = :type " + 
//				"		AND " + 
//				"	( " + 
//				"		oti.strike = ( " + 
//				"			SELECT " + 
//				"				MAX(oti.strike) " + 
//				"			FROM " + 
//				"				IntradayOptionTick oti " + 
//				"			WHERE " + 
//				"				oti.quoteTimestamp = :quoteTimestamp " + 
//				"					AND " + 
//				"				oti.type = :type " + 
//				"					AND " + 
//				"				oti.strike < :strike " + 
//				"		)" + 
//				"			OR" + 
//				"		oti.strike = (" + 
//				"			SELECT " + 
//				"				MIN(oti.strike)" + 
//				"			FROM " + 
//				"				IntradayOptionTick oti" + 
//				"			WHERE" + 
//				"				oti.quoteTimestamp = :quoteTimestamp " + 
//				"					AND " + 
//				"				oti.type = :type " + 
//				"					AND" + 
//				"				oti.strike > :strike" + 
//				"		)" + 
//				"	)" + 
//				"		AND" + 
//				"	oti.expirationDate = (" + 
//				"		SELECT " + 
//				"			MIN(oti.expirationDate)" + 
//				"		FROM " + 
//				"			IntradayOptionTick oti" + 
//				"		WHERE" + 
//				"			oti.quoteTimestamp = :quoteTimestamp " + 
//				"				AND" + 
//				"			oti.expirationDate >  :expirationDate " + 
//				"    )";
//		
//		EntityManagerFactory emfactory = Persistence.createEntityManagerFactory( "stock_data" );
//	      
//		EntityManager em = emfactory.createEntityManager( );
//		TypedQuery<IntradayOptionTick> optionPossiblesQuery = em.createQuery( queryString, IntradayOptionTick.class );
//		
//		optionPossiblesQuery.setParameter("underlyingSymbol", "AVP");
//		optionPossiblesQuery.setParameter("quoteTimestamp", quoteTimestamp);
//		optionPossiblesQuery.setParameter("expirationDate", earliestExpirationDate);
//		optionPossiblesQuery.setParameter("type", 0);
//		optionPossiblesQuery.setParameter("strike", 39.67f);
//		
//		List<IntradayOptionTick> resultset = optionPossiblesQuery.getResultList();
//		
//		for ( IntradayOptionTick ot : resultset )
//		{
//			System.out.println("\tPossible strike price: " + ot.getStrike() );
//		}
//		
//		System.out.println( "Done testing option possibles query." );
	}
	
	protected static void printUsage()
	{
		System.out.println(
			"Usage: \n\n" +
			"StockDataAnalysisTest.jar command\n\n" +
			"Commands: optionticks"
		);
		
	}
		
}
