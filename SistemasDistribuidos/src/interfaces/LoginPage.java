package interfaces;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cliente.CaesarCrypt;
import servidor.uteis.ValidarJson;

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

public class LoginPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtEmail;
	private JButton btnLogar;

	// Variáveis
	static PrintWriter saida = null;
	static BufferedReader entrada = null;
	private JPasswordField passwordField;

	public LoginPage(Socket echoSocket) {
		setTitle("Login");
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

		btnLogar = new JButton("Logar");
		btnLogar.setFont(new Font("Arial", Font.PLAIN, 14));
		btnLogar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JsonObject login = realizarLogin(echoSocket);
					// Mensagem
					if (ValidarJson.verificarCodigo(login)) {
						JOptionPane.showMessageDialog(null, "Login realizado!");
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {
									HomePage frame = new HomePage(echoSocket, login);
									frame.setVisible(true);
									frame.setLocationRelativeTo(null);
									dispose();
								} catch (Exception e) {
									JOptionPane.showMessageDialog(null, "Erro ao iniciar a página inicial.", "Erro",
											JOptionPane.ERROR_MESSAGE);
								}
							}
						});
					} else {
						if (ValidarJson.verificarMensagem(login) && login.has("codigo"))
							JOptionPane.showMessageDialog(null, login.get("mensagem").getAsString(),
									login.get("codigo").getAsString(), JOptionPane.ERROR_MESSAGE);
						else
							JOptionPane.showMessageDialog(null, "Json não possui o campo mensagem ou código.", "500",
									JOptionPane.ERROR_MESSAGE);
					}
				} catch (JsonSyntaxException | IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Erro durante o login.", "Erro", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		btnLogar.setBounds(62, 205, 100, 25);
		contentPane.add(btnLogar);

		JLabel lblNewLabel = new JLabel("LOGIN");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblNewLabel.setBounds(155, 23, 75, 30);
		contentPane.add(lblNewLabel);

		JButton btnFazerCadastro = new JButton("Fazer Cadastro ");
		btnFazerCadastro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
						    CadastroPage frame = new CadastroPage(echoSocket);
						    frame.setVisible(true);
						    frame.setLocationRelativeTo(null); 
						    dispose();
						} catch (Exception ex) {
						    JOptionPane.showMessageDialog(null, "Erro ao iniciar a página de cadastro.", "Erro", JOptionPane.ERROR_MESSAGE);
						}

					}
				});
			}
		});

		btnFazerCadastro.setFont(new Font("Arial", Font.PLAIN, 12));
		btnFazerCadastro.setBounds(204, 205, 120, 25);
		contentPane.add(btnFazerCadastro);

		passwordField = new JPasswordField();
		passwordField.setBounds(62, 155, 260, 30);
		contentPane.add(passwordField);
	}

	private JsonObject realizarLogin(Socket echoSocket) throws JsonSyntaxException, IOException {
		try {
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id_operacao", 3);

		jsonObject.addProperty("email", txtEmail.getText());
		String senha = CaesarCrypt.encrypt(new String(passwordField.getPassword()));
		jsonObject.addProperty("senha", senha);

		// Pre-definido
		jsonObject.addProperty("email", "lukaskenji@gmail.com");
		jsonObject.addProperty("senha", "t}si{9:;");

		saida.println(jsonObject);
		System.out.println("ENVIADO: " + jsonObject);
		Gson gson = new Gson();
		try {
			JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
			if (resposta_servidor != null)
				System.out.println("\nRESPOSTA: " + resposta_servidor);
			System.out.println("************************************************************************\n");
			return resposta_servidor;
		} catch (JsonSyntaxException | IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erro ao receber resposta do servidor", "Erro",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

	}

}
