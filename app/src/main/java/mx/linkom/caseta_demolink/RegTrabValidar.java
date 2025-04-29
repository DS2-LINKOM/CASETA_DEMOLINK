package mx.linkom.caseta_demolink;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

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

public class RegTrabValidar extends mx.linkom.caseta_demolink.Menu {

    EditText editTextClaveElector;
    Button btnBuscarTrabajador;
    Configuracion Conf;
    ImageView ImageViewClaveElector;
    TextView TextViewMensajeClaveElector;
    LinearLayout LinLayEditTextClaveTrab, LinLayBtnBuscarTrabajador;
    JSONArray ja1, ja2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_trab_validar);

        Conf = new Configuracion(this);

        editTextClaveElector = (EditText) findViewById(R.id.editTextClaveElector);
        editTextClaveElector.setFilters(new InputFilter[]{filter, new InputFilter.AllCaps() {
        }});
        btnBuscarTrabajador = (Button) findViewById(R.id.btnBuscarTrabajador);

        btnBuscarTrabajador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarTrabajador();
            }
        });

        ImageViewClaveElector = (ImageView) findViewById(R.id.ImageViewClaveElector);
        TextViewMensajeClaveElector = (TextView) findViewById(R.id.TextViewMensajeClaveElector);
        LinLayEditTextClaveTrab = (LinearLayout) findViewById(R.id.LinLayEditTextClaveTrab);
        LinLayBtnBuscarTrabajador = (LinearLayout) findViewById(R.id.LinLayBtnBuscarTrabajador);


        validarLimite();
    }

    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isSpaceChar(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };

    private void validarTrabajador() {
        if (editTextClaveElector.getText().toString().trim().isEmpty()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabValidar.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Clave de elector no valida")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create().show();
        }else {
            String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/reg_traba10.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    Log.e("INE", response);

                    if (response.equals("error")){
                        Intent i = new Intent(getApplication(), RegTrabActivity.class);
                        i.putExtra("num_ine", editTextClaveElector.getText().toString().trim());
                        startActivity(i);
                        finish();
                    }else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabValidar.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Ya existe un trabajador con la clave de elector.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create().show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", "Error: " + error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    params.put("numero_ine", editTextClaveElector.getText().toString().trim());

                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
        Log.e("trab", editTextClaveElector.getText().toString().trim());
    }



    private void validarLimite() {

            String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/reg_traba8.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    if (response.equals("error")){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabValidar.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Ocurrio un error. Por favor verifique su conexión e intentelo más tarde")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplication(), RegTrab2Activity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();
                    }else {

                        response = response.replace("][",",");
                        if (response.length()>0) {
                            try {
                                ja1 = new JSONArray(response);
                                Log.e("responseLimite", ja1.getString(2));
                                cantidadTrabajadores();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", "Error: " + error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());

                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }


    private void cantidadTrabajadores() {

        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/reg_traba9.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("error")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabValidar.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Ocurrio un error. Por favor verifique su conexión e intentelo más tarde")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplication(), RegTrab2Activity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                }else {

                    response = response.replace("][",",");
                    if (response.length()>0) {
                        try {
                            ja2 = new JSONArray(response);
                            Log.e("cantTraba", ja2.getString(0));

                            if (ja2.getInt(0) >= ja1.getInt(2)){ //YA SUPERO EL LIMITE
                                TextViewMensajeClaveElector.setText("Se ha superado el límite actual de trabajadores, pongase en contacto con administración para solicitar más trabajadores");
                            }else {//PUEDE REGISTRAR
                                ImageViewClaveElector.setVisibility(View.VISIBLE);
                                TextViewMensajeClaveElector.setText("Ejemplo de clave de elector");
                                LinLayEditTextClaveTrab.setVisibility(View.VISIBLE);
                                LinLayBtnBuscarTrabajador.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), RegTrab2Activity.class);
        startActivity(intent);
        finish();
    }

}