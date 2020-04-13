package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.Color;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerDateModel;

import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.PriorityQueue;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;

import fabrica.Fabrica;
import fabrica.Ordens;
import opc.OpcClient;
import udp.estatistica.Estatistica;

import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.Font;

public class Gui {

	public JFrame frame;
	private JTable table;
	private JTable table_1;
	private JTable table_2;
	private DefaultTableModel modelStock;
	private DefaultTableModel modelOrdemProcessamento;
	private DefaultTableModel modelOrdemEspera;
	private JButton btnNewButton;
	private JButton btnVerRelatrios;
	private Estatistica estatistica;
	private Timer counterTimer;
	private Timer counterTimer2;
	private JLabel label_1;
	private OpcClient opcClient;
	private Fabrica fabrica;
	/**
	 * Launch the application.
	 */

	/**
	 * Create the application.
	 */
 	public Gui() {
		this.estatistica = new Estatistica();
		this.opcClient = OpcClient.getInstance(); 
		this.fabrica = Fabrica.getInstance();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("MES");
		

		frame.getContentPane().setBackground(SystemColor.desktop);
		frame.setBounds(100, 100, 1020,620);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setSize((int)((int)dimension.getWidth()*0.75) , (int)((int)dimension.getHeight() *0.75));
		frame.setLocation(x, y);
		JPanel panel = new JPanel();
		panel.setAlignmentX(0.0f);
		panel.setBackground(Color.GRAY);
		
		initializeFrame(panel);
		initializeButtons();
		
		backgroundTimer();
		counterTimer2.start();
		
		
	}
	private void initializeFrame(JPanel panel) {
		JPanel panel_1 = new JPanel();
		panel_1.setAlignmentX(0.0f);
		panel_1.setBackground(new Color(204, 204, 204));
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
				.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
		);
		
		JLabel lblP = new JLabel("Pe\u00E7as na f\u00E1brica");
		lblP.setBackground(SystemColor.window);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(SystemColor.window);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel lblNewLabel = new JLabel("Ordens em execu\u00E7\u00E3o");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel label = new JLabel("Ordens em espera");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel lblNewLabel_1 = new JLabel("Stock");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(48)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
						.addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
					.addGap(70)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
						.addComponent(scrollPane_1, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
						.addComponent(label, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
					.addGap(68))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap(482, Short.MAX_VALUE)
					.addComponent(lblP, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addGap(100))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
							.addGap(140)
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
							.addGap(0, 0, Short.MAX_VALUE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(37)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lblP, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
							.addGap(40)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
					.addGap(52))
		);
		
	
		
		label_1 = new JLabel("2");
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
					.addGap(10))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addComponent(label_1, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
		);
		panel_2.setLayout(gl_panel_2);
		panel_1.setLayout(gl_panel_1);
		
		btnNewButton = new JButton("Gerar relat\u00F3rios");
		
		
		btnVerRelatrios = new JButton("Ver relat\u00F3rios");
		
		
		JPanel panel_3 = new JPanel();
		
		JButton btnConectarDb = new JButton("Conectar DB");
		
		JButton btnConectarOpc = new JButton("Conectar OPC");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(39)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(btnVerRelatrios, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnConectarDb, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnConectarOpc, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(11, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(40)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addGap(33)
					.addComponent(btnVerRelatrios, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
					.addComponent(btnConectarOpc, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnConectarDb, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addGap(39)
					.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		panel_3.setLayout(new GridLayout(4, 0, 0, 0));
		
		nomes(panel_3);
		
		
		panel.setLayout(gl_panel);
		frame.getContentPane().setLayout(groupLayout);
		
		tableStockJScrollPane(scrollPane_2);
		tableOrdemProcessamento(scrollPane);
		tableOrdemEspera(scrollPane_1);
	}
	private void nomes(JPanel panel_3) {
		JLabel lblNewLabel_2 = new JLabel("F\u00E1bio Morais");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("TI-Nspire", Font.BOLD, 14));
		panel_3.add(lblNewLabel_2);
		
		JLabel lblDiogoSilva = new JLabel("Diogo Silva");
		lblDiogoSilva.setHorizontalAlignment(SwingConstants.CENTER);
		lblDiogoSilva.setFont(new Font("TI-Nspire", Font.BOLD, 14));
		panel_3.add(lblDiogoSilva);
		
		JLabel lblMarcoRocha = new JLabel("Marco Rocha");
		lblMarcoRocha.setHorizontalAlignment(SwingConstants.CENTER);
		lblMarcoRocha.setFont(new Font("TI-Nspire", Font.BOLD, 14));
		panel_3.add(lblMarcoRocha);
		
		JLabel lblJooSantos = new JLabel("Jo\u00E3o Santos");
		lblJooSantos.setHorizontalAlignment(SwingConstants.CENTER);
		lblJooSantos.setFont(new Font("TI-Nspire", Font.BOLD, 14));
		panel_3.add(lblJooSantos);
	}
	
	private void initializeButtons() {
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				criaPopUp();
					
				
			}
		});
		btnVerRelatrios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				estatistica.exportaFicheiros(true);
			}
		});
	}
	private void criaPopUp() {
		Object[] options1 = { "Ok", "Sair" };
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 1, 10, 10));


		
		JLabel lblNome2 = new JLabel("Intervalo de tempo a gerar (em s)");
		panel.add(lblNome2);
		SpinnerDateModel model = new SpinnerDateModel();
		model.setCalendarField(Calendar.MINUTE);


		JSpinner spinner= new JSpinner();

		spinner.setValue(10);


        panel.add(spinner);
		
		if(JOptionPane.showOptionDialog(null, panel, "Gerar Relatorio", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options1, options1[1])== JOptionPane.YES_OPTION) {
			backgroundTimerExportar(((int)spinner.getValue()) * 1000);
			counterTimer.start();
		}

	}
	
	private void tableStockJScrollPane (JScrollPane scrollPane_2) {
		modelStock = new DefaultTableModel(new Object[][]  {
			{"P1", "2"},
			{"P2", "3"},
			{"P3", "2"},
			{"P4", "1"},
			{"P5", "5"},
			{"P6", "12"},
			{"P7", "32"},
			{"P8", "4"},
			{"P9", "7"},
		}, new String[] { "Tipo pe\u00E7a", "Quantidade" }) {

			private static final long serialVersionUID = 1880689174093893276L;
			boolean[] columnEditables = new boolean[] { false, false };
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class,  String.class,  String.class };

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		
		table_2 = new JTable();
		tableStyle(table_2, modelStock);
		scrollPane_2.setViewportView(table_2);

	}
	
	private void tableOrdemProcessamento(JScrollPane scrollPane) {
		
		modelOrdemProcessamento = new DefaultTableModel(new Object[][]  {
			{"123", "50", "carga"},
			{"12", "2", "descarga"},
			{"43", "12", "carga"},
			{"23432", "32", "descarga"},
			{"43", "534", "carga"},
			{"434", "1232", "carga"},
			{"1213", "123", null},
			{null, "123", null}}, new String[] {  "numero ordem", "Tempo restante", "Tipo Ordem" }) {

			private static final long serialVersionUID = 1880689174093893276L;
			boolean[] columnEditables = new boolean[] { false, false };
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class,  String.class,  String.class };

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		
		table = new JTable();
		tableStyle(table, modelOrdemProcessamento);
		scrollPane.setViewportView(table);
	}
	
	private void tableOrdemEspera(JScrollPane scrollPane_1) {

		modelOrdemEspera = new DefaultTableModel(new Object[][]  {
		}, new String[] {  "Numero Ordem","tipo de ordem", "tempo restante" }) {

			private static final long serialVersionUID = 1880689174093893276L;
			boolean[] columnEditables = new boolean[] { false, false, false };
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class , String.class  };

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};

		table_1 = new JTable();
		tableStyle(table_1, modelOrdemEspera);

		scrollPane_1.setViewportView(table_1);
	
	}
	
	private void tableStyle(JTable table, DefaultTableModel model) {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		table.setDefaultRenderer(String.class, centerRenderer);
		
		table.setModel(model);


		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoCreateRowSorter(true);// para ordenar
		table.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	private void backgroundTimerExportar(int time) {
		if(counterTimer != null) {
			counterTimer.stop();
			counterTimer = null;
		}
		counterTimer = new Timer(time, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				estatistica.exportaFicheiros(false);

			}

		});

	}
	
	private void backgroundTimer() {
		
		counterTimer2 = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stock();
				ordensPendente();
				ordensExecucao();
				pecasFabrica();

			}

		});

	}
	private void ordensExecucao() {
		
	}
	
	private void ordensPendente() {
		PriorityQueue<Ordens> aux = fabrica.getCopyHeapOrdemPendente();
		int size = aux.size();
		for(int i=0; i<size; i++) {
			Ordens ord = aux.poll();
			if(i<modelOrdemEspera.getRowCount()) {
				modelOrdemEspera.setValueAt(ord.getNumeroOrdem(), i, 0);
				modelOrdemEspera.setValueAt((ord.getPrioridade() == -1 ? "Descarga" : "Carga"), i, 1);
				modelOrdemEspera.setValueAt(""+ (ord.getPrioridade()== -1 ? 0 : ord.getPrioridade()), i, 2);
			}else {				
				modelOrdemEspera.addRow(new Object[] {ord.getNumeroOrdem(), (ord.getPrioridade()== -1 ? "Descarga" : "Carga"), 
						""+ (ord.getPrioridade()== -1 ? 0 : ord.getPrioridade()) });
			}
			

			
			
		}
		for(int i=size; i<modelOrdemEspera.getRowCount(); i++) {
			modelOrdemEspera.removeRow(i);
		}
		/*
		 * FALTA REMOVER AS QUE JA NAO EXISTEM
		 * */
	}
	private void stock() {
		short[] stock = opcClient.getValue("SFS","Stock");
		for(int i=0; i<stock.length; i++) {
			modelStock.setValueAt("P"+(i+1), i, 0);
			modelStock.setValueAt(""+stock[i], i, 1);

		}
	}
	private void pecasFabrica() {
		label_1.setText(""+opcClient.getValue("SFS","PecasSistema")[0]);

	}
	
}

