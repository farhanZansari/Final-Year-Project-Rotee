package com.example.fireauthlog;

import android.Manifest;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.util.ArrayList;
import java.util.HashMap;
// NavigationDrawer Activity Showing All The Options In Navigation Activity
public class NavigationDrawActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "";
    private TextView tvNavUname, tvNavUemail;

    SupportMapFragment mapFragment;
    int count = 0;

    String key = "";
    FrameLayout frameLayout;
    LinearLayout linearLayoutmaps;
    ArrayList<String> arrayList;
    FirebaseAuth mFirebaseAuth;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    DatabaseReference databaseReference, usersrefernce, databaseReference3;

    String currentauthid;
    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_draw);
        setTitle("Rotee | Home");


        linearLayoutmaps = findViewById(R.id.livisibilty);
        frameLayout = findViewById(R.id.framelayout);
        mFirebaseAuth = FirebaseAuth.getInstance();
        arrayList = new ArrayList<>();
        currentauthid = mFirebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        tvNavUname = (TextView) findViewById(R.id.usernameView);
        tvNavUemail = findViewById(R.id.emailView);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        floatingActionButton = findViewById(R.id.fab);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        tvNavUname = (TextView) headerView.findViewById(R.id.usernameView);
        tvNavUemail = (TextView) headerView.findViewById(R.id.emailView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        mapFragment.setMenuVisibility(true);

        foo();
        listner();

    }

    private void listner() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addpostactivity();

            }
        });


    }

    private void addpostactivity() {
        String id = mFirebaseAuth.getCurrentUser().getUid();
        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("Post")) {
                    Toast.makeText(getApplicationContext(), "Post Already Exist ", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(NavigationDrawActivity.this, PostDetails.class);
                    startActivity(i);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_order) {
            Intent i = new Intent(NavigationDrawActivity.this, OrderScreen.class);
            i.putExtra("SellId", currentauthid);
            i.putExtra("state", "seller");

            startActivity(i);
        } else if (id == R.id.nav_recent_post) {

            //loadfragment(new RecentPost());
            Intent i = new Intent(NavigationDrawActivity.this, Post.class);
            startActivity(i);

        } else if (id == R.id.nav_purchase) {
            Intent i = new Intent(NavigationDrawActivity.this, History.class);
            startActivity(i);


        } else if (id == R.id.nav_sell) {
            Intent i = new Intent(NavigationDrawActivity.this, Sold.class);
            startActivity(i);

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(NavigationDrawActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void foo() {

        String id = mFirebaseAuth.getCurrentUser().getUid();
        usersrefernce = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        //      DatabaseReference ref = database.child("Users").child(mFirebaseAuth.getUid());
        usersrefernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("firstName").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();


                    tvNavUname.setText(name);
                    tvNavUemail.setText(email);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        final String id = mFirebaseAuth.getCurrentUser().getUid();

        HashMap hashMap = new HashMap();
        hashMap.put("latitide", location.getLatitude());
        hashMap.put("longitude", location.getLongitude());
        databaseReference.child(id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    final String id = mFirebaseAuth.getCurrentUser().getUid();
                    databaseReference.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                final String latitide = dataSnapshot.child("latitide").getValue().toString();
                                String longitude = dataSnapshot.child("longitude").getValue().toString();
                                String userid = dataSnapshot.child("Id").getValue().toString();

                                Location location = new Location("");
                                location.setLatitude(Double.parseDouble(latitide));
                                location.setLongitude(Double.parseDouble(longitude));
                                checkusersmaps(userid, latitide, longitude);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
    }

    private void checkusersmaps(final String id, final String latitude, final String longitude) {


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        final User user = dataSnapshot1.getValue(User.class);

                        if (!id.equals(user.getId())) {
                            Location user1location = new Location("");
                            user1location.setLatitude(Double.parseDouble(latitude));
                            user1location.setLongitude(Double.parseDouble(longitude));
                            final Location user2location = new Location("");
                            if (user.getLatitude() != null && user.getLongitude() != null) {
                                user2location.setLatitude(user.getLatitude());
                                user2location.setLongitude(user.getLongitude());
                                float totaldistance = user1location.distanceTo(user2location);
                                if (totaldistance <= 1000.000 && dataSnapshot1.child("Post").exists()) {
                                    databaseReference.child(user.getId()).child("Post").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild("MealName")) {
                                                final String mealname = dataSnapshot.child("MealName").getValue().toString();
                                                if (mealname != null) {
                                                    MarkerOptions markerOptions1 = new MarkerOptions();
                                                    markerOptions1.position(new LatLng(user2location.getLatitude(), user2location.getLongitude())).title(mealname).icon(BitmapDescriptorFactory.fromResource(R.drawable.lunchicons));


                                                    mMap.addMarker(markerOptions1);
                                                    //(new MarkerOptions().position(new LatLng(user2location.getLatitude(),user2location.getLongitude())).title(mealname));
                                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                        @Override
                                                        public boolean onMarkerClick(Marker marker) {

                                                            Intent intent = new Intent(NavigationDrawActivity.this, UserPostsDetails.class);
                                                            intent.putExtra("postid", user.getId());
                                                            intent.putExtra("salername", user.firstName);
                                                            intent.putExtra("saleraddress", user.Address);
                                                            startActivity(intent);
                                                            return true;
                                                        }
                                                    });
                                                }


                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    protected synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

}
