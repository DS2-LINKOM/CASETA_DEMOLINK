package mx.linkom.caseta_demolink;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mx.linkom.caseta_demolink.adaptadores.ListasClassGrid;
import mx.linkom.caseta_demolink.adaptadores.ListasClassReservaciones;
import mx.linkom.caseta_demolink.adaptadores.adaptador_Modulo;

public class ListaReservacionesActivity extends mx.linkom.caseta_demolink.Menu {

    private Configuracion Conf;
    private GridView gridList;
    private TextView tituloReservaciones;
    private LinearLayout linLayoutMensajeNoHayReservaciones;
    JSONArray ja1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reservaciones);

        Conf = new Configuracion(this);
        gridList = (GridView) findViewById(R.id.gridList);
        tituloReservaciones = (TextView) findViewById(R.id.tituloReservaciones);
        linLayoutMensajeNoHayReservaciones = (LinearLayout) findViewById(R.id.linLayoutMensajeNoHayReservaciones);

        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

        tituloReservaciones.setText(nombre.toUpperCase(Locale.ROOT));

        reservaciones();
    }

    public void reservaciones() {
        String URL = "https://demo.linkom.mx/plataforma/casetaV2/controlador/dm_access/reservaciones_mes.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        //String URL = "http://192.168.0.110/Android/reservaciones_mes.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.trim().equals("error")){
                    linLayoutMensajeNoHayReservaciones.setVisibility(View.VISIBLE);
                }else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja1 = new JSONArray(response);
                            llenado();
                        } catch (JSONException e) {
                            alertaError("Alerta", "Error de conexión \n\nCódigo de error: LRmrJA1\n\nFavor de intentar más tarde", "Aceptar");
                            e.printStackTrace();
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertaError("Alerta", "Error de conexión \n\nCódigo de error: LRmrVE2\n\nFavor de intentar más tarde", "Aceptar");
                Log.e("Error ", "Id: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void llenado() {
        ArrayList<ListasClassReservaciones> reservaciones = new ArrayList<ListasClassReservaciones>();

        for (int i = 0; i < ja1.length(); i += 12) {
            try {
                reservaciones.add(new ListasClassReservaciones(ja1.getString(i + 3), ja1.getString(i + 6) + " " + ja1.getString(i + 7), "UP: " + ja1.getString(i + 11), Integer.parseInt(ja1.getString(i + 0))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_item_lista_reservaciones, reservaciones) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    final TextView amenidad = (TextView) view.findViewById(R.id.amenidad);
                    if (amenidad != null)
                        amenidad.setText(((ListasClassReservaciones) entrada).getAmenidad());

                    final TextView fecha = (TextView) view.findViewById(R.id.fecha);
                    if (fecha != null)
                        fecha.setText(((ListasClassReservaciones) entrada).getFecha());

                    final TextView up = (TextView) view.findViewById(R.id.unidadPrivativa);
                    if (up != null)
                        up.setText(((ListasClassReservaciones) entrada).getUp());

                    gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.ReservacionActivity.class);
                            i.putExtra("id_amenidad", reservaciones.get(position).getId_amenidad());
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void alertaError(String titulo, String mensaje, String textoBtn){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListaReservacionesActivity.this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder
                .setMessage(mensaje)
                .setPositiveButton(textoBtn,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), ReportesActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.ReportesActivity.class);
        startActivity(intent);
        finish();
    }
}