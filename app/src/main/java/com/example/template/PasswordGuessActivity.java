package com.example.template;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PasswordGuessActivity extends AppCompatActivity {
    private CardView btnGuess;
    private EditText edtPassGuess;
    private DatabaseReference dataRef;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_guess);

        initUi();
        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGuessPhone();
            }
        });
    }

    private void initUi() {
        btnGuess = findViewById(R.id.btn_guess);
        edtPassGuess = findViewById(R.id.edt_pass_guess);
        dataRef = FirebaseDatabase.getInstance().getReference().child("Listener");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendSMS();
        }
    }

    private void onClickGuessPhone() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                PasswordGuessActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay != 0) {
                            time = hourOfDay + " giờ " + minute + " phút";
                        }
                        else time = minute + " phút";

                        dataRef.child("Hour").setValue(hourOfDay);
                        dataRef.child("Minute").setValue(minute);

                        if(ContextCompat.checkSelfPermission(PasswordGuessActivity.this, Manifest.permission.SEND_SMS)
                                == PackageManager.PERMISSION_GRANTED) {
                            sendSMS();

                        } else {
                            ActivityCompat.requestPermissions(PasswordGuessActivity.this, new String[]{Manifest.permission.SEND_SMS},
                                    100);
                        }
                    }
                }, 0, 30, true
        );

        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();
    }

    // Hàm để gửi tin nhắn SMS
    private void sendSMS() {
        Random random = new Random();
        StringBuilder randomDigits = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            randomDigits.append(digit);
        }

        String strPassGuess = edtPassGuess.getText().toString().trim();

        if(strPassGuess.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_LONG).show();
            return;
        } else if(strPassGuess.length() < 9 || strPassGuess.length() > 10) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đúng số điện thoại", Toast.LENGTH_LONG).show();
            return;
        }

        String phoneNumber = "+84" + strPassGuess.substring(1);
        String msgPass = "Mật khẩu khách của bạn là: " + randomDigits.toString();
        String msgTime = "Mật khẩu sẽ có tác dụng trong " + time + " kể từ thời điểm này.";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, msgPass, null, null);
            smsManager.sendTextMessage(phoneNumber, null, msgTime, null, null);
            Toast.makeText(getApplicationContext(), "Mật khẩu đã được gửi thành công.", Toast.LENGTH_LONG).show();
            dataRef.child("Guest").setValue(randomDigits.toString());
            btnGuess.setClickable(false);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Gửi thất bại. Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
        }
    }
}