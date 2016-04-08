
package com.elements.beya.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.elements.beya.R;
import com.elements.beya.fragments.MapFragmentUbicarProveedores;
import com.elements.beya.fragments.SolicitarServicio;
import com.elements.beya.services.ServiceActualizarUbicacionProveedor;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;



public class Gestion extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private gestionSharedPreferences sharedPreferences;
    private TextView textViewnameUser;
    private TextView textViewemailUser;
    String name, email, tipoUsuario;
    SwitchCompat switchActivarLocation;
    public static String _urlWebService;
    public String event;


    //GUARDAR OPCION SELECTED SWITCH.
    private boolean isCheckedSwitch;


    private String statusOnline = "0";  // 0 is OffLine; 1 is Online.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = new gestionSharedPreferences(getApplicationContext());
        name = sharedPreferences.getString("nombreUsuario");
        email = sharedPreferences.getString("emailUser");
        tipoUsuario = sharedPreferences.getString("tipoUsuario");

        Log.w("TOKEN :: ","."+sharedPreferences.getString("tokenGCM"));

        Bundle extras = getIntent().getExtras();


        if (extras != null)
        {
            event = extras.getString("Event:MyGcmPushReceiver");

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();

        MenuItem item = menu.findItem(R.id.nav_activar_geolocalizacion);

        //EVENTO DEL SWITCH BUTTON.
        switchActivarLocation = (SwitchCompat) MenuItemCompat.getActionView(item);

        switchActivarLocation.setChecked(sharedPreferences.getBoolean("isCheckedSwitch"));

        switchActivarLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                statusOnline = "";

                if (isChecked)
                {
                    //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                    isCheckedSwitch = true;
                    sharedPreferences.putBoolean("isCheckedSwitch",isCheckedSwitch );


                    Log.e("CHECKED", "onCheckedChanged" + isChecked);
                    statusOnline = "1";
                    sharedPreferences.putString("statusOnline",statusOnline );
                    startService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                }

                else
                {
                    isCheckedSwitch = false;// PENDIENTE
                    sharedPreferences.putBoolean("isCheckedSwitch",isCheckedSwitch );
                    statusOnline = "0";
                    Log.e("CHECKED", "onCheckedChanged" + isChecked);
                    stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                    sharedPreferences.putString("statusOnline", statusOnline );
                }
            }
        });


        if (!tipoUsuario.equals("E"))
        {
            menu.getItem(1).setVisible(false);
        }

        else if (tipoUsuario.equals("E"))
        {
            menu.getItem(0).getSubMenu().getItem(2).setVisible(false);
            Log.e("LEA :::: ", menu.getItem(0).getTitle().toString());
        }




        textViewnameUser = (TextView) navigationView.getHeaderView(0).findViewById(R.id.NombreUserHeaderNavGestion);
        textViewemailUser = (TextView) navigationView.getHeaderView(0).findViewById(R.id.EmailHeaderNavGestion);
        textViewnameUser.setText(""+name);
        textViewemailUser.setText("" + email);
        navigationView.setNavigationItemSelectedListener(this);

        textViewnameUser = (TextView) findViewById(R.id.NombreUserHeaderNavGestion);
        textViewemailUser = (TextView) findViewById(R.id.EmailHeaderNavGestion);

        if (savedInstanceState == null)
        {

            if (tipoUsuario.equals("E"))
            {
                //MOSTRAMOS POR DEFECTO EL NAV MIS SERVICIOS CUANDO ES ESTETICISTA
                navigationView.setCheckedItem(R.id.nav_activar_geolocalizacion);

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new MapFragmentUbicarProveedores());
                fragmentTransaction.commit();

            }

            else
            {


                //MOSTRAMOS POR DEFECTO EL NAV SOLICITAR SERVICIOS CUANDO ES CLIENTE
                navigationView.setCheckedItem(R.id.nav_solicitar_servicio);

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new SolicitarServicio());
                fragmentTransaction.commit();
            }

        }
    }



    public String getLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;
        Log.w("UBICACION FALSA RANDOM", "Longitude: " + foundLongitude + "  Latitude: " + foundLatitude);
        return (foundLongitude + ":" + foundLatitude);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gestion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        Class fragmentClass;

        switch (item.getItemId())
        {
            case R.id.nav_solicitar_servicio:
                fragmentClass = SolicitarServicio.class;
                break;

            case R.id.nav_cerrar_sesion:
                AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
                builder
                        .setMessage("¿Deseas cerrar sesión? Se eliminaran los datos de ingreso.")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {

                                _webServiceCerrarSesionChangeStateOnLine(sharedPreferences.getString("serialUsuario"));
                                sharedPreferences.putBoolean("GuardarSesion",false);
                                sharedPreferences.clear();
                                stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                                Intent intent = new Intent(Gestion.this, Login.class);
                                startActivity(intent);
                                finish();
                                //overridePendingTransition(R.anim.left_out, R.anim.left_in);
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).show();

            default:
                fragmentClass = SolicitarServicio.class;

        }

        try
        {
            fragment = (Fragment) fragmentClass.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();

        item.setChecked(true);
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    private void _webServiceCerrarSesionChangeStateOnLine(final String serialUsuario)
    {

        _urlWebService = "http://52.72.85.214/ws/CerrarSesion";

       // progressBar.setVisibility(View.VISIBLE);
       // textViewProgressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            String status = response.getString("status");

                            if(status.equals("cierre_session_success"))
                            {
                                Log.w("cierre_session_success ::", "cierre_session_success");


                            }

                            else
                            {
                                if(status.equals("cierre_session_failed"))
                                {


                                    AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
                                    builder
                                            .setMessage("cierre_session_failed")
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
                        }

                        catch (JSONException e)
                        {


                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("serialUsuario", serialUsuario);
                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }





}
