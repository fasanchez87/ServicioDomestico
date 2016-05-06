package com.elements.beya.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.KeyEvent;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.elements.beya.CircularImageView.CircularNetworkImageView;
import com.elements.beya.R;
import com.elements.beya.adapters.ServicioDetalleHistorial;
import com.elements.beya.adapters.ServiciosAdapter;
import com.elements.beya.beans.Servicio;
import com.elements.beya.decorators.DividerItemDecoration;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetalleServicioHistorial extends AppCompatActivity
{

    private String _urlWebService;

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();
    CircularNetworkImageView imagenUsuario;

    private TextView nombreSolicitudDetalleHistorial,fechaSolicitudDetalleHistorial,
                     nombreClienteDetalleHistorial, direccionDetalleHistorial,
                     estadoDetalleHistorial, fechaAceptacionDetalleHistorial,
                     fechaFinalizacionDetalleHistorial,nombreEsteticistaDetalleHistorial,
                     calificacionDetalleHistorial, valorSolicitudDetalleHistorial;

    private ArrayList<Servicio> servicioList;
    private RecyclerView recyclerView;
    private ServicioDetalleHistorial mAdapter;

    private String codigoSolicitud;


    private gestionSharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_servicio_historial);

        servicioList = new ArrayList<Servicio>();
        sharedPreferences = new gestionSharedPreferences(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_servicios_detalle);


        imagenUsuario = ((CircularNetworkImageView) findViewById(R.id.imagenUsuarioDetalleHistorial));

        nombreSolicitudDetalleHistorial = (TextView) findViewById(R.id.nombreSolicitudDetalleHistorial);
        fechaSolicitudDetalleHistorial = (TextView) findViewById(R.id.fechaSolicitudDetalleHistorial);
        nombreClienteDetalleHistorial = (TextView) findViewById(R.id.nombreClienteDetalleHistorial);
        direccionDetalleHistorial = (TextView) findViewById(R.id.direccionDetalleHistorial);
        estadoDetalleHistorial = (TextView) findViewById(R.id.estadoDetalleHistorial);
        fechaAceptacionDetalleHistorial = (TextView) findViewById(R.id.fechaAceptacionDetalleHistorial);
        fechaFinalizacionDetalleHistorial = (TextView) findViewById(R.id.fechaFinalizacionDetalleHistorial);
        nombreEsteticistaDetalleHistorial = (TextView) findViewById(R.id.nombreEsteticistaDetalleHistorial);
        calificacionDetalleHistorial = (TextView) findViewById(R.id.calificacionDetalleHistorial);
        valorSolicitudDetalleHistorial = (TextView) findViewById(R.id.valorSolicitudDetalleHistorial);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ServicioDetalleHistorial(servicioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);


        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
            {
                codigoSolicitud = null;
            }
            else
            {
                codigoSolicitud = extras.getString("codigoSolicitud");
            }
        }
        else
        {
            codigoSolicitud = (String) savedInstanceState.getSerializable("codigoSolicitud");
        }


        _webServiceGetDetalleServicios(codigoSolicitud);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void _webServiceGetDetalleServicios(final String codigoSolicitud)
    {
        _urlWebService = "http://52.72.85.214/ws/DetalleHistorial";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            JSONObject object;
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            String imgUsuario = "";

                            if(status)
                            {

                                object = response.getJSONObject("result");

                                nombreSolicitudDetalleHistorial.setText("SOLICITUD N°: "+codigoSolicitud);
                                fechaSolicitudDetalleHistorial.setText(object.getString("fecSolicitudCliente"));
                                nombreClienteDetalleHistorial.setText(object.getJSONObject("cliente").getString("nombresUsuario")+" "+
                                        object.getJSONObject("cliente").getString("apellidosUsuario"));
                                direccionDetalleHistorial.setText(object.getString("direccionDomicilio"));
                                estadoDetalleHistorial.setText(object.getString("nombreEstado"));
                                fechaAceptacionDetalleHistorial.setText(object.getString("fecAceptaServicioEsteticista"));
                                fechaFinalizacionDetalleHistorial.setText(object.getString("fechaFinalizacion"));

                                nombreEsteticistaDetalleHistorial.setText(object.getJSONObject("esteticista").getString("nombresUsuario") + " " +
                                        object.getJSONObject("esteticista").getString("apellidosUsuario"));
                                calificacionDetalleHistorial.setText(object.getString("calificacionServicio"));
                                valorSolicitudDetalleHistorial.setText(object.getString("valorSolicitud"));

                                imgUsuario = object.getString("imgUsuario");

                                if (imageLoader == null)
                                    imageLoader = ControllerSingleton.getInstance().getImageLoader();

                                imagenUsuario.setImageUrl(imgUsuario, imageLoader);

                                JSONArray servicios = object.getJSONArray("servicios");

                                for (int i = 0; i <= servicios.length()-1; i++)
                                {
                                    object = servicios.getJSONObject(i);

                                    Servicio servicio = new Servicio();
                                    servicio.setImagen(object.getString("imagenServicio"));
                                    servicio.setId(object.getString("codigoServicio"));
                                    servicio.setNombreServicio(object.getString("nombreServicio"));
                                    servicio.setDescripcionServicio(object.getString("descripcionServicio"));
                                    servicio.setValorServicio(object.getString("valorServicio"));
                                    servicioList.add(servicio);

                                }


                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetalleServicioHistorial.this);
                                builder
                                        .setMessage(message)
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
                        catch (JSONException e)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(DetalleServicioHistorial.this);
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

                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        if (error instanceof TimeoutError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(DetalleServicioHistorial.this);
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
                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(DetalleServicioHistorial.this);
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
                        }

                        else

                        if (error instanceof AuthFailureError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(DetalleServicioHistorial.this);
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
                        }

                        else

                        if (error instanceof ServerError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(DetalleServicioHistorial.this);
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
                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(DetalleServicioHistorial.this);
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
                        }

                        else

                        if (error instanceof ParseError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(DetalleServicioHistorial.this);
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
                        }

                    }

                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("codigoSolicitud", codigoSolicitud);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

}
