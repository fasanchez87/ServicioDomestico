package com.elements.beya.services;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class ServiceActualizarUbicacionProveedor extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    private static final String TAG = "ServiceActualizarUbicacionProveedor";
    private LocationManager mLocationManager;
    private double mLatitude = 0;
    private double mLongitude = 0;
    Location mCurrentLocation;

    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 1;

    LocationRequest mLocationRequest;

    private gestionSharedPreferences sharedPreferences;

    public static final long NOTIFY_INTERVAL = 2 * 1000; // 2 seconds
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    private String _urlWebService;

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate()
    {
        // cancel if already existed
        sharedPreferences = new gestionSharedPreferences(getApplicationContext());

        mGoogleApiClient = new GoogleApiClient.Builder(this.getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        mGoogleApiClient.connect();

        if (!isGooglePlayServicesAvailable())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("SIN SOPORTE DE GOOGLE PLAY SERVICES.")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                            //startActivity(intent);
                            //finish();
                        }
                    }).show();
        }

        if (mGoogleApiClient.isConnected())
        {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
        // TODO Auto-generated method stub
        if (mTimer != null)
        {
            mTimer.cancel();
        }
        else
        {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 1000, NOTIFY_INTERVAL);
        return START_STICKY;
    }

    private boolean isGooglePlayServicesAvailable()
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
        {
            return true;
        }
        else
        {
            //GooglePlayServicesUtil.getErrorDialog(status, this.getApplication()., 0).show();
            return false;
        }
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        mTimer.cancel();
        _webServiceUpdatePositionProvider((mLatitude + ":" + mLongitude),
                sharedPreferences.getString("serialUsuario"), sharedPreferences.getString("statusOnline"));

        Log.e(TAG, "onDestroy");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
        super.onDestroy();

    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLatitude = mCurrentLocation.getLatitude();
        mLongitude = mCurrentLocation.getLongitude();
        sharedPreferences.putDouble("latitudCliente", mCurrentLocation.getLatitude());
        sharedPreferences.putDouble("longitudCliente", mCurrentLocation.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());

    }

    class TimeDisplayTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            // run on another thread
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {

                    if(haveNetworkConnection())
                    {
                        _webServiceUpdatePositionProvider( ( mLatitude + ":" + mLongitude ) ,
                                sharedPreferences.getString("serialUsuario") , sharedPreferences.getString("statusOnline"));

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "SIN CONEXION, NO SE PUEDE ACTUALIZAR LA UBICACION.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }

    }

    private  boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi =  false ;
        boolean haveConnectedMobile =  false ;

        ConnectivityManager cm =  (ConnectivityManager) getSystemService ( Context . CONNECTIVITY_SERVICE );
        NetworkInfo[] netInfo = cm . getAllNetworkInfo ();
        for  ( NetworkInfo ni : netInfo )
        {
            if  ( ni . getTypeName (). equalsIgnoreCase ( "WIFI" ))
                if  ( ni . isConnected ())
                    haveConnectedWifi =  true ;
            if  ( ni . getTypeName (). equalsIgnoreCase ( "MOBILE" ))
                if  ( ni . isConnected ())
                    haveConnectedMobile =  true ;
        }
        return haveConnectedWifi || haveConnectedMobile ;
    }

    private void _webServiceUpdatePositionProvider(String locationUser, final String serialUser, final String statusOnline)
    {
        _urlWebService = "http://52.72.85.214/ws/ActualizarUbicacionEsteticista";

        String[] parts = locationUser.split(":");
        final String latitudUsuario = parts[0];
        final String longitudUsuario = parts[1];

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {

                    }
                },

                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("serialUsuario", serialUser);
                headers.put("latitudUsuario", latitudUsuario);
                headers.put("longitudUsuario", longitudUsuario);
                headers.put("statusOnline", statusOnline);
                headers.put("tokenGCM", sharedPreferences.getString("TOKEN"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
    }
}
