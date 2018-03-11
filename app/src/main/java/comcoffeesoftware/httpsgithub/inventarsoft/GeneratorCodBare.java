package comcoffeesoftware.httpsgithub.inventarsoft;

/**
 * Cod pentru verificarea valorii introduse ca si cod al produsului si pentru transformarea lui in cod EAN13 in format binar
 */

public class GeneratorCodBare {

    // Generare cod binar EAN13
    public static String codCompletBinarizat(String stringCod) {
        stringCod = codCompletNebinarizat(stringCod);
        stringCod = binarizare(stringCod);
        return stringCod;
    }

    // Generare cod EAN13 in format decimal
    public static String codCompletNebinarizat(String stringCod) {
        if (stringCod == null || stringCod.isEmpty()) return "NU_EXISTA_STRING";
        int length = stringCod.length();
        if (length > 9) return "STRING_PREA_LUNG";
        for (int i = length; i < 9; i++) {
            stringCod = 7 + stringCod;
        }
        stringCod = 594 + stringCod;
        stringCod = stringCod + obtineCifraControl(stringCod);
        return stringCod;
    }

    // Transformare din decimal in binar conform standardului EAN13
    private static String binarizare(String stringCod) {
        // Eliminare cifra initiala
        stringCod = stringCod.substring(1, 13);

        // copie a stringului cu codul
        String stringCodCopy = stringCod;

        // Cod de inceput
        stringCod = "101";
        // 42 cifre binare
        stringCod = stringCod + codificareL(Integer.parseInt(stringCodCopy.substring(0, 1)));
        stringCod = stringCod + codificareG(Integer.parseInt(stringCodCopy.substring(1, 2)));
        stringCod = stringCod + codificareG(Integer.parseInt(stringCodCopy.substring(2, 3)));
        stringCod = stringCod + codificareL(Integer.parseInt(stringCodCopy.substring(3, 4)));
        stringCod = stringCod + codificareL(Integer.parseInt(stringCodCopy.substring(4, 5)));
        stringCod = stringCod + codificareG(Integer.parseInt(stringCodCopy.substring(5, 6)));
        // Separator de mijloc
        stringCod = stringCod + "01010";
        // 42 cifre binare
        stringCod = stringCod + codificareR(Integer.parseInt(stringCodCopy.substring(6, 7)));
        stringCod = stringCod + codificareR(Integer.parseInt(stringCodCopy.substring(7, 8)));
        stringCod = stringCod + codificareR(Integer.parseInt(stringCodCopy.substring(8, 9)));
        stringCod = stringCod + codificareR(Integer.parseInt(stringCodCopy.substring(9, 10)));
        stringCod = stringCod + codificareR(Integer.parseInt(stringCodCopy.substring(10, 11)));
        stringCod = stringCod + codificareR(Integer.parseInt(stringCodCopy.substring(11, 12)));
        // Cod final
        stringCod = stringCod + "101";

        return stringCod;
    }

    // Codificare dupa grila R
    private static String codificareR(int x) {
        switch (x) {
            case 0:
                return "1110010";
            case 1:
                return "1100110";
            case 2:
                return "1101100";
            case 3:
                return "1000010";
            case 4:
                return "1011100";
            case 5:
                return "1001110";
            case 6:
                return "1010000";
            case 7:
                return "1000100";
            case 8:
                return "1001000";
            case 9:
                return "1110100";
            default:
                return "1110010";
        }
    }

    // Codificare dupa grila L
    private static String codificareL(int x) {
        switch (x) {
            case 0:
                return "0001101";
            case 1:
                return "0011001";
            case 2:
                return "0010011";
            case 3:
                return "0111101";
            case 4:
                return "0100011";
            case 5:
                return "0110001";
            case 6:
                return "0101111";
            case 7:
                return "0111011";
            case 8:
                return "0110111";
            case 9:
                return "0001011";
            default:
                return "0001101";
        }
    }

    // Codificare dupa grila G
    private static String codificareG(int x) {
        switch (x) {
            case 0:
                return "0100111";
            case 1:
                return "0110011";
            case 2:
                return "0011011";
            case 3:
                return "0100001";
            case 4:
                return "0011101";
            case 5:
                return "0111001";
            case 6:
                return "0000101";
            case 7:
                return "0010001";
            case 8:
                return "0001001";
            case 9:
                return "0010111";
            default:
                return "0100111";
        }
    }

    // Generare cifra control
    private static int obtineCifraControl(String string12Cifre) {
        int[] arrayCod = new int[12];
        for (int i = 0; i < 12; i++) {
            arrayCod[i] = Character.getNumericValue(string12Cifre.charAt(i));
        }
        int s1 = 0;
        int s2 = 0;
        for (int i = 0; i < 11; i = i + 2) {
            s1 = s1 + arrayCod[i];
            s2 = s2 + arrayCod[i + 1];
        }

        int s = s1 + 3 * s2;
        int cifraControl = 10 - (s % 10);
        return cifraControl;
    }

}
