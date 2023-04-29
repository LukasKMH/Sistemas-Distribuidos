package cliente;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;
import javax.swing.JTextField;

import com.google.gson.Gson;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class ClienteGUI {

	private static String serverHostname = new String("127.0.0.1");
	private static PrintWriter out;
	private static Socket echoSocket;
	private static BufferedReader resposta;
	private static JFrame frame;
	private static JTextField txtNome;
	private static JTextField txtEmail;
	private static JTextField txtSenha;

	private static void iniciarCLiente() {

		System.out.println("Attemping to connect to host " + serverHostname + " on port 10008.");
		try {
			echoSocket = new Socket(serverHostname, 10008);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			resposta = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			System.out.println("Conectado.");
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + serverHostname);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
			System.exit(1);
		}
	}

	public static void cadastrar() {

		Login cliente1 = new Login();
		Gson gson = new Gson();
		cliente1.setId_Operacao(1);
		cliente1.setNome(txtNome.getText());
		cliente1.setEmail(txtEmail.getText());
		cliente1.setSenha(txtSenha.getText());
		String gson1 = gson.toJson(cliente1);
		out.println(gson1);

		// System.out.println(gson1);
		// enviarMensagem(gson1);
	}

	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClienteGUI window = new ClienteGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		if (args.length > 0)
			serverHostname = args[0];
		iniciarCLiente();

		// PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
		// BufferedReader in = new BufferedReader(new
		// InputStreamReader(echoSocket.getInputStream()));
		// BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		Boolean ligado = true;
		int operacao = 0;
		Scanner entrada = new Scanner(System.in);
		Login cliente1 = new Login();
		Gson gson = new Gson();

		while (ligado) {
			System.out.println("1 - Cadastrar: \n2- Atualizar Cadastro: \n3 - Login: \n0 - Sair:\n");
//			operacao = entrada.nextInt();
//			switch (operacao) {
//			case 1: {
//
//				System.out.println("====== Cadastrar ======");
//				cliente1.setId_Operacao(operacao);
//				cliente1.setNome(txtNome.getText());
//				cliente1.setEmail(txtEmail.getText());
//				cliente1.setSenha(txtSenha.getText());
//				String gson1 = gson.toJson(cliente1);
//				FileWriter writer = new FileWriter("dados.json");
//				writer.write(gson1);
//				writer.close();
//				out.println(gson1);
//				break;
//			}
//
//			case 0: {
//				ligado = false;
//				System.out.println("Saiu do servidor.");
//				break;
//			}
//
//			}

			System.out.println("RESPOSTSA!!!");
			System.out.println("Respota servidor: " + resposta.readLine());

			if (!ligado) {
				break;
			}

		}

//		out.close();
//		in.close();
//		stdIn.close();
		echoSocket.close();

	}

	/**
	 * Create the application.
	 */
	public ClienteGUI() {
		initialize();

	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 449, 326);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Nome:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel.setBounds(93, 28, 49, 14);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Email:");
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(93, 92, 49, 14);
		frame.getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Senha:");
		lblNewLabel_2.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(93, 156, 49, 14);
		frame.getContentPane().add(lblNewLabel_2);

		txtNome = new JTextField();
		txtNome.setColumns(10);
		txtNome.setBounds(93, 46, 266, 30);
		frame.getContentPane().add(txtNome);

		txtEmail = new JTextField();
		txtEmail.setColumns(10);
		txtEmail.setBounds(93, 110, 266, 30);
		frame.getContentPane().add(txtEmail);

		txtSenha = new JTextField();
		txtSenha.setColumns(10);
		txtSenha.setBounds(93, 174, 266, 30);
		frame.getContentPane().add(txtSenha);

		JButton btnCadastrar = new JButton("Cadastrar");
		btnCadastrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cadastrar();
			}
		});

		btnCadastrar.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 14));
		btnCadastrar.setBounds(175, 226, 110, 23);
		frame.getContentPane().add(btnCadastrar);

	}
}
