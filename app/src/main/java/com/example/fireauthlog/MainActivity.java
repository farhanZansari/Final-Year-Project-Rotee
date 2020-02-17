package com.example.fireauthlog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
//Signup Screen For New registration of user
public class MainActivity extends AppCompatActivity {

    public EditText emailId, password, fName, lName;
    Button btnSignUp;
    TextView tvsignin;
    FirebaseAuth mFirebaseAuth;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Rotee | Sign Up");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        fName = findViewById(R.id.fname);
        lName = findViewById(R.id.lname);
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        tvsignin = findViewById(R.id.textView);
        btnSignUp = findViewById(R.id.button);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fname = fName.getText().toString();
                final String lname = lName.getText().toString();
                final String email = emailId.getText().toString();
                final String pass = password.getText().toString();
                if (fname.isEmpty()) {
                    fName.setError("Please Enter First Name!");
                    fName.requestFocus();
                } else if (lname.isEmpty()) {
                    lName.setError("Please Enter Last Name!");
                    lName.requestFocus();
                } else if (email.isEmpty()) {
                    emailId.setError("Please Enter Email!");
                    emailId.requestFocus();
                } else if (pass.isEmpty()) {
                    password.setError("Enter Password!");
                    password.requestFocus();
                } else if (email.isEmpty() && pass.isEmpty() && fname.isEmpty() && lname.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pass.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("firstName", fname);
                                hashMap.put("lastName", lname);
                                hashMap.put("email", email);
                                hashMap.put("password", pass);
                                //                          hashMap.put("latitute",lastLocation.getLatitude());
                                //                        hashMap.put("longitude",lastLocation.getLongitude());
                                hashMap.put("Id", mFirebaseAuth.getCurrentUser().getUid());

                                mDatabase.child(mFirebaseAuth.getUid()).setValue(hashMap);
                                //                              GeoFire geoFire=new GeoFire(mDatabase);
//                                geoFire.setLocation(mFirebaseAuth.getUid(),new GeoLocation(lastLocation.getLatitude(),lastLocation.getLongitude()));

//                                writeNewUser(fname,lname,email,pass);
                                startActivity(new Intent(MainActivity.this, NavigationDrawActivity.class));
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Error in Sign up", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Toast.makeText(MainActivity.this, "YOU ARE LOGGED IN", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, NavigationDrawActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                }

            }
        };
    }

    private void writeNewUser(String fname, String lname, String email, String password, Double latitude, Double longitute) {
        String uID = mFirebaseAuth.getCurrentUser().getUid();
        User user = new User(fname, lname, email, password);

        mDatabase.child("users").child(uID).setValue(user);
    }

}
