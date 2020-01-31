package com.disposis.StockDataAnalysis.Models;

import java.time.LocalDate;

public class StockTick {
	
	private String symbol = null;
	private LocalDate timestamp;
	private float open = 0;
	private float high = 0;
	private float low = 0;
	private float close = 0;
	private float adjClose = 0;
	
	public StockTick(String symbol, LocalDate timestamp, float open, float high, float low, float close, float adjClose) {
		super();
		
		this.symbol = symbol;
		this.timestamp = timestamp;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.adjClose = adjClose;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public LocalDate getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDate timestamp) {
		this.timestamp = timestamp;
	}

	public float getOpen() {
		return open;
	}

	public void setOpen(float open) {
		this.open = open;
	}

	public float getHigh() {
		return high;
	}

	public void setHigh(float high) {
		this.high = high;
	}

	public float getLow() {
		return low;
	}

	public void setLow(float low) {
		this.low = low;
	}

	public float getClose() {
		return close;
	}

	public void setClose(float close) {
		this.close = close;
	}

	public float getAdjClose() {
		return adjClose;
	}

	public void setAdjClose(float adjClose) {
		this.adjClose = adjClose;
	}
	
	
}
