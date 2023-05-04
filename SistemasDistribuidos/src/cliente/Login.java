package cliente;

public class Login {
	private int id_operacao;
	private String nome;
	private String email;
	private String senha;

	public Login() {

	}

	public void imprimirDados() {
		System.out.println("Nome: " + this.nome);
		System.out.println("Email: " + this.email);
		System.out.println("Senha: " + this.senha);
	}

	public int getId_operacao() {
		return id_operacao;
	}

	public void setId_operacao(int id_operacao) {
		this.id_operacao = id_operacao;
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

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

}
