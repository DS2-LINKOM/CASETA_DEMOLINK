package mx.linkom.caseta_demolink;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EntregaFolio  extends mx.linkom.caseta_demolink.Menu {
    private mx.linkom.caseta_demolink.Configuracion Conf;
    EditText folio;
    Button buscar;
    JSONArray ja1, ja2;

    Spinner spinnerFolios;
    ArrayList<String> arrayFolios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entregafolio);
        Conf = new mx.linkom.caseta_demolink.Configuracion(this);
        folio = (EditText) findViewById(R.id.setFolio);
        buscar = (Button) findViewById(R.id.btnBuscar);

        spinnerFolios = (Spinner) findViewById(R.id.spinerFoliosEntrega);
        spinnerFolios.setEnabled(false);
        arrayFolios = new ArrayList<String>();

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }});

        listaFolios();

    }

    public void listaFolios() {
        String url = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/foliosHoy.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.e("FOLIOS", response);

                response = response.replace("][", ",");
                Log.e("Placas", response);

                if (response.length() > 0) {
                    try {
                        ja2 = new JSONArray(response);
                        cargarSpinner();
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
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void cargarSpinner(){


        try{
            for (int i=0;i<ja2.length();i+=1){
                arrayFolios.add(ja2.getString(i+0));
            }

            Collections.sort(arrayFolios);

            if (ja2.length() > 0){
                spinnerFolios.setEnabled(true);
                arrayFolios.add(0,"Seleccionar..");
                arrayFolios.add(1,"Seleccionar...");
            }else {
                arrayFolios.add(0,"No se econtraron folios");
            }


            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,arrayFolios);
            spinnerFolios.setAdapter(adapter1);
            spinnerFolios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(spinnerFolios.getSelectedItem().equals("Seleccionar..")){
                        arrayFolios.remove(0);
                    }else if(spinnerFolios.getSelectedItem().equals("Seleccionar...")){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaFolio.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("No selecciono ninguna placa...")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                }).create().show();
                    }else{
                        Log.e("CALLE", spinnerFolios.getSelectedItem().toString());

                        folio.setText(spinnerFolios.getSelectedItem().toString());

                        if (spinnerFolios.getSelectedItem().toString() != "No se econtraron folios" ){
                            check();
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void check() {
        String url = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/correspondencia_5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);

                        Conf.setPlacas(ja1.getString(0));
                        Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.EntregaActivity.class);
                        startActivity(i);
                        finish();
                    } catch (JSONException e) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaFolio.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("No existe folio")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplicationContext(),CorrespondenciaActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();


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
                params.put("Folio", folio.getText().toString().trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
        startActivity(intent);
        finish();
    }


}
