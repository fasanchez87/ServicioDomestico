package com.elements.beya.adapters;

/**
 * Created by FABiO on 05/02/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.elements.beya.CircularImageView.CircularNetworkImageView;
import com.elements.beya.R;
import com.elements.beya.beans.Servicio;
import com.elements.beya.beans.SolicitudServicio;
import com.elements.beya.beans.ValorServicio;
import com.elements.beya.fragments.SolicitarServicio;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.internal.zzir.runOnUiThread;

public class ServiciosAdapter extends RecyclerView.Adapter <ServiciosAdapter.MyViewHolder>
{

    private List<Servicio> serviciosList;
    private Context mContext;

    private gestionSharedPreferences sharedPreferences;


    public static int valorTotal = 0;

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();




    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView idServicio,nombreServicio, descripcionServicio, valorServicio;
        public CheckBox checkServicio;
        public CircularNetworkImageView imagenServicio;

        public MyViewHolder(View view)
        {
            super(view);

            idServicio= (TextView) view.findViewById(R.id.textViewIDServicio);
            nombreServicio = (TextView) view.findViewById(R.id.textViewNombreServicio);
            descripcionServicio = (TextView) view.findViewById(R.id.textViewDescServicio);
            valorServicio = (TextView) view.findViewById(R.id.textViewValorServicio);
            checkServicio = (CheckBox) view.findViewById(R.id.checkBoxServicio);
            imagenServicio = (CircularNetworkImageView) view.findViewById(R.id.imageItemService);

        }
    }


    public ServiciosAdapter(List<Servicio> serviciosList)
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
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        final Servicio servicio = serviciosList.get(position);
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

        holder.checkServicio.setChecked(servicio.isSelected());
        holder.checkServicio.setTag(servicio);


        holder.checkServicio.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox) v;
                Servicio s = (Servicio) cb.getTag();

                s.setSelected(cb.isChecked());
                serviciosList.get(position).setSelected(cb.isChecked());

                final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);


              /*  Toast.makeText(v.getContext(), "Clicked on Checkbox: " + cb.getText() + " is "+ cb.isChecked(), Toast.LENGTH_LONG).show();
                Toast.makeText(v.getContext(), "Clicked on : " + s.getValorServicio(), Toast.LENGTH_LONG).show();*/

                if(cb.isChecked())
                {
                    //sumo si selecciona servicios
                    valorTotal = valorTotal+(Integer.parseInt(s.getValorServicio()));
                    //Toast.makeText(v.getContext(), ""+valorTotal, Toast.LENGTH_LONG).show();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            //SolicitarServicio.valorTotalTextView.setText("" +(valorTotal));
                            SolicitarServicio.valorTotalTextView.setText("" + nf.format(valorTotal));



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

                            //SolicitarServicio.valorTotalTextView.setText("" + (valorTotal));
                            SolicitarServicio.valorTotalTextView.setText("" + nf.format(valorTotal));

                        }
                    });

                }
                ValorServicio.setValorServicio(valorTotal);
                Toast.makeText(v.getContext(), ""+ValorServicio.getValorServicio(), Toast.LENGTH_LONG).show();


               /* sharedPreferences = new gestionSharedPreferences(v.getContext());
                sharedPreferences.putInt("valorTotalServicios",valorTotal);*/


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