package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import javax.swing.SwingUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import servidor.dao.BancoDados;
import servidor.dao.ClienteDao;
import servidor.dao.IncidenteDao;
import servidor.entidades.Cliente;
import servidor.entidades.Incidente;
import servidor.uteis.ValidarDados;
import servidor.uteis.ValidarJson;

public class Servidor extends Thread {
	private Socket clientSocket;
	Connection conexao = null;
	JsonArray listaClientes;
	PrintWriter out = null;
	BufferedReader in = null;
	String mensagem;
	static ServidorGUI servidor;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					servidor = new ServidorGUI();
					servidor.setVisible(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Servidor(Socket echoSocket) {
		clientSocket = echoSocket;
		start();
	}

	public void run() {
		System.out.println("Nova Thread de Comunicacao Iniciada.\n");

		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// String recebida do cliente
			String entrada_cliente;
			while ((entrada_cliente = in.readLine()) != null) {
				String retorno_cliente = "";
				JsonObject jsonObject = new JsonObject();
				Connection conexao = null;
				// Imprime o json
				System.out.println("ENTRADA : " + entrada_cliente);
				Gson gson = new Gson();
				JsonObject dados = gson.fromJson(entrada_cliente, JsonObject.class);

				if (dados != null && ValidarDados.validarIdOperacao(dados)) {
					int operacao = dados.get("id_operacao").getAsInt();
					switch (operacao) {
					// Resto do código do switch case
					case 1:
					case 2:
						jsonObject = ValidarJson.verificarCamposCadastro(dados);
						if (jsonObject.get("codigo").getAsInt() == 200) {
							Cliente cliente = new Cliente();
							cliente.setNome(dados.get("nome").getAsString());
							cliente.setEmail(dados.get("email").getAsString());
							cliente.setSenha(dados.get("senha").getAsString());
							cliente.setToken("");

							if (operacao == 2) {
								if (dados != null && dados.has("id_usuario") && !dados.get("id_usuario").isJsonNull()) {
									cliente.setId(Integer.parseInt(dados.get("id_usuario").getAsString()));
								} else {
									jsonObject.addProperty("codigo", 500);
								}
							}

							if (jsonObject.get("codigo").getAsInt() == 200)
								jsonObject = ValidarDados.validarDadosCadastro(cliente, conexao);

							if (jsonObject.get("codigo").getAsInt() == 200) {
								conexao = BancoDados.conectar();
								new ClienteDao(conexao).cadastrar(cliente);

								if (operacao == 2) {
									conexao = BancoDados.conectar();
									jsonObject = new ClienteDao(conexao).fazerLogin(cliente.getEmail(),
											cliente.getSenha());
									jsonObject.remove("id_usuario");
									// servidor.mostrarClientesLogados();
								}
							}

						}

						break;

					case 3:
						jsonObject = ValidarJson.verificarCamposLogin(dados);
						if (jsonObject.get("codigo").getAsInt() == 200) {
							conexao = BancoDados.conectar();
							jsonObject = new ClienteDao(conexao).fazerLogin(dados.get("email").getAsString(),
									dados.get("senha").getAsString());
							// servidor.mostrarClientesLogados();
						}

						break;

					case 4:
					case 10:
						jsonObject = ValidarJson.verificarCamposIncidente(dados);
						if (jsonObject.get("codigo").getAsInt() == 200) {
							Incidente incidente = new Incidente();
							jsonObject = ValidarDados.validarDadosIncidente(dados);
							if (jsonObject.get("codigo").getAsInt() == 200) {
								incidente = new Incidente(dados);
								if (operacao == 10)
									incidente.setId(Integer.parseInt(dados.get("id_incidente").getAsString()));
								conexao = BancoDados.conectar();
								jsonObject = new IncidenteDao(conexao).reportarIncidente(incidente);
							}

						}
						break;

					case 5:
						try {
							jsonObject = ValidarJson.verificarCamposListaIncidentes(dados);
							if (jsonObject.get("codigo").getAsInt() == 200) {
								jsonObject = ValidarDados.validarDadosListarIncidente(dados);
								if (jsonObject.get("codigo").getAsInt() == 200) {
									conexao = BancoDados.conectar();
									jsonObject = new IncidenteDao(conexao).filtrarIncidentes(dados);
								}
							}
						} catch (ParseException e) {
							System.err.println("Erro ao solicitar lista de incidentes.");
						}
						break;

					case 6:
						try {
							jsonObject = ValidarJson.verificarCamposLogout(dados);
							if (jsonObject.get("codigo").getAsInt() == 200) {
								conexao = BancoDados.conectar();
								jsonObject = new IncidenteDao(conexao).listarMeusIncidentes(dados);
							}
						} catch (ParseException e) {
							System.err.println("Erro ao ao exibir lista de incidentes do usuario.");
						}
						break;

					case 7:
						jsonObject = ValidarJson.verificarCamposRemoverIncidente(dados);
						if (jsonObject.get("codigo").getAsInt() == 200) {
							conexao = BancoDados.conectar();
							jsonObject = new IncidenteDao(conexao).excluirIncidente(dados);
						}
						break;

					case 8:
						jsonObject = ValidarJson.verificarCamposRemoverCadastro(dados);
						if (jsonObject.get("codigo").getAsInt() == 200) {
							conexao = BancoDados.conectar();
							jsonObject = new ClienteDao(conexao).excluirCliente(dados);
							// servidor.mostrarClientesLogados();
						}
						break;

					case 9:
						jsonObject = ValidarJson.verificarCamposLogout(dados);
						if (jsonObject.get("codigo").getAsInt() == 200) {
							jsonObject = ValidarDados.validarToken(dados, conexao);
							if (jsonObject.get("codigo").getAsInt() == 200) {
								conexao = BancoDados.conectar();
								jsonObject = new ClienteDao(conexao).fazerLogout(dados);
								// servidor.mostrarClientesLogados();
							}
						}
						break;
					}
				} else {
					System.err.println("Operacao nao suportada.");
					jsonObject.addProperty("codigo", 500);
					jsonObject.addProperty("mensagem", "Operacao nao suportada");
				}
				retornarCliente(jsonObject, retorno_cliente);
			}

			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Problema com o Servidor de Comunicacao");
		} catch (SQLException e) {
			System.err.println("Problema ao se conectar com o Banco de Dados.");
		} catch (JsonSyntaxException e) {
			System.err.println("O conteudo nao e um Json valido");
			JsonObject jsonObject = new JsonObject();
			String retorno_cliente = "";
			jsonObject.addProperty("codigo", 500);
			jsonObject.addProperty("mensagem", "O conteudo nao e um Json valido.");
			retornarCliente(jsonObject, retorno_cliente);
		} catch (NumberFormatException e) {
	    System.err.println("Valor invalido para id_operacao.");
	    JsonObject jsonObject = new JsonObject();
		String retorno_cliente = "";
	    jsonObject.addProperty("codigo", 500);
	    jsonObject.addProperty("mensagem", "Valor invalido para id_operacao");
	    retornarCliente(jsonObject, retorno_cliente);
	}
		
	}

	public void retornarCliente(JsonObject jsonObject, String retorno_cliente) {
		try {
			retorno_cliente = new Gson().toJson(jsonObject);
			out.println(retorno_cliente);
			System.out.println("RETORNO : " + retorno_cliente);
			System.out
					.println("==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#\n");
		} catch (Exception e) {
			System.err.println("Erro receber resposta do servidor.");
		}
	}

}
