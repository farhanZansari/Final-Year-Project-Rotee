package com.example.fireauthlog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

// User Can Order Food With Help Of This class
public class UserPostsDetails extends AppCompatActivity {

    DatabaseReference databaseReference, dbhistory, databaseReferenceusers;
    Intent intent;
    String getid, salername, saleraddress;
    TextView uPostName, uPostDiscription, uPostPrice, uPostDelivery, uPostAddress;
    CircleImageView uPostImage;
    Spinner uPostSpinner;
    int platesmenu;
    Button uPostOrderNow;
    String getplateposition;
    String address = "";
    ArrayList<ModelPost> postArrayList;

    ArrayAdapter arrayAdapter;
    int totalplates;
    LinearLayout linearLayout;
    FirebaseAuth firebaseAuth;
    BroadcastReceiver broadcastReceiver;
    int updateplates;
    String id;
    String buyeradrres = "";
    Boolean isalive;
    ArrayList<String> arrayList;
    ArrayList<String> forbuyertable;
    ArrayAdapter aa;
    private NotificationManagerCompat notificationManager;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts_details);
        setTitle("Rotee | Meal");


        firebaseAuth = FirebaseAuth.getInstance();
        init();
//       fun(1000);
        //checkpostexists();


        listner();
        //ho gea h
        // wait

        loaddata();
        // getbuyerlatitude();
        //  loaddatausingchildlistner();

//        checkcurrentplates();


        // checkupdateplates();
//    updatedetailspost();


    }


    private void checkmyplates() {

         Toast.makeText(getApplicationContext(),"plate size is"+getplateposition,Toast.LENGTH_LONG).show();
        //String platespos=forbuyertable.get(1);

        int getpos = uPostSpinner.getSelectedItemPosition();
        final int platesize = Integer.parseInt(forbuyertable.get(1)) - Integer.parseInt(getplateposition);
        aa.remove(arrayList.get(Integer.parseInt(getplateposition)));
        HashMap hashMap = new HashMap();
        hashMap.put("Plates", platesize);
        databaseReference.child(getid).child("Post").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                setplates(String.valueOf(platesize));
                forbuyertable.set(1, String.valueOf(platesize));

                Toast.makeText(getApplicationContext(), "plate size is" + platesize + "db plates" + forbuyertable.get(1), Toast.LENGTH_LONG).show();//platesize=0;


            }
        });
    }

    public void checkcurrentplates() {
        databaseReference.child(getid).child("Post").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //    uPostPrice.setText("found");

                    String plates = dataSnapshot.child("Plates").getValue().toString();
                    Toast.makeText(getApplicationContext(), "Current Plates are" + forbuyertable.get(1).toString(), Toast.LENGTH_LONG).show();

/*                    if(Integer.parseInt(plates)<updateplates)
                    {
                        loaddata();
                    }
                    else {

                        Toast.makeText(getApplicationContext(),"Size same",Toast.LENGTH_LONG).show();
                    }*/
                } else {
                    uPostPrice.setText("not found");
                    Toast.makeText(getApplicationContext(), "not found", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updatedetailspost() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                checkpostexist();
//                loaddata();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void checkpostexist() {
        databaseReference.child(getid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Post").exists()) {

                    startActivity(new Intent(getApplicationContext(), NavigationDrawActivity.class));
                    Toast.makeText(getApplicationContext(), "This Order Delivered To Other Customer", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        //      loaddata();


    }

    @Override
    protected void onStart() {
        super.onStart();
//        loaddata();


        updatedetailspost();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    private void listner() {

        uPostOrderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // startActivity(new Intent(getApplicationContext(),OrderScreen.class));


                // checkmyplates();
                buyer();
                //  deletemypost();
            }
        });

    }


    private void init() {

        postArrayList = new ArrayList<>();
        id = firebaseAuth.getCurrentUser().getUid();

        progressDialog = new ProgressDialog(this);
        notificationManager = NotificationManagerCompat.from(this);

        //linearLayout = findViewById(R.id.linearLayID);
        arrayList = new ArrayList<>();
        uPostImage = findViewById(R.id.order_view);
        uPostSpinner = findViewById(R.id.order_quantity);
        //uPostSpinner.setOnItemSelectedListener(this);
        uPostName = findViewById(R.id.order_name);
        uPostDiscription = findViewById(R.id.order_detail_food);
        uPostAddress = findViewById(R.id.order_address);
        uPostPrice = findViewById(R.id.order_price);
        uPostDelivery = findViewById(R.id.order_delivery);

        forbuyertable = new ArrayList<>();
        //uPostChat = findViewById(R.id.order_chat);
        uPostOrderNow = findViewById(R.id.order_now);
        intent = getIntent();
        saleraddress = intent.getStringExtra("saleraddress");
        salername = intent.getStringExtra("salername");
        //  Toast.makeText(getApplicationContext(),salername+"  "+saleraddress,Toast.LENGTH_LONG).show();
        getid = intent.getStringExtra("postid");
//        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(getid).child("Post");

        // aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        dbhistory = FirebaseDatabase.getInstance().getReference().child("Users").child(getid).child("History");
//        checkpostdata();


    }

    private void checkpostdata() {
        databaseReference.child(getid).child("Post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        ModelPost modelPost = dataSnapshot1.getValue(ModelPost.class);
                        Toast.makeText(getApplicationContext(), "post description is " + modelPost.mealdescription, Toast.LENGTH_LONG).show();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loaddata() {

        databaseReference.child(getid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.child("Post").exists()) {
                    // if (dataSnapshot.exists())
                    //   {

                   // Toast.makeText(getApplicationContext(), "Post  Exist", Toast.LENGTH_LONG).show();

                    databaseReference.child(getid).child("Post").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {


//                                progressDialog.dismiss();
                                String address = dataSnapshot.child("Address").getValue().toString();
                                final String plates = dataSnapshot.child("Plates").getValue().toString();
                                platesmenu = Integer.parseInt(plates);
                                String mealdescription = dataSnapshot.child("mealdescription").getValue().toString();
                                String price = dataSnapshot.child("price").getValue().toString();
                                String availability = dataSnapshot.child("Availability").getValue().toString();
                                String picture = dataSnapshot.child("picture").getValue().toString();
                                String mealname = dataSnapshot.child("MealName").getValue().toString();
                                forbuyertable.add(address);
                                forbuyertable.add(plates);
                                forbuyertable.add(mealdescription);
                                forbuyertable.add(price);
                                forbuyertable.add(mealname);
                                forbuyertable.add(picture);
                                uPostSpinner.invalidate();
                                if (!picture.equals("empty")) {

                                    byte[] imageBytes = android.util.Base64.decode(picture, android.util.Base64.DEFAULT);

                                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    uPostImage.setImageBitmap(decodedImage);


                                } else {
                                    uPostImage.setImageResource(R.drawable.add_btn);

                                }
                                setplates(plates);
                                uPostAddress.setText(address);


                                uPostPrice.setText(price);
                                uPostName.setText(mealname);
                                uPostDiscription.setText(mealdescription);
                                if (availability.equals("yes")) {
                                    uPostDelivery.setText("Available");
                                } else {
                                    uPostDelivery.setText("Not Available");
                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    //     }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void setplates(String totalplates) {
        int myplates = Integer.parseInt(totalplates);
        arrayList.clear();
        for (int i = 1; i <= myplates; i++) {
            arrayList.add(String.valueOf(i));
        }
        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList);
        uPostSpinner.setAdapter(aa);
        uPostSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                getplateposition = arrayList.get(position);
                //  Toast.makeText(getApplicationContext(), "spinner is" + getplateposition, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getbuyerlatitude() {
        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String latitude = dataSnapshot.child("latitide").getValue().toString();
                    String longitude = dataSnapshot.child("longitude").getValue().toString();
//                String address=getcompleteaddress(Double.parseDouble(latitude),Double.parseDouble(longitude));

                    buyeradrres = getcompleteaddress(Double.parseDouble(latitude), Double.parseDouble(longitude));

                    // 6 index ha
                    Toast.makeText(getApplicationContext(), "address is" + address, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void buyer() {

//        progressDialog.show();
        final String userid = firebaseAuth.getCurrentUser().getUid();
        final HashMap hashMap = new HashMap();
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calobj = Calendar.getInstance();

        hashMap.put("SalerAddres", forbuyertable.get(0).toString());
        hashMap.put("SalerName", salername);
        hashMap.put("Mealdescription", forbuyertable.get(2).toString());
        hashMap.put("MealName", forbuyertable.get(4).toString());
        hashMap.put("DateTime", df.format(calobj.getTime()));
        hashMap.put("plates", getplateposition);

        hashMap.put("price", forbuyertable.get(3));
        hashMap.put("picture", forbuyertable.get(5));


        databaseReference.child(userid).child("History").child("Buyer").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    databaseReference.child(userid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                            //final String longitude=dataSnapshot.child("longitude").getValue().toString();
                            String name = dataSnapshot.child("firstName").getValue().toString();
                            //   String myaddress= getcompleteaddress(Double.parseDouble(latitude),Double.parseDouble(longitude));
                            final HashMap hashMap = new HashMap();
                            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                            Calendar calobj = Calendar.getInstance();

                            hashMap.put("BuyerName", name);
                            hashMap.put("Mealdescription", forbuyertable.get(2).toString());
                            hashMap.put("MealName", forbuyertable.get(4).toString());
                            hashMap.put("DateTime", df.format(calobj.getTime()));
                            hashMap.put("plates", getplateposition);
                            hashMap.put("price", forbuyertable.get(3));
                            hashMap.put("picture", forbuyertable.get(5));


                            final int platesize = Integer.parseInt(forbuyertable.get(1)) - Integer.parseInt(getplateposition);
                            if (platesize == 0) {

                                databaseReference.child(id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String latitude = dataSnapshot.child("latitide").getValue().toString();
                                        String longitude = dataSnapshot.child("longitude").getValue().toString();
//                String address=getcompleteaddress(Double.parseDouble(latitude),Double.parseDouble(longitude));

                                        String buyeradrres = getcompleteaddress(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                        hashMap.put("BuyerAddres", buyeradrres);

                                        databaseReference.child(getid).child("History").child("Seller").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    databaseReference.child(getid).child("Post").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            Intent intent = new Intent(getApplicationContext(), OrderScreen.class);
                                                            intent.putExtra("SellerId", getid);
                                                            intent.putExtra("platesize", forbuyertable.get(1));
                                                            startActivity(intent);
                                                            finish();// me ek select k or next screen pr total price me do ka total aya
                                                            sentnotification(getid);

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


                            } else {
                                databaseReference.child(id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String latitude = dataSnapshot.child("latitide").getValue().toString();
                                        String longitude = dataSnapshot.child("longitude").getValue().toString();
//                String address=getcompleteaddress(Double.parseDouble(latitude),Double.parseDouble(longitude));

                                        String buyeradrres = getcompleteaddress(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                        hashMap.put("BuyerAddres", buyeradrres);
                                        aa.remove(arrayList.get(Integer.parseInt(getplateposition)));
                                        final HashMap hashMap1 = new HashMap();
                                        hashMap1.put("Plates", platesize);
                                        databaseReference.child(getid).child("History").child("Seller").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    databaseReference.child(getid).child("Post").updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {


                                                            setplates(String.valueOf(platesize));
                                                            forbuyertable.set(1, String.valueOf(platesize));
                                                            Intent intent = new Intent(getApplicationContext(), OrderScreen.class);
                                                            intent.putExtra("SellerId", getid);
                                                            intent.putExtra("platesize", String.valueOf(platesize));
                                                            startActivity(intent);
                                                            finish();

                                                            sentnotification(getid);
                                                            //     Toast.makeText(getApplicationContext(),"plate size is"+platesize+"db plates"+forbuyertable.get(1),Toast.LENGTH_LONG).show();//platesize=0;
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


                            //  databaseReference.child(getid).child("History").child("Seller").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                              /*  @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        int platesize = Integer.parseInt(forbuyertable.get(1));
                                        int plateposition = Integer.parseInt(getplateposition);


                                       updateplates = platesize - plateposition;


                                     //   Toast.makeText(getApplicationContext(), "plate size" + platesize, Toast.LENGTH_LONG).show();

                                        final HashMap hashMap2 = new HashMap();
                                        hashMap2.put("Plates", updateplates);

                                        if (updateplates==0)
                                        {
                                            databaseReference.child(getid).child("Post").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful())
                                                    {

                                                        uPostSpinner.invalidate();
                                                        Intent intent=new Intent(getApplicationContext(),OrderScreen.class);
                                                        intent.putExtra("SellerId",getid);
                                                        intent.putExtra("platesize",String.valueOf(updateplates));
                                                        startActivity(intent);
                                                        finish();
                                                        //   sentnotification(getid);

                                                    }
                                                }
                                            });
                                            //Toast.makeText(getApplicationContext(),"size 0",Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            //    Toast.makeText(getApplicationContext(),updateplates+"",Toast.LENGTH_LONG).show();
                                            databaseReference.child(getid).child("Post").updateChildren(hashMap2).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        uPostSpinner.invalidate();

                                                        Intent intent=new Intent(getApplicationContext(),OrderScreen.class);
                                                        intent.putExtra("SellerId",getid);
                                                        intent.putExtra("platesize",String.valueOf(updateplates));
                                                        startActivity(intent);
                                                        finish();

                                                        // sentnotification(getid);
//                                                            progressDialog.dismiss();

                                                    }
                                                }
                                            });



                                        }

                                    }
                                }
                            });
                            */

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    // startActivity(new Intent(getApplicationContext(),Post.class));

                }
            }
        });
    }

    public String getcompleteaddress(Double latitude, Double longitude) {
        Geocoder geocoder = new Geocoder(UserPostsDetails.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null) {
                Address address1 = addressList.get(0);
                StringBuilder stringBuilder = new StringBuilder("");
                for (int i = 0; i <= address1.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(address1.getAddressLine(i)).append("\n");
                }
                address = stringBuilder.toString();
                //           mealAddress.append(address);
            }
            // return address;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public void sentnotification(String sellerid) {
        databaseReference.child(sellerid).child("Post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Device")) {
                    String devicetoken = dataSnapshot.child("Device").getValue().toString();
                    if (devicetoken != null) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("DeviceToken", devicetoken);
                        databaseReference.child("Notifications").child(id).child(getid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Notification sent", Toast.LENGTH_LONG).show();
                                }
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

