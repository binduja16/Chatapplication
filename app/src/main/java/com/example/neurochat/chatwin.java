package com.example.neurochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatwin extends AppCompatActivity {
    private String reciverImg, reciverUid, reciverName, SenderUID;
    private CircleImageView profile;
    private TextView reciverNName;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    public static String senderImg;
    public static String reciverIImg;
    private CardView sendbtn;
    private EditText textmsg;

    private String senderRoom, reciverRoom;
    private RecyclerView messageAdpter;
    private ArrayList<msgModelclass> messagesArrayList;
    private messageAdpter mmessagesAdpter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Firebase components
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Get data passed from previous activity
        reciverName = getIntent().getStringExtra("nameeee");
        reciverImg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        // Initialize UI components
        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        reciverNName = findViewById(R.id.recivername);
        profile = findViewById(R.id.profileimgg);
        messageAdpter = findViewById(R.id.msgadpter);

        // Set up RecyclerView and its adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdpter.setLayoutManager(linearLayoutManager);
        mmessagesAdpter = new messageAdpter(chatwin.this, messagesArrayList);
        messageAdpter.setAdapter(mmessagesAdpter);

        // Set receiver profile image and name
        Picasso.get().load(reciverImg).into(profile);
        reciverNName.setText(reciverName);

        // Get current user UID
        SenderUID = firebaseAuth.getUid();

        // Set up chat room identifiers
        senderRoom = SenderUID + reciverUid;
        reciverRoom = reciverUid + SenderUID;

        // Get sender's profile picture
        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid()));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue(String.class);
                reciverIImg = reciverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chatwin.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch messages from Firebase database
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");
        chatreference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    if (messages != null) {
                        messagesArrayList.add(messages);
                    }
                }
                mmessagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chatwin.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });

        // Send message when send button is clicked
        sendbtn.setOnClickListener(view -> {
            String message = textmsg.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(chatwin.this, "Enter the message first", Toast.LENGTH_SHORT).show();
                return;
            }
            textmsg.setText("");
            Date date = new Date();
            msgModelclass messageToSend = new msgModelclass(message, SenderUID, date.getTime());

            // Push message to both sender and receiver rooms
            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .push().setValue(messageToSend).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Also save the message to the receiver's chat room
                            database.getReference().child("chats")
                                    .child(reciverRoom)
                                    .child("messages")
                                    .push().setValue(messageToSend);
                        } else {
                            Toast.makeText(chatwin.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
