package mx.linkom.caseta_demolink;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mx.linkom.caseta_demolink.Componentes.LoadingProcess;
import mx.linkom.caseta_demolink.detectPlaca.DetectarPlaca;
import mx.linkom.caseta_demolink.detectPlaca.objectDetectorClass;
import mx.linkom.caseta_demolink.offline.Database.UrisContentProvider;

public class EscaneoVisitaSalidaActivity extends mx.linkom.caseta_demolink.Menu {
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";
    FloatingActionButton fabReiniciar;
    TextView tvRespusta;
    Configuracion Conf;
    EditText qr, placas;
    Button Buscar, Buscar1;
    JSONArray ja1, ja2, ja3, ja4;
    LinearLayout rlOtro;

    Button Lector;
    LinearLayout Qr, Qr2;

    /*ImageView iconoInternet;
    boolean Offline = false;*/

    LinearLayout LayoutBtnPlaca, FotoPlacaView;
    Button btnFotoPlaca;
    ImageView viewPlaca;
    private mx.linkom.caseta_demolink.detectPlaca.objectDetectorClass objectDetectorClass;
    String rutaImagenPlaca, nombreImagenPlaca;
    Uri uri_img;
    boolean modeloCargado=false;
    JSONArray ja5,ja6,ja7;
    Spinner spinnerPlacas;
    ArrayList<String> arrayPlacas;

    LoadingProcess loadingProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaneo_visita_salidas);
        Conf = new Configuracion(this);
        placas = (EditText) findViewById(R.id.editText1);
        Buscar1 = (Button) findViewById(R.id.btnBuscar1);
        placas.setFilters(new InputFilter[]{filter, new InputFilter.AllCaps() {
        }});
        Qr = (LinearLayout) findViewById(R.id.qr);
        Qr2 = (LinearLayout) findViewById(R.id.qr2);
        Lector = (Button) findViewById(R.id.btnLector);

        loadingProcess = new LoadingProcess(this);

        qr = (EditText) findViewById(R.id.editText);
        Buscar = (Button) findViewById(R.id.btnBuscar);
        rlOtro = (LinearLayout) findViewById(R.id.rlOtro);

        LayoutBtnPlaca = (LinearLayout) findViewById(R.id.LayoutBtnPlaca);
        FotoPlacaView = (LinearLayout) findViewById(R.id.FotoPlacaView);
        btnFotoPlaca = (Button) findViewById(R.id.btnFotoPlaca);
        viewPlaca = (ImageView) findViewById(R.id.viewPlaca);

        spinnerPlacas = (Spinner) findViewById(R.id.spinerPlacasParaSalidas);
        spinnerPlacas.setEnabled(false);
        arrayPlacas = new ArrayList<String>();
        Global.setBuscarPorPlaca(false);


        // qr.setFilters(new InputFilter[] { filter,new InputFilter.AllCaps() {
        //} });

        /*iconoInternet = (ImageView) findViewById(R.id.iconoInternetEscaneoVisitaSalidas);

        if (Global_info.getINTERNET().equals("Si")) {
            iconoInternet.setImageResource(R.drawable.ic_online);
            Offline = false;
        } else {
            iconoInternet.setImageResource(R.drawable.ic_offline);
            Offline = true;
        }

        iconoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline) {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
                    alertDialogBuilder.setTitle(Global_info.getTituloAviso());
                    alertDialogBuilder
                            .setMessage(Global_info.getModoOffline())
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                } else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
                    alertDialogBuilder.setTitle(Global_info.getTituloAviso());
                    alertDialogBuilder
                            .setMessage(Global_info.getModoOnline())
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }
            }
        });*/


        try {
            objectDetectorClass = new objectDetectorClass(getAssets(), "detectPlacaLKM.tflite", "labelmapTf.txt", 320);
            Log.e("MainActivity", "Modelo cargado correctamente");
            modeloCargado = true;
        } catch (IOException e) {
            modeloCargado = false;
            Log.e("MainActivity", "Error al cargar modelo");
        }

        menu();

        btnFotoPlaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFotoPlacaOffline();
            }
        });


        Lector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Qr.setVisibility(View.VISIBLE);
                Qr2.setVisibility(View.VISIBLE);
            }
        });


        Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QR_codigo();

                /*if (Offline) {
                    QR_codigoOffline();
                } else {
                    QR_codigo();
                }*/
            }
        });
        Buscar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placas();

                /*if (Offline) {
                    placasOffline();
                } else {
                    placas();
                }*/
            }
        });

        Conf = new Configuracion(this);
        Conf.setQR(null);
        Conf.setST(null);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        tvRespusta = (TextView) findViewById(R.id.tvRespuesta);

        initQR();
        rlOtro.setVisibility(View.VISIBLE);

        buscarPlacas();


    }

    private void buscarPlacas() {
        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/placasHoy.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                Log.e("Placas", response);

                if (response.length() > 0) {
                    try {
                        ja7 = new JSONArray(response);
                        cargarSpinner();
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
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
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

    public void cargarSpinner(){


        try{
            for (int i=0;i<ja7.length();i+=1){
                arrayPlacas.add(ja7.getString(i+0));
            }

            Collections.sort(arrayPlacas);

            if (ja7.length() > 0){
                spinnerPlacas.setEnabled(true);
                arrayPlacas.add(0,"Seleccionar..");
                arrayPlacas.add(1,"Seleccionar...");
            }else {
                arrayPlacas.add(0,"No se econtraron placas");
            }


            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,arrayPlacas);
            spinnerPlacas.setAdapter(adapter1);
            spinnerPlacas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(spinnerPlacas.getSelectedItem().equals("Seleccionar..")){
                        arrayPlacas.remove(0);
                    }else if(spinnerPlacas.getSelectedItem().equals("Seleccionar...")){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("No selecciono ninguna placa...")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                }).create().show();
                    }else{
                        Log.e("CALLE", spinnerPlacas.getSelectedItem().toString());

                        loadingProcess.show();

                        placas.setText(spinnerPlacas.getSelectedItem().toString());

                        if (spinnerPlacas.getSelectedItem().toString() != "No se econtraron placas" ){
                            placas();
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
        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/menu.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
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
        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/menu_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
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
                            if (ja6.getString(9).trim().equals("1")){
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
            Log.e("FOTOPLACA", ja6.getString(9));
            if (ja6.getString(10).equals("1")) {
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
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
                    String txtPlaca = DetectarPlaca.getTextFromImage(DetectarPlaca.reconocerPlaca(bitmap, objectDetectorClass, 1), EscaneoVisitaSalidaActivity.this);

                    Log.e("PLACA", txtPlaca);
                    if (!txtPlaca.isEmpty())  placas.setText(txtPlaca);
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


    public void initQR() {

        // Creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // Creo la camara
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        // Listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // Verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(EscaneoVisitaSalidaActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Verificamos la version de ANdroid que sea al menos la M para mostrar
                        // El dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // Preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // Obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    // Verificamos que el token anterior no se igual al actual
                    // Esto es util para evitar multiples llamadas empleando el mismo token
                    if (!token.equals(tokenanterior)) {

                        // Guardamos el ultimo token proceado
                        tokenanterior = token;
                        Log.i("Token", token);

                        if (URLUtil.isValidUrl(token)) {

                            Conf.setQR(token);
                            QR();

                            /*if (Offline) {
                                QROffline();
                            } else {
                                QR();
                            }*/
                        } else {
                            Conf.setQR(token);
                            QR();

                            /*if (Offline) {
                                QROffline();
                            } else {
                                QR();
                            }*/
                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // Limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        });
    }

    public void placasOffline() {
        Log.e("info", "PlacasOffline");
        if (placas.getText().toString().equals("")) {

            placas.setText("");
            rlOtro.setVisibility(View.VISIBLE);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if (placas.getText().toString().equals(" ")) {

            placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();

        } else {
            try {
                String placa = placas.getText().toString().trim();
                String id_residencial = Conf.getResid().trim();
                String parametros[] = {id_residencial, placa};

                Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS, null, "consulta1", parametros, null);

                if (cursor.moveToFirst()) {
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

                    placas2Offline(ja1.getString(2));

                } else {
                    rlOtro.setVisibility(View.VISIBLE);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Placa inexistente en modo offline")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();
                }

                cursor.close();

            } catch (Exception ex) {
                Log.e("Exception", ex.toString());
            }
        }
    }


    public void placas() {
        if (placas.getText().toString().equals("")) {

            loadingProcess.cancel();

            placas.setText("");
            rlOtro.setVisibility(View.VISIBLE);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if (placas.getText().toString().equals(" ")) {

            loadingProcess.cancel();

            placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();

        } else {
            String url = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/vst_php8.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    if (response.equals("error")) {
                        loadingProcess.cancel();
                        rlOtro.setVisibility(View.VISIBLE);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Placa Inexistente")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                }).create().show();

                    } else {

                        response = response.replace("][", ",");
                        if (response.length() > 0) {

                            try {
                                ja1 = new JSONArray(response);
                                Conf.setPlacas(placas.getText().toString().trim());
                                placas2(ja1.getString(2));
                            } catch (JSONException e) {
                                // placas.setText("");
                            }
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingProcess.cancel();
                    Log.e("TAG", "Error: " + error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Placas", placas.getText().toString().trim());
                    params.put("id_residencial", Conf.getResid().trim());

                    return params;
                }
            };

            requestQueue.add(stringRequest);
        }
    }

    public void placas2Offline(final String id_visita) {

        try {
            String id_residencial = Conf.getResid().trim();
            String visita = id_visita.trim();
            String parametros[] = {visita, id_residencial};

            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_VISITA, null, "vst_php9", parametros, null);

            if (cursor.moveToFirst()) {
                ja2 = new JSONArray();
                ja2.put(cursor.getString(0));
                ja2.put(cursor.getString(1));
                ja2.put(cursor.getString(2));
                ja2.put(cursor.getString(3));
                ja2.put(cursor.getString(4));
                ja2.put(cursor.getString(5));
                ja2.put(cursor.getString(6));
                ja2.put(cursor.getString(7));
                ja2.put(cursor.getString(8));
                ja2.put(cursor.getString(9));
                ja2.put(cursor.getString(10));
                ja2.put(cursor.getString(11));
                ja2.put(cursor.getString(12));
                ja2.put(cursor.getString(13));
                ja2.put(cursor.getString(14));
                ja2.put(cursor.getString(15));


                String sCadena = ja2.getString(12);
                String palabra = sCadena.substring(0, 1);

                if (ja2.getString(5).equals("2")) {
                    Conf.setST("Aceptado");
                    Conf.setQR(ja2.getString(12));
                    Log.e("EscaneoVisitaSalida", "AccesosMultiplesSalidasActivity");
                    Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else if (palabra.equals("M")) {
                    Conf.setST("Aceptado");
                    Conf.setQR(ja2.getString(12));
                    Log.e("EscaneoVisitaSalida", "AccesosMultiplesSalidasActivity2");
                    Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Conf.setST("Aceptado");
                    Conf.setQR(ja2.getString(12));
                    Log.e("EscaneoVisitaSalida", "AccesosSalidasActivity");
                    Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                    startActivity(i);
                    finish();
                }
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al obtener datos")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), EscaneoVisitaSalidaActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }
    }

    public void placas2(final String id_visita) {
        String url = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/vst_php9.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja2 = new JSONArray(response);
                        String sCadena = ja2.getString(12);
                        String palabra = sCadena.substring(0, 1);

                        if (ja2.getString(5).equals("2")) {
                            Conf.setST("Aceptado");
                            Conf.setQR(ja2.getString(12));
                            Global.setBuscarPorPlaca(true);
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else if (palabra.equals("M")) {
                            Conf.setST("Aceptado");
                            Conf.setQR(ja2.getString(12));
                            Global.setBuscarPorPlaca(true);
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Conf.setST("Aceptado");
                            Conf.setQR(ja2.getString(12));
                            Global.setBuscarPorPlaca(true);
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosSalidasActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } catch (JSONException e) {
                        loadingProcess.cancel();
                        e.printStackTrace();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingProcess.cancel();
                Log.e("TAG", "Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_visita", id_visita.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    public void QROffline() {
        try {
            String qr = Conf.getQR();
            String id_residencial = Conf.getResid().trim();

            String[] parametros = {qr, id_residencial};
            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_VISITA, null, "vst1", parametros, null);

            if (cursor.moveToFirst()) {
                ja3 = new JSONArray();
                ja3.put(cursor.getString(0));
                ja3.put(cursor.getString(1));
                ja3.put(cursor.getString(2));
                ja3.put(cursor.getString(3));
                ja3.put(cursor.getString(4));
                ja3.put(cursor.getString(5));
                ja3.put(cursor.getString(6));
                ja3.put(cursor.getString(7));
                ja3.put(cursor.getString(8));
                ja3.put(cursor.getString(9));
                ja3.put(cursor.getString(10));
                ja3.put(cursor.getString(11));
                ja3.put(cursor.getString(12));
                ja3.put(cursor.getString(13));
                ja3.put(cursor.getString(14));
                ja3.put(cursor.getString(15));

                String sCadena = Conf.getQR().trim();
                String palabra = sCadena.substring(0, 1);

                Log.e("Info", "" + ja3.getString(6).length());
                Log.e("Info", "" + ja3.getString(6));
                if (ja3.getString(6).length() > 0) {
                    Conf.setEvento(ja3.getString(6));
                    Conf.setST("Aceptado");
                    Log.e("EscaneoVisitaSalida", "ListaGrupalSalidaActivity");
                    Intent i = new Intent(getApplicationContext(), ListaGrupalSalidaActivity.class);
                    startActivity(i);
                    finish();
                } else if (ja3.getString(5).equals("2")) {
                    Conf.setST("Aceptado");
                    Log.e("EscaneoVisitaSalida", "AccesosMultiplesSalidasActivity");
                    Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else if (palabra.equals("M")) {
                    Conf.setST("Aceptado");
                    Log.e("EscaneoVisitaSalida", "AccesosMultiplesSalidasActivity");
                    Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Conf.setST("Aceptado");
                    Log.e("EscaneoVisitaSalida", "AccesosSalidasActivity");
                    Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                    startActivity(i);
                    finish();
                }
            } else {
                Conf.setST("Denegado");
                Log.e("EscaneoVisitaSalida", "AccesosSalidasActivity1");
                Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                startActivity(i);
                finish();
            }
            cursor.close();

        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }
    }

    public void QR() {
        String url = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/vst_php1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {
                    Conf.setST("Denegado");

                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    response = response.replace("][", ",");

                    try {
                        ja3 = new JSONArray(response);
                        String sCadena = Conf.getQR().trim();
                        String palabra = sCadena.substring(0, 1);

                        if (ja3.getString(6).length() > 0) {
                            Conf.setEvento(ja3.getString(6));
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), ListaGrupalSalidaActivity.class);
                            startActivity(i);
                            finish();
                        } else if (ja3.getString(5).equals("2")) {
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else if (palabra.equals("M")) {
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosSalidasActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ", "Id: " + error.toString());
            }
        }) {
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

    public void QR_codigoOffline() {

        try {
            String cod_qr = qr.getText().toString().trim();
            String id_residencial = Conf.getResid().trim();

            String[] parametros = {cod_qr, id_residencial};
            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_VISITA, null, "vst1", parametros, null);

            if (cursor.moveToFirst()) {
                ja4 = new JSONArray();
                ja4.put(cursor.getString(0));
                ja4.put(cursor.getString(1));
                ja4.put(cursor.getString(2));
                ja4.put(cursor.getString(3));
                ja4.put(cursor.getString(4));
                ja4.put(cursor.getString(5));
                ja4.put(cursor.getString(6));
                ja4.put(cursor.getString(7));
                ja4.put(cursor.getString(8));
                ja4.put(cursor.getString(9));
                ja4.put(cursor.getString(10));
                ja4.put(cursor.getString(11));
                ja4.put(cursor.getString(12));
                ja4.put(cursor.getString(13));
                ja4.put(cursor.getString(14));
                ja4.put(cursor.getString(15));


                String sCadena = qr.getText().toString().trim();
                String palabra = sCadena.substring(0, 1);

                if (ja4.getString(6).length() > 0) {
                    Conf.setEvento(ja4.getString(6));
                    Conf.setQR(qr.getText().toString().trim());
                    Conf.setST("Aceptado");
                    Intent i = new Intent(getApplicationContext(), ListaGrupalSalidaActivity.class);
                    startActivity(i);
                    finish();
                } else if (ja4.getString(5).equals("2")) {

                    Conf.setST("Aceptado");
                    Conf.setQR(qr.getText().toString().trim());
                    Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else if (palabra.equals("M")) {
                    Conf.setST("Aceptado");
                    Conf.setQR(qr.getText().toString().trim());

                    Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Conf.setST("Aceptado");
                    Conf.setQR(qr.getText().toString().trim());

                    Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                    startActivity(i);
                    finish();
                }
            } else {
                Conf.setST("Denegado");

                Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                startActivity(i);
                finish();
            }
            cursor.close();
        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }
    }

    public void QR_codigo() {

        String url = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/vst_php1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response.equals("error")) {
                    Conf.setST("Denegado");

                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    response = response.replace("][", ",");

                    try {
                        ja4 = new JSONArray(response);
                        String sCadena = qr.getText().toString().trim();
                        String palabra = sCadena.substring(0, 1);

                        if (ja4.getString(6).length() > 0) {
                            Conf.setEvento(ja4.getString(6));
                            Conf.setQR(qr.getText().toString().trim());
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), ListaGrupalSalidaActivity.class);
                            startActivity(i);
                            finish();
                        } else if (ja4.getString(5).equals("2")) {

                            Conf.setST("Aceptado");
                            Conf.setQR(qr.getText().toString().trim());
                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else if (palabra.equals("M")) {
                            Conf.setST("Aceptado");
                            Conf.setQR(qr.getText().toString().trim());

                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Conf.setST("Aceptado");
                            Conf.setQR(qr.getText().toString().trim());

                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.AccesosSalidasActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ", "Id: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("QR", qr.getText().toString().trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.EntradasSalidasActivity.class);
        startActivity(intent);
        finish();
    }


}
