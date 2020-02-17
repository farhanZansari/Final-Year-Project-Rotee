package com.example.fireauthlog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Login Screen For Authentication
public class LoginActivity extends AppCompatActivity {

    public EditText emailId, password;
    Button btnSignIn;
    TextView tvsignUp;
    FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Rotee | Login");
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        tvsignUp = findViewById(R.id.textView);
        btnSignIn = findViewById(R.id.button);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Toast.makeText(LoginActivity.this, "YOU ARE LOGGED IN", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, NavigationDrawActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pass = password.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Please Enter Email");

                } else if (pass.isEmpty()) {
                    password.setError("Enter Password");
                    password.requestFocus();
                } else if (email.isEmpty() && pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pass.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent inToHome = new Intent(LoginActivity.this, NavigationDrawActivity.class);
                                startActivity(inToHome);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Error Please Login Again.", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intSignUp);
                finish();
            }
        });


    }


    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
