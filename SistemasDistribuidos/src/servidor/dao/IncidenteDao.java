package servidor.dao;

import java.sql.Connection;
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
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import servidor.Dados;
import servidor.entidades.Incidente;
import servidor.entidades.TipoIncidente;

public class IncidenteDao {
	private Connection conn;
	JsonObject retorno_servidor = new JsonObject();
	String mensagem;

	public IncidenteDao(Connection conn) {
		this.conn = conn;
	}

	public boolean reportarIncidente(Incidente incidente) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// Verificar se o ID do incidente já existe
			String query = "SELECT COUNT(*) FROM incidentes WHERE id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, incidente.getId());
			rs = st.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			rs.close();
			st.close();
			if (count > 0) {
				// Atualizar os dados do incidente
				System.out.println("Dados do incidente atualizados.");
				return atualizarIncidente(incidente);
			} else {
				// Inserir novo incidente
				System.out.println("Novo incidente cadastrado.");
				return inserirIncidente(incidente);
			}
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}
	}

	private boolean atualizarIncidente(Incidente incidente) throws SQLException {
		try (PreparedStatement st = conn.prepareStatement(
				"UPDATE incidentes SET data = ?, rodovia = ?, km = ?, tipo_incidente = ? WHERE id = ?")) {
			st.setTimestamp(1, new Timestamp(incidente.getData().getTime()));
			st.setString(2, incidente.getRodovia());
			st.setInt(3, incidente.getKm());
			st.setInt(4, incidente.getTipo());
			st.setInt(5, incidente.getId());
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
				return true;
			} else {
				throw new SQLException("Falha ao atualizar incidente.");
			}
		}
	}

	private boolean inserirIncidente(Incidente incidente) throws SQLException {
		try (PreparedStatement st = conn.prepareStatement(
				"INSERT INTO incidentes (data, rodovia, km, tipo_incidente, id_usuario) VALUES (?, ?, ?, ?, ?)")) {
			st.setTimestamp(1, new Timestamp(incidente.getData().getTime()));
			st.setString(2, incidente.getRodovia());
			st.setInt(3, incidente.getKm());
			st.setInt(4, incidente.getTipo());
			st.setInt(5, incidente.getId_usuario());
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
				return true;
			} else {
				throw new SQLException("Falha ao cadastrar incidente.");
			}
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

		try {
			// Construir a consulta SQL
			String buscaSQL = "SELECT * FROM incidentes WHERE DATE(data) = ? AND TIME(data) BETWEEN ? AND ? AND rodovia = ?";

			if (dados.has("faixa_km") && !dados.get("faixa_km").equals(JsonNull.INSTANCE)) {
				if (dados.get("faixa_km").getAsString().length() > 0) {
					List<Integer> faixaKm = Dados.separarNumeros(dados.get("faixa_km").getAsString());
					if (faixaKm.size() == 2) {
						buscaSQL += " AND km BETWEEN ? AND ?";
						params.add(faixaKm.get(0));
						params.add(faixaKm.get(1));
					}
				}

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
				Timestamp dataResult = rs.getTimestamp("data");
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

	public JsonArray listarMeusIncidentes(JsonObject dados) throws SQLException, ParseException {
		PreparedStatement st = null;
		ResultSet rs = null;
		JsonArray listaIncidentes = new JsonArray();

		int userId = Integer.parseInt(dados.get("id_usuario").getAsString()); // ID do usuário

		try {

			// Construir a consulta SQL
			String buscaSQL = "SELECT * FROM incidentes WHERE id_usuario = ?";

			st = conn.prepareStatement(buscaSQL);
			st.setInt(1, userId);
			rs = st.executeQuery();

			// Processar os resultados da consulta
			while (rs.next()) {
				int id = rs.getInt("id");
				String rodoviaResult = rs.getString("rodovia");
				Timestamp dataResult = rs.getTimestamp("data");
				int km = rs.getInt("km");
				int tipoCodigo = rs.getInt("tipo_incidente");
				// Obter a string correspondente ao tipo de incidente
				String mensagemIncidente = TipoIncidente.getMensagemFromCode(tipoCodigo);

				// Construir o objeto JSON com os dados do incidente
				JsonObject incidenteJson = new JsonObject();
				incidenteJson.addProperty("id_incidente", id);
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

	public JsonObject excluirIncidente(JsonObject dados) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			// Construir a consulta SQL
			String deleteSQL = "DELETE FROM incidentes WHERE id = ? AND id_usuario = ?";

			st = conn.prepareStatement(deleteSQL);
			st.setInt(1, dados.get("id_incidente").getAsInt());
			st.setInt(2, dados.get("id_usuario").getAsInt());
			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				retorno_servidor.addProperty("codigo", 200);
				System.out.println("Incidente excluído com sucesso!");
			} else {
				retorno_servidor.addProperty("codigo", 500);
				mensagem = "Não foi possível excluir o incidente. Verifique se o ID do incidente e o ID do usuário estão corretos.";
				System.out.println(mensagem);
				retorno_servidor.addProperty("mensagem", mensagem);
			}

			return retorno_servidor;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}
		return dados;
	}

}
