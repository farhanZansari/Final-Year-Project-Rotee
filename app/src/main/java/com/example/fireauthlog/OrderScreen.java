package com.example.fireauthlog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

// Order Screen For Buyer And Seller
public class OrderScreen extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    TextView orderName, orderPerPlatePrice, orderTotalPrice, oredrBuyerAddress;
    private GoogleMap mMap;

    int buyer = 0, saler = 0;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    Button orderScreenBtn;

    String getid, getlatitude, getlongitude, authid, getplatesize, getsellerstate, checksalerauthid;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String currentid;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_screen);
        setTitle("Current Order Detail");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maporder);
        mapFragment.getMapAsync(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentid = firebaseAuth.getCurrentUser().getUid();
        intent = getIntent();
        orderScreenBtn = findViewById(R.id.orderScreenbtn);
        getsellerstate = intent.getStringExtra("state");
        orderName = findViewById(R.id.orderScreenName);
        orderPerPlatePrice = findViewById(R.id.orderPerPrice);
        orderTotalPrice = findViewById(R.id.totalPrice);
        getid = intent.getStringExtra("SellerId");
        checksalerauthid = intent.getStringExtra("SellId");

        getlatitude = intent.getStringExtra("latitude");
        getlongitude = intent.getStringExtra("longitude");
        getplatesize = intent.getStringExtra("platesize");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        showdetailstoseller();
        showdetailstobuyer();
        if (orderScreenBtn.getText().toString().equals("Buyer")) {
            orderScreenBtn.setVisibility(View.GONE);
        }

        orderScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (orderScreenBtn.getText().toString().equals("Seller")) {
                    if (checksalerauthid != null) {
                        databaseReference.child("DetailsForSeller").child(checksalerauthid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String id = dataSnapshot.child("Buyerid").getValue().toString();
                                    databaseReference.child("BuyerOrderDetails").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                databaseReference.child("DetailsForSeller").child(checksalerauthid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

            }
        });

    }

    private void showdetailstobuyer() {

        if (checksalerauthid != null) {
            databaseReference.child("BuyerOrderDetails").child(checksalerauthid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {

                        Toast.makeText(getApplicationContext(), "No Orders", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), NavigationDrawActivity.class));
                        finish();

                    } else {
                        String buyerid = dataSnapshot.child("Buyerid").getValue().toString();
                        if (checksalerauthid.equals(buyerid)) {
                            final String mealname = dataSnapshot.child("MealName").getValue().toString();
                            String price = dataSnapshot.child("price").getValue().toString();
                            String totalpric = dataSnapshot.child("TotalPrice").getValue().toString();
                            String latitude = dataSnapshot.child("buyerlatitude").getValue().toString();
                            String longitude = dataSnapshot.child("buyerlongitude").getValue().toString();

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).title("Buyer location");
                            mMap.addMarker(markerOptions);
                            // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            orderName.setText(mealname);
                            orderPerPlatePrice.setText(price);
                            orderTotalPrice.setText(totalpric);

                            orderScreenBtn.setText("Buyer");
                            orderScreenBtn.setVisibility(View.INVISIBLE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }

    private void showdetailstoseller() {

        if (checksalerauthid != null) {
            databaseReference.child("DetailsForSeller").child(checksalerauthid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {

                        Toast.makeText(getApplicationContext(), "No orders Yet", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), NavigationDrawActivity.class));
                        finish();
                    } else {
                        String sellerid = dataSnapshot.child("sellerid").getValue().toString();
                        if (checksalerauthid.equals(sellerid)) {
                            final String mealname = dataSnapshot.child("MealName").getValue().toString();
                            String price = dataSnapshot.child("price").getValue().toString();
                            String totalpric = dataSnapshot.child("TotalPrice").getValue().toString();
                            String latitude = dataSnapshot.child("buyerlatitude").getValue().toString();
                            String longitude = dataSnapshot.child("buyerlongitude").getValue().toString();

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).title("Buyer location");
                            mMap.addMarker(markerOptions);
                            // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(21));
                            orderName.setText(mealname);
                            orderPerPlatePrice.setText(price);
                            orderTotalPrice.setText(totalpric);

                            orderScreenBtn.setText("Seller");
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }

    public void setTextOrderScreen() {

        if (getid != null) {
            databaseReference.child(getid).child("Post").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String mealname = dataSnapshot.child("MealName").getValue().toString();
                    String price = dataSnapshot.child("price").getValue().toString();
                    int totalprice = Integer.parseInt(price) * Integer.parseInt(getplatesize);
                    orderName.setText(mealname);
                    orderPerPlatePrice.setText(price);
                    orderTotalPrice.setText(totalprice + "");
                    final HashMap hashMap = new HashMap();
                    hashMap.put("MealName", mealname);
                    hashMap.put("price", price);
                    hashMap.put("TotalPrice", String.valueOf(totalprice));
                    hashMap.put("sellerid", getid);
                    hashMap.put("Buyerid", currentid);
                    Toast.makeText(getApplicationContext(), String.valueOf(lastLocation.getLongitude()) + "longitude", Toast.LENGTH_LONG).show();
                    //      hashMap.put("buyerlatitude",lastLocation.getLatitude());
                    //    hashMap.put("buyerlongitude",lastLocation.getLongitude());
                    databaseReference.child("BuyerOrderDetails").child(currentid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                               // Toast.makeText(getApplicationContext(), "dataentered", Toast.LENGTH_LONG).show();


                                databaseReference.child("DetailsForSeller").child(getid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                          //  Toast.makeText(getApplicationContext(), "dataentered", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            getmylocation();

        }
    }

    private void getmylocation() {
        if (getid != null) {
            databaseReference.child(getid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String latitude = dataSnapshot.child("latitide").getValue().toString();
                    String longitude = dataSnapshot.child("longitude").getValue().toString();
                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng).title("Seller location");
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(21));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied... ", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        currentUserLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));


        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
        if (getid != null) {
            databaseReference.child(getid).child("Post").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String mealname = dataSnapshot.child("MealName").getValue().toString();
                    String price = dataSnapshot.child("price").getValue().toString();
                    int totalprice = Integer.parseInt(price) * Integer.parseInt(getplatesize);
                    orderName.setText(mealname);
                    orderPerPlatePrice.setText(price);
                    orderTotalPrice.setText(totalprice + "");
                    final HashMap hashMap = new HashMap();
                    hashMap.put("MealName", mealname);
                    hashMap.put("price", price);
                    hashMap.put("TotalPrice", String.valueOf(totalprice));
                    hashMap.put("sellerid", getid);
                    hashMap.put("Buyerid", currentid);
                    //Toast.makeText(getApplicationContext(),String.valueOf(lastLocation.getLongitude())+"longitude",Toast.LENGTH_LONG).show();
                    hashMap.put("buyerlatitude", lastLocation.getLatitude());
                    hashMap.put("buyerlongitude", lastLocation.getLongitude());
                    databaseReference.child("BuyerOrderDetails").child(currentid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "dataentered", Toast.LENGTH_LONG).show();


                                databaseReference.child("DetailsForSeller").child(getid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "dataentered", Toast.LENGTH_LONG).show();


                                        }
                                    }
                                });
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

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
}
