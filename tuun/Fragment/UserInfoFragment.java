package com.penguinsonabeach.tuun.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.penguinsonabeach.tuun.Object.ChatMessage;
import com.penguinsonabeach.tuun.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Phoenix on 3/4/2018.
 */

public class UserInfoFragment extends Fragment {

    private PopupWindow mPopupWindow;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser gUser;
    DatabaseReference messageRef, myRef;
    String lUid, lName, gName, gPhoto;

    public static UserInfoFragment newInstance(Bundle arguments) {
        UserInfoFragment userTab1 = new UserInfoFragment();
        if (arguments != null) {
            userTab1.setArguments(arguments);
        }
        return userTab1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        String lPoints = String.valueOf(this.getArguments().getInt("points"));
        String lDate = String.valueOf(this.getArguments().getString("date"));
        String lClub = String.valueOf(this.getArguments().getString("club"));
        lUid = String.valueOf(this.getArguments().get("key"));
        lName = String.valueOf(this.getArguments().get("name"));
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");
        View rootView = inflater.inflate(R.layout.fragment_user_home,container,false);

        TextView pTv = rootView.findViewById(R.id.infoPointsTitle);
        TextView pointsTv = rootView.findViewById(R.id.infoPointsValue);
        TextView cTv = rootView.findViewById(R.id.infoClubTitle);
        TextView clubTv = rootView.findViewById(R.id.infoClubValue);
        TextView dateTv = rootView.findViewById(R.id.infoJoinTitle);
        TextView joinDate = rootView.findViewById(R.id.infoJoinValue);
        Button sendMessage = rootView.findViewById(R.id.sendMessageButton);
        pTv.setTypeface(customFont);
        cTv.setTypeface(customFont);
        dateTv.setTypeface(customFont);

        setPointsText(pointsTv, lPoints);
        clubTv.setText(lClub);
        joinDate.setText(lDate);

        setUpFirebase();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMessagePopup();
            }
        });

        return rootView;
    }

    protected void setUpFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        gUser = FirebaseAuth.getInstance().getCurrentUser();
        messageRef = firebaseDatabase.getReference("messages").child(lUid).child(gUser.getUid());
        myRef = firebaseDatabase.getReference("users").child(gUser.getUid());
    }
    protected void setPointsText(TextView tv, String lPoints){
        tv.setText(" " + lPoints);
        if(Integer.parseInt(lPoints) >= 1000){
            tv.setTextColor(ContextCompat.getColor(this.getContext(),R.color.gold));
        }
        if(Integer.parseInt(lPoints) >= 200 && Integer.parseInt(lPoints) < 1000){
            tv.setTextColor(ContextCompat.getColor(this.getContext(),R.color.silver));
        }
        else if(Integer.parseInt(lPoints) < 200){
            tv.setTextColor(ContextCompat.getColor(this.getContext(),R.color.bronze));
        }
    }
    protected String sendMessage(final String message){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("username").exists()) {
                    gName = dataSnapshot.child("username").getValue().toString();
                    gPhoto = dataSnapshot.child("photoUrl").getValue().toString();
                    ChatMessage lMessage = new ChatMessage(message,gName,gPhoto);
                    messageRef.setValue(lMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            launchMessageToast(lName);
                        }
                    });
                }
                else{
                    gName = dataSnapshot.child("name").getValue().toString();
                    gPhoto = dataSnapshot.child("photoUrl").getValue().toString();
                    ChatMessage lMessage = new ChatMessage(message,gName,gPhoto);
                    messageRef.setValue(lMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            launchMessageToast(lName);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return gName;
    }
    protected void launchMessagePopup(){

        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

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
                sendMessage(message);

                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation(linearLayout, Gravity.CENTER,0,0);
    }
    protected void launchMessageToast(String userName){
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) getActivity().findViewById(R.id.toast_layout));
        layout.setBackgroundResource(R.drawable.borderconnection);
        ImageView image = layout.findViewById(R.id.toastimage);
        image.setImageResource(R.mipmap.ic_action_email);
        TextView text = layout.findViewById(R.id.toasttext);
        text.setText("Message Sent to : "+ userName);
        text.setTypeface(customFont);


        Toast pToast = new Toast(getActivity());
        pToast.setGravity(Gravity.TOP, 0, 250);
        pToast.setDuration(Toast.LENGTH_SHORT);
        pToast.setView(layout);
        pToast.show();
    }
}
