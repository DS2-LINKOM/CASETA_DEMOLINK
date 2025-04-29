package mx.linkom.caseta_demolink;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_demolink.offline.Global_info;

public class VisitasRecepSalidasDetalle extends mx.linkom.caseta_demolink.Menu {
    mx.linkom.caseta_demolink.Configuracion Conf;
    FirebaseStorage storage;
    StorageReference storageReference;

    LinearLayout rlPermitido, rlDenegado,rlVista,ContinuarBoton;
    TextView  tvMensaje;
    TextView Nombre,Dire,Visi,Pasajeros,Placas,Tipo,Tel;

    ArrayList<String> names;
    JSONArray ja1,ja2,ja3,ja4,ja5,ja6,ja7;
    Date FechaA;
    String estado;

    Button Registrar,Continuar;
    ImageView view1,view2,view3;
    TextView nombre_foto1,nombre_foto2,nombre_foto3;
    LinearLayout Foto1, Foto2,Foto3,Foto1View,Foto2View,Foto3View,espacio2,espacio3,espacio4,espacio5,espacio6,espacio8,espacio9,espacio10;
    LinearLayout PlacasL;
    EditText Comentarios;

    TextView txtFoto1, txtFoto2, txtFoto3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitasrecepsalidasdetalle);

        Conf = new mx.linkom.caseta_demolink.Configuracion(this);
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        names = new ArrayList<String>();

        Comentarios = (EditText)findViewById(R.id.setComentarios);
        PlacasL = (LinearLayout) findViewById(R.id.PlacasL);
        Foto1 = (LinearLayout) findViewById(R.id.Foto1);
        Foto2 = (LinearLayout) findViewById(R.id.Foto2);
        Foto3 = (LinearLayout) findViewById(R.id.Foto3);
        Foto1View = (LinearLayout) findViewById(R.id.Foto1View);
        Foto2View = (LinearLayout) findViewById(R.id.Foto2View);
        Foto3View = (LinearLayout) findViewById(R.id.Foto3View);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        espacio3 = (LinearLayout) findViewById(R.id.espacio3);
        espacio4 = (LinearLayout) findViewById(R.id.espacio4);
        espacio5 = (LinearLayout) findViewById(R.id.espacio5);
        espacio6 = (LinearLayout) findViewById(R.id.espacio6);
        espacio8 = (LinearLayout) findViewById(R.id.espacio8);
        espacio9 = (LinearLayout) findViewById(R.id.espacio9);
        espacio10 = (LinearLayout) findViewById(R.id.espacio10);
        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);
        ContinuarBoton = (LinearLayout) findViewById(R.id.ContinuarBoton);
        tvMensaje = (TextView)findViewById(R.id.setMensaje);

        Registrar = (Button) findViewById(R.id.Registrar);

        nombre_foto1 = (TextView) findViewById(R.id.nombre_foto1);
        nombre_foto2 = (TextView) findViewById(R.id.nombre_foto2);
        nombre_foto3 = (TextView) findViewById(R.id.nombre_foto3);

        view1 = (ImageView) findViewById(R.id.view1);
        view2 = (ImageView) findViewById(R.id.view2);
        view3 = (ImageView) findViewById(R.id.view3);

        txtFoto1 = (TextView) findViewById(R.id.txtFotoVisitasRecepSalidasDetalle1);
        txtFoto2 = (TextView) findViewById(R.id.txtFotoVisitasRecepSalidasDetalle2);
        txtFoto3 = (TextView) findViewById(R.id.txtFotoVisitasRecepSalidasDetalle3);

        txtFoto1.setText(Global_info.getTexto1Imagenes());
        txtFoto2.setText(Global_info.getTexto1Imagenes());
        txtFoto3.setText(Global_info.getTexto1Imagenes());

        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);
        tvMensaje = (TextView)findViewById(R.id.setMensaje);

        //SI ES ACEPTADO O DENEGAODO
        if(Conf.getST().equals("Aceptado")){
            rlVista.setVisibility(View.VISIBLE);
            rlPermitido.setVisibility(View.GONE);
            rlDenegado.setVisibility(View.GONE);
            menu();
        }else if(Conf.getST().equals("Denegado")){
            rlDenegado.setVisibility(View.VISIBLE);
            rlVista.setVisibility(View.GONE);
            rlPermitido.setVisibility(View.GONE);
            tvMensaje.setText("Placa Inexistente");
        }

        Nombre = (TextView)findViewById(R.id.setNombre);
        Dire = (TextView)findViewById(R.id.setDire);
        Tel = (TextView)findViewById(R.id.setTel);
        Visi = (TextView) findViewById(R.id.setVisi);
        Placas = (TextView) findViewById(R.id.setPlacas);
        Pasajeros = (TextView) findViewById(R.id.setPasajeros);
        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }
        });


        Tipo = (TextView)findViewById(R.id.setTipo);
        Continuar = (Button) findViewById(R.id.continuar);


        Continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), AccesosMorososActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


    public void menu() {
        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/menu.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja5 = new JSONArray(response);
                        submenu(ja5.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public void submenu(final String id_app) {
        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/menu_3.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.equals("error")){
                    int $arreglo[]={0};
                    try {
                        ja6 = new JSONArray($arreglo);
                        Visita();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja6 = new JSONArray(response);
                            Visita();
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
                params.put("id_app", id_app.trim());
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Visita(){

        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/vst_php1.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);

                        Usuario(ja1.getString(2));
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
                params.put("QR", Conf.getQR().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Usuario(final String IdUsu){ //DATOS USUARIO

        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/vst_php2.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja2 = new JSONArray(response);
                        dtlLugar(ja2.getString(0));

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
                params.put("IdUsu", IdUsu.trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void dtlLugar(final String idUsuario){
        String URLResidencial = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/vst_php3.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLResidencial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {
                    sincasa();
                } else {

                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja3 = new JSONArray(response);
                            salidas(ja1.getString(0));


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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", idUsuario.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public void salidas (final String id_visitante){
        String URLResidencial = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/vst_php6_2.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLResidencial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Tipo: " + response);

                try {
                    if (response.trim().equals("error")){

                        String $arreglo[]={"0","0","0"};
                        ja4 = new JSONArray($arreglo);
                        dtl_recpecion();
                    }else{
                        response = response.replace("][",",");
                        ja4 = new JSONArray(response);
                        dtl_recpecion();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_visitante", id_visitante.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public void dtl_recpecion (){
        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/visitas_recep_4.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Tipo: " + response);

                try {
                        response = response.replace("][",",");
                        ja7 = new JSONArray(response);
                        ValidarQR();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("id_visita", ja1.getString(0).trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public void sincasa(){

        rlVista.setVisibility(View.GONE);
        rlPermitido.setVisibility(View.GONE);
        rlDenegado.setVisibility(View.VISIBLE);
        tvMensaje.setText(" No tiene asignada una unidad privativa.");

    }



    public void ValidarQR(){
        try {
            if(ja1.getString(11).equals("0000-00-00 00:00:00")){
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.VISIBLE);
                rlDenegado.setVisibility(View.GONE);

                Nombre.setText(ja2.getString(1) + " " + ja2.getString(2) + " " + ja2.getString(3));
                if(ja1.getString(4).equals("1") || ja1.getString(12).equals("0")){
                    Tipo.setText("Visita");
                }else if(ja1.getString(4).equals("2")){
                    Tipo.setText("Proveedor / Servicios");
                }else if(ja1.getString(4).equals("3")){
                    Tipo.setText("Taxista");
                }
                Dire.setText(ja3.getString(0));
                Visi.setText(ja1.getString(7));
                Pasajeros.setText(ja4.getString(6));
                Tel.setText(ja2.getString(4));



                if(ja4.getString(7).equals("")){
                    PlacasL.setVisibility(View.GONE);

                }else{
                    PlacasL.setVisibility(View.VISIBLE);
                    Placas.setText(ja4.getString(7));
                }
                //FOTO1
                if(ja4.getString(3).equals("")){
                    Foto1.setVisibility(View.GONE);
                    espacio2.setVisibility(View.GONE);
                    Foto1View.setVisibility(View.GONE);
                    espacio3.setVisibility(View.GONE);
                    txtFoto1.setVisibility(View.GONE);

                }else{
                    nombre_foto1.setText(ja6.getString(4)+":");

                    storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(3))
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override

                        public void onSuccess(Uri uri) {
                            Glide.with(VisitasRecepSalidasDetalle.this)
                                    .load(uri)
                                    .error(R.drawable.log)
                                    .centerInside()
                                    .into(view1);

                            txtFoto1.setVisibility(View.GONE);
                            view1.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            txtFoto1.setText(Global_info.getTexto2Imagenes());
                        }
                    });
                }

                //FOTO2
                if(ja4.getString(4).equals("")){
                    Foto2.setVisibility(View.GONE);
                    espacio5.setVisibility(View.GONE);
                    Foto2View.setVisibility(View.GONE);
                    espacio6.setVisibility(View.GONE);
                    txtFoto2.setVisibility(View.GONE);
                }else{
                    nombre_foto2.setText(ja6.getString(6)+":");

                    storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(4))
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override

                        public void onSuccess(Uri uri) {
                            Glide.with(VisitasRecepSalidasDetalle.this)
                                    .load(uri)
                                    .error(R.drawable.log)
                                    .centerInside()
                                    .into(view2);

                            txtFoto2.setVisibility(View.GONE);
                            view2.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            txtFoto2.setText(Global_info.getTexto2Imagenes());
                        }
                    });
                }
                //FOTO3
                if(ja4.getString(5).equals("")){
                    Foto3.setVisibility(View.GONE);
                    espacio8.setVisibility(View.GONE);
                    Foto3View.setVisibility(View.GONE);
                    espacio9.setVisibility(View.GONE);
                    txtFoto3.setVisibility(View.GONE);
                }else{
                    nombre_foto3.setText(ja6.getString(8)+":");

                    storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(5))
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override

                        public void onSuccess(Uri uri) {
                            Glide.with(VisitasRecepSalidasDetalle.this)
                                    .load(uri)
                                    .error(R.drawable.log)
                                    .centerInside()
                                    .into(view3);

                            txtFoto3.setVisibility(View.GONE);
                            view3.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            txtFoto3.setText(Global_info.getTexto2Imagenes());
                        }
                    });
                }

            }else{

                Calendar c = Calendar.getInstance();
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                FechaA = Calendar.getInstance().getTime();
                Date dateentrada = (Date)formatter.parse(ja1.getString(10));
                Date datesalida = (Date)formatter.parse(ja1.getString(11));


                //ANTES DE LA ENTRADA

                        rlVista.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.VISIBLE);
                        rlDenegado.setVisibility(View.GONE);

                        Nombre.setText(ja2.getString(1) + " " + ja2.getString(2) + " " + ja2.getString(3));
                        if(ja1.getString(4).equals("1") || ja1.getString(12).equals("0")){
                            Tipo.setText("Visita");
                        }else if(ja1.getString(4).equals("2")){
                            Tipo.setText("Proveedor / Servicios");
                        }else if(ja1.getString(4).equals("3")){
                            Tipo.setText("Taxista");
                        }
                        Dire.setText(ja3.getString(0));
                        Visi.setText(ja1.getString(7));
                        Pasajeros.setText(ja4.getString(6));
                        Comentarios.setText(ja1.getString(9));
                        Tel.setText(ja2.getString(4));

                        if(ja4.getString(7).equals("")){
                            PlacasL.setVisibility(View.GONE);

                        }else{
                            PlacasL.setVisibility(View.VISIBLE);
                            Placas.setText(ja4.getString(7));
                        }
                        //FOTO1
                        if(ja4.getString(3).equals("")){
                            Foto1.setVisibility(View.GONE);
                            espacio2.setVisibility(View.GONE);
                            Foto1View.setVisibility(View.GONE);
                            espacio3.setVisibility(View.GONE);
                            txtFoto1.setVisibility(View.GONE);

                        }else{
                            nombre_foto1.setText(ja6.getString(4)+":");

                            storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(3))
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override

                                public void onSuccess(Uri uri) {
                                    Glide.with(VisitasRecepSalidasDetalle.this)
                                            .load(uri)
                                            .error(R.drawable.log)
                                            .centerInside()
                                            .into(view1);

                                    txtFoto1.setVisibility(View.GONE);
                                    view1.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    txtFoto1.setText(Global_info.getTexto2Imagenes());
                                }
                            });
                        }

                        //FOTO2
                        if(ja4.getString(4).equals("")){
                            Foto2.setVisibility(View.GONE);
                            espacio5.setVisibility(View.GONE);
                            Foto2View.setVisibility(View.GONE);
                            espacio6.setVisibility(View.GONE);
                            txtFoto2.setVisibility(View.GONE);
                        }else{
                            nombre_foto2.setText(ja6.getString(6)+":");

                            storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(4))
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override

                                public void onSuccess(Uri uri) {
                                    Glide.with(VisitasRecepSalidasDetalle.this)
                                            .load(uri)
                                            .error(R.drawable.log)
                                            .centerInside()
                                            .into(view2);

                                    txtFoto2.setVisibility(View.GONE);
                                    view2.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    txtFoto2.setText(Global_info.getTexto2Imagenes());
                                }
                            });
                        }
                        //FOTO3
                        if(ja4.getString(5).equals("")){
                            Foto3.setVisibility(View.GONE);
                            espacio8.setVisibility(View.GONE);
                            Foto3View.setVisibility(View.GONE);
                            espacio9.setVisibility(View.GONE);
                            txtFoto3.setVisibility(View.GONE);
                        }else{
                            nombre_foto3.setText(ja6.getString(8)+":");

                            storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(5))
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override

                                public void onSuccess(Uri uri) {
                                    Glide.with(VisitasRecepSalidasDetalle.this)
                                            .load(uri)
                                            .error(R.drawable.log)
                                            .centerInside()
                                            .into(view3);

                                    txtFoto3.setVisibility(View.GONE);
                                    view3.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    txtFoto3.setText(Global_info.getTexto2Imagenes());
                                }
                            });
                        }



            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void Validacion() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitasRecepSalidasDetalle.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar la salida ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Registrar();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //Intent i = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
                       // startActivity(i);
                       // finish();
                    }
                }).create().show();
    }


    public void Registrar (){


        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/visitas_recep_5.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.equals("succes")){
                    //Toast.makeText(getApplicationContext(),"Salida Registrada", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitasRecepSalidasDetalle.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Salida Exitosa")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.RecepcionVisitasActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();


                }else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitasRecepSalidasDetalle.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Salida No Exitosa")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.RecepcionVisitasActivity.class);
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
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {



                    params.put("id", ja7.getString(0).trim());
                    params.put("id_vigilante", Conf.getUsu().trim());

                    params.put("usuario",ja2.getString(1).trim() + " " + ja2.getString(2).trim() + " " + ja2.getString(3).trim());
                    params.put("token", ja2.getString(5).trim());
                    params.put("correo",ja2.getString(6).trim());
                    params.put("visita",ja1.getString(7).trim());

                    params.put("id_residencial", Conf.getResid().trim());
                    params.put("comentarios",Comentarios.getText().toString().trim());
                    params.put("id_visita", ja1.getString(0).trim());
                } catch (JSONException e) {
                    Log.e("TAG","Error: " + e.toString());
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);


    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
        startActivity(intent);
        finish();
    }
}
