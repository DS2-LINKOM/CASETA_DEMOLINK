package mx.linkom.caseta_demolink;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private mx.linkom.caseta_demolink.Configuracion Conf;

    private static final String TAG = "NOTIFICACION";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Conf = new mx.linkom.caseta_demolink.Configuracion(this);
        String opcion = "";
        String idMensaje = "";
        String codigoMensaje = "";

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            opcion = remoteMessage.getData().get("opcion");
            idMensaje = remoteMessage.getData().get("idMensaje");
            codigoMensaje = remoteMessage.getData().get("codigoMensaje");
        }

        sendNotification(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(), opcion, idMensaje, codigoMensaje);
    }

    //PROCESA NOTIFICACION
    private void sendNotification(final String title, final String messageBody, final String opcion,
                                  final String idMensaje, final String codigoMensaje){

        //Intent intent = new Intent(this, NavegadorActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //mBuilder.setContentIntent(pendingIntent);
        //Conf.setNoti(title);

        //Conf.setNoti(title);




        //Intent intent = new Intent(this,mx.linkom.caseta_demolink.NavegadorActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
       // Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       // NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            //    .setSmallIcon(R.drawable.log)
             //   .setColor(getResources().getColor(R.color.colorAccent))
             //   .setVibrate(new long[] { 400, 400, 400, 400, 400 })
             //   .setLights(Color.WHITE, 3000, 3000)
            //    .setContentTitle(title)
            //    .setContentText(messageBody)
             //   .setAutoCancel(true)
             //   .setSound(soundUri)
             //   .setContentIntent(pendingIntent);
        //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       // notificationManager.notify(0, notificationBuilder.build());









    }

}
