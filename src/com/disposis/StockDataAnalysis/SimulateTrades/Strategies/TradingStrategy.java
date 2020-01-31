package com.disposis.StockDataAnalysis.SimulateTrades.Strategies;

import com.disposis.StockDataAnalysis.Models.OptionTransaction;
import com.disposis.StockDataAnalysis.SimulateTrades.Trader;
import com.disposis.trading.ticks.StockTick;
import java.util.Set;

public abstract class TradingStrategy extends Strategy {
    
    public class Transactions {
        public final Set<OptionTransaction> buy;
        public final Set<OptionTransaction> sell;

        public Transactions(Set<OptionTransaction> buy, Set<OptionTransaction> sell) {
            this.buy = buy;
            this.sell = sell;
        }
    }
    
	protected static class Tracker {}
    
    protected Trader trader = null;
    
    OptionSelectionStrategy iss;
	
	protected String label = "";
    protected Transactions transactions;
	
	public TradingStrategy( OptionSelectionStrategy iss, String label ) {
		super( label );
		
        this.iss = iss;
	}
    
    public TradingStrategy( String label ) throws Exception {
		super( label );
	}
	
	public abstract void feedTick( StockTick tick );
    
    public Transactions getTransactions() {
        return transactions;
    }
}
