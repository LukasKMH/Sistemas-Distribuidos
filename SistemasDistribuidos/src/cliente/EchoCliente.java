package cliente;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.google.gson.Gson;

public class EchoCliente {
	public static void main(String[] args) throws IOException {

		//10.20.8.179
		String serverHostname = new String("127.0.0.1");

		if (args.length > 0)
			serverHostname = args[0];
		System.out.println("Attemping to connect to host " + serverHostname + " on port 10008.");

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		//24001
		try {
			echoSocket = new Socket(serverHostname, 10008);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
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
		Scanner entrada = new Scanner(System.in);
		Login cliente1 = new Login();
		Gson gson = new Gson();

		while (ligado) {
			System.out.println("1 - Cadastrar: \n2- Atualizar Cadastro: \n3 - Login: \n0 - Sair:\n");
			operacao = entrada.nextInt();
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
				FileWriter writer = new FileWriter("dados.json");
				writer.write(gson1);
				writer.close();
				out.println(gson1);
				break;
			}

			case 2: {
				System.out.println("====== Atualizar Cadastro ======");
				cliente1.setId_Operacao(operacao);
				System.out.println("Nome: ");
				cliente1.setNome(stdIn.readLine());
				System.out.println("Email: ");
				cliente1.setEmail(stdIn.readLine());
				System.out.println("Senha: ");
				cliente1.setSenha(stdIn.readLine());
				String gson1 = gson.toJson(cliente1);
				out.println(gson1);
				break;

			}

			case 3: {
				System.out.println("====== Login ======");
				cliente1.setId_Operacao(operacao);
				System.out.println("Nome: ");
				cliente1.setNome(stdIn.readLine());
				System.out.println("Email: ");
				cliente1.setEmail(stdIn.readLine());
				System.out.println("Senha: ");
				cliente1.setSenha(stdIn.readLine());
				String gson1 = gson.toJson(cliente1);
				out.println(gson1);
				break;

			}

			case 0: {
				ligado = false;
				System.out.println("Saiu do servidor.");
				break;
			}

			}
			System.out.println("Respota servidor: " + in.readLine());

			if (!ligado) {
				break;
			}

		}
		entrada.close();
		out.close();
		in.close();
		stdIn.close();
		echoSocket.close();
	}
}
