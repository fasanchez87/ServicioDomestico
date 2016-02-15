package com.elements.beya.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.elements.beya.R;
import com.elements.beya.fragments.SolicitarServicio;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;

public class Gestion extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private gestionSharedPreferences sharedPreferences;
    private TextView textViewnameUser;
    private TextView textViewemailUser;
    String name, email;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        textViewnameUser = (TextView) navigationView.getHeaderView(0).findViewById(R.id.NombreUserHeaderNavGestion);
        textViewemailUser = (TextView) navigationView.getHeaderView(0).findViewById(R.id.EmailHeaderNavGestion);
        textViewnameUser.setText(""+name);
        textViewemailUser.setText("" + email);
        navigationView.setNavigationItemSelectedListener(this);

        textViewnameUser = (TextView) findViewById(R.id.NombreUserHeaderNavGestion);
        textViewemailUser = (TextView) findViewById(R.id.EmailHeaderNavGestion);

        if (savedInstanceState == null)
        {
            //MOSTRAMOS POR DEFECTO EL NAV SOLICITAR SERVICIOS
            navigationView.setCheckedItem(R.id.nav_solicitar_servicio);

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, new SolicitarServicio());
            fragmentTransaction.commit();

        }

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
                                sharedPreferences.putBoolean("GuardarSesion",false);
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
}
