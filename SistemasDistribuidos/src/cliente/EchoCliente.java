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
		
		// 10.20.8.179 Leo
		// 26.10.188.162 Leo Radmin
		
		// 10.20.8.78 Salles
		// 26.59.167.57 Salles Radmin
		
		// 26.157.130.119 Sauter Radmin
		
		// 26.211.0.15 Gui Radmin
		
		String ipv4 = new String("26.20.133.105");
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
		login.addProperty("token", "fnsjnfjlsfnsjldnfjsdfsfff");
		login.addProperty("id_usuario", "1");

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
//				senha = CaesarCrypt.encrypt(stdIn.readLine());
//				jsonObject.addProperty("senha", senha);
				jsonObject.addProperty("senha", stdIn.readLine());
				saida.println(jsonObject);
				break;
			}

			case 2: {
				System.out.println("========= Atualizar Cadastro =========");
				break;

			}

			case 3: {
				System.out.println("========= Login =========");
				jsonObject.addProperty("id_operacao", operacao);
				System.out.println("Email: ");
				jsonObject.addProperty("email", stdIn.readLine());
				System.out.println("Senha: ");
				// Usando criptografia
//				senha = CaesarCrypt.encrypt(stdIn.readLine());
//				jsonObject.addProperty("senha", senha);
				jsonObject.addProperty("senha", stdIn.readLine());
				saida.println(jsonObject);
				break;

			}

			case 9: {
				System.out.println("========= Logout =========");
				login.addProperty("id_operacao", operacao);
				//login.addProperty("token", "fnsjnfjlsfnsjldnfjsdfsfff");
				System.out.println(login);
				saida.println(login);
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
			Gson gson = new Gson();
			JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
			if (resposta_servidor != null)
				System.out.println("\nServidor: " + resposta_servidor);
			System.out.println("=====================================================================\n");

			if (operacao == 3) {
				login = resposta_servidor;
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
	
	public static void imprimirOperacoes () {
		
		System.out.println("1 - Cadastrar: ");
		System.out.println("3 - Login: ");
		System.out.println("9 - Logout: ");
		System.out.println("0 - Sair: ");
	}

}
