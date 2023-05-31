package servidor;

import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import servidor.dao.BancoDados;
import servidor.dao.ClienteDao;
import servidor.dao.IncidenteDao;
import servidor.entidades.Cliente;
import servidor.entidades.Incidente;

import java.io.*;

public class EchoServidor extends Thread {
	protected Socket clientSocket;
	PrintWriter out = null;
	BufferedReader in = null;

	public static void main(String[] args) throws IOException {
		ServerSocket servidor = null;
		int porta = 24001;
		try {

			servidor = new ServerSocket(porta);

			System.out.println("Connection Socket Created");
			try {
				while (true) {
					System.out.println("Esperando pela conexao.");
					new EchoServidor(servidor.accept());
				}
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + porta + ".");
			System.exit(1);
		} finally {
			try {
				servidor.close();
			} catch (IOException e) {
				System.err.println("Could not close port: 10008.");
				System.exit(1);
			}
		}
	}

	public EchoServidor(Socket clientSoc) {
		clientSocket = clientSoc;
		start();
	}

	public void run() {
		System.out.println("New Communication Thread Started.\n");

		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// String recebida do cliente
			String entrada_cliente;
			Boolean logado = false;

			while ((entrada_cliente = in.readLine()) != null) {
				String retorno_cliente = "";
				Cliente cliente = new Cliente();
				Incidente incidente = new Incidente();
				JsonObject jsonObject = new JsonObject();
				Connection conexao = null;
				// Imprime o json
				System.out.println("ENTRADA : " + entrada_cliente);
				Gson gson = new Gson();
				JsonObject dados = gson.fromJson(entrada_cliente, JsonObject.class);

				if (dados.has("id_operacao") && !dados.get("id_operacao").equals(JsonNull.INSTANCE)) {
					int operacao = dados.get("id_operacao").getAsInt();
					switch (operacao) {
					// Resto do código do switch case
					case 1:
					case 2:
						cliente.setNome(dados.get("nome").getAsString());
						cliente.setEmail(dados.get("email").getAsString());
						cliente.setSenha(dados.get("senha").getAsString());
						cliente.setToken("");
						if (operacao == 2)
							cliente.setId(Integer.parseInt(dados.get("id_usuario").getAsString()));

						jsonObject = ValidarDados.validarDadosCadastro(cliente, conexao);

						if (jsonObject.get("codigo").getAsInt() == 200) {
							// Conecta com o banco de dados
							conexao = BancoDados.conectar();
							new ClienteDao(conexao).cadastrar(cliente);

							if (operacao == 2) {
								conexao = BancoDados.conectar();
								jsonObject = new ClienteDao(conexao).fazerLogin(cliente.getEmail(), cliente.getSenha());
							}
						}
						break;

					case 3:
						conexao = BancoDados.conectar();
						jsonObject = new ClienteDao(conexao).fazerLogin(dados.get("email").getAsString(),
								dados.get("senha").getAsString());
						if (jsonObject.get("codigo").getAsInt() == 200)
							logado = true;
						break;

					case 4:
					case 10:
						jsonObject = ValidarDados.validarDadosIncidente(dados);
						if (jsonObject.get("codigo").getAsInt() == 200) {
							incidente = new Incidente(dados);
							if (operacao == 10)
								incidente.setId(Integer.parseInt(dados.get("id_incidente").getAsString()));
							conexao = BancoDados.conectar();
							jsonObject = new IncidenteDao(conexao).reportarIncidente(incidente);
						}

						break;

					case 5:
						try {
							conexao = BancoDados.conectar();
							jsonObject = new IncidenteDao(conexao).filtrarIncidentes(dados);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						break;

					case 6:
						try {
							conexao = BancoDados.conectar();
							jsonObject = new IncidenteDao(conexao).listarMeusIncidentes(dados);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						break;

					case 7:
						conexao = BancoDados.conectar();
						jsonObject = new IncidenteDao(conexao).excluirIncidente(dados);
						break;

					case 9:
						if (logado && dados != null) {
							jsonObject = ValidarDados.validarToken(dados, conexao);

							if (jsonObject.get("codigo").getAsInt() == 200) {
								conexao = BancoDados.conectar();
								jsonObject = new ClienteDao(conexao).fazerLogout(dados);
								// dados.remove("token");
								logado = false;
							}
						} else {
							System.out.println("Cliente nao logado.");
							jsonObject.addProperty("codigo", 500);
							jsonObject.addProperty("mensagem", "Cliente nao logado");
						}
						break;
					}
				} else {
					System.out.println("Operacao nula.");
					jsonObject.addProperty("codigo", 500);
					jsonObject.addProperty("mensagem", "Operacao nula");
				}
				retornarCliente(jsonObject, retorno_cliente);
			}

			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Problem with Communication Server");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void retornarCliente(JsonObject jsonObject, String retorno_cliente) {
		retorno_cliente = new Gson().toJson(jsonObject);
		out.println(retorno_cliente);
		System.out.println("RETORNO : " + retorno_cliente);
		System.out.println("==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#\n");
	}

}