package com.example.template;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {

    private LinearLayout btnSignOut;
    private LinearLayout btnRfid;
    private LinearLayout btnFinger;
    private LinearLayout btnPass;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUi();
        initListener();
    }

    private void initUi() {
        btnSignOut = findViewById(R.id.btn_sign_out);
        btnRfid = findViewById(R.id.btn_rfid);
        btnFinger = findViewById(R.id.btn_finger);
        btnPass = findViewById(R.id.btn_pass);
    }

    private void initListener(){
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(SettingActivity.this, SplashActivity.class);
                startActivity(intent);
            }
        });

        btnRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(SettingActivity.this, InfoRFIDActivity.class);
                startActivity(intent);
            }
        });

        btnFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(SettingActivity.this, infoFrintActivity.class);
                startActivity(intent);
            }
        });

        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(SettingActivity.this, PasswordGuessActivity.class);
                startActivity(intent);
            }
        });
    }
}