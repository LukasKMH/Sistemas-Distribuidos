package servidor.entidades;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.google.gson.JsonObject;

public class Incidente {
	private int id;
	private Timestamp data;
	private String rodovia;
	private int km;
	private int token;
	private int tipo;
	private int id_usuario;

	public Incidente() {
	}

	public Incidente(JsonObject dados) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            java.util.Date utilDate = dateFormat.parse(dados.get("data").getAsString());
            this.data = new Timestamp(utilDate.getTime());
            this.data.setNanos(0); // Definir os milissegundos como zero
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.rodovia = dados.get("rodovia").getAsString();
        this.km = Integer.parseInt(dados.get("km").getAsString());
        this.tipo = Integer.parseInt(dados.get("tipo_incidente").getAsString());
        this.id_usuario = Integer.parseInt(dados.get("id_usuario").getAsString());
    }

	public Timestamp getData() {
		return data;
	}

	public void setData(Timestamp data) {
		this.data = data;
	}

	public int getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}

	@Override
	public String toString() {
		return "Incidente [id=" + id + ", data=" + data + ", rodovia=" + rodovia + ", km=" + km + ", token=" + token
				+ ", tipo=" + tipo + ", id_usuario=" + id_usuario + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRodovia() {
		return rodovia;
	}

	public void setRodovia(String rodovia) {
		this.rodovia = rodovia;
	}

	public int getKm() {
		return km;
	}

	public void setKm(int km) {
		this.km = km;
	}

	public int getToken() {
		return token;
	}

	public void setToken(int token) {
		this.token = token;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

}