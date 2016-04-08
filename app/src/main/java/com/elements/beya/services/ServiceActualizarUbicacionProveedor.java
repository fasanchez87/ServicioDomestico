package com.elements.beya.services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
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
import com.elements.beya.activities.Gestion;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceActualizarUbicacionProveedor extends Service {

    Gestion gestion;
    private gestionSharedPreferences sharedPreferences;


    public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    private String _urlWebService;

    public ServiceActualizarUbicacionProveedor() {

    }

    @Override
    public void onCreate() {
        // cancel if already existed
        gestion = new Gestion();
        sharedPreferences = new gestionSharedPreferences(getApplicationContext());


    }

    @Override
    public IBinder onBind(Intent intent) {
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
        } else
        {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
        Toast.makeText(this, "Servicio en Ejecucion", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mTimer.cancel();
        _webServiceUpdatePositionProvider(gestion.getLocation(4.102533, -76.202582, 10000),
                sharedPreferences.getString("serialUsuario") , sharedPreferences.getString("statusOnline"));
        Toast.makeText(this, "Servicio destruido", Toast.LENGTH_SHORT).show();
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast

                    _webServiceUpdatePositionProvider(gestion.getLocation(4.102533, -76.202582, 10000),
                            sharedPreferences.getString("serialUsuario") , sharedPreferences.getString("statusOnline"));

                    Toast.makeText(getApplicationContext(), gestion.getLocation(4.102533, -76.202582, 10000),
                            Toast.LENGTH_SHORT).show();

                    Toast.makeText(getApplicationContext(), sharedPreferences.getString("statusOnline"),
                            Toast.LENGTH_SHORT).show();
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

    }

    private void _webServiceUpdatePositionProvider(String locationUser, final String serialUser, final String statusOnline) {
        _urlWebService = "http://52.72.85.214/ws/ActualizarUbicacionEsteticista";

        String[] parts = locationUser.split(":");
        final String latitudUsuario = parts[0];
        final String longitudUsuario = parts[1];

        Toast.makeText(getApplicationContext(), "LATITUD::" + latitudUsuario + "  LONGITUD::" + longitudUsuario + " SERIALUSER:: " + serialUser,
                Toast.LENGTH_SHORT).show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");


                            if (status.equals("update_location_success")) {

                            } else {
                                if (status.equals("update_location_failed")) {


                                }

                            }
                        } catch (JSONException e) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActualizarUbicacionProveedor.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActualizarUbicacionProveedor.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActualizarUbicacionProveedor.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActualizarUbicacionProveedor.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActualizarUbicacionProveedor.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActualizarUbicacionProveedor.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActualizarUbicacionProveedor.this);
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

//				    @Override
//		            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError
//		            {
//				    	HashMap<String, String> params = new HashMap<String, String>();
//				    	//params.put("Content-Type", "application/json");
//				    	params.put("email_cliente", "MMM" );
//				    	params.put("pass_cliente", "MMM" );
//				    	params.put("name_cliente", "MMM");
//				    	params.put("ape_cliente", "MMM" );
//
//
//		                return params;
//		            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));

                headers.put("serialUsuario", serialUser);
                headers.put("latitudUsuario", latitudUsuario);
                headers.put("longitudUsuario", longitudUsuario);
                headers.put("statusOnline", statusOnline);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));


                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }
}
