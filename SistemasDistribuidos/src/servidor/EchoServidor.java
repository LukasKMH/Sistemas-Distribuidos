package servidor;

import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import cliente.Login;

import java.io.*;

public class EchoServidor extends Thread {
	protected Socket clientSocket;

	public static void main(String[] args) throws IOException {
		ServerSocket servidor = null;

		try {
			servidor = new ServerSocket(10008);
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
		System.out.println("New Communication Thread Started");

		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// String recebida do cliente
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				// Imprime o json
				System.out.println("Servidor: " + inputLine);
				Gson gson = new Gson();
				Login cliente = gson.fromJson(inputLine, Login.class);
				cliente.imprimirDados();
				
				// Mensagem retornada ao cliente
				int codigo = validarDados(cliente) ? 200 : 500;
			
				out.println(codigo);


		

				if (inputLine.equals("Até logo."))
					break;
			}

			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Problem with Communication Server");
			System.exit(1);
		}
	}

	

	public boolean validarDados(Login cliente) {
		return validarNome(cliente.getNome()) && validarEmail(cliente.getEmail()) && validarSenha(cliente.getSenha());
	}
	
	public static boolean validarNome(String nome) {
		Pattern padrao = Pattern.compile("^[a-zA-Z]{3,32}$");
		Matcher matcher = padrao.matcher(nome);
		return matcher.matches();
	}

	public static boolean validarEmail(String email) {
		// Verifica se o email tem no mínimo 16 e no máximo 50 caracteres
		if (email.length() < 16 || email.length() > 50) {
			return false;
		}

		// Verifica se o email contém um "@" e um "."
		if (!email.contains("@") || !email.contains(".")) {
			return false;
		}

		return true;
	}

	public static boolean validarSenha(String senha) {
		// Retorna true se a senha tem no mínimo 8 e no máximo 32 caracteres
		return senha.length() >= 8 && senha.length() <= 32 ? true : false;
	}

}
