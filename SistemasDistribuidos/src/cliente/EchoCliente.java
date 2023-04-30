package cliente;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.google.gson.Gson;

public class EchoCliente {
	public static void main(String[] args) throws IOException {

		// 10.20.8.179
		String serverHostname = new String("127.0.0.1");

		if (args.length > 0)
			serverHostname = args[0];
		System.out.println("Attemping to connect to host " + serverHostname + " on port 10008.");

		Socket echoSocket = null;
		PrintWriter saida = null;
		BufferedReader entrada = null;

		// 24001
		try {
			echoSocket = new Socket(serverHostname, 10008);
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
		Login cliente1 = new Login();
		Gson gson = new Gson();

		while (ligado) {
			System.out.println("1 - Cadastrar: \n2 - Atualizar Cadastro: \n3 - Login: \n0 - Sair:\n");
			operacao = scanner.nextInt();
			switch (operacao) {
			case 1: {

				System.out.println("====== Cadastrar ======");
				cliente1.setId_Operacao(operacao);
				System.out.println("Nome: ");
				cliente1.setNome(stdIn.readLine());
				System.out.println("Email: ");
				cliente1.setEmail(stdIn.readLine());
				System.out.println("Senha: ");
				cliente1.setSenha(stdIn.readLine());
				String gson1 = gson.toJson(cliente1);
//				FileWriter writer = new FileWriter("dados.json");
//				writer.write(gson1);
//				writer.close();
				saida.println(gson1);
				break;
			}

			case 2: {
				System.out.println("====== Atualizar Cadastro ======");
				break;

			}

			case 3: {
				System.out.println("====== Login ======");

				break;

			}

			case 0: {
				ligado = false;
				System.out.println("Saiu do servidor.");
				break;
			}

			}
			System.out.println("Respota servidor: " + entrada.readLine());

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
}
