package com.disposis.StockDataAnalysis.Models;

import com.disposis.trading.instruments.Stock;
import java.time.LocalDate;

public class Option {
	
	public static final int TYPE_PUT = 1;
	public static final int TYPE_CALL = 0;
    
    protected final Stock stock;
    protected final LocalDate expirationDate;
    protected final float strike;
    protected final int type;
	
    public Option(Stock stock, LocalDate expirationDate, float strike, int type ) {
		super();

		this.stock = stock;
		this.expirationDate = expirationDate;
		this.strike = strike;
		this.type = type;
	}

    public Stock getStock() {
        return stock;
    }
    
	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public float getStrike() {
		return strike;
	}

	public int getType() {
		return type;
	}

    @Override
    public String toString() {
        return "Option{" + "stock=" + stock + ", expirationDate=" + expirationDate + ", strike=" + strike + ", type=" + type + '}';
    }

}
