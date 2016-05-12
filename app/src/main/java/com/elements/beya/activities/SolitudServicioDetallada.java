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
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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
import com.elements.beya.CircularImageView.CircularNetworkImageView;
import com.elements.beya.R;
import com.elements.beya.adapters.ServiciosAdapter;
import com.elements.beya.adapters.ServiciosDisponiblesAdapter;
import com.elements.beya.adapters.ServiciosSeleccionadosPush;
import com.elements.beya.app.Config;
import com.elements.beya.beans.Proveedor;
import com.elements.beya.beans.Servicio;
import com.elements.beya.beans.SolicitudServicio;
import com.elements.beya.beans.SolicitudServicioSeleccionada;
import com.elements.beya.decorators.DividerItemDecoration;
import com.elements.beya.fragments.ServiciosDisponibles;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolitudServicioDetallada extends AppCompatActivity implements LocationListener,
        GoogleMap.OnMarkerClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();


    private String _urlWebServiceAceptarSolicitudServicio;
    private String _urlWebServiceCancelarSolicitudServicio;


    private String ubicacionEsteticista;
    Double latitud, longitud;

    Button botonFinalizarServicio, buttonLlegadaEsteticista;

    public TextView nombreClienteSolicitudServicioDetallada, telefonoClienteSolicitudServicioDetallada,
            fechaClienteSolicitudServicioDetallada,DireccionClienteSolicitudServicioDetallada;

    public static TextView precioClienteSolicitudServicioDetallada;

    private String tiempoLlegada;


    private boolean isCheckedSwitch;

    private BroadcastReceiver mRegistrationBroadcastReceiver;



    ProgressBar progressBar;
    private RecyclerView recyclerView;

    GoogleMap mGoogleMap;
    Spinner mSprPlaceType;
    MapView mapView;
    private Marker marker;
    private MarkerOptions markerOptions;

    private MenuItem menuDetalleServicio;
    private MenuItem menuRevisarServicios;
    private MenuItem menuAceptarSolocitud;
    private MenuItem action_cancelar_aceptacion_servicio_esteticista;
    private boolean mostrarItemDetalleServicio = false;

    double mLatitude = 0;
    double mLongitude = 0;

    GoogleApiClient mGoogleApiClient;

    Location mCurrentLocation;


    Gestion gestion;

    CheckBox checkBoxServicio;

    private ArrayList<Servicio> servicioList;

    public SwitchCompat switchActivarLocation;
    String datosCliente;



    private gestionSharedPreferences sharedPreferences;

    private ServiciosSeleccionadosPush mAdapter;
    ArrayList<String> locationCliente;


    private String keyCodigoSolicitudSeleccionado, costoSolicitud, direccionDomicilio,keyCodigoClienteSolicitudSeleccionada,
            ubicacionCliente, nombreUsuario, fechaSolicitud, telefonoCliente,imgUsuario;




    private static final String TAG = "SOLICITUD SERVICIO DETALLADA";
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    private String statusOnline;


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
        setContentView(R.layout.activity_solitud_servicio_detallada);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "onCreate ...............................");
        if (!isGooglePlayServicesAvailable())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_CANCELAR_SERVICIO_ESTETICISTA))
                {
                    String codigoSolicitud = intent.getExtras().getString("codigoSolicitud");
                    //displayAlertDialogFinalizarServicioCliente(codigoSolicitud);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                    builder
                            .setMessage("La solicitud ha sido cancelada por el Cliente")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {

                                    isCheckedSwitch = true;
                                    sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                                    statusOnline = "1";
                                    sharedPreferences.putString("statusOnline", statusOnline);
                                    Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).setCancelable(false).show();
                }

                else

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_LLEGADA_ESTETICISTA))
                {
                    buttonLlegadaEsteticista.setEnabled(true);

                }
            }
        };

        sharedPreferences = new gestionSharedPreferences(this);

        Intent intent = getIntent();
        keyCodigoSolicitudSeleccionado = intent.getStringExtra("codigoSolicitud");
        ubicacionCliente = intent.getStringExtra("ubicacionCliente");
        keyCodigoClienteSolicitudSeleccionada = intent.getStringExtra("codigoCliente");
        nombreUsuario = intent.getStringExtra("nombreUsuario");
        fechaSolicitud = intent.getStringExtra("fechaSolicitud");
        telefonoCliente = intent.getStringExtra("telefonoUsuario");
        direccionDomicilio = intent.getStringExtra("direccionDomicilio");
        costoSolicitud = intent.getStringExtra("costoSolicitud");
        imgUsuario = intent.getStringExtra("imgUsuario");


        gestion = new Gestion();

        tiempoLlegada = "";


        TextView nombreClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.nombreClienteSolicitudServicioDetallada);
        TextView telefonoClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.telefonoClienteSolicitudServicioDetallada);
        TextView fechaClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.fechaClienteSolicitudServicioDetallada);
        precioClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.precioClienteSolicitudServicioDetallada);
        TextView DireccionClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.DireccionClienteSolicitudServicioDetallada);
        CircularNetworkImageView imagenCliente = ((CircularNetworkImageView) findViewById(R.id.imagenClienteSolicitudServicioDetallada));
        imagenCliente.setImageUrl(imgUsuario, imageLoader);

        nombreClienteSolicitudServicioDetallada.setText(nombreUsuario);
        fechaClienteSolicitudServicioDetallada.setText(fechaSolicitud);
        precioClienteSolicitudServicioDetallada.setText(costoSolicitud);
        telefonoClienteSolicitudServicioDetallada.setText(telefonoCliente);
        DireccionClienteSolicitudServicioDetallada.setText(direccionDomicilio);


        botonFinalizarServicio = (Button) findViewById(R.id.buttonFinalizarServicio);
        botonFinalizarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                builder
                        .setMessage("¿Esta seguro de finalizar el servicio?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                displayAlertDialogFinalizarServicio();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                        //startActivity(intent);
                        //finish();
                    }
                }).show();


            }
        });


        buttonLlegadaEsteticista = (Button) findViewById(R.id.buttonLlegadaEsteticista);
        buttonLlegadaEsteticista.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                builder
                        .setMessage("¿Esta seguro de haber llegado al domicilio?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {

                                //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                                isCheckedSwitch = false;
                                sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                                statusOnline = "0";
                                sharedPreferences.putString("statusOnline", statusOnline);
                                stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                                buttonLlegadaEsteticista.setVisibility(View.GONE);
                                botonFinalizarServicio.setEnabled(true);
                                action_cancelar_aceptacion_servicio_esteticista.setVisible(false);


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



            ubicacionEsteticista = mLatitude+","+mLongitude;
            setUbicacionEsteticista(ubicacionEsteticista);

            markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(latitud,longitud);
            markerOptions.position(latLng);
            markerOptions.title("Tu cliente aqui!");


           // markerOptions.snippet("a 2 horas");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.beya_logo_on_map));

            mGoogleMap.addMarker(markerOptions);

        mAdapter = new ServiciosSeleccionadosPush(servicioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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
    public void onPause() {
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
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
                new IntentFilter(Config.PUSH_NOTIFICATION_CANCELAR_SERVICIO_ESTETICISTA));

        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_LLEGADA_ESTETICISTA));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates()
    {
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menuDetalleServicio.setVisible(false);
        menuRevisarServicios.setVisible(true);
        menuAceptarSolocitud.setVisible(true);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_esteticista_solicitud_detallada, menu);

        menuDetalleServicio = (MenuItem) menu.findItem(R.id.action_observar_servicios);
        menuRevisarServicios = (MenuItem) menu.findItem(R.id.action_revisar_servicios_solicitud);
        menuAceptarSolocitud = (MenuItem) menu.findItem(R.id.action_aceptar_servicio_aceptacion_servicio_detallado);
        action_cancelar_aceptacion_servicio_esteticista = (MenuItem) menu.findItem(R.id.action_cancelar_aceptacion_servicio_esteticista);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_observar_servicios:
                Intent intent = new Intent(SolitudServicioDetallada.this, ListaServiciosEsteticista.class);
                intent.putExtra("codigoSolicitud", keyCodigoSolicitudSeleccionado);
                startActivity(intent);
                return true;

            case R.id.action_cancelar_aceptacion_servicio_esteticista:
                //cancelar servicio por parte del esteticista
                AlertDialog.Builder builder2 = new AlertDialog.Builder(SolitudServicioDetallada.this);
                builder2
                        .setMessage("¿Esta seguro de cancelar el servicio? Tendrá una penalidad de 5.000 COP")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                             public void onClick(DialogInterface dialog, int id)
                            {
                                displayAlertDialogCancelarServicioEsteticista();
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

            case R.id.action_aceptar_servicio_aceptacion_servicio_detallado:
            displayAlertDialog();
            return true;

            case R.id.action_revisar_servicios_solicitud:
                Intent intentRevisionServicios = new Intent(SolitudServicioDetallada.this, ListaServiciosRevisarEsteticista.class);
                intentRevisionServicios.putExtra("codigoSolicitud", keyCodigoSolicitudSeleccionado);
                intentRevisionServicios.putExtra("costoSolicitud", costoSolicitud);
                startActivity(intentRevisionServicios);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRadioButtonClicked(View view)
    {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonDiezMinutos:
                if (checked)
                    // Pirates are the best
                    tiempoLlegada = "10 Minutos";
                    break;
            case R.id.radioButtonTreintaMinutos:
                if (checked)
                    // Ninjas rule
                    tiempoLlegada = "30 Minutos";

                break;
            case R.id.radioButtonCincuentaMinutos:
                if (checked)
                    // Ninjas rule
                    tiempoLlegada = "50 Minutos";

                break;
        }
    }

    public void displayAlertDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_aceptacion_servicio_esteticista, null);
        RadioButton radioTiempoButton;

        final RadioGroup RadioGroup = (RadioGroup) alertLayout.findViewById(R.id.radioGroupTiemposLlegadaEsteticista);
        final Button buttonAceptarServicioDialogAceptacionServicio = (Button) alertLayout.findViewById(R.id.buttonAceptarServicioDialogAceptacionServicio);
        final Button buttonCancelarServicioDialogAceptacionServicio = (Button) alertLayout.findViewById(R.id.buttonCancelarServicioDialogAceptacionServicio);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmar Solicitud");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        buttonAceptarServicioDialogAceptacionServicio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                _webServiceAceptarSolicitudServicios();
                mostrarItemDetalleServicio = true;
                menuDetalleServicio.setVisible(true);
                menuRevisarServicios.setVisible(false);
                menuAceptarSolocitud.setVisible(false);
                action_cancelar_aceptacion_servicio_esteticista.setVisible(true);

                dialog.dismiss();
            }
        });

        buttonCancelarServicioDialogAceptacionServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();


    }

    public void displayAlertDialogFinalizarServicio()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_finalizar_servicio_esteticista, null);
        RadioButton radioTiempoButton;

        final Button buttonEnviarComentarioFinalizarServicio = (Button) alertLayout.findViewById(R.id.buttonEnviarComentarioFinalizarServicio);
        final EditText editTextComentariosServicioPrestadoEsteticista = (EditText)
                         alertLayout.findViewById(R.id.editTextComentariosServicioPrestadoEsteticista);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Finalizar Servicio");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        buttonEnviarComentarioFinalizarServicio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

               /* stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                stopService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));*/
               /* isCheckedSwitch = false;
                sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                statusOnline = "0";
                sharedPreferences.putString("statusOnline", statusOnline);*/

                //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                isCheckedSwitch = true;
                sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                statusOnline = "1";
                sharedPreferences.putString("statusOnline", statusOnline);
                startService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                buttonLlegadaEsteticista.setVisibility(View.GONE);

                String comentarioEsteticista = editTextComentariosServicioPrestadoEsteticista.getText().toString();
                _webServiceFinalizarServicio(comentarioEsteticista);
                dialog.dismiss();
                Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
                startActivity(intent);
                finish();





            }
        });

        dialog.show();

    }

    public String getUbicacionEsteticista() {
        return ubicacionEsteticista;
    }

    public void setUbicacionEsteticista(String ubicacionEsteticista) {
        this.ubicacionEsteticista = ubicacionEsteticista;
    }

    public void displayAlertDialogCancelarServicioEsteticista()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_finalizar_servicio_esteticista, null);

        final Button buttonEnviarEsteticistaCancelarServicio = (Button) alertLayout.findViewById(R.id.buttonEnviarEsteticistaCancelarServicio);
        final EditText editTextObservacionEsteticistaCancelarServicio = (EditText) alertLayout.findViewById(R.id.editTextObservacionEsteticistaCancelarServicio);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Cancelación Servicio");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        buttonEnviarEsteticistaCancelarServicio.setOnClickListener(new View.OnClickListener()
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

                String observacionEsteticista = editTextObservacionEsteticistaCancelarServicio.getText().toString();
                stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                isCheckedSwitch = false;
                sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                statusOnline = "0";
                sharedPreferences.putString("statusOnline", statusOnline);
                _webServiceCancelarSolicitudServicioEsteticista(keyCodigoSolicitudSeleccionado, observacionEsteticista);
                dialog.dismiss();

            }
        });

        dialog.show();


    }


    public void _webServiceCancelarSolicitudServicioEsteticista(final String codigoSolicitud, final String observacionEsteticista)
    {
        _urlWebServiceCancelarSolicitudServicio = "http://52.72.85.214/ws/CancelarSolicitudEsteticista";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceCancelarSolicitudServicio, null,
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage(message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).setCancelable(false).show();
                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage(message)
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
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
                headers.put("observacionEsteticista", observacionEsteticista);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;


            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 6, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
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
                            String message = response.getString("message");

                            if(status)
                            {



                               /* JSONObject json = response.getJSONObject("datosCliente");
                                String name = json.getString("nombresUsuario");*/

                                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage("Solicitud de servicio en Marcha, guiese en el mapa para llegar donde su Cliente.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                //startActivity(intent);
                                                //finish();

                                                botonFinalizarServicio.setVisibility(View.VISIBLE);
                                                buttonLlegadaEsteticista.setVisibility(View.VISIBLE);
                                                botonFinalizarServicio.setEnabled(false);


                                            }
                                        }).show();
                            }

                            else
                            {
                               AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage("Error Solicitud Esteticista: "+message)
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
                headers.put("tiempoLlegadaEsteticista", tiempoLlegada);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;


            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 6, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
    }

    public void _webServiceFinalizarServicio(final String comentarioEsteticista)
    {
        _urlWebServiceAceptarSolicitudServicio = "http://52.72.85.214/ws/FinalizarServicio";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebServiceAceptarSolicitudServicio, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            // String message = response.getString("status");

                            if(status)
                            {


                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage(message)
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
                headers.put("codigoSolicitud", keyCodigoSolicitudSeleccionado);
                headers.put("observaServicio", comentarioEsteticista );
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

    private void updateUI()
    {
        Log.d(TAG, "UI update initiated............."+TAG);
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



    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }



    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLatitude = mCurrentLocation.getLatitude();
        mLongitude = mCurrentLocation.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);
       // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
       // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(100));
        updateUI();
    }
}
