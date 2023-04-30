package servidor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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

	public List<Cliente> buscarTodos() {

		return null;
	}

	public Cliente buscarPorRA(int ra) {

		return null;
	}

	public void atualizar(Cliente aluno) {

	}

	public int excluir(int ra) {

		return 0;
	}
}
