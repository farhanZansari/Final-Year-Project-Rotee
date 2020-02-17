package com.example.fireauthlog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// Sold Meal Activity Where All the History Will Shown that food was sold
public class Sold extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ArrayList<BuyerSalerModel> arrayList;
    FirebaseAuth firebaseAuth;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold);
        setTitle("Sold | History");
        init();
        loadhistory();
    }

    private void loadhistory() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        BuyerSalerModel user = dataSnapshot1.getValue(BuyerSalerModel.class);
                        arrayList.add(user);
                    }
                    recyclerView.setAdapter(new SoldAdapter(arrayList, getApplicationContext()));
                } else {
                    Toast.makeText(getApplicationContext(), "No history", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(getApplicationContext(), NavigationDrawActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void init() {

        firebaseAuth = FirebaseAuth.getInstance();
        id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("History").child("Seller");

        arrayList = new ArrayList<>();

        recyclerView = findViewById(R.id.salerhistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);


    }
}
