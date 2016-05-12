package com.elements.beya.services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.elements.beya.activities.AceptacionServicio;
import com.elements.beya.activities.Gestion;
import com.elements.beya.fragments.MapFragmentUbicarProveedores;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by FABiO on 19/04/2016.
 */
public class ServiceObtenerUbicacionEsteticista extends Service
{

    public static double latitud;
    public static double longitud;
    public static String fechaMovimiento;
    private static final String TAG = "ServiceActualizarUbicacionProveedor";

    private String codigoEsteticista;

    AceptacionServicio aceptacionServicio;

    public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds
    // run on another Thread to avoid crash
    private Handler mHandler;
    // timer handling
    private Timer mTimer = null;

    Gestion gestion;
    private gestionSharedPreferences sharedPreferences;

    private String _urlWebService;

    @Override
    public void onCreate()
    {
        // cancel if already existed
        gestion = new Gestion();
        sharedPreferences = new gestionSharedPreferences(getApplicationContext());
        aceptacionServicio = new AceptacionServicio();
        mHandler = new Handler();
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

        MapFragmentUbicarProveedores.countDownTimer.cancel();
//        MapFragmentUbicarProveedores.alertDialog.dismiss();
        MapFragmentUbicarProveedores.alertDialogBuilder.show().dismiss();

        String datosEsteticista = intent.getStringExtra("datosEsteticista");
        String datosCliente = intent.getStringExtra("datosCliente");
        String codigoSolicitud = intent.getStringExtra("codigoSolicitud");
        codigoEsteticista = intent.getStringExtra("codigoEsteticista");

        Intent aceptacionServicio = new Intent(this,AceptacionServicio.class);
        aceptacionServicio.putExtra("datosEsteticista", datosEsteticista);
        aceptacionServicio.putExtra("datosCliente", datosCliente);
        aceptacionServicio.putExtra("codigoSolicitud", codigoSolicitud);
        aceptacionServicio.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(aceptacionServicio);

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
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 5000, NOTIFY_INTERVAL);
        Toast.makeText(this, "Servicio Rastreo Ubicacion Esteticista: ACTIVO", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    private  boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi =  false ;
        boolean haveConnectedMobile =  false ;

        ConnectivityManager cm =  (ConnectivityManager) getSystemService ( Context. CONNECTIVITY_SERVICE );
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

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        mTimer.cancel();
       // _webServiceObtenerUbicacionEsteticista();
        Toast.makeText(this, "Servicio destruido", Toast.LENGTH_SHORT).show();
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
                    try
                    {
                        if (haveNetworkConnection())
                        {
                            _webServiceObtenerUbicacionEsteticista();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "SIN CONEXION, NO SE PUEDE OBTENER LA UBICACION.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "ERROR: "+e.getMessage().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public static double getLatitud()
    {
        return latitud;
    }

    public void setLatitud(double latitud)
    {
        this.latitud = latitud;
    }

    public static double getLongitud()
    {
        return longitud;
    }

    public void setLongitud(double longitud)
    {
        this.longitud = longitud;
    }

    public static String getFechaMovimiento()
    {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(String fechaMovimiento)
    {
        this.fechaMovimiento = fechaMovimiento;
    }

    private void _webServiceObtenerUbicacionEsteticista()
    {
        _urlWebService = "http://52.72.85.214/ws/ObtenerUbicacionEsteticista";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if (status)
                            {
                                JSONArray ubicacion = response.getJSONArray("result");

                                for( int i=0; i<= ubicacion.length()-1; i++ )
                                {
                                    setLatitud(Double.parseDouble(ubicacion.getJSONObject(i).getString("latitudUsuario").toString()));
                                    setLongitud(Double.parseDouble(ubicacion.getJSONObject(i).getString("longitudUsuario").toString()));
                                    setFechaMovimiento(ubicacion.getJSONObject(i).getString("fecMovimiento").toString());
                                }

                            }

                        }

                        catch (JSONException e)
                        {
                            Toast.makeText(getApplicationContext(), "Error: -> " + "\n" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
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
                headers.put("serialUsuario", codigoEsteticista);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

}
