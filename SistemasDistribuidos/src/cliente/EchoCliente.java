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
		// 10.20.8.179 Leo
		// 26.10.188.162 Leo Radmin
		// 10.20.8.78 Salles
		// 26.157.130.119 Sauter Radmin
		// 127.0.0.1
		String serverHostname = new String("26.157.130.119");

		if (args.length > 0)
			serverHostname = args[0];
		System.out.println("Attemping to connect to host " + serverHostname + " on port 10008.");

		Socket echoSocket = null;
		PrintWriter saida = null;
		BufferedReader entrada = null;

		// 24001
		try {
			echoSocket = new Socket(serverHostname, 24001);
			saida = new PrintWriter(echoSocket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + serverHostname);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
			System.exit(1);
		}

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		Boolean ligado = true;
		int operacao = 0;
		Scanner scanner = new Scanner(System.in);

//		Gson gson = new Gson();
//		Login cliente1 = new Login();
//		cliente1.setId_operacao(operacao);
//		System.out.println("Email: ");
//		cliente1.setEmail(stdIn.readLine());
//		System.out.println("Senha: ");
//		cliente1.setSenha(stdIn.readLine());
//		String gson1 = gson.toJson(cliente1);
		JsonObject login = new JsonObject();
		while (ligado) {
			JsonObject jsonObject = new JsonObject();
			System.out.println("1 - Cadastrar: \n2 - Atualizar Cadastro: \n3 - Login: \n9 - Logout: \n0 - Sair:\n");
			operacao = scanner.nextInt();
			switch (operacao) {
			case 1: {

				System.out.println("======== Cadastrar ========");
				jsonObject.addProperty("id_operacao", operacao);
				System.out.println("Nome: ");
				jsonObject.addProperty("nome", stdIn.readLine());
				System.out.println("Email: ");
				jsonObject.addProperty("email", stdIn.readLine());
				System.out.println("Senha: ");
				jsonObject.addProperty("senha", stdIn.readLine());
				saida.println(jsonObject);
				break;
			}

			case 2: {
				System.out.println("====== Atualizar Cadastro ======");
				break;

			}

			case 3: {
				System.out.println("======== Login ========");
				jsonObject.addProperty("id_operacao", operacao);
				System.out.println("Email: ");
				jsonObject.addProperty("email", stdIn.readLine());
				System.out.println("Senha: ");
				jsonObject.addProperty("senha", stdIn.readLine());
				saida.println(jsonObject);
				break;

			}

			case 9: {
				System.out.println("======== Logout ========");
				login.addProperty("id_operacao", operacao);
				login.remove("codigo");
				saida.println(login);
				System.out.println(login);
				break;

			}

			case 0: {
				ligado = false;
				System.out.println("Saiu do servidor.");
				break;
			}

			}
			// Pegar a reposta do servidor e trasnformar em Json
			Gson gson = new Gson();
			JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
			if (resposta_servidor != null)
				System.out.println("\nRespota servidor: " + resposta_servidor);
			
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

	public JsonObject logar(String resposta_servidor) {
		Login login = new Login();
		Gson gson = new Gson();
		JsonObject dados = gson.fromJson(resposta_servidor, JsonObject.class);
		
		login.setId(dados.get("id_usuario").getAsInt());
		login.setToken(dados.get("token").getAsString());
		return dados;

	}
}
