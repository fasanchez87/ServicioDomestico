package com.elements.serviciodomestico.activities;

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
import com.elements.serviciodomestico.R;
import com.elements.serviciodomestico.volley.ControllerSingleton;

import org.json.JSONArray;
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

    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutClave;

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


        textRegistro.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this, Registro.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();            }
        });

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        textInputLayoutClave = (TextInputLayout) findViewById(R.id.input_layout_password);

        emailLogin = (EditText) findViewById(R.id.input_email);
        claveLogin = (EditText) findViewById(R.id.input_password);

        emailLogin.setText("joan@elements.com.co");
        claveLogin.setText("colombia");


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

                                if(status.equals("ok"))
                                {

                                    String message =response.getString("message");
                                    Toast toast1 =
                                            Toast.makeText(getApplicationContext(),
                                                    message, Toast.LENGTH_SHORT);

                                    toast1.show();

                                    progressBar.setVisibility(View.GONE);
                                    textViewProgressBar.setVisibility(View.GONE);


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

                                        progressBar.setVisibility(View.GONE);
                                        textViewProgressBar.setVisibility(View.GONE);

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

                            progressBar.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

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
}
