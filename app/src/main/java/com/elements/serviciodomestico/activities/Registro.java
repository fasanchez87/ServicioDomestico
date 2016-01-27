package com.elements.serviciodomestico.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elements.serviciodomestico.R;
/**
 * Created by FABiO on 23/01/2016.
 */
public class Registro extends AppCompatActivity
{

    TextInputLayout textInputLayoutNameUser;
    TextInputLayout textInputLayoutApellidoUser;
    TextInputLayout textInputLayoutEmailUser;
    TextInputLayout textInputLayoutClaveUser;
    TextInputLayout textInputLayoutNumeroTarjetaCreditoUser;
    TextInputLayout textInputLayoutMesTarjetaCreditoUser;
    TextInputLayout textInputLayoutAñoTarjetaCreditoUser;
    TextInputLayout textInputLayoutCVVTarjetaCreditoUser;


    EditText EditTextNameUser;
    EditText EditTextApellidoUser;
    EditText EditTextEmailUser;
    EditText EditTextClaveUser;
    EditText EditTextNumeroTarjetaCreditoUser;
    EditText EditTextMesTarjetaCreditoUser;
    EditText EditTextAñoTarjetaCreditoUser;
    EditText EditTextCVVTarjetaCreditoUser;

    Button botonRegistroUsuario;

    ProgressBar progressBarRegistroUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textInputLayoutNameUser = (TextInputLayout) findViewById(R.id.input_layout_nombre_registro);
        textInputLayoutApellidoUser = (TextInputLayout) findViewById(R.id.input_layout_apellido_registro);
        textInputLayoutEmailUser = (TextInputLayout) findViewById(R.id.input_layout_email_registro);
        textInputLayoutClaveUser = (TextInputLayout) findViewById(R.id.input_layout_clave_registro);
        //textInputLayoutNumeroTarjetaCreditoUser = (TextInputLayout) findViewById(R.id.input_layout_numero_tarjeta_registro);
        textInputLayoutMesTarjetaCreditoUser = (TextInputLayout) findViewById(R.id.input_layout_mes_tarjeta_credito_registro);
        textInputLayoutAñoTarjetaCreditoUser = (TextInputLayout) findViewById(R.id.input_layout_año_tarjeta_credito_registro);
        textInputLayoutCVVTarjetaCreditoUser = (TextInputLayout) findViewById(R.id.input_layout_cvv_tarjeta_credito_registro);

        EditTextNameUser = (EditText) findViewById(R.id.edit_text_nombre_registro);
        EditTextApellidoUser = (EditText) findViewById(R.id.edit_text_apellido_registro);
        EditTextEmailUser = (EditText) findViewById(R.id.edit_text_email_registro);
        EditTextClaveUser = (EditText) findViewById(R.id.edit_text_clave_registro);
        EditTextNumeroTarjetaCreditoUser = (EditText) findViewById(R.id.edit_text_numero_tarjeta_registro);
        EditTextMesTarjetaCreditoUser = (EditText) findViewById(R.id.edit_text_mes_tarjeta_credito_registro);
        EditTextAñoTarjetaCreditoUser = (EditText) findViewById(R.id.edit_text_año_tarjeta_credito_registro);
        EditTextCVVTarjetaCreditoUser = (EditText) findViewById(R.id.edit_text_cvv_tarjeta_credito_registro);

        botonRegistroUsuario = (Button) findViewById(R.id.btn_registro);
        botonRegistroUsuario.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Registro();
            }
        });

        progressBarRegistroUsuario = (ProgressBar) findViewById(R.id.progressBarRegistro);

        EditTextNameUser.addTextChangedListener(new RevisorText(EditTextNameUser));
        EditTextApellidoUser.addTextChangedListener(new RevisorText(EditTextApellidoUser));
        EditTextEmailUser.addTextChangedListener(new RevisorText(EditTextEmailUser));
        EditTextClaveUser.addTextChangedListener(new RevisorText(EditTextClaveUser));
        EditTextNumeroTarjetaCreditoUser.addTextChangedListener(new RevisorText(EditTextNumeroTarjetaCreditoUser));
        EditTextMesTarjetaCreditoUser.addTextChangedListener(new RevisorText(EditTextMesTarjetaCreditoUser));
        EditTextAñoTarjetaCreditoUser.addTextChangedListener(new RevisorText(EditTextAñoTarjetaCreditoUser));
        EditTextCVVTarjetaCreditoUser.addTextChangedListener(new RevisorText(EditTextCVVTarjetaCreditoUser));

        EditTextNumeroTarjetaCreditoUser.setText("377813200654045");
        EditTextNameUser.setText("377813200654045");
                EditTextApellidoUser.setText("377813200654045");
        EditTextEmailUser.setText("sdsdsd@gfgfg.com");
                EditTextClaveUser.setText("377813200654045");



    }


    private void Registro()
    {
        if (!validateName())
        {
            return;
        }

        if (!validateApellido())
        {
            return;
        }

        if (!validateEmail())
        {
            return;
        }

        if (!validateClave())
        {
            return;
        }

        if (!validateNumberCreditCard())
        {
            return;
        }

        if (!validateCardAlgorithmLuhn())
        {
            return;
        }


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

    private boolean validateNumberCreditCard()
    {

        String numberCard = EditTextNumeroTarjetaCreditoUser.getText().toString();


        if (EditTextNumeroTarjetaCreditoUser.getText().toString().trim().isEmpty())
        {
            EditTextNumeroTarjetaCreditoUser.setError("Número de tarjeta invalido");
            requestFocus(EditTextNumeroTarjetaCreditoUser);
            return false;
        }


        else
        {
            textInputLayoutNameUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCardAlgorithmLuhn()
    {

        String numberCard = EditTextNumeroTarjetaCreditoUser.getText().toString();
        numberCard = numberCard.replaceAll("\\s+","");


        if ( !validarTarjetaAlgoritmoLuhn( numberCard ) )
        {
            EditTextNumeroTarjetaCreditoUser.setError("Número de tarjeta invalido");
            requestFocus(EditTextNumeroTarjetaCreditoUser);
            return false;
        }


        else
        {
            textInputLayoutNameUser.setErrorEnabled(false);
        }

        return true;
    }



    public boolean validarTarjetaAlgoritmoLuhn( String numberCard ) {


        //numberCard = "377565656655665";

        //if (numberCard.length() == 15) {
            char[] c = numberCard.toCharArray();
            int[] cint = new int[numberCard.length()];
            for (int i = 0; i < numberCard.length(); i++) {
                if (i % 2 == 1) {
                    cint[i] = Integer.parseInt(String.valueOf(c[i])) * 2;
                    if (cint[i] > 9)
                        cint[i] = 1 + cint[i] % 10;
                } else
                    cint[i] = Integer.parseInt(String.valueOf(c[i]));
            }

            int sum = 0;

            for (int i = 0; i < numberCard.length(); i++) {
                sum += cint[i];
            }

            if (sum % 10 == 0)
                //result.setText("Card is Valid");

                return true;


            else
                //result.setText("Card is Invalid");
            Log.w("Error Tarjeta: ","Card is Valid_1");

            return false;

       // }

      //  else
        //    Log.w("Error Tarjeta: ","Card is Valid_3");
         //   return false;
    }

    public void validateFranquiciasIcon()
    {

        String numberCard = EditTextNumeroTarjetaCreditoUser.getText().toString();



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

                case R.id.edit_text_numero_tarjeta_registro:
                    validateNumberCreditCard();
                    //validateFranquiciasIcon();
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

            if(EditTextNumeroTarjetaCreditoUser.getText().toString().length() > 1 &&
                 (EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 2).equals("34") ||
                  EditTextNumeroTarjetaCreditoUser.getText().toString().substring(0, 2).equals("37"))
               )
            {

                EditTextNumeroTarjetaCreditoUser.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.icon_amex, 0, 0, 0);
                EditTextNumeroTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
            }

            else
            {
                EditTextNumeroTarjetaCreditoUser.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.logo_tarjeta_x, 0, 0, 0);
                EditTextNumeroTarjetaCreditoUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            }




        }
    }

}