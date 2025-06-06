package mx.linkom.caseta_demolink;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_demolink.adaptadores.ModuloClassGrid;
import mx.linkom.caseta_demolink.adaptadores.adaptador_Modulo;

public class ReportesActivity extends mx.linkom.caseta_demolink.Menu {
    private mx.linkom.caseta_demolink.Configuracion Conf;
    JSONArray ja1;

    private GridView gridList,gridList2,gridList3,gridList4,gridList5,gridList6,gridList0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);
        Conf = new mx.linkom.caseta_demolink.Configuracion(this);
        gridList = (GridView)findViewById(R.id.gridList);
        gridList2 = (GridView)findViewById(R.id.gridList2);
        gridList3 = (GridView)findViewById(R.id.gridList3);
        gridList4 = (GridView)findViewById(R.id.gridList4);
        gridList5 = (GridView)findViewById(R.id.gridList5);
        gridList6 = (GridView)findViewById(R.id.gridList6);
        gridList0 = (GridView)findViewById(R.id.gridList0);

    }
    @Override
    public void onStart() {
        super.onStart();
        menu();
    }



    public void menu() {

        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/menu.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                        llenado();
                        llenado2();
                        llenado3();
                        llenado4();
                        llenado5();
                        llenado6();
                        llenadoReservaciones();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Usuario y/o Contraseña Incorrectos", Toast.LENGTH_LONG).show();

                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "NO HAY CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();

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

    public void llenadoReservaciones(){
        ArrayList<ModuloClassGrid> lista = new ArrayList<ModuloClassGrid>();

        Log.e("llenado", "res");
        lista.add(new ModuloClassGrid(R.drawable.ic_calendario_reservaciones,"Reservaciones","#FF4081"));

        /*try {
            if(ja1.getString(6).equals("1")  ){

            }else{

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        gridList0.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            if(position==0) {
                                Intent reservaciones = new Intent(getApplication(), mx.linkom.caseta_demolink.ListaReservacionesActivity.class);
                                startActivity(reservaciones);
                                finish();
                            }
                        }
                    });

                }
            }

        });
    }

    public void llenado(){
        ArrayList<ModuloClassGrid> lista = new ArrayList<ModuloClassGrid>();

        try {
            if(ja1.getString(6).equals("1")  ){
                lista.add(new ModuloClassGrid(R.drawable.ic_baseline_directions_car_24,"Placas","#FF4081"));
            }else{

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            if(position==0) {
                                   Intent placas = new Intent(getApplication(), mx.linkom.caseta_demolink.PlacasActivity.class);
                                    startActivity(placas);
                                    finish();
                            }
                        }
                    });

                }
            }

        });
    }


    public void llenado2(){
        ArrayList<ModuloClassGrid> lista2= new ArrayList<ModuloClassGrid>();

        try {

            if(ja1.getString(7).equals("1")   ){
                lista2.add(new ModuloClassGrid(R.drawable.ic_baseline_assignment_ind_24,"Trabajadores","#FF4081"));
            }else{

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList2.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista2){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            if(position==0) {
                                Intent inci = new Intent(getApplication(), mx.linkom.caseta_demolink.EscaneoTrabajadorActivity.class);
                                startActivity(inci);
                                finish();
                            }
                        }
                    });

                }
            }

        });
    }


    public void llenado3(){
        ArrayList<ModuloClassGrid> lista3 = new ArrayList<ModuloClassGrid>();

        try {

            if(ja1.getString(8).equals("1")  ){
                lista3.add(new ModuloClassGrid(R.drawable.ic_baseline_sports_kabaddi_24,"Incidencias","#4cd2c7"));
            }else{

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList3.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista3){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            if(position==0) {
                               Intent inci = new Intent(getApplication(), mx.linkom.caseta_demolink.IncidenciaActivity.class);
                                startActivity(inci);
                                finish();
                            }
                        }
                    });

                }
            }

        });
    }


    public void llenado4(){
        ArrayList<ModuloClassGrid> lista4 = new ArrayList<ModuloClassGrid>();

        try {

            if(ja1.getString(9).equals("1")  ){
                lista4.add(new ModuloClassGrid(R.drawable.ic_baseline_markunread_mailbox_24,"Paquetería","#FF4081"));
            }else{

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList4.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista4){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            if(position==0) {
                                Intent placas = new Intent(getApplication(), mx.linkom.caseta_demolink.CorrespondenciaActivity.class);
                                startActivity(placas);
                                finish();
                            }
                        }
                    });

                }
            }

        });
    }

    public void llenado5(){
        ArrayList<ModuloClassGrid> lista5 = new ArrayList<ModuloClassGrid>();

        try {

            if(ja1.getString(10).equals("1")  ){
                lista5.add(new ModuloClassGrid(R.drawable.ic_baseline_add_location_24,"Rondines","#FF4081"));
            }else{

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList5.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista5){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            if(position==0) {
                                Intent placas = new Intent(getApplication(), mx.linkom.caseta_demolink.Rondines.class);
                                startActivity(placas);
                                finish();
                            }
                        }
                    });

                }
            }

        });
    }

    public void llenado6(){
        ArrayList<ModuloClassGrid> lista6 = new ArrayList<ModuloClassGrid>();

        //if(Conf.getRegTraba().equals("1")  ) {
        if(Conf.getRegTraba().equals("1")  ) {

            lista6.add(new ModuloClassGrid(R.drawable.ic_trabajadores, "Registro Trabajadores", "#FF4081"));
        }
        gridList6.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista6){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList6.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            if(position==0) {

                                Intent traba = new Intent(getApplication(), RegTrab2Activity.class);
                                startActivity(traba);
                                finish();
                            }
                        }
                    });

                }
            }

        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.DashboardActivity.class);
        startActivity(intent);
        finish();
    }



}