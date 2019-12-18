import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Algo_Arbit_CrossEx implements Runnable { 
	
	private volatile boolean shutdown;
	private static final Logger _logger = LoggerFactory.getLogger(Algo_Arbit_CrossEx.class);
	private PriceUpdate DataA;
	private PriceUpdate DataB;	
	public BigDecimal BestBid_A 			= new BigDecimal("0.00000000"); // Binance
	public BigDecimal BestBid_B 			= new BigDecimal("0.00000000"); // Huobi
	public BigDecimal BestAsk_A 			= new BigDecimal("0.00000000");
	public BigDecimal BestAsk_B 			= new BigDecimal("0.00000000");	
	public BigDecimal Spread_B 				= new BigDecimal("0.00000000");
	public BigDecimal Spread_A 				= new BigDecimal("0.00000000");
	public BigDecimal InterSpread_A 		= new BigDecimal("0.00000000");
	public BigDecimal InterSpread_B 		= new BigDecimal("0.00000000");	
	public String CrossTrade = "";
	public boolean b_Signal = false;	
	public boolean b_TriggerOrder = false;
	private String threadName;
	public Book_Order OrderBook ;
	
	public SingleOrder.Side SideA;
	public SingleOrder.Side SideB;
	public BigDecimal BestPriceA 			= new BigDecimal("0.00000000");	
	public BigDecimal BestPriceB 			= new BigDecimal("0.00000000");	
	
	// Algo Setting
	public String CommA = "0.001"; // 0.001
	public String CommB = "0.002"; //	0.001
	public String TradeSize = "0.001";
	public String OrderType = "limit";
	public String CurrA = "";
	public String CurrB = "";	
	public BigDecimal OrderPrice_A = new BigDecimal(0); // Gemini
	public BigDecimal OrderPrice_B = new BigDecimal(0); // Kraken
	
	public int DailyLimit = 4;
	public int OrderCount = 0;
		
	public Algo_Arbit_CrossEx (	String AlgoName, PriceUpdate Feed_A, PriceUpdate Feed_B, String Symbol_A	, String Symbol_B , Book_Order F_OB )
	{
		threadName = AlgoName;
		this.DataA = Feed_A;
		this.DataB = Feed_B;
		this.CurrA = Symbol_A;
		this.CurrB = Symbol_B;
		this.OrderBook = F_OB;
		
		
		
	}	
	
	public void SetAlgoParemater( String Size, String CommA,String CommB, int limit )
	{		
		this.TradeSize 	=	Size ;	
		this.CommA 		= CommA;
		this.CommB 		= CommB;	
		this.DailyLimit = limit;
		
		_logger.info("SetAlgoParemater: {},{},{} " ,Size , CommA , CommB );
	}
	
	public synchronized void TriggerOrder() 	
	{
		b_TriggerOrder= true;
		_logger.info("TriggerOrder" );
		long OrderNum = (long) new Date().getTime();
		SingleOrder F_Order = new SingleOrder("ETH/BTC", SideA, BestPriceA, TradeSize, OrderNum,SingleOrder.State.FILLED, "Binance", CommA);
		OrderBook.AddOrder("ETH/BTC" , F_Order, OrderNum);  
		_logger.info("TriggerOrder Binance {},{},{},{},{},{},{},{}", "ETH/BTC", SideA, BestPriceA, TradeSize, OrderNum,SingleOrder.State.FILLED, "Binance", CommA ); 
		OrderBook.SetReplyUpdate();  
		
		OrderNum = (long) new Date().getTime();
		SingleOrder F_Order2 = new SingleOrder("ETH/BTC", SideB, BestPriceB, TradeSize, OrderNum,SingleOrder.State.FILLED, "Huobi", CommB);
		OrderBook.AddOrder("ETH/BTC" , F_Order2, OrderNum);
		OrderBook.SetReplyUpdate();  
		_logger.info("TriggerOrder Binance {},{},{},{},{},{},{},{}", "ETH/BTC", SideB, BestPriceB, TradeSize, OrderNum,SingleOrder.State.FILLED, "Huobi", CommB );		 
		 b_Signal =false;
		 shutdown=true;		
	}
	
public void CalculateCost() {
		
	System.out.println("CalculateCost " );
		BestAsk_A = DataA.getAskPrice(0);
		BestAsk_B= DataB.getAskPrice(0);		
		BestBid_A = DataA.getBidPrice(0);
		BestBid_B = DataB.getBidPrice(0);
		_logger.info("Binance: " + BestBid_A + "  "+BestAsk_A + "  "+"Huobi: " +BestBid_B+ "  "+ BestAsk_B+ "  ");	

		if (BestAsk_A.compareTo(new BigDecimal(0)) > 0 && BestAsk_B.compareTo(new BigDecimal(0)) > 0
				&& BestBid_A.compareTo(new BigDecimal(0)) > 0 && BestBid_B.compareTo(new BigDecimal(0)) > 0) {
			BigDecimal Cost_Spread_A = new BigDecimal(0);
			BigDecimal Cost_Spread_B = new BigDecimal(0);
			BigDecimal SellTakerCostA = BestAsk_A.multiply(new BigDecimal(CommA));
			BigDecimal BuyTakerCostA = BestBid_A.multiply(new BigDecimal(CommA));
			BigDecimal SellTakerCostB = BestAsk_B.multiply(new BigDecimal(CommB));
			BigDecimal BuyTakerCostB = BestBid_B.multiply(new BigDecimal(CommB));
			
			Spread_A = BestBid_A.subtract(BestAsk_B);
			Spread_B = BestBid_B.subtract(BestAsk_A);
			InterSpread_A = BestAsk_A.subtract(BestBid_A);
			InterSpread_B = BestAsk_B.subtract(BestBid_B);
			Cost_Spread_A = BuyTakerCostA.add(SellTakerCostB);
			Cost_Spread_B = BuyTakerCostB.add(SellTakerCostA);

			// Spread_A.setScale(6, BigDecimal.ROUND_UP);
			// Spread_B.setScale(6, BigDecimal.ROUND_UP);
			// Cost_Spread_A.setScale(6, BigDecimal.ROUND_UP);
			// Cost_Spread_B.setScale(6, BigDecimal.ROUND_UP);

			if (Spread_A.compareTo(Cost_Spread_A) > 0) {
				CrossTrade = "ShortALongB";
				_logger.info("Signal ");
				_logger.info("ShortALongB Send Spread=" + Spread_A + "  TotalCostA=" + Cost_Spread_A);
				_logger.info(" Huobi  "+CommB+" BestBid_B= " + BestBid_B + "Binance "+CommA+" BestAsk_A= " + BestAsk_A);
				_logger.info(" Binance "+CommA+" BestBid_A " + BestBid_A + "Huobi "+CommB+" BestAsk_B= " + BestAsk_B);
				_logger.info("ShortALongB, Spread_A=" + Spread_A + "  TotalCostA=" + Cost_Spread_A + " Spread_B="
						+ Spread_B + "  TotalCostB=" + Cost_Spread_B);
				b_Signal = true;
				BestPriceA = BestBid_A;
				BestPriceB = BestAsk_B;
				SideA = SideA.SELL;
				SideB = SideB.BUY;
				
			}
			if ((Spread_B.compareTo(Cost_Spread_B)) > 0) {
				CrossTrade = "ShortBLongA";
				_logger.info("Signal ");
				_logger.info("ShortBLongA Send Spread=" + Spread_B + "  TotalCostA=" + Cost_Spread_A);
				_logger.info(" Huobi  "+CommB+" BestBid_B= " + BestBid_B + " Binance "+CommA+" BestAsk_A= " + BestAsk_A);
				_logger.info(" Binance "+CommA+" BestBid_A " + BestBid_A + " Huobi  "+CommB+" BestAsk_B= " + BestAsk_B);
				_logger.info("ShortBLongA, Spread_A=" + Spread_A + "  TotalCostA=" + Cost_Spread_A + " Spread_B="
						+ Spread_B + "  TotalCostB=" + Cost_Spread_B);
				b_Signal = true;
				BestPriceA = BestAsk_A;
				BestPriceB = BestBid_B;
				SideA = SideA.BUY;
				SideB = SideB.SELL;
				
			} else {
				BigDecimal Ratio_A = new BigDecimal(0);
				BigDecimal Ratio_B = new BigDecimal(0);
				Ratio_A = Spread_A.divide(Cost_Spread_A, BigDecimal.ROUND_HALF_UP);
				Ratio_B = Spread_B.divide(Cost_Spread_B, BigDecimal.ROUND_HALF_UP);

				if ((Spread_A.compareTo(new BigDecimal(0))) > 0 && (Ratio_A.compareTo(new BigDecimal(0.5))) > 0) {
					//_logger.info("Ratio_A=" + Ratio_A.toPlainString());
					_logger.info("No valid TotalCostA=" + Cost_Spread_A.toPlainString() + " TotalCostB="
							+ Cost_Spread_B.toPlainString());
					_logger.info("No valid CrossSpread_A= " + Spread_A.toPlainString() + " BestBid_A ="
							+ BestBid_A.toPlainString() + "  BestAsk_B=" + BestAsk_B.toPlainString());
					// _logger.info( "No valid CrossSpread_B= " +
					// Spread_B.toPlainString() +" BestBid_B =" +
					// BestBid_B.toPlainString() + " BestAsk_A=" +
					// BestAsk_A.toPlainString() );
					_logger.info("No valid InterSpread_A= " + InterSpread_A.toPlainString() + " BestBid_A ="
							+ BestBid_A.toPlainString() + "  BestAsk_A=" + BestAsk_A.toPlainString());
					// _logger.info( "No valid InterSpread_B= " +
					// InterSpread_B.toPlainString() +" BestBid_B =" +
					// BestBid_B.toPlainString() + " BestAsk_B=" +
					// BestAsk_B.toPlainString() );
				}

				if ((Spread_B.compareTo(new BigDecimal(0))) > 0 && (Ratio_B.compareTo(new BigDecimal(0.5))) > 0) {
					//_logger.info("Ratio_B=" + Ratio_B.toPlainString());
					_logger.info("No valid TotalCostA=" + Cost_Spread_A.toPlainString() + " TotalCostB="
							+ Cost_Spread_B.toPlainString());
					// _logger.info( "No valid CrossSpread_A= " +
					// Spread_A.toPlainString() + " BestBid_A =" +
					// BestBid_A.toPlainString() + " BestAsk_B=" +
					// BestAsk_B.toPlainString() );
					_logger.info("No valid CrossSpread_B= " + Spread_B.toPlainString() + " BestBid_B ="
							+ BestBid_B.toPlainString() + "  BestAsk_A=" + BestAsk_A.toPlainString());
					// _logger.info( "No valid InterSpread_A= " +
					// InterSpread_A.toPlainString() +" BestBid_A =" +
					// BestBid_A.toPlainString() + " BestAsk_A=" +
					// BestAsk_A.toPlainString() );
					_logger.info("No valid InterSpread_B= " + InterSpread_B.toPlainString() + " BestBid_B ="
							+ BestBid_B.toPlainString() + "  BestAsk_B=" + BestAsk_B.toPlainString());
				}
			}
		}
		
	}

public void CloseAlgo() {
	shutdown = true;
	_logger.info("CloseAlgo");
}

public void OnAlgo() {
	shutdown = false;
	_logger.info("OnAlgo");
}

public void SetCurrencyA(String F_CurrA) {
	CurrA = F_CurrA;
}

public void SetCurrencyB(String F_CurrB) {
	CurrB = F_CurrB;
}

	
	
	public void run() {
		while (!shutdown) {
			
			/*
			if (PriceList.getLadderUpdate()) {
				_logger.info("Price Ladder Change");
				UpdateOrderQty();
			}
			*/
			
			if (b_Signal && OrderCount < DailyLimit) {
					if (b_TriggerOrder == false ) 
					{
						System.out.println("Main: FireOrder: " + CurrA + " " + CurrB);
						TriggerOrder();
						//VerifyPosition();
						 OrderCount = OrderCount + 1;
						 b_TriggerOrder = false	;
						 
						 try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						 
					} //else {
						//System.out.println("Main: Continue Verifying");
						//VerifyPosition();
							//}
			} 
			else 
			{
				//System.out.println("Main:  CalculateCost");
				CalculateCost();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}
			
		}
	}
		
	public static void main(String[] args) 
	{				
		Table_MarketData TblMD0 = new Table_MarketData("Binance");	
		Table_MarketData TblMD1 = new Table_MarketData("Huobi");
		Book_Order NewOrderBook = new Book_Order ();
		
			
		Data_Binance  	MDfeed_Binance = new Data_Binance ("ETHBTC", TblMD0.GetPriceData());
		Data_Huobi  	MDfeed_Huobi	 = new Data_Huobi ("ethbtc", TblMD1.GetPriceData());	
		
		Thread []AlgoThread =	
		{ 		
			//new Thread() ,	
			new Thread()
		};	
		
		Algo_Arbit_CrossEx[] strategy = 
		{			
			new Algo_Arbit_CrossEx("CrossAlgo1",TblMD0.GetPriceData(),TblMD1.GetPriceData(),"ETHBTC", "ethbtc" , NewOrderBook)	
			//new Algo_CrossArb_HitBTC_HuoBi_10("CrossA"CrossAlgo1" ,DataA ,DataB , "xrpbtc"  	, "XRPBTC"),
		};				
		
		for ( int i=0; i<1; i++ )
		{
			System.out.println("AlgoThread Started" ) ;
			AlgoThread[i] = new Thread(strategy[i], "Cryto"+i);
			AlgoThread[i].start();	
			strategy[i].OnAlgo();					
		}	
		
		FileOutputStream file;
		try 
		{
			Format formatter = new SimpleDateFormat("dd_MMM_yy");
		    String s = formatter.format(new Date());
		    
		    Path currentRelativePath = Paths.get("");
			String filepath = currentRelativePath.toAbsolutePath().toString();
			System.out.println("Current relative path is: " + filepath);
		    
			//file = new FileOutputStream( "C:/KookHeng/Log/Test/Binance_Huobi_"+s+"_"+System.currentTimeMillis()+".txt" );	
			file = new FileOutputStream( filepath+"/Binance_HitBTC_"+s+"_"+System.currentTimeMillis()+".txt" );		
			TeePrintStream tee = new TeePrintStream(file, System.out);
			System.setOut(tee);	   
			System.setErr(tee);			
		}		
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	
}
