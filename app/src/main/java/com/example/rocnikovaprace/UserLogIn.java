package com.example.rocnikovaprace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rocnikovaprace.ui.Nastaveni.Nastaveni;
import com.example.rocnikovaprace.ui.SpravujSlovicka.VytvoreniHesla;
import com.example.rocnikovaprace.ui.Zacni.Zacni;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserLogIn extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private TextView banner, signInUser, zapomenuteHeslo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log_in);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(com.example.rocnikovaprace.R.id.DomluvSe);
        banner.setOnClickListener(this);

        signInUser = (Button) findViewById(com.example.rocnikovaprace.R.id.signInUser);
        signInUser.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(com.example.rocnikovaprace.R.id.email);
        editTextPassword = (EditText) findViewById(com.example.rocnikovaprace.R.id.password);

        zapomenuteHeslo = (TextView) findViewById(com.example.rocnikovaprace.R.id.zapomenuteHeslo);
        zapomenuteHeslo.setOnClickListener(this);


        progressBar = (ProgressBar) findViewById(com.example.rocnikovaprace.R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case com.example.rocnikovaprace.R.id.DomluvSe:
                startActivity(new Intent(this, VytvoreniHesla.class));
                break;
            case com.example.rocnikovaprace.R.id.signInUser:
                signInUser();
                break;
            case R.id.zapomenuteHeslo:
                resetPassword();


        }

    }

    private void resetPassword(){
        String email = editTextEmail.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UserLogIn.this, "Přejděte do své emailové schránky a postupujte podle pokynů v e-mailu.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }else{
                    Toast.makeText(UserLogIn.this, "Zaslání emailu selhalo. Zkontrolujte email a zkuste to znovu", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void signInUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }


        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // Přihlásí uživatele pomocí Firebase
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UserLogIn.this, "Přihlášení bylo úspěšné", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(UserLogIn.this, MainActivity.class));

                }else{
                    Toast.makeText(UserLogIn.this, "Přihlášení selhalo, zkontrolujte své údaje", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);

            }
        });

    }
}