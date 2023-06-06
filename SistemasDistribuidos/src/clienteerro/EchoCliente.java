package clienteerro;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cliente.interfaces.ConexaoPage;

public class EchoCliente {
	private static PrintWriter saida;
	private static BufferedReader entrada;
	String ipv4 = new String("127.0.0.1");
	int porta = 24001;
	private static Socket echoSocket;
	static String nulo = null;
	static boolean logado = false;

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

		// 26.137.30.79 Eduardo Radmin
		// 10.40.11.114 Eduardo

		echoSocket = new ConexaoPage().conectarServidor2("127.0.0.1", 24001);
		saida = new PrintWriter(echoSocket.getOutputStream(), true);
		entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		Boolean ligado = true;
		int operacao = 0;
		Scanner scanner = new Scanner(System.in);
		JsonObject login = new JsonObject();

		while (ligado) {
			JsonObject jsonObject = new JsonObject();
			if (!logado)
				imprimirOperacoes();
			else
				imprimirOperacoes2();
			operacao = scanner.nextInt();
			switch (operacao) {
			case 1:
				System.out.println("========= Cadastrar =========");
				jsonObject.addProperty("id_operacao", operacao);
//				System.out.println("Nome: ");
//				jsonObject.addProperty("nome", stdIn.readLine());
//				System.out.println("Email: ");
//				jsonObject.addProperty("email", stdIn.readLine());
//				System.out.println("Senha: ");
//				senha = CaesarCrypt.encrypt(stdIn.readLine());
//				jsonObject.addProperty("senha", senha);

				jsonObject.addProperty("nome", "kenji");
				jsonObject.addProperty("email", "lukaskenji@gmail.com");
				jsonObject.addProperty("senha", "lukas123");
				saida.println(jsonObject);
				break;

			case 2:
				System.out.println("========= Editar cadastro =========");
				jsonObject.addProperty("id_operacao", operacao);
				jsonObject.addProperty("nome", "kenji");
				jsonObject.addProperty("email", "lukaskenji@gmail.com");
				jsonObject.addProperty("senha", "lukas123");
				saida.println(jsonObject);

			case 3: {

				System.out.println("========= Login =========");
				jsonObject.addProperty("id_operacao", operacao);
//				System.out.println("Email: ");
//				jsonObject.addProperty("email", stdIn.readLine());
//				System.out.println("Senha: ");
//				senha = CaesarCrypt.encrypt(stdIn.readLine());
//				jsonObject.addProperty("senha", senha);
				jsonObject.addProperty("email", "lukaskenji@gmail.com");
				jsonObject.addProperty("senha", "t}si{9:;");
//				jsonObject.addProperty("email", nulo);
//				jsonObject.addProperty("senha", nulo);

				saida.println(jsonObject);
				break;

			}

			case 4:
				System.out.println("========= Reportar incidentes =========");
				jsonObject.addProperty("id_operacao", operacao);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String dataAtual = formatter.format(LocalDateTime.now());
				jsonObject.addProperty("data", dataAtual);
				jsonObject.addProperty("rodovia", "BR-123");
				jsonObject.addProperty("km", 58);
				jsonObject.addProperty("tipo_incidente", 6);
				//jsonObject.addProperty("token", login.get("token").getAsString());
				jsonObject.addProperty("id_usuario", login.get("id_usuario").getAsString());
				saida.println(jsonObject);
				break;

			case 6:

				System.out.println("========= Meus incidentes =========");
				jsonObject.addProperty("id_operacao", operacao);

				// jsonObject.addProperty("senha", nulo);

				saida.println(jsonObject);
				break;

			case 9:
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

			case 10:
				System.out.println("========= Editar incidentes =========");
				jsonObject.addProperty("id_operacao", operacao);
				saida.println(jsonObject);
				break;

			case 0: {
				saida.println("Bye.");
				ligado = false;
				logado = false;
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
				logado = true;

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
		System.out.println("3 - Login: ");
		System.out.println("0 - Sair: ");
	}

	public static void imprimirOperacoes2() {

		System.out.println("2 - Atualizar cadastro: ");
		System.out.println("4 - Reportar incidentes: ");
		System.out.println("5 - Lista de incidentes: ");
		System.out.println("6 - Meus incidentes: ");
		System.out.println("9 - Logout: ");
	}

}
