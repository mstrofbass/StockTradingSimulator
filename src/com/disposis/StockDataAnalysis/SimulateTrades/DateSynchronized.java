package com.disposis.StockDataAnalysis.SimulateTrades;

import java.time.LocalDate;

public class DateSynchronized {
	
	protected static LocalDate currentDate;
	
	public static LocalDate getCurrentDate() {
		return currentDate;
	}

	public static void setCurrentDate(LocalDate currentDate) {
		DateSynchronized.currentDate = currentDate;
	}
}
