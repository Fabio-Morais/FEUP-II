package gui;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DataBase;
import db.Ordem;
import fabrica.Fabrica;
import fabrica.Ordens;
import fabrica.Stock;
import opc.OpcClient;
import udp.estatistica.Estatistica;

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
	private Timer counterTimer3;
	private Timer counterTimer4;
	private Timer counterTimer5;
	private JLabel label_1;
	private Fabrica fabrica;
	private JLabel dbIcon;
	private JLabel opcIcon;
	private JButton btnConectarDb;
	private JButton btnConectarOpc;
	private JLabel hora;

	private boolean opcRunning;
	private boolean dbRunning;
	private DataBase db;
	private OpcClient opc;
	private JPanel panel_2;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the application.
	 */
	public Gui() {
		this.db = DataBase.getInstance();

		this.opc = OpcClient.getInstance();
		this.estatistica = new Estatistica();

		this.fabrica = Fabrica.getInstance();
		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("MES");

		frame.getContentPane().setBackground(SystemColor.desktop);
		frame.setBounds(100, 100, 1020, 620);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setSize((int) ((int) dimension.getWidth() * 0.75), (int) ((int) dimension.getHeight() * 0.75));
		frame.setLocation(x, y);
		panel_2 = new JPanel();
		panel_2.setAlignmentX(0.0f);
		panel_2.setBackground(Color.GRAY);
		URL iconURL = getClass().getResource("/img/logo.png");
		ImageIcon img = new ImageIcon(iconURL);
		frame.setIconImage(img.getImage());

		initializeFrame(panel_2);
		initializeButtons();

		backgroundTimer();
		backgroundTimerConexoes();
		backgroundTimerHora();
		counterTimer3.start();
		counterTimer4.start();
		counterTimer2.start();
		opcRunning = false;
		dbRunning = false;

	}

	private void initializeFrame(JPanel panel) {
		JPanel panel_1 = new JPanel();
		panel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				table.clearSelection();
				table_2.clearSelection();
				table_1.clearSelection();

			}
		});
		panel_1.setAlignmentX(0.0f);
		panel_1.setBackground(new Color(204, 204, 204));
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 758, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel_2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
				.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE));

		JScrollPane scrollPane = new JScrollPane();

		JLabel lblNewLabel = new JLabel("Ordens em execu\u00E7\u00E3o");
		DefaultDesign.styleLabel(lblNewLabel);

		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel label = new JLabel("Ordens em espera");
		DefaultDesign.styleLabel(label);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		JScrollPane scrollPane_1 = new JScrollPane();

		JLabel lblNewLabel_1 = new JLabel("Stock");
		DefaultDesign.styleLabel(lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);

		JScrollPane scrollPane_2 = new JScrollPane();

		JSeparator separator = new JSeparator();

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		panel_4.setBackground(new Color(204, 204, 204));

		JLabel logo = new JLabel("");
		logo.setIcon(new ImageIcon(Gui.class.getResource("/img/logo3.png")));

		JLabel lblInf = new JLabel("Inf\u00F3rmatica Industrial 19/20");
		lblInf.setFont(new Font("HP Simplified", Font.BOLD, 24));
		Color colorText = Color.decode("#364f6b");
		lblInf.setForeground(colorText);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel_1
				.createSequentialGroup()
				.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel_1.createSequentialGroup()
						.addContainerGap()
						.addComponent(logo, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lblInf, GroupLayout.PREFERRED_SIZE, 302, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 185, Short.MAX_VALUE)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panel_1.createSequentialGroup()
										.addComponent(separator, GroupLayout.PREFERRED_SIZE, 1,
												GroupLayout.PREFERRED_SIZE)
										.addGap(74))
								.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)))
						.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup().addGap(50).addGroup(gl_panel_1
								.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 671,
										Short.MAX_VALUE)
								.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 671,
										Short.MAX_VALUE)
								.addGroup(gl_panel_1.createSequentialGroup()
										.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
												.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 235,
														Short.MAX_VALUE)
												.addComponent(lblNewLabel_1, Alignment.TRAILING,
														GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
										.addGap(99)
										.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
												.addComponent(scrollPane_1, 0, 0, Short.MAX_VALUE).addComponent(label,
														GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE))))))
				.addGap(37)));
		gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1
				.createSequentialGroup().addContainerGap()
				.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup().addGap(21)
								.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
								.addGap(28).addComponent(separator, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
								.addComponent(logo, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblInf, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel_1.createSequentialGroup()
								.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(scrollPane_2, 0, 171, Short.MAX_VALUE))
						.addGroup(gl_panel_1.createSequentialGroup()
								.addComponent(label, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(scrollPane_1, 0, 0, Short.MAX_VALUE)))
				.addPreferredGap(ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
				.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE).addGap(32)));

		JLabel lblP = new JLabel("Pe\u00E7as na f\u00E1brica");
		DefaultDesign.styleLabel(lblP);

		label_1 = new JLabel("2");
		label_1.setFont(new Font("Consolas", Font.BOLD, 16));
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setForeground(Color.BLACK);
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_4.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblP, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE).addGap(2)));
		gl_panel_4.setVerticalGroup(gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_4.createSequentialGroup().addGap(2)
						.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblP, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
								.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));
		panel_4.setLayout(gl_panel_4);
		panel_1.setLayout(gl_panel_1);

		btnNewButton = new JButton("Gerar relat\u00F3rios");
		btnNewButton.setMargin(new Insets(2, 30, 2, 14));
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		DefaultDesign.buttons(btnNewButton);

		btnVerRelatrios = new JButton("Ver relat\u00F3rios");
		btnVerRelatrios.setMargin(new Insets(2, 30, 2, 14));
		btnVerRelatrios.setHorizontalAlignment(SwingConstants.LEFT);
		btnVerRelatrios.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		DefaultDesign.buttons(btnVerRelatrios);

		JPanel panel_3 = new JPanel();

		btnConectarDb = new JButton("Conectar DB");
		btnConectarDb.setMargin(new Insets(2, 30, 2, 14));
		btnConectarDb.setHorizontalAlignment(SwingConstants.LEFT);
		btnConectarDb.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		DefaultDesign.buttons(btnConectarDb);

		btnConectarOpc = new JButton("Conectar OPC");
		btnConectarOpc.setMargin(new Insets(2, 30, 2, 14));
		btnConectarOpc.setHorizontalAlignment(SwingConstants.LEFT);
		btnConectarOpc.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		DefaultDesign.buttons(btnConectarOpc);

		dbIcon = new JLabel("");
		dbIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/off3.png")));

		opcIcon = new JLabel("");
		opcIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/off3.png")));

		hora = new JLabel("17/04/2020 15:37:20");
		DefaultDesign.styleLabelData(hora);
		String aux = Ordem.converteData(Ordem.localDate());
		String[] split = aux.split(" ");
		if (split.length == 2) {
			hora.setText(split[0] + "  -  " + split[1]);
		}
		hora.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_panel_2 = new GroupLayout(panel);
		gl_panel_2.setHorizontalGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel_2
				.createSequentialGroup()
				.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel_2.createSequentialGroup()
						.addGap(11)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panel_2.createSequentialGroup()
										.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
												.addComponent(btnVerRelatrios, GroupLayout.PREFERRED_SIZE, 162,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 162,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(hora, GroupLayout.PREFERRED_SIZE, 162,
														GroupLayout.PREFERRED_SIZE))
										.addGap(33))))
						.addGroup(gl_panel_2.createSequentialGroup().addGap(39).addGroup(gl_panel_2
								.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_2.createSequentialGroup()
										.addComponent(btnConectarDb, GroupLayout.PREFERRED_SIZE, 162,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
										.addComponent(dbIcon))
								.addGroup(gl_panel_2.createSequentialGroup()
										.addComponent(btnConectarOpc, GroupLayout.PREFERRED_SIZE, 162,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED, 11, Short.MAX_VALUE).addComponent(
												opcIcon, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)))))
				.addContainerGap()));
		gl_panel_2.setVerticalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_2
				.createSequentialGroup().addContainerGap().addComponent(hora).addGap(30)
				.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE).addGap(33)
				.addComponent(btnVerRelatrios, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED, 160, Short.MAX_VALUE)
				.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
						.addComponent(opcIcon, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnConectarOpc, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
				.addGap(14)
				.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
						.addComponent(dbIcon, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnConectarDb, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
				.addGap(38).addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
				.addGap(11)));
		panel_3.setLayout(new GridLayout(4, 0, 0, 0));

		nomes(panel_3);

		panel.setLayout(gl_panel_2);
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

		btnConectarDb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (db.checkConnection()) {
					dbIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/on3.png")));
				} else {
					dbIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/off3.png")));
				}
			}
		});

		btnConectarOpc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// All code inside SwingWorker runs on a seperate thread
				SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
					@Override
					public Boolean doInBackground() {
						if (opc.connect())
							return true;
						else
							return false;
					}

					@Override
					public void done() {
						try {
							if (get()) {
								opcIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/on3.png")));
								opcRunning = true;
							} else {
								opcIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/off3.png")));
								opcRunning = false;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				};

				// Call the SwingWorker from within the Swing thread
				worker.execute();
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

		JSpinner spinner = new JSpinner();

		spinner.setValue(10);

		panel.add(spinner);

		if (JOptionPane.showOptionDialog(null, panel, "Gerar Relatorio", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options1, options1[1]) == JOptionPane.YES_OPTION) {
			backgroundTimerExportar(((int) spinner.getValue()) * 1000);
			counterTimer.start();
		}

	}

	private void tableStockJScrollPane(JScrollPane scrollPane_2) {
		modelStock = new DefaultTableModel(
				new Object[][] { { "P1", "2" }, { "P2", "3" }, { "P3", "2" }, { "P4", "1" }, { "P5", "5" },
						{ "P6", "12" }, { "P7", "32" }, { "P8", "4" }, { "P9", "7" }, },
				new String[] { "Tipo pe\u00E7a", "Quantidade" }) {

			private static final long serialVersionUID = 1880689174093893276L;
			boolean[] columnEditables = new boolean[] { false, false };
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class, String.class };

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

		modelOrdemProcessamento = new DefaultTableModel(new Object[][] {}, new String[] { "numero ordem",
				"Tempo restante", "Tipo Ordem", "Peças produzidas", "Peças em produção", "Peças pendentes", "Operação" }) {

			private static final long serialVersionUID = 1880689174093893276L;
			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false };
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class, String.class,
					String.class, String.class };

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

		modelOrdemEspera = new DefaultTableModel(new Object[][] {},
				new String[] { "Numero Ordem", "tipo de ordem", "tempo restante", "Operação" }) {

			private static final long serialVersionUID = 1880689174093893276L;
			boolean[] columnEditables = new boolean[] { false, false, false, false };
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class };

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
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(String.class, centerRenderer);

		table.setModel(model);

		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoCreateRowSorter(true);// para ordenar
		table.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	private void backgroundTimerExportar(int time) {
		if (counterTimer != null) {
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

		counterTimer2 = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (opcRunning) {
					stock();
					pecasFabrica();
				}
				ordensPendente();
				ordensExecucao();

			}

		});

	}

	private void backgroundTimerConexoes() {
		counterTimer3 = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 verificaConexoes();
			}
		});
	}

	private void backgroundTimerHora() {
		counterTimer4 = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// All code inside SwingWorker runs on a seperate thread
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					public Void doInBackground() {
						String aux = Ordem.converteData(Ordem.localDate());
						String[] split = aux.split(" ");
						if (split.length == 2) {
							hora.setText(split[0] + "  -  " + split[1]);
						}
						return null;
					}
				};

				// Call the SwingWorker from within the Swing thread
				worker.execute();
				
			}

		});

	}



	private void ordensExecucao() {
		HashMap<String, Ordens> aux = fabrica.getCopyHeapOrdemExecucao();
		int size = aux.size();
		int i = 0;
		for (Map.Entry<String, Ordens> entry : aux.entrySet()) {

			Ordens ord = entry.getValue();
			if (i < modelOrdemProcessamento.getRowCount()) {
				modelOrdemProcessamento.setValueAt(ord.getNumeroOrdem(), i, 0);
				modelOrdemProcessamento.setValueAt("" + (ord.getPrioridade() == -1 ? 0 : ord.getPrioridade()), i, 1);
				modelOrdemProcessamento.setValueAt((ord.getAtrasoMaximo() == -1 ? "Descarga" : "Transormacao"), i, 2);
				modelOrdemProcessamento.setValueAt(ord.getPecasProduzidas(), i, 3);
				modelOrdemProcessamento.setValueAt(ord.getPecasEmProducao(), i, 4);
				modelOrdemProcessamento.setValueAt(ord.getPecasPendentes(), i, 5);
				if(ord.getTransform() != null)
					modelOrdemProcessamento.setValueAt(ord.getTransform().getFrom()+"->"+ord.getTransform().getTo(), i, 6);
				else if(ord.getUnload() != null)
					modelOrdemProcessamento.setValueAt(ord.getUnload().getType()+"->"+ord.getUnload().getDestinantion(), i, 6);
			} else {
				modelOrdemProcessamento.addRow(
						new Object[] { ord.getNumeroOrdem(), "" + (ord.getPrioridade() == -1 ? 0 : ord.getPrioridade()),
								(ord.getAtrasoMaximo() == -1 ? "Descarga" : "Transormacao"), ord.getPecasProduzidas(),
								ord.getPecasEmProducao(), ord.getPecasPendentes() });
				if(ord.getTransform() != null)
					modelOrdemProcessamento.setValueAt(ord.getTransform().getFrom()+"->"+ord.getTransform().getTo(), i, 6);
				else if(ord.getUnload() != null)
					modelOrdemProcessamento.setValueAt(ord.getUnload().getType()+"->"+ord.getUnload().getDestinantion(), i, 6);
			}
			i++;

		}

		for (int j = size; j < modelOrdemProcessamento.getRowCount(); j++) {
			modelOrdemProcessamento.removeRow(j);
		}
	}

	private void ordensPendente() {
		PriorityQueue<Ordens> aux = fabrica.getCopyHeapOrdemPendente();
		int size = aux.size();
		for (int i = 0; i < size; i++) {
			Ordens ord = aux.poll();
			if (i < modelOrdemEspera.getRowCount()) {
				modelOrdemEspera.setValueAt(ord.getNumeroOrdem(), i, 0);
				modelOrdemEspera.setValueAt((ord.getAtrasoMaximo() == -1 ? "Descarga" : "Transormacao"), i, 1);
				modelOrdemEspera.setValueAt("" + (ord.getPrioridade() == -1 ? 0 : ord.getPrioridade()), i, 2);
				if(ord.getTransform() != null)
					modelOrdemEspera.setValueAt(ord.getTransform().getFrom()+"->"+ord.getTransform().getTo(), i, 3);
				else if(ord.getUnload() != null)
					modelOrdemEspera.setValueAt(ord.getUnload().getType()+"->"+ord.getUnload().getDestinantion(), i, 3);
			} else {
				modelOrdemEspera.addRow(
						new Object[] { ord.getNumeroOrdem(), (ord.getAtrasoMaximo() == -1 ? "Descarga" : "Transormacao"),
								"" + (ord.getPrioridade() == -1 ? 0 : ord.getPrioridade()),"" });
				if(ord.getTransform() != null)
					modelOrdemEspera.setValueAt(ord.getTransform().getFrom()+"->"+ord.getTransform().getTo(), i, 3);
				else if(ord.getUnload() != null)
					modelOrdemEspera.setValueAt(ord.getUnload().getType()+"->"+ord.getUnload().getDestinantion(), i, 3);
			}

		}
		for (int i = size; i < modelOrdemEspera.getRowCount(); i++) {
			modelOrdemEspera.removeRow(i);
		}
		/*
		 * FALTA REMOVER AS QUE JA NAO EXISTEM
		 */
	}

	private void stock() {
		short[] stock = opc.getValue("SFS", "Stock");
		for (int i = 0; i < stock.length; i++) {
			modelStock.setValueAt("P" + (i + 1), i, 0);
			modelStock.setValueAt("" + stock[i], i, 1);
			Stock.setPecasStock((short) i, stock[i]);
		}
	}

	private void pecasFabrica() {
		label_1.setText("" + opc.getValue("SFS", "PecasnoSistema")[0]);

	}

	private void verificaConexoes() {
		// All code inside SwingWorker runs on a seperate thread
		SwingWorker<Boolean, Void> worker1 = new SwingWorker<Boolean, Void>() {
			@Override
			public Boolean doInBackground() {
		if (db.checkConnection()) {
			dbRunning = true;
			return true;
		} else {
			dbRunning = false;
			return false;
		}
			}
			@Override
			public void done() {
				try {
					if(get()) {
						dbIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/on3.png")));

					}else {
						dbIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/off3.png")));

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};

		// Call the SwingWorker from within the Swing thread
		worker1.execute();
			

		// All code inside SwingWorker runs on a seperate thread
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			public Boolean doInBackground() {
				if (opc.getValue("SFS", "PecasnoSistema").length > 0)
					return true;
				else
					return false;
			}

			@Override
			public void done() {
				try {
					if (get()) {
						opcIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/on3.png")));
						opcRunning = true;
					} else {
						opcIcon.setIcon(new ImageIcon(Gui.class.getResource("/img/off3.png")));
						opcRunning = false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};

		// Call the SwingWorker from within the Swing thread
		worker.execute();

	}

}
