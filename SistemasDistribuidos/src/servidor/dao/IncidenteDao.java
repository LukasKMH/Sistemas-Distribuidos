package servidor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import servidor.entidades.Incidente;
import servidor.uteis.Dados;

public class IncidenteDao {
	private Connection conn;
	JsonObject retorno_servidor = new JsonObject();
	String mensagem;

	public IncidenteDao(Connection conn) {
		this.conn = conn;
	}

	public JsonObject reportarIncidente(Incidente incidente) throws SQLException {
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
				mensagem = "Novo incidente cadastrado.";
				System.out.println(mensagem);
				return inserirIncidente(incidente);
			}
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.finalizarResultSet(rs);
			BancoDados.desconectar();
		}
	}

	private JsonObject atualizarIncidente(Incidente incidente) throws SQLException {
		try (PreparedStatement st = conn.prepareStatement(
				"UPDATE incidentes SET data = ?, rodovia = ?, km = ?, tipo_incidente = ? WHERE id = ?")) {
			st.setTimestamp(1, new Timestamp(incidente.getData().getTime()));
			st.setString(2, incidente.getRodovia());
			st.setInt(3, incidente.getKm());
			st.setInt(4, incidente.getTipo());
			st.setInt(5, incidente.getId());
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
				retorno_servidor.addProperty("codigo", 200);
			} else {
				retorno_servidor.addProperty("codigo", 500);
				throw new SQLException("Falha ao atualizar incidente.");
			}
			return retorno_servidor;
		}
	}

	private JsonObject inserirIncidente(Incidente incidente) throws SQLException {
		try (PreparedStatement st = conn.prepareStatement(
				"INSERT INTO incidentes (data, rodovia, km, tipo_incidente, id_usuario) VALUES (?, ?, ?, ?, ?)")) {
			st.setTimestamp(1, new Timestamp(incidente.getData().getTime()));
			st.setString(2, incidente.getRodovia());
			st.setInt(3, incidente.getKm());
			st.setInt(4, incidente.getTipo());
			st.setInt(5, incidente.getId_usuario());
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
				retorno_servidor.addProperty("codigo", 200);
			} else {
				retorno_servidor.addProperty("codigo", 500);
				throw new SQLException("Falha ao cadastrar incidente.");
			}
			return retorno_servidor;
		}
	}

	public JsonObject filtrarIncidentes(JsonObject dados) throws SQLException, ParseException {
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    JsonArray listaIncidentes = new JsonArray();

	    try {
	        // Construir a consulta SQL
	        String buscaSQL = "SELECT * FROM incidentes WHERE DATE(data) = ? AND TIME(data) BETWEEN ? AND ? AND rodovia = ?";

	        if (dados.has("faixa_km") && !dados.get("faixa_km").isJsonNull() && !dados.get("faixa_km").getAsString().isEmpty()) {
	            List<Integer> faixaKm = Dados.separarNumeros(dados.get("faixa_km").getAsString());
	            if (faixaKm.size() == 2) {
	                buscaSQL += " AND km BETWEEN ? AND ?";
	            }
	        }

	        st = conn.prepareStatement(buscaSQL);

	        // Data - dia
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        java.util.Date utilDate = dateFormat.parse(dados.get("data").getAsString());
	        st.setTimestamp(1, new Timestamp(utilDate.getTime()));

	        // Horario - periodo
	        int periodo = Integer.parseInt(dados.get("periodo").getAsString());
	        List<LocalTime> horarios = Dados.obterHorarios(periodo);
	        st.setTime(2, Time.valueOf(horarios.get(0)));
	        st.setTime(3, Time.valueOf(horarios.get(1)));

	        // Rodovia
	        st.setString(4, dados.get("rodovia").getAsString());

	        // Faixa Km
	        int paramIndex = 5;
	        if (dados.has("faixa_km") && !dados.get("faixa_km").isJsonNull() && !dados.get("faixa_km").getAsString().isEmpty()) {
	            List<Integer> faixaKm = Dados.separarNumeros(dados.get("faixa_km").getAsString());
	            if (faixaKm.size() == 2) {
	                st.setInt(paramIndex++, faixaKm.get(0));
	                st.setInt(paramIndex++, faixaKm.get(1));
	            }
	        }

	        rs = st.executeQuery();

	        // Processar os resultados da consulta
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	        while (rs.next()) {
	            // Construir o objeto JSON com os dados do incidente
	            JsonObject incidenteJson = new JsonObject();
	            incidenteJson.addProperty("id_incidente", rs.getInt("id"));

	            LocalDateTime data = rs.getTimestamp("data").toLocalDateTime();
	            String dataFormatada = data.format(formatter);
	            incidenteJson.addProperty("data", dataFormatada);
	            incidenteJson.addProperty("rodovia", rs.getString("rodovia"));
	            incidenteJson.addProperty("km", rs.getInt("km"));
	            int tipoCodigo = rs.getInt("tipo_incidente");
	            incidenteJson.addProperty("tipo_incidente", tipoCodigo);

	            // Adicionar o objeto JSON à lista de incidentes
	            listaIncidentes.add(incidenteJson);
	        }

	        retorno_servidor.addProperty("codigo", 200);
	        System.out.println("Consulta executada com sucesso.");
	        retorno_servidor.add("lista_incidentes", listaIncidentes);

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Definir código de retorno e mensagem de erro
	        retorno_servidor.addProperty("codigo", 500);
	        mensagem = "Ocorreu um erro na execução da consulta.";
	        System.out.println(mensagem);
	        retorno_servidor.addProperty("mensagem", mensagem);

	    } finally {
	        BancoDados.finalizarStatement(st);
	        BancoDados.finalizarResultSet(rs);
	        BancoDados.desconectar();
	    }

	    return retorno_servidor;
	}


	public JsonObject listarMeusIncidentes(JsonObject dados) throws SQLException, ParseException {
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    JsonArray listaIncidentes = new JsonArray();
	    try {
	        st = conn.prepareStatement("SELECT * FROM incidentes WHERE id_usuario = ?");
	        st.setInt(1, Integer.parseInt(dados.get("id_usuario").getAsString()));
	        rs = st.executeQuery();

	        // Processar os resultados da consulta
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	        while (rs.next()) {
	            // Construir o objeto JSON com os dados do incidente
	            JsonObject incidenteJson = new JsonObject();
	            incidenteJson.addProperty("id_incidente", rs.getInt("id"));

	            LocalDateTime data = rs.getTimestamp("data").toLocalDateTime();
	            String dataFormatada = data.format(formatter);
	            incidenteJson.addProperty("data", dataFormatada);

	            incidenteJson.addProperty("rodovia", rs.getString("rodovia"));
	            incidenteJson.addProperty("km", rs.getInt("km"));
	            int tipoCodigo = rs.getInt("tipo_incidente");
	            incidenteJson.addProperty("tipo_incidente", tipoCodigo);
	            //incidenteJson.addProperty("tipo_incidente", TipoIncidente.getMensagemFromCode(tipoCodigo));

	            // Adicionar o objeto JSON à lista de incidentes
	            listaIncidentes.add(incidenteJson);
	        }

	        retorno_servidor.addProperty("codigo", 200);
	        retorno_servidor.add("lista_incidentes", listaIncidentes);
	        System.out.println("Lista de incidentes reportadas pelo usuario.");

	    } catch (SQLException e) {
	        e.printStackTrace();
	        retorno_servidor.addProperty("codigo", 500);
	        mensagem = "Erro ao listar os incidentes do usuário.";
	        System.out.println(mensagem);
	        retorno_servidor.addProperty("mensagem", mensagem);

	    } finally {
	        BancoDados.finalizarStatement(st);
	        BancoDados.finalizarResultSet(rs);
	        BancoDados.desconectar();
	    }

	    return retorno_servidor;
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
				System.out.println("Incidente excluido com sucesso!");
			} else {
				retorno_servidor.addProperty("codigo", 500);
				mensagem = "Nao foi possivel excluir o incidente. Verifique se o ID do incidente e o ID do usuario estao corretos.";
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
