package clienteerro;

import com.google.gson.JsonObject;

public class ClienteVazio {

	JsonObject jsonObject = new JsonObject();
	String texto = "";

	public JsonObject JsonVazio(int operacao) {
		jsonObject.addProperty("id_operacao", operacao);
		switch (operacao) {

		case 1: // Cadastrar usuário
		case 2: // Atualizar cadastro

			//jsonObject.addProperty("nome", texto);
			jsonObject.addProperty("email", texto);
			jsonObject.addProperty("senha", texto);
			if (operacao == 2) {
				jsonObject.addProperty("token", texto);
				jsonObject.addProperty("id_usuario", texto);
			}
			break;

		case 3: // Logar no sistema

			jsonObject.addProperty("email", texto);
			//jsonObject.addProperty("senha", texto);
			break;

		case 4: // Reportar incidente
		case 10: // Editar incidente

			jsonObject.addProperty("data", texto);
			jsonObject.addProperty("rodovia", texto);
			jsonObject.addProperty("km", texto);
			//jsonObject.addProperty("tipo_incidente", texto);
			jsonObject.addProperty("token", texto);
			jsonObject.addProperty("id_usuario", texto);
			if (operacao == 10)
				jsonObject.addProperty("id_incidente", texto);
			break;

		case 5: // Solicitar lista de incidentes

			jsonObject.addProperty("rodovia", texto);
			jsonObject.addProperty("data", texto);
			// jsonObject.addProperty("faixa_km", texto);
			jsonObject.addProperty("periodo", texto);
			break;

		case 6: // Solicitar meus incidentes
		case 7: // Remover incidentes
		case 8: // Remover cadastro do usuario
		case 9: // Fazer logout

			if (operacao == 8) {
				jsonObject.addProperty("email", texto);
				jsonObject.addProperty("senha", texto);
			}
			jsonObject.addProperty("token", texto);
			if (operacao == 7)
				jsonObject.addProperty("id_incidente", texto);
			//jsonObject.addProperty("id_usuario", texto);
			break;

		}
		return jsonObject;

	}
}
