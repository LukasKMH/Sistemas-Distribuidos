package cliente;

public class CaesarCrypt {
	public static String encrypt(String pswd) {

		String hashed = "";
		for (int i = 0; i < pswd.length(); i++) {
			char c = pswd.charAt(i);
			int asciiValue = (int) c;
			int novoAsciiValue = asciiValue + pswd.length();
			if (novoAsciiValue > 127) {
				novoAsciiValue = novoAsciiValue - 127 + 32;
			}
			char novoCaractere = (char) novoAsciiValue;
			hashed += novoCaractere;
		}
		return hashed;
	}
}
