package cliente.interfaces.incidentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import interfaces.HomePage;
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
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EditarIncidente extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JFormattedTextField txtData;
	private JTextField txtRodovia;
	private JTextField txtKm;
	private JComboBox<String> comboBoxTipo;

	// Variáveis
	static PrintWriter saida = null;
	static BufferedReader entrada = null;

	MaskFormatter mfdata;

	public EditarIncidente(Socket echoSocket, JsonObject login) {
		// Pega a foramtação na interface
		try {
			mfdata = new MaskFormatter("####-##-## ##:##:##");

		} catch (ParseException e2) {
			System.out.println("Erro ao criar a mascara da Data.");
		}

		setTitle("Editar incidentes");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 320, 320);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblReportarIncidente = new JLabel("EDITAR INCIDENTE");
		lblReportarIncidente.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblReportarIncidente.setBounds(45, 11, 230, 30);
		contentPane.add(lblReportarIncidente);

		txtRodovia = new JTextField();
		txtRodovia.setBounds(33, 130, 100, 25);
		contentPane.add(txtRodovia);
		txtRodovia.setColumns(10);

		JLabel lblKm = new JLabel("Km:");
		lblKm.setFont(new Font("Arial", Font.PLAIN, 14));
		lblKm.setBounds(169, 110, 49, 16);
		contentPane.add(lblKm);

		JLabel lblRodovia = new JLabel("Rodovia:");
		lblRodovia.setFont(new Font("Arial", Font.PLAIN, 14));
		lblRodovia.setBounds(33, 110, 60, 16);
		contentPane.add(lblRodovia);

		txtKm = new JTextField();
		txtKm.setColumns(10);
		txtKm.setBounds(169, 130, 100, 25);
		contentPane.add(txtKm);

		JLabel lblTipo = new JLabel("Tipo:");
		lblTipo.setFont(new Font("Arial", Font.PLAIN, 14));
		lblTipo.setBounds(32, 183, 40, 16);
		contentPane.add(lblTipo);

		JButton btnReportar = new JButton("Editar");
		btnReportar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					atualizarIncidente(echoSocket, login);
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								dispose();
							} catch (Exception e) {
								JOptionPane.showMessageDialog(null, "Erro ao fechar o Frame atual.", "Erro",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					});
				} catch (JsonSyntaxException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnReportar.setFont(new Font("Arial", Font.BOLD, 16));
		btnReportar.setBounds(169, 231, 100, 25);
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
		comboBoxTipo.setBounds(79, 177, 190, 25);
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
							JOptionPane.showMessageDialog(null, "Erro ao iniciar a Home Page.", "Erro",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
		});
		btnVoltar.setFont(new Font("Arial", Font.PLAIN, 14));
		btnVoltar.setBounds(32, 232, 100, 25);
		contentPane.add(btnVoltar);

		txtData = new JFormattedTextField(mfdata);
		txtData.setColumns(10);
		txtData.setBounds(79, 67, 190, 25);
		contentPane.add(txtData);

		JLabel lblData = new JLabel("Data:");
		lblData.setFont(new Font("Arial", Font.PLAIN, 14));
		lblData.setBounds(32, 70, 40, 16);
		contentPane.add(lblData);
	}

	public void atualizarIncidente(Socket echoSocket, JsonObject login) throws JsonSyntaxException, IOException {
		try {
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id_operacao", 10);
			String data = txtData.getValue() != null ? txtData.getValue().toString() : "";
			if (data.isEmpty()) {
				data = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
			}

			jsonObject.addProperty("data", data);
			jsonObject.addProperty("token", login.get("token").getAsString());
			jsonObject.addProperty("id_incidente", login.get("id_incidente").getAsString());
			jsonObject.addProperty("id_usuario", login.get("id_usuario").getAsString());
			jsonObject.addProperty("rodovia", txtRodovia.getText());
			jsonObject.addProperty("km", Integer.parseInt(txtKm.getText()));
			jsonObject.addProperty("tipo_incidente", comboBoxTipo.getSelectedIndex() + 1);
			saida.println(jsonObject);
			System.out.println("ENVIADO: " + jsonObject);
			Gson gson = new Gson();
			JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
			if (resposta_servidor != null)
				System.out.println("\nRESPOSTA: " + resposta_servidor);
			System.out.println("************************************************************************\n");

			if (ValidarJson.verificarCodigo(resposta_servidor)) {
				JOptionPane.showMessageDialog(null, "Incidente atualizado!");

			} else {
				if (ValidarJson.verificarMensagem(resposta_servidor) && resposta_servidor.has("codigo"))
					JOptionPane.showMessageDialog(null, resposta_servidor.get("mensagem").getAsString(),
							resposta_servidor.get("codigo").getAsString(), JOptionPane.ERROR_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "Json não possui o campo mensagem ou código.", "500",
							JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao se comunicar com o servidor.",
					"Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
		}

	}
}
