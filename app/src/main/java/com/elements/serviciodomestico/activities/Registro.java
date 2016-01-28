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

    private String nameFranquicia;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        nameFranquicia = "";

        textInputLayoutNameUser = (TextInputLayout) findViewById(R.id.input_layout_nombre_registro);
        textInputLayoutApellidoUser = (TextInputLayout) findViewById(R.id.input_layout_apellido_registro);
        textInputLayoutEmailUser = (TextInputLayout) findViewById(R.id.input_layout_email_registro);
        textInputLayoutClaveUser = (TextInputLayout) findViewById(R.id.input_layout_clave_registro);
        textInputLayoutNumeroTarjetaCreditoUser = (TextInputLayout) findViewById(R.id.input_layout_numero_tarjeta_registro);
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

        textInputLayoutNumeroTarjetaCreditoUser.setHintAnimationEnabled(false);
        EditTextNumeroTarjetaCreditoUser.setHint("Número tarjeta de crédito");


        //EditTextNumeroTarjetaCreditoUser.setText("377813200654045");
        EditTextNameUser.setText("377813200654045");
                EditTextApellidoUser.setText("377813200654045");
        EditTextEmailUser.setText("sdsdsd@gfgfg.com");
                EditTextClaveUser.setText("377813200654045");

        EditTextCVVTarjetaCreditoUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    EditTextCVVTarjetaCreditoUser.setText("");
                }
            }

        });
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

        if (!validateMesCreditCard()) {
            return;
        }

        if (!validateAñoCreditCard())
        {
            return;
        }

        if (!validateCVVCreditCard())
        {
            return;
        }

        if (!validateCVVFranquiciaCreditCard())
        {
            return;
        }

        Toast toast =
                Toast.makeText(getApplicationContext(),
                        "make RESt.", Toast.LENGTH_SHORT);
        toast.show();




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
            EditTextEmailUser.setError(getString(R.string.err_msg_email));//cambiar a edittext en register!!
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
        Log.w("Error Tarjeta: ",numberCard);

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

}
