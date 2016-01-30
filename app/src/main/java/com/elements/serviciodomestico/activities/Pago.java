package com.elements.serviciodomestico.activities;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.elements.serviciodomestico.R;
import com.elements.serviciodomestico.sharedPreferences.gestionSharedPreferences;
import com.elements.serviciodomestico.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Pago extends AppCompatActivity
{

    TextInputLayout textInputLayoutNumeroTarjetaCreditoUser;
    TextInputLayout textInputLayoutMesTarjetaCreditoUser;
    TextInputLayout textInputLayoutAñoTarjetaCreditoUser;
    TextInputLayout textInputLayoutCVVTarjetaCreditoUser;

    EditText EditTextNumeroTarjetaCreditoUser;
    EditText EditTextMesTarjetaCreditoUser;
    EditText EditTextAñoTarjetaCreditoUser;
    EditText EditTextCVVTarjetaCreditoUser;

    TextView TextViewLabelProgressBar;

    Button botonRegistroUsuario;

    ProgressBar progressBarRegistroUsuario;

    private String nameFranquicia;

    private gestionSharedPreferences sharedPreferences;

    private String numeroTarjetaCredito;
    private String MesTarjetaCredito;
    private String AñoTarjetaCredito;
    private String CVVTarjetaCredito;
    private String fechaVigencia;

    private String _urlWebService;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences = new gestionSharedPreferences(this.getApplicationContext());

        textInputLayoutNumeroTarjetaCreditoUser = (TextInputLayout) findViewById(R.id.input_layout_numero_tarjeta_registro);
        textInputLayoutMesTarjetaCreditoUser = (TextInputLayout) findViewById(R.id.input_layout_mes_tarjeta_credito_registro);
        textInputLayoutAñoTarjetaCreditoUser = (TextInputLayout) findViewById(R.id.input_layout_año_tarjeta_credito_registro);
        textInputLayoutCVVTarjetaCreditoUser = (TextInputLayout) findViewById(R.id.input_layout_cvv_tarjeta_credito_registro);

        EditTextNumeroTarjetaCreditoUser = (EditText) findViewById(R.id.edit_text_numero_tarjeta_registro);
        EditTextMesTarjetaCreditoUser = (EditText) findViewById(R.id.edit_text_mes_tarjeta_credito_registro);
        EditTextAñoTarjetaCreditoUser = (EditText) findViewById(R.id.edit_text_año_tarjeta_credito_registro);
        EditTextCVVTarjetaCreditoUser = (EditText) findViewById(R.id.edit_text_cvv_tarjeta_credito_registro);

        botonRegistroUsuario = (Button) findViewById(R.id.btn_registro);
        botonRegistroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                registrarDatosRESt();
            }
        });

        progressBarRegistroUsuario = (ProgressBar) findViewById(R.id.progressBarRegistro);

        EditTextNumeroTarjetaCreditoUser.addTextChangedListener(new RevisorText(EditTextNumeroTarjetaCreditoUser));
        EditTextMesTarjetaCreditoUser.addTextChangedListener(new RevisorText(EditTextMesTarjetaCreditoUser));
        EditTextAñoTarjetaCreditoUser.addTextChangedListener(new RevisorText(EditTextAñoTarjetaCreditoUser));
        EditTextCVVTarjetaCreditoUser.addTextChangedListener(new RevisorText(EditTextCVVTarjetaCreditoUser));

        textInputLayoutNumeroTarjetaCreditoUser.setHintAnimationEnabled(false);
        EditTextNumeroTarjetaCreditoUser.setHint("Número tarjeta de crédito");

        TextViewLabelProgressBar = (TextView) findViewById(R.id.textViewProgressBarTarjetaCredito);

        EditTextCVVTarjetaCreditoUser.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    EditTextCVVTarjetaCreditoUser.setText("");
                }
            }

        });

        nameFranquicia = "";

    }


    private boolean validarDatosPago()
    {
        if (!validateNumberCreditCard())
        {
            return false;
        }

        if (!validateCardAlgorithmLuhn())
        {
            return false;
        }

        if (!validateMesCreditCard()) {
            return false;
        }

        if (!validateAñoCreditCard())
        {
            return false;
        }

        if (!validateCVVCreditCard())
        {
            return false;
        }

        if (!validateCVVFranquiciaCreditCard())
        {
            return false;
        }

        return true;
    }

    public void registrarDatosRESt()
    {
        if( validarDatosPago() )
        {
            numeroTarjetaCredito = EditTextNumeroTarjetaCreditoUser.getText().toString();
            MesTarjetaCredito = EditTextMesTarjetaCreditoUser.getText().toString();
            AñoTarjetaCredito = EditTextAñoTarjetaCreditoUser.getText().toString();
            CVVTarjetaCredito = EditTextCVVTarjetaCreditoUser.getText().toString();

            fechaVigencia = EditTextAñoTarjetaCreditoUser.getText().toString()+"/"+EditTextMesTarjetaCreditoUser.getText().toString();

            Log.w("numeroTarjetaCredito",numeroTarjetaCredito);
            Log.w("MesTarjetaCredito",MesTarjetaCredito);
            Log.w("AñoTarjetaCredito",AñoTarjetaCredito);
            Log.w("CVVTarjetaCredito",CVVTarjetaCredito);
            Log.w("fechaVigencia",fechaVigencia);
            Log.w("nombresUsuario",sharedPreferences.getString("nombresUsuario"));
            Log.w("apellidosUsuario","");
            Log.w("emailUsuario","");
            Log.w("documentoUsuario","");
            Log.w("claveUsuario","");
            Log.w("direccionIp","");
            Log.w("sistemaOperativo","");
            Log.w("tipoUsuario","");


            _webServiceRegistroUsuario(sharedPreferences.getString("nombresUsuario"), sharedPreferences.getString("apellidosUsuario"),
                    sharedPreferences.getString("emailUsuario"), sharedPreferences.getString("documentoUsuario"),
                    sharedPreferences.getString("claveUsuario"), sharedPreferences.getString("direccionIp"),
                    sharedPreferences.getString("sistemaOperativo"), sharedPreferences.getString("tipoUsuario"), numeroTarjetaCredito,
                    fechaVigencia, getNameFranquicia());

        }
    }


    private boolean validateNumberCreditCard()
    {

        String numberCard = EditTextNumeroTarjetaCreditoUser.getText().toString();
        textInputLayoutNumeroTarjetaCreditoUser.setHintAnimationEnabled(true);

        if (EditTextNumeroTarjetaCreditoUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutNumeroTarjetaCreditoUser.setError("Número de tarjeta invalido");
            requestFocus(EditTextNumeroTarjetaCreditoUser);
            return false;
        }


        else
        {
            textInputLayoutNumeroTarjetaCreditoUser.setErrorEnabled(false);
            //EditTextNumeroTarjetaCreditoUser.setError("");
        }

        return true;
    }

    private boolean validateAñoCreditCard()
    {

        String numberCard = EditTextAñoTarjetaCreditoUser.getText().toString();

        if (EditTextAñoTarjetaCreditoUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutAñoTarjetaCreditoUser.setError("Año no valido");
            requestFocus(EditTextAñoTarjetaCreditoUser);
            return false;
        }

        else

        if (EditTextAñoTarjetaCreditoUser.getText().toString().trim().length() < 4)
        {
            textInputLayoutAñoTarjetaCreditoUser.setError("Año no valido");
            requestFocus(EditTextAñoTarjetaCreditoUser);
            return false;
        }

        else
        {
            textInputLayoutAñoTarjetaCreditoUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMesCreditCard()
    {

        String mesCard = EditTextMesTarjetaCreditoUser.getText().toString();


        if ((EditTextMesTarjetaCreditoUser.getText().toString().trim().isEmpty()) ||
                (Integer.parseInt(mesCard.toString()) >= 13 )  )
        {
            textInputLayoutMesTarjetaCreditoUser.setError("Mes no valido");
            requestFocus(EditTextMesTarjetaCreditoUser);
            return false;
        }

        else

        if (EditTextMesTarjetaCreditoUser.getText().toString().trim().length() < 2)
        {
            textInputLayoutMesTarjetaCreditoUser.setError("Mes no valido");
            requestFocus(EditTextMesTarjetaCreditoUser);
            return false;
        }

        else
        {
            textInputLayoutMesTarjetaCreditoUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCVVCreditCard()
    {

        String cvvCard = EditTextCVVTarjetaCreditoUser.getText().toString();

        if (EditTextCVVTarjetaCreditoUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
            requestFocus(EditTextCVVTarjetaCreditoUser);
            return false;
        }

        else
        {
            textInputLayoutCVVTarjetaCreditoUser.setErrorEnabled(false);
        }

        return true;
    }

    public String getNameFranquicia()
    {
        return nameFranquicia;
    }

    public void setNameFranquicia(String nameFranquicia)
    {
        this.nameFranquicia = nameFranquicia;
    }

    private boolean validateCVVFranquiciaCreditCard()
    {

        String cvvCard = EditTextCVVTarjetaCreditoUser.getText().toString();

        nameFranquicia = getNameFranquicia();

        if(nameFranquicia.equals("amex"))
        {
            nameFranquicia="";

            if (EditTextCVVTarjetaCreditoUser.getText().toString().trim().length() < 4)
            {
                textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
                requestFocus(EditTextCVVTarjetaCreditoUser);
                return false;
            }

            else
            {
                textInputLayoutNumeroTarjetaCreditoUser.setErrorEnabled(false);
            }
        }

        else

        if(nameFranquicia.equals("visa"))
        {

            if (EditTextCVVTarjetaCreditoUser.getText().toString().trim().length() < 3)
            {
                textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
                requestFocus(EditTextCVVTarjetaCreditoUser);
                return false;
            }
        }

        else

        if(nameFranquicia.equals("master"))
        {
            nameFranquicia="";

            if (EditTextCVVTarjetaCreditoUser.getText().toString().trim().length() < 3)
            {
                textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
                requestFocus(EditTextCVVTarjetaCreditoUser);
                return false;
            }

            else
            {
                textInputLayoutNumeroTarjetaCreditoUser.setErrorEnabled(false);
            }
        }

        else

        if(nameFranquicia.equals("generica"))
        {
            if (EditTextCVVTarjetaCreditoUser.getText().toString().trim().length() < 3)
            {
                textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
                requestFocus(EditTextCVVTarjetaCreditoUser);
                return false;
            }
        }

        else
        {
            textInputLayoutCVVTarjetaCreditoUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCardAlgorithmLuhn()
    {

        String numberCard = EditTextNumeroTarjetaCreditoUser.getText().toString().replaceAll("\\s+", "");
        Log.w("Error Tarjeta: ", numberCard);

        if ( !validateCardNumber(numberCard) )
        {
            textInputLayoutNumeroTarjetaCreditoUser.setHintAnimationEnabled(true);
            textInputLayoutNumeroTarjetaCreditoUser.setError("Número de tarjeta invalido");
            requestFocus(EditTextNumeroTarjetaCreditoUser);
            return false;
        }


        else
        {
            textInputLayoutNumeroTarjetaCreditoUser.setErrorEnabled(false);
        }

        return true;
    }

    public boolean validateCardNumber(String cardNumber)
    {
        int sum = 0, digit, addend = 0;
        boolean doubled = false;

        for (int i = cardNumber.length () - 1; i >= 0; i--)
        {
            digit = Integer.parseInt (cardNumber.substring (i, i + 1));
            if (doubled) {
                addend = digit * 2;
                if (addend > 9)
                {
                    addend -= 9;
                }
            }

            else
            {
                addend = digit;
            }
            sum += addend;
            doubled = !doubled;
        }
        return (sum % 10) == 0;
    }

    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class RevisorText implements TextWatcher
    {

        private View view;

        private RevisorText(View view)
        {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
        }

        public void afterTextChanged(Editable editable)
        {

            char space = ' ';

            switch (view.getId())
            {
                case R.id.edit_text_numero_tarjeta_registro:
                    validateNumberCreditCard();
                    break;

                case R.id.edit_text_mes_tarjeta_credito_registro:
                    validateMesCreditCard();
                    break;

                case R.id.edit_text_año_tarjeta_credito_registro:
                    validateAñoCreditCard();
                    break;

                case R.id.edit_text_cvv_tarjeta_credito_registro:
                    validateCVVCreditCard();
                    break;
            }

            // Remove spacing char
            if (editable.length() > 0 && (editable.length() % 5) == 0)
            {
                final char c = editable.charAt(editable.length() - 1);
                if (space == c)
                {
                    editable.delete(editable.length() - 1, editable.length());
                }
            }
            // Insert char where needed.
            if (editable.length() > 0 && (editable.length() % 5) == 0)
            {
                char c = editable.charAt(editable.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(editable.toString(), String.valueOf(space)).length <= 3)
                {
                    editable.insert(editable.length() - 1, String.valueOf(space));
                }
            }

            //Validar tipos de tarjeta de credito.

            //American Express:

            if(EditTextNumeroTarjetaCreditoUser.getText().toString().length() > 1 &&
                    (EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 2).equals("34") ||
                            EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 2).equals("37"))
                    )
            {

                EditTextNumeroTarjetaCreditoUser.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.icon_amex, 0, 0, 0);
                EditTextNumeroTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                EditTextCVVTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                setNameFranquicia("");
                setNameFranquicia("amex");
            }

            else

                //Visa

                if(EditTextNumeroTarjetaCreditoUser.getText().toString().length() > 0 &&
                        (EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 1).equals("4")))
                {

                    EditTextNumeroTarjetaCreditoUser.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.logo_visa, 0, 0, 0);
                    EditTextNumeroTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                    EditTextCVVTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                    setNameFranquicia("");
                    setNameFranquicia("visa");

                }


                //MasterCard

                else

                if(EditTextNumeroTarjetaCreditoUser.getText().toString().length() > 1 &&
                        (EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 2).equals("51") ||
                                EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 2).equals("52") ||
                                EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 2).equals("53") ||
                                EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 2).equals("54") ||
                                EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 2).equals("55")
                        ))
                {

                    EditTextNumeroTarjetaCreditoUser.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.logo_master, 0, 0, 0);
                    EditTextNumeroTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                    EditTextCVVTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                    setNameFranquicia("");
                    setNameFranquicia("master");

                }

                else
                {
                    EditTextNumeroTarjetaCreditoUser.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.logo_tarjeta_x, 0, 0, 0);
                    EditTextNumeroTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                    EditTextCVVTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                    setNameFranquicia("");
                    setNameFranquicia("generica");

                }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(Pago.this, Registro.class);
                startActivity(intent);
                finish();
                //overridePendingTransition(R.anim.left_out, R.anim.left_in);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(Pago.this, Registro.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(R.anim.left_out, R.anim.left_in);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void _webServiceRegistroUsuario(String nombre, String apellido, String email, String documento,
                             String clave, String direccionIP, String sistemaOperativo, String tipoUsuario,
                             final String numeroTarjetaCredito, final String fechaVigencia, String tipoTarjeta)
    {

        _urlWebService = "http://52.72.85.214/ws/RegistroUsuario";

        progressBarRegistroUsuario.setVisibility(View.VISIBLE);
        TextViewLabelProgressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            String status = response.getString("result");

                            if(status.equals("true"))
                            {

                                progressBarRegistroUsuario.setVisibility(View.GONE);
                                TextViewLabelProgressBar.setVisibility(View.GONE);

                                new AlertDialog.Builder(Pago.this.getApplication())
                                        .setTitle("Notificación Registro")
                                        .setMessage("El usuario ha sido creado exitosamente.")
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                Intent intent = new Intent(Pago.this.getApplicationContext(), Login.class);
                                                startActivity(intent);
                                                finish();
                                                sharedPreferences.clear();
                                            }                                                  //overridePendingTransition(R.anim.left_out, R.anim.left_in);

                                        });
                            }

                            else
                            {
                                if(status.equals("error"))
                                {
                                    String message =response.getString("message");
                                    Toast toast2 =
                                            Toast.makeText(getApplicationContext(),
                                                    message, Toast.LENGTH_SHORT);

                                    toast2.show();

                                    progressBarRegistroUsuario.setVisibility(View.GONE);
                                    TextViewLabelProgressBar.setVisibility(View.GONE);

                                }

                            }
                        }

                        catch (JSONException e)
                        {
                            String message =e.getMessage().toString();
                            Toast toast2 =
                                    Toast.makeText(getApplicationContext(),
                                            message, Toast.LENGTH_SHORT);

                            toast2.show();

                            progressBarRegistroUsuario.setVisibility(View.GONE);
                            TextViewLabelProgressBar.setVisibility(View.GONE);

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

                            Toast toast =
                                    Toast.makeText(getApplicationContext(),
                                            "Error de conexión, sin respuesta del servidor.", Toast.LENGTH_SHORT);
                            toast.show();

                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            Toast toast =
                                    Toast.makeText(getApplicationContext(),
                                            "Por favor, conectese a la red.", Toast.LENGTH_SHORT);
                            toast.show();

                        }

                        else

                        if (error instanceof AuthFailureError)
                        {
                            Toast toast =
                                    Toast.makeText(getApplicationContext(),
                                            "Error de autentificación en la red, favor contacte a su proveedor de servicios.", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        else

                        if (error instanceof ServerError)
                        {
                            Toast toast =
                                    Toast.makeText(getApplicationContext(),
                                            "Error server, sin respuesta del servidor.", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        else

                        if (error instanceof NetworkError)
                        {
                            Toast toast =
                                    Toast.makeText(getApplicationContext(),
                                            "Error de red, contacte a su proveedor de servicios.", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        else

                        if (error instanceof ParseError)
                        {
                            Toast toast =
                                    Toast.makeText(getApplicationContext(),
                                            "Error de conversión Parser, contacte a su proveedor de servicios.", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        progressBarRegistroUsuario.setVisibility(View.GONE);
                        TextViewLabelProgressBar.setVisibility(View.GONE);


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

            //Enviar parametros via Headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {

                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("nombresUsuario", sharedPreferences.getString("nombresUsuario") );
                headers.put("apellidosUsuario", sharedPreferences.getString("apellidosUsuario"));
                headers.put("emailUsuario", sharedPreferences.getString("emailUsuario"));
                headers.put("documentoUsuario", sharedPreferences.getString("documentoUsuario"));
                headers.put("claveUsuario", sharedPreferences.getString("claveUsuario"));
                headers.put("direccionIp", sharedPreferences.getString("direccionIp"));
                headers.put("sistemaOperativo", sharedPreferences.getString("sistemaOperativo"));
                headers.put("tipoUsuario", sharedPreferences.getString("tipoUsuario"));
                headers.put("numeroTarjeta", numeroTarjetaCredito);
                headers.put("fechaVigencia", fechaVigencia);
                headers.put("tipoTarjeta", getNameFranquicia());
                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }
}
