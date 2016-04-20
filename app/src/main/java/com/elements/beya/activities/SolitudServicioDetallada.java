package com.elements.beya.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
import com.elements.beya.R;
import com.elements.beya.adapters.ServiciosAdapter;
import com.elements.beya.adapters.ServiciosDisponiblesAdapter;
import com.elements.beya.adapters.ServiciosSeleccionadosPush;
import com.elements.beya.beans.Proveedor;
import com.elements.beya.beans.Servicio;
import com.elements.beya.beans.SolicitudServicio;
import com.elements.beya.decorators.DividerItemDecoration;
import com.elements.beya.services.ServiceActualizarUbicacionProveedor;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolitudServicioDetallada extends AppCompatActivity implements LocationListener, GoogleMap.OnMarkerClickListener
{

    private String _urlWebServiceAceptarSolicitudServicio;



    private String ubicacionEsteticista;
    Double latitud, longitud;

    Button botonAceptarServicio, botonRechazarServicio;

    private boolean isCheckedSwitch;


    ProgressBar progressBar;
    private RecyclerView recyclerView;

    GoogleMap mGoogleMap;
    Spinner mSprPlaceType;
    MapView mapView;
    private Marker marker;
    private MarkerOptions markerOptions;

    double mLatitude = 0;
    double mLongitude = 0;

    GoogleApiClient mGoogleApiClient;

    Gestion gestion;

    CheckBox checkBoxServicio;

    private ArrayList<Servicio> servicioList;

    public SwitchCompat switchActivarLocation;



    private gestionSharedPreferences sharedPreferences;

    private ServiciosSeleccionadosPush mAdapter;
    ArrayList<String> locationCliente;


    private String keyCodigoSolicitudSeleccionado, keyCodigoClienteSolicitudSeleccionada, ubicacionCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solitud_servicio_detallada);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = new gestionSharedPreferences(this);

        Intent intent = getIntent();
        keyCodigoSolicitudSeleccionado = intent.getStringExtra("codigoSolicitud");
        ubicacionCliente = intent.getStringExtra("ubicacionCliente");
        keyCodigoClienteSolicitudSeleccionada = intent.getStringExtra("codigoCliente");

        gestion = new Gestion();




        botonRechazarServicio = (Button) findViewById(R.id.buttonRechazarServicioViaPush);
        botonRechazarServicio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapClienteSolicitudServicioViaPush)).getMap();

        servicioList = new ArrayList<Servicio>();



        locationCliente = new ArrayList<String>(Arrays.asList(ubicacionCliente.split(",")));

        latitud = Double.parseDouble(locationCliente.get(0));
        longitud = Double.parseDouble(locationCliente.get(1));

        servicioList = sharedPreferences.getHashMapObjectServicio().get(keyCodigoSolicitudSeleccionado);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_servicioSolictadoViaPush);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getBaseContext());


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

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
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
            Log.i("SolitudServicioDetallada", String.valueOf(location));


            if (location != null)
            {
                onLocationChanged(location);
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();

            }

            ubicacionEsteticista = mLatitude+","+mLongitude;
            setUbicacionEsteticista(ubicacionEsteticista);

            botonAceptarServicio = (Button) findViewById(R.id.buttonAceptarServicioViaPush);
            botonAceptarServicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                    builder
                            .setMessage("¿Esta seguro de aceptar el servicio?" + "\n" + "Se procedera a atender la solicitud.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {


                                    _webServiceAceptarSolicitudServicios();
                                /*    isCheckedSwitch = false;// PENDIENTE
                                    sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);

                                    String statusOnline = "0";
                                    stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                                    sharedPreferences.putString("statusOnline", statusOnline);
*/
/*

                                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                                    Menu menu = navigationView.getMenu();
                                    MenuItem item = menu.findItem(R.id.nav_activar_geolocalizacion);
                                    switchActivarLocation = (SwitchCompat) MenuItemCompat.getActionView(item);
                                    switchActivarLocation.setChecked(sharedPreferences.getBoolean("isCheckedSwitch"));
                                   *//* Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
                                    startActivity(intent);
                                    finish();*/



                                }
                            }).setNegativeButton("Volver", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                            //startActivity(intent);
                            //finish();
                        }
                    }).show();
                }
            });


            //SE OBTIENE LAS COORDENADAS DEL CLIENTE QUE SOLICITA EL SERVICIO.
            /*sharedPreferences.putDouble("latitudEsteticista", mLatitude);
            sharedPreferences.putDouble("longitudEsteticista", mLongitude);*/


            locationManager.requestLocationUpdates(provider, 90000, 0, this);

            markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(latitud,longitud);
            markerOptions.position(latLng);
            markerOptions.title("Tu cliente aqui!");

           // markerOptions.snippet("a 2 horas");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.beya_logo_on_map));

            mGoogleMap.addMarker(markerOptions);



        }




        mAdapter = new ServiciosSeleccionadosPush(servicioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
    public String getUbicacionEsteticista() {
        return ubicacionEsteticista;
    }

    public void setUbicacionEsteticista(String ubicacionEsteticista) {
        this.ubicacionEsteticista = ubicacionEsteticista;
    }


    public void _webServiceAceptarSolicitudServicios()
    {
        _urlWebServiceAceptarSolicitudServicio = "http://52.72.85.214/ws/AceptarSolicitudServicio";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceAceptarSolicitudServicio, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                           // String message = response.getString("status");

                            if(status)
                            {
                               AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage("bien")
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
                            {
                               AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage("error")
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
                        catch (JSONException e)
                        {


                            //progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this.getApplicationContext());
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
                headers.put("serialUsuarioEsteticista", sharedPreferences.getString("serialUsuario"));
                headers.put("serialUsuarioCliente", keyCodigoClienteSolicitudSeleccionada);
                headers.put("codigoSolicitud", keyCodigoSolicitudSeleccionado);
                headers.put("ubicacionEsteticista", getUbicacionEsteticista());
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;


            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 6, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            sharedPreferences.remove(keyCodigoSolicitudSeleccionado);
            this.finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        setUbicacionEsteticista(ubicacionEsteticista);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
