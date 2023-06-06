package servidor.uteis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import servidor.dao.BancoDados;
import servidor.dao.ClienteDao;
import servidor.entidades.Cliente;
import com.google.gson.JsonObject;

public class ValidarDados {

	public static JsonObject validarDadosCadastro(Cliente cliente, Connection conexao)
			throws SQLException, IOException {
		conexao = BancoDados.conectar();
		JsonObject retorno_servidor = new JsonObject();
		String mensagem = "";
		if (cliente != null) {
			boolean nomeValido = validarNome(cliente.getNome());
			if (nomeValido) {
				boolean emailValido = validarEmail(cliente.getEmail());
				if (emailValido) {
					boolean emailCadastrado = new ClienteDao(conexao).verificarEmailExiste(cliente.getEmail());
					if (emailCadastrado) {
						boolean senhaValida = validarSenha(cliente.getSenha());
						if (senhaValida) {
							retorno_servidor.addProperty("codigo", 200);
							return retorno_servidor;
						} else {
							mensagem = "Senha invalida";
						}
					} else {
						mensagem = "E-mail ja cadastrado";
					}
				} else {
					mensagem = "E-mail invalido";
				}
			} else {
				mensagem = "Nome invalido";
			}
		} else {
			mensagem = "Cliente nulo";
		}

		retorno_servidor.addProperty("codigo", 500);
		retorno_servidor.addProperty("mensagem", mensagem);
		System.out.println(mensagem);
		return retorno_servidor;
	}

	public static boolean validarNome(String nome) {
		if (nome != null && !nome.isEmpty()) {
			Pattern padrao = Pattern.compile("^\\D{3,32}$");
			Matcher matcher = padrao.matcher(nome);
			return matcher.matches();
		}
		return false;
	}

	public static boolean validarEmail(String email) {
		if (email != null && !email.isEmpty()) {
			if (email.length() >= 16 && email.length() <= 50) {
				return email.contains("@");
			}
		}
		return false;
	}

	public static boolean validarSenha(String senha) {
		if (senha != null && !senha.isEmpty()) {
			return senha.length() >= 8 && senha.length() <= 32;
		}
		return false;
	}

	// TOKEN
	public static JsonObject validarToken(JsonObject dados, Connection conexao) throws SQLException, IOException {
		JsonObject retorno_servidor = new JsonObject();
		if (dados != null) {
			String token = dados.get("token").getAsString();
			retorno_servidor.addProperty("codigo", (token.length() >= 16 && token.length() <= 36) ? 200 : 500);

			if (retorno_servidor.get("codigo").getAsInt() == 200) {
				conexao = BancoDados.conectar();
				retorno_servidor = new ClienteDao(conexao).verificarTokenCliente(dados);
			} else {
				retorno_servidor.addProperty("mensagem", "O token deve ter um tamanho entre 16 e 36 caracteres.");
			}
			return retorno_servidor;
		}
		return retorno_servidor;
	}

	public static JsonObject validarDadosIncidente(JsonObject dados) {
		JsonObject resultado = new JsonObject();
		if (dados != null && dados.has("data") && dados.has("rodovia") && dados.has("km")
				&& dados.has("tipo_incidente")) {
			String mensagem = "";
			if (!validarData(dados)) {
				mensagem = "A data deve seguir o seguinte formato: yyyy-MM-dd HH:mm:ss.";
			} else if (!validarRodovia(dados)) {
				mensagem = "A rodovia deve seguir o seguinte formato: BR-123.";
			} else if (!validarKm(dados)) {
				mensagem = "Os Km devem estar entre 0 e 999.";
			} else if (!validarTipoIncidente(dados)) {
				mensagem = "O incidente deve estar entre 1 e 14.";
			} else {
				resultado.addProperty("codigo", 200);
				return resultado;
			}

			resultado.addProperty("codigo", 500);
			System.out.println(mensagem);
			resultado.addProperty("mensagem", mensagem);
			return resultado;
		} else {
			resultado.addProperty("codigo", 500);
			resultado.addProperty("mensagem", "Campos obrigatorios faltando");
			return resultado;
		}
	}

	public static boolean validarData(JsonObject dados) {
		String data = dados.get("data").getAsString();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			dateFormat.parse(data);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static boolean validarRodovia(JsonObject dados) {

		String rodovia = dados.get("rodovia").getAsString();
		Pattern padrao = Pattern.compile("^[A-Z]{2}-\\d{1,3}$");
		Matcher matcher = padrao.matcher(rodovia);
		return matcher.matches();

	}

	public static boolean validarKm(JsonObject dados) {

		int km = dados.get("km").getAsInt();
		return km >= 1 && km <= 999;
	}

	public static boolean validarTipoIncidente(JsonObject dados) {

		int tipoIncidente = dados.get("tipo_incidente").getAsInt();
		return tipoIncidente >= 1 && tipoIncidente <= 14;
	}
}
