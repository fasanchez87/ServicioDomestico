package com.elements.beya.adapters;

/**
 * Created by FABiO on 05/02/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.elements.beya.CircularImageView.CircularNetworkImageView;
import com.elements.beya.R;
import com.elements.beya.beans.Servicio;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ServiciosEsteticistaAdapter extends RecyclerView.Adapter <ServiciosEsteticistaAdapter.MyViewHolder>
{

    private List<Servicio> serviciosDetalle;

    private gestionSharedPreferences sharedPreferences;


    public static int valorTotal = 0;

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView idServicio,nombreServicio, descripcionServicio, valorServicio;
        public CircularNetworkImageView imagenServicio;


        public MyViewHolder(View view)
        {
            super(view);

            idServicio= (TextView) view.findViewById(R.id.textViewIDServicio);
            nombreServicio = (TextView) view.findViewById(R.id.textViewNombreServicio);
            descripcionServicio = (TextView) view.findViewById(R.id.textViewDescServicio);
            valorServicio = (TextView) view.findViewById(R.id.textViewValorServicio);
            imagenServicio = (CircularNetworkImageView) view.findViewById(R.id.imageItemService);
        }
    }


    public ServiciosEsteticistaAdapter(List<Servicio> serviciosList)
    {
        this.serviciosDetalle = serviciosList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.servicios_ofrecidos_esteticista_row, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        final Servicio servicio = serviciosDetalle.get(position);

        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);


        if(servicio.getImagen().isEmpty())
        {
            holder.imagenServicio.setVisibility(View.GONE);
        }

        holder.imagenServicio.setImageUrl(servicio.getImagen(), imageLoader);
        holder.imagenServicio.setDefaultImageResId(R.drawable.ic_blower);// poner imagen por default
        holder.imagenServicio.setErrorImageResId(R.drawable.ic_blower);// en caso de error poner esta imagen.
        holder.idServicio.setText(servicio.getId());
        holder.nombreServicio.setText(servicio.getNombreServicio());
        holder.descripcionServicio.setText(servicio.getDescripcionServicio());
        holder.valorServicio.setText(nf.format(Integer.parseInt(servicio.getValorServicio())));

    }

    @Override
    public int getItemCount()
    {
        return serviciosDetalle.size();
    }

    public List<Servicio> getServiciosList() {
        return serviciosDetalle;
    }
}