package cliente.UI;

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

public class EditarCadastroPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNome;
	private JTextField txtEmail;
	private JTextField txtSenha;
	private JButton btnLogar;

	// Variáveis

	private static PrintWriter saida = null;
	private static BufferedReader entrada = null;

	public EditarCadastroPage(Socket echoSocket) {
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

		txtSenha = new JTextField();
		txtSenha.setBounds(60, 215, 260, 30);
		contentPane.add(txtSenha);
		txtSenha.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Email:");
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(60, 133, 49, 14);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Senha:");
		lblNewLabel_2.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(60, 197, 49, 14);
		contentPane.add(lblNewLabel_2);

		btnLogar = new JButton("Atualizar Dados");
		btnLogar.setFont(new Font("Arial", Font.PLAIN, 14));
		btnLogar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editarCadastro(echoSocket);
			}
		});
		btnLogar.setBounds(180, 267, 140, 25);
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
		});
		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 14));
		btnNewButton.setBounds(60, 267, 80, 25);
		contentPane.add(btnNewButton);
	}

	private boolean editarCadastro(Socket echoSocket) {
		try {
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id_operacao", 2);
		jsonObject.addProperty("nome", txtNome.getText());
		jsonObject.addProperty("email", txtEmail.getText());
		// Usando criptografia
//      String senha = CaesarCrypt.encrypt(txtSenha.getText());
//		jsonObject.addProperty("senha", senha);
		jsonObject.addProperty("senha", txtSenha.getText());
		saida.println(jsonObject);
		System.out.println("ENVIADO: " + jsonObject);
		return receberResposta();

	}

	private boolean receberResposta() {
		try {
			Gson gson = new Gson();
			JsonObject respostaServidor = gson.fromJson(entrada.readLine(), JsonObject.class);

			if (respostaServidor != null) {
				System.out.println("\nRESPOSTA: " + respostaServidor);
				// Faça o tratamento adequado da resposta do servidor aqui
			}
			System.out.println("************************************************************************\n");

			// Mensagem
			if (respostaServidor.has("codigo") && respostaServidor.get("codigo").getAsInt() == 200) {
				JOptionPane.showMessageDialog(null, "Login realizado!");
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "Dados incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
