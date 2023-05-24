package cliente.UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import java.awt.Font;

public class IP_Server extends JFrame {

	private JPanel contentPane;
	private JTextField txtIP;
	private JTextField txtPorta;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IP_Server frame = new IP_Server();
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
	public IP_Server() {
		getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(165, 35, 96, 20);
		getContentPane().add(textField);
		textField.setColumns(10);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 260);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtIP = new JTextField();
		txtIP.setBounds(90, 60, 96, 20);
		contentPane.add(txtIP);
		txtIP.setColumns(10);
		
		txtPorta = new JTextField();
		txtPorta.setBounds(90, 110, 96, 20);
		contentPane.add(txtPorta);
		txtPorta.setColumns(10);
		
		JButton btnConectarServidor = new JButton("Conectar");
		btnConectarServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ipv4 = txtIP.getText().toString();
				int porta = Integer.parseInt(txtPorta.getText().toString());
				Socket echoSocket = null;
				PrintWriter saida = null;
				BufferedReader entrada = null;
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Attemping to connect to host " + ipv4 + " on port " + porta + ".");

				try {
					echoSocket = new Socket(ipv4, porta);
					saida = new PrintWriter(echoSocket.getOutputStream(), true);
					entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
				} catch (UnknownHostException e1) {
					System.err.println("Don't know about host: " + ipv4);
					System.exit(1);
				} catch (IOException e2) {
					System.err.println("Couldn't get I/O for " + "the connection to: " + ipv4);
					System.exit(1);
				}

			}
		});
		btnConectarServidor.setBounds(90, 174, 89, 23);
		contentPane.add(btnConectarServidor);
		
		JLabel lblNewLabel = new JLabel("IP");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel.setBounds(40, 60, 30, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Porta");
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(40, 110, 40, 14);
		contentPane.add(lblNewLabel_1);
	}
}
