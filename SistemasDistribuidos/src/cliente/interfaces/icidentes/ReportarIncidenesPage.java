package cliente.interfaces.icidentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cliente.interfaces.HomePage;
import servidor.uteis.ValidarJson;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportarIncidenesPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtRodovia;
	private JTextField txtKm;
	private JComboBox<String> comboBoxTipo;
	// Variáveis
	static PrintWriter saida = null;
	static BufferedReader entrada = null;

	MaskFormatter mfdata;

	public ReportarIncidenesPage(Socket echoSocket, JsonObject login) {
		// Pega a foramtação na interface
//        try {
//            mfdata = new MaskFormatter("####-##-## ##:##:##");
//        } catch (ParseException e2) {
//            e2.printStackTrace();
//        }
		setTitle("Reportar incidentes");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 264);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblReportarIncidente = new JLabel("REPORTAR INCIDENTE");
		lblReportarIncidente.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblReportarIncidente.setBounds(32, 11, 250, 30);
		contentPane.add(lblReportarIncidente);

		txtRodovia = new JTextField();
		txtRodovia.setBounds(33, 72, 100, 25);
		contentPane.add(txtRodovia);
		txtRodovia.setColumns(10);

		JLabel lblKm = new JLabel("Km:");
		lblKm.setFont(new Font("Arial", Font.PLAIN, 14));
		lblKm.setBounds(169, 52, 49, 16);
		contentPane.add(lblKm);

		JLabel lblRodovia = new JLabel("Rodovia:");
		lblRodovia.setFont(new Font("Arial", Font.PLAIN, 14));
		lblRodovia.setBounds(33, 52, 60, 16);
		contentPane.add(lblRodovia);

		txtKm = new JTextField();
		txtKm.setColumns(10);
		txtKm.setBounds(169, 72, 100, 25);
		contentPane.add(txtKm);

		JLabel lblTipo = new JLabel("Tipo:");
		lblTipo.setFont(new Font("Arial", Font.PLAIN, 14));
		lblTipo.setBounds(32, 125, 40, 16);
		contentPane.add(lblTipo);

		JButton btnReportar = new JButton("Reportar");
		btnReportar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					reportarIncidente(echoSocket, login);
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
				} catch (JsonSyntaxException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnReportar.setFont(new Font("Arial", Font.BOLD, 16));
		btnReportar.setBounds(169, 173, 100, 25);
		contentPane.add(btnReportar);
		
		comboBoxTipo = new JComboBox<String>();
		comboBoxTipo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBoxTipo.addItem("Vento");
		comboBoxTipo.addItem("Chuva");
		comboBoxTipo.addItem("Nevoeiro");
		comboBoxTipo.addItem("Neve");
		comboBoxTipo.addItem("Gelo na pista");
		comboBoxTipo.addItem("Granizo");
		comboBoxTipo.addItem("Transito parado");
		comboBoxTipo.addItem("Filas de transito");
		comboBoxTipo.addItem("Transito lento");
		comboBoxTipo.addItem("Acidente desconhecido");
		comboBoxTipo.addItem("Incidente desconhecido");
		comboBoxTipo.addItem("Trabalhos na estrada");
		comboBoxTipo.addItem("Bloqueio de pista");
		comboBoxTipo.addItem("Bloqueio de estrada");
		comboBoxTipo.setBounds(79, 119, 190, 25);
		contentPane.add(comboBoxTipo);

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
		btnVoltar.setBounds(32, 174, 100, 25);
		contentPane.add(btnVoltar);
	}

	public void reportarIncidente(Socket echoSocket, JsonObject login) throws JsonSyntaxException, IOException {
		try {
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id_operacao", 4);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String dataAtual = formatter.format(LocalDateTime.now());
			jsonObject.addProperty("data", dataAtual);
			jsonObject.addProperty("rodovia", txtRodovia.getText());
			jsonObject.addProperty("km", txtKm.getText());
			jsonObject.addProperty("tipo_incidente", comboBoxTipo.getSelectedIndex() + 1);
			jsonObject.addProperty("token", login.get("token").getAsString());
			jsonObject.addProperty("id_usuario", login.get("id_usuario").getAsString());
			saida.println(jsonObject);
			System.out.println("ENVIADO: " + jsonObject);

			Gson gson = new Gson();
			JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
			if (resposta_servidor != null)
				System.out.println("\nRESPOSTA: " + resposta_servidor);
			System.out.println("************************************************************************\n");

			if (ValidarJson.verificarCodigo(resposta_servidor)) {
				JOptionPane.showMessageDialog(null, "Incidente Reportado!");

			} else if (ValidarJson.verificarMensagem(resposta_servidor)) {
				JOptionPane.showMessageDialog(null, resposta_servidor.get("mensagem").getAsString(),
						resposta_servidor.get("codigo").getAsString(), JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao se comunicar com o servidor.",
					"Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

}
