package mx.linkom.caseta_demolink;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import mx.linkom.caseta_demolink.offline.Database.UrisContentProvider;
import mx.linkom.caseta_demolink.offline.Servicios.subirFotos;


public class fotosPendientes extends mx.linkom.caseta_demolink.Menu {

    TextView txtCantidadFotos;
    int cantidadFotos = 0;
    Button btnActualizarCantidad, btnSubirFotos;
    /*ImageView iconoInternet;
    boolean Offline = false;*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_pendientes);

        txtCantidadFotos = (TextView) findViewById(R.id.setCantidadFotos);
        btnSubirFotos = (Button) findViewById(R.id.btnSubirFotos);

        txtCantidadFotos.setText(""+cantidadFotosLocal());

        /*iconoInternet = (ImageView) findViewById(R.id.iconoInternetFotosPendientes);

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
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(fotosPendientes.this);
                    alertDialogBuilder.setTitle(Global_info.getTituloAviso());
                    alertDialogBuilder
                            .setMessage(Global_info.getModoOffline())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(fotosPendientes.this);
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

        btnSubirFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cantidadFotosLocal() != 0){
                    if (subirFotos()){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fotosPendientes.this);
                        alertDialogBuilder.setTitle("Aviso");
                        alertDialogBuilder
                                .setMessage("Las imágenes se están cargando, revisar el estado en la barra de notificaciones de su dispositivo.")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                }).create().show();
                    }else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fotosPendientes.this);
                        alertDialogBuilder.setTitle("Aviso");
                        alertDialogBuilder
                                .setMessage("Verifique en la barra de notificaciones de su dispositivo no se estén subiendo fotos actualmente, si es así, espere a que finalice e intente de nuevo cuando finalice.")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                }).create().show();
                    }
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fotosPendientes.this);
                    alertDialogBuilder.setTitle("Aviso");
                    alertDialogBuilder
                            .setMessage("Por el momento no hay fotos para subir.")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private int cantidadFotosLocal(){
        int cantidad = 0;
        Cursor cursoFotos = null;
        cursoFotos = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, null, "todos", null, null);
        if (cursoFotos.moveToFirst()) {
            do {
                cantidad++;
            } while (cursoFotos.moveToNext());
        }
        cursoFotos.close();

        return cantidad;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean subirFotos(){
        boolean servicio = false;
        //Solo ejecutar si el servicio no se esta ejecutando
        if (!servicioFotos()){
            servicio = true;
            Cursor cursoFotos = null;

            cursoFotos = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE,null,"todos", null,null);

            ArrayList<String> titulos = new ArrayList<String>();
            ArrayList<String> direccionesFirebase = new ArrayList<String>();
            ArrayList<String> rutasDispositivo = new ArrayList<String>();

            if (cursoFotos.moveToFirst()){
                do {
                    titulos.add(cursoFotos.getString(0));
                    direccionesFirebase.add(cursoFotos.getString(1));
                    rutasDispositivo.add(cursoFotos.getString(2));

                } while (cursoFotos.moveToNext());
            }

            cursoFotos.close();


            //Si hay fotos sin subir iniciar servicio para subir fotos a firebase

            if (titulos.size() > 0 && direccionesFirebase.size() > 0){
                System.out.println("Si hay fotos para subir");
                Intent cargarFotos = new Intent(fotosPendientes.this, subirFotos.class);
                cargarFotos.putExtra("nombres", titulos);
                cargarFotos.putExtra("direccionesFirebase", direccionesFirebase);
                cargarFotos.putExtra("rutasDispositivo", rutasDispositivo);
                startService(cargarFotos);
            }
        }

        return servicio;
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
        finish();

    }
}