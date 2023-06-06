package cliente.interfaces;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cliente.interfaces.icidentes.ListarIncidentesPage;
import cliente.interfaces.icidentes.ReportarIncidenesPage;
import cliente.interfaces.icidentes.TabelaMeusIncidentes;
import servidor.uteis.ValidarJson;

import javax.swing.JButton;

import java.awt.EventQueue;
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

	public HomePage(Socket echoSocket, JsonObject login) {
		setTitle("Home");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 414, 246);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		            JsonObject resposta_servidor = realizarLogout(echoSocket, login, 9);
		            if (resposta_servidor != null) {
		                EventQueue.invokeLater(new Runnable() {
		                    public void run() {
		                        try {
		                            LoginPage frame = new LoginPage(echoSocket);
		                            frame.setVisible(true);
		                            frame.setLocationRelativeTo(null);
		                            dispose();
		                        } catch (Exception ex) {
		                            JOptionPane.showMessageDialog(null, "Ocorreu um erro ao retornar para pagina de Login.",
		                                    "Erro", JOptionPane.ERROR_MESSAGE);
		                            ex.printStackTrace();
		                        }
		                    }
		                });
		            }

		        } catch (JsonSyntaxException e1) {
		            JOptionPane.showMessageDialog(null, "Ocorreu um erro ao realizar o logout.",
		                    "Erro", JOptionPane.ERROR_MESSAGE);
		            e1.printStackTrace();
		        }
		    }
		});

		btnLogout.setFont(new Font("Arial", Font.PLAIN, 14));
		btnLogout.setBounds(219, 150, 150, 30);
		contentPane.add(btnLogout);

		JButton btnReportarIncidentes = new JButton("Reportar incidente");
		btnReportarIncidentes.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ReportarIncidenesPage frame = new ReportarIncidenesPage(echoSocket, login);
							frame.setVisible(true);
							frame.setLocationRelativeTo(null);
							dispose();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Ocorreu um erro ao abrir a pagina de reportar incidentes.",
		                            "Erro", JOptionPane.ERROR_MESSAGE);
		                    e.printStackTrace();
						}
					}
				});
			}
		});
		btnReportarIncidentes.setFont(new Font("Arial", Font.PLAIN, 14));
		btnReportarIncidentes.setBounds(30, 68, 150, 30);
		contentPane.add(btnReportarIncidentes);

		JButton btnListarIncidentes = new JButton("Listar incidentes");
		btnListarIncidentes.setFont(new Font("Arial", Font.PLAIN, 14));
		btnListarIncidentes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ListarIncidentesPage frame = new ListarIncidentesPage(echoSocket, login);
							frame.setVisible(true);
							frame.setLocationRelativeTo(null);
							dispose();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null,
									"Ocorreu um erro ao abrir a pagina de listar incidentes.", "Erro",
									JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		btnListarIncidentes.setBounds(30, 109, 150, 30);
		contentPane.add(btnListarIncidentes);

		JButton btnAtualizarCadastro = new JButton("Atualizar Cadastro");
		btnAtualizarCadastro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							EditarCadastroPage frame = new EditarCadastroPage(echoSocket, login);
							frame.setVisible(true);
							frame.setLocationRelativeTo(null);
							dispose();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null,
									"Ocorreu um erro ao abrir a pagina de atualizar de cadastro.", "Erro",
									JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnAtualizarCadastro.setFont(new Font("Arial", Font.PLAIN, 14));
		btnAtualizarCadastro.setBounds(219, 68, 150, 30);
		contentPane.add(btnAtualizarCadastro);

		JLabel lblHomePage = new JLabel("HOME PAGE");
		lblHomePage.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblHomePage.setBounds(140, 11, 130, 30);
		contentPane.add(lblHomePage);

		JButton btnMeusIncidentes = new JButton("Meus Incidentes");
		btnMeusIncidentes.setFont(new Font("Arial", Font.PLAIN, 14));
		btnMeusIncidentes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JsonObject resposta_servidor = realizarLogout(echoSocket, login, 6);
					if (resposta_servidor != null) {
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {
									JsonArray lista_incidentes = resposta_servidor.getAsJsonArray("lista_incidentes");
									TabelaMeusIncidentes frame = new TabelaMeusIncidentes(echoSocket, lista_incidentes,
											login);
									frame.setVisible(true);
									// dispose();
								} catch (Exception e) {
									JOptionPane.showMessageDialog(null,
											"Ocorreu um erro ao abrir a tabela de meus incidentes.", "Erro",
											JOptionPane.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						});
					}
				} catch (JsonSyntaxException e1) {
					JOptionPane.showMessageDialog(null, "Ocorreu um erro ao exibir os incidentes.", "Erro",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
	
		btnMeusIncidentes.setBounds(30, 150, 150, 30);
		contentPane.add(btnMeusIncidentes);

		JButton btnExcluirCadastro = new JButton("Excluir Cadastro");
		btnExcluirCadastro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ExcluirCadastroPage frame = new ExcluirCadastroPage(echoSocket, login);
							frame.setVisible(true);
							frame.setLocationRelativeTo(null);
							dispose();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null,
									"Ocorreu um erro ao abrir a pagina de exclusao de cadastro.", "Erro",
									JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnExcluirCadastro.setFont(new Font("Arial", Font.PLAIN, 14));
		btnExcluirCadastro.setBounds(219, 109, 150, 30);
		contentPane.add(btnExcluirCadastro);
	}

	private JsonObject realizarLogout(Socket echoSocket, JsonObject login, int operacao) {
		try {
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id_operacao", operacao);
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
				if (operacao == 9)
			    JOptionPane.showMessageDialog(null, "Logout realizado!");
			    return resposta_servidor;
			} else if (ValidarJson.verificarMensagem(resposta_servidor)) {
				if (operacao == 9)
				JOptionPane.showMessageDialog(null, resposta_servidor.get("mensagem").getAsString(), "Erro",
			            JOptionPane.ERROR_MESSAGE);
			    return resposta_servidor;
			}} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao se comunicar com o servidor.",
					"Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		return null;

	}

}
