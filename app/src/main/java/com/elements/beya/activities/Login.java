package com.elements.beya.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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
import com.elements.beya.sharedPreferences.gestionSharedPreferences;
import com.elements.beya.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by FABiO on 23/01/2016.
 */
public class Login extends AppCompatActivity
{

    TextView textBienvenidoLogin;
    TextView textDescripcionLogin;
    TextView textOlvidoClave;
    TextView textRegistro;
    TextView textViewRegistroLogin;
    TextView textViewProgressBar;

    private gestionSharedPreferences sharedPreferences;

    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutClave;

    private Boolean saveLogin;

    EditText emailLogin;
    EditText claveLogin;

    Button botonLogin;

    ProgressBar progressBar;

    private String emailUser,claveUser,_urlWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        String fontPath = "fonts/Raleway-Medium.ttf";




        // text view label

        textOlvidoClave = (TextView) findViewById(R.id.textViewRecordarClaveLogin);
        textRegistro = (TextView) findViewById(R.id.textViewRegistroLogin);
        textViewProgressBar = (TextView) findViewById(R.id.textViewProgressBar);

        textOlvidoClave.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this, ValidarEmail.class);
                startActivity(intent);
                // overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();            }
        });



        textRegistro.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this, Registro.class);
                startActivity(intent);
               // overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();            }
        });

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        textInputLayoutClave = (TextInputLayout) findViewById(R.id.input_layout_password);

        emailLogin = (EditText) findViewById(R.id.input_email);
        claveLogin = (EditText) findViewById(R.id.input_password);



        botonLogin = (Button) findViewById(R.id.btn_signup);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Login();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);



        emailLogin.addTextChangedListener(new RevisorText(emailLogin));
        claveLogin.addTextChangedListener(new RevisorText(claveLogin));

        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        // Applying font
        //textBienvenidoLogin.setTypeface(tf);
        //textDescripcionLogin.setTypeface(tf);
        //textOlvidoClave.setTypeface(tf);
        //textRegistro.setTypeface(tf);

        //saveLogin = false;

        sharedPreferences = new gestionSharedPreferences(getApplicationContext());
        //sharedPreferences.putBoolean("GuardarSesion",false);
        saveLogin = sharedPreferences.getBoolean("GuardarSesion");

        if (saveLogin == true)
        {

            cargarActivitySharedPrefenrenceToGestion();
            emailLogin.setText(sharedPreferences.getString("email"));
            claveLogin.setText(sharedPreferences.getString("clave"));
            //recordarClave.setChecked(true);
        }


    }

    public void cargarActivitySharedPrefenrenceToGestion()
    {

        startActivity(new Intent(Login.this, Gestion.class));
        Login.this.finish();

    }

    private void Login()
    {
        if (!validateEmail())
        {
            return;
        }

        if (!validatePassword())
        {
            return;
        }

        emailUser = emailLogin.getText().toString();
        claveUser = claveLogin.getText().toString();

        _webServiceLogin(emailUser, claveUser);

    }

    private void _webServiceLogin(final String emailUser , final String claveUser)
    {
        _urlWebService = "http://52.72.85.214/ws/login";

        progressBar.setVisibility(View.VISIBLE);
        textViewProgressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                               String status = response.getString("status");
                               String message = response.getString("message");



                            if(status.equals("ok"))
                                {


                                    String nombre = response.getString("nombresUsuario");
                                    String apellidos = response.getString("apellidosUsuario");
                                    String nombreUsuario = (nombre+" "+apellidos);
                                    String emailUser = response.getString("emailUsuario");

                                    sharedPreferences.putString("nombreUsuario",nombreUsuario);
                                    sharedPreferences.putString("emailUser",emailUser);

                                    progressBar.setVisibility(View.GONE);
                                    textViewProgressBar.setVisibility(View.GONE);

                                    //AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                    //builder
                                      //      .setMessage(message)
                                        //    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                          //  {
                                            //    @Override
                                              //  public void onClick(DialogInterface dialog, int id)
                                                //{
                                                    Intent intent = new Intent(Login.this, Gestion.class);
                                                    startActivity(intent);
                                                    sharedPreferences.putBoolean("GuardarSesion",true);
                                                    sharedPreferences.putString("email", emailUser);
                                                    sharedPreferences.putString("clave",claveUser);
                                                    finish();

                                    //}
                                            //}).show();


                                }

                                else
                                {
                                    if(status.equals("error"))
                                    {

                                        progressBar.setVisibility(View.GONE);
                                        textViewProgressBar.setVisibility(View.GONE);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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
                            }

                        catch (JSONException e)
                        {

                            progressBar.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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


                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        if (error instanceof TimeoutError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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
                        textViewProgressBar.setVisibility(View.GONE);

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

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap <String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("user", emailUser);
                headers.put("pass", claveUser);
                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");

    }


    private boolean validateEmail()
    {
        String email = emailLogin.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email))
        {
            textInputLayoutEmail.setError(getString(R.string.err_msg_email));//cambiar a edittext en register!!
            requestFocus(emailLogin);
            return false;
        }

        else
        {
            textInputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword()
    {
        if (claveLogin.getText().toString().trim().isEmpty())
        {
            textInputLayoutClave.setError(getString(R.string.err_msg_password));
            requestFocus(claveLogin);
            return false;
        }

        else
        {
            textInputLayoutClave.setErrorEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private static boolean isValidEmail(String email)
    {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
            switch (view.getId())
            {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;

            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {

            finish();
            //overridePendingTransition(R.anim.left_out, R.anim.left_in);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}