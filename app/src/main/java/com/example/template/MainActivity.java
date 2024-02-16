package com.example.template;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButton;
    BottomNavigationView navigationView;
    private DatabaseReference dataRef;
    private boolean statusDoor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        initListener();
        getToken();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println("Token " + token);
                    }
                });
    }

    private void initUi() {
        navigationView = findViewById(R.id.bottom_nav);
        imageButton = findViewById(R.id.lock);
        dataRef = FirebaseDatabase.getInstance().getReference().child("Listener");
    }

    private void initListener() {
        dataRef.child("Door").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                statusDoor = snapshot.getValue(boolean.class);

                if(statusDoor) {
                    imageButton.setImageResource(R.drawable.lock);
                }
                else {
                    imageButton.setImageResource(R.drawable.unlock);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                statusDoor = !statusDoor;

                if(statusDoor) {
                    imageButton.setImageResource(R.drawable.lock);
                }
                else {
                    imageButton.setImageResource(R.drawable.unlock);
                }

                dataRef.child("Door").setValue(statusDoor);
            }
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent;

                switch (item.getItemId()) {
                    case R.id.action_history:
                        intent = new Intent(MainActivity.this, HistoryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_setting:
                        intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });
    }
}