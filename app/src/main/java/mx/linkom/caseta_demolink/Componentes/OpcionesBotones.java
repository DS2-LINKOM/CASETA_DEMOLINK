package mx.linkom.caseta_demolink.Componentes;

import android.widget.Button;
import android.widget.ImageButton;

import mx.linkom.caseta_demolink.R;

public class OpcionesBotones {

    public static void botonPresionado(Button button, int estado) {
        //estado --> 0=presionado   1=restablecer

        if (estado == 0) {
            button.setBackgroundResource(R.drawable.btn_presionado);
            button.setTextColor(0xFF5A6C81);
            button.setEnabled(false);
        } else if (estado == 1) {
            button.setBackgroundResource(R.drawable.ripple_effect);
            button.setTextColor(0xFF27374A);
            button.setEnabled(true);
        }
    }

    public static void imageBotonPresionado(ImageButton button, int estado) {
        //estado --> 0=presionado   1=restablecer

        if (estado == 0) {
            button.setBackgroundResource(R.drawable.btn_presionado);
        } else if (estado == 1) {
            button.setBackgroundResource(R.drawable.ripple_effect);
            button.setEnabled(true);
        }
    }

    public static void botonInicioSesionPresionado(Button button, int estado) {
        //estado --> 0=presionado   1=restablecer
        if (estado == 0) {
            button.setBackgroundResource(R.drawable.btn_presionado_inicio_sesion);
            button.setTextColor(0xFF5A6C81);
        } else if (estado == 1) {
            button.setBackgroundResource(R.drawable.ripple_effect2);
            button.setTextColor(0xFF27374A);
            button.setEnabled(true);
        }
    }


}
