package com.elements.beya.adapters;

/**
 * Created by FABiO on 05/02/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.elements.beya.R;
import com.elements.beya.activities.AceptacionServicio;
import com.elements.beya.beans.Servicio;
import com.elements.beya.fragments.SolicitarServicio;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import java.util.List;

import static com.google.android.gms.internal.zzir.runOnUiThread;

public class ServiciosAceptacionAdapter extends RecyclerView.Adapter <ServiciosAceptacionAdapter.MyViewHolder>
{

    private List<Servicio> serviciosList;
    public static int valorTotal = 0;
    public static int valorAcarreado = 0;


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView nombreServicio, valorServicio;
        public CheckBox checkServicio;

        public MyViewHolder(View view)
        {
            super(view);

            nombreServicio = (TextView) view.findViewById(R.id.textViewNombreServicioAceptacionServicios);
            valorServicio = (TextView) view.findViewById(R.id.textViewValorServicioAceptacionServicios);
            checkServicio = (CheckBox) view.findViewById(R.id.checkBoxServicioAceptacionServicios);
        }
    }


    public ServiciosAceptacionAdapter(List<Servicio> serviciosList)
    {
        this.serviciosList = serviciosList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.aceptacion_servicios_row, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        final Servicio servicio = serviciosList.get(position);

        if(servicio.getImagen().isEmpty())
        {

        }

        holder.nombreServicio.setText(servicio.getNombreServicio());
        holder.valorServicio.setText(servicio.getValorServicio());

        holder.checkServicio.setChecked(servicio.isSelected());
        holder.checkServicio.setTag(servicio);

        holder.checkServicio.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox) v;
                Servicio s = (Servicio) cb.getTag();

                valorAcarreado = 0;

                if(cb.isChecked())
                {
                    //sumo si selecciona servicios
                    valorTotal = valorTotal+(Integer.parseInt(s.getValorServicio()));
                    Toast.makeText(v.getContext(), ""+valorTotal, Toast.LENGTH_LONG).show();

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            valorAcarreado = Integer.parseInt(AceptacionServicio.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.getText().toString());
                            valorAcarreado += valorTotal;
                            AceptacionServicio.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.setText("" + valorAcarreado);

                        }
                    });


                }

                else
                {
                    //resto si lo quita
                    valorTotal = valorTotal-(Integer.parseInt(s.getValorServicio()));
                    // holder.valorTotalTextView.setText(""+valorTotal);
                    //Toast.makeText(v.getContext(), ""+valorTotal, Toast.LENGTH_LONG).show();


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            valorAcarreado = Integer.parseInt(AceptacionServicio.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.getText().toString());
                            valorAcarreado -= valorTotal;
                            AceptacionServicio.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.setText("" + valorAcarreado);

                        }
                    });

                }

                /*sharedPreferences = new gestionSharedPreferences(v.getContext());
                sharedPreferences.putInt("valorTotalServicios", valorTotal);*/
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return serviciosList.size();
    }

    public List<Servicio> getServiciosList() {
        return serviciosList;
    }
}