package com.elements.serviciodomestico;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
        textBienvenidoLogin = (TextView) findViewById(R.id.textViewBienvenidoLogin);
        textDescripcionLogin = (TextView) findViewById(R.id.textViewDescripcionLogin);
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
        textBienvenidoLogin.setTypeface(tf);
        textDescripcionLogin.setTypeface(tf);
        textOlvidoClave.setTypeface(tf);
        textRegistro.setTypeface(tf);




    }

    private void Login()
    {
        if (!validateEmail())
        {
            return;
        }

        if (!validatePassword()) {
            return;
        }


        Toast.makeText(getApplicationContext(), "Registro! hacer RESt", Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.VISIBLE);
        textViewProgressBar.setVisibility(View.VISIBLE);
    }


    private boolean validateEmail()
    {
        String email = emailLogin.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email))
        {
            textInputLayoutEmail.setError(getString(R.string.err_msg_email));
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
