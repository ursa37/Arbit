import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.TreeMap;
import java.util.Collections;

public class PriceUpdate {
    private String Symbol="";    
  	
	private BigDecimal [] BidVol = new BigDecimal[10];
	private BigDecimal [] BidPrice= new BigDecimal[10] ;	
	private BigDecimal [] AskVol = new BigDecimal[10];
	private BigDecimal [] AskPrice = new BigDecimal[10];
	public boolean b_TSUpdate 	= false;	
    public boolean b_BidPriceEmpty 	= false;
    public boolean b_AskPriceEmpty 	= false;
    public boolean b_BidVolEmpty 	= false;
    public boolean b_AskVolEmpty 	= false;
    private boolean LadderUpdate =false;    
    private static final Logger _logger = LoggerFactory.getLogger(PriceUpdate.class);    
    private boolean b_DataUpdate = false;
          
    public PriceUpdate()
    {
    	//System.out.println("PriceFeedSim In") ;
    }     
    public synchronized void SetBidPrice(int level, BigDecimal F_BidPrice )
    {    	
    	try{
    		this.BidPrice[level] = F_BidPrice;    	
        	b_BidPriceEmpty = true; 
    	}
    	 catch (final Exception e) {
       	  _logger.info("Exception caught SetBidPrice");
           _logger.info(System.err.toString());
           e.printStackTrace(System.err);       
         }
    }    
    public synchronized void SetBidVol(int level, BigDecimal  F_BidVol )
    {
    	try{
    		this.BidVol[level]  	=	F_BidVol;
        	b_BidVolEmpty = true;
    	}
    	 catch (final Exception e) {
          	  _logger.info("Exception caught SetBidVol");
              _logger.info(System.err.toString());
              e.printStackTrace(System.err);       
            }
    }    
    public synchronized void SetAskPrice(int level, BigDecimal F_AskPrice)
    {    	
    	try
    	{
    		this.AskPrice [level]	=	F_AskPrice ;    	
        	b_AskPriceEmpty = true;
    	}
    	 catch (final Exception e) {
         	  _logger.info("Exception caught SetBidVol");
             _logger.info(System.err.toString());
             e.printStackTrace(System.err);       
           }
    }      
    public synchronized void SetAskVol(int level, BigDecimal  F_AskVol )
    {
    	try {
    		this.AskVol[level]  	=	F_AskVol;
        	b_AskVolEmpty = true;
    	}
    	 catch (final Exception e) {
        	  _logger.info("Exception caught SetBidVol");
            _logger.info(System.err.toString());
            e.printStackTrace(System.err);       
          }	
    }      
    
    /*
    public   synchronized void Store_DepthData (  NavigableMap<BigDecimal, BigDecimal> nm_Bid,  NavigableMap<BigDecimal, BigDecimal> nm_Ask )
	{	       		
       int b=0;
       
      // Map<BigDecimal, BigDecimal> reverseSortedMap = new TreeMap<BigDecimal, BigDecimal>(Collections.reverseOrder());       
       //reverseSortedMap.putAll(nm_Bid);
       
       for (Map.Entry<BigDecimal, BigDecimal> entry : nm_Bid.entrySet()) 
 	   {
    	    SetBidPrice(b, entry.getKey());
    	    SetBidVol(b, entry.getValue());
    	    _logger.info("Bid Price : " + entry.getKey() + " Bid Vol : " + entry.getValue()); 			
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
    	    SetAskPrice	(a, entry.getKey());
   	    	SetAskVol	(a, entry.getValue());
    	   _logger.info( "Ask Price : " + entry.getKey() + " Ask Vol : " + entry.getValue());
    	   a= a+1 ; 	
    	   if (a==10)
			{
				break;
			}
    	   
 	   }	
       while (b<10)
       {
    	   SetBidPrice	(b, new BigDecimal(0));
    	   SetBidVol	(b, new BigDecimal(0));
    	   b= b+1 ;
       }
       while (a<10)
       {
    	   SetAskPrice	(a, new BigDecimal(0));
    	   SetAskVol	(a, new BigDecimal(0));
    	   a= a+1 ;
       }
       System.out.println("Bid b    : " + b);
       System.out.println("Ask a    : " + a);
		b_BidPriceEmpty = true;
		b_AskPriceEmpty = true;
		b_BidVolEmpty = true;
		b_AskVolEmpty = true;
	}            
    */
    
    public synchronized boolean getDataUpdate( )
	{ 		
		if (b_DataUpdate)
		{
			b_DataUpdate=false;			
			//System.out.println("getOrderUpdate : "+ true);
			return true;
		}
		else
		{		
			//System.out.println("getOrderUpdate : "+ false);
			return false;
		}
	}
   
	public   synchronized BigDecimal getBidPrice( int Level)
	{			
		return BidPrice[Level];
	}		
	public    synchronized BigDecimal getAskPrice(int Level )
	{			
		
		return AskPrice[Level];		
	}		
	public  synchronized BigDecimal getAskVol(int Level )
	{		 		
		return AskVol[Level];
	}		
	public  synchronized BigDecimal getBidVol(int Level )
	{		
		//_logger.info("getBidVol") ;
		return BidVol[Level];	
	}	
	
	public synchronized boolean getBidPrice_Update()
    { 
    	//_logger.info("getBidPrice_Update") ;
    	if (b_BidPriceEmpty)
		{
    		b_BidPriceEmpty=false;			
			//System.out.println("getOrderUpdate : "+ true);
			return true;
		}
		else
		{		
			//System.out.println("getOrderUpdate : "+ false);
			return false;
		}     	
    }      
    
    public synchronized boolean getAskPrice_Update()
    {    	
    	//_logger.info("getAskPrice_Update") ;    	
    	if (b_AskPriceEmpty)
		{
    		b_AskPriceEmpty=false;			
			//System.out.println("getOrderUpdate : "+ true);
			return true;
		}
		else
		{		
			//System.out.println("getOrderUpdate : "+ false);
			return false;
		}    	
    }      
    
    public synchronized boolean getBidVol_Update( )
    {    	
    	//_logger.info("getBidVol_Update") ;    	
    	if (b_BidVolEmpty)
		{
    		b_BidVolEmpty=false;			
			//System.out.println("getOrderUpdate : "+ true);
			return true;
		}
		else
		{		
			//System.out.println("getOrderUpdate : "+ false);
			return false;
		}    	
    }      
    
    public synchronized boolean getAskVol_Update( )
    {    	
    	//_logger.info("getAskVol_Update") ;
    	if (b_AskVolEmpty)
		{
    		b_AskVolEmpty=false;			
			//System.out.println("getOrderUpdate : "+ true);
			return true;
		}
		else
		{		
			//System.out.println("getOrderUpdate : "+ false);
			return false;
		}    	
    }  
    
	public synchronized boolean getLadderUpdate()
	 {				
		if (LadderUpdate)
		{
			LadderUpdate=false;			
			//System.out.println("getOrderUpdate : "+ true);
			return true;
		}
		else
		{		
			//System.out.println("getOrderUpdate : "+ false);
			return false;
		}
	 }	
	public synchronized void SetLadderUpdate(  boolean F_Update)
	 {				
		LadderUpdate =F_Update;		
	 }	
}