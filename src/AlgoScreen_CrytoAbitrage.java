import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

public class AlgoScreen_CrytoAbitrage extends JFrame {
	private JPanel contentPane;	
	private Book_Order OB 				=	new Book_Order();	
	public Table_MarketData TblMD0 = new Table_MarketData("Binance");	
	public Table_MarketData TblMD1 = new Table_MarketData("Huobi");
	private Table_OrderBook  TblOB0 = new Table_OrderBook( OB  );
	
	public static int line=0;		
	private JTable TableMarketData_0;
	private JTable TableMarketData_1;
	private JTable Table_OrderRecord_0;	
	
	private JScrollPane scrollPane_0;		
	private JScrollPane scrollPane_1;
	private JLabel lblSymbol0;	
	private Data_Binance  	MDfeed_Binance 	= new Data_Binance ("ETHBTC", TblMD0.GetPriceData());
	private Data_Huobi  	MDfeed_Huobi	 = new Data_Huobi ("ethbtc", TblMD1.GetPriceData());	
	private JScrollPane scrollPane;
	
	 private static final Logger _logger = LoggerFactory.getLogger(AlgoScreen_CrytoAbitrage.class);  

		
	 private	Thread []AlgoThread =	
		{ 		
			//new Thread() ,	
			new Thread()
		};	
		
	 private	Algo_Arbit_CrossEx[] strategy = 
		{			
			new Algo_Arbit_CrossEx("CrossAlgo1",TblMD0.GetPriceData(),TblMD1.GetPriceData(),"ETHBTC", "ethbtc" , OB)	
			//new Algo_CrossArb_HitBTC_HuoBi_10("CrossA"CrossAlgo1" ,DataA ,DataB , "xrpbtc"  	, "XRPBTC"),
		};				
	 private JTextField textField;
	 private JTextField textField_1;
	 private JTextField textField_2;
	 private JTextField textField_3;
		
		
	 
	 
	 
	 
	
	public JTable get_JTbl0()
	{
		return TableMarketData_0;
	}	
	
	public JTable get_JTbl1()
	{
		return TableMarketData_1;
	}	
	
	public Table_MarketData getTbl0()
	{
		return TblMD0;
	}	
	
	public Table_MarketData getTbl1()
	{
		return TblMD1;
	}
	
	public  JLabel getlbl0( )
	{
		 return lblSymbol0 ;	
	}		
	private class TableSearchRenderer extends DefaultTableCellRenderer {
		 
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(null);
            Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);            
            
            
            if ( value.toString().contains("PLACED")) 
            {
                setBackground(Color.green);
                setForeground(Color.blue);
            }                         
            else if ( value.toString().contains("CANCEL_REJECT")) 
            {
            	setBackground(Color.gray);
                setForeground(Color.WHITE);
            }              
            else if ( value.toString().contains("REJECTED")) 
            {
            	setBackground(Color.red);
                setForeground(Color.blue);
            }
            else if ( value.toString().contains("FILLED")) 
            {
            	setBackground(Color.yellow);
                setForeground(Color.blue);            	
            }            
            else if ( value.toString().contains("CANCEL")) 
            {
                setBackground(Color.gray);
                setForeground(Color.WHITE);
            }   
            
            else if ( value.toString().contains("BUY")) 
            {               
                setForeground(Color.GREEN);
            }     
            
            else if ( value.toString().contains("SELL")) 
            {
            	setForeground(Color.red);
            	//setBackground(Color.MAGENTA);
            }             
            else
            {
            	//setForeground(Color.cyan);
            	setForeground(Color.getColor("#00bfff"));
            }             
            return tableCellRendererComponent;
        }		
    }
	
	
	private static void createBlink(  final JTable table, final BlinkCellRenderer blinkCellRenderer, int r, int c)
    {		
        Timer timer = new Timer(500, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {	if (line<1)
            	blinkCellRenderer.addBlinkingCell(r, c);
            	else
            	{
            		blinkCellRenderer.removeBlinkingCell(r, c);    
            		((Timer)e.getSource()).stop();
            		
            	}
            	line=line+1; 
            	// System.out.println(line);
            }             
        });               
        timer.setInitialDelay(0);
        timer.start();
        
        
        
        //System.out.println("createBlink");
    }
	
    
    /**
     * A TableCellRenderer which can let particular cells of 
     * a JTable blink. That is, it switches the background
     * color at a regular interval and triggers a repaint
     * of the table. The cell renderer components whose 
     * background is switched are provided by a delegate
     * cell renderer
     */
    public static class BlinkCellRenderer extends DefaultTableCellRenderer          
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = 6896646544236592534L;

        /**
         * Simple class storing the coordinates of a 
         * particular table cell
         */
        static class Cell
        {
            final int r;
            final int c;
            Cell(int r, int c)
            {
                this.r = r;
                this.c = c;
            }
            @Override
            public int hashCode()
            {
                return 31 * c + r;
            }
            @Override
            public boolean equals(Object object)
            {
                if (object instanceof Cell)
                {
                    Cell cell = (Cell)object;
                    return r == cell.r && c == cell.c; 
                }
                return false;
            }
        }

        /** 
         * The delegate cell renderer that provides the
         * cell renderer components
         */
        private final TableCellRenderer delegate;

        /**
         * The set of cells that are currently blinking
         */
        private final Set<Cell> blinkingCells = new HashSet<Cell>();

        /**
         * The current blinking state (that is, whether
         * the cells should be highlighted or not)
         */
        private boolean blinkingState = true;

        /**
         * Creates a BlinkCellRenderer that will let cells of
         * the given table blink. The cell renderer components
         * are provided by the given delegate
         * 
         * @param table The table
         * @param delegate The delegate
         */
        BlinkCellRenderer(final JTable table, TableCellRenderer delegate)
        {
            this.delegate = delegate;
            int delayMS = 200;
            Timer blinkingTimer = new Timer(delayMS, new ActionListener()
            {
                boolean timerBlinkingState = true;

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    blinkingState = timerBlinkingState;
                    table.repaint();
                    timerBlinkingState = !timerBlinkingState;
                }
            });
            blinkingTimer.setInitialDelay(0);
            blinkingTimer.start();         
        }

        /**
         * Add the cell with the given coordinates to the
         * set of cells that are currently blinking
         * 
         * @param r The row
         * @param c The column
         */
        void addBlinkingCell(int r, int c)
        {
        	 //System.out.println("addBlinkingCell");
            blinkingCells.add(new Cell(r, c));
        }
        /**
         * Remove the cell with the given coordinates from the
         * set of cells that are currently blinking
         * 
         * @param r The row
         * @param c The column
         */
        void removeBlinkingCell(int r, int c)
        {
            blinkingCells.remove(new Cell(r,c));
        }
        /**
         * Removes all blinking cells
         */
        void clearBlinkingCells()
        {
            blinkingCells.clear();
        }
        @Override
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,  boolean hasFocus, int row, int column)
        {
            Component component = delegate.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            Cell cell = new Cell(row, column);
            if (blinkingState && blinkingCells.contains(cell))
            {
                component.setBackground(Color.RED);
            }
            else
            {
                component.setBackground(null);
            }

            return component;
        }                
    }
	
	
	
	
	
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AlgoScreen_CrytoAbitrage frame = new AlgoScreen_CrytoAbitrage();
					frame.setTitle("Arbitrage Algo Screen - By Kook Heng");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AlgoScreen_CrytoAbitrage() {
		
		TableSearchRenderer renderer = new TableSearchRenderer();
		TableCellRenderer delegate = new DefaultTableCellRenderer();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1210, 610);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(119, 136, 153));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane_0 = new JScrollPane();
		scrollPane_0.setBounds(10, 25, 551, 199);
		contentPane.add(scrollPane_0);
		
		TableMarketData_0 = new JTable(TblMD0);
		TableMarketData_0.setBorder(new BevelBorder(BevelBorder.LOWERED, SystemColor.textHighlight, SystemColor.textHighlight, SystemColor.textHighlight, SystemColor.textHighlight));
		TableMarketData_0.setForeground(Color.YELLOW);
		TableMarketData_0.setEnabled(false);
		TableMarketData_0.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		TableMarketData_0.setFillsViewportHeight(true);
		TableMarketData_0.setCellSelectionEnabled(true);
		TableMarketData_0.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		TableMarketData_0.setShowVerticalLines(true);
		TableMarketData_0.setShowHorizontalLines(true);
		BlinkCellRenderer blinkCellRenderer = new BlinkCellRenderer(TableMarketData_0, delegate);
		TableMarketData_0.setDefaultRenderer(Object.class, blinkCellRenderer);
		//createBlinkChecker(TableMarketData, blinkCellRenderer);  
		TableMarketData_0.setBackground(Color.BLACK);
		TableMarketData_0.setSurrendersFocusOnKeystroke(true);
		
		scrollPane_0.setViewportView(TableMarketData_0);
		
		lblSymbol0 = new JLabel("Binance");
		lblSymbol0.setForeground(Color.YELLOW);
		lblSymbol0.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblSymbol0.setBounds(10, 0, 110, 25);
		contentPane.add(lblSymbol0);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(586, 25, 551, 199);
		contentPane.add(scrollPane_1);
		
		TableMarketData_1 = new JTable(TblMD1);
		TableMarketData_1.setBorder(new BevelBorder(BevelBorder.LOWERED, SystemColor.textHighlight, SystemColor.textHighlight, SystemColor.textHighlight, SystemColor.textHighlight));
		TableMarketData_1.setForeground(Color.CYAN);
		TableMarketData_1.setEnabled(false);
		TableMarketData_1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		TableMarketData_1.setFillsViewportHeight(true);
		TableMarketData_1.setCellSelectionEnabled(true);
		TableMarketData_1.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		TableMarketData_1.setShowVerticalLines(true);
		TableMarketData_1.setShowHorizontalLines(true);
		blinkCellRenderer = new BlinkCellRenderer(TableMarketData_1, delegate);
		TableMarketData_1.setDefaultRenderer(Object.class, blinkCellRenderer);
		//createBlinkChecker(TableMarketData, blinkCellRenderer);  
		TableMarketData_1.setBackground(Color.BLACK);
		TableMarketData_1.setSurrendersFocusOnKeystroke(true);
		
		scrollPane_1.setViewportView(TableMarketData_1);
		
		JLabel lblHuobi = new JLabel("Huobi");
		lblHuobi.setForeground(Color.CYAN);
		lblHuobi.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblHuobi.setBounds(591, 0, 110, 25);
		contentPane.add(lblHuobi);
		
		JScrollPane scrollPane_OB0 = new JScrollPane();
		scrollPane_OB0.setBounds(10, 339, 759, 207);
		contentPane.add(scrollPane_OB0);
		
		JButton btnStartAlgo = new JButton("Start Algo");
		btnStartAlgo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				for ( int i=0; i<1; i++ )
				{
					System.out.println("AlgoThread Started" ) ;
					AlgoThread[i] = new Thread(strategy[i], "Cryto"+i);
					AlgoThread[i].start();	
					strategy[i].OnAlgo();					
				}	
				
				
				
			}
		});
		btnStartAlgo.setBounds(10, 285, 90, 30);
		contentPane.add(btnStartAlgo);
		 
		JButton btnStopAlgo = new JButton("Stop Algo");
		btnStopAlgo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				for ( int i=0; i<1; i++ )
				{
					
					strategy[i].CloseAlgo();					
				}	
				
			}
		});
		btnStopAlgo.setBounds(112, 285, 90, 30);
		contentPane.add(btnStopAlgo);
		


	
		Table_OrderRecord_0 = new JTable(TblOB0) ;
		Table_OrderRecord_0.setForeground(new Color(0, 191, 255));
		Table_OrderRecord_0.setBackground(Color.BLACK);
		Table_OrderRecord_0.setFillsViewportHeight(true);
		Table_OrderRecord_0.setFont(new Font("SansSerif", Font.PLAIN, 12));
		Table_OrderRecord_0.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Table_OrderRecord_0.setShowVerticalLines(true);
		Table_OrderRecord_0.setShowHorizontalLines(true);
		Table_OrderRecord_0.setDefaultRenderer(Object.class, renderer);
	
		scrollPane_OB0.setViewportView(Table_OrderRecord_0);
		
		textField = new JTextField("0.001");
		textField.setBounds(994, 361, 122, 30);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField("0.002");
		textField_1.setColumns(10);
		textField_1.setBounds(994, 403, 122, 30);
		contentPane.add(textField_1);
		
		textField_2 = new JTextField("0.001");
		textField_2.setColumns(10);
		textField_2.setBounds(994, 442, 122, 30);
		contentPane.add(textField_2);
		
		JLabel lblBinanceTakerComm = new JLabel("Binance Taker (CommA)");
		lblBinanceTakerComm.setBounds(816, 369, 166, 24);
		contentPane.add(lblBinanceTakerComm);
		
		JLabel lblHuobiTakerComm = new JLabel("Huobi Taker (CommB)");
		lblHuobiTakerComm.setBounds(816, 406, 161, 24);
		contentPane.add(lblHuobiTakerComm);
		
		JLabel lblTradeSize = new JLabel("Trade Size");
		lblTradeSize.setBounds(816, 445, 120, 24);
		contentPane.add(lblTradeSize);
		
		JButton btnSave = new JButton("Reset");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String T_CommA = textField.getText();
				String T_CommB = textField_1.getText();
				String T_Size = textField_2.getText();
				String limit = textField_3.getText();
				int intlimit = Integer.parseInt(limit);
				
				strategy[0].CloseAlgo();
				strategy[0].SetAlgoParemater(T_Size, T_CommA, T_CommB,intlimit);
			}
		});
		btnSave.setBounds(816, 516, 90, 30);
		contentPane.add(btnSave);
		
		JLabel lblAlgoSetting = new JLabel("Algo Setting");
		lblAlgoSetting.setFont(new Font("SansSerif", Font.PLAIN, 17));
		lblAlgoSetting.setBounds(814, 339, 122, 18);
		contentPane.add(lblAlgoSetting);
		
		JLabel lblDailyLimit = new JLabel("Daily Limit");
		lblDailyLimit.setBounds(816, 481, 120, 24);
		contentPane.add(lblDailyLimit);
		
		textField_3 = new JTextField("4");
		textField_3.setColumns(10);
		textField_3.setBounds(994, 484, 122, 30);
		contentPane.add(textField_3);
		
		FileOutputStream file;
		
		try 
		{
			
			Format formatter = new SimpleDateFormat("dd_MMM_yy");
		    String s = formatter.format(new Date());
			Path currentRelativePath = Paths.get("");
			String filepath = currentRelativePath.toAbsolutePath().toString();
			
			System.out.println("Current relative path is: " + filepath);
			//file = new FileOutputStream( "C:/KookHeng/Log/Test/Binance_HitBTC_"+s+"_"+System.currentTimeMillis()+".txt" );	
			file = new FileOutputStream( filepath+"/Binance_Huobi_"+s+"_"+System.currentTimeMillis()+".txt" );	
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
	
	

	public void SetTblMarketData(	Table_MarketData Tbl , int Index	)
	{	
		switch (Index) 
		{
	        case 0:  
	        		 TblMD0 =Tbl;  
	        case 1: 
       		 		 TblMD1=Tbl;		    		 
	        default:
	        		 TblMD0=Tbl;
		}  
	}
}
