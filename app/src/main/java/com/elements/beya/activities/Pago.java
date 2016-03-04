package com.elements.beya.activities;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
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
import com.elements.beya.R;
import com.elements.beya.app.Config;
import com.elements.beya.gcm.GcmIntentService;
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

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


//  Llamado de Editext desde la actividad Registro, con el fin de recordar los datos al devolverse.
    EditText EditTextNameUser;
    EditText EditTextApellidoUser;
    EditText EditTextEmailUser;
    EditText EditTextClaveUser;
    EditText EditTextDocumentoUser;

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
    public String tokenGCM;

    private Intent _intent;

    Registro registro;

    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    tokenGCM = intent.getStringExtra("token");
                    sharedPreferences.putString("TOKEN :: ",tokenGCM);

                    Toast.makeText(getApplicationContext(), "GCM registration token: " + tokenGCM, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };



        if (checkPlayServices()) {
            registerGCM();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



       // registro = new Registro();


        EditTextNameUser = (EditText) findViewById(R.id.edit_text_nombre_registro);
        EditTextApellidoUser = (EditText) findViewById(R.id.edit_text_apellido_registro);
        EditTextEmailUser = (EditText) findViewById(R.id.edit_text_email_registro);
        EditTextClaveUser = (EditText) findViewById(R.id.edit_text_clave_registro);
        EditTextDocumentoUser = (EditText) findViewById(R.id.edit_text_documento_registro);

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

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
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

            numeroTarjetaCredito = EditTextNumeroTarjetaCreditoUser.getText().toString().replaceAll("\\s+", "");
            MesTarjetaCredito = EditTextMesTarjetaCreditoUser.getText().toString();
            AñoTarjetaCredito = EditTextAñoTarjetaCreditoUser.getText().toString();
            CVVTarjetaCredito = EditTextCVVTarjetaCreditoUser.getText().toString();

            fechaVigencia = EditTextAñoTarjetaCreditoUser.getText().toString()+"/"+EditTextMesTarjetaCreditoUser.getText().toString();

            Log.w("tokenGCM",""+tokenGCM);
            Log.w("numeroTarjetaCredito",numeroTarjetaCredito);
            Log.w("MesTarjetaCredito",MesTarjetaCredito);
            Log.w("AñoTarjetaCredito",AñoTarjetaCredito);
            Log.w("CVVTarjetaCredito",CVVTarjetaCredito);
            Log.w("fechaVigencia",fechaVigencia);
            Log.w("nombresUsuario", sharedPreferences.getString("nombresUsuario"));
            Log.w("NameFranquicia",getNameFranquicia());
            Log.w("apellidosUsuario",sharedPreferences.getString("nombresUsuario"));
            Log.w("emailUsuario",sharedPreferences.getString("emailUsuario"));
            Log.w("documentoUsuario", sharedPreferences.getString("documentoUsuario"));
            Log.w("claveUsuario",sharedPreferences.getString("claveUsuario"));
            Log.w("direccionIp",sharedPreferences.getString("direccionIp"));
            Log.w("sistemaOperativo",sharedPreferences.getString("sistemaOperativo"));
            Log.w("tipoUsuario", sharedPreferences.getString("tipoUsuario"));


            _webServiceRegistroUsuario( tokenGCM, sharedPreferences.getString("nombresUsuario"), sharedPreferences.getString("apellidosUsuario"),
                    sharedPreferences.getString("emailUsuario"), sharedPreferences.getString("documentoUsuario"),
                    sharedPreferences.getString("claveUsuario"), sharedPreferences.getString("direccionIp"),
                    sharedPreferences.getString("sistemaOperativo"), sharedPreferences.getString("tipoUsuario"), numeroTarjetaCredito,
                    fechaVigencia, getNameFranquicia() );








        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
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
                //setNameFranquicia("");
                setNameFranquicia("AMEX");
            }

            else

                //Visa

                if(EditTextNumeroTarjetaCreditoUser.getText().toString().length() > 0 &&
                        (EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 1).equals("4")))
                {

                    EditTextNumeroTarjetaCreditoUser.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.logo_visa, 0, 0, 0);
                    EditTextNumeroTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                    EditTextCVVTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                    //setNameFranquicia("");
                    setNameFranquicia("VISA");

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
                    // setNameFranquicia("");
                    setNameFranquicia("MASTERCARD");

                }

                else
                {
                    EditTextNumeroTarjetaCreditoUser.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.logo_tarjeta_x, 0, 0, 0);
                    EditTextNumeroTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                    EditTextCVVTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                    //setNameFranquicia("");
                    setNameFranquicia("GENERICA");

                }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:






                //EditTextNameUser.setText(sharedPreferences.getString("apellidosUsuario"));
                //EditTextApellidoUser.setText(sharedPreferences.getString("apellidosUsuario"));
                //EditTextEmailUser.setText(sharedPreferences.getString("emailUsuario"));
                //EditTextDocumentoUser.setText(sharedPreferences.getString("documentoUsuario"));
                //EditTextClaveUser.setText(sharedPreferences.getString("claveUsuario"));

                Intent intent = new Intent(Pago.this, Registro.class);
                startActivity(intent);
                finish();
                //EditTextNameUser.setText("");

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

    private void _webServiceRegistroUsuario(final String tokenGCM , String nombre, String apellido, String email, String documento,
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
                            String status = response.getString("status");
                            String message =response.getString("message");

                            if(status.equals("register_ok"))
                            {

                                progressBarRegistroUsuario.setVisibility(View.GONE);
                                TextViewLabelProgressBar.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);
                                builder
                                        .setMessage("El usuario ha sido creado exitosamente.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(Pago.this.getApplicationContext(), Login.class);
                                                startActivity(intent);
                                                finish();
                                                sharedPreferences.clear();
                                            }
                                        }).show();

                            }

                            else

                            if (response.getString("status").equals("register_failed"))
                            {

                                progressBarRegistroUsuario.setVisibility(View.GONE);
                                TextViewLabelProgressBar.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);
                                builder
                                        .setMessage("El email registrado, ya existe en el sistema, por favor verifique e intente de nuevo")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).show();

                            }

                            else

                            if(response.getString("status").equals("invalid_card"))
                            {


                                progressBarRegistroUsuario.setVisibility(View.GONE);
                                TextViewLabelProgressBar.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);
                                builder
                                        .setMessage("Numero de tarjeta invalido, por favor verifique e intente de nuevo")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).show();

                            }


                            if(status.equals("error"))
                            {

                                progressBarRegistroUsuario.setVisibility(View.GONE);
                                TextViewLabelProgressBar.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);
                                builder
                                        .setMessage(message)
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

                        catch (JSONException e)
                        {

                            progressBarRegistroUsuario.setVisibility(View.GONE);
                            TextViewLabelProgressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);
                        builder
                                .setMessage(error.getMessage().toString())
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                        //startActivity(intent);
                                        //finish();
                                    }
                                }).show();





                        if (error instanceof TimeoutError)
                        {

                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                //startActivity(intent);
                                //finish();
                            }
                            }).show();


                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

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

                headers.put("tokenGCM", tokenGCM);
                headers.put("documentoUsuario", sharedPreferences.getString("documentoUsuario"));
                headers.put("nombresUsuario", sharedPreferences.getString("nombresUsuario") );
                headers.put("apellidosUsuario", sharedPreferences.getString("apellidosUsuario"));
                headers.put("emailUsuario", sharedPreferences.getString("emailUsuario"));

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
