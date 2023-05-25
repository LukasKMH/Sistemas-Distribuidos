package cliente;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class EchoCliente {
	public static void main(String[] args) throws IOException {

		// 10.20.8.81 meu ip4
		// 26.20.133.105 meu ip4 Radmin
		// 127.0.0.1

		// 26.137.30.79 Radmin Eduardo

		// 10.20.8.179 Leo
		// 26.10.188.162 Leo Radmin

		// 10.20.8.78 Salles
		// 26.59.167.57 Salles Radmin

		// 26.157.130.119 Sauter Radmin
		// 10.20.8.198 Sauter

		// 26.211.0.15 Gui Radmin
		// 10.20.8.76 Gui

		// 10.20.8.132 Pedro
		// 10.40.11.114 Eduardo
		String ipv4 = new String("127.0.0.1");
		int porta = 24001;

		Socket echoSocket = null;
		PrintWriter saida = null;
		BufferedReader entrada = null;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		Boolean ligado = true;
		int operacao = 0;
		Scanner scanner = new Scanner(System.in);
		String senha;

		JsonObject login = new JsonObject();

		if (args.length > 0)
			ipv4 = args[0];
		System.out.println("Attemping to connect to host " + ipv4 + " on port " + porta + ".");

		try {
			echoSocket = new Socket(ipv4, porta);
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + ipv4);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to: " + ipv4);
			System.exit(1);
		}

		while (ligado) {
			JsonObject jsonObject = new JsonObject();
			imprimirOperacoes();
			operacao = scanner.nextInt();
			switch (operacao) {
			case 1: {

				System.out.println("========= Cadastrar =========");
				jsonObject.addProperty("id_operacao", operacao);
				System.out.println("Nome: ");
				jsonObject.addProperty("nome", stdIn.readLine());
				System.out.println("Email: ");
				jsonObject.addProperty("email", stdIn.readLine());
				// Usando criptografi
				System.out.println("Senha: ");
				senha = CaesarCrypt.encrypt(stdIn.readLine());
				jsonObject.addProperty("senha", senha);
				// jsonObject.addProperty("senha", stdIn.readLine());
				saida.println(jsonObject);
				break;
			}


			case 3: {
				System.out.println("========= Login =========");
				jsonObject.addProperty("id_operacao", operacao);
				System.out.println("Email: ");
				jsonObject.addProperty("email", stdIn.readLine());
				System.out.println("Senha: ");
				// Usando criptografia
				senha = CaesarCrypt.encrypt(stdIn.readLine());
				jsonObject.addProperty("senha", senha);
				// jsonObject.addProperty("senha", stdIn.readLine());
				saida.println(jsonObject);
				break;

			}

			case 9: {
				System.out.println("========= Logout =========");
				login.addProperty("id_operacao", operacao);
				// login.addProperty("token", "fnsjnfjlsfnsjldnfjsdfsfff");
				if (!login.has("token") && !login.has("id_usuario")) {
					login.addProperty("token", "");
					login.addProperty("id_usuario", "");
				}

				saida.println(login);
				System.out.println("Enviado: " + login);
				login.addProperty("token", "");
				login.addProperty("id_usuario", "");

				break;

			}

			case 10: {
				System.out.println("========= NULL =========");
				// login.addProperty("id_operacao", operacao);
				// login.addProperty("token", "fnsjnfjlsfnsjldnfjsdfsfff");
				System.out.println(jsonObject);
				saida.println(jsonObject);
				break;

			}

			case 0: {
				saida.println("Bye.");
				ligado = false;
				System.out.println("Saiu do servidor.");
				break;
			}

			}
			// Pegar a reposta do servidor e trasnformar em Json
			if (operacao != 9)
				System.out.println("ENVIADO: " + jsonObject);

			Gson gson = new Gson();
			JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
			if (resposta_servidor != null)
				System.out.println("\nRESPOSTA: " + resposta_servidor);
			System.out.println("************************************************************************\n");

			if (operacao == 3 && resposta_servidor.has("token") && resposta_servidor.has("id_usuario")) {
				login.addProperty("token", resposta_servidor.get("token").getAsString());
				login.addProperty("id_usuario", resposta_servidor.get("id_usuario").getAsString());

			}

			if (!ligado) {
				break;
			}

		}
		scanner.close();
		saida.close();
		entrada.close();
		stdIn.close();
		echoSocket.close();
	}

	public static void imprimirOperacoes() {

		System.out.println("1 - Cadastrar: ");
		System.out.println("2 - Atualizar cadastro: ");
		System.out.println("3 - Login: ");
		System.out.println("9 - Logout: ");
		System.out.println("10 - Teste NUll: ");
		System.out.println("0 - Sair: ");
	}

	public static void imprimirOperacoes2() {

		System.out.println("2 - Cadastrar: ");
		System.out.println("4 - Reportar incidentes: ");
		System.out.println("5 - Lista de incidentes: ");
		System.out.println("9 - Logout: ");
		System.out.println("0 - Sair: ");
	}

}
