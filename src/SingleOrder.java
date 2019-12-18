import java.math.BigDecimal;

public class SingleOrder 
{	
	public enum State { UNKNOWN, PENDING, PLACED, PARTIAL_FILL, FILLED, CANCEL_PENDING, CANCEL, REPLACE_PENDING, REPLACED, REJECTED, REJECTED_SNAP, CANCEL_REJECT, REPLACE_REJECT, REPLACED_SNAP, EXPIRED };
	public enum Side { NONE, BUY, SELL }
	
	private BigDecimal Price;
	private String Size;
	private long OrderID;
	private long CV =	0;
	private long EV	=	0;
	private State orderState;
	private String  Exchange;	
	private Side Side;
	private String Symbol;	
	private String Comm;	
	private long RemQty =	0;
	private long FilledQty =	0;
	private long TPQty =	0;
	private long CloseQty =	0;
	
	
	public  SingleOrder(String F_Symbol,Side side1 , BigDecimal Price1,  String Size1, long lastOrderID, State status1,String Exch, String Comm1 )
	{		
		  Symbol 		= 	F_Symbol;
		  Side 			=   side1 ;
		  Price			=	Price1 ;
		  Size			=	Size1;
		  OrderID		=	lastOrderID;
		  orderState	=	status1;			
		  Exchange		=	Exch;
		  Comm		=	Comm1;

	}
		
	public void SetRemQty (	long F_RemQty )
	{
		RemQty = F_RemQty;
	}	
	public void SetFilledQty (	long F_FilledQty )
	{
		FilledQty = F_FilledQty;
	}	
	public void SetTPQty (	long F_TPQty )
	{
		TPQty = F_TPQty;
	}	
	public void SetCloseQty (	long F_CloseQty )
	{
		CloseQty = F_CloseQty;
	}
	
	
	
	public void SetCV(	long F_CV )
	{
		CV = F_CV	;
	}		
	public void SetEV(	long F_EV )
	{
		EV = F_EV	;
	}		
	public long getCV(	)
	{
		return CV ;
	}		
	public long getEV(	)
	{
		return EV ;
	}	
	public void SetSymbol(	String F_Symbol	)
	{
		this.Symbol = F_Symbol ; 
	}	
	public String getSymbol ()
	{
		return Symbol;
	}	
	public void SetState(	State F_State )
	{
		orderState = F_State;
	}	
	
	public  Side getSide()
	{
		return this.Side;
	}	
	public BigDecimal getPrice()
	{
		return this.Price;
	}	
	public String getSize()
	{
		return this.Size;
	}	
	public long getOrderID()
	{
		return this.OrderID;
	}	
	public State getStatus()
	{
		return this.orderState;
	}	
	public String getExchange()
	{
		return this.Exchange;
	}
	
	public String getComm()
	{
		return this.Comm;
	}	
	
	public void SetComm (String Comm1 )
	{
		this.Comm = Comm1;
	}	
	

	
	
	
	public long getRemQty()
	{
		return this.RemQty;
	}
	public long getFilledQty()
	{
		return this.FilledQty;
	}
	public long getTPQty()
	{
		return this.TPQty;
	}
	public long getCloseQty()
	{
		return this.CloseQty;
	}
	
	
}
