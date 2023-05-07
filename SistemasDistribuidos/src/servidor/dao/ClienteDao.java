package servidor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import servidor.entidades.Cliente;

public class ClienteDao {

	private Connection conn;
	private String token;

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

//	public Cliente fazerLogin(String email, String senha) throws SQLException {
//
//		PreparedStatement st = null;
//		ResultSet rs = null;
//
//		try {
//			st = conn.prepareStatement("select * from clientes where email = ? and senha = ?");
//			st.setString(1, email);
//			st.setString(2, senha);
//
//			rs = st.executeQuery();
//
//			if (rs.next()) {
//
//				Cliente cliente = new Cliente();
//				cliente.setId(rs.getInt("id"));
//				cliente.setNome(rs.getString("nome"));
//				cliente.setEmail(rs.getString("email"));
//				cliente.setSenha(rs.getString("senha"));
//				cliente.imprimirDados();
//				return cliente;
//			}
//
//		} finally {
//			BancoDados.finalizarStatement(st);
//			BancoDados.finalizarResultSet(rs);
//			BancoDados.desconectar();
//		}
//
//		return null;
//	}
//
////	inserirToken(cliente);
////	token = rs.getString("token");
////	cliente.setToken(rs.getString("token"));
////	System.out.println("Cliente: dasdas" + token);
//	public void inserirToken(Cliente cliente) throws SQLException {
//		PreparedStatement st = null;
//
//		st = conn.prepareStatement("update clientes set token = ? where id = ?");
//		st.setString(1, "tk");
//		st.setInt(2, cliente.getId());
//		st.executeUpdate();
//
//	}
	
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
	    // Implemente a lógica para gerar um token único aqui
	    // Pode ser um UUID, um hash, ou qualquer outra forma de token que você preferir
	    // Retorne o token gerado como uma string
	    return "tk";
	}


	public void fazerLogout(Cliente cliente) throws SQLException {

		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("update clientes set token = null where id = ?");
			st.setInt(1, cliente.getId());
			st.executeUpdate();
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.desconectar();
		}
	}

}
