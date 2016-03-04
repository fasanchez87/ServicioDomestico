package com.elements.beya.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.elements.beya.R;
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

import java.util.List;


public class MapFragmentUbicarProveedores extends Fragment implements LocationListener, GoogleMap.OnMarkerClickListener
{

    GoogleMap mGoogleMap;
    Spinner mSprPlaceType;
    MapView mapView;
    private Marker marker;
    private MarkerOptions markerOptions;

    GoogleApiClient mGoogleApiClient;

    private gestionSharedPreferences sharedPreferences;

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    String[] mPlaceType = null;
    String[] mPlaceTypeName = null;

    double mLatitude = 0;
    double mLongitude = 0;

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


            LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(provider, 80000, 0, this);


            cargarProveedoresServicios();


        }

    }

    public static void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
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

            markerOptions.snippet("aa:"+sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getEmailProveedor().toString());





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





            TextView tv=(TextView) popup.findViewById(R.id.title);
            tv.setText(marker.getTitle());
            tv=(TextView)popup.findViewById(R.id.snippet);




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


























}
