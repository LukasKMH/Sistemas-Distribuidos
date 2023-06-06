package cliente.interfaces;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cliente.CaesarCrypt;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class ExcluirCadastroPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtEmail;
	private JButton btnLogar;

	// Variáveis
	static PrintWriter saida = null;
	static BufferedReader entrada = null;
	private JPasswordField passwordField;

	public ExcluirCadastroPage(Socket echoSocket, JsonObject login) {
		setTitle("Excluir cadastro");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 389, 280);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtEmail = new JTextField();
		txtEmail.setBounds(62, 91, 260, 30);
		contentPane.add(txtEmail);
		txtEmail.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Email:");
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(62, 73, 49, 14);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Senha:");
		lblNewLabel_2.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(62, 137, 49, 14);
		contentPane.add(lblNewLabel_2);

		btnLogar = new JButton("Excluir");
		btnLogar.setFont(new Font("Arial", Font.PLAIN, 14));
		btnLogar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JsonObject dados = excluirCadastro(echoSocket, login);
					// Mensagem
					if (dados != null) {
						if (dados.has("codigo") && Integer.parseInt(dados.get("codigo").getAsString()) == 200) {
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									try {
										LoginPage frame = new LoginPage(echoSocket);
										frame.setVisible(true);
										frame.setLocationRelativeTo(null);
										dispose();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						} 
					}

				} catch (JsonSyntaxException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnLogar.setBounds(222, 207, 100, 25);
		contentPane.add(btnLogar);

		JLabel lblNewLabel = new JLabel("EXCLUIR CADASTRO");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblNewLabel.setBounds(85, 23, 230, 30);
		contentPane.add(lblNewLabel);

		passwordField = new JPasswordField();
		passwordField.setBounds(62, 155, 260, 30);
		contentPane.add(passwordField);

		JButton btnVoltar = new JButton("Voltar");
		btnVoltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							HomePage frame = new HomePage(echoSocket, login);
							frame.setVisible(true);
							frame.setLocationRelativeTo(null);
							dispose();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnVoltar.setFont(new Font("Arial", Font.PLAIN, 14));
		btnVoltar.setBounds(62, 209, 100, 25);
		contentPane.add(btnVoltar);
	}

	public JsonObject excluirCadastro(Socket echoSocket, JsonObject login) throws JsonSyntaxException, IOException {
		JsonObject resposta_servidor = null;
		try {
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			int confirm = JOptionPane.showConfirmDialog(null, "Deseja excluir o incidente?");
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id_operacao", 8);
			jsonObject.addProperty("email", txtEmail.getText());
			jsonObject.addProperty("token", login.get("token").getAsString());
			jsonObject.addProperty("id_usuario", login.get("id_usuario").getAsString());

			// Pega e entrada sem criptografia
			// jsonObject.addProperty("senha", new String(passwordField.getPassword()));

			// Usando criptografia
			String senha = CaesarCrypt.encrypt(new String(passwordField.getPassword()));
			jsonObject.addProperty("senha", senha);
			if (confirm == JOptionPane.YES_OPTION) {
				try {
					saida.println(jsonObject);
					System.out.println("ENVIADO: " + jsonObject);
					Gson gson = new Gson();
					resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
					if (resposta_servidor != null)
						System.out.println("\nRESPOSTA: " + resposta_servidor);
					System.out.println("************************************************************************\n");
					// Mensagem
					if (Integer.parseInt(resposta_servidor.get("codigo").getAsString()) == 200) {
						JOptionPane.showMessageDialog(null, "Cliente excluído!");

					} else {
						JOptionPane.showMessageDialog(null, resposta_servidor.get("mensagem").getAsString(), resposta_servidor.get("codigo").getAsString(),
								JOptionPane.ERROR_MESSAGE);
					}
					return resposta_servidor;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resposta_servidor;
	}

}
