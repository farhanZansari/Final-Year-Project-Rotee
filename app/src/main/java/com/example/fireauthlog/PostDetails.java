package com.example.fireauthlog;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
// for posting your food Post Activity
public class PostDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Spinner spinner;

    ArrayList<String> persons;
    DatabaseReference postdetails;
    FirebaseAuth firebaseAuth;
    String currentId;
    String pos;
    String currentstate;
    RadioButton r1, r2;
    String text, getPicture;

    EditText mealName, mealAddress, mealPrice, mealDescription;
    Button post;
    ImageView imgPost;
    Uri imageuri;
    static int PERMISSION_CODE = 100;
    private static final int requestpercode = 100;
    private GoogleMap mMap;
    private Location lastlocation;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    Marker currentlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        setTitle("Post your Meal");


        persons = new ArrayList<>();
        persons.add("Plates Per Person");
        persons.add("1");
        persons.add("2");
        persons.add("3");
        persons.add("4");

        init();
        listners();
        spin();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkpermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.displayaddress);
        mapFragment.getMapAsync(this);


    }

    public String getcompleteaddress(Double latitude, Double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(PostDetails.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null) {
                Address address1 = addressList.get(0);
                StringBuilder stringBuilder = new StringBuilder("");
                for (int i = 0; i <= address1.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(address1.getAddressLine(i)).append("\n");
                }
                address = stringBuilder.toString();
                mealAddress.append(address);
            }
            // return address;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private synchronized void builgoogleapi() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    private boolean checkpermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestpercode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestpercode);

            }
            return false;

        } else {
            return true;
        }


    }

    private void storepost() {
        String devicetoken = FirebaseInstanceId.getInstance().getToken();
        HashMap hashMap = new HashMap();

        hashMap.put("MealName", mealName.getText().toString());
        hashMap.put("Address", mealAddress.getText().toString());
        hashMap.put("price", mealPrice.getText().toString());
        hashMap.put("mealdescription", mealDescription.getText().toString());
        hashMap.put("Availability", text);
        hashMap.put("Plates", pos);
        hashMap.put("picture", getPicture);
        hashMap.put("Status", "yes");
        hashMap.put("Device", devicetoken);

        postdetails.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent i = new Intent(PostDetails.this, NavigationDrawActivity.class);
                    startActivity(i);
                    finish();
                    // Toast.makeText(getApplicationContext(),"Posted Successfully",Toast.LENGTH_SHORT).show();
                } else {
                    //        Toast.makeText(getApplicationContext(),"Posted Failed",Toast.LENGTH_SHORT).show();


                }
            }
        });


    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        String id = firebaseAuth.getCurrentUser().getUid();
        postdetails = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("Post");
        mealName = findViewById(R.id.post_Meal_Name);
        mealAddress = findViewById(R.id.post_address);
        mealPrice = findViewById(R.id.post_price);
        mealDescription = findViewById(R.id.post_description);
        post = findViewById(R.id.meal_postbtn);
        imgPost = findViewById(R.id.postimage);
        r1 = findViewById(R.id.post_delivery_yes);
        r2 = findViewById(R.id.post_delivery_no);
    }

    private void listners() {
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = "yes";
                // Toast.makeText(getApplicationContext(),"yes",Toast.LENGTH_SHORT).show();
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = "No";
                Toast.makeText(getApplicationContext(), "no", Toast.LENGTH_SHORT).show();


            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mealName.getText().toString()) || TextUtils.isEmpty(mealAddress.getText().toString())
                        || TextUtils.isEmpty(mealPrice.getText().toString()) || TextUtils.isEmpty(mealDescription.getText().toString())) {


                    Toast.makeText(getApplicationContext(), "Please fill the required Fields", Toast.LENGTH_SHORT).show();
                } else if (pos.equals("Plates Per Person")) {
                    Toast.makeText(getApplicationContext(), "Please select minimum 1 Plate", Toast.LENGTH_SHORT).show();

                } else if (text.equals("")) {
                    Toast.makeText(getApplicationContext(), "check availability", Toast.LENGTH_SHORT).show();

                } else {
                    storepost();
                }

            }
        });
        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGalleryCamera();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (currentstate.equals("camera")) {
            if (resultCode == RESULT_OK) {
                imageuri = data.getData();
                final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imgPost.setImageBitmap(bitmap);
                // cimage.setVisibility(View.VISIBLE);
                getPicture = getBytesFromBitmap(bitmap);

            }
        } else if (currentstate.equals("gallery")) {
            if (resultCode == RESULT_OK && requestCode == PERMISSION_CODE && data != null && data.getData() != null) {
                imageuri = data.getData();

                try {
                    final Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                    // imgPost.setVisibility(View.VISIBLE);
                    imgPost.setImageBitmap(bitmap1);
                    getPicture = getBytesFromBitmap(bitmap1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        String encodedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        return encodedImage;
    }

    public void spin() {
        spinner = findViewById(R.id.post_spinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, persons);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        pos = persons.get(position);
        //     Toast.makeText(getApplicationContext(),pos,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void openCamera() {
        currentstate = "camera";
        Intent Cam_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(Cam_intent, 1000);
    }

    public void openGalleryCamera() {
        CharSequence option[] = new CharSequence[]{"Camera", "Gallery"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(PostDetails.this);
        builder.setTitle("Select A Option");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                            openCamera();
                        } else {
                            ActivityCompat.requestPermissions(PostDetails.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
                        }
                    } else {
                        openCamera();
                    }
                    builder.setCancelable(true);
                } else if (i == 1) {
                    currentstate = "gallery";
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PERMISSION_CODE);

                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lastlocation = location;
        if (currentlocation != null) {

            currentlocation.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        markerOptions.title("current address");

        markerOptions.title(getcompleteaddress(latLng.latitude, latLng.longitude));
        currentlocation = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            builgoogleapi();
            mMap.setMyLocationEnabled(true);

//            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case requestpercode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            builgoogleapi();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Denied", Toast.LENGTH_LONG).show();
                }

        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
        }


    }


}

