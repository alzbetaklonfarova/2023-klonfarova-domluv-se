package com.example.rocnikovaprace.ui.Zacni;

import static android.os.Build.VERSION_CODES.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.ui.Nastaveni.Nastaveni;
import com.example.rocnikovaprace.ui.SpravujSlovicka.VytvoreniHesla;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private TextView banner, signInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.rocnikovaprace.R.layout.activity_sign_in_user);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(com.example.rocnikovaprace.R.id.DomluvSe);
        banner.setOnClickListener(this);

        signInUser = (Button) findViewById(com.example.rocnikovaprace.R.id.signInUser);
        signInUser.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(com.example.rocnikovaprace.R.id.email);
        editTextPassword = (EditText) findViewById(com.example.rocnikovaprace.R.id.password);


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


        }

    }

    private void signInUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();




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
                    startActivity(new Intent(SignInUser.this, Nastaveni.class));

                }else{
                    Toast.makeText(SignInUser.this, "Registrace selhala, zkontrolujte své údaje", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

}