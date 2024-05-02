package mx.linkom.caseta_demolink;

import android.app.ActivityManager;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Map;
import java.util.Random;

import mx.linkom.caseta_demolink.detectPlaca.DetectarPlaca;
import mx.linkom.caseta_demolink.offline.Database.UrisContentProvider;
import mx.linkom.caseta_demolink.offline.Global_info;
import mx.linkom.caseta_demolink.offline.Servicios.subirFotos;

public class RegTrabActivity extends mx.linkom.caseta_demolink.Menu {

    Button registrar;
    Spinner Tipo, Departamento;
    ArrayList<String> tipo, departamento;
    FirebaseStorage storage;
    StorageReference storageReference;
    JSONArray ja1, ja2, ja3, ja4, ja5, ja6;
    Configuracion Conf;
    EditText nombre,telefono,correo,comentarios,tag;
    LinearLayout Foto1View,espacio2,Foto2,espacio3,Foto2View,espacio4,registrar1,espacio5;
    Button btn_foto1,btn_foto2;
    ImageView view1,view2;
    Uri uri_img,uri_img2;
    int foto;
    String nfoto1,nfoto2;
    ProgressDialog pd, pd2, pd3;

    String rutaImagen1="", rutaImagen2="", rutaImagen3="", rutaImagenPlaca="", nombreImagen1="", nombreImagen2="", nombreImagen3="", nombreImagenPlaca="";

    String numeroIne = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regtrab);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Conf = new Configuracion(this);


        btn_foto1 = (Button) findViewById(R.id.btn_foto1);
        btn_foto2 = (Button) findViewById(R.id.btn_foto2);
        view1 = (ImageView) findViewById(R.id.view1);
        view2 = (ImageView) findViewById(R.id.view2);

        Foto1View = (LinearLayout) findViewById(R.id.Foto1View);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        Foto2 = (LinearLayout) findViewById(R.id.Foto2);
        espacio3 = (LinearLayout) findViewById(R.id.espacio3);
        Foto2View = (LinearLayout) findViewById(R.id.Foto2View);
        espacio4 = (LinearLayout) findViewById(R.id.espacio4);
        registrar1 = (LinearLayout) findViewById(R.id.registrar1);
        espacio5 = (LinearLayout) findViewById(R.id.espacio5);

        tipo = new ArrayList<String>();
        departamento = new ArrayList<String>();
        registrar = (Button) findViewById(R.id.registrar);
        Tipo = (Spinner) findViewById(R.id.setTipo);
        Departamento = (Spinner) findViewById(R.id.setDepa);
        nombre = (EditText) findViewById(R.id.setNombre);
        telefono = (EditText) findViewById(R.id.setTel);
        correo = (EditText) findViewById(R.id.setCorreo);
        comentarios = (EditText) findViewById(R.id.setComen);
        tag = (EditText) findViewById(R.id.setTag);

        Tipo();
        cargarDepartamento2();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        botonPresionado(0);
                        Validacion();
                    }
                }, 300);
            }
        });

        btn_foto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=1;
                imgFoto1();

            }
        });

        btn_foto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=2;
                imgFoto2();
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Registrando...");

        pd2 = new ProgressDialog(this);
        pd2.setMessage("Subiendo Imagenes.");

        pd3 = new ProgressDialog(this);
        pd3.setMessage("Subiendo Imagenes..");


        Intent intent = getIntent();
        numeroIne = intent.getStringExtra("num_ine");

        if (numeroIne == null) {
            Log.e("INTENT", "No se enviaron los datos");
            numeroIne = "";
        }

        Log.e("numero_ine", numeroIne);


    }


    //ALETORIO
    Random primero = new Random();
    int prime= primero.nextInt(9);

    String [] segundo = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun = (int) Math.round(Math.random() * 25 ) ;

    Random tercero = new Random();
    int tercer= tercero.nextInt(9);

    String [] cuarto = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart = (int) Math.round(Math.random() * 25 ) ;

    String numero_aletorio=prime+segundo[numRandonsegun]+tercer+cuarto[numRandoncuart];

    //ALETORIO2

    Random primero2 = new Random();
    int prime2= primero2.nextInt(9);

    String [] segundo2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun2 = (int) Math.round(Math.random() * 25 ) ;

    Random tercero2 = new Random();
    int tercer2= tercero2.nextInt(9);

    String [] cuarto2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart2 = (int) Math.round(Math.random() * 25 ) ;

    String numero_aletorio2=prime2+segundo2[numRandonsegun2]+tercer2+cuarto2[numRandoncuart2];


    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);


    //FOTOS


    public void imgFoto1(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                nombreImagen1 = "app"+anio+mes+dia+nombre.getText().toString()+"-"+numero_aletorio+".png";
                foto= new File(getApplication().getExternalFilesDir(null),nombreImagen1);
                rutaImagen1 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
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
                startActivityForResult(intentCaptura, 0);
            }
        }
    }

    public void imgFoto2(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {
            File foto=null;
            try {
                nombreImagen2 = "app"+anio+mes+dia+nombre.getText().toString()+"-"+numero_aletorio2+".png";
                foto = new File(getApplication().getExternalFilesDir(null),nombreImagen2);
                rutaImagen2 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {
                uri_img2= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img2);
                startActivityForResult( intentCaptura, 1);
            }
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/"+nombreImagen1);

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

                Foto1View.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                view1.setImageBitmap(bitmap);
                espacio2.setVisibility(View.VISIBLE);

                Foto2.setVisibility(View.VISIBLE);
                espacio3.setVisibility(View.VISIBLE);
                registrar1.setVisibility(View.VISIBLE);

            }
            if (requestCode == 1) {

                Bitmap bitmap2 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/"+nombreImagen2);

                bitmap2 = DetectarPlaca.fechaHoraFoto(bitmap2);

                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(rutaImagen2);
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, fos); // compress and save as JPEG
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Foto2View.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                view2.setImageBitmap(bitmap2);
                espacio4.setVisibility(View.VISIBLE);

            }


        }
    }


    public void Tipo() {
        tipo.add("Seleccionar..");
        tipo.add("Seleccionar...");
        tipo.add("Inmueble");
        tipo.add("Colono");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tipo);
        Tipo.setAdapter(adapter1);
        Tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Tipo.getSelectedItem().equals("Seleccionar..")) {
                    tipo.remove(0);
                } else if (Tipo.getSelectedItem().equals("Seleccionar...")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("No selecciono ningún tipo...")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();
                } else {
                    departamento.clear();
                    traeDepartamento();


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void traeDepartamento() {

        String URL = "https://demo.kap-adm.mx/plataforma/casetaV2/controlador/dm_access/reg_traba1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                        cargarDepartamento();
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
                params.put("departamento", Tipo.getSelectedItem().toString());

                return params;

            }
        };
        requestQueue.add(stringRequest);
    }


    public void cargarDepartamento() {


        try {
            departamento.add("Seleccionar..");
            departamento.add("Seleccionar...");

            for (int i = 0; i < ja1.length(); i += 7) {
                departamento.add(ja1.getString(i + 2));
            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, departamento);
            Departamento.setAdapter(adapter1);
            Departamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (Departamento.getSelectedItem().equals("Seleccionar..")) {
                        departamento.remove(0);
                    } else if (Departamento.getSelectedItem().equals("Seleccionar...")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("No selecciono ningún departamento...")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                }).create().show();
                    } else {
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

    public void cargarDepartamento2() {

        departamento.add("Seleccionar...");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, departamento);
        Departamento.setAdapter(adapter1);
    }

    public void Validacion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar al trabajador ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        pd.show();
                        traeDepartamento2();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        botonPresionado(1);
                        //Intent i = new Intent(getApplicationContext(), RegTrabActivity.class);
                        // startActivity(i);
                        // finish();

                    }
                }).setCancelable(false).create().show();
    }

    public void traeDepartamento2() {

        if (Tipo.getSelectedItem().toString().equals("Seleccionar...") || Departamento.getSelectedItem().toString().equals("Seleccionar...")) {

            pd.dismiss();
            botonPresionado(1);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("No selecciono ningún tipo / departamento...")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if(nombre.getText().toString().equals("") || nombre.getText().toString().equals(" ")){
            pd.dismiss();
            botonPresionado(1);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Rellenar campo de nombre...")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else {
            String URL = "https://demo.kap-adm.mx/plataforma/casetaV2/controlador/dm_access/reg_traba2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


                @Override
                public void onResponse(String response) {

                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja2 = new JSONArray(response);
                            Registro();
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
                    params.put("id_residencial", Conf.getResid().trim());
                    params.put("tipo", Tipo.getSelectedItem().toString());
                    params.put("departamento", Departamento.getSelectedItem().toString());

                    return params;

                }
            };
            requestQueue.add(stringRequest);
        }
    }

    public void Registro() {
        String URL = "https://demo.kap-adm.mx/plataforma/casetaV2/controlador/dm_access/reg_traba3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {


                if(response.equals("error")){
                    pd.dismiss();
                    botonPresionado(1);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Registro No Exitoso")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.RegTrab2Activity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();


                }else {

                    if (Global_info.getCantidadFotosEnEsperaEnSegundoPlano(RegTrabActivity.this) >= Global_info.getLimiteFotosSegundoPlano()){
                        if(foto==1){
                            upload1();
                        }else if(foto==2){
                            upload1();
                            upload2();
                        }
                    }else {
                        if(foto==1){
                            ContentValues val_img1 = ValuesImagen(nombreImagen1, Conf.getPin() + "/trabajadores/" + nombreImagen1.trim(), rutaImagen1);
                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);
                            //upload1();
                        }else if(foto==2){
                            ContentValues val_img1 = ValuesImagen(nombreImagen1, Conf.getPin() + "/trabajadores/" + nombreImagen1.trim(), rutaImagen1);
                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);
                            //upload1();
                            ContentValues val_img2 = ValuesImagen(nombreImagen2, Conf.getPin() + "/trabajadores/" + nombreImagen2.trim(), rutaImagen2);
                            Uri uri2 = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img2);
                            //upload2();
                        }
                    }



                   Finalizar();
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


                params.put("id_residencial", Conf.getResid().trim());
                try {
                    params.put("tipo", Tipo.getSelectedItem().toString());
                    params.put("departamento", ja2.getString(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("nombre", nombre.getText().toString().trim());
                params.put("tag", tag.getText().toString().trim());
                params.put("telefono", telefono.getText().toString().trim());
                params.put("correo", correo.getText().toString().trim());
                params.put("comentarios", comentarios.getText().toString().trim());
                params.put("foto1", nombreImagen1);
                params.put("foto2", nombreImagen2);

                params.put("numero_ine", numeroIne);


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
        mountainImagesRef = storageReference.child(Conf.getPin() + "/trabajadores/" + nombreImagen1);

        Uri uri  = Uri.fromFile(new File(rutaImagen1));
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
                Toast.makeText(RegTrabActivity.this, "Fallado", Toast.LENGTH_SHORT).show();
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

    public void upload2() {

        StorageReference mountainImagesRef2 = null;
        mountainImagesRef2 = storageReference.child(Conf.getPin() + "/trabajadores/" + nombreImagen2);

        Uri uri  = Uri.fromFile(new File(rutaImagen2));
        UploadTask uploadTask = mountainImagesRef2.putFile(uri);


        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
                //Toast.makeText(getApplicationContext(),"Cargando Imagen PLACA " + progress + "%", Toast.LENGTH_SHORT).show();
                pd3.show();
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(RegTrabActivity.this, "Fallado", Toast.LENGTH_SHORT).show();
                pd3.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                eliminarFotoDirectorioLocal(nombreImagen2);
                pd3.dismiss();
            }
        });


    }

    public void eliminarFotoDirectorioLocal(String nombreFoto){
        String tempfilepath ="";
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
                            }catch (Exception e){
                                Log.e("EliminarFoto", e.toString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ErrorFile", "deleteDirectory: Failed to onCreate directory  " + tempfilepath + " for an unknown reason.");

            }

        }else {
        }
    }

    public void Finalizar(){

        if (Global_info.getCantidadFotosEnEsperaEnSegundoPlano(RegTrabActivity.this) > 0){
            //Solo ejecutar si el servicio no se esta ejecutando
            if (!servicioFotos()) {
                Intent cargarFotos = new Intent(RegTrabActivity.this, subirFotos.class);
                startService(cargarFotos);
            }
        }

        pd.dismiss();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Registro Exitoso")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.RegTrab2Activity.class);
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

    public void botonPresionado(int estado){
        //estado --> 0=presionado   1=restablecer

        Button button = registrar;

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
        pd.dismiss();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
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
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_demolink.RegTrab2Activity.class);
        startActivity(intent);
        finish();
    }
}

