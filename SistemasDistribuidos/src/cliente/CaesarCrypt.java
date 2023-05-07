package cliente;

public class CaesarCrypt {
        public static String encrypt(String pass) {
        int i;
        int key = pass.length();
        String encripted = "";

        for (i = 0; i < key; i++) {
            encripted = encripted + (char) (pass.charAt(i) + key);
        }

        return (encripted);
    }
}
