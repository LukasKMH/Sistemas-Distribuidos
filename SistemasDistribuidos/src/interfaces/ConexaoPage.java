package interfaces;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;

public class ConexaoPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtIP;
	private JTextField txtPorta;
	private JTextField textField;
	private Socket echoSocket;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConexaoPage frame = new ConexaoPage();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Erro ao iniciar a página de conexão.", "Erro",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

	}

	// Cria o Frame
	public ConexaoPage() {
		getContentPane().setLayout(null);

		textField = new JTextField();
		textField.setBounds(165, 35, 96, 20);
		getContentPane().add(textField);
		textField.setColumns(10);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 260, 260);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtIP = new JTextField();
		txtIP.setBounds(75, 60, 120, 25);
		contentPane.add(txtIP);
		txtIP.setColumns(10);

		txtPorta = new JTextField();
		txtPorta.setBounds(75, 110, 120, 25);
		contentPane.add(txtPorta);
		txtPorta.setColumns(10);

		JButton btnConectarServidor = new JButton("Conectar");
		btnConectarServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
//					String ipv4 = txtIP.getText().toString();
//					int porta = Integer.parseInt(txtPorta.getText().toString());

					// conectarServidor(ipv4, porta);
					// meu ip4 127.0.0.1
					// Salles 10.20.8.78
					 conectarServidor("10.20.8.132", 24001);
				} catch (Exception e1) {
					System.err.println("Campos Incorretos.");
					JOptionPane.showMessageDialog(null, "ipv4 ou Porta incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnConectarServidor.setBounds(85, 174, 89, 23);
		contentPane.add(btnConectarServidor);

		JLabel lblNewLabel = new JLabel("IP:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		lblNewLabel.setBounds(25, 65, 30, 14);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Porta:");
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(25, 115, 40, 14);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("CONEXÃO");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel_2.setBounds(89, 11, 90, 20);
		contentPane.add(lblNewLabel_2);
	}

	public void conectarServidor(String ipv4, int porta) {
		System.out.println("Tentando conectar ao host " + ipv4 + " na porta " + porta + ".\n");
		try {
			echoSocket = new Socket(ipv4, porta);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						LoginPage frame = new LoginPage(echoSocket);
						frame.setVisible(true);
						frame.setLocationRelativeTo(null);
						dispose();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Erro ao se conectar com o servidor.", "Erro",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		} catch (UnknownHostException e) {
			System.err.println("Não foi possível determinar o host: " + ipv4);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Nao foi possível obter entrada/saída para a conexão com: " + ipv4);
			System.exit(1);
		}
	}

	public Socket conectarServidor2(String ipv4, int porta) {
		System.out.println("Tentando conectar ao host " + ipv4 + " na porta " + porta + ".\n");
		try {
			echoSocket = new Socket(ipv4, porta);
		} catch (UnknownHostException e) {
			System.err.println("Nao foi possível determinar o host: " + ipv4);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Nao foi possível obter entrada/saída para a conexão com: " + ipv4);
			System.exit(1);
		}
		return echoSocket;
	}
}
