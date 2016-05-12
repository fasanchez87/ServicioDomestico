package com.elements.beya.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.elements.beya.CircularImageView.CircularNetworkImageView;
import com.elements.beya.R;
import com.elements.beya.adapters.ServiciosAceptacionAdapter;

import com.elements.beya.adapters.ServiciosAdapter;
import com.elements.beya.app.Config;
import com.elements.beya.beans.Servicio;
import com.elements.beya.beans.ValorServicio;
import com.elements.beya.decorators.DividerItemDecoration;
import com.elements.beya.fragments.SolicitarServicio;
import com.elements.beya.services.ServiceActualizarUbicacionProveedor;
import com.elements.beya.services.ServiceObtenerUbicacionEsteticista;
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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.leolin.shortcutbadger.ShortcutBadger;





public class AceptacionServicio extends AppCompatActivity implements LocationListener, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    TextView nombreEsteticista, apellidoEsteticista, kilometrosDistanciaEsteticista,
            tiempoLlegadaEsteticista, telefonoEsteticistaAceptacionServicios;

    private String calificacionEsteticista;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    public static TextView precioTemporalAceptacionServicios;

    private MenuItem menuLlegadaEsteticista;
    private MenuItem menuCancelarServicio;


    CircularNetworkImageView imagenEsteticista;

    public static TextView valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios;
    public static TextView valorTotalServiciosSeleccionados;

    private String distancia;
    private String tiempo;

    ServiceObtenerUbicacionEsteticista serviceObtenerUbicacionEsteticista;
    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 7 * 1000; // 5 seconds

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

    Location mCurrentLocation;


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
    private String codigoSolicitud;
    MarkerOptions options;
    Marker mapMarker;
    LatLng latLng;
    private static final String TAG = "ACEPTACION SERVICIO";
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 1;


    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aceptacion_servicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAceptacionServicio);
        setSupportActionBar(toolbar);



        Log.d(TAG, "onCreate ...............................");
        if (!isGooglePlayServicesAvailable())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
            builder
                    .setMessage("not google play services availables now!")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                            //startActivity(intent);
                            finish();
                        }
                    }).show();

        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_FINALIZAR_SERVICIO_ESTETICISTA))
                {
                    String codigoSolicitud = intent.getExtras().getString("codigoSolicitud");
                    displayAlertDialogFinalizarServicioCliente(codigoSolicitud);
                }

                else

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_CANCELAR_SERVICIO_ESTETICISTA))
                {
                    String codigoSolicitud = intent.getExtras().getString("codigoSolicitud");
                    AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
                    builder
                            .setMessage("Lo sentimos, el servicio ha sido cancelado por el esteticista, por favor vuelva a solicitar el servicio.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {

                                    sharedPreferences.remove("valorTotalServiciosTemporalSolicitarServicio");
                                    sharedPreferences.remove("serviciosEscogidos");
                                    sharedPreferences.remove("serviciosEscogidosEnSolicitarServicio");
                                    sharedPreferences.remove("proveedores");
                                    sharedPreferences.remove("latitudCliente");
                                    sharedPreferences.remove("longitudCliente");
                                    sharedPreferences.remove("direccionDomicilio");
                                    sharedPreferences.remove("serviciosEscojidosListaServiciosCliente");
                                    sharedPreferences.remove("serviciosEscogidosEnListaServiciosCliente");

                                    Servicio servicio = new Servicio();
                                    servicio = null;

                                    Intent intent = new Intent(AceptacionServicio.this, Gestion.class);
                                    startActivity(intent);
                                    stopService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));
                                    finish();
                                }

                    }).show().setCancelable(false);


                }

            }
        };

        calificacionEsteticista = "";

        serviceObtenerUbicacionEsteticista = new ServiceObtenerUbicacionEsteticista();

        precioTemporalAceptacionServicios = (TextView) findViewById(R.id.precioTemporalAceptacionServicios);

        sharedPreferences = new gestionSharedPreferences(this);

        allServices = new ArrayList<Servicio>();

        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_servicios_agregar_aceptacionServicios);

        imagenEsteticista = ((CircularNetworkImageView) findViewById(R.id.imagenClienteSolicitudServicioDetallada));


        nombreEsteticista = (TextView) this.findViewById(R.id.nombreEsteticistaAceptacionServicios);
        kilometrosDistanciaEsteticista = (TextView) this.findViewById(R.id.kilometrosEsteticistaAceptacionServicios);
        tiempoLlegadaEsteticista = (TextView) this.findViewById(R.id.tiempoLlegadaEsteticistaAceptacionServicios);
        valorTotalServiciosSeleccionados = (TextView) this.findViewById(R.id.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios);
        telefonoEsteticistaAceptacionServicios = (TextView) this.findViewById(R.id.telefonoEsteticistaAceptacionServicios);

        valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios = (TextView) findViewById(R.id.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios);

        checkBoxServicio = (CheckBox) this.findViewById(R.id.checkBoxServicio);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapaSeguimientoEsteticistaAceptacionServicios)).getMap();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                datosEsteticista = null;
                datosCliente = null;
                codigoSolicitud = null;
            } else {
                datosEsteticista = extras.getString("datosEsteticista");
                datosCliente = extras.getString("datosCliente");
                codigoSolicitud = extras.getString("codigoSolicitud");
            }
        } else {
            datosEsteticista = (String) savedInstanceState.getSerializable("datosEsteticista");
            datosCliente = (String) savedInstanceState.getSerializable("datosCliente");
            codigoSolicitud = (String) savedInstanceState.getSerializable("codigoSolicitud");
        }

        Log.w("ACEPTACION SERVICIO", datosEsteticista);

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);

        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;

        }

        mAdapter = new ServiciosAceptacionAdapter(allServices);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        cargarDatosEsteticista();

        mAdapter.notifyDataSetChanged();

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
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), (7 * 1000), NOTIFY_INTERVAL);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
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
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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

                    //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))


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
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        //mTimer.cancel();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

       /* // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));*/
        //ShortcutBadger.removeCount(this.getApplicationContext()); //for 1.1.4

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_FINALIZAR_SERVICIO_ESTETICISTA));


        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_CANCELAR_SERVICIO_ESTETICISTA));





    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_llego_esteticista:

                AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
                builder
                        .setMessage("¿Ha llegado el Esteticista a su domicilio?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {

                                _webNotificarLlegadaEsteticista(codigoSolicitud);
                                stopService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));
                                menuLlegadaEsteticista.setVisible(false);
                                menuCancelarServicio.setVisible(false);
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

                Intent intent = new Intent(AceptacionServicio.this, ListaServiciosCliente.class);
                intent.putExtra("codigoSolicitud", codigoSolicitud);
                intent.putExtra("datosCliente", datosCliente);
                startActivity(intent);
                //finish();
                return true;


            case R.id.action_cancelar_servicio:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(AceptacionServicio.this);
                builder2
                        .setMessage("¿Esta seguro de cancelar el servicio? Tendrá una penalidad de 5.000 COP")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                String indicaMulta = "1";
                                _webServiceCancelarSolicitudServicioCliente(codigoSolicitud, indicaMulta);
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
                }).setCancelable(false).show();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menuLlegadaEsteticista.setVisible(true);
        menuCancelarServicio.setVisible(true);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_aceptacion_servicio, menu);
        menuLlegadaEsteticista = (MenuItem) menu.findItem(R.id.action_llego_esteticista);
        menuCancelarServicio = (MenuItem) menu.findItem(R.id.action_cancelar_servicio);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLatitude = mCurrentLocation.getLatitude();
        mLongitude = mCurrentLocation.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(100));

        //updateUI();
    }

    private void updateUI()
    {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());


            Toast.makeText(getApplicationContext(),"At Time: " + "\n" +
                            "Latitude: " + lat + "\n" +
                            "Longitude: " + lng + "\n" +
                            "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                            "Provider: " + mCurrentLocation.getProvider(),
                    Toast.LENGTH_SHORT).show();



        } else {
            Log.d(TAG, "location is null ...............");
        }
    }



    public void setLongitudUsuario(double longitudUsuario) {
        this.longitudUsuario = longitudUsuario;
    }

    public void setSerialUsuarioEsteticista(String serialUsuarioEsteticista) {
        this.serialUsuarioEsteticista = serialUsuarioEsteticista;
    }

    public void setLatitudUsuario(double latitudUsuario) {
        this.latitudUsuario = latitudUsuario;
    }

    public void cargarDatosEsteticista()
    {
        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);


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
            precioTemporalAceptacionServicios.setText("" + nf.format(ValorServicio.getValorServicio()));
            valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.setText
                    ("" + SolicitarServicio.valorTotalTextView.getText());






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

    public void onRadioButtonClicked(View view)
    {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId())
        {
            case R.id.radioButton1Estrellas:
                if (checked)
                    // Pirates are the best
                    calificacionEsteticista = "1";
                break;
            case R.id.radioButton2Estrellas:
                if (checked)
                    // Ninjas rule
                    calificacionEsteticista = "2";
                break;
            case R.id.radioButton3Estrellas:
                if (checked)
                    // Ninjas rule
                    calificacionEsteticista = "3";
                break;
            case R.id.radioButton4Estrellas:
                if (checked)
                    // Ninjas rule
                    calificacionEsteticista = "4";
                break;
            case R.id.radioButton5Estrellas:
                if (checked)
                    // Ninjas rule
                    calificacionEsteticista = "5";
                break;
        }
    }

    public void displayAlertDialogFinalizarServicioCliente(final String codigoSolicitud)
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_finalizar_cliente, null);
        RadioButton radioTiempoButton;

        final RadioGroup RadioGroup = (RadioGroup) alertLayout.findViewById(R.id.radioGroupCalificacionEsteticista);
        final Button buttonEnviarCalificacionEsteticista = (Button) alertLayout.findViewById(R.id.buttonEnviarCalificacionEsteticista);
        final EditText editTextObservacionClienteFinalizarServicio = (EditText) alertLayout.findViewById(R.id.editTextObservacionClienteFinalizarServicio);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Calificación Servicio");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        buttonEnviarCalificacionEsteticista.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                stopService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));*/
                sharedPreferences.remove("valorTotalServiciosTemporalSolicitarServicio");
                sharedPreferences.remove("serviciosEscogidos");
                sharedPreferences.remove("serviciosEscogidosEnSolicitarServicio");
                sharedPreferences.remove("proveedores");
                sharedPreferences.remove("latitudCliente");
                sharedPreferences.remove("longitudCliente");
                sharedPreferences.remove("direccionDomicilio");
                sharedPreferences.remove("serviciosEscojidosListaServiciosCliente");
                sharedPreferences.remove("serviciosEscogidosEnListaServiciosCliente");

                Servicio servicio = new Servicio();
                servicio = null;

                String comentarioCliente = editTextObservacionClienteFinalizarServicio.getText().toString();
                _webServiceCalificarServicio(codigoSolicitud, comentarioCliente, calificacionEsteticista);
                dialog.dismiss();
                Intent intent = new Intent(AceptacionServicio.this, Gestion.class);
                startActivity(intent);

                stopService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));

                finish();
            }
        });

        dialog.show();


    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        return false;
    }

    private void _webServiceCalificarServicio( final String codigoSolicitud, final String comentario, final String calificacionEsteticista)
    {
        _urlWebService = "http://52.72.85.214/ws/CalificarServicio";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if(status)
                            {

                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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
                headers.put("codigoSolicitud", codigoSolicitud);
                headers.put("observaServicio", comentario);
                headers.put("calificacionServicio", calificacionEsteticista);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        // jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }


    private void _webNotificarLlegadaEsteticista( final String codigoSolicitud )
    {
        _urlWebService = "http://52.72.85.214/ws/NotificarLlegadaEsteticista";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if(status)
                            {

                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this.getApplicationContext());
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
                headers.put("codigoSolicitud", codigoSolicitud);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        // jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

    public void _webServiceCancelarSolicitudServicioCliente(final String codigoSolicitud, final String indicaMulta)
    {
        _urlWebService = "http://52.72.85.214/ws/CancelarSolicitudCliente";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if(status)
                            {

                                AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
                                builder
                                .setMessage(message)
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        sharedPreferences.remove("valorTotalServiciosTemporalSolicitarServicio");
                                        sharedPreferences.remove("serviciosEscogidos");
                                        sharedPreferences.remove("serviciosEscogidosEnSolicitarServicio");
                                        sharedPreferences.remove("proveedores");
                                        sharedPreferences.remove("latitudCliente");
                                        sharedPreferences.remove("longitudCliente");
                                        sharedPreferences.remove("direccionDomicilio");
                                        sharedPreferences.remove("serviciosEscojidosListaServiciosCliente");
                                        sharedPreferences.remove("serviciosEscogidosEnListaServiciosCliente");

                                        Servicio servicio = new Servicio();
                                        servicio = null;

                                        Intent intent = new Intent(AceptacionServicio.this, Gestion.class);
                                        startActivity(intent);
                                        stopService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));
                                        finish();
                                    }
                                }).setCancelable(false).show();
                            }

                            else
                            {
                               AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
                                builder
                                        .setMessage("Error cancelando la solicitud, intente de nuevo")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                //startActivity(intent);
                                                //finish();
                                            }
                                        }).setCancelable(false).show();
                            }
                        }
                        catch (JSONException e)
                        {


                            //progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AceptacionServicio.this);
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
                headers.put("codigoSolicitud", codigoSolicitud);
                headers.put("indicaMulta", indicaMulta);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;


            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 6, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
    }






}
