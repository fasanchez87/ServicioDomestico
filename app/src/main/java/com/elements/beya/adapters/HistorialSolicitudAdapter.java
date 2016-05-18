package com.elements.beya.adapters;

/**
 * Created by FABiO on 05/02/2016.
 */

import android.content.SharedPreferences;
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
import com.elements.beya.activities.ListaServiciosCliente;
import com.elements.beya.beans.HistorialSolicitud;
import com.elements.beya.beans.Servicio;
import com.elements.beya.fragments.SolicitarServicio;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.internal.zzir.runOnUiThread;

public class HistorialSolicitudAdapter extends RecyclerView.Adapter <HistorialSolicitudAdapter.MyViewHolder>
{

    SharedPreferences sharedPreferences;
    private List<HistorialSolicitud> historialList;


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView idSolicitud, nombreSolicitud, fechaSolicitud, estadoSolicitud, valorSolicitud;

        public MyViewHolder(View view)
        {
            super(view);
            idSolicitud = (TextView) view.findViewById(R.id.textViewCodigoSolicitudHistorialServicio);
            nombreSolicitud = (TextView) view.findViewById(R.id.textViewNombreSolicitudHistorialServicio);
            fechaSolicitud = (TextView) view.findViewById(R.id.fechaSolicitudHistorialServicio);
            estadoSolicitud = (TextView) view.findViewById(R.id.textViewEstadoSolicitudHistorialServicio);
            valorSolicitud = (TextView) view.findViewById(R.id.textViewValorServicioHistorialServicio);
        }
    }

    public HistorialSolicitudAdapter(List<HistorialSolicitud> serviciosList)
    {
        this.historialList = serviciosList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.historial_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        final HistorialSolicitud historialItem = historialList.get(position);

        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);


        holder.idSolicitud.setText(historialItem.getIdSolicitud());
        holder.nombreSolicitud.setText(historialItem.getNombreSolicitud());
        holder.fechaSolicitud.setText(historialItem.getFechaSolicitud());
        holder.estadoSolicitud.setText(historialItem.getEstadoSolicitud());
        holder.valorSolicitud.setText(nf.format(Integer.parseInt(historialItem.getValorSolicitud())));

    }

    @Override
    public int getItemCount()
    {
        return historialList.size();
    }

    public List<HistorialSolicitud> getHistorialList()
    {
        return historialList;
    }
}