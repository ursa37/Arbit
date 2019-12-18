import java.math.BigDecimal;
import java.util.List;
import java.util.NavigableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.binance.api.client.BinanceApiClientFactory;
import com.huobi.client.SubscriptionClient;
import com.huobi.client.SubscriptionOptions;
import com.huobi.client.model.DepthEntry;
import com.huobi.client.model.enums.CandlestickInterval;

public class Data_Huobi {
private PriceUpdate feed;	
private final String symbol;
private static final Logger _logger = LoggerFactory.getLogger(Data_Huobi.class);   

public Data_Huobi(String symbol, PriceUpdate feed) {
    this.symbol = symbol;
    this.feed = feed;
    StoreData();
  }	 
	

public PriceUpdate GetPriceData()
{	  
	  _logger.info( "PriceUpdate GetPriceData");
	  return feed;
}  

public void StoreData()
{
	 try {
		 SubscriptionClient subscriptionClient = SubscriptionClient.create();
		    subscriptionClient.subscribePriceDepthEvent(symbol, (priceDepthEvent) -> 
		    {
		      System.out.println("------------Subscribe Huobi Price Depth-----------------");	      
		      System.out.println("bids 0 price: " + priceDepthEvent.getData().getBids().get(0).getPrice());
		      System.out.println("bids 0 volume: " + priceDepthEvent.getData().getBids().get(0).getAmount());	      
		      List<DepthEntry> bids =     priceDepthEvent.getData().getBids();
		      List<DepthEntry> asks =     priceDepthEvent.getData().getAsks();
		      int a=0 ;
		      int b=0 ;
		      
		      for (  a=0;a<asks.size(); a++)
		      {
		    	  if (a==10)
					{
						break;
					}		    	  
		    	  //System.out.println("Ask "+a+" price: " + priceDepthEvent.getData().getAsks().get(a).getPrice());
			      //System.out.println("Ask "+a+" volume: " + priceDepthEvent.getData().getAsks().get(a).getAmount());
		    	  feed.SetAskPrice	(a, asks.get(a).getPrice());
		    	  feed.SetAskVol	(a, asks.get(a).getAmount());		    	  
		      } 	
		      
		      for ( b=0;b<bids.size(); b++)
		      {
		    	  if (b==10)
					{
						break;
					}		    	  
		      	  //System.out.println("bids "+b+" price: " + bids.get(b).getPrice());
			      //System.out.println("bids "+b+" volume: " + bids.get(b).getAmount());
		    	  feed.SetBidPrice	(b, bids.get(b).getPrice());
		    	  feed.SetBidVol	(b, bids.get(b).getAmount());			      
		      } 
		    
		      while (b<10)
		      {
		    	 //System.out.println("bids  deth: ");
		     	 feed.SetBidPrice	(b, new BigDecimal(0));
		     	 feed.SetBidVol	(b, new BigDecimal(0));
		   	   	 b= b+1 ;
		      }
		      while (a<10)
		      {
		    	 //System.out.println("ASk  deth: ");
		     	 feed.SetAskPrice	(a, new BigDecimal(0));
		     	 feed.SetAskVol	(a, new BigDecimal(0));
		     	 a= a+1 ;
		      }
		      
		    });
	 }    
    catch (final Exception e) {
    	  _logger.info("Exception caught processing depth event");
        _logger.info(System.err.toString());
        e.printStackTrace(System.err);       
      }
}
  public static void main(String[] args) {
	  PriceUpdate fee1  = new PriceUpdate();
	  Data_Huobi kk = new Data_Huobi( "ethbtc", fee1	);	
  }
}
