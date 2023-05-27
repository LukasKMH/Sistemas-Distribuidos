package servidor;

import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

	public static void main(String[] args) throws IOException {
		ServerSocket servidor = null;

		try {
			servidor = new ServerSocket(24001);
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
			System.err.println("Could not listen on port: 10008.");
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
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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

						// Mensagem retornada ao cliente
						int codigo = validarDados(cliente, conexao) ? 200 : 500;

						// Adiciona algumas propriedades
						jsonObject.addProperty("codigo", codigo);
						String mensagem;

						if (codigo == 200) {
							// Conecta com o banco de dados
							conexao = BancoDados.conectar();
							new ClienteDao(conexao).cadastrar(cliente);

							if (operacao == 2) {
								conexao = BancoDados.conectar();
								cliente = new ClienteDao(conexao).fazerLogin(cliente.getEmail(), cliente.getSenha());
								jsonObject.addProperty("token", cliente.getToken());

							}

						} else {
							mensagem = (operacao == 1 ? "Erro ao realizar cadastro." : "Erro ao alterar os dados.");
							jsonObject.addProperty("mensagem", mensagem);
							System.out.println(mensagem);
						}

						retorno_cliente = new Gson().toJson(jsonObject);
						out.println(retorno_cliente);
						break;

					case 3:
						String email = dados.get("email").getAsString();
						String senha = dados.get("senha").getAsString();
						conexao = BancoDados.conectar();
						cliente = new ClienteDao(conexao).fazerLogin(email, senha);
						if (cliente != null) {
							jsonObject.addProperty("codigo", 200);
							jsonObject.addProperty("token", cliente.getToken());
							jsonObject.addProperty("id_usuario", cliente.getId());
							System.out.println("Login realizado.");
							logado = true;
						} else {
							jsonObject.addProperty("codigo", 500);
							jsonObject.addProperty("mensagem", "Email ou senha invalidos");
							System.out.println("Erro ao realizar login.");
						}
						retorno_cliente = new Gson().toJson(jsonObject);
						out.println(retorno_cliente);

						break;

					case 4:
						incidente = new Incidente(dados);
						System.out.println("Incidente: " + incidente);
						// Conectar com o BD
						conexao = BancoDados.conectar();
						codigo = new IncidenteDao(conexao).reportarIncidente(incidente) ? 200 : 500;
							
						jsonObject.addProperty("codigo", codigo);
						if (codigo == 500)
							jsonObject.addProperty("codigo", "Falha ao cadastrar incidente.");
						retorno_cliente = new Gson().toJson(jsonObject);
						out.println(retorno_cliente);
						break;

					case 5: 
						conexao = BancoDados.conectar();
						
						
//						int periodo = Integer.parseInt(dados.get("periodo").getAsString());
//						List<LocalTime> horarios = Dados.obterHorarios(periodo);
//						System.out.println("Horarios: " + horarios);
//						List<Integer> faixaKm = Dados.separarNumeros(dados.get("faixa_km").getAsString());
//						System.out.println("Faixa KM: " + faixaKm);
						
						
						try {
							JsonArray listaIncidentes = new IncidenteDao(conexao).filtrarIncidentes(dados);
							jsonObject.addProperty("codigo", 200);
							jsonObject.add("lista_incidentes", listaIncidentes);
						} catch (ParseException e) {
						    e.printStackTrace();
						    jsonObject.addProperty("codigo", 500);
						}

						jsonObject.addProperty("codigo", 200);
		

						retorno_cliente = new Gson().toJson(jsonObject);
						out.println(retorno_cliente);
						break;


						
					case 9:

						if (logado && dados != null) {
							String token = dados.get("token").getAsString();
							int id = dados.get("id_usuario").getAsInt();
							codigo = (token.length() >= 16 && token.length() <= 36) ? 200 : 500;
							jsonObject.addProperty("codigo", codigo);

							if (codigo == 200) {
								// Conecta com o banco de dados
								conexao = BancoDados.conectar();
								new ClienteDao(conexao).fazerLogout(token, id);
								// dados.addProperty("token", "");
								dados.remove("token");
								System.out.println("Logout realizado.");
								logado = false;

							} else {
								System.out.println("Erro ao fazer logout.");
								jsonObject.addProperty("mensagem", "Erro ao realizar logout");

							}
						} else {
							System.out.println("Cliente nao logado.");
							jsonObject.addProperty("codigo", 500);
							jsonObject.addProperty("mensagem", "Cliente nao logado");
						}

						retorno_cliente = new Gson().toJson(jsonObject);
						out.println(retorno_cliente);
						;
						break;
					}
				} else {
					System.out.println("Operacao nula.");
					jsonObject.addProperty("codigo", 500);
					jsonObject.addProperty("mensagem", "Operacao nula");
					retorno_cliente = new Gson().toJson(jsonObject);
					out.println(retorno_cliente);
				}
				System.out.println("RETORNO : " + retorno_cliente);
				System.out.println("==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#==#\n");
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

	public boolean validarDados(Cliente cliente, Connection conexao) throws SQLException, IOException {
		conexao = BancoDados.conectar();
		return validarNome(cliente.getNome()) && validarEmail(cliente.getEmail())
				&& new ClienteDao(conexao).verificarEmail(cliente.getEmail()) && validarSenha(cliente.getSenha());
	}

	public static boolean validarNome(String nome) {
		Pattern padrao = Pattern.compile("^\\D{3,32}$");
		Matcher matcher = padrao.matcher(nome);
		return matcher.matches();
	}

	public static boolean validarEmail(String email) {
		// Verifica se o email tem no mínimo 16 e no máximo 50 caracteres
		if (email.length() < 16 || email.length() > 50) {
			return false;
		}

		// Verifica se o email contém um "@"
		if (!email.contains("@")) {
			return false;
		}

		return true;
	}

	public static boolean validarSenha(String senha) {
		// Retorna true se a senha tem no mínimo 8 e no máximo 32 caracteres
		return senha.length() >= 8 && senha.length() <= 32 ? true : false;
	}

}
