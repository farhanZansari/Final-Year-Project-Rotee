package com.example.fireauthlog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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
// Purchased History Class
public class History extends AppCompatActivity {


    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String currentid;
    ArrayList<BuyerSalerModel> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("Purchased | History");
        init();
        loaddata();


    }

    private void loaddata() {
        databaseReference.child(currentid).child("History").child("Buyer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        BuyerSalerModel user = dataSnapshot1.getValue(BuyerSalerModel.class);

                        arrayList.add(user);
                    }
                    recyclerView.setAdapter(new MyAdapter(arrayList, getApplicationContext()));
                } else {
                    Toast.makeText(getApplicationContext(), "No History Found", Toast.LENGTH_LONG).show();

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
        arrayList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        currentid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


    }
}
