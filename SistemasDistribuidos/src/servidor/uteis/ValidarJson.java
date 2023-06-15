package servidor.uteis;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class ValidarJson {
	static JsonObject resposta_servidor = new JsonObject();

	public static JsonObject verificarCamposCadastro(JsonObject json) {

		if (json.has("nome") && json.has("email") && json.has("senha")) {
			if (!json.get("nome").isJsonNull() && !json.get("email").isJsonNull() && !json.get("senha").isJsonNull()) {
				resposta_servidor.addProperty("codigo", 200);
			} else {
				resposta_servidor.addProperty("codigo", 500);
				resposta_servidor.addProperty("mensagem", "Os campos nome, email e senha nao podem ser nulos.");
			}
		} else {
			resposta_servidor.addProperty("codigo", 500);
			resposta_servidor.addProperty("mensagem", "O arquivo JSON deve conter os campos nome, email e senha.");
		}

		return resposta_servidor;
	}

	public static JsonObject verificarCamposLogin(JsonObject json) {

		if (json.has("email") && json.has("senha")) {
			if (!json.get("email").isJsonNull() && !json.get("senha").isJsonNull()) {
				resposta_servidor.addProperty("codigo", 200);
			} else {
				resposta_servidor.addProperty("codigo", 500);
				resposta_servidor.addProperty("mensagem", "Os campos email e senha nao podem ser nulos.");
			}
		} else {
			resposta_servidor.addProperty("codigo", 500);
			resposta_servidor.addProperty("mensagem", "O arquivo JSON deve conter os campos email e senha.");
		}

		return resposta_servidor;
	}

	public static JsonObject verificarCamposIncidente(JsonObject json) {

		if (json.has("data") && json.has("rodovia") && json.has("km") && json.has("tipo_incidente") && json.has("token")
				&& json.has("id_usuario")) {
			if (!json.get("data").isJsonNull() && !json.get("rodovia").isJsonNull() && !json.get("km").isJsonNull()
					&& !json.get("tipo_incidente").isJsonNull() && !json.get("token").isJsonNull()
					&& !json.get("id_usuario").isJsonNull()) {
				resposta_servidor.addProperty("codigo", 200);
			} else {
				resposta_servidor.addProperty("codigo", 500);
				resposta_servidor.addProperty("mensagem", "O arquivo JSON nao deve conter campos nulos.");
			}
		} else {
			resposta_servidor.addProperty("codigo", 500);
			resposta_servidor.addProperty("mensagem", "Campos obrigatorios faltando.");
		}

		return resposta_servidor;
	}

	public static JsonObject verificarCamposListaIncidentes(JsonObject json) {
		if (json.has("rodovia") && json.has("data") && json.has("periodo")) {
			if (!json.get("rodovia").isJsonNull() && !json.get("data").isJsonNull() && !json.get("periodo").isJsonNull()) {
				if (json.has("faixa_km") && json.get("faixa_km").getAsString().length() > 1) {
					if (!json.get("faixa_km").isJsonNull())
						resposta_servidor.addProperty("codigo", 200);
					else {
						resposta_servidor.addProperty("codigo", 500);
						resposta_servidor.addProperty("mensagem", "O arquivo JSON nao deve conter campos nulos.");
					}
				}
				resposta_servidor.addProperty("codigo", 200);
			} else {
				resposta_servidor.addProperty("codigo", 500);
				resposta_servidor.addProperty("mensagem", "O arquivo JSON nao deve conter campos nulos.");
			}
		} else {
			resposta_servidor.addProperty("codigo", 500);
			resposta_servidor.addProperty("mensagem", "Campos obrigatorios faltando.");
		}
		return resposta_servidor;
	}

	public static JsonObject verificarCamposRemoverIncidente(JsonObject json) {
		if (json.has("token") && json.has("id_incidente") && json.has("id_usuario")) {
			if (!json.get("token").isJsonNull() && !json.get("id_incidente").isJsonNull()
					&& !json.get("id_usuario").isJsonNull()) {
				resposta_servidor.addProperty("codigo", 200);
			} else {
				resposta_servidor.addProperty("codigo", 500);
				resposta_servidor.addProperty("mensagem", "O arquivo JSON nao deve conter campos nulos.");
			}
		} else {
			resposta_servidor.addProperty("codigo", 500);
			resposta_servidor.addProperty("mensagem", "Campos obrigatorios faltando.");
		}
		return resposta_servidor;
	}

	public static JsonObject verificarCamposRemoverCadastro(JsonObject json) {
		if (json.has("email") && json.has("senha") && json.has("token") && json.has("id_usuario")) {
			if (!json.get("email").isJsonNull() && !json.get("senha").isJsonNull() && !json.get("token").isJsonNull()
					&& !json.get("id_usuario").isJsonNull()) {
				resposta_servidor.addProperty("codigo", 200);
			} else {
				resposta_servidor.addProperty("codigo", 500);
				resposta_servidor.addProperty("mensagem", "O arquivo JSON nao deve conter campos nulos.");
			}
		} else {
			resposta_servidor.addProperty("codigo", 500);
			resposta_servidor.addProperty("mensagem", "Campos obrigatorios faltando.");
		}
		return resposta_servidor;
	}

	public static JsonObject verificarCamposLogout(JsonObject json) {
		if (json.has("token") && json.has("id_usuario")) {
			if (!json.get("token").isJsonNull() && !json.get("id_usuario").isJsonNull()) {
				resposta_servidor.addProperty("codigo", 200);
			} else {
				resposta_servidor.addProperty("codigo", 500);
				resposta_servidor.addProperty("mensagem", "O arquivo JSON nao deve conter campos nulos.");
			}
		} else {
			resposta_servidor.addProperty("codigo", 500);
			resposta_servidor.addProperty("mensagem", "Campos obrigatorios faltando.");
		}
		return resposta_servidor;
	}

	public static boolean verificarCodigo(JsonObject json) {
		if (json.has("codigo") && !json.get("codigo").equals(JsonNull.INSTANCE)
				&& Integer.parseInt(json.get("codigo").getAsString()) == 200)
			return true;
		return false;
	}

	public static boolean verificarMensagem(JsonObject json) {
		if (json.has("mensagem") && !json.get("mensagem").equals(JsonNull.INSTANCE))
			return true;
		return false;
	}
}
