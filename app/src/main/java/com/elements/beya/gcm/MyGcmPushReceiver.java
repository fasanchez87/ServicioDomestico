package com.elements.beya.gcm;

/**
 * Created by FABiO on 23/02/2016.
 * This is a receiver class in which onMessageReceived() method will be triggered whenever device receives new push notification.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.elements.beya.activities.Gestion;
import com.elements.beya.fragments.ServiciosDisponibles;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.google.android.gms.gcm.GcmListenerService;

import com.elements.beya.app.Config;
import org.json.JSONArray;
import me.leolin.shortcutbadger.ShortcutBadger;


public class MyGcmPushReceiver extends GcmListenerService
{

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private gestionSharedPreferences sharedPreferences;
    private int countPush=0;

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle)
    {
        String title = bundle.getString("title");
        String message = bundle.getString("message");
        Boolean isBackground = Boolean.valueOf(bundle.getString("is_background"));
        String image = bundle.getString("image");
        String timestamp = bundle.getString("created_at");
        String pantallaMostrarPushAndroid = bundle.getString("pantallaMostrarPushAndroid");
        String datosEsteticista = bundle.getString("datosEsteticista");//aqui esta el error
        String datosCliente = bundle.getString("datosCliente");//aqui esta el error
        Log.e(TAG, "From: " + from);
        Log.e(TAG, "Title: " + title);
        Log.e(TAG, "message: " + message);
        Log.e(TAG, "image: " + image);
        Log.e(TAG, "timestamp: " + timestamp);
        Log.e(TAG, "isBackground: " + pantallaMostrarPushAndroid);

        sharedPreferences = new gestionSharedPreferences(getApplicationContext());
        countPush=0;
        sharedPreferences.putInt("countPush", countPush = sharedPreferences.getInt("countPush") + 1);
        ShortcutBadger.applyCount(this, sharedPreferences.getInt("countPush")); //for 1.1.4

        if (!isBackground)
        {
            // verifying whether the app is in background or foreground
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
            {
                if(pantallaMostrarPushAndroid.equals("pushNotificationNormal"))
                {
                        // app is in foreground, broadcast the push message
                        //Si la app esta al frente, creamos un Broadcast receiver; con el objetivo de que cuando
                        //llegue un push se dispare dicho evento llamando al broadcast receiver en la
                        //actividad mediante el metodo onreceive();-> ver -> ServiciosDisponibles
                        //si el valor recibido es pushNotificationNormal quier decir que muestta la notificacion normal la pantalla.
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("message", message);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                        // play notification sound
                        NotificationUtils notificationUtils = new NotificationUtils();
                        notificationUtils.playNotificationSound();

                  /*  Intent resultIntent = new Intent(getApplicationContext(), Gestion.class);
                    if (TextUtils.isEmpty(image))
                    {
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    }

                    else
                    {
                        // push notification contains image
                        // show it with the image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, image);
                    }*/

                    }

                else

                if(pantallaMostrarPushAndroid.equals("pushNotificationAceptacionServicio"))
                {

                    //Creamos un intent receiver de modo que apenas llegue me lleve a la activity cuando se acepta un servicio.
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION_PANTALLA);
                    pushNotification.putExtra("datosEsteticista", datosEsteticista);
                    pushNotification.putExtra("datosCliente", datosCliente);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();

                    sharedPreferences.putString("datosCliente", datosCliente); //datos del cliente que hizo la solicutud.

                }


            }

            else
            {
                // app is in background. show the message in notification try
                Intent resultIntent = new Intent(getApplicationContext(), Gestion.class);
                // check for push notification image attachment
                if (TextUtils.isEmpty(image))
                {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                }

                else
                {
                    // push notification contains image
                    // show it with the image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, image);
                }
            }
        }

         else
        {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
            // podemos almacenar los mensajes si en el backend se desea almacenar los mensajes enviados
            // sin necesidad de mostrar el push al Cliente.
        }

    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

}