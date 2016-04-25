package com.elements.beya.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.elements.beya.R;
import com.elements.beya.adapters.ServiciosAceptacionAdapter;

import com.elements.beya.adapters.ServiciosAdapter;
import com.elements.beya.beans.Servicio;
import com.elements.beya.decorators.DividerItemDecoration;
import com.elements.beya.services.ServiceObtenerUbicacionEsteticista;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class AceptacionServicio extends AppCompatActivity implements LocationListener, GoogleMap.OnMarkerClickListener
{



    NetworkImageView imagenEsteticista;

    TextView nombreEsteticista, apellidoEsteticista, kilometrosDistanciaEsteticista,
             tiempoLlegadaEsteticista, telefonoEsteticistaAceptacionServicios;

    public static TextView precioTemporalAceptacionServicios;

   public static TextView valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios;
   public static TextView valorTotalServiciosSeleccionados;

    private String distancia;
    private String tiempo;

    ServiceObtenerUbicacionEsteticista serviceObtenerUbicacionEsteticista;
    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 2 * 1000; // 5 seconds


    private Handler mHandler = new Handler();


    public static String serialUsuarioEsteticista;

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

    double latitudEsteticista = 0;
    double longitudEsteticista = 0;

    public static double latitudUsuario;
    public static double longitudUsuario;

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
    private String datosCliente;
    MarkerOptions options;
    Marker mapMarker;
    LatLng latLng;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aceptacion_servicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAceptacionServicio);
        setSupportActionBar(toolbar);

        serviceObtenerUbicacionEsteticista = new ServiceObtenerUbicacionEsteticista();

        precioTemporalAceptacionServicios = (TextView) findViewById(R.id.precioTemporalAceptacionServicios);



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

        valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios = (TextView) findViewById(R.id.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios);












       /* botonAceptarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                builder
                        .setMessage("¿Esta seguro de aceptar el servicio?" + "\n" + "Se procedera a atender la solicitud.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {


                    }
                }).show();
            }
        });*/

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
                datosCliente= null;
            }
            else
            {
                datosEsteticista= extras.getString("datosEsteticista");
                datosCliente= extras.getString("datosCliente");
            }
        }

        else
        {
            datosEsteticista= (String) savedInstanceState.getSerializable("datosEsteticista");
            datosCliente= (String) savedInstanceState.getSerializable("datosCliente");
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

        //_webServiceGetRoutesEsteticista();

        mAdapter = new ServiciosAceptacionAdapter(allServices);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);



        cargarDatosEsteticista();
        _webServiceObtenerServiciosEsteticista();

        mAdapter.notifyDataSetChanged();


        startService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));

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


        //startService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));




       /* if (imageLoader == null)
            imageLoader = ControllerSingleton.getInstance().getImageLoader();
        */



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

                    serviceObtenerUbicacionEsteticista = new ServiceObtenerUbicacionEsteticista();
/*
                    Toast.makeText(getApplicationContext(), "desde aceptacion servicios: "+serviceObtenerUbicacionEsteticista.getLatitud()
                                    + " : " +serviceObtenerUbicacionEsteticista.getLongitud() + " : " +
                                    serviceObtenerUbicacionEsteticista.getFechaMovimiento(),
                            Toast.LENGTH_SHORT).show();*/

                    options = new MarkerOptions();
                    latLng = new LatLng( serviceObtenerUbicacionEsteticista.getLatitud() ,
                            serviceObtenerUbicacionEsteticista.getLongitud() );
                    options.position(latLng);
                    mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(nombreEsteticista.getText().toString())).
                            setSnippet("" + serviceObtenerUbicacionEsteticista.getFechaMovimiento());
                    //mapMarker = mGoogleMap.addMarker(options);
                    //LatLng latLng
                    //mapMarker.setTitle(""+serviceObtenerUbicacionEsteticista.getFechaMovimiento());
                    Log.d("AceptacionServicio", "Marker added.............................");


                }

            });
        }

        private String getDateTime()
        {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_cancelar_aceptacion_servicio:


                AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
                builder
                        .setMessage("¿Esta seguro de cancelar el servicio? tendrá un costo de 5.000 COP.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                //startActivity(intent);
                                //finish();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                        //startActivity(intent);
                        //finish();
                    }
                }).show();
                return true;


            case R.id.action_acentar_servicios_seleccionados:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AceptacionServicio.this);
                builder1
                        .setMessage("¿Esta seguro de agregar estos servicios? no se podrá volver a seleccionar servicios.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                String data = "";

                                List<Servicio> lista_servicios = ((ServiciosAceptacionAdapter) mAdapter).getServiciosList();
                                for (int i = 0; i < lista_servicios.size(); i++)
                                {
                                    Servicio servicio = lista_servicios.get(i);

                                    if (servicio.isSelected() == true)
                                    {
                                        data = data+servicio.getId().toString()+",";

                                    }
                                }

                                if(data.isEmpty())
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
                                    builder
                                            .setMessage("Debe seleccionar al menos (1) Servicio a agregar.")
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id)
                                                {
                                                    //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                    //startActivity(intent);
                                                    //finish();
                                                }
                                            }).show();
                                    return;
                                }

                                else

                                {
                                    //BORRAR ULTIMA COMA Y SEPARARLOS POR DOS PUNTOS ':'
                                    String serviciosEscogidos = data.substring(0, data.lastIndexOf(","));
                                    sharedPreferences.putString("serviciosAgregadosEnAceptacionServicio", serviciosEscogidos);
                                    Toast.makeText(AceptacionServicio.this,
                                            "Selected Services: \n" + serviciosEscogidos, Toast.LENGTH_LONG)
                                            .show();

                                   // _webServiceEnviarServiciosAgregadosEsteticista(serviciosEscogidos);



                                }
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                //startActivity(intent);
                                //finish();
                            }
                        }).show();
                return true;


            case R.id.action_finalizar_aceptacion_servicio:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(AceptacionServicio.this);
                builder2
                        .setMessage("¿Esta seguro de finalizar servicios? se pasara al cobro.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                //startActivity(intent);
                                //finish();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                        //startActivity(intent);
                        //finish();
                    }
                }).show();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_aceptacion_servicio, menu);
        return true;
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

    public static double getLongitudUsuario() {
        return longitudUsuario;
    }

    public void setLongitudUsuario(double longitudUsuario) {
        this.longitudUsuario = longitudUsuario;
    }

    public static double getLatitudUsuario() {
        return latitudUsuario;
    }

    public static String getSerialUsuarioEsteticista() {
        return serialUsuarioEsteticista;
    }

    public void setSerialUsuarioEsteticista(String serialUsuarioEsteticista) {
        this.serialUsuarioEsteticista = serialUsuarioEsteticista;
    }

    public void setLatitudUsuario(double latitudUsuario) {
        this.latitudUsuario = latitudUsuario;
    }


    public void cargarDatosEsteticista()
    {

        String nombreUsuario = null, apellidoUsuario = null,
               imgUsuario=null,telefonoUsuario = null,serialUsuarioEsteticista = null,
               distanciaUsuario, tiempoLlegada;

        try
        {
            JSONArray jsonArray = new JSONArray(datosEsteticista);

            for (int i = 0; i < jsonArray.length(); i++)
            {

                JSONObject servicio = jsonArray.getJSONObject(i);
                nombreUsuario = servicio.getString("nombresUsuario");
                nombreUsuario += " "+servicio.getString("apellidosUsuario");
                this.setLatitudUsuario(Double.parseDouble(servicio.getString("latitudUsuario")));
                this.setLongitudUsuario(Double.parseDouble(servicio.getString("longitudUsuario")));

                imgUsuario = servicio.getString("imgUsuario");
                telefonoUsuario = servicio.getString("telefonoUsuario");
                tiempoLlegada = servicio.getString("tiempoLlegadaEsteticista");
                serialUsuarioEsteticista = servicio.getString("serialUsuario"); //Serial de Usuario Esteticista que acepta servicio.
                //sharedPreferences.putString("serialUsuarioEsteticista", serialUsuarioEsteticista);
                setSerialUsuarioEsteticista(serialUsuarioEsteticista);
                kilometrosDistanciaEsteticista.setText("Estoy a: " + tiempoLlegada);
                tiempoLlegadaEsteticista.setText("Llego en Aprox: " + tiempoLlegada);
            }



            if (imageLoader == null)
                imageLoader = ControllerSingleton.getInstance().getImageLoader();

            imagenEsteticista.setImageUrl(imgUsuario, imageLoader);

            nombreEsteticista.setText(nombreUsuario);
            //apellidoEsteticista.setText(apellidoUsuario);
            telefonoEsteticistaAceptacionServicios.setText(telefonoUsuario);
            precioTemporalAceptacionServicios.setText(""+sharedPreferences.getInt("totalServiciosEscogidosEnSolicitarServicio"));
            valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.setText(""+sharedPreferences.getInt("totalServiciosEscogidosEnSolicitarServicio"));

          /*  markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(getLatitudUsuario(),getLongitudUsuario());//esto es lo dinamico!

            markerOptions.position(latLng);
            markerOptions.title("Tu esteticista aqui!");

            // markerOptions.snippet("a 2 horas");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.beya_logo_on_map));

            mGoogleMap.addMarker(markerOptions);*/
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


    private void _webServiceObtenerServiciosEsteticista()
    {
        _urlWebService = "http://52.72.85.214/ws/ObtenerServiciosOfrecidosEsteticista";

        //progressBar.setVisibility(View.VISIBLE);

        Log.i("INFO: ", "" + getSerialUsuarioEsteticista() + " : " + "" + sharedPreferences.getString("serviciosEscogidosEnSolicitarServicio"));

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

                           // progressBar.setVisibility(View.GONE);
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
                headers.put("serialUsuarioEsteticista", getSerialUsuarioEsteticista());
                headers.put("servicios",sharedPreferences.getString("serviciosEscogidosEnSolicitarServicio"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

       // jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

    /*private void _webServiceGetRoutesEsteticista()
    {

        _urlWebService = "http://maps.google.com/maps/api/directions/json?origin="+this.getLatitudUsuario()+","+this.getLongitudUsuario()+
                "&destination="+mLatitude+","+mLongitude+"&"+"sensor=false";

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

                            kilometrosDistanciaEsteticista.setText("Estoy a: " + distOb.getString("text"));
                            tiempoLlegadaEsteticista.setText("Llego en Aprox: " + timeOb.getString("text"));


                        }
                        catch (JSONException e)
                        {

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


                });



        //jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }*/

}
