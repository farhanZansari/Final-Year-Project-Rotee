package com.example.fireauthlog;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

// class for editing in current post
public class Post extends AppCompatActivity {

    TextView editPostSpinner, editPostName, editPostDiscription, editPostPrice, editPostDelivery, editPostAddress;
    CircleImageView editPostImage;
    RelativeLayout relativeLayout;
    Button editPost, deletePost;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String currentid;
    byte[] imageBytes;
    Bitmap decodedImage;
    String currentstate;
    private final static int PERMISSION_CODE = 100;
    String getPicture;

    Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setTitle("Recent Post");

        firebaseAuth = FirebaseAuth.getInstance();
        currentid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentid);
        relativeLayout = findViewById(R.id.recentpostvisiblity);
        editPostImage = findViewById(R.id.edit_view);
        editPostName = findViewById(R.id.edit_name);
        editPostDiscription = findViewById(R.id.edit_detail_food);
        editPostAddress = findViewById(R.id.edit_address);
        editPostPrice = findViewById(R.id.edit_price);
        editPostDelivery = findViewById(R.id.edit_delivery_status);
        editPostSpinner = findViewById(R.id.edit_quantity);
        deletePost = findViewById(R.id.delete);
        init();
        listner();
    }

    private void listner() {
        editPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryCamera();
            }
        });
        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Post").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Post Deleted Successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), NavigationDrawActivity.class);
                            startActivity(intent);
                            // Toast.makeText(getContext(),"Post Updated Successfully",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


    }

    public void openGalleryCamera() {
        CharSequence option[] = new CharSequence[]{"Camera", "Gallery"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(Post.this);
        builder.setTitle("Select A Option");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                            openCamera();
                        } else {
                            ActivityCompat.requestPermissions(Post.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
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

    private void openCamera() {
        currentstate = "camera";
        Intent Cam_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(Cam_intent, 1000);

    }

    public String getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        String encodedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentstate.equals("camera")) {
            if (resultCode == RESULT_OK) {
                imageuri = data.getData();
                final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                editPostImage.setImageBitmap(bitmap);
                // cimage.setVisibility(View.VISIBLE);
                getPicture = getBytesFromBitmap(bitmap);

            }
        } else if (currentstate.equals("gallery")) {
            if (resultCode == RESULT_OK && requestCode == PERMISSION_CODE && data != null && data.getData() != null) {
                imageuri = data.getData();

                try {
                    final Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                    // imgPost.setVisibility(View.VISIBLE);
                    editPostImage.setImageBitmap(bitmap1);
                    getPicture = getBytesFromBitmap(bitmap1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void updatepost() {

        final HashMap hashMap = new HashMap();
        hashMap.put("MealName", editPostName.getText().toString());
        hashMap.put("Address", editPostAddress.getText().toString());
        hashMap.put("price", editPostPrice.getText().toString());
        hashMap.put("mealdescription", editPostDiscription.getText().toString());
        hashMap.put("Availability", editPostDelivery.getText().toString());
        hashMap.put("Plates", editPostSpinner.getText().toString());
        //   hashMap.put("Status","available");

        if (getPicture != null) {
            hashMap.put("picture", getPicture);

        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Post").exists()) {
                    databaseReference.child("Post").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Post Updated  ", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), NavigationDrawActivity.class);
                                startActivity(intent);

                            }
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Post Not Exist ", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void init() {


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Post").exists()) {
                    databaseReference.child("Post").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String address = dataSnapshot.child("Address").getValue().toString();
                                String plates = dataSnapshot.child("Plates").getValue().toString();
                                String mealdescription = dataSnapshot.child("mealdescription").getValue().toString();
                                String price = dataSnapshot.child("price").getValue().toString();
                                String availability = dataSnapshot.child("Availability").getValue().toString();
                                String picture = dataSnapshot.child("picture").getValue().toString();
                                String mealname = dataSnapshot.child("MealName").getValue().toString();

                                //  Toast.makeText(getContext(),address+plates,Toast.LENGTH_LONG).show();
                                if (!picture.equals("empty")) {
                                    imageBytes = android.util.Base64.decode(picture, android.util.Base64.DEFAULT);
                                    decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    editPostImage.setImageBitmap(decodedImage);
                                }
                                editPostName.setText(mealname);
                                editPostDiscription.setText(mealdescription);
                                editPostAddress.append(address);
                                editPostPrice.setText(price);
                                editPostDelivery.setText(availability);
                                editPostSpinner.setText(plates);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {

                    Toast.makeText(getApplicationContext(), "Post Not Exist Please Create Post", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), PostDetails.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
