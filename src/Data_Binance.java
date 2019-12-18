import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Illustrates how to use the depth event stream to create a local cache of bids/asks for a symbol.
 *
 * Snapshots of the order book can be retrieved from the REST API.
 * Delta changes to the book can be received by subscribing for updates via the web socket API.
 *
 * To ensure no updates are missed, it is important to subscribe for updates on the web socket API
 * _before_ getting the snapshot from the REST API. Done the other way around it is possible to
 * miss one or more updates on the web socket, leaving the local cache in an inconsistent state.
 *
 * Steps:
 * 1. Subscribe to depth events and cache any events that are received.
 * 2. Get a snapshot from the rest endpoint and use it to build your initial depth cache.
 * 3. Apply any cache events that have a final updateId later than the snapshot's update id.
 * 4. Start applying any newly received depth events to the depth cache.
 *
 * The example repeats these steps, on a new web socket, should the web socket connection be lost.
 */
public class Data_Binance  {

  private static final String BIDS = "BIDS";
  private static final String ASKS = "ASKS";

  private final String symbol;
  private final BinanceApiRestClient restClient;
  private final BinanceApiWebSocketClient wsClient;
  private final WsCallback wsCallback = new WsCallback();
  private final Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache = new HashMap<>();

  private long lastUpdateId = -1;
  private volatile Closeable webSocket;
  private PriceUpdate feed_Binance;	
  private static final Logger _logger = LoggerFactory.getLogger(Data_Binance.class);   

  public Data_Binance(String symbol, PriceUpdate feed) {
    this.symbol = symbol;
    this.feed_Binance = feed;
    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
    this.wsClient = factory.newWebSocketClient();
    this.restClient = factory.newRestClient();
    initialize();
  }

  private void initialize() {
    // 1. Subscribe to depth events and cache any events that are received.
    final List<DepthEvent> pendingDeltas = startDepthEventStreaming();
    // 2. Get a snapshot from the rest endpoint and use it to build your initial depth cache.
    initializeDepthCache();
    // 3. & 4. handled in here.
    applyPendingDeltas(pendingDeltas);
  }

  /**
   * Begins streaming of depth events.
   *
   * Any events received are cached until the rest API is polled for an initial snapshot.
   */
  private List<DepthEvent> startDepthEventStreaming() {
    final List<DepthEvent> pendingDeltas = new CopyOnWriteArrayList<>();
    wsCallback.setHandler(pendingDeltas::add);
    this.webSocket = wsClient.onDepthEvent(symbol.toLowerCase(), wsCallback);
    return pendingDeltas;
  }

  /**
   * 2. Initializes the depth cache by getting a snapshot from the REST API.
   */
  private void initializeDepthCache() {
    OrderBook orderBook = restClient.getOrderBook(symbol.toUpperCase(), 10);

    this.lastUpdateId = orderBook.getLastUpdateId();

    NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.reverseOrder());
    for (OrderBookEntry ask : orderBook.getAsks()) {
      asks.put(new BigDecimal(ask.getPrice()), new BigDecimal(ask.getQty()));
    }
    depthCache.put(ASKS, asks);

    NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
    for (OrderBookEntry bid : orderBook.getBids()) {
      bids.put(new BigDecimal(bid.getPrice()), new BigDecimal(bid.getQty()));
    }
    depthCache.put(BIDS, bids);
  }

  /**
   * Deal with any cached updates and switch to normal running.
   */
  private void applyPendingDeltas(final List<DepthEvent> pendingDeltas) {
    final Consumer<DepthEvent> updateOrderBook = newEvent -> {
      if (newEvent.getFinalUpdateId() > lastUpdateId) {
        lastUpdateId = newEvent.getFinalUpdateId();
        updateOrderBook(getAsks(), newEvent.getAsks());
        updateOrderBook(getBids(), newEvent.getBids());   
        //StoreData();
        
        Store_DepthData();
      }
    };

    final Consumer<DepthEvent> drainPending = newEvent -> {
      pendingDeltas.add(newEvent);

      // 3. Apply any deltas received on the web socket that have an update-id indicating they come
      // after the snapshot.
      pendingDeltas.stream()
          .filter(
              e -> e.getFinalUpdateId() > lastUpdateId) // Ignore any updates before the snapshot
          .forEach(updateOrderBook);

      // 4. Start applying any newly received depth events to the depth cache.
      wsCallback.setHandler(updateOrderBook);
    };

    wsCallback.setHandler(drainPending);
  }

  /**
   * Updates an order book (bids or asks) with a delta received from the server.
   *
   * Whenever the qty specified is ZERO, it means the price should was removed from the order book.
   */
  private void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries,
                               List<OrderBookEntry> orderBookDeltas) {
    for (OrderBookEntry orderBookDelta : orderBookDeltas) {
      BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
      BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
      if (qty.compareTo(BigDecimal.ZERO) == 0) {
        // qty=0 means remove this level
        lastOrderBookEntries.remove(price);
      } else {
        lastOrderBookEntries.put(price, qty);
      }
    }
  }

  public NavigableMap<BigDecimal, BigDecimal> getAsks() {
    return depthCache.get(ASKS);
  }

  public NavigableMap<BigDecimal, BigDecimal> getBids() {
    return depthCache.get(BIDS);
  }

  /**
   * @return the best ask in the order book
   */
  public Map.Entry<BigDecimal, BigDecimal> getBestAsk() {
    return getAsks().lastEntry();
  
  }

  /**
   * @return the best bid in the order book
   */
  private Map.Entry<BigDecimal, BigDecimal> getBestBid() {
    return getBids().firstEntry();
  }

  /**
   * @return a depth cache, containing two keys (ASKs and BIDs), and for each, an ordered list of book entries.
   */
  public Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache() {
    return depthCache;
  }

  public void close() throws IOException {
    webSocket.close();
  }

  /**
   * Prints the cached order book / depth of a symbol as well as the best ask and bid price in the book.
   */
  private void printDepthCache() {
    System.out.println(depthCache);
    
    System.out.println("ASKS:(" + getAsks().size() + ")");
    getAsks().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
    
    System.out.println("BIDS:(" + getBids().size() + ")");    
    
    getBids().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
    System.out.println("BEST ASK: " + toDepthCacheEntryString(getBestAsk()));
    System.out.println("BEST BID: " + toDepthCacheEntryString(getBestBid()));
  }
    
  
  public void StoreData()
  {
	   printDepthCache();
	      
	   //Map<BigDecimal, BigDecimal> unSortedMap = getAsks();       
	   //System.out.println("Unsorted Map : " + unSortedMap);	    
	   //Map<BigDecimal, BigDecimal> sortedMap = new TreeMap<BigDecimal, BigDecimal>(unSortedMap);	    
	   //System.out.println("Sorted Map   : " + sortedMap);
	  
	   /*
	   for (Map.Entry<BigDecimal, BigDecimal> entry : nm_Ask.entrySet()) 
	   {
			System.out.println("Bid Price : " + entry.getKey() + " Bid Vol : " + entry.getValue() + "Ask Price : " + entry.getKey() + " Ask Vol : " + entry.getValue());
		
	   }
	   */
	 
	   //feed.Store_DepthData(nm_Bid ,nm_Ask );
  }
  
  public PriceUpdate GetPriceData()
  {	  
	  _logger.info( "PriceUpdate GetPriceData");
	  return feed_Binance;
  }  
  
  public   synchronized void Store_DepthData (   )
  {	  
	  
	  ArrayList<Long> ListTemp;	   
	  NavigableMap<BigDecimal, BigDecimal> nm_Ask ;
	  NavigableMap<BigDecimal, BigDecimal> nm_Bid ;
	   
	  nm_Ask = getAsks();   
	  nm_Bid = getBids();	
	  
     int b=0;
     
    // Map<BigDecimal, BigDecimal> reverseSortedMap = new TreeMap<BigDecimal, BigDecimal>(Collections.reverseOrder());       
     //reverseSortedMap.putAll(nm_Bid);
     
     for (Map.Entry<BigDecimal, BigDecimal> entry : nm_Bid.entrySet()) 
	   {
    	 feed_Binance.SetBidPrice(b, entry.getKey());
    	 feed_Binance.SetBidVol(b, entry.getValue());
  	   // _logger.info("Bid Price : " + entry.getKey() + " Bid Vol : " + entry.getValue()); 			
			b= b+1 ; 
			if (b==10)
			{
				break;
			}
				
	   }
     
     int a=0 ; 
     
     Map<BigDecimal, BigDecimal> unSortedMap =nm_Ask;       
	   //System.out.println("Unsorted Map : " + unSortedMap);	 	   
	   Map<BigDecimal, BigDecimal> sortedMap = new TreeMap<BigDecimal, BigDecimal>(unSortedMap);	    
	  // System.out.println("Sorted Map   : " + sortedMap);
     
     //Map<BigDecimal, BigDecimal> sortedMap = new TreeMap<BigDecimal, BigDecimal>(nm_Ask);      
     
     for (Map.Entry<BigDecimal, BigDecimal> entry : sortedMap.entrySet()) 
	   {
    	 feed_Binance.SetAskPrice	(a, entry.getKey());
    	 feed_Binance.SetAskVol	(a, entry.getValue());
  	   //_logger.info( "Ask Price : " + entry.getKey() + " Ask Vol : " + entry.getValue());
  	   a= a+1 ; 	
  	   if (a==10)
			{
				break;
			}
  	   
	   }	
     while (b<10)
     {
    	 feed_Binance.SetBidPrice	(b, new BigDecimal(0));
    	 feed_Binance.SetBidVol	(b, new BigDecimal(0));
  	   b= b+1 ;
     }
     while (a<10)
     {
    	 feed_Binance.SetAskPrice	(a, new BigDecimal(0));
    	 feed_Binance.SetAskVol	(a, new BigDecimal(0));
  	   	a= a+1 ;
     }
     //System.out.println("Bid b    : " + b);
     //System.out.println("Ask a    : " + a);
     feed_Binance.b_BidPriceEmpty = true;
     feed_Binance.b_AskPriceEmpty = true;
     feed_Binance.b_BidVolEmpty = true;
     feed_Binance.b_AskVolEmpty = true;
	} 
  
  
  /**
   * Pretty prints an order book entry in the format "price / quantity".
   */
  private static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
    return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
  }

  
  public static void main(String[] args)
  {
    //new Data("ETHBTC");	  
	  PriceUpdate fee1  = new PriceUpdate();
	  Data_Binance kk = new Data_Binance( "ETHBTC", fee1	);	 
  }
  
  private final class WsCallback implements BinanceApiCallback<DepthEvent> {
    private final AtomicReference<Consumer<DepthEvent>> handler = new AtomicReference<>();
    @Override
    public void onResponse(DepthEvent depthEvent) {
      try {
        handler.get().accept(depthEvent);
      } catch (final Exception e) {
    	  _logger.info("Exception caught processing depth event");
        _logger.info(System.err.toString());
        e.printStackTrace(System.err);       
      }
    }

    @Override
    public void onFailure(Throwable cause) {
      //System.out.println("WS connection failed. Reconnecting. cause:" + cause.getMessage());
      _logger.info("WS connection failed. Reconnecting. cause:" + cause.getMessage());
      initialize();
    }

    private void setHandler(final Consumer<DepthEvent> handler) {
      this.handler.set(handler);
    }
  }
}
