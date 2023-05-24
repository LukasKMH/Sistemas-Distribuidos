package servidor.entidades;

import java.util.Date;

public class Incidente {
	private Date data;
	private String rodovia;
	private TipoIncidente tipo;
	private int token;
	private int id;

	public Incidente() {
	}

	// Métodos getters e setters para os atributos

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getRodovia() {
		return rodovia;
	}

	public void setRodovia(String rodovia) {
		this.rodovia = rodovia;
	}

	public TipoIncidente getTipo() {
		return tipo;
	}

	public void setTipo(TipoIncidente tipo) {
		this.tipo = tipo;
	}

	public int getToken() {
		return token;
	}

	public void setToken(int token) {
		this.token = token;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
