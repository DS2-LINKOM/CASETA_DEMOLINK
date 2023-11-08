package mx.linkom.caseta_demolink.offline;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

import mx.linkom.caseta_demolink.offline.Database.UrisContentProvider;

public class Global_info extends Application {
    private static boolean INTERNET_DISPOSITIVO = false;
    private static int SEGUNDOS = 0;
    private static String ULTIMA_ACTUALIZACION = "No se ha registrado ninguna actualización";
    private static String MODO_OFFLINE = "Equipo sin conexión a internet \n" + "Trabajando fuera de línea";
    private static String MODO_ONLINE = "Equipo conectado a internet \n" + "Actualizando datos";
    private static String TITULO_AVISO = "Aviso";
    private static String INTERNET = "Si";
    private static String TEXTO1_IMAGENES = "Cargando fotografía";
    private static String TEXTO2_IMAGENES = "Fotografía pendiente por subir";
    private static String TEXTO3_IMAGENES = "Fotografía no disponible en offline";
    //private static String URL = "http://192.168.0.110/android/demoCaseta/";
    private static String URL = "https://demo.linkom.mx/plataforma/casetaV2/controlador/dm_access/off-line/";

    private static int LIMITE_FOTOS_SEGUNDO_PLANO = 0;

    public static int getLimiteFotosSegundoPlano() {
        return LIMITE_FOTOS_SEGUNDO_PLANO;
    }

    public static int getCantidadFotosEnEsperaEnSegundoPlano(Context context) {
        Cursor cursoFotos = null;
        int cantidadFotos = 0;

        cursoFotos = context.getContentResolver().query(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, null, "cantidad", null, null);

        if (cursoFotos.moveToFirst()) {
            do {
                cantidadFotos = cursoFotos.getInt(0);
            } while (cursoFotos.moveToNext());
        }

        cursoFotos.close();

        return cantidadFotos;
    }

    public static String getTexto3Imagenes() {
        return TEXTO3_IMAGENES;
    }

    public static String getTexto1Imagenes() {
        return TEXTO1_IMAGENES;
    }

    public static String getTexto2Imagenes() {
        return TEXTO2_IMAGENES;
    }

    public static String getModoOffline() {
        return MODO_OFFLINE;
    }

    public static String getModoOnline() {
        return MODO_ONLINE;
    }

    public static String getTituloAviso() {
        return TITULO_AVISO;
    }

    public static String getINTERNET() {
        return INTERNET;
    }

    public static void setINTERNET(String INTERNET) {
        Global_info.INTERNET = INTERNET;
    }

    public boolean getINTERNET_DISPOSITIVO() {
        return INTERNET_DISPOSITIVO;
    }

    public void setINTERNET_DISPOSITIVO(boolean INTERNET_DISPOSITIVO) {
        this.INTERNET_DISPOSITIVO = INTERNET_DISPOSITIVO;
    }

    public static String getULTIMA_ACTUALIZACION() {
        return ULTIMA_ACTUALIZACION;
    }

    public static void setULTIMA_ACTUALIZACION(String ultimaActualizacion) {
        ULTIMA_ACTUALIZACION = ultimaActualizacion;
    }

    public int getSEGUNDOS() {
        return SEGUNDOS;
    }

    public void setSEGUNDOS(int SEGUNDOS) {
        this.SEGUNDOS = SEGUNDOS;
    }

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        Global_info.URL = URL;
    }
}

