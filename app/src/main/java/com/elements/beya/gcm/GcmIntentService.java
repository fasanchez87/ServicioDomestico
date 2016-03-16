package com.elements.beya.gcm;

/**
 * Created by FABiO on 23/02/2016.
 *
 * This service extends IntentService which acts as a background service. This service basically used for three purposes:

 1. To connect with gcm server and fetch the registration token. Uses registerGCM() method.

 2. Subscribe to a topic. Uses subscribeToTopic(“topic”) method.

 3. Unsubscribe from a topic. Uses unsubscribeFromTopic(“topic”) method.

 */


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.elements.beya.R;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import com.elements.beya.app.Config;


public class GcmIntentService extends IntentService
{


    private static final String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService()
    {
        super(TAG);
    }

    public static final String KEY = "key";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";


    @Override
    protected void onHandleIntent(Intent intent)
    {

        String key = intent.getStringExtra(KEY);

        switch (key)
        {
            case SUBSCRIBE:
                // subscribe to a topic
                String topic = intent.getStringExtra(TOPIC);
                subscribeToTopic(topic);
                break;

            case UNSUBSCRIBE:
                String topic1 = intent.getStringExtra(TOPIC);
                unsubscribeFromTopic(topic1);
                break;

            default:
                // if key is not specified, register with GCM
                registerGCM();
        }

    }

    /**
     * Registering with GCM and obtaining the gcm registration id
     */
    private void registerGCM()
    {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = null;


        try
        {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);



            Log.e(TAG, "GCM Registration Token: " + token);

            // sending the registration id to our server
            sendRegistrationToServer(token);

            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();
        }

        catch (Exception e)
        {
            Log.e(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token)
    {
        // Send the registration token to our server
        // to keep it in MySQL

    }

    /**
     * Subscribe to a topic
     */
    public void subscribeToTopic(String topic)
    {
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try
        {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null)
            {
                pubSub.subscribe(token, "/topics/" + topic, null);
                Log.e(TAG, "Subscribed to topic: " + topic);
            }

            else
            {
                Log.e(TAG, "error: gcm registration id is null");
            }

        }

        catch (IOException e)
        {
            Log.e(TAG, "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void unsubscribeFromTopic(String topic)
    {
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;

        try
        {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null)
            {
                pubSub.unsubscribe(token, "");
                Log.e(TAG, "Unsubscribed from topic: " + topic);
            }

            else
            {
                Log.e(TAG, "error: gcm registration id is null");
            }

        }

        catch (IOException e)
        {
            Log.e(TAG, "Topic unsubscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}