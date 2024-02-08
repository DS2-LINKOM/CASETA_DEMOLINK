package mx.linkom.caseta_demolink;

public class Global {

    public static String TOKEN = "";
    public static String EMAIL = "";
    public static String USER = "";
    public static String PASS = "";

    public static String VERSION_APP = "24.23.38";
    public static boolean FOTO_PLACA = false;

    public static boolean BUSCAR_POR_PLACA = false;

    public static boolean getFotoPlaca() {
        return FOTO_PLACA;
    }

    public static void setFotoPlaca(boolean fotoPlaca) {
        FOTO_PLACA = fotoPlaca;
    }

    public static String getVersionApp() {
        return VERSION_APP;
    }

    public static boolean isBuscarPorPlaca() {
        return BUSCAR_POR_PLACA;
    }

    public static void setBuscarPorPlaca(boolean buscarPorPlaca) {
        BUSCAR_POR_PLACA = buscarPorPlaca;
    }
}
