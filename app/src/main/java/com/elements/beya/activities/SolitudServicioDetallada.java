package com.elements.beya.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.elements.beya.R;
import com.elements.beya.adapters.ServiciosAdapter;
import com.elements.beya.beans.Proveedor;
import com.elements.beya.beans.Servicio;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;

import java.util.ArrayList;

public class SolitudServicioDetallada extends AppCompatActivity
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String _urlWebService;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ProgressBar progressBar;

    CheckBox checkBoxServicio;

    private gestionSharedPreferences sharedPreferences;

    private String serialUsuario;

    private ArrayList<Servicio> servicioList = new ArrayList<>();
    private ArrayList<Proveedor> provedoresList;

    private RecyclerView recyclerView;
    private ServiciosAdapter mAdapter;

    private Button buttonSeleccionarServicios;



    private boolean foundService;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solitud_servicio_detallada);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

}
