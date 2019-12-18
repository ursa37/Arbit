import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JTable;
import java.awt.Panel;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Dialog.ModalExclusionType;
import javax.swing.JSplitPane;


public class TradingSystem {

	private JFrame frmArbitrageAlgoScreen;
	private JTable table;
	private JTable table_1;
	private JTable table_2;
	private JTextField textField;
	private JTable table_3;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TradingSystem window = new TradingSystem();
					window.frmArbitrageAlgoScreen.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TradingSystem() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmArbitrageAlgoScreen = new JFrame();
		frmArbitrageAlgoScreen.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frmArbitrageAlgoScreen.setTitle("Arbitrage Algo Screen - By Kook Heng");
		frmArbitrageAlgoScreen.getContentPane().setBackground(UIManager.getColor("CheckBox.background"));
		frmArbitrageAlgoScreen.getContentPane().setLayout(null);
		
		Panel panel = new Panel();
		panel.setBackground(UIManager.getColor("CheckBox.background"));
		panel.setBounds(45, 10, 1042, 410);
		frmArbitrageAlgoScreen.getContentPane().add(panel);
		panel.setLayout(null);
		
		table = new JTable();
		table.setBounds(7, 29, 300, 362);
		table.setForeground(new Color(0, 0, 0));
		table.setBackground(UIManager.getColor("CheckBox.background"));
		panel.add(table);
		
		table_1 = new JTable();
		table_1.setBounds(355, 29, 300, 362);
		table_1.setBackground(UIManager.getColor("Button.highlight"));
		panel.add(table_1);
		
		table_2 = new JTable();
		table_2.setBounds(681, 29, 300, 362);
		table_2.setBackground(UIManager.getColor("CheckBox.background"));
		panel.add(table_2);
		
		JLabel lblBinance = new JLabel("Binance");
		lblBinance.setBounds(7, 7, 58, 18);
		lblBinance.setForeground(UIManager.getColor("Button.focus"));
		lblBinance.setFont(new Font("Arial", Font.BOLD, 15));
		panel.add(lblBinance);
		
		JLabel lblKraken = new JLabel("Kraken");
		lblKraken.setBounds(355, 7, 51, 18);
		lblKraken.setBackground(new Color(0, 0, 0));
		lblKraken.setForeground(UIManager.getColor("CheckBox.focus"));
		lblKraken.setFont(new Font("Arial", Font.BOLD, 15));
		panel.add(lblKraken);
		
		JLabel lblHuobi = new JLabel("Huobi");
		lblHuobi.setBounds(681, 7, 42, 18);
		lblHuobi.setForeground(UIManager.getColor("CheckBox.focus"));
		lblHuobi.setFont(new Font("Arial", Font.BOLD, 15));
		panel.add(lblHuobi);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(48, 708, 988, 200);
		frmArbitrageAlgoScreen.getContentPane().add(tabbedPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_1, null);
		panel_1.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(120, 6, 114, 30);
		panel_1.add(textField);
		textField.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(6, 144, 98, 26);
		panel_1.add(btnSave);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(120, 38, 114, 30);
		panel_1.add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(120, 80, 114, 30);
		panel_1.add(textField_2);
		
		JButton btnStart = new JButton("Start");
		btnStart.setBounds(120, 144, 98, 26);
		panel_1.add(btnStart);
		
		JButton btnStop = new JButton("Stop");
		btnStop.setBounds(230, 144, 98, 26);
		panel_1.add(btnStop);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(UIManager.getColor("CheckBox.background"));
		panel_2.setBounds(45, 426, 1036, 252);
		frmArbitrageAlgoScreen.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		table_3 = new JTable();
		table_3.setBackground(UIManager.getColor("CheckBox.focus"));
		table_3.setBounds(6, 31, 985, 202);
		panel_2.add(table_3);
		
		JLabel lblOrderBook = new JLabel("Order Book");
		lblOrderBook.setForeground(UIManager.getColor("Button.focus"));
		lblOrderBook.setBackground(new Color(0, 0, 0));
		lblOrderBook.setFont(new Font("Arial", Font.BOLD, 15));
		lblOrderBook.setBounds(6, 6, 100, 32);
		panel_2.add(lblOrderBook);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setBounds(290, 980, 411, 53);
		frmArbitrageAlgoScreen.getContentPane().add(splitPane);
		frmArbitrageAlgoScreen.setBounds(100, 100, 1126, 1219);
		frmArbitrageAlgoScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
