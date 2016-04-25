package com.elements.beya.services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

    AceptacionServicio aceptacionServicio;

    public static final long NOTIFY_INTERVAL = 10 * 1000; // 5 seconds
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    Gestion gestion;
    private gestionSharedPreferences sharedPreferences;

    private String _urlWebService;

    public ServiceObtenerUbicacionEsteticista()
    {

    }

    @Override
    public void onCreate()
    {
        // cancel if already existed
        gestion = new Gestion();
        sharedPreferences = new gestionSharedPreferences(getApplicationContext());
        aceptacionServicio = new AceptacionServicio();
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
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
        Toast.makeText(this, "Servicio Rastreo Ubicacion Esteticista: ACTIVO", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        mTimer.cancel();
        _webServiceObtenerUbicacionEsteticista();
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

                    _webServiceObtenerUbicacionEsteticista();

                  /*  Toast.makeText(getApplicationContext(), getLatitud()+" : "+getLongitud()+" : "+getFechaMovimiento(),
                            Toast.LENGTH_SHORT).show();

                    Toast.makeText(getApplicationContext(),  "Aceptacion::: "+sharedPreferences.getString("serialUsuarioEsteticista"),
                            Toast.LENGTH_SHORT).show();*/

                    /*Toast.makeText(getApplicationContext(), sharedPreferences.getString("statusOnline"),
                            Toast.LENGTH_SHORT).show();*/
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

                                    Log.i(TAG, "" + getLatitud());
                                    Log.i(TAG, ""+getLongitud());
                                    Log.i(TAG,""+getFechaMovimiento());
                                }

                            }
                            else
                            {

                                if (!status)
                                {
                                  AlertDialog.Builder builder = new AlertDialog.Builder(ServiceObtenerUbicacionEsteticista.this);
                                    builder
                                            .setMessage(message.toString())
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                    //startActivity(intent);
                                                    //finish();
                                                }
                                            }).show();

                                }

                            }
                        } catch (JSONException e) {


                          AlertDialog.Builder builder = new AlertDialog.Builder(ServiceObtenerUbicacionEsteticista.this);
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();

                            e.printStackTrace();
                        }


                    }


                },

                new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceObtenerUbicacionEsteticista.this);
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();


                        } else if (error instanceof NoConnectionError) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceObtenerUbicacionEsteticista.this);
                            builder
                                    .setMessage("Por favor, conectese a la red.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();

                        } else if (error instanceof AuthFailureError) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceObtenerUbicacionEsteticista.this);
                            builder
                                    .setMessage("Error de autentificación en la red, favor contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();


                        } else if (error instanceof ServerError) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceObtenerUbicacionEsteticista.this);
                            builder
                                    .setMessage("Error server, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();


                        } else if (error instanceof NetworkError) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceObtenerUbicacionEsteticista.this);
                            builder
                                    .setMessage("Error de red, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();


                        } else if (error instanceof ParseError) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceObtenerUbicacionEsteticista.this);
                            builder
                                    .setMessage("Error de conversión Parser, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                        }


                    }
                })
        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));

                headers.put("serialUsuario", aceptacionServicio.getSerialUsuarioEsteticista());
                headers.put("MyToken", sharedPreferences.getString("MyToken"));


                return headers;
            }

        };

        //jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

}
