package com.elements.serviciodomestico.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
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

import com.elements.serviciodomestico.R;
import com.elements.serviciodomestico.sharedPreferences.gestionSharedPreferences;

/**
 * Created by FABiO on 23/01/2016.
 */
public class Registro extends AppCompatActivity
{

    TextInputLayout textInputLayoutNameUser;
    TextInputLayout textInputLayoutApellidoUser;
    TextInputLayout textInputLayoutEmailUser;
    TextInputLayout textInputLayoutClaveUser;
    TextInputLayout textInputLayoutDocumentoUser;

    EditText EditTextNameUser;
    EditText EditTextApellidoUser;
    EditText EditTextEmailUser;
    EditText EditTextClaveUser;
    EditText EditTextDocumentoUser;

    Button botonRegistroUsuario;

    private String nameFranquicia;

    private gestionSharedPreferences sharedPreferences;

    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
    private String documentoUsuario;
    private String claveUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences = new gestionSharedPreferences(getApplicationContext());

        nameFranquicia = "";

        textInputLayoutNameUser = (TextInputLayout) findViewById(R.id.input_layout_nombre_registro);
        textInputLayoutApellidoUser = (TextInputLayout) findViewById(R.id.input_layout_apellido_registro);
        textInputLayoutEmailUser = (TextInputLayout) findViewById(R.id.input_layout_email_registro);
        textInputLayoutClaveUser = (TextInputLayout) findViewById(R.id.input_layout_clave_registro);
        textInputLayoutDocumentoUser = (TextInputLayout) findViewById(R.id.input_layout_documento_registro);


        EditTextNameUser = (EditText) findViewById(R.id.edit_text_nombre_registro);
        EditTextApellidoUser = (EditText) findViewById(R.id.edit_text_apellido_registro);
        EditTextEmailUser = (EditText) findViewById(R.id.edit_text_email_registro);
        EditTextClaveUser = (EditText) findViewById(R.id.edit_text_clave_registro);
        EditTextDocumentoUser = (EditText) findViewById(R.id.edit_text_documento_registro);


        botonRegistroUsuario = (Button) findViewById(R.id.btn_siguiente);
        botonRegistroUsuario.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                intentToActivityPago();
            }
        });

        EditTextNameUser.addTextChangedListener(new RevisorText(EditTextNameUser));
        EditTextApellidoUser.addTextChangedListener(new RevisorText(EditTextApellidoUser));
        EditTextEmailUser.addTextChangedListener(new RevisorText(EditTextEmailUser));
        EditTextClaveUser.addTextChangedListener(new RevisorText(EditTextClaveUser));
        EditTextDocumentoUser.addTextChangedListener(new RevisorText(EditTextDocumentoUser));


        //EditTextNumeroTarjetaCreditoUser.setText("377813200654045");
        EditTextNameUser.setText("377813200654045");
                EditTextApellidoUser.setText("377813200654045");
        EditTextEmailUser.setText("sdsdsd@gfgfg.com");
                EditTextClaveUser.setText("377813200654045");

    }

    private boolean ValidarFormulario()
    {
        if (!validateName())
        {
            return false;
        }

        if (!validateApellido())
        {
            return false;
        }

        if (!validateEmail())
        {
            return false;
        }

        if (!validateClave())
        {
            return false;
        }

        if (!validateDocumento())
        {
            return false;
        }

        return true;

    }

    private String getDireccionIP()
    {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public void intentToActivityPago()
    {
        if(ValidarFormulario())
        {

            nombreUsuario = EditTextNameUser.getText().toString();
            apellidoUsuario = EditTextApellidoUser.getText().toString();
            emailUsuario = EditTextEmailUser.getText().toString();
            documentoUsuario = EditTextDocumentoUser.getText().toString();
            claveUsuario = EditTextClaveUser.getText().toString();

            sharedPreferences.putString("nombresUsuario",nombreUsuario);
            sharedPreferences.putString("apellidosUsuario",apellidoUsuario);
            sharedPreferences.putString("emailUsuario",emailUsuario);
            sharedPreferences.putString("documentoUsuario",documentoUsuario);
            sharedPreferences.putString("claveUsuario",claveUsuario);
            sharedPreferences.putString("direccionIp", getDireccionIP());
            sharedPreferences.putString("sistemaOperativo", "Android");
            sharedPreferences.putString("tipoUsuario", "C");

            Intent intent = new Intent(Registro.this, Pago.class);
            startActivity(intent);
            //overridePendingTransition(R.anim.right_in, R.anim.right_in);
            finish();

        }
    }


    public String getNameFranquicia()
    {
        return nameFranquicia;
    }

    public void setNameFranquicia(String nameFranquicia)
    {
        this.nameFranquicia = nameFranquicia;
    }

    private boolean validateName()
    {



        if (EditTextNameUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutNameUser.setError(getString(R.string.err_msg_name));
            requestFocus(EditTextNameUser);
            return false;
        }

        else
        {
            textInputLayoutNameUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateApellido()
    {
        if (EditTextApellidoUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutApellidoUser.setError(getString(R.string.err_msg_apellido));
            requestFocus(EditTextApellidoUser);
            return false;
        }

        else
        {
            textInputLayoutApellidoUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail()
    {
        String email = EditTextEmailUser.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email))
        {
            textInputLayoutEmailUser.setError(getString(R.string.err_msg_email));//cambiar a edittext en register!!
            requestFocus(EditTextEmailUser);
            return false;
        }

        else
        {
            textInputLayoutEmailUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateClave()
    {
        if (EditTextClaveUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutClaveUser.setError(getString(R.string.err_msg_password));
            requestFocus(EditTextClaveUser);
            return false;
        }

        else

        if(EditTextClaveUser.getText().toString().trim().length() < 5)
        {
            textInputLayoutClaveUser.setError(getString(R.string.err_msg_password_length));
            requestFocus(EditTextClaveUser);
            return false;
        }

        else
        {
            textInputLayoutClaveUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDocumento()
    {
        if (EditTextDocumentoUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutDocumentoUser.setError("Por favor, dígite numero de documento");
            requestFocus(EditTextDocumentoUser);
            return false;
        }

        else

        if(EditTextDocumentoUser.getText().toString().trim().length() < 5)
        {
            textInputLayoutDocumentoUser.setError("El documento debe tener al menos 5 digitos");
            requestFocus(EditTextDocumentoUser);
            return false;
        }

        else
        {
            textInputLayoutDocumentoUser.setErrorEnabled(false);
        }

        return true;
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

            char space = ' ';

            switch (view.getId())
            {
                case R.id.edit_text_nombre_registro:
                    validateName();
                    break;

                case R.id.edit_text_apellido_registro:
                    validateApellido();
                    break;

                case R.id.edit_text_email_registro:
                    validateEmail();
                    break;

                case R.id.edit_text_clave_registro:
                    validateClave();
                    break;

                case R.id.edit_text_documento_registro:
                    validateDocumento();
                    break;
            }

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(Registro.this, Login.class);
                startActivity(intent);
                finish();
                //overridePendingTransition(R.anim.left_out, R.anim.left_in);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(Registro.this, Login.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(R.anim.left_out, R.anim.left_in);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
