package servidor.dao;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import servidor.entidades.Cliente;

public class ClienteDao {

	private Connection conn;

	public ClienteDao(Connection conn) {

		this.conn = conn;
	}

	public void cadastrar(Cliente cliente) throws SQLException {

		PreparedStatement st = null;

		try {

			st = conn.prepareStatement("insert into clientes (nome, email, senha, token) values(?,?,?,?)");

			st.setString(1, cliente.getNome());
			st.setString(2, cliente.getEmail());
			st.setString(3, cliente.getSenha());
			st.setString(4, cliente.getToken());

			st.executeUpdate();
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.desconectar();
		}

	}

	public Cliente fazerLogin(String email, String senha) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT * FROM clientes WHERE email = ? AND senha = ?");
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

				cliente.imprimirDados();
				return cliente;
			}
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}

		return null;
	}

	public void inserirTokenCliente(Cliente cliente) throws SQLException {
		PreparedStatement st = null;

		try {
			// Gerar um novo token
			String novoToken = gerarToken();

			// Atualizar o campo "token" no banco de dados
			st = conn.prepareStatement("UPDATE clientes SET token = ? WHERE id = ?");
			st.setString(1, novoToken);
			st.setInt(2, cliente.getId());
			st.executeUpdate();

			// Atualizar o objeto Cliente com o novo token
			cliente.setToken(novoToken);
		} finally {
			BancoDados.finalizarStatement(st);
		}
	}

	public String gerarToken() {

		String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "0123456789" + "!@#$%^&*()_+-=[]{}\\|;:'\",.<>?";
		Random random = new SecureRandom();
		int tamanho = 16 + random.nextInt(21);
		StringBuilder token = new StringBuilder(tamanho);
		for (int i = 0; i < tamanho; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			token.append(CHARACTERS.charAt(randomIndex));
		}
		return token.toString();

	}

	public void fazerLogout(String token, int id) throws SQLException {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("UPDATE clientes SET token = ? WHERE token = ? and id = ?");
			st.setString(1, "");
			st.setString(2, token);
			st.setInt(3, id);
			st.executeUpdate();
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.desconectar();
		}
	}

}
