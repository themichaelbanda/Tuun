package com.penguinsonabeach.tuun.Fragment;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.penguinsonabeach.tuun.R;

import org.w3c.dom.Text;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ProfileInfoFragment extends Fragment {
    private String lPoints, lDate, lName, lUserName, lClub;
    private Typeface customFont;
    private ValueEventListener userNameListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef, userNamesRef;
    private FirebaseUser gUser;
    TextView userNameTv, nameTv, dateTv, pointsTv, clubTv;
    Button usernameButton;

    public static ProfileInfoFragment newInstance(Bundle arguments) {
        ProfileInfoFragment userTab1 = new ProfileInfoFragment();
        if (arguments != null) {
            userTab1.setArguments(arguments);
        }
        return userTab1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if (getArguments() != null) {
        lPoints = String.valueOf(getArguments().getInt("points"));
        lDate = String.valueOf(getArguments().getString("joindate"));
        lName = String.valueOf(getArguments().getString("name"));
        lUserName = String.valueOf(getArguments().getString("username"));
        lClub = String.valueOf(getArguments().getString("club"));
        }
        customFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");
        View rootView = inflater.inflate(R.layout.fragment_profile_info,container,false);

        pointsTv = rootView.findViewById(R.id.profilePointsValue);
        dateTv = rootView.findViewById(R.id.profileJoinValue);
        nameTv = rootView.findViewById(R.id.profileNameValue);
        userNameTv = rootView.findViewById(R.id.profileStreetNameValue);
        clubTv = rootView.findViewById(R.id.profileClubValue);
        TextView pointsTitle = rootView.findViewById(R.id.profilePointsTitle);
        TextView nameTitle = rootView.findViewById(R.id.profileNameTitle);
        TextView dateTitle = rootView.findViewById(R.id.profileJoinTitle);
        TextView usernameTitle = rootView.findViewById(R.id.profileStreetNameTitle);
        TextView clubTitle = rootView.findViewById(R.id.profileClubTitle);
        usernameButton = rootView.findViewById(R.id.usernameButton);

        pointsTitle.setTypeface(customFont);
        nameTitle.setTypeface(customFont);
        dateTitle.setTypeface(customFont);
        usernameTitle.setTypeface(customFont);
        clubTitle.setTypeface(customFont);

        pointsTv.setText(lPoints);
        dateTv.setText(lDate);
        nameTv.setText(lName);
        clubTv.setText(lClub);

        if(lUserName.equals("null")){
            Log.d("Street Name", "null");
            userNameTv.setText("Not Selected");
            usernameButton.setVisibility(View.VISIBLE);
            usernameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createUserName();
                }
            });
        }
        else {
            Log.d("Street Name", lUserName);
            userNameTv.setText(lUserName);
        }

        setUpFirebase();
        return rootView;
    }

    private void setUpFirebase(){
        database = FirebaseDatabase.getInstance();
        gUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference("users").child(gUser.getUid());
        userNamesRef = database.getReference("usernames");
    }

    protected void createUserName(){

        final PopupWindow mPopupWindow;
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popup_username,null);

        // Get a reference for the layout within popup window
        LinearLayout linearLayout1 = customView.findViewById(R.id.linearLayout1);

        // Get a reference for the layout within popup window
        final EditText userNameEditText = customView.findViewById(R.id.editTextUserName);
        final TextView userNameTextView = customView.findViewById(R.id.userNameTitleTv);
        userNameTextView.setTypeface(customFont);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(false);

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        final Button verifyNameButton =  customView.findViewById(R.id.verifyNameButton);


        // Set a click listener for the popup window close button
        verifyNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!lUserName.equals("null")){
                    mPopupWindow.dismiss();
                    Log.d("UserName: ", "First Loop");
                }if(userNameEditText.getText().toString().equals("null")){
                    createUserNameToast("Username Cannot Be null!");
                    return;
                }else if(userNameEditText.getText().toString().length() < 1){
                    createUserNameToast("Username Cannot Be Empty!");
                    return;
                }
                else{
                    userNameListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(userNameEditText.getText().toString()).exists()){
                                Toast.makeText(getContext()," User Name is Taken, Please try again!", Toast.LENGTH_SHORT).show();
                            }else{
                                myRef.child("username").setValue(userNameEditText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        myRef.child(userNameEditText.getText().toString()).setValue(gUser.getUid());
                                        userNameTv.setText(userNameEditText.getText().toString());
                                        usernameButton.setVisibility(View.INVISIBLE);
                                        createUserNameToast(userNameEditText.getText().toString()+" has been set as your user name!");
                                        mPopupWindow.dismiss();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    userNamesRef.addListenerForSingleValueEvent(userNameListener);
                }
                // Dismiss the popup window
                mPopupWindow.dismiss();

            }
        });
        mPopupWindow.showAtLocation(linearLayout1, Gravity.CENTER,0,0);
    }

    private void createUserNameToast(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) getActivity().findViewById(R.id.toast_layout));
        layout.setBackgroundResource(R.drawable.borderconnection);
        ImageView image = layout.findViewById(R.id.toastimage);
        image.setImageResource(R.mipmap.ic_warning);
        TextView text = layout.findViewById(R.id.toasttext);
        text.setText(message);
        text.setTypeface(customFont);


        Toast pToast = new Toast(getContext());
        pToast.setGravity(Gravity.TOP, 0, 250);
        pToast.setDuration(Toast.LENGTH_SHORT);
        pToast.setView(layout);
        pToast.show();
    }

}
