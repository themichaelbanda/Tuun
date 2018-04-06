package com.penguinsonabeach.tuun.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.penguinsonabeach.tuun.Adapter.CarsRecycleViewAdapter;
import com.penguinsonabeach.tuun.Object.Car;
import com.penguinsonabeach.tuun.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Phoenix on 3/4/2018.
 */

public class UserInfoCarsFragment extends Fragment implements CarsRecycleViewAdapter.CustomClickListener {
    String lUser;
    public CarsRecycleViewAdapter adapter;
    private RecyclerView carRecycleView;
    private final ArrayList<Car> cars = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    private ChildEventListener carListener;
    private DatabaseReference userRef;
    private Car lCar;

    public static UserInfoCarsFragment newInstance(Bundle arguments) {
        UserInfoCarsFragment userTab2 = new UserInfoCarsFragment();
        if (arguments != null) {
            userTab2.setArguments(arguments);
        }
        return userTab2;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        lUser = String.valueOf(this.getArguments().getString("key"));
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");
        View rootView = inflater.inflate(R.layout.fragment_cars,container,false);
        carRecycleView = rootView.findViewById(R.id.carRecycleView);
        carRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CarsRecycleViewAdapter(cars,getActivity());
        carRecycleView.setAdapter(adapter);
        setUpFirebase();
        getCars();
       return rootView;
    }

    private void setUpFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("users").child(lUser).child("vehicles");


    }
    private void getCars(){
        if(carListener == null){
            carListener = new ChildEventListener(){

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    lCar = dataSnapshot.getValue(Car.class);
                    Log.d("Fragment",lCar.toString());
                    cars.add(lCar);
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

    public void onCarClicked(int position){
        //gCarPhotoReference.child(cars.get(position).getId());
    }
}
