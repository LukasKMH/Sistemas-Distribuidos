package cliente.interfaces;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cliente.CaesarCrypt;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class EditarCadastroPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNome;
	private JTextField txtEmail;
	private JButton btnLogar;

	// Variáveis

	private static PrintWriter saida = null;
	private static BufferedReader entrada = null;
	private JPasswordField passwordField;

	public EditarCadastroPage(Socket echoSocket, JsonObject login) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("Conectado ao servidor - CADASTRO.\n");

		setBounds(100, 100, 389, 340);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtEmail = new JTextField();
		txtEmail.setBounds(60, 151, 260, 30);
		contentPane.add(txtEmail);
		txtEmail.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Email:");
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(60, 133, 49, 14);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Senha:");
		lblNewLabel_2.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(60, 197, 49, 14);
		contentPane.add(lblNewLabel_2);

		btnLogar = new JButton("Atualizar Dados");
		btnLogar.setFont(new Font("Arial", Font.BOLD, 16));
		btnLogar.addActionListener(e -> {
			JsonObject dados = editarCadastro(echoSocket, login);
			int codigo = dados.get("codigo").getAsInt();

			if (codigo == 200) {
				JOptionPane.showMessageDialog(null, "Dados atualizados!");
				int idUsuario = Integer.parseInt(login.get("id_usuario").getAsString());
				dados.addProperty("id_usuario", idUsuario);
				EventQueue.invokeLater(() -> {
					try {
						HomePage frame = new HomePage(echoSocket, dados);
						frame.setVisible(true);
						frame.setLocationRelativeTo(null);
						dispose();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "Ocorreu um erro ao retornar a Home Page.", "Erro",
								JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
				});
			} else {
				JOptionPane.showMessageDialog(null, dados.get("mensagem").getAsString(), "Erro",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		btnLogar.setBounds(160, 267, 160, 25);
		contentPane.add(btnLogar);

		JLabel lblNewLabel = new JLabel("EDITAR CADASTRO");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblNewLabel.setBounds(80, 23, 220, 30);
		contentPane.add(lblNewLabel);

		txtNome = new JTextField();
		txtNome.setColumns(10);
		txtNome.setBounds(60, 87, 260, 30);
		contentPane.add(txtNome);

		JLabel lblNewLabel_1_1 = new JLabel("Nome:");
		lblNewLabel_1_1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_1_1.setBounds(60, 69, 49, 14);
		contentPane.add(lblNewLabel_1_1);

		JButton btnNewButton = new JButton("Voltar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							HomePage frame = new HomePage(echoSocket, login);
							frame.setVisible(true);
							frame.setLocationRelativeTo(null);
							dispose();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null, "Ocorreu um erro ao retornar a Home Page.", "Erro",
									JOptionPane.ERROR_MESSAGE);
							ex.printStackTrace();
						}
					}
				});
			}
		});

		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 14));
		btnNewButton.setBounds(60, 267, 80, 25);
		contentPane.add(btnNewButton);

		passwordField = new JPasswordField();
		passwordField.setBounds(60, 215, 260, 30);
		contentPane.add(passwordField);
	}

	private JsonObject editarCadastro(Socket echoSocket, JsonObject login) {
		try {
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			login.remove("codigo");
			login.addProperty("id_operacao", 2);
			login.addProperty("nome", txtNome.getText());
			login.addProperty("email", txtEmail.getText());
			String senha = CaesarCrypt.encrypt(new String(passwordField.getPassword()));
			login.addProperty("senha", senha);
			saida.println(login);
			System.out.println("ENVIADO: " + login);
			return receberResposta();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao se comunicar com o servidor.",
					"Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}

	private JsonObject receberResposta() {
		try {
			Gson gson = new Gson();
			JsonObject respostaServidor = gson.fromJson(entrada.readLine(), JsonObject.class);

			if (respostaServidor != null)
				System.out.println("\nRESPOSTA: " + respostaServidor);
			System.out.println("************************************************************************\n");
			return respostaServidor;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao receber a resposta do servidor.",
					"Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}

}
