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
import com.elements.beya.beans.Servicio;
import com.elements.beya.volley.ControllerSingleton;

import java.util.List;
public class ServiciosSeleccionadosPush extends RecyclerView.Adapter <ServiciosSeleccionadosPush.MyViewHolder>
{

    private List<Servicio> serviciosList;
    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView idServicio,nombreServicio, descripcionServicio, valorServicio;
        public NetworkImageView imagenServicio;


        public CheckBox checkServicio;

        public MyViewHolder(View view)
        {
            super(view);

            idServicio= (TextView) view.findViewById(R.id.textViewIDServicio);
            nombreServicio = (TextView) view.findViewById(R.id.textViewNombreServicio);
            imagenServicio = (NetworkImageView) view.findViewById(R.id.imageItemService);
            descripcionServicio = (TextView) view.findViewById(R.id.textViewDescServicio);
            valorServicio = (TextView) view.findViewById(R.id.textViewValorServicio);
            checkServicio = (CheckBox) view.findViewById(R.id.checkBoxServicio);
            checkServicio = (CheckBox) view.findViewById(R.id.checkBoxServicio);
        }
    }


    public ServiciosSeleccionadosPush(List<Servicio> serviciosList)
    {
        this.serviciosList = serviciosList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.servicio_list_row, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        final Servicio servicio = serviciosList.get(position);
        holder.idServicio.setText(servicio.getId());
        holder.nombreServicio.setText(servicio.getNombreServicio());
        holder.descripcionServicio.setText(servicio.getDescripcionServicio());
        holder.valorServicio.setText(servicio.getValorServicio());
        holder.imagenServicio.setVisibility(View.GONE);
        holder.checkServicio.setVisibility(View.GONE);
        holder.checkServicio.setTag(servicio);

       /* holder.checkServicio.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox) v;
                Servicio s = (Servicio) cb.getTag();

                s.setSelected(cb.isChecked());
                serviciosList.get(position).setSelected(cb.isChecked());

               *//**//* Toast.makeText(
                        v.getContext(),
                        "Clicked on Checkbox: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_LONG).show();*//**//*
            }
        });*/

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