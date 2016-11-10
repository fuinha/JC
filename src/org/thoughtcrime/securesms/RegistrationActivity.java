package org.thoughtcrime.securesms;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.thoughtcrime.securesms.crypto.MasterSecret;
import org.thoughtcrime.securesms.util.Dialogs;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.thoughtcrime.securesms.util.Util;
import org.whispersystems.signalservice.api.util.PhoneNumberFormatter;

//---------------phone functions-------------------
//date time
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//MAIL
//imei
import android.content.Intent;
import android.telephony.TelephonyManager;
//unique id
import android.provider.Settings.Secure;
import android.content.Context;
//---------------phone functions-------------------

//activate--------------------------
//CRIP
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
//activate--------------------------

/**
 * The register account activity.  Prompts ths user for their registration information
 * and begins the account registration process.
 *
 * @author Moxie Marlinspike
 */
public class RegistrationActivity extends BaseActionBarActivity {

    private static final int PICK_COUNTRY = 1;
    private static final String TAG = RegistrationActivity.class.getSimpleName();

    private AsYouTypeFormatter countryFormatter;
    private ArrayAdapter<String> countrySpinnerAdapter;
    private Spinner countrySpinner;
    private TextView countryCode;
    private TextView number;
    private Button createButton;
    private Button skipButton;

    private MasterSecret masterSecret;

    //  /*
    //activate--------------------------
    private TextView TextViewCodes;
    private EditText EditTextCodes;
    private String CodesIM = null;
    private String CodesID = null;
    private String CodesFull = null;
    private String Temps = null;

    //------------CRIPTUS---------------
    EncodeDecodeAES myCrip = new EncodeDecodeAES();

    String Seeds = "jcunion";
    String passwordEnc = null;
    String passwordDecs = null;
    //------------CRIPTUS---------------

    //------------DEVICES---------------

    String unique_id = null;
    String unique_imei = null;
    String unique_value = null;
    String unique_temp = null;
    String unique_mail = null;

    TelephonyManager telephonyManager;
    private Context context;
    //------------DEVICES---------------

    //--------------------------------------------------------------
    private String takeImed() {
        String returno = null;
        //------------DEVICES---------------
        //imei object
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        //DATE
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        //DeviceFinalDate.setText(date);//datae time

        //MAIL

        //DeviceFinalMail.setText(unique_mail);

        // TODO Auto-generated method stub
        unique_imei = telephonyManager.getDeviceId();

        unique_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        //DeviceModel= android.os.Build.MODEL;
        //DeviceName= android.os.Build.MANUFACTURER;

        //------------DEVICES---------------
        CodesID = unique_id;
        CodesIM = unique_imei;

        CodesFull = CodesID + "#" + CodesIM;
        //cripto
        //------------ECRIPTUS---------------
        try {
            passwordEnc = myCrip.encrypt(Seeds, CodesFull);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //------------CRIPTUS---------------
        CodesFull = CodesIM + "#" + CodesID;
        returno = passwordEnc;

        return returno;
    }

    //--------------------------------------------------------------
    private Boolean testeImed(String Codesf) {
        Boolean returnos = false;

        String passwordRoot = "M0NGRENEOEY5QzM0MDFCRjlBNTMzODJBODBBM0VENjM="; //Marcelo24
        String returnosS = null;
        returnosS = "";

        try {
            passwordRoot = EncodeDecodeAES.decrypt(Seeds, passwordRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //------------DCRIPTUS---------------
        if (!(Codesf.equals(""))) {//not null

            try {
                //code insterted is criptus
                passwordDecs = EncodeDecodeAES.decrypt(Seeds, Codesf);
                //------------CRIPTUS---------------

                 returnosS = passwordDecs;//dencript

                //------------RotateCode---------------

                int position = returnosS.lastIndexOf("#");
                String parteFinal = returnosS.substring(position + 1);
                CodesIM = parteFinal;

                parteFinal = returnosS.substring(0, position);
                CodesID = parteFinal;
                returnosS = CodesIM + "#" + CodesID;//dencript

            } catch (Exception e) {
                e.printStackTrace();
            }

            //------------RotateCode---------------
            //test rote cripted and encoded
            if (returnosS.equals(CodesFull))
                returnos = true;

            if (returnos == false) {
                //using root not crypted
                if ((Codesf.equals(passwordRoot)))
                    returnos = true;
            }
            //null final test
            if ((CodesID.equals(null)) || (CodesIM.equals(null)))
                returnos = false;
        }
        //descripto
        return returnos;
    }


    //activate--------------------------
//*/
//--------------------------------------------------------------
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.registration_activity);

        getSupportActionBar().setTitle(getString(R.string.RegistrationActivity_connect_with_signal));

        initializeResources();
        initializeSpinner();
        initializeNumber();

        // /*
        //activate--------------------------
        String Codes = takeImed();
        TextViewCodes.setText(Codes);//
        //activate--------------------------
// */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_COUNTRY && resultCode == RESULT_OK && data != null) {
            this.countryCode.setText(data.getIntExtra("country_code", 1) + "");
            setCountryDisplay(data.getStringExtra("country_name"));
            setCountryFormatter(data.getIntExtra("country_code", 1));
        }
    }

    private void initializeResources() {
        this.masterSecret = getIntent().getParcelableExtra("master_secret");
        this.countrySpinner = (Spinner) findViewById(R.id.country_spinner);
        this.countryCode = (TextView) findViewById(R.id.country_code);
        this.number = (TextView) findViewById(R.id.number);
        this.createButton = (Button) findViewById(R.id.registerButton);
        this.skipButton = (Button) findViewById(R.id.skipButton);

        this.countryCode.addTextChangedListener(new CountryCodeChangedListener());
        this.number.addTextChangedListener(new NumberChangedListener());
        this.createButton.setOnClickListener(new CreateButtonListener());
        this.skipButton.setOnClickListener(new CancelButtonListener());

        // /*
        //activate--------------------------
        //ACTIVATE
        this.TextViewCodes = (TextView) findViewById(R.id.textViewCode);//codes activate
        this.EditTextCodes = (EditText) findViewById(R.id.editTextCode);

        //activate--------------------------
//*/
        if (getIntent().getBooleanExtra("cancel_button", false)) {
            this.skipButton.setVisibility(View.VISIBLE);
        } else {
            this.skipButton.setVisibility(View.INVISIBLE);
        }

        findViewById(R.id.twilio_shoutout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://universalcasino.es"));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.w(TAG, e);
                }
            }
        });
    }

    private void initializeSpinner() {
        this.countrySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        this.countrySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setCountryDisplay(getString(R.string.RegistrationActivity_select_your_country));

        this.countrySpinner.setAdapter(this.countrySpinnerAdapter);
        this.countrySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(RegistrationActivity.this, CountrySelectionActivity.class);
                    startActivityForResult(intent, PICK_COUNTRY);
                }
                return true;
            }
        });
        this.countrySpinner.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent intent = new Intent(RegistrationActivity.this, CountrySelectionActivity.class);
                    startActivityForResult(intent, PICK_COUNTRY);
                    return true;
                }
                return false;
            }
        });
    }

    private void initializeNumber() {
        PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
        String localNumber = Util.getDeviceE164Number(this);

        try {
            if (!TextUtils.isEmpty(localNumber)) {
                Phonenumber.PhoneNumber localNumberObject = numberUtil.parse(localNumber, null);

                if (localNumberObject != null) {
                    this.countryCode.setText(String.valueOf(localNumberObject.getCountryCode()));
                    this.number.setText(String.valueOf(localNumberObject.getNationalNumber()));
                }
            } else {
                String simCountryIso = Util.getSimCountryIso(this);

                if (!TextUtils.isEmpty(simCountryIso)) {
                    this.countryCode.setText(numberUtil.getCountryCodeForRegion(simCountryIso) + "");
                }
            }
        } catch (NumberParseException npe) {
            Log.w(TAG, npe);
        }
    }

    private void setCountryDisplay(String value) {
        this.countrySpinnerAdapter.clear();
        this.countrySpinnerAdapter.add(value);
    }

    private void setCountryFormatter(int countryCode) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        String regionCode = util.getRegionCodeForCountryCode(countryCode);

        if (regionCode == null) this.countryFormatter = null;
        else this.countryFormatter = util.getAsYouTypeFormatter(regionCode);
    }

    private String getConfiguredE164Number() {
        return PhoneNumberFormatter.formatE164(countryCode.getText().toString(),
                number.getText().toString());
    }

    private class CreateButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final RegistrationActivity self = RegistrationActivity.this;

            if (TextUtils.isEmpty(countryCode.getText())) {
                Toast.makeText(self,
                        getString(R.string.RegistrationActivity_you_must_specify_your_country_code),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(number.getText())) {
                Toast.makeText(self,
                        getString(R.string.RegistrationActivity_you_must_specify_your_phone_number),
                        Toast.LENGTH_LONG).show();
                return;
            }

            final String e164number = getConfiguredE164Number();

            if (!PhoneNumberFormatter.isValidNumber(e164number)) {
                Dialogs.showAlertDialog(self,
                        getString(R.string.RegistrationActivity_invalid_number),
                        String.format(getString(R.string.RegistrationActivity_the_number_you_specified_s_is_invalid),
                                e164number));
                return;
            }

            int gcmStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(self);

            if (gcmStatus != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(gcmStatus)) {
                    GooglePlayServicesUtil.getErrorDialog(gcmStatus, self, 9000).show();
                } else {
                    Dialogs.showAlertDialog(self, getString(R.string.RegistrationActivity_unsupported),
                            getString(R.string.RegistrationActivity_sorry_this_device_is_not_supported_for_data_messaging));
                }
                return;
            }

            // /*
            //activate--------------------------
            Boolean liberate = testeImed(EditTextCodes.getText().toString());
            if (liberate) {//only liberate in activate
                //activate--------------------------
// */
                AlertDialog.Builder dialog = new AlertDialog.Builder(self);
                dialog.setTitle(PhoneNumberFormatter.getInternationalFormatFromE164(e164number));
                dialog.setMessage(R.string.RegistrationActivity_we_will_now_verify_that_the_following_number_is_associated_with_your_device_s);
                dialog.setPositiveButton(getString(R.string.RegistrationActivity_continue),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(self, RegistrationProgressActivity.class);
                                intent.putExtra("e164number", e164number);
                                intent.putExtra("master_secret", masterSecret);
                                startActivity(intent);
                                finish();
                            }
                        });
                dialog.setNegativeButton(getString(R.string.RegistrationActivity_edit), null);
                dialog.show();
// /*
                //activate--------------------------
            }//end liberate
            //activate--------------------------
// */
        } //end onClick
    }

    private class CountryCodeChangedListener implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) {
                setCountryDisplay(getString(R.string.RegistrationActivity_select_your_country));
                countryFormatter = null;
                return;
            }

            int countryCode = Integer.parseInt(s.toString());
            String regionCode = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(countryCode);

            setCountryFormatter(countryCode);
            setCountryDisplay(PhoneNumberFormatter.getRegionDisplayName(regionCode));

            if (!TextUtils.isEmpty(regionCode) && !regionCode.equals("ZZ")) {
                number.requestFocus();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    private class NumberChangedListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            if (countryFormatter == null)
                return;

            if (TextUtils.isEmpty(s))
                return;

            countryFormatter.clear();

            String number = s.toString().replaceAll("[^\\d.]", "");
            String formattedNumber = null;

            for (int i = 0; i < number.length(); i++) {
                formattedNumber = countryFormatter.inputDigit(number.charAt(i));
            }

            if (formattedNumber != null && !s.toString().equals(formattedNumber)) {
                s.replace(0, s.length(), formattedNumber);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }

    private class CancelButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextSecurePreferences.setPromptedPushRegistration(RegistrationActivity.this, true);
            Intent nextIntent = getIntent().getParcelableExtra("next_intent");

            if (nextIntent == null) {
                nextIntent = new Intent(RegistrationActivity.this, ConversationListActivity.class);
            }

            startActivity(nextIntent);
            finish();
        }
    }
}
