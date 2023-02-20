package com.example.mobilenoauthentication.Otp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.mobilenoauthentication.R;
import com.google.android.material.button.MaterialButton;
import com.hbb20.CountryCodePicker;

public class SignUpActivity extends AppCompatActivity {

    CountryCodePicker codePicker;
    EditText mobileInput;
    MaterialButton sendotp;
    String  MN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        codePicker=findViewById(R.id.ccp_id);
        mobileInput=  findViewById(R.id.mobile_input_id);
        sendotp=findViewById(R.id.sendOtp_btn_id);
        codePicker.registerCarrierNumberEditText(mobileInput);



        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MN=codePicker.getSelectedCountryCodeWithPlus().toString().concat(mobileInput.getText().toString());
                System.out.println(MN);
                Intent intent=new Intent(SignUpActivity.this,OtpfillActivity.class);

                intent.putExtra("Mobile",MN);

                startActivity(intent);



            }
        });



    }
}