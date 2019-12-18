import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//MarketDataModel.java
//A custom table model for use with the MYOSM enviornment.

public class Table_OrderBook extends AbstractTableModel implements Runnable {
 int delay;  
 private int StockIndex;   
 private String F_Symbol ;
 private SingleOrder.Side Side1 ; 
 private BigDecimal Price1;  
 private String Size1; 
 private long lastOrderID; 
 private SingleOrder.State Status1;
 private String Exchange;

 private String Comm;
 private String CurrentSymbol; 

 private SingleOrder OldOrder =		new SingleOrder (  F_Symbol,  Side1 ,  Price1,   Size1,  lastOrderID,  Status1, Exchange , Comm);
 private ConcurrentHashMap<Long,SingleOrder>	hmOrder	=	new ConcurrentHashMap<Long,SingleOrder>(); 
 private Vector <SingleOrder > vec_Order = new Vector <SingleOrder>();    
 private  Book_Order CurrOB ;  
 private boolean  TBL_OrderUpdate;
 
  Thread runner;
  private static final Logger _logger = LoggerFactory.getLogger(Table_OrderBook.class);  
 
  public Table_OrderBook(  Book_Order F_OB ) 
  {	
	 CurrOB = F_OB;	 
	 StockIndex=0;
	 Thread runner = new Thread(this);
	 runner.setName("Table_OrderBook");
	 runner.start();
  }    
  String[] headers = { "No","Symbol", "Side","Price", "Size", "OrderID", "Status","Exchange", "Comm(%)"};
  
  public void SetCurrentSymbol( String F_Symbol)
  {
	  CurrentSymbol = F_Symbol ;
	 // System.out.println(" Table_OrderBook CurrentSymbol" + CurrentSymbol  );
  }    
  public void SetStockIndex( int FIndex)
  {
	  StockIndex = FIndex ;
	//  System.out.println(" Table_SV CurrentSymbol" + CurrentSymbol  );
  }
  
  public int getRowCount() {
    return vec_Order.size();
  }
  public int getColumnCount() {
    return headers.length;
  }
  public String getColumnName(int c) {
    return headers[c];
  }
  public synchronized Object getValueAt(int r, int c) 
  {
	 try
	 {
	  
	  OldOrder= (SingleOrder)vec_Order.get(r);	
	 
		    switch (c)     
		    {	    
			    case 0:
			      return r+1;	      
			    case 1:
				      return OldOrder.getSymbol();	      
			    case 2:  	
			      return OldOrder.getSide();	      
			    case 3:
			      return OldOrder.getPrice();   
			    case 4:
			      return OldOrder.getSize();
			    case 5:
			      return OldOrder.getOrderID();    	
			    case 6:
			        return OldOrder.getStatus();
			    case 7:
			    	return OldOrder.getExchange();	
			    	
			    case 8:
			    	return OldOrder.getComm();	
		    }  
	 }
	 
	 catch(IllegalArgumentException e)
	 {
		 _logger.info(	"Error : Bad cell (" + r + ", " + c + ")" );
	 }
	 
	 catch (ArrayIndexOutOfBoundsException e) 
	 {
		 _logger.info(	"Error : ArrayIndexOutOfBoundsException vec_Order Size= " + vec_Order.size() + "   r= " + r + "  C=" + c);						
		 _logger.info(	"Error : ArrayIndexOutOfBoundsException StockIndex=" + StockIndex);		
	 }  
	 
	return c;
    
    //throw new IllegalArgumentException("Bad cell (" + r + ", " + c + ")");
   
  }
  
  public synchronized void updateStocks() 
  {	  		
	  hmOrder = CurrOB.gethmOrder(StockIndex);		
	  //_logger.info(	"Table_OrderBook: updateStocks() StockIndex"	+ StockIndex );
		//hmOrder = CurrOB.gethmOrder();		
	  vec_Order.removeAllElements();		
		//  _logger.info(	"updateStocks() StockIndex"	+ StockIndex);
	  int Index=0;	
		for (Map.Entry m : hmOrder.entrySet()) 
		{			
				OldOrder =  (SingleOrder) m.getValue();
				vec_Order.add(OldOrder);
		}	
  }
    
  public void run() {
    while (true) {
    	TBL_OrderUpdate= CurrOB.getReplyUpdate();
    	if (TBL_OrderUpdate)
    	{    	
		      // Blind update . . . we could check for real deltas if necessary
		      updateStocks();
		      // We know there are no new columns, so don't fire a data change,
		      // only
		      // fire a row update . . . this keeps the table from flashing
		      //fireTableRowsUpdated(0, vec_Order.size()); 
		      fireTableDataChanged();
    	}
    	else
    	{    		
    		 
    	}
    }
  }

}