package servidor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Dados {

	public static List<LocalTime> obterHorarios(int periodo) {
		List<LocalTime> horarios = null;

		switch (periodo) {
		case 1:
			horarios = List.of(LocalTime.parse("06:00:00"), LocalTime.parse("11:59"));
			break;
		case 2:
			horarios = List.of(LocalTime.parse("12:00:00"), LocalTime.parse("17:59"));
			break;
		case 3:
			horarios = List.of(LocalTime.parse("18:00:00"), LocalTime.parse("23:59"));
			break;
		case 4:
			horarios = List.of(LocalTime.parse("00:00:00"), LocalTime.parse("05:59"));
			break;
		}

		return horarios;
	}

	public static List<Integer> separarNumeros(String texto) {
		List<Integer> faixaKms = new ArrayList<>();

		if (texto != null && texto.contains("-")) {
			String[] partes = texto.split("-");
			for (String parte : partes) {
				try {
					int numero = Integer.parseInt(parte);
					faixaKms.add(numero);
				} catch (NumberFormatException e) {
					// Caso haja algum erro na conversão, pode ser tratado aqui
					e.printStackTrace();
				}
			}
		}

		return faixaKms;
	}

}
