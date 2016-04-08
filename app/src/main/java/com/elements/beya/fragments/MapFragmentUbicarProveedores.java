package com.elements.beya.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import com.elements.beya.beans.Servicio;
import com.elements.beya.vars.vars;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.elements.beya.activities.Gestion;
import com.elements.beya.beans.Proveedor;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import java.util.List;
import java.util.Map;


public class MapFragmentUbicarProveedores extends Fragment implements LocationListener, GoogleMap.OnMarkerClickListener
{

    GoogleMap mGoogleMap;
    Spinner mSprPlaceType;
    MapView mapView;
    private Marker marker;
    private MarkerOptions markerOptions;

    private String TAG = MapFragmentUbicarProveedores.class.getSimpleName();


    private String indicaAndroid = "";

    GoogleApiClient mGoogleApiClient;


    public vars vars;

    private gestionSharedPreferences sharedPreferences;

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    String[] mPlaceType = null;
    String[] mPlaceTypeName = null;

    double mLatitude = 0;
    double mLongitude = 0;

    private String _urlWebService;

    private Button buttonSeleccionarServicioFragmentSolicitarServicio;

    Button buttonFindCoach;
    LocationManager lm;

    public MapFragmentUbicarProveedores()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        sharedPreferences = new gestionSharedPreferences(this.getActivity());

        vars = new vars();



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        //findCoachMap();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_fragment_ubicar_proveedores, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);


        mGoogleMap = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map)).getMap();


        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity().getBaseContext());


        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this.getActivity(), requestCode);
            dialog.show();
            return;

        } else

        {


            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);

            if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(),
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


            //Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           /* if (location!=null){
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String locLat = String.valueOf(latitude)+","+String.valueOf(longitude);
            }*/


            LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, false);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                onLocationChanged(location);
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
            }

            locationManager.requestLocationUpdates(provider, 90000, 0, this);

           /* mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();*/

            //SE OBTIENE LAS COORDENADAS DEL CLIENTE QUE SOLICITA EL SERVICIO.
            sharedPreferences.putDouble("latitudCliente", mLatitude);
            sharedPreferences.putDouble("longitudCliente",mLongitude);

            cargarProveedoresServicios();

            //EVENTO BOTON SOLICITAR SERVICIOS A LOS PROVEEDORES DE SERVICIO MEDIANTE PUSH.
            buttonSeleccionarServicioFragmentSolicitarServicio = (Button) this.getActivity().
                    findViewById(R.id.buttonSeleccionarServicioFragmentSolicitarServicio);
            buttonSeleccionarServicioFragmentSolicitarServicio.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    _webServiceEnviarNotificacionPushATodos( sharedPreferences.getString("serialUsuario") );
                }
            });

        }

    }

    public void cargarProveedoresServicios()
    {


        for( int i = 0; i <= sharedPreferences.getListObject("proveedores", Proveedor.class).size()-1; i++ )
        {

            mGoogleMap.setInfoWindowAdapter(new IconizedWindowAdapter(getActivity().getLayoutInflater()));

            markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng( Double.parseDouble(sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getLatitudUsuario()),
                    Double.parseDouble(sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getLongitudUsuario()));

            markerOptions.position(latLng);
            markerOptions.title(sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getNombreProveedor().toString() + " " +
                    sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getApellidoProveedor());

            markerOptions.snippet(sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getEmailProveedor().toString());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.beya_logo_on_map));

            mGoogleMap.addMarker(markerOptions);

        }

       /* mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0)
            {

                *//*String name = arg0.getTitle().toString();
                Intent intent = new Intent(Map.this, Perfil.class);
                intent.putExtra("name",name);
                startActivity(intent);
                finish();
*//*
                return true;
            }

        });*/

    }


    /**
     * Called when the location has changed.
     * <p>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location)
    {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider)
    {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider)
    {

    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        return false;
    }



    public class IconizedWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        LayoutInflater inflater=null;
        //private View view;
        View popup;

        public IconizedWindowAdapter(LayoutInflater inflater) {
            this.inflater=inflater;
            if (imageLoader == null)
                imageLoader = ControllerSingleton.getInstance().getImageLoader();
        }

        public IconizedWindowAdapter() {
            popup = inflater.inflate(R.layout.custom_info_market_map, null);
            if (imageLoader == null)
                imageLoader = ControllerSingleton.getInstance().getImageLoader();

        }

        @Override
        public View getInfoWindow(Marker marker)
        {





         /*   // iv.setDefaultImageResId(R.drawable.ic_launcher);// poner imagen por default
            if (imageLoader == null)
                imageLoader = ControllerSingleton.getInstance().getImageLoader();

            NetworkImageView iv = (NetworkImageView) view.findViewById(R.id.imagenProveedorCustomInfoMarket);
            iv.setImageUrl("http://52.72.85.214/ws/images/minion.jpg", imageLoader);
            // iv.setDefaultImageResId(R.drawable.ic_launcher);// poner imagen por default
            iv.setErrorImageResId(R.mipmap.beya_logo);// en caso de error poner esta imagen.


*/


            return (null);


        }

        @Override
        public View getInfoContents(Marker marker)
        {



            popup = inflater.inflate(R.layout.custom_info_market_map, null);

            if (imageLoader == null)
                imageLoader = ControllerSingleton.getInstance().getImageLoader();

            NetworkImageView iv = (NetworkImageView) popup.findViewById(R.id.imagenProveedorCustomInfoMarket);





            TextView tvTitle = (TextView) popup.findViewById(R.id.title);
            TextView tvSnippet = (TextView) popup.findViewById(R.id.title);
            tvTitle.setText(marker.getTitle());
            tvSnippet = (TextView) popup.findViewById(R.id.snippet);
            tvSnippet.setText(marker.getSnippet());




            for( int i = 0; i <= sharedPreferences.getListObject("proveedores", Proveedor.class).size()-1; i++ )
            {


                iv.setImageUrl("http://52.72.85.214/ws/images/minion.jpg", imageLoader);

            }







/*

            for( int i = 0; i <= sharedPreferences.getListObject("proveedores", Proveedor.class).size()-1; i++ )
            {
                String img = sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getImgUsuario().toString();

                iv.setImageUrl(img, imageLoader);
                //iv.setErrorImageResId(R.drawable.ic_launcher);// en caso de error poner esta imagen.
            }*/



           /* RatingBar rb = (RatingBar) view.findViewById(R.id.infowindow_rating);
            rb.setRating(3.5);*/

            return(popup);
        }
    }

    private void _webServiceEnviarNotificacionPushATodos( final String serialUsuario )
    {
        _urlWebService = "http://52.72.85.214/ws/SendPushNotificationForALL";

        Log.e(TAG, " Se escojieron: "+sharedPreferences.getString("serviciosEscogidos"));


        indicaAndroid = vars.indicaAndroid;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if(status)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
                                builder
                                        .setMessage("Solicitud enviada con exito a todos los Esteticistas; en instantes se le asignara su servicio.")
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
                             /*   Intent intent = new Intent(Login.this, Gestion.class);
                                startActivity(intent);
                                sharedPreferences.putBoolean("GuardarSesion", true);
                                sharedPreferences.putString("email", emailUser);
                                sharedPreferences.putString("clave",claveUser);
                                sharedPreferences.putString("tipoUsuario", tipoUsuario);
                                sharedPreferences.putString("serialUsuario", serialUsuario);
                                finish();*//*

                                //}
                                //}).show();*/


                            }

                            else
                            {
                                if(!status)
                                {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
                                    builder
                                            .setMessage("Error al enviar solicitud: Error: "+message)
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                    //startActivity(intent);
                                                    //finish();
                                                }
                                            }).show();
                                }
                            }
                        }

                        catch (JSONException e)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
                            builder
                                    .setMessage(e.getMessage().toString())
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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
                headers.put("serialUsuario", serialUsuario);
                headers.put("latitudCliente", "" + sharedPreferences.getDouble("latitudCliente", 0));
                headers.put("longitudCliente", "" + sharedPreferences.getDouble("longitudCliente", 0));
                headers.put("servicios", sharedPreferences.getString("serviciosEscogidos"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;

            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");

    }



















}
