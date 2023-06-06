package servidor.dao;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import com.google.gson.JsonObject;

import servidor.entidades.Cliente;

public class ClienteDao {

	private Connection conexao;
	JsonObject retorno_servidor = new JsonObject();

	public ClienteDao(Connection conn) {
		this.conexao = conn;
	}

	public boolean verificarEmailExiste(String email) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conexao.prepareStatement("SELECT * FROM clientes WHERE email = ?");
			st.setString(1, email);
			rs = st.executeQuery();

			if (rs.next()) {
				return false;
			}

		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}
		return true;
	}

	public void cadastrar(Cliente cliente) throws SQLException {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			// Verificar se o ID do cliente já existe
			String query = "SELECT COUNT(*) FROM clientes WHERE id = ?";
			st = conexao.prepareStatement(query);
			st.setInt(1, cliente.getId());
			rs = st.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			rs.close();
			st.close();

			if (count > 0) {
				// Atualizar os dados do cliente
				st = conexao.prepareStatement("UPDATE clientes SET nome = ?, email = ?, senha = ? WHERE id = ?");
				st.setString(1, cliente.getNome());
				st.setString(2, cliente.getEmail());
				st.setString(3, cliente.getSenha());
				st.setInt(4, cliente.getId());
				st.executeUpdate();
				System.out.println("Dados do cliente atualizados.");
			} else {
				// Inserir novo cliente
				st = conexao.prepareStatement("INSERT INTO clientes (nome, email, senha, token) VALUES (?, ?, ?, ?)");
				st.setString(1, cliente.getNome());
				st.setString(2, cliente.getEmail());
				st.setString(3, cliente.getSenha());
				st.setString(4, cliente.getToken());
				st.executeUpdate();
				System.out.println("Novo cliente cadastrado.");
			}
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}

	}

	public JsonObject fazerLogin(String email, String senha) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conexao.prepareStatement("SELECT * FROM clientes WHERE email = ? AND senha = ?");
			st.setString(1, email);
			st.setString(2, senha);

			rs = st.executeQuery();

			if (rs.next()) {
				Cliente cliente = new Cliente();
				cliente.setId(rs.getInt("id"));
				cliente.setNome(rs.getString("nome"));
				cliente.setEmail(rs.getString("email"));
				cliente.setSenha(rs.getString("senha"));

				// Inserir o token no banco de dados e no objeto Cliente
				inserirTokenCliente(cliente);

				retorno_servidor.addProperty("codigo", 200);
				retorno_servidor.addProperty("token", cliente.getToken());
				retorno_servidor.addProperty("id_usuario", cliente.getId());
				System.out.println("Login realizado.");
			} else {
				String mensagem = "E-mail ou senha invalidos.";
				retorno_servidor.addProperty("codigo", 500);
				retorno_servidor.addProperty("mensagem", mensagem);
				System.out.println(mensagem);
			}
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}

		return retorno_servidor;
	}

	public void inserirTokenCliente(Cliente cliente) throws SQLException {
		PreparedStatement st = null;

		try {
			
			String novoToken = gerarToken();
			st = conexao.prepareStatement("UPDATE clientes SET token = ? WHERE id = ?");
			st.setString(1, novoToken);
			st.setInt(2, cliente.getId());
			st.executeUpdate();
			cliente.setToken(novoToken);
		} finally {
			BancoDados.finalizarStatement(st);
		}
	}

	public String gerarToken() {
		String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}\\|;:'\",.<>?";
		Random random = new SecureRandom();
		int tamanho = 16 + random.nextInt(21); // Gera um número aleatório entre 16 e 36
		StringBuilder token = new StringBuilder(tamanho);
		for (int i = 0; i < tamanho; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			token.append(CHARACTERS.charAt(randomIndex));
		}
		return token.toString();
	}

	public JsonObject verificarTokenCliente(JsonObject dados) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conexao.prepareStatement("SELECT * FROM clientes WHERE id = ? AND token = ?");
			st.setString(1, dados.get("id_usuario").getAsString());
			st.setString(2, dados.get("token").getAsString());
			rs = st.executeQuery();

			if (rs.next()) {
				retorno_servidor.addProperty("codigo", 200);
				System.out.println("Logout realizado.");
			} else {
				retorno_servidor.addProperty("codigo", 500);
				String mensagem = "Os tokens nao sao iguais.";
				retorno_servidor.addProperty("mensagem", mensagem);
				System.out.println(mensagem);
			}

		} catch (SQLException e) {
			// Ocorreu um erro ao verificar o token
			e.printStackTrace();
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}
		return retorno_servidor;

		// Não foi possível verificar o token

	}

	public JsonObject fazerLogout(JsonObject dados) throws SQLException {
		PreparedStatement st = null;
		JsonObject retorno_servidor = new JsonObject();

		try {
			st = conexao.prepareStatement("UPDATE clientes SET token = ? WHERE token = ? and id = ?");
			st.setString(1, "");
			st.setString(2, dados.get("token").getAsString());
			st.setInt(3, dados.get("id_usuario").getAsInt());
			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0)
				retorno_servidor.addProperty("codigo", 200);
			else {
				String mensagem = "Erro ao realizar Logout.";
				retorno_servidor.addProperty("codigo", 500);
				retorno_servidor.addProperty("mensagem", mensagem);
				System.out.println(mensagem);
			}
		} catch (SQLException e) {
			// Ocorreu um erro durante o logout
			e.printStackTrace();
			retorno_servidor.addProperty("codigo", 500);
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.desconectar();
		}

		return retorno_servidor;
	}

	public JsonObject excluirCliente(JsonObject dados) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			// Construir a consulta SQL
			String deleteSQL = "DELETE FROM clientes WHERE email = ? AND senha = ? AND token = ? AND id = ?";

			st = conexao.prepareStatement(deleteSQL);
			st.setString(1, dados.get("email").getAsString());
			st.setString(2, dados.get("senha").getAsString());
			st.setString(3, dados.get("token").getAsString());
			st.setInt(4, dados.get("id_usuario").getAsInt());
			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				retorno_servidor.addProperty("codigo", 200);
				System.out.println("Cliente excluido.");
			} else {
				retorno_servidor.addProperty("codigo", 500);
				String mensagem = "Erro excluir o cliente. E-mail ou senha incorretos.";
				System.out.println(mensagem);
				retorno_servidor.addProperty("mensagem", mensagem);
			}

			return retorno_servidor;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}
		return dados;
	}
}
