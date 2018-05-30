package com.penguinsonabeach.tuun.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.penguinsonabeach.tuun.Adapter.MessagesRecycleViewAdapter;
import com.penguinsonabeach.tuun.Object.ChatMessage;
import com.penguinsonabeach.tuun.Object.User;
import com.penguinsonabeach.tuun.R;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    TextView title;
    RecyclerView messagesRecycleView;
    private RecyclerView.Adapter messageAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseDatabase firebaseDatabase;
    private Query messagesQuery;
    private DatabaseReference messageRef, myRef, userRef;
    private ValueEventListener userListener;
    private FirebaseAuth mAuth;
    private final ArrayList<ChatMessage> messages = new ArrayList<>();
    private final ArrayList<String> userPhotos = new ArrayList<>();
    private String gUserPhoto;
    private ChatMessage gMessage;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_messages);

        //Adding toolbar to the activity
        Toolbar toolbar = findViewById(R.id.toolbarMessages);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        title = findViewById(R.id.messagesTitle);

        Typeface customFont = Typeface.createFromAsset(this.getAssets(),"fonts/Capture_it.ttf");
        title.setTypeface(customFont);

        messagesRecycleView = findViewById(R.id.messagesRV);

        mLayoutManager = new LinearLayoutManager(this);
        messagesRecycleView.setLayoutManager(mLayoutManager);

        messageAdapter = new MessagesRecycleViewAdapter(messages, userPhotos,this);
        messagesRecycleView.setAdapter(messageAdapter);

        setUpFirebase();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpFirebase() {

        //Authentication
        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        messageRef = firebaseDatabase.getReference("messages");
        userRef = firebaseDatabase.getReference("users");
        myRef = messageRef.child(mAuth.getCurrentUser().getUid());
        messagesQuery = myRef.orderByChild("messageTime");

        messagesQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                gMessage = dataSnapshot.getValue(ChatMessage.class);
                getUserPhoto(dataSnapshot.getKey());
                messages.add(gMessage);
                //messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserPhoto(String id){

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gUserPhoto = dataSnapshot.child("photoUrl").getValue().toString();
                userPhotos.add(gUserPhoto);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userRef.child(id).addListenerForSingleValueEvent(userListener);
    }
}
