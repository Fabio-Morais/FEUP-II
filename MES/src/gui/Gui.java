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
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;

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
	private JButton btnNewButton;
	private JButton btnVerRelatrios;
	private Estatistica estatistica;
	private Timer counterTimer;
	private JLabel label_1;
	private OpcClient opcClient = OpcClient.getInstance();
	/**
	 * Launch the application.
	 */

	/**
	 * Create the application.
	 */
 	public Gui() {
		this.estatistica = new Estatistica();
		initialize();
		timerPecas();
	}
	public void abreGui() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("MES");
		frame.getContentPane().setBackground(SystemColor.desktop);
		frame.setBounds(100, 100, 874, 506);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setAlignmentX(0.0f);
		panel.setBackground(Color.GRAY);
		
		initializeFrame(panel);
		initializeButtons();
		
		
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
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
							.addGap(8))
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
					.addGap(62)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_panel_1.createParallelGroup(Alignment.TRAILING)
							.addComponent(lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(scrollPane_1, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
							.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
						.addComponent(label, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
					.addGap(68))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap(336, Short.MAX_VALUE)
					.addComponent(lblP, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addGap(100))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(37)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(lblP, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addGap(40)
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
					.addGap(52))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(140)
					.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
					.addGap(133))
		);
		
		table_2 = new JTable();
		scrollPane_2.setViewportView(table_2);
		table_2.setModel(new DefaultTableModel(
			new Object[][] {
				{"P1", "2"},
				{"P2", "3"},
				{"P3", "2"},
				{"P4", "1"},
				{"P5", "5"},
				{"P6", "12"},
				{"P7", "32"},
				{"P8", "4"},
				{"P9", "7"},
			},
			new String[] {
				"Tipo pe\u00E7a", "Quantidade"
			}
		));
		
		table_1 = new JTable();
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
				{"123"},
				{"32"},
				{"2"},
				{"4"},
				{"55"},
				{"32"},
				{"12"},
				{"32"},
				{null},
			},
			new String[] {
				"numero ordem"
			}
		));
		scrollPane_1.setViewportView(table_1);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"123", "50", "carga"},
				{"12", "2", "descarga"},
				{"43", "12", "carga"},
				{"23432", "32", "descarga"},
				{"43", "534", "carga"},
				{"434", "1232", "carga"},
				{"1213", "123", null},
				{null, "123", null},
			},
			new String[] {
				"numero ordem", "Tempo restante", "Tipo Ordem"
			}
		));
		table.getColumnModel().getColumn(0).setPreferredWidth(85);
		table.getColumnModel().getColumn(1).setPreferredWidth(88);
		scrollPane.setViewportView(table);
		
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
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(39)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(btnVerRelatrios, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)))
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
					.addPreferredGap(ComponentPlacement.RELATED, 237, Short.MAX_VALUE)
					.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		panel_3.setLayout(new GridLayout(4, 0, 0, 0));
		
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
		panel.setLayout(gl_panel);
		frame.getContentPane().setLayout(groupLayout);
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
			backgroundTimer(((int)spinner.getValue()) * 1000);
			counterTimer.start();
		}

	}
	
	
	private void backgroundTimer(int time) {
		if(counterTimer != null) {
			counterTimer.stop();
			counterTimer = null;
		}
		counterTimer = new Timer(time, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("entrou");
				estatistica.exportaFicheiros(false);

			}

		});

		

	}
	
	private void timerPecas() {
		Timer time = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				label_1.setText(""+opcClient.getValue("SFS","PecasSistema")[0]);
			}

		});
		
		time.start();

	}

}

