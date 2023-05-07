package servidor.entidades;

public class Cliente {

	private int id;
	private String nome;
	private String email;
	private String senha;
	private String token;

	public Cliente() {

	}

	public void imprimirDados() {
		System.out.println("ID: " + this.id);
		System.out.println("Nome: " + this.nome);
		System.out.println("Email: " + this.email);
		System.out.println("Senha: " + this.senha);
		System.out.println("Token: " + this.token);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}


}
