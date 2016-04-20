package com.elements.beya.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
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
import com.elements.beya.R;
import com.elements.beya.activities.SolitudServicioDetallada;
import com.elements.beya.adapters.ServiciosDisponiblesAdapter;
import com.elements.beya.app.Config;
import com.elements.beya.beans.Servicio;
import com.elements.beya.beans.SolicitudServicio;
import com.elements.beya.decorators.DividerItemDecoration;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class ServiciosDisponibles extends Fragment
{

    private String TAG = ServiciosDisponibles.class.getSimpleName();

    private String _urlWebService;
    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Servicio> serviciosDisponibles;
    private ArrayList<SolicitudServicio> solicitudesServicios;
    private RecyclerView recyclerViewServiciosDisponibles;
    private ServiciosDisponiblesAdapter mAdapter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    HashMap <String, ArrayList<Servicio>> hashTableSolicitudDetallada;

    boolean active= false;

    ProgressBar progressBar;


    public ServiciosDisponibles()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        sharedPreferences = new gestionSharedPreferences(this.getActivity());
        serviciosDisponibles = new ArrayList<Servicio>();
        solicitudesServicios = new ArrayList<SolicitudServicio>();

        hashTableSolicitudDetallada =  new HashMap <String, ArrayList<Servicio>>();

        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                {
                    // new push notification is received

                    Log.w("ALERTA", "Push notification is received!"+intent.getStringExtra("message"));

                    solicitudesServicios.clear();
                    //data.addAll(datas);
                    //notifyDataSetChanged();

                    _webServiceGetSolicitudesServicios();
                    mAdapter.notifyDataSetChanged();
                    ShortcutBadger.removeCount(ServiciosDisponibles.this.getActivity()); //for 1.1.4


                    Toast.makeText(ServiciosDisponibles.this.getActivity(), "Push notification is received!" + intent.getExtras().getString("message"), Toast.LENGTH_LONG).show();
                }
            }
        };





    }



    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(ServiciosDisponibles.this.getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

       /* // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));*/
        ShortcutBadger.removeCount(ServiciosDisponibles.this.getActivity()); //for 1.1.4

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(ServiciosDisponibles.this.getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_servicios_disponibles, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ShortcutBadger.removeCount(ServiciosDisponibles.this.getActivity()); //for 1.1.4
        sharedPreferences.remove("countPush");

        progressBar = (ProgressBar) this.getActivity().findViewById(R.id.toolbar_progress_bar);
        recyclerViewServiciosDisponibles = (RecyclerView) this.getActivity().findViewById(R.id.recycler_view_servicios_disponibles);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewServiciosDisponibles.setLayoutManager(layoutManager);
        mAdapter = new ServiciosDisponiblesAdapter(solicitudesServicios);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerViewServiciosDisponibles.setLayoutManager(mLayoutManager);
        recyclerViewServiciosDisponibles.setItemAnimator(new DefaultItemAnimator());
        recyclerViewServiciosDisponibles.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewServiciosDisponibles.setAdapter(mAdapter);

        recyclerViewServiciosDisponibles.addOnItemTouchListener(new RecyclerTouchListener(this.getActivity(),
                recyclerViewServiciosDisponibles, new ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                SolicitudServicio solicitudServicio = solicitudesServicios.get(position);

                Intent intent = new Intent(ServiciosDisponibles.this.getActivity(), SolitudServicioDetallada.class);
                intent.putExtra("codigoSolicitud",solicitudServicio.getCodigoSolicitudServicio());
                intent.putExtra("codigoCliente",solicitudServicio.getCodigoClienteSolicitudServicio());
                intent.putExtra("ubicacionCliente",solicitudServicio.getUbicacionSolicitudServicio());
                startActivity(intent);

                sharedPreferences.putHashMapObjectServicio(hashTableSolicitudDetallada);

            }

            @Override
            public void onLongClick(View view, int position)
            {

            }
        }));

        //progressBar.setVisibility(View.VISIBLE);
        _webServiceGetSolicitudesServicios();
        mAdapter.notifyDataSetChanged();

    }

    public void _webServiceGetSolicitudesServicios()
    {
        _urlWebService = "http://52.72.85.214/ws/ObtenerSolicitudesDisponibles";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    Geocoder geocoder;
                    List<Address> addresses;
                    ArrayList<String> fechaSolicitud;
                    ArrayList<String> location;
                    String ubicacionCliente;
                    Float lat;
                    Float lon;
                    Double latitude, longitude;

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {

                            Boolean solicitudesDisponibles = response.getBoolean("status");

                            if (solicitudesDisponibles)
                            {
                                JSONArray solicitudes = response.getJSONArray("result");
                                JSONObject object;
                                JSONObject objectServicios;
                                JSONObject objectServiciosSolicitados;
                                JSONArray listadoServicios;

                                for (int i = 0; i <= solicitudes.length()-1; i++)
                                {
                                    object = solicitudes.getJSONObject(i);

                                    SolicitudServicio solicitudServicio = new SolicitudServicio();

                                    solicitudServicio.setCodigoSolicitudServicio(object.getString("codigoSolicitud"));
                                    solicitudServicio.setCodigoClienteSolicitudServicio(object.getString("codigoCliente"));
                                    String fecha = object.getString("fecSolicitudCliente");

                                    fechaSolicitud = new ArrayList<String>(Arrays.asList(fecha.split(" ")));

                                    solicitudServicio.setFechaSolicitudServicio(fechaSolicitud.get(0));
                                    solicitudServicio.setHoraSolicitudServicio(fechaSolicitud.get(1));

                                    solicitudServicio.setUbicacionSolicitudServicio(object.getString("ubicacionCliente"));
                                   // solicitudServicio.setEstadoSolicitud(object.getString("esAtendida"));

                                    solicitudesServicios.add(solicitudServicio);
                                    listadoServicios = object.getJSONArray("servicios");

                                    serviciosDisponibles = new ArrayList<Servicio>();

                                    for (int j = 0; j <= listadoServicios.length() - 1; j++)
                                    {
                                        objectServiciosSolicitados = listadoServicios.getJSONObject(j);
                                        Servicio servicio = new Servicio();
                                        servicio.setNombreServicio(objectServiciosSolicitados.getString("nombreServicio"));
                                        servicio.setDescripcionServicio(objectServiciosSolicitados.getString("descripcionServicio"));
                                        servicio.setValorServicio(objectServiciosSolicitados.getString("valorServicio"));
                                        serviciosDisponibles.add(servicio);
                                    }

                                    hashTableSolicitudDetallada.put(solicitudesServicios.get(i).getCodigoSolicitudServicio(),
                                            serviciosDisponibles);


                                }
                                progressBar.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();

                            }

                            else
                            {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
                                builder
                                        .setMessage("No existen solicitudes de servicio en este momento.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                //startActivity(intent);
                                                //finish();
                                            }
                                        }).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                        catch (JSONException e)
                        {


                            progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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

                      /*  catch (IOException e)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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
                            progressBar.setVisibility(View.GONE);


                            e.printStackTrace();
                        }*/


                    }

                },


                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        if (error instanceof TimeoutError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            progressBar.setVisibility(View.GONE);



                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
                            builder
                                    .setMessage("Por favor, conectese a la red.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            progressBar.setVisibility(View.GONE);


                        }

                        else

                        if (error instanceof AuthFailureError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
                            builder
                                    .setMessage("Error de autentificación en la red, favor contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            progressBar.setVisibility(View.GONE);




                        }

                        else

                        if (error instanceof ServerError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
                            builder
                                    .setMessage("Error server, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            progressBar.setVisibility(View.GONE);




                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
                            builder
                                    .setMessage("Error de red, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            progressBar.setVisibility(View.GONE);



                        }

                        else

                        if (error instanceof ParseError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
                            builder
                                    .setMessage("Error de conversión Parser, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            progressBar.setVisibility(View.GONE);

                        }


                    }


                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("serialUsuario", sharedPreferences.getString("serialUsuario"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 6, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }


    public interface ClickListener
    {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
    {

        private GestureDetector gestureDetector;
        private ServiciosDisponibles.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ServiciosDisponibles.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }



        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e)
        {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
        {

        }
    }



}
