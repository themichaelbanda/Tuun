package com.penguinsonabeach.tuun.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.penguinsonabeach.tuun.Adapter.MessagesRecycleViewAdapter;
import com.penguinsonabeach.tuun.Fragment.UserInfoFragment;
import com.penguinsonabeach.tuun.Object.ChatMessage;
import com.penguinsonabeach.tuun.Object.User;
import com.penguinsonabeach.tuun.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MessagesActivity extends AppCompatActivity implements MessagesRecycleViewAdapter.CustomClickListener {

    TextView title;
    RecyclerView messagesRecycleView;
    public MessagesRecycleViewAdapter messageAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseDatabase firebaseDatabase;
    private Query messagesQuery;
    private DatabaseReference messageRef, myRef, myUserInfoRef;
    private FirebaseAuth mAuth;
    private PopupWindow mPopupWindow;
    private final ArrayList<ChatMessage> messages = new ArrayList<>();
    private ChatMessage gMessage;
    String gName, gPhoto;
    int position;


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

        messageAdapter = new MessagesRecycleViewAdapter(messages,this);
        messagesRecycleView.setAdapter(messageAdapter);
        messageAdapter.setOnClick(this);

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
        myRef = messageRef.child(mAuth.getCurrentUser().getUid());
        myUserInfoRef = firebaseDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
        messagesQuery = myRef.orderByChild("messageTime");

        messagesQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                gMessage = dataSnapshot.getValue(ChatMessage.class);
                gMessage.setMessageUserId(dataSnapshot.getKey());
                messages.add(gMessage);
                messageAdapter.notifyDataSetChanged();
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

    @Override
    public void onMessageClicked(final int position) {
        this.position = position;
        final ChatMessage currentMessage = messages.get(position);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(currentMessage.getMessageUser());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(currentMessage.getMessageText());
        builder.setCancelable(true);


        if(currentMessage.getMessageRead().equalsIgnoreCase("Unread")){
        currentMessage.setMessageRead("Read");
        messageAdapter.notifyDataSetChanged();
        myRef.child(currentMessage.getMessageUserId()).child("messageRead").setValue("Read");}

        builder.setPositiveButton(
                "Reply",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        launchMessagePopup();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void launchMessagePopup(){

        Typeface customFont = Typeface.createFromAsset(this.getAssets(),"fonts/Capture_it.ttf");

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popup_message,null);

        // Get a reference for the layout within popup window
        LinearLayout linearLayout = customView.findViewById(R.id.linearLayout1);
        TextView title = customView.findViewById(R.id.messageTitleTv);
        title.setTypeface(customFont);

        // Get a reference for the layout within popup window
        final EditText messageEditText = customView.findViewById(R.id.editTextMessage);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // Set an elevation value for popup window
        mPopupWindow.setElevation(5.0f);
        mPopupWindow.setFocusable(true);

        // Get a reference for the custom view close button
        Button sendMessageButton =  customView.findViewById(R.id.sendMessageButton);

        // Set a click listener for the popup window close button
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = messageEditText.getText().toString();
                sendMessage(message, position);

                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation(linearLayout, Gravity.CENTER,0,0);
    }

    protected void sendMessage(final String message, int position) {
        //Gathering UID from user in order to set path
        final ChatMessage currentMessage = messages.get(position);
        final String toUser = currentMessage.getMessageUserId();

        myUserInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("username").exists()) {
                    gName = dataSnapshot.child("username").getValue().toString();
                    gPhoto = dataSnapshot.child("photoUrl").getValue().toString();
                    ChatMessage lMessage = new ChatMessage(message, gName, gPhoto);
                    messageRef.child(toUser).child(mAuth.getCurrentUser().getUid()).setValue(lMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            launchMessageToast();
                            messageRef.child(mAuth.getCurrentUser().getUid()).child(toUser).child("messageRead").setValue("Replied");
                            currentMessage.setMessageRead("Replied");
                            messageAdapter.notifyDataSetChanged();
                        }

                    });
                } else {
                    gName = dataSnapshot.child("name").getValue().toString();
                    gPhoto = dataSnapshot.child("photoUrl").getValue().toString();
                    ChatMessage lMessage = new ChatMessage(message, gName, gPhoto);
                    messageRef.child(toUser).child(mAuth.getCurrentUser().getUid()).setValue(lMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            launchMessageToast();
                            messageRef.child(mAuth.getCurrentUser().getUid()).child(toUser).child("messageRead").setValue("Replied");
                            currentMessage.setMessageRead("Replied");
                            messageAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void launchMessageToast(){
        Typeface customFont = Typeface.createFromAsset(this.getAssets(),"fonts/Capture_it.ttf");
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) this.findViewById(R.id.toast_layout));
        layout.setBackgroundResource(R.drawable.borderconnection);
        ImageView image = layout.findViewById(R.id.toastimage);
        image.setImageResource(R.mipmap.ic_action_email);
        TextView text = layout.findViewById(R.id.toasttext);
        text.setText("Reply Sent");
        text.setTypeface(customFont);


        Toast pToast = new Toast(this);
        pToast.setGravity(Gravity.TOP, 0, 250);
        pToast.setDuration(Toast.LENGTH_SHORT);
        pToast.setView(layout);
        pToast.show();
    }
}
