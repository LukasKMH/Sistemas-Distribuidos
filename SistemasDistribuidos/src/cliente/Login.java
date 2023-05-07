package cliente;

public class Login {
	private int id_usuario;
	private String token;

	public Login() {

	}

	public void imprimirDados() {
		System.out.println("Nome: " + this.id_usuario);
		System.out.println("Senha: " + this.token);
	}

	public int getId() {
		return id_usuario;
	}

	public void setId(int id) {
		this.id_usuario = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
