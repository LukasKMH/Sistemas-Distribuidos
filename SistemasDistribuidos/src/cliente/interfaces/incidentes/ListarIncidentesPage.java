package cliente.interfaces.incidentes;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import interfaces.HomePage;
import servidor.uteis.ValidarJson;

import javax.swing.JComboBox;
import java.awt.Font;

public class ListarIncidentesPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFaixaKmInicial;
	private JTextField txtFaixaKmFinal;
	private JFormattedTextField txtData;
	private JComboBox<String> comboBoxPeriodo;

	// Variáveis
	private static PrintWriter saida = null;
	private static BufferedReader entrada = null;
	private JTextField txtRodovia;

	public ListarIncidentesPage(Socket echoSocket, JsonObject login) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 245, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblData = new JLabel("Data:");
		lblData.setFont(new Font("Arial", Font.PLAIN, 14));
		lblData.setBounds(18, 50, 50, 14);
		contentPane.add(lblData);

		try {
			MaskFormatter mfData = new MaskFormatter("####-##-##");
			txtData = new JFormattedTextField(mfData);
			txtData.setBounds(18, 69, 80, 25);
			contentPane.add(txtData);
		} catch (Exception e) {
			System.out.println("Erro ao criar a mascara da Data.");
		}

		JLabel lblFaixaKm = new JLabel("Faixa de Km:");
		lblFaixaKm.setFont(new Font("Arial", Font.PLAIN, 14));
		lblFaixaKm.setBounds(18, 119, 85, 14);
		contentPane.add(lblFaixaKm);

		txtFaixaKmInicial = new JTextField();
		txtFaixaKmInicial.setBounds(106, 116, 40, 25);
		contentPane.add(txtFaixaKmInicial);
		txtFaixaKmInicial.setColumns(10);

		JLabel lblFaixaKmTraco = new JLabel("-");
		lblFaixaKmTraco.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblFaixaKmTraco.setBounds(158, 119, 20, 14);
		contentPane.add(lblFaixaKmTraco);

		txtFaixaKmFinal = new JTextField();
		txtFaixaKmFinal.setBounds(176, 116, 40, 25);
		contentPane.add(txtFaixaKmFinal);
		txtFaixaKmFinal.setColumns(10);

		JLabel lblPeriodo = new JLabel("Período:");
		lblPeriodo.setFont(new Font("Arial", Font.PLAIN, 14));
		lblPeriodo.setBounds(18, 167, 60, 14);
		contentPane.add(lblPeriodo);

		comboBoxPeriodo = new JComboBox<String>();
		comboBoxPeriodo.addItem("Manhã");
		comboBoxPeriodo.addItem("Tarde");
		comboBoxPeriodo.addItem("Noite");
		comboBoxPeriodo.addItem("Madrugada");
		comboBoxPeriodo.setBounds(78, 163, 140, 25);
		contentPane.add(comboBoxPeriodo);

		JButton btnReportar = new JButton("Listar");
		btnReportar.setFont(new Font("Arial", Font.BOLD, 16));
		btnReportar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JsonObject respostaServidor = reportarIncidente(echoSocket);
				if (respostaServidor != null && ValidarJson.verificarCodigo(respostaServidor)) {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								JsonArray lista_incidentes = respostaServidor.getAsJsonArray("lista_incidentes");
								HomePage frame2 = new HomePage(echoSocket, login);
								frame2.setVisible(true);
								frame2.setLocationRelativeTo(null);
								TabelaIncidentes frame = new TabelaIncidentes(lista_incidentes);
								frame.setVisible(true);
								dispose();
							} catch (Exception e) {
								JOptionPane.showMessageDialog(null, "Erro ao mudar os Frames.", "Erro",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					});
				}

			}
		});
		btnReportar.setBounds(131, 213, 90, 25);
		contentPane.add(btnReportar);

		JLabel lblNewLabel = new JLabel("Rodovia:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		lblNewLabel.setBounds(149, 50, 60, 14);
		contentPane.add(lblNewLabel);

		txtRodovia = new JTextField();
		txtRodovia.setBounds(149, 69, 70, 25);
		contentPane.add(txtRodovia);
		txtRodovia.setColumns(10);

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
							JOptionPane.showMessageDialog(null, "Erro ao iniciar a Home Page.", "Erro",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
		});
		btnVoltar.setFont(new Font("Arial", Font.PLAIN, 14));
		btnVoltar.setBounds(18, 213, 80, 25);
		contentPane.add(btnVoltar);

		JLabel lblNewLabel_1 = new JLabel("LISTAR INCIDENTES");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setBounds(49, 9, 150, 30);
		contentPane.add(lblNewLabel_1);
	}

	private JsonObject reportarIncidente(Socket echoSocket) {

		try {
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id_operacao", 5);
			jsonObject.addProperty("rodovia", txtRodovia.getText());
			jsonObject.addProperty("data", txtData.getText() + " 00:00:00");
			String faixa_km;
			if (txtFaixaKmInicial.getText().isEmpty() || txtFaixaKmFinal.getText().isEmpty())
				faixa_km = "";
			else
				faixa_km = txtFaixaKmInicial.getText() + "-" + txtFaixaKmFinal.getText();
			jsonObject.addProperty("faixa_km", faixa_km);
			jsonObject.addProperty("periodo", comboBoxPeriodo.getSelectedIndex() + 1);

			saida.println(jsonObject);
			System.out.println("ENVIADO: " + jsonObject);
			return receberResposta(echoSocket);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao se comunicar com o servidor.",
					"Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	private JsonObject receberResposta(Socket echoSocket) {
		try {
			Gson gson = new Gson();
			JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);

			if (resposta_servidor != null) 
				System.out.println("\nRESPOSTA: " + resposta_servidor);
			System.out.println("************************************************************************\n");

			if (ValidarJson.verificarCodigo(resposta_servidor)) {
				return resposta_servidor;
			} else {
				if (ValidarJson.verificarMensagem(resposta_servidor) && resposta_servidor.has("codigo"))
					JOptionPane.showMessageDialog(null, resposta_servidor.get("mensagem").getAsString(),
							resposta_servidor.get("codigo").getAsString(), JOptionPane.ERROR_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "Json não possui o campo mensagem ou código.", "500",
							JOptionPane.ERROR_MESSAGE);
				return resposta_servidor;
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao se comunicar com o servidor.",
					"Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}
}
