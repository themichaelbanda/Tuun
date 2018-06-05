package com.penguinsonabeach.tuun.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.penguinsonabeach.tuun.Adapter.CarsRecycleViewAdapter;
import com.penguinsonabeach.tuun.Object.Car;
import com.penguinsonabeach.tuun.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Phoenix on 3/25/2018.
 */

public class GarageCarsFragment extends Fragment implements CarsRecycleViewAdapter.CustomClickListener {
    private static final String gUserPhotosStorageRef = "user_photos";
    private static final int RC_PHOTO_PICKER = 1;
    FirebaseUser gUser;
    public CarsRecycleViewAdapter adapter;
    private RecyclerView carRecycleView;
    private final ArrayList<Car> cars = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    StorageReference gCarPhotoReference;
    private ChildEventListener carListener;
    private DatabaseReference userRef;
    private Car gCar;
    int position;
    private PopupWindow mPopupWindow;
    private Typeface customFont;

    public static GarageCarsFragment newInstance(Bundle arguments) {
        GarageCarsFragment userTab1 = new GarageCarsFragment();
        if (arguments != null) {
            userTab1.setArguments(arguments);
        }
        return userTab1;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        customFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");
        View rootView = inflater.inflate(R.layout.fragment_cars,container,false);
        carRecycleView = rootView.findViewById(R.id.carRecycleView);
        carRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CarsRecycleViewAdapter(cars,getActivity());
        carRecycleView.setAdapter(adapter);
        adapter.setOnClick(this);
        setUpFirebase();
        getCars();
        setUpSlide();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            uploadImage(data);
        }

    }
    private void setUpFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        gUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = firebaseDatabase.getReference("users").child(gUser.getUid()).child("vehicles");
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        gCarPhotoReference = mFirebaseStorage.getReference().child(gUserPhotosStorageRef).child(gUser.getUid());

    }
    private void getCars(){
        if(carListener == null){
            carListener = new ChildEventListener(){

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    gCar = dataSnapshot.getValue(Car.class);
                    Log.d("Fragment", gCar.toString());
                    cars.add(gCar);
                    adapter.notifyDataSetChanged();
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
                    Log.e(TAG, "Database error" + databaseError);
                }
            };
            userRef.addChildEventListener(carListener);
        }
    }
    private void setUpSlide(){
        final ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final Car currentCar = cars.get(viewHolder.getAdapterPosition());
                String removeMessage = getResources().getString(R.string.remove_vehicle_message)+" "+currentCar.toString();
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.remove_vehicle_title);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage(removeMessage);
                builder.setCancelable(true);
                final StorageReference deleteReference = gCarPhotoReference.child(currentCar.getId().concat(".jpg"));
                Log.d("Reference",deleteReference.toString());

                builder.setPositiveButton(
                        "Remove Vehicle",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),currentCar.toString()+" removed",Toast.LENGTH_SHORT).show();
                                        userRef.child(currentCar.getId()).removeValue();
                                        cars.remove(currentCar);
                                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getContext(),"Error attempting to remove "+currentCar.toString(),Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                    }
                                });
                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(carRecycleView);
    }

    @Override
    public void onCarClicked(final int position){
        this.position = position;
        gCarPhotoReference.child(cars.get(position).getId());
        final Car currentCar = cars.get(position);
        String lMessage = getResources().getString(R.string.add_image_message);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_image_title);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(lMessage);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Update Photo",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        photoIntent.setType("image/jpeg");
                        photoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        startActivityForResult(Intent.createChooser(photoIntent, "Complete action using"), RC_PHOTO_PICKER);
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton(
                "Update Mods",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Initialize a new instance of LayoutInflater service
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                        // Inflate the custom layout/view
                        View customView = inflater.inflate(R.layout.popup_mods,null);
                        // Get a reference for the layout within popup window
                        LinearLayout linearLayout2 = customView.findViewById(R.id.linearLayout2);
                        TextView textView = customView.findViewById(R.id.popupModsTv);
                        textView.setTypeface(customFont);
                        // Get a reference for the layout within popup window
                        final EditText modsEditText = customView.findViewById(R.id.editTextMods);
                        modsEditText.setText(currentCar.getMods());
                        // Initialize a new instance of popup window
                        mPopupWindow = new PopupWindow(
                                customView,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        // Set an elevation value for popup window
                        // Call requires API level 21
                        if(Build.VERSION.SDK_INT>=21){
                            mPopupWindow.setElevation(5.0f);
                            mPopupWindow.setFocusable(true);
                        }
                        // Get a reference for the custom view close button
                        Button addModsButton =  customView.findViewById(R.id.addModsButton);

                        // Set a click listener for the popup window close button
                        addModsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                    String modsEntry = modsEditText.getText().toString();
                                    currentCar.setMods(modsEntry);
                                    userRef.child(currentCar.getId()).setValue(currentCar);

                                // Dismiss the popup window
                                    mPopupWindow.dismiss();
                            }
                        });
                        mPopupWindow.showAtLocation(linearLayout2, Gravity.CENTER,0,0);
                        dialog.cancel();

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void uploadImage(Intent data){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        Uri selectedImageUri = data.getData();

        StorageReference photoRef = gCarPhotoReference.child(cars.get(position).getId().concat(".jpg"));
        // Upload file to Firebase Storage
        photoRef.putFile(selectedImageUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("Position",cars.get(position).toString());
                cars.get(position).setPhotoUrl(downloadUrl.toString());
                Log.d("PhotoUpload",cars.get(position).getPhotoUrl());
                userRef.child(cars.get(position).getId()).child("photoUrl").setValue(cars.get(position).getPhotoUrl());
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Error uploading image. Please try again",Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded "+(int)progress+"%");
            }
        });
    }

}

