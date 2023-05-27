package servidor.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import servidor.Dados;
import servidor.entidades.Incidente;
import servidor.entidades.TipoIncidente;

public class IncidenteDao {
	private Connection conn;

	public IncidenteDao(Connection conn) {
		this.conn = conn;
	}

	public boolean reportarIncidente(Incidente incidente) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			// Inserir novo incidente
			st = conn.prepareStatement(
					"INSERT INTO incidentes (data, rodovia, km, tipo, id_usuario) VALUES (?, ?, ?, ?, ?)");
			st.setTimestamp(1, new Timestamp(incidente.getData().getTime()));
			st.setString(2, incidente.getRodovia());
			st.setInt(3, incidente.getKm());
			st.setInt(4, incidente.getTipo());
			st.setInt(5, incidente.getId_usuario());
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Novo incidente cadastrado.");
				return true;
			} else {
				System.out.println("Falha ao cadastrar incidente.");
				return false;
			}
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}
	}

	public JsonArray filtrarIncidentes(JsonObject dados) throws SQLException, ParseException {
		PreparedStatement st = null;
		ResultSet rs = null;
		JsonArray listaIncidentes = new JsonArray();

		// Data - dia
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date utilDate = dateFormat.parse(dados.get("data").getAsString());
		Timestamp data = new Timestamp(utilDate.getTime());

		// Horario - periodo
		int periodo = Integer.parseInt(dados.get("periodo").getAsString());
		List<LocalTime> horarios = Dados.obterHorarios(periodo);

		List<Object> params = new ArrayList<>();
		List<Integer> faixaKm = Dados.separarNumeros(dados.get("faixa_km").getAsString());

		try {
			// Construir a consulta SQL
			String buscaSQL = "SELECT * FROM incidentes WHERE DATE(data) = ? AND TIME(data) BETWEEN ? AND ? AND rodovia = ?";

			if (faixaKm.size() == 2) {
				buscaSQL += " AND km BETWEEN ? AND ?";
				params.add(faixaKm.get(0));
				params.add(faixaKm.get(1));
			}

			st = conn.prepareStatement(buscaSQL);
			st.setTimestamp(1, data);
			st.setTime(2, Time.valueOf(horarios.get(0)));
			st.setTime(3, Time.valueOf(horarios.get(1)));
			st.setString(4, dados.get("rodovia").getAsString());

			int paramIndex = 5;
			for (Object param : params) {
				if (param instanceof Integer) {
					st.setInt(paramIndex, (Integer) param);
				}
				paramIndex++;
			}

			rs = st.executeQuery();

			// Processar os resultados da consulta
			while (rs.next()) {
				int id = rs.getInt("id");
				String rodoviaResult = rs.getString("rodovia");
				Date dataResult = rs.getDate("data");
				int km = rs.getInt("km");
				int tipoCodigo = rs.getInt("tipo_incidente");
				// Obter a string correspondente ao tipo de incidente
				String mensagemIncidente = TipoIncidente.getMensagemFromCode(tipoCodigo);

				// Construir o objeto JSON com os dados do incidente
				JsonObject incidenteJson = new JsonObject();
				incidenteJson.addProperty("id", id);

				incidenteJson.addProperty("data", dataResult.toString());
				incidenteJson.addProperty("rodovia", rodoviaResult);
				incidenteJson.addProperty("km", km);
				incidenteJson.addProperty("tipo_incidente", mensagemIncidente);

				// Adicionar o objeto JSON à lista de incidentes
				listaIncidentes.add(incidenteJson);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}

		return listaIncidentes;
	}

}
