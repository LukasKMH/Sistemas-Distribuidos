package cliente.UI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import javax.swing.JButton;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HomePage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	// Variáveis
	static PrintWriter saida = null;
	static BufferedReader entrada = null;

	public HomePage(Socket echoSocket) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 269, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnLogout.setFont(new Font("Arial", Font.PLAIN, 14));
		btnLogout.setBounds(49, 191, 150, 30);
		contentPane.add(btnLogout);

		JButton btnReportarIncidentes = new JButton("Reportar incidente");
		btnReportarIncidentes.setFont(new Font("Arial", Font.PLAIN, 14));
		btnReportarIncidentes.setBounds(49, 68, 150, 30);
		contentPane.add(btnReportarIncidentes);

		JButton btnListarIncidentes = new JButton("Listar incidentes");
		btnListarIncidentes.setFont(new Font("Arial", Font.PLAIN, 14));
		btnListarIncidentes.setBounds(49, 109, 150, 30);
		contentPane.add(btnListarIncidentes);

		JButton btnAtualizarCadastro = new JButton("Atualizar Cadastro");
		btnAtualizarCadastro.setFont(new Font("Arial", Font.PLAIN, 14));
		btnAtualizarCadastro.setBounds(49, 150, 150, 30);
		contentPane.add(btnAtualizarCadastro);

		JLabel lblHomePage = new JLabel("HOME PAGE");
		lblHomePage.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblHomePage.setBounds(60, 11, 130, 30);
		contentPane.add(lblHomePage);
	}
	
	private boolean realizarLogin(Socket echoSocket) throws JsonSyntaxException, IOException {
		try {
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id_operacao", 9);
		jsonObject.addProperty("token", "");
		jsonObject.addProperty("id_usuario", "");
		saida.println(jsonObject);
		System.out.println("ENVIADO: " + jsonObject);

		Gson gson = new Gson();
		JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
		if (resposta_servidor != null)
			System.out.println("\nRESPOSTA: " + resposta_servidor);
		System.out.println("************************************************************************\n");

		// Mensagem
		if (Integer.parseInt(resposta_servidor.get("codigo").getAsString()) == 200) {
			JOptionPane.showMessageDialog(null, "Logout realizado!");
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Não foi possivel realizar o logout.", "Erro", JOptionPane.ERROR_MESSAGE);
		}
		return false;

	}
}