package com.example.neurochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdpter adapter;
    FirebaseDatabase database;
    ArrayList<user> usersArrayList;


    ImageView imglogout, cumbut, setbut;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase and UI elements
        auth = FirebaseAuth.getInstance();  // FirebaseAuth initialization
        database = FirebaseDatabase.getInstance();
        usersArrayList = new ArrayList<>();

        // Initialize RecyclerView
        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);

        if (mainUserRecyclerView == null) {
            Log.e("MainActivity", "RecyclerView not found in layout. Check the XML.");
            Toast.makeText(this, "RecyclerView not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));  // Correct setup for RecyclerView
        adapter = new UserAdpter(this, usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);

        loadUsers();

        imglogout = findViewById(R.id.logoutimg);
        cumbut = findViewById(R.id.camBut);
        setbut = findViewById(R.id.settingBut);

        imglogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, login.class));
            finish();
        });

        cumbut.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 10);  // Request code for camera
        });

        setbut.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, setting.class))
        );

        // Check if user is logged in
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, login.class));
            finish();  // Ensure to finish this activity to prevent navigating back
        }
    }

    // Load users from Firebase Realtime Database
    private void loadUsers() {
        database.getReference().child("user").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing list before adding new users
                usersArrayList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        // Map data to user object
                        user userObj = child.getValue(user.class);
                        if (userObj != null) {
                            usersArrayList.add(userObj);  // Add user to the list
                        }
                    }
                    adapter.notifyDataSetChanged();  // Notify adapter that data has changed
                } else {
                    Toast.makeText(MainActivity.this, "No users found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
