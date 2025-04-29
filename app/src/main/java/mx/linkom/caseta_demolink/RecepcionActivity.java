package mx.linkom.caseta_demolink;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import mx.linkom.caseta_demolink.detectPlaca.DetectarPlaca;
import mx.linkom.caseta_demolink.offline.Database.UrisContentProvider;
import mx.linkom.caseta_demolink.offline.Global_info;
import mx.linkom.caseta_demolink.offline.Servicios.subirFotos;

public class RecepcionActivity extends mx.linkom.caseta_demolink.Menu {

    private mx.linkom.caseta_demolink.Configuracion Conf;
    FirebaseStorage storage;
    StorageReference storageReference;
    JSONArray ja1, ja2, ja3, ja4, ja5, ja6;
    Spinner Calle, Numero;
    ArrayList<String> calles, numero;
    LinearLayout Numero_o;

    EditText comen;
    Button foto, Registrar;
    ImageView ViewFoto;
    LinearLayout View, BtnReg, espacio, espacio2;

    ProgressDialog pd, pd2;
    int fotos;
    Bitmap bitmap;
    String usuario, nombre, correo, token, notificacion;
    Uri uri_img;

    String rutaImagen1 = "", nombreImagen1 = "";

    private ImageButton btnMicrofonoComentarios;
    private static final int TXT_COMENTARIOS = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepcion);
        Conf = new mx.linkom.caseta_demolink.Configuracion(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        calles = new ArrayList<String>();
        numero = new ArrayList<String>();

        Calle = (Spinner) findViewById(R.id.setCalle);
        Numero = (Spinner) findViewById(R.id.setNumero);
        Numero_o = (LinearLayout) findViewById(R.id.numero);
        Numero_o.setVisibility(View.GONE);
        calles();

        comen = (EditText) findViewById(R.id.setComent);
        comen.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        foto = (Button) findViewById(R.id.foto);
        Registrar = (Button) findViewById(R.id.btnRegistrar);
        View = (LinearLayout) findViewById(R.id.View);
        BtnReg = (LinearLayout) findViewById(R.id.BtnReg);
        espacio = (LinearLayout) findViewById(R.id.espacio);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        ViewFoto = (ImageView) findViewById(R.id.viewFoto);

        btnMicrofonoComentarios = (ImageButton) findViewById(R.id.btnMicrofonoComentarios);
        btnMicrofonoComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarEntradVoz("Diga los comentarios para esta correspondencia", TXT_COMENTARIOS);
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotos = 1;
                imgFoto();
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Registrando...");

        pd2 = new ProgressDialog(this);
        pd2.setMessage("Subiendo Imagen.");

        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registrar.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        botonPresionado(0);
                        Validacion();
                    }
                }, 300);
            }
        });
        Numero_o.setVisibility(View.VISIBLE);
        cargarSpinner3();

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

    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);


    //IMAGEN FOTO

    private void iniciarEntradVoz(String promt, int campo) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, promt);

        intent.putExtra("FIELD_EXTRA", campo);

        try {
            startActivityForResult(intent, campo);
        } catch (ActivityNotFoundException e) {
            Log.e("RECTETXT", e.toString());
        }
    }

    public void imgFoto() {
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto = null;
            try {
                nombreImagen1 = "app" + dia + mes + anio + "-" + numero_aletorio + ".png";
                foto = new File(getApplication().getExternalFilesDir(null), nombreImagen1);
                rutaImagen1 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {

                uri_img = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT, uri_img);
                startActivityForResult(intentCaptura, 0);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0 && resultCode == RESULT_OK) {


            Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/" + nombreImagen1);

            bitmap = DetectarPlaca.fechaHoraFoto(bitmap);

            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(rutaImagen1);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // compress and save as JPEG
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ViewFoto.setVisibility(View.VISIBLE);
            ViewFoto.setImageBitmap(bitmap);
            View.setVisibility(View.VISIBLE);
            espacio.setVisibility(View.VISIBLE);
            BtnReg.setVisibility(View.VISIBLE);
            espacio2.setVisibility(View.VISIBLE);


        }

        if (requestCode == TXT_COMENTARIOS && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String txtAnterior = " " + comen.getText() + " " + result.get(0);
            comen.setText(txtAnterior);
        }
    }


    public void Validacion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea notificar la correspondencia ?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        pd.show();
                        Datos();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        botonPresionado(1);

                        /*Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                        startActivity(i);
                        finish();*/
                    }
                }).setCancelable(false).create().show();
    }

    public void calles() {

        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/correspondencia_1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
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

    public void cargarSpinner() {


        try {
            calles.add("Seleccionar..");
            calles.add("Seleccionar...");

            for (int i = 0; i < ja1.length(); i += 1) {
                calles.add(ja1.getString(i + 0));
            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, calles);
            Calle.setAdapter(adapter1);
            Calle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (Calle.getSelectedItem().equals("Seleccionar..")) {
                        calles.remove(0);
                    } else if (Calle.getSelectedItem().equals("Seleccionar...")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("No selecciono ninguna calle...")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                }).create().show();
                    } else {
                        numero.clear();
                        numeros(Calle.getSelectedItem().toString());
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void numeros(final String IdUsu) {

        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/correspondencia_2.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja2 = new JSONArray(response);
                        cargarSpinner2();
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
                params.put("calle", IdUsu);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void cargarSpinner2() {

        numero.add("Seleccionar...");

        try {
            for (int i = 0; i < ja2.length(); i += 1) {
                numero.add(ja2.getString(i + 0));
            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, numero);
            Numero.setAdapter(adapter1);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarSpinner3() {

        numero.add("Seleccionar...");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, numero);
        Numero.setAdapter(adapter1);
    }

    public void Datos() {

        if (Calle.getSelectedItem().equals("Seleccionar..") || Calle.getSelectedItem().equals("Seleccionar...") || Numero.getSelectedItem().equals("Seleccionar...")) {

            pd.dismiss();
            botonPresionado(1);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("No selecciono ninguna calle o número...")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else {

            String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/correspondencia_3.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    if (response.equals("error")) {
                        pd.dismiss();
                        botonPresionado(1);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Está UP no esta habitada")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.CorrespondenciaActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();
                    } else {
                        try {
                            ja3 = new JSONArray(response);
                            Registrar();
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", "Error: " + error.toString());
                    botonPresionado(1);
                    alertaErrorAlRegistrar("Error al registrar \n\nNo se ha podido establecer comunicación con el servidor, inténtelo de nuevo");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("calle", Calle.getSelectedItem().toString());
                    params.put("numero", Numero.getSelectedItem().toString());
                    params.put("id_residencial", Conf.getResid().trim());


                    return params;
                }
            };
            requestQueue.add(stringRequest);

        }

    }


    public void Registrar() {
        String URL = "https://demo.elkm.mx/plataforma/casetaV2/controlador/dm_access/correspondencia_4.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {
                    pd.dismiss();
                    botonPresionado(1);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Registro de Correspondencia No Exitoso")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.CorrespondenciaActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                } else {

                    if (Global_info.getCantidadFotosEnEsperaEnSegundoPlano(RecepcionActivity.this) >= Global_info.getLimiteFotosSegundoPlano()) {
                        upload1();
                    } else {
                        //Registrar fotos en SQLite
                        ContentValues val_img1 = ValuesImagen(nombreImagen1, Conf.getPin() + "/correspondencia/" + nombreImagen1.trim(), rutaImagen1);
                        Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);
                    }

                    pd.dismiss();
                    terminar(response);
                    //upload1(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
                botonPresionado(1);
                alertaErrorAlRegistrar("Error al registrar \n\nNo se ha podido establecer comunicación con el servidor, inténtelo de nuevo");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                try {
                    usuario = ja3.getString(0);
                    nombre = ja3.getString(1) + " " + ja3.getString(2) + " " + ja3.getString(3);
                    correo = ja3.getString(4);
                    token = ja3.getString(5);
                    notificacion = ja3.getString(6);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", usuario);
                params.put("guardia", Conf.getUsu().trim());
                params.put("comen", comen.getText().toString().trim());
                params.put("foto_recep", nombreImagen1);
                params.put("nombre", nombre);
                params.put("correo", correo);
                params.put("token", token);
                params.put("id_residencial", Conf.getResid().trim());
                params.put("nom_residencial", Conf.getNomResi().trim());
                params.put("notificacion", notificacion);

                return params;

            }
        };
        requestQueue.add(stringRequest);


    }

    public ContentValues ValuesImagen(String nombre, String rutaFirebase, String rutaDispositivo) {
        ContentValues values = new ContentValues();
        values.put("titulo", nombre);
        values.put("direccionFirebase", rutaFirebase);
        values.put("rutaDispositivo", rutaDispositivo);
        return values;
    }

    public void upload1() {
        StorageReference mountainImagesRef = null;
        mountainImagesRef = storageReference.child(Conf.getPin() + "/correspondencia/" + nombreImagen1);

        Uri uri = Uri.fromFile(new File(rutaImagen1));
        UploadTask uploadTask = mountainImagesRef.putFile(uri);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                pd2.show(); // double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
                // Toast.makeText(getApplicationContext(),"Cargando Imagen INE " + progress + "%", Toast.LENGTH_SHORT).show();

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(RecepcionActivity.this, "Fallado", Toast.LENGTH_SHORT).show();
                pd2.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                eliminarFotoDirectorioLocal(nombreImagen1);
                pd2.dismiss();

            }
        });
    }

    public void eliminarFotoDirectorioLocal(String nombreFoto) {
        String tempfilepath = "";
        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir != null) {
            tempfilepath = externalFilesDir.getAbsolutePath();
            try {
                File grTempFiles = new File(tempfilepath);
                if (grTempFiles.exists()) {
                    File[] files = grTempFiles.listFiles();
                    if (grTempFiles.isDirectory() && files != null) {
                        int numofFiles = files.length;

                        for (int i = 0; i < numofFiles; i++) {
                            try {
                                File path = new File(files[i].getAbsolutePath());
                                if (!path.isDirectory() && path.getName().equals(nombreFoto)) {
                                    path.delete();
                                }
                            } catch (Exception e) {
                                Log.e("Eliminar Foto", e.toString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ErrorFile", "deleteDirectory: Failed to onCreate directory  " + tempfilepath + " for an unknown reason.");

            }

        } else {
        }
    }

    public void terminar(String resp) {
        if (Global_info.getCantidadFotosEnEsperaEnSegundoPlano(RecepcionActivity.this) > 0) {
            //Solo ejecutar si el servicio no se esta ejecutando
            if (!servicioFotos()) {
                Intent cargarFotos = new Intent(RecepcionActivity.this, subirFotos.class);
                startService(cargarFotos);
            }
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Registro de Correspondencia  Exitoso FOLIO:" + resp)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }


    //Método para saber si es que el servicio ya se esta ejecutando
    public boolean servicioFotos() {
        //Obtiene los servicios que se estan ejecutando
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //Se recorren todos los servicios obtnidos para saber si el servicio creado ya se esta ejecutando
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (subirFotos.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void botonPresionado(int estado) {
        //estado --> 0=presionado   1=restablecer

        Button button = Registrar;

        if (estado == 0) {
            button.setBackgroundResource(R.drawable.btn_presionado);
            button.setTextColor(0xFF5A6C81);
        } else if (estado == 1) {
            button.setBackgroundResource(R.drawable.ripple_effect);
            button.setTextColor(0xFF27374A);
            button.setEnabled(true);
        }
    }

    public void alertaErrorAlRegistrar(String texto) {
        pd.dismiss();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage(texto)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.CorrespondenciaActivity.class);
        startActivity(intent);
        finish();

    }

}
