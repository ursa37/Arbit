import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Book_Order {
	private Vector <SingleOrder > vec_Order = 		new Vector <SingleOrder>();	
	private ConcurrentHashMap<String,SingleOrder>	hmOrder0	=	new ConcurrentHashMap<String,SingleOrder>();	
	private boolean b_OrderUpdate;
	private static final Logger _logger = LoggerFactory.getLogger(Book_Order.class);	
	  
	public Book_Order()
	{
	    	//System.out.println("OrderBook In") ;
	} 	
	
	public synchronized void  AddOrder( String StockIndex, SingleOrder F_Order, String F_OrderID)	
	{			
		_logger.info( "Book_Order AddOrder  Currrency" +   StockIndex + " OrderID : " +   F_OrderID);			
		hmOrder0.put(F_OrderID , F_Order );
		SetReplyUpdate();                     
        b_OrderUpdate =true;		
	}	
	
	public Vector <SingleOrder> getOrderList()
	{
		synchronized (vec_Order) 
		  {
			return vec_Order;
		  }		
	}
	
	public synchronized void SetReplyUpdate ()	
	{
		b_OrderUpdate= true;		
	}	
	public synchronized boolean getReplyUpdate( )
	{ 		
		if (b_OrderUpdate)
		{
			b_OrderUpdate=false;			
			//System.out.println("getOrderUpdate : "+ true);
			return true;
		}
		else
		{		
			//System.out.println("getOrderUpdate : "+ false);
			return false;
		}
	}		
		
	public synchronized ConcurrentHashMap<String,SingleOrder> gethmOrder( int StockIndex )
	{		
		return hmOrder0;
	}	
	
	
	
	
	public synchronized void UpdateOB_Order ( SingleOrder F_Order , ConcurrentHashMap<String,SingleOrder> hmOrder ) 
	{
		synchronized( hmOrder )
		{
			hmOrder.put( F_Order.getOrderID(), F_Order );			
		}		
		// _logger.info( "UpdateOB_Order" +   F_Order.getOrderID()  );
	}		
}
