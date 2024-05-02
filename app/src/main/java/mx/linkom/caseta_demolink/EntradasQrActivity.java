package mx.linkom.caseta_demolink;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mx.linkom.caseta_demolink.detectPlaca.DetectarPlaca;
import mx.linkom.caseta_demolink.detectPlaca.objectDetectorClass;
import mx.linkom.caseta_demolink.offline.Database.UrisContentProvider;

public class EntradasQrActivity extends mx.linkom.caseta_demolink.Menu {

    JSONArray ja1;
    mx.linkom.caseta_demolink.Configuracion Conf;
    EditText Placas;
    Button Registro,Registro2;

    /*ImageView iconoInternet;
    boolean Offline = false;*/

    LinearLayout LayoutBtnPlaca, FotoPlacaView;
    Button btnFotoPlaca;
    ImageView viewPlaca;
    private mx.linkom.caseta_demolink.detectPlaca.objectDetectorClass objectDetectorClass;
    String rutaImagenPlaca, nombreImagenPlaca;
    Uri uri_img;
    boolean modeloCargado=false;
    JSONArray ja5,ja6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entradasqr);


        Conf = new mx.linkom.caseta_demolink.Configuracion(this);

        Placas = (EditText) findViewById(R.id.editText1);
        Registro = (Button) findViewById(R.id.btnBuscar1);

        LayoutBtnPlaca = (LinearLayout) findViewById(R.id.LayoutBtnPlaca);
        FotoPlacaView = (LinearLayout) findViewById(R.id.FotoPlacaView);
        btnFotoPlaca = (Button) findViewById(R.id.btnFotoPlaca);
        viewPlaca = (ImageView) findViewById(R.id.viewPlaca);

        /*iconoInternet = (ImageView) findViewById(R.id.iconoInternetEntradasQr);

        if (Global_info.getINTERNET().equals("Si")){
            iconoInternet.setImageResource(R.drawable.ic_online);
            Offline = false;
        }else {
            iconoInternet.setImageResource(R.drawable.ic_offline);
            Offline = true;
        }

        iconoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EntradasQrActivity.this);
                    alertDialogBuilder.setTitle(Global_info.getTituloAviso());
                    alertDialogBuilder
                            .setMessage(Global_info.getModoOffline())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EntradasQrActivity.this);
                    alertDialogBuilder.setTitle(Global_info.getTituloAviso());
                    alertDialogBuilder
                            .setMessage(Global_info.getModoOnline())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }
            }
        });*/

        try {
            objectDetectorClass = new objectDetectorClass(getAssets(), "detectPlacaLKM.tflite", "labelmapTf.txt", 320);
            Log.e("EntradasQRActivity", "Modelo cargado correctamente");
            modeloCargado = true;
        } catch (IOException e) {
            modeloCargado = false;
            Log.e("EntradasQRActivity", "Error al cargar modelo");
        }

        menu();

        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placas();

                /*if (Offline){
                    placasOffline();
                }else {
                    placas();
                }*/
            }
        });
        Placas.setFilters(new InputFilter[]{filter, new InputFilter.AllCaps() {
        }});

        Registro2 = (Button) findViewById(R.id.btnBuscar2);

        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placas();

                /*if (Offline){
                    placasOffline();
                }else {
                    placas();
                }*/
            }});

        Registro2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Conf.setTipoReg("Peatonal");
                Conf.setPlacas("");
                if (Conf.getTipoQr().equals("Normal")) {
                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosActivity.class);
                    startActivity(i);
                    finish();
                } else if (Conf.getTipoQr().equals("Multiples")) {
                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosMultiplesActivity.class);
                    startActivity(i);
                    finish();
                } else if (Conf.getTipoQr().equals("Grupal")) {
                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosGrupalActivity.class);
                    startActivity(i);
                    finish();
                }
            }});

        btnFotoPlaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFotoPlacaOffline();
            }
        });
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

    //ALETORIO
    Random primero = new Random();
    int prime = primero.nextInt(9);

    String[] segundo = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    int numRandonsegun = (int) Math.round(Math.random() * 25);

    Random tercero = new Random();
    int tercer = tercero.nextInt(9);

    String[] cuarto = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    int numRandoncuart = (int) Math.round(Math.random() * 25);

    String numero_aletorio = prime + segundo[numRandonsegun] + tercer + cuarto[numRandoncuart];


    //ALETORIO2

    Random primero2 = new Random();
    int prime2 = primero2.nextInt(9);

    String[] segundo2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    int numRandonsegun2 = (int) Math.round(Math.random() * 25);

    Random tercero2 = new Random();
    int tercer2 = tercero2.nextInt(9);

    String[] cuarto2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    int numRandoncuart2 = (int) Math.round(Math.random() * 25);

    String numero_aletorio2 = prime2 + segundo2[numRandonsegun2] + tercer2;


    //ALETORIO3

    Random primero3 = new Random();
    int prime3 = primero3.nextInt(9);

    String[] segundo3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    int numRandonsegun3 = (int) Math.round(Math.random() * 25);

    Random tercero3 = new Random();
    int tercer3 = tercero3.nextInt(9);

    String[] cuarto3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    int numRandoncuart3 = (int) Math.round(Math.random() * 25);

    String numero_aletorio3 = prime3 + segundo3[numRandonsegun3] + tercer3 + cuarto3[numRandoncuart3];

    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);

    public void menu() {
        String URL = "https://demo.kap-adm.mx/plataforma/casetaV2/controlador/dm_access/menu.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
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
        String URL = "https://demo.kap-adm.mx/plataforma/casetaV2/controlador/dm_access/menu_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {
                    int $arreglo[] = {0};
                    try {
                        ja6 = new JSONArray($arreglo);
                        Global.setFotoPlaca(false);
                        imagenes();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja6 = new JSONArray(response);
                            Log.e("ja6", response);
                            if (ja6.getString(10).trim().equals("1")){
                                Global.setFotoPlaca(true);
                            }else {
                                Global.setFotoPlaca(false);
                            }
                            imagenes();
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

    public void imagenes(){

        try {
            Log.e("FOTOPLACA", ja6.getString(10));
            Log.e("FOTOPLACA", ja6.getString(11));
            if (ja6.getString(10).equals("1")) {
                Log.e("OK", ja6.getString(11));
                LayoutBtnPlaca.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void imgFotoPlacaOffline(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                nombreImagenPlaca = "appPlaca"+anio+mes+dia+"-"+numero_aletorio+numero_aletorio2+numero_aletorio3+".png";
                foto= new File(getApplication().getExternalFilesDir(null),nombreImagenPlaca);
                rutaImagenPlaca = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradasQrActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {

                uri_img= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img);
                startActivityForResult(intentCaptura, 3);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            if (requestCode == 3) {

                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/"+nombreImagenPlaca);
                if (modeloCargado){
                    String txtPlaca = DetectarPlaca.getTextFromImage(DetectarPlaca.reconocerPlaca(bitmap, objectDetectorClass, 1), EntradasQrActivity.this);

                    Log.e("PLACA", txtPlaca);
                    if (!txtPlaca.isEmpty())  Placas.setText(txtPlaca);
                }

                Matrix matrix = new Matrix();
                matrix.postRotate(90);

                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                bitmap = rotatedBitmap;

                bitmap = DetectarPlaca.fechaHoraFoto(bitmap);

                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(rutaImagenPlaca);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // compress and save as JPEG
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                FotoPlacaView.setVisibility(View.VISIBLE);
                viewPlaca.setVisibility(View.VISIBLE);
                viewPlaca.setImageBitmap(bitmap);

            }
        }
    }

    public void placasOffline() {

        if (Placas.getText().toString().equals("")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradasQrActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if (Placas.getText().toString().equals(" ")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradasQrActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();

        } else {

            try {
                String placas = Placas.getText().toString().trim();
                String id_resid = Conf.getResid().trim();

                String parametros[] = {id_resid, placas};

                Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS, null, "consulta1", parametros, null);

                Conf.setTipoReg("Auto");
                if (cursor.moveToFirst()){
                    try {
                        ja1 = new JSONArray();
                        ja1.put(cursor.getString(0));
                        ja1.put(cursor.getString(1));
                        ja1.put(cursor.getString(2));
                        ja1.put(cursor.getString(3));
                        ja1.put(cursor.getString(4));
                        ja1.put(cursor.getString(5));
                        ja1.put(cursor.getString(6));
                        ja1.put(cursor.getString(7));
                        ja1.put(cursor.getString(8));
                        ja1.put(cursor.getString(9));
                        ja1.put(cursor.getString(10));
                        ja1.put(cursor.getString(11));
                        ja1.put(cursor.getString(12));
                        ja1.put(cursor.getString(13));
                        ja1.put(cursor.getString(14));
                        ja1.put(cursor.getString(15));

                        Conf.setPlacas(ja1.getString(9));
                        Conf.setIdPre(ja1.getString(2));


                        if (Conf.getTipoQr().equals("Normal")) {
                            Log.e("tipoPlaca", "Normal");
                            Intent i = new Intent(getApplicationContext(), PreEntradasQrActivity.class);
                            startActivity(i);
                            finish();
                        } else if (Conf.getTipoQr().equals("Multiples")) {
                            Log.e("tipoPlaca", "Multiples");
                            Intent i = new Intent(getApplicationContext(), PreEntradasMultiplesQrActivity.class);
                            startActivity(i);
                            finish();
                        } else if (Conf.getTipoQr().equals("Grupal")) {
                            Log.e("tipoPlaca", "Grupal");
                            Intent i = new Intent(getApplicationContext(), PreEntradasGrupalActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Conf.setPlacas(Placas.getText().toString().trim());

                    if (Conf.getTipoQr().equals("Normal")) {
                        Log.e("tipoPlaca", "Normal no existe");
                        Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                        startActivity(i);
                        finish();
                    } else if (Conf.getTipoQr().equals("Multiples")) {
                        Log.e("tipoPlaca", "Multiples no existe");
                        Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                        startActivity(i);
                        finish();
                    } else if (Conf.getTipoQr().equals("Grupal")) {
                        Log.e("tipoPlaca", "Grupal no existe");
                        Intent i = new Intent(getApplicationContext(), AccesosGrupalActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
                cursor.close();
            }catch (Exception ex){

            }

        }

    }

    public void placas() {

        if (Placas.getText().toString().equals("")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradasQrActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if (Placas.getText().toString().equals(" ")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradasQrActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();

        } else {

            String url = "https://demo.kap-adm.mx/plataforma/casetaV2/controlador/dm_access/vst_reg_4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Conf.setTipoReg("Auto");

                    if (response.equals("error")) {
                        Conf.setPlacas(Placas.getText().toString().trim());

                        if (Conf.getTipoQr().equals("Normal")) {
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosActivity.class);
                            i.putExtra("rutaDispositivo", rutaImagenPlaca);
                            i.putExtra("nombreFotoPlaca", nombreImagenPlaca);
                            i.putExtra("btnPlacas", "1");
                            startActivity(i);
                            finish();
                        } else if (Conf.getTipoQr().equals("Multiples")) {
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosMultiplesActivity.class);
                            i.putExtra("rutaDispositivo", rutaImagenPlaca);
                            i.putExtra("nombreFotoPlaca", nombreImagenPlaca);
                            i.putExtra("btnPlacas", "1");
                            startActivity(i);
                            finish();
                        } else if (Conf.getTipoQr().equals("Grupal")) {
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosGrupalActivity.class);
                            i.putExtra("rutaDispositivo", rutaImagenPlaca);
                            i.putExtra("nombreFotoPlaca", nombreImagenPlaca);
                            i.putExtra("btnPlacas", "1");
                            startActivity(i);
                            finish();
                        }


                    } else {
                        response = response.replace("][", ",");
                        if (response.length() > 0) {
                            try {
                                ja1 = new JSONArray(response);

                                Conf.setPlacas(ja1.getString(9));
                                Conf.setIdPre(ja1.getString(2));


                                if (Conf.getTipoQr().equals("Normal")) {
                                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.PreEntradasQrActivity.class);
                                    i.putExtra("rutaDispositivo", rutaImagenPlaca);
                                    i.putExtra("nombreFotoPlaca", nombreImagenPlaca);
                                    i.putExtra("btnPlacas", "1");
                                    startActivity(i);
                                    finish();
                                } else if (Conf.getTipoQr().equals("Multiples")) {
                                    Intent i = new Intent(getApplicationContext(), PreEntradasMultiplesQrActivity.class);
                                    i.putExtra("rutaDispositivo", rutaImagenPlaca);
                                    i.putExtra("nombreFotoPlaca", nombreImagenPlaca);
                                    i.putExtra("btnPlacas", "1");
                                    startActivity(i);
                                    finish();
                                } else if (Conf.getTipoQr().equals("Grupal")) {
                                    Intent i = new Intent(getApplicationContext(), PreEntradasGrupalActivity.class);
                                    i.putExtra("rutaDispositivo", rutaImagenPlaca);
                                    i.putExtra("nombreFotoPlaca", nombreImagenPlaca);
                                    i.putExtra("btnPlacas", "1");
                                    startActivity(i);
                                    finish();
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
                    params.put("Placas", Placas.getText().toString().trim());
                    params.put("id_residencial", Conf.getResid().trim());

                    return params;
                }
            };


        requestQueue.add(stringRequest);
    }

}



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.EntradasSalidasActivity.class);
        startActivity(intent);
        finish();
    }


}
