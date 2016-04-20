package com.elements.beya.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.elements.beya.R;
import com.elements.beya.adapters.ServiciosAceptacionAdapter;
import com.elements.beya.adapters.ServiciosAdapter;

import com.elements.beya.beans.Servicio;
import com.elements.beya.decorators.DividerItemDecoration;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AceptacionServicio extends AppCompatActivity implements LocationListener, GoogleMap.OnMarkerClickListener
{

    NetworkImageView imagenEsteticista;

    TextView nombreEsteticista, apellidoEsteticista, kilometrosDistanciaEsteticista,
             tiempoLlegadaEsteticista, valorTotalServiciosSeleccionados, telefonoEsteticistaAceptacionServicios;



    private String distancia;
    private String tiempo;

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    Button botonFinalizarOrdenServicio, botonCancelarOrdenServicio;

    private String ubicacionEsteticista;

    GoogleMap mGoogleMap;

    private boolean isCheckedSwitch;

    Spinner mSprPlaceType;
    MapView mapView;
    private Marker marker;
    private MarkerOptions markerOptions;

    double mLatitude = 0;
    double mLongitude = 0;

    private double latitudUsuario;

    private double longitudUsuario;

    GoogleApiClient mGoogleApiClient;

    Gestion gestion;

    CheckBox checkBoxServicio;

    public SwitchCompat switchActivarLocation;

    private gestionSharedPreferences sharedPreferences;

    ProgressBar progressBar;

    JSONArray jsonArray;

    private ArrayList<Servicio> allServices;

    private RecyclerView recyclerView;
    private ServiciosAceptacionAdapter mAdapter;

    private String _urlWebService;

    private String datosEsteticista;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aceptacion_servicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAceptacionServicio);
        setSupportActionBar(toolbar);


        sharedPreferences = new gestionSharedPreferences(this);

        sharedPreferences = new gestionSharedPreferences(this);
        allServices = new ArrayList<Servicio>();

        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_servicios_agregar_aceptacionServicios);

        imagenEsteticista = (NetworkImageView) this.findViewById(R.id.imagenEsteticistaAceptacionServicios);

        nombreEsteticista = (TextView)this.findViewById(R.id.nombreEsteticistaAceptacionServicios);
        kilometrosDistanciaEsteticista = (TextView)this.findViewById(R.id.kilometrosEsteticistaAceptacionServicios);
        tiempoLlegadaEsteticista = (TextView)this.findViewById(R.id.tiempoLlegadaEsteticistaAceptacionServicios);
        valorTotalServiciosSeleccionados = (TextView)this.findViewById(R.id.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios);
        telefonoEsteticistaAceptacionServicios = (TextView)this.findViewById(R.id.telefonoEsteticistaAceptacionServicios);

        botonFinalizarOrdenServicio = (Button) this.findViewById(R.id.botonFinalizarServiciosEsteticistaAceptacionServicios);
        botonCancelarOrdenServicio = (Button) this.findViewById(R.id.botonCancelarServicioEsteticistaAceptacionServicios);

        checkBoxServicio = (CheckBox) this.findViewById(R.id.checkBoxServicio);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapaSeguimientoEsteticistaAceptacionServicios)).getMap();

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getBaseContext());

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras == null)
            {
                datosEsteticista= null;
            }
            else
            {
                datosEsteticista= extras.getString("codigoSolicitud");
            }
        }

        else
        {
            datosEsteticista= (String) savedInstanceState.getSerializable("codigoSolicitud");
        }

        Log.w("ACEPTACION SERVICIO", datosEsteticista);


        if (status != ConnectionResult.SUCCESS)
        { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
            return;
        }

        else
        {

            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);

            if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;

                }


            //Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           /* if (location!=null){
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String locLat = String.valueOf(latitude)+","+String.valueOf(longitude);
            }*/


            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, false);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null)
            {
                onLocationChanged(location);
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
            }



            locationManager.requestLocationUpdates(provider, 90000, 0, this);

           /* mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();*/

            //SE OBTIENE LAS COORDENADAS DEL CLIENTE QUE SOLICITA EL SERVICIO.
          /*  sharedPreferences.putDouble("latitudCliente", mLatitude);
            sharedPreferences.putDouble("longitudCliente",mLongitude);*/

           // cargarProveedoresServicios();

            //EVENTO BOTON SOLICITAR SERVICIOS A LOS PROVEEDORES DE SERVICIO MEDIANTE PUSH.
          /*  buttonSeleccionarServicioFragmentSolicitarServicio = (Button) this.getActivity().
                    findViewById(R.id.buttonSeleccionarServicioFragmentSolicitarServicio);
            buttonSeleccionarServicioFragmentSolicitarServicio.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    _webServiceEnviarNotificacionPushATodos( sharedPreferences.getString("serialUsuario") );
                }
            });*/

        }

        _webServiceGetRoutesEsteticista();

        mAdapter = new ServiciosAceptacionAdapter(allServices);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);



        cargarDatosEsteticista();
        _webServiceGetAllServices();

        mAdapter.notifyDataSetChanged();



       /* if (imageLoader == null)
            imageLoader = ControllerSingleton.getInstance().getImageLoader();
        */



    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    public String getUbicacionEsteticista()
    {
        return ubicacionEsteticista;
    }

    public void setUbicacionEsteticista(String ubicacionEsteticista)
    {
        this.ubicacionEsteticista = ubicacionEsteticista;
    }

    public double getLongitudUsuario() {
        return longitudUsuario;
    }

    public void setLongitudUsuario(double longitudUsuario) {
        this.longitudUsuario = longitudUsuario;
    }

    public double getLatitudUsuario() {
        return latitudUsuario;
    }

    public void setLatitudUsuario(double latitudUsuario) {
        this.latitudUsuario = latitudUsuario;
    }


    public void cargarDatosEsteticista()
    {

        String nombreUsuario = null, apellidoUsuario = null,
               imgUsuario=null,telefonoUsuario = null,serialUsuario,
               distanciaUsuario, tiempoUsuario;

        _webServiceGetRoutesEsteticista();

        try
        {
            JSONArray jsonArray = new JSONArray(datosEsteticista);

            for (int i = 0; i < jsonArray.length(); i++)
            {

                JSONObject servicio = jsonArray.getJSONObject(i);
                nombreUsuario = servicio.getString("nombresUsuario");
                nombreUsuario += " "+servicio.getString("apellidosUsuario");
                setLatitudUsuario(latitudUsuario = Double.parseDouble(servicio.getString("latitudUsuario")));
                setLongitudUsuario(longitudUsuario = Double.parseDouble(servicio.getString("longitudUsuario")));
                imgUsuario = servicio.getString("imgUsuario");
                telefonoUsuario = servicio.getString("telefonoUsuario");

            }

            if (imageLoader == null)
                imageLoader = ControllerSingleton.getInstance().getImageLoader();

            imagenEsteticista.setImageUrl(imgUsuario,imageLoader);

            nombreEsteticista.setText(nombreUsuario);
            //apellidoEsteticista.setText(apellidoUsuario);
            telefonoEsteticistaAceptacionServicios.setText(telefonoUsuario);

            markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(getLatitudUsuario(),getLongitudUsuario());//esto es lo dinamico!

            markerOptions.position(latLng);
            markerOptions.title("Tu esteticista aqui!");

            // markerOptions.snippet("a 2 horas");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.beya_logo_on_map));

            mGoogleMap.addMarker(markerOptions);









        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        return false;
    }


    private void _webServiceGetAllServices()
    {
        _urlWebService = "http://52.72.85.214/ws/ObtenerServicios";

        //progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            String message = response.getString("message");
                            Log.w("Mensaje : ", message);
                            JSONArray servicios = response.getJSONArray("result");
                            JSONObject object;

                            for (int i = 0; i <= servicios.length()-1; i++)
                            {
                                object = servicios.getJSONObject(i);

                                Servicio servicio = new Servicio();
                                servicio.setImagen(object.getString("imagenServicio"));
                                servicio.setId(object.getString("codigoServicio"));
                                servicio.setNombreServicio(object.getString("nombreServicio"));
                                servicio.setDescripcionServicio(object.getString("descripcionServicio"));
                                servicio.setValorServicio(object.getString("valorServicio"));
                                allServices.add(servicio);

                            }

                            //progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();

                        }
                        catch (JSONException e)
                        {

                            progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                        //progressBar.setVisibility(View.GONE);
                        //buttonSeleccionarServicios.setVisibility(View.GONE);
                    }


                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

    private void _webServiceGetRoutesEsteticista()
    {



        Log.i("LATITUD ESTETICISTA", ""+getLatitudUsuario());
        Log.i("LONGITUD ESTETICISTA", ""+getLongitudUsuario());
        Log.i("LATITUD CLIENTE", ""+mLatitude);
        Log.i("LONGITUD CLIENTE", ""+mLongitude); //sacaba error porque la location del emulador siempre es 0.0.

        _urlWebService = "http://maps.google.com/maps/api/directions/json?origin="+"4.0857975,-76.1754153"+"&destination="+
                mLatitude+","+mLongitude+"&"+"sensor=false";


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {

                            JSONArray routeArray = response.getJSONArray("routes");
                            JSONObject routes = routeArray.getJSONObject(0);

                            JSONArray newTempARr = routes.getJSONArray("legs");
                            JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                            JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                            JSONObject timeOb = newDisTimeOb.getJSONObject("duration");


                            Log.i("Distance :", "" + distOb.getString("text"));
                            Log.i("Time :", "" + timeOb.getString("text"));


                            kilometrosDistanciaEsteticista.setText("" + distOb.getString("text"));
                            tiempoLlegadaEsteticista.setText("" + timeOb.getString("text"));


                        }
                        catch (JSONException e)
                        {

                            //progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                           /* AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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
                                    }).show();*/



                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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

                        //progressBar.setVisibility(View.GONE);
                        //buttonSeleccionarServicios.setVisibility(View.GONE);
                    }


                });



        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

}
