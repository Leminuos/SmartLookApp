package com.example.template;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private TextView textView1, textView2, tvSubmitMsg;
    private CardView btnForgot;
    private TextInputLayout tilForgotPasswordEmail;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initUi();
        initListener();
    }

    private void initUi() {
        edtEmail = findViewById(R.id.forgotPasswordEmail);
        btnForgot = findViewById(R.id.btn_forgot);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        tvSubmitMsg = findViewById(R.id.tvSubmitMsg);
        tilForgotPasswordEmail = findViewById(R.id.tilForgotPasswordEmail);
        progressDialog = new ProgressDialog(this);
    }

    private void initListener() {
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String strEmail = edtEmail.getText().toString().trim();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if(validateEmail(strEmail)) {
            progressDialog.show();
            auth.sendPasswordResetEmail(strEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                tilForgotPasswordEmail.setVisibility(View.GONE);
                                btnForgot.setVisibility(View.GONE);
                                textView1.setVisibility(View.GONE);
                                textView2.setVisibility(View.GONE);
                                tvSubmitMsg.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this, "Reset password failed, try again latter.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilForgotPasswordEmail.setError("Enter valid email address");
            return false;
        }
        else {
            tilForgotPasswordEmail.setError(null);
            return true;
        }
    }
}