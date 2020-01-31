package com.disposis.StockDataAnalysis.Models;

import java.time.LocalDate;

public class OptionTransaction {
    
    protected final Option option;
    
    protected final LocalDate buyDate;
    protected final int quantity;
    protected final float buyPrice;
    protected final float cost;
    protected LocalDate sellDate;
    protected float sellPrice = 0;
    protected float proceeds = 0;
    protected boolean isRealPrice = false;
    
    protected float buyTriggerPct;
    protected float sellTriggerPct;
    
    protected StockTick stockTick;
    protected final OptionTick optionTick;
	
    public OptionTransaction(Option option, LocalDate buyDate, int quantity, float buyPrice, boolean isRealPrice, OptionTick optionTick ) {
		super();
        
        this.option = option;

		this.buyDate = buyDate;
		this.quantity = quantity;
		this.buyPrice = buyPrice;
		this.cost = quantity * (buyPrice * 100);
		
		this.isRealPrice = isRealPrice;
		
		this.optionTick = optionTick;
	}
    
    public boolean isBuy() {
        return sellDate == null;
    }
    
    public boolean isSale() {
        return !isBuy();
    }
    
    public boolean wasSold()
    {
        return sellDate != null;
    }

    public Option getOption() {
        return option;
    }

	public LocalDate getBuyDate() {
		return buyDate;
	}

	public float getBuyPrice() {
		return buyPrice;
	}

	public LocalDate getSellDate() {
		return sellDate;
	}

	public void setSellDate(LocalDate sellDate) {
		this.sellDate = sellDate;
	}

	public float getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(float sellPrice) {
		this.sellPrice = sellPrice;
		
		this.proceeds = 100 * sellPrice * quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public float getCost() {
		return cost;
	}

	public float getProceeds() {
		return proceeds;
	}

	public void setProceeds(float proceeds) {
		this.proceeds = proceeds;
	}

	public boolean isRealPrice() {
		return isRealPrice;
	}
    
    public void setIsRealPrice( boolean isRealPrice) {
		this.isRealPrice = isRealPrice;
	}
	
	public float getBuyTriggerPct() {
		return buyTriggerPct;
	}

	public void setBuyTriggerPct(float buyTriggerPct) {
		this.buyTriggerPct = buyTriggerPct;
	}

	public float getSellTriggerPct() {
		return sellTriggerPct;
	}

	public void setSellTriggerPct(float sellTriggerPct) {
		this.sellTriggerPct = sellTriggerPct;
	}

	public StockTick getStockTick() {
		return stockTick;
	}

	public void setStockTick(StockTick stockTick) {
		this.stockTick = stockTick;
	}

	public OptionTick getOptionTick() {
		return optionTick;
	}

    @Override
    public String toString() {
        return "OptionTransaction{" + "option=" + option + ", buyDate=" + buyDate + ", quantity=" + quantity + ", buyPrice=" + buyPrice + ", cost=" + cost + ", sellDate=" + sellDate + ", sellPrice=" + sellPrice + ", proceeds=" + proceeds + ", isRealPrice=" + isRealPrice + ", buyTriggerPct=" + buyTriggerPct + ", sellTriggerPct=" + sellTriggerPct + ", stockTick=" + stockTick + ", optionTick=" + optionTick + '}';
    }

	
}
