package mx.linkom.caseta_demolink;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccesosTrabajadorSalidaActivity extends mx.linkom.caseta_demolink.Menu {
    mx.linkom.caseta_demolink.Configuracion Conf;
    LinearLayout rlPermitido, rlDenegado,rlVista;
    TextView tvMensaje;
    JSONArray ja1,ja2,ja3,ja4,ja5;
    Date FechaA;
    TextView Nombre,Puesto,Vigencia;
    Button Registrar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_accesostrabajadorsalida);

        Conf = new mx.linkom.caseta_demolink.Configuracion(this);
        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);
        tvMensaje = (TextView)findViewById(R.id.setMensaje);

        Nombre = (TextView)findViewById(R.id.setNombre);
        Puesto = (TextView)findViewById(R.id.setPuesto);
        Vigencia = (TextView)findViewById(R.id.setVigencia);
        Registrar = (Button)findViewById(R.id.registrar);


        if(Conf.getST().equals("Aceptado")){
            Trabajador();
        }else if(Conf.getST().equals("Denegado")){
            rlPermitido.setVisibility(View.GONE);
            rlVista.setVisibility(View.GONE);
            rlDenegado.setVisibility(View.VISIBLE);
            tvMensaje.setText("QR Inexistente");

        }

        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registrar.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        botonPresionado(0);
                        Registrar();
                    }
                }, 300);
            }
        });


    }
    public void Trabajador(){

        String url = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/tbj_php1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {

                        ja1 = new JSONArray(response);
                        Dtl(ja1.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Codigo", Conf.getQR().trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




    public void Dtl(final String id_traba){
        String url = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/tbj_php2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    if (response.trim().equals("error")){
                        int $arreglo[]={0,0,0,0,0,0,0,0,0};
                        ja2 = new JSONArray($arreglo);
                        ValidarQR();

                    }else{
                        ja2 = new JSONArray(response);
                        ValidarQR();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_trabajador", id_traba.trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public void ValidarQR() throws JSONException {

        try {
            Calendar c = Calendar.getInstance();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = (Date)formatter.parse(ja1.getString(17));

            FechaA = Calendar.getInstance().getTime();

            //Vigencia de Trabajador
            if(c.getTime().before(date)) {

                //Acceso de Trabajador
                if (ja1.getString(20).equals("1")) {

                    if (ja2.getString(8).equals("1")) {

                        rlVista.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.VISIBLE);
                        rlDenegado.setVisibility(View.GONE);

                        Nombre.setText(ja1.getString(6));
                        Puesto.setText(ja1.getString(12));
                        Vigencia.setText(ja1.getString(17));
                    }else if(ja2.getString(8).equals("2")){

                        rlVista.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.GONE);
                        rlDenegado.setVisibility(View.VISIBLE);

                        rlDenegado.setVisibility(View.VISIBLE);
                        tvMensaje.setText("El trabajador ya salio");
                    }else if(ja2.getString(8).equals("0")){

                        rlVista.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.GONE);
                        rlDenegado.setVisibility(View.VISIBLE);

                        rlDenegado.setVisibility(View.VISIBLE);
                        tvMensaje.setText("El trabajador no ha entrado");
                    }
                }else {
                    rlVista.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.GONE);
                    rlDenegado.setVisibility(View.VISIBLE);

                    rlDenegado.setVisibility(View.VISIBLE);
                    tvMensaje.setText("Trabajador Desactivado");
                }

            }else {
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.GONE);
                rlDenegado.setVisibility(View.VISIBLE);
                tvMensaje.setText("Vigencia Vencida");

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void Registrar(){


        String url = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/tbj_php4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response){


                if(response.equals("error")){
                    botonPresionado(1);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesosTrabajadorSalidaActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Salida de Trabajador No Exitosa")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.EntradasSalidasActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesosTrabajadorSalidaActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Salida de Trabajador Exitosa")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.EntradasSalidasActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
                botonPresionado(1);
                alertaErrorAlRegistrar("Error al registrar visita \n\nNo se ha podido establecer comunicación con el servidor, inténtelo de nuevo");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();
                try {

                    params.put("id", ja2.getString(0).trim());
                    params.put("guardia", Conf.getUsu().trim());
                } catch (JSONException e) {
                    Log.e("TAG","Error: " + e.toString());
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void botonPresionado(int estado){
        //estado --> 0=presionado   1=restablecer

        Button button = Registrar;

        if (estado == 0){
            button.setBackgroundResource(R.drawable.btn_presionado);
            button.setTextColor(0xFF5A6C81);
        }else if (estado == 1){
            button.setBackgroundResource(R.drawable.ripple_effect);
            button.setTextColor(0xFF27374A);
            button.setEnabled(true);
        }
    }

    public void alertaErrorAlRegistrar(String texto){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesosTrabajadorSalidaActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage(texto)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create().show();
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.EntradasSalidasActivity.class);
        startActivity(intent);
        finish();
    }



}
