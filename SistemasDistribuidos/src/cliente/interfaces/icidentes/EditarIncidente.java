package cliente.interfaces.icidentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cliente.interfaces.HomePage;

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

public class EditarIncidente extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtRodovia;
    private JTextField txtKm;
    private JTextField txtTipo;

    // Variáveis
    static PrintWriter saida = null;
    static BufferedReader entrada = null;

    MaskFormatter mfdata;

    public EditarIncidente(Socket echoSocket, JsonObject login) {
    			//Pega a foramtação na interface
//        try {
//            mfdata = new MaskFormatter("####-##-## ##:##:##");
//        } catch (ParseException e2) {
//            e2.printStackTrace();
//        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 320, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblReportarIncidente = new JLabel("EDITAR INCIDENTE");
        lblReportarIncidente.setFont(new Font("Tahoma", Font.PLAIN, 24));
        lblReportarIncidente.setBounds(42, 11, 250, 30);
        contentPane.add(lblReportarIncidente);

        txtRodovia = new JTextField();
        txtRodovia.setBounds(33, 72, 230, 25);
        contentPane.add(txtRodovia);
        txtRodovia.setColumns(10);

        JLabel lblNewLabel_1 = new JLabel("Km:");
        lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNewLabel_1.setBounds(33, 107, 49, 16);
        contentPane.add(lblNewLabel_1);

        JLabel lblRodovia = new JLabel("Rodovia:");
        lblRodovia.setFont(new Font("Arial", Font.PLAIN, 14));
        lblRodovia.setBounds(33, 52, 60, 16);
        contentPane.add(lblRodovia);

        txtKm = new JTextField();
        txtKm.setColumns(10);
        txtKm.setBounds(33, 127, 230, 25);
        contentPane.add(txtKm);

        JLabel lblNewLabel_1_2 = new JLabel("Tipo do incidene:");
        lblNewLabel_1_2.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNewLabel_1_2.setBounds(33, 162, 120, 16);
        contentPane.add(lblNewLabel_1_2);

        JButton btnReportar = new JButton("Editar");
        btnReportar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    atualizarIncidente(echoSocket, login);
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            try {
//                                HomePage frame = new HomePage(echoSocket, login);
//                                frame.setVisible(true);
//                                frame.setLocationRelativeTo(null);
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
        btnReportar.setBounds(163, 225, 100, 25);
        contentPane.add(btnReportar);

        txtTipo = new JTextField();
        txtTipo.setColumns(10);
        txtTipo.setBounds(33, 182, 230, 25);
        contentPane.add(txtTipo);
        
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
        btnVoltar.setBounds(33, 225, 100, 25);
        contentPane.add(btnVoltar);
    }

    public void atualizarIncidente(Socket echoSocket, JsonObject login) throws JsonSyntaxException, IOException {
        try {
            saida = new PrintWriter(echoSocket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id_operacao", 10);
        jsonObject.addProperty("token", login.get("token").getAsString());
        jsonObject.addProperty("id_incidente", login.get("id_incidente").getAsString());
        jsonObject.addProperty("id_usuario", login.get("id_usuario").getAsString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dataAtual = formatter.format(LocalDateTime.now());
        jsonObject.addProperty("data", dataAtual);
        jsonObject.addProperty("rodovia", txtRodovia.getText());
        jsonObject.addProperty("km", txtKm.getText());
        jsonObject.addProperty("tipo_incidente", txtTipo.getText());
        saida.println(jsonObject);
        System.out.println("ENVIADO: " + jsonObject);

        Gson gson = new Gson();
        JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
        if (resposta_servidor != null)
            System.out.println("\nRESPOSTA: " + resposta_servidor);
        System.out.println("************************************************************************\n");

        // Mensagem
        if (Integer.parseInt(resposta_servidor.get("codigo").getAsString()) == 200) {
            JOptionPane.showMessageDialog(null, "Incidente atualizado!");

        } else {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar incidente.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
