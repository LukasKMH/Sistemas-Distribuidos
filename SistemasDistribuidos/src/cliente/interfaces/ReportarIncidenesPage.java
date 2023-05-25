package cliente.interfaces;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JComboBox;
import javax.swing.JButton;

public class ReportarIncidenesPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtData;
	private JTextField txtRodovia;
	private JTextField txtFaixa;
	private JTextField txtKm;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReportarIncidenesPage frame = new ReportarIncidenesPage();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ReportarIncidenesPage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 359, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblReportarIncidente = new JLabel("REPORTAR INCIDENTE");
		lblReportarIncidente.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblReportarIncidente.setBounds(42, 11, 250, 30);
		contentPane.add(lblReportarIncidente);
		
		txtData = new JTextField();
		txtData.setBounds(33, 86, 100, 25);
		contentPane.add(txtData);
		txtData.setColumns(10);
		
		txtRodovia = new JTextField();
		txtRodovia.setBounds(199, 86, 100, 25);
		contentPane.add(txtRodovia);
		txtRodovia.setColumns(10);
		
		txtFaixa = new JTextField();
		txtFaixa.setBounds(199, 141, 100, 25);
		contentPane.add(txtFaixa);
		txtFaixa.setColumns(10);
		
		JComboBox comboTipoIncidente = new JComboBox();
		comboTipoIncidente.setBounds(33, 201, 100, 25);
		contentPane.add(comboTipoIncidente);
		
		JLabel lblNewLabel = new JLabel("Data:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		lblNewLabel.setBounds(33, 64, 49, 16);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Km:");
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(33, 119, 49, 16);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblRodovia = new JLabel("Rodovia:");
		lblRodovia.setFont(new Font("Arial", Font.PLAIN, 14));
		lblRodovia.setBounds(199, 64, 60, 16);
		contentPane.add(lblRodovia);
		
		txtKm = new JTextField();
		txtKm.setColumns(10);
		txtKm.setBounds(33, 141, 100, 25);
		contentPane.add(txtKm);
		
		JLabel lblNewLabel_1_1 = new JLabel("Faixa:");
		lblNewLabel_1_1.setFont(new Font("Arial", Font.PLAIN, 14));
		lblNewLabel_1_1.setBounds(199, 119, 49, 16);
		contentPane.add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_2 = new JLabel("Tipo do incidene:");
		lblNewLabel_1_2.setFont(new Font("Arial", Font.PLAIN, 14));
		lblNewLabel_1_2.setBounds(33, 179, 120, 16);
		contentPane.add(lblNewLabel_1_2);
		
		JButton btnNewButton = new JButton("Reportar");
		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 14));
		btnNewButton.setBounds(199, 201, 100, 25);
		contentPane.add(btnNewButton);
	}
}
