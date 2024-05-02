package mx.linkom.caseta_demolink;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ReservacionActivity extends mx.linkom.caseta_demolink.Menu {

    private Configuracion Conf;
    JSONArray ja1;
    TextView textViewFolio, textViewUp, textViewUsuario, textViewAmenidad, textViewFechaReservacion, textViewHorario, textViewComentarios;
    int id_amenidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservacion);

        Conf = new Configuracion(this);

        textViewFolio = (TextView) findViewById(R.id.textViewFolio);
        textViewUsuario = (TextView) findViewById(R.id.textViewUsuario);
        textViewUp = (TextView) findViewById(R.id.textViewUp);
        textViewAmenidad = (TextView) findViewById(R.id.textViewAmenidad);
        textViewFechaReservacion = (TextView) findViewById(R.id.textViewFechaReservacion);
        textViewHorario = (TextView) findViewById(R.id.textViewHorario);
        textViewComentarios = (TextView) findViewById(R.id.textViewComentarios);

        Intent intent = getIntent();
        id_amenidad = intent.getIntExtra("id_amenidad", 0);

        if (id_amenidad != 0){
            obtenerInformacion(id_amenidad);
        }else {
            alertaError("Alerta", "Error de conexión \n\nCódigo de error: RdiIA1\n\nFavor de intentar más tarde", "Aceptar");
        }


    }

    public void obtenerInformacion(final int id_app) {
        String URL = "https://demo.kap-adm.mx/plataforma/casetaV2/controlador/dm_access/reservacion_individual.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {
                    alertaError("Alerta", "Error de conexión \n\nCódigo de error: RmoiER1\n\nFavor de intentar más tarde", "Aceptar");
                    int $arreglo[] = {0};
                    try {
                        ja1 = new JSONArray($arreglo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja1 = new JSONArray(response);

                            textViewFolio.setText(ja1.getString(0));
                            textViewUsuario.setText(ja1.getString(9));
                            textViewUp.setText(ja1.getString(8));
                            textViewAmenidad.setText(ja1.getString(3));
                            textViewFechaReservacion.setText(ja1.getString(5));
                            textViewHorario.setText(ja1.getString(6));
                            textViewComentarios.setText(ja1.getString(7));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertaError("Alerta", "Error de conexión \n\nCódigo de error: RmoiOER2\n\nFavor de intentar más tarde", "Aceptar");
                Log.e("TAG", "Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id_app+"");
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void alertaError(String titulo, String mensaje, String textoBtn){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReservacionActivity.this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder
                .setMessage(mensaje)
                .setPositiveButton(textoBtn,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), ListaReservacionesActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ListaReservacionesActivity.class);
        startActivity(intent);
        finish();
    }
}