package cliente;

public class Login {
	private int id_operacao;
	private String nome;
	private String email;
	private String senha;

	public Login() {

	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getId_Operacao() {
		return id_operacao;
	}

	public void setId_Operacao(int id) {
		this.id_operacao = id;
	}

	public int getId_operacao() {
		return id_operacao;
	}

	public void setId_operacao(int id_operacao) {
		this.id_operacao = id_operacao;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

}
