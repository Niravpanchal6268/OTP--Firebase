package com.example.mobilenoauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OtpfillActivity extends AppCompatActivity {

    TextView receverNo,cdtime;
    MaterialButton continueBtn;
    TextInputLayout otpinput;
    String otpid;
    FirebaseAuth mAuth;
   String MobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpfill);

        otpinput=findViewById(R.id.otp_input_id);
        mAuth=FirebaseAuth.getInstance();
        continueBtn=findViewById(R.id.continue_btn_id);

        MobileNo=getIntent().getStringExtra("Mobile").toString();
        receverNo=findViewById(R.id.reciver_mobile_id);
        receverNo.setText(MobileNo);
        System.out.println(MobileNo);

//        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        otp();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (otpinput.getEditText().getText().toString().isEmpty())
                    {
                        Toast.makeText(OtpfillActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                    }
                    else if (otpinput.getEditText().getText().toString().length()!=6)
                    {
                        Toast.makeText(OtpfillActivity.this, "Enter 6 digit of code", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                            PhoneAuthCredential credential=PhoneAuthProvider.getCredential(otpid,otpinput.getEditText().getText().toString());
                            SignwithphoneCredential(credential);
                    }

            }
        });






    }

    private void otp() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                MobileNo,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpid=s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                    {
                        SignwithphoneCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Toast.makeText(OtpfillActivity.this, "otp recever Error -"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        );


    }

    private void SignwithphoneCredential(PhoneAuthCredential phoneAuthCredential) {

            mAuth.signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(OtpfillActivity.this, "sign successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(OtpfillActivity.this,HomeActivity.class));
                                finish();
                            }
                            else {
                                Toast.makeText(OtpfillActivity.this, "sign fails", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OtpfillActivity.this, "sign in Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


    }
}