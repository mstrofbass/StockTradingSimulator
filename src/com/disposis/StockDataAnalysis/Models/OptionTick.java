package com.disposis.StockDataAnalysis.Models;

import java.time.LocalDate;

public class OptionTick {
	
	public static final int TYPE_CALL = 0;
	public static final int TYPE_PUT = 1;
	
    protected String underlyingSymbol;
	
	protected String root;
	
	
    protected LocalDate quoteTimestamp;
	
	
    protected int type;
	
	
    protected float strike;
	
	
    protected LocalDate expirationDate;
   
	
    protected float open;
	
	
    protected float close;
	
	
    protected float high;
	
	
    protected float low;
    

    protected long volume;
    
    protected float bid1545;

    protected float ask1545;
    
    protected double vwap;
    
    protected OptionTick()
    {
    		
    }
    
    public OptionTick(
    						String underlyingSymbol, 
    						String root,
    						LocalDate quoteDatetime, 
    						int type, 
    						float strike, 
    						LocalDate expirationDate, 
    						float open, 
    						float close, 
    						float high,
    						float low, 
    						long volume,
    						float bid1545,
    						float ask1545,
    						double vwap
    					) 
    {
		super();
		this.underlyingSymbol = underlyingSymbol;
		this.root = root;
		this.quoteTimestamp = quoteDatetime;
		this.type = type;
		this.strike = strike;
		this.expirationDate = expirationDate;
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.volume = volume;
		this.bid1545 = bid1545;
		this.ask1545 = ask1545;
		this.vwap = vwap;
	}

    public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
	public String getUnderlyingSymbol() {
		return underlyingSymbol;
	}

	protected void setUnderlyingSymbol(String underlyingSymbol) {
		this.underlyingSymbol = underlyingSymbol;
	}

	public LocalDate getQuoteTimestamp() {
		return quoteTimestamp;
	}

	protected void setQuoteTimestamp(LocalDate quoteTimestamp) {
		this.quoteTimestamp = quoteTimestamp;
	}

	public int getType() {
		return type;
	}

	protected void setType(int type) {
		this.type = type;
	}

	public float getStrike() {
		return strike;
	}

	protected void setStrike(float strike) {
		this.strike = strike;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	protected void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public float getOpen() {
		return open;
	}

	protected void setOpen(float open) {
		this.open = open;
	}

	public float getClose() {
		return close;
	}

	protected void setClose(float close) {
		this.close = close;
	}

	public float getHigh() {
		return high;
	}

	protected void setHigh(float high) {
		this.high = high;
	}

	public float getLow() {
		return low;
	}

	protected void setLow(float low) {
		this.low = low;
	}

	public long getVolume() {
		return volume;
	}

	protected void setVolume(long volume) {
		this.volume = volume;
	}

	public float getBid1545() {
		return bid1545;
	}

	protected void setBid1545(float bid1545) {
		this.bid1545 = bid1545;
	}

	public float getAsk1545() {
		return ask1545;
	}

	protected void setAsk1545(float ask1545) {
		this.ask1545 = ask1545;
	}

	public double getVWAP() {
		return vwap;
	}

	public void setVWAP(double vwap) {
		this.vwap = vwap;
	}

	@Override
	public String toString() {
		return "OptionTick [underlyingSymbol=" + underlyingSymbol + ", quoteTimestamp=" + quoteTimestamp + ", strike=" + strike + ", expirationDate=" + expirationDate
				+ ", open=" + open + ", close=" + close + ", high=" + high + ", low=" + low + ", volume=" + volume
				+ "]";
	}

}
