package servidor;

import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import servidor.dao.BancoDados;
import servidor.dao.ClienteDao;
import servidor.entidades.Cliente;

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
					System.out.println("Waiting for Connection");
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
				String retorno_cliente;
				Cliente cliente = new Cliente();
				JsonObject jsonObject = new JsonObject();
				Connection conexao;
				if (entrada_cliente.equals("Bye."))
					break;
				// Imprime o json

				System.out.println("Recebido: " + entrada_cliente);
				Gson gson = new Gson();
				JsonObject dados = gson.fromJson(entrada_cliente, JsonObject.class);
				int operacao = dados.get("id_operacao").getAsInt();
				switch (operacao) {
				case 1:
					cliente.setNome(dados.get("nome").getAsString());
					cliente.setEmail(dados.get("email").getAsString());
					cliente.setSenha(dados.get("senha").getAsString());
					cliente.setToken("");

					// Mensagem retornada ao cliente
					int codigo = validarDados(cliente) ? 200 : 500;

					// Adiciona algumas propriedades
					jsonObject.addProperty("codigo", codigo);
					// Converte o JsonObject em uma string JSON

					if (codigo == 200) {
						// Conecta com o banco de dados
						conexao = BancoDados.conectar();
						new ClienteDao(conexao).cadastrar(cliente);
						System.out.println("Cliente cadastrado.");

					} else {
						System.out.println("Erro ao cadastrar cliente.");
						jsonObject.addProperty("mensagem", "Erro ao realizar cadastro");

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

				case 9:

					if (logado && dados != null) {
						String token = dados.get("token").getAsString();
						int id = dados.get("id_usuario").getAsInt();
						codigo = (token.length() >= 16 && token.length() <=36) ? 200 : 500;
						jsonObject.addProperty("codigo", codigo);

						if (codigo == 200) {
							// Conecta com o banco de dados
							conexao = BancoDados.conectar();
							new ClienteDao(conexao).fazerLogout(token, id);
							dados.addProperty("token", "");

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
					break;

				}
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

	public boolean validarDados(Cliente cliente) {
		return validarNome(cliente.getNome()) && validarEmail(cliente.getEmail()) && validarSenha(cliente.getSenha());
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
