
import com.disposis.trading.instruments.Stock;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author blake
 */
public class Loader {
    BlockingQueue<Stock> loadQueue;
    BlockingQueue<Stock> loadCompleteQueue;

    public Loader(BlockingQueue<Stock> loadQueue, BlockingQueue<Stock> loadCompleteQueue) {
        this.loadQueue = loadQueue;
        this.loadCompleteQueue = loadCompleteQueue;
    }
    
    
}
