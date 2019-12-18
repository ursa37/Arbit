
import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Table_MarketData extends AbstractTableModel implements Runnable {
  Thread runner;
  int delay;
  private PriceUpdate stocks= new PriceUpdate();  
  private PriceUpdate NewPriceList = new PriceUpdate(); 	
  public int UILoadIndex =0;
  private static final Logger _logger = LoggerFactory.getLogger(Table_MarketData.class);
  private String threadname;
  
  public PriceUpdate GetPriceData()
  {	  
	 // _logger.info( "PriceFeedSim GetPriceData");
	  return NewPriceList;
  }  
  
  public Table_MarketData( String F_TableName ) 
  {	 
	//_logger.info("Table_MarketData") ;
    Thread runner = new Thread(this);
    runner.setName(F_TableName);
    runner.start();
    this.threadname = F_TableName;
  }     
  String[] headers = {  "B.Vol", "BidPrice", "AskPrice", "A.Vol" };

  public int getRowCount() {
    return 10;
  }
  public int getColumnCount() {
    return headers.length;
  }
  public String getColumnName(int c) {
    return headers[c];
  }
  public synchronized Object getValueAt(int r, int c) 
  {
	 // _logger.info( "Market Data getValueAt" );
	  
	try 
	{
		  
		DecimalFormat df = new DecimalFormat("#.######");
	    switch (c)     
	    {  
		    case 0:
		    	return stocks.getBidVol(r);   
		    case 1:
		    	return  String.format("%.6f",  stocks.getBidPrice(r));	    	
		    case 2:	    	
		    	return String.format("%.6f", stocks.getAskPrice(r));
		    case 3:
		    	return stocks.getAskVol(r);		   
	    }  		  
	}   

    catch(IllegalArgumentException e)
	 {
		/// _logger.info(	"Error : Bad cell (" + r + ", " + c + ")" );
	 }
	 
	 catch (ArrayIndexOutOfBoundsException e) 
	 {
		/// _logger.info(	"Error : ArrayIndexOutOfBoundsException ");			
	 }
	
	return c;    
  } 

  private synchronized void updateStocks() 
  {	 	
	 System.out.println("updateStocks" + threadname) ;
	for (int i=0;i<getRowCount();i++)
	  {		
		BigDecimal BidPrice = NewPriceList.getBidPrice(i);
		BigDecimal AskPrice = NewPriceList.getAskPrice(i);	
		BigDecimal BidVol = NewPriceList.getBidVol(i);
		BigDecimal AskVol = NewPriceList.getAskVol(i);
		
		if (BidPrice == null)
		{	
			 BidPrice= new BigDecimal(0);
			 //System.out.println("Null BidPrice Price") ;
		}
		
		if (AskPrice == null)
		{
			 AskPrice = new BigDecimal(0);
			 //System.out.println("Null AskPrice Price") ;
		}
		
		if (BidVol == null)
		{	
			 BidVol= new BigDecimal(0);
			 //System.out.println("Null BidVol Price") ;
		}
		
		if (AskVol == null)
		{
			 AskVol = new BigDecimal(0);
			 //System.out.println("Null AskVol Price") ;
		}
		
		stocks.SetAskPrice(i,  AskPrice);
		stocks.SetBidPrice(i,  BidPrice);
		stocks.SetAskVol(i, AskVol);
		stocks.SetBidVol(i, BidVol);
		
		NewPriceList.SetAskPrice(i,  AskPrice);
		NewPriceList.SetBidPrice(i,  BidPrice);
		NewPriceList.SetAskVol(i, AskVol);
		NewPriceList.SetBidVol(i, BidVol);	
		
	  }
	fireTableRowsUpdated(0, 9);
	//_logger.info("updateStocks");
  }    
  
  private synchronized void CheckUpdate()
  {		  
	 // _logger.info("CheckUpdate "+ threadname) ;	 	
	//System.out.println(stocks.getBidPrice(0)) ;	  
	// _logger.info(NewPriceList.getBidPrice(0).toString()) ;	  
	
	//System.out.println("Ask") ;	  
	//System.out.println(stocks.getAskPrice(0)) ;	  
	  //_logger.info( NewPriceList.getAskPrice(0).toString()) ;	
	
	//
	boolean PriceChange=false;
	if (	stocks.getBidPrice(0)!=NewPriceList.getBidPrice(0)|| stocks.getAskPrice(0)!=NewPriceList.getAskPrice(0)	)
	{
		PriceChange=true;
		NewPriceList.SetLadderUpdate( true );		
	}
	else
	{
		PriceChange=false;
	}
	
	for (int i=0;i<getRowCount();i++)
	{								
		if (PriceChange)
		{	
			
		//  ------------------	Bid Vol	----------------------------			
				if (stocks.getBidVol(i)!=NewPriceList.getBidVol(i) ||  stocks.getBidVol(i).signum() == 0)
				  {										
					stocks.SetBidVol(i, NewPriceList.getBidVol(i));						
					fireTableRowsUpdated(0, 4);
				  }		
				
		//  ------------------	Bid Price	----------------------------		
			if (	stocks.getBidPrice(i)!=NewPriceList.getBidPrice(i)	||  stocks.getBidPrice(i).signum()==0)
			  {									
				stocks.SetBidPrice(i, NewPriceList.getBidPrice(i));						
				fireTableRowsUpdated(0, 9);
			  }						
			
		//  ------------------	Ask Vol	----------------------------			
			if ( stocks.getAskVol(i)!=NewPriceList.getAskVol(i) ||  stocks.getAskVol(i).signum()==0)
			{													
				stocks.SetAskVol(i, NewPriceList.getAskVol(i));		
				fireTableRowsUpdated(0, 9);		
			}
				
				
		//  ------------------	Ask Price	-------------------------								
			if ( stocks.getAskPrice(i)!=NewPriceList.getAskPrice(i) ||  stocks.getAskPrice(i).signum()==0 )
			  {													
				stocks.SetAskPrice(i, NewPriceList.getAskPrice(i));			
				fireTableRowsUpdated(0, 9);
			  }							
	}			
			
		else
		{				
			//_logger.info( " BidVol i="+i + " Value =[" + stocks.getBidVol(i) +"]" );			
		//  ------------------	Remain Bid Vol	----------------------------
			if (	stocks.getBidVol(i)!=NewPriceList.getBidVol(i)	)
			  {									
				stocks.SetBidVol(i, NewPriceList.getBidVol(i));						
				fireTableRowsUpdated(0, 9);
			  }				
							
			//  ------------------	Remain Ask Vol	----------------------------				
			if (	stocks.getAskVol(i)!=NewPriceList.getAskVol(i)	)
			{										
				stocks.SetAskVol(i, NewPriceList.getAskVol(i));				
				fireTableRowsUpdated(0, 9);					
			}		
		}			
	  }	
  }  
  
  public  void run() {	
    while (true) 
    {   		  
	    		if (UILoadIndex<1)
	    		{	 
	    			updateStocks();
	    			//_logger.info("updateStocks()");
	    		}
	    		else
	    		{
	    			if ( NewPriceList.getBidPrice_Update()==true ||NewPriceList.getAskPrice_Update()==true||NewPriceList.getBidVol_Update()==true||NewPriceList.getAskVol_Update()==true )
	    	    	{
	    			CheckUpdate();
	    			//_logger.info("CheckUpdate()");
	    	    	}
	    		}    		
			      // Blind update . . . we could check for real deltas if necessary
			      //updateStocks();		      
			      // We know there are no new columns, so don't fire a data change,
			      // only
			      // fire a row update . . . this keeps the table from flashing
			      //fireTableRowsUpdated(2, 2);
			     // fireTableCellUpdated(0,2);
			      UILoadIndex=UILoadIndex+1;    
	      try {    
	        Thread.sleep(1);
	      } catch (InterruptedException ie) {
	      }
    }
  }
}