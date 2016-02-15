package com.elements.beya.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.elements.beya.R;
import com.elements.beya.activities.Gestion;
import com.elements.beya.adapters.ServiciosAdapter;
import com.elements.beya.beans.Proveedor;
import com.elements.beya.beans.Servicio;
import com.elements.beya.decorators.DividerItemDecoration;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SolicitarServicio extends Fragment
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


    private ArrayList<Servicio> servicioList = new ArrayList<>();
    private ArrayList<Proveedor> provedoresList;

    private RecyclerView recyclerView;
    private ServiciosAdapter mAdapter;

    private Button buttonSeleccionarServicios;



    private boolean foundService;

    public SolicitarServicio()
    {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPreferences = new gestionSharedPreferences(this.getActivity());
        provedoresList = new ArrayList<Proveedor>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment




        return inflater.inflate(R.layout.fragment_solicitar_servicio, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) this.getActivity().findViewById(R.id.recycler_view);

        progressBar = (ProgressBar) this.getActivity().findViewById(R.id.toolbar_progress_bar);
        buttonSeleccionarServicios = (Button) this.getActivity().findViewById(R.id.buttonSeleccionarServicioFragmentSolicitarServicio);

        checkBoxServicio = (CheckBox) this.getActivity().findViewById(R.id.checkBoxServicio);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ServiciosAdapter(servicioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        buttonSeleccionarServicios.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String data = "";
                List<Servicio> lista_servicios = ((ServiciosAdapter) mAdapter).getServiciosList();

                for (int i = 0; i < lista_servicios.size(); i++)
                {
                    Servicio servicio = lista_servicios.get(i);

                    if (servicio.isSelected() == true)
                    {
                        data = data+servicio.getId().toString()+":";
                    }
                }

                if(data.isEmpty())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
                    builder
                            .setMessage("Debe seleccionar al menos (1) Servicio.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                    //startActivity(intent);
                                    //finish();
                                }
                            }).show();
                    return;
                }

                else
                {
                    //BORRAR ULTIMA COMA
                    String serviciosEscogidos = data.substring(0, data.lastIndexOf(":"));
                    Toast.makeText(SolicitarServicio.this.getActivity(),
                            "Selected Services: \n" + serviciosEscogidos, Toast.LENGTH_LONG)
                            .show();

                    _webServiceGetProviderServicesOnMAP(serviciosEscogidos);

                }


            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getActivity(), recyclerView, new ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Servicio servicio = servicioList.get(position);
                //Toast.makeText(SolicitarServicio.this.getActivity().getApplicationContext(), servicio.getNombreServicio().toString() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position)
            {

            }
        }));

        _webServiceGetServices();
        mAdapter.notifyDataSetChanged();

    }

    public boolean isFoundService() {
        return foundService;
    }

    public void setFoundService(boolean foundService) {
        this.foundService = foundService;
    }

    private void _webServiceGetProviderServicesOnMAP(final String id_services)
    {
        _urlWebService = "http://52.72.85.214/ws/ObtenerProveedoresServicios";

        /*progressBar.setVisibility(View.VISIBLE);
        buttonSeleccionarServicios.setVisibility(View.GONE);*/

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(_urlWebService,
                new Response.Listener<JSONArray>()
                {

                    @Override
                    public void onResponse(JSONArray response)
                    {
                        try
                        {
                            JSONObject object;
                            String status="";


                            //VERIFICAR SI EXISTEN PROVEEDORES DEL SERVICIO REST.
                            if(response.length() == 0)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
                                builder
                                        .setMessage("NO SE ENCONTRARON PROVEEDORES DE LOS SERVICIOS SELECCIONADOS.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                //startActivity(intent);
                                                //finish();
                                            }
                                        }).show();

                                return;
                            }

                            else

                            {
                                for (int i = 0; i <= response.length() - 1; i++)
                                {
                                    object = response.getJSONObject(i);

                                    Proveedor proveedor = new Proveedor();
                                    proveedor.setNombreProveedor(object.getString("nombresUsuario"));
                                    proveedor.setApellidoProveedor(object.getString("apellidosUsuario"));
                                    proveedor.setEmailProveedor(object.getString("emailUsuario"));
                                    //proveedor.setPromedioServicios(object.getString("promedioServicios"));
                                    proveedor.setLatitudUsuario(object.getString("latitudUsuario"));
                                    proveedor.setLongitudUsuario(object.getString("longitudUsuario"));
                                    proveedor.setImgUsuario(object.getString("imgUsuario"));
                                    provedoresList.add(proveedor);
                                }

                                if (!provedoresList.isEmpty())
                                {
                                    Fragment fragment;
                                    fragment = new MapFragmentUbicarProveedores();

                                    //MOSTRAMOS EL FRAGMENT DEL MAPA REEMPLANZANDO EL CONTENIDO DEL FRAGMENT MANAGER AQUI.
                                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_container, fragment);
                                    fragmentTransaction.commit();
                                }

                                //SHAREDPREFERENCES OBJETO PROVEEDOR.
                                sharedPreferences.putListObject("proveedores", provedoresList);

                                for (int i = 0; i <= provedoresList.size() - 1; i++)
                                {
                                    Log.w("Proveedor", "" + provedoresList.get(i).getNombreProveedor());
                                    Log.w("Proveedor", "" + provedoresList.get(i).getImgUsuario().toString());
                                }

                                //progressBar.setVisibility(View.GONE);
                                //buttonSeleccionarServicios.setVisibility(View.VISIBLE);
                                //mAdapter.notifyDataSetChanged();
                            }

                        }
                        catch (JSONException e)
                        {

                            //progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity().getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                        progressBar.setVisibility(View.GONE);
                        //buttonSeleccionarServicios.setVisibility(View.GONE);
                    }


                })
        {

//                  GESTION DE PARAMETROS POR VIA GET.
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
                HashMap <String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("servicios", id_services);
                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }


    private void _webServiceGetServices()
    {
        _urlWebService = "http://52.72.85.214/ws/ObtenerServicios";

        progressBar.setVisibility(View.VISIBLE);
        buttonSeleccionarServicios.setVisibility(View.GONE);


        JsonArrayRequest jsonObjReq = new JsonArrayRequest(_urlWebService,
                new Response.Listener<JSONArray>()
                {

                    @Override
                    public void onResponse(JSONArray response)
                    {
                        try
                        {
                            JSONObject object;

                            for (int i = 0; i <= response.length()-1; i++)
                            {
                                object = response.getJSONObject(i);

                                Servicio servicio = new Servicio();
                                servicio.setId(object.getString("codigoServicio"));
                                servicio.setNombreServicio(object.getString("nombreServicio"));
                                servicio.setDescripcionServicio(object.getString("descripcionServicio"));
                                servicio.setValorServicio(object.getString("valorServicio"));
                                servicioList.add(servicio);

                            }

                            progressBar.setVisibility(View.GONE);
                            buttonSeleccionarServicios.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();

                        }
                        catch (JSONException e)
                        {

                            progressBar.setVisibility(View.GONE);
                            buttonSeleccionarServicios.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                        progressBar.setVisibility(View.GONE);
                        buttonSeleccionarServicios.setVisibility(View.GONE);
                    }


                });


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

    public interface ClickListener
    {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
    {

        private GestureDetector gestureDetector;
        private SolicitarServicio.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SolicitarServicio.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }




        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}