package com.penguinsonabeach.tuun.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.penguinsonabeach.tuun.Adapter.CarsRecycleViewAdapter;
import com.penguinsonabeach.tuun.Object.Car;
import com.penguinsonabeach.tuun.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    //verify vin vinUrl1 + VIN + vinUrl2 + currentCar.getYear();
    final String vinUrl2="?format=json&modelyear=";
    //final String vin;
    private RequestQueue requestQueue;
    private PopupWindow mPopupWindow;
    private Context mContext;
    private EditText vinEditText;


    public static GarageCarsFragment newInstance(Bundle arguments) {
        GarageCarsFragment userTab1 = new GarageCarsFragment();
        if (arguments != null) {
            userTab1.setArguments(arguments);
        }
        return userTab1;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");
        View rootView = inflater.inflate(R.layout.fragment_cars,container,false);
        carRecycleView = rootView.findViewById(R.id.carRecycleView);
        carRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CarsRecycleViewAdapter(cars,getActivity());
        carRecycleView.setAdapter(adapter);
        adapter.setOnClick(this);
        setUpFirebase();
        getCars();
        setUpSlide();
        requestQueue= Volley.newRequestQueue(getActivity());
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            // Get the application context
            mContext = getContext();

            StorageReference photoRef = gCarPhotoReference.child(cars.get(position).getId()).child(cars.get(position).getId().concat(".jpg"));

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    cars.get(position).setPhotoUrl(downloadUrl.toString());
                    userRef.child(cars.get(position).getId()).child("photoUrl").setValue(cars.get(position).getPhotoUrl());
                    //Disable progress bar
                }

            });
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

                builder.setPositiveButton(
                        "Remove Vehicle",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                userRef.child(currentCar.getId()).removeValue();
                                Toast.makeText(getContext(),currentCar.toString()+" removed",Toast.LENGTH_SHORT).show();
                                cars.remove(currentCar);
                                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
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
        builder.setNegativeButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.fragment_verify_popup,null);
                vinEditText = getActivity().findViewById(R.id.editTextVin);
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
                ImageButton closeButton =  customView.findViewById(R.id.ib_close);

                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });
                mPopupWindow.showAtLocation(getView(), Gravity.CENTER,0,0);
                dialog.cancel();
            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    public void verifyVehicle(Car car){

        switch(car.getMake().toString()){
            case "Acura":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;

            case "Audi":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "Bentley":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "BMW":
                validateVinGeneral(car,"Model","Series");
                break;
            case "Bugatti":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "Buick":
                validateVinBuick(car,"Model","DisplacementL");
                break;
            case "Cadillac":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Series");
                        break;
                    default:
                        validateVinTrim(car,"Model","Series");
                        break;
                }
                break;
            case "Chevrolet":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Series");
                        break;
                    default:
                        validateVinTrim(car,"Model","Series");
                        break;
                }
                break;
            case "Chrysler":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "Datsun":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "Dodge":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "Ferrari":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "Fiat":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "Ford":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                //TODO Special Handling due to Note
                break;
            case "Honda":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "Hummer":
                validateVinGeneral(car,"Model","Series");
                break;
            case "Hyundai":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                //TODO Handle Series Standard / Turbo / R-Spec / Rally Edition is returned
                break;
            case "Infiniti":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","OtherEngineInfo");
                        break;
                    default:
                        validateVinTrim(car,"Model","OtherEngineInfo");
                        break;
                }
                //TODO Handle trim via motor compare
                break;
            case "Jaguar":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Series");
                        break;
                    default:
                        validateVinTrim(car,"Model","Series");
                        break;
                }
                break;
            case "Jeep":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "KIA":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "Lamborghini":
                validateVinGeneral(car,"Model","Note");
                break;
            case "Land Rover":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Series");
                        break;
                    default:
                        validateVinTrim(car,"Model","Series");
                        break;
                }
                break;
            case "Lexus":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Series2");
                        break;
                    default:
                        validateVinTrim(car,"Model","Series2");
                        break;
                }
                //TODO special handling Series also exists
                break;
            case "Lotus":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "Maserati":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "Mazda":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "McLaren":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "Mercedes-Benz":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "Mini":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Series");
                        break;
                    default:
                        validateVinTrim(car,"Model","Series");
                        break;
                }
                break;
            case "Mitsubishi":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Series");
                        break;
                    default:
                        validateVinTrim(car,"Model","Series");
                        break;
                }
                break;
            case "Nissan":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Series");
                        break;
                    default:
                        validateVinTrim(car,"Model","Series");
                        break;
                }
                break;
            case "Pontiac":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "Porsche":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Series");
                        break;
                    default:
                        validateVinTrim(car,"Model","Series");
                        break;
                }
                break;
            case "Rolls-Royce":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "Subaru":
                switch(car.getTrim()){
                    case "Base":
                        validateVinSubaru(car,"Model","Trim");
                        break;
                    default:
                        validateVinSubaruSTI(car,"Model","Trim");
                        break;
                }
                break;
            case "Tesla":
                validateVinGeneral(car,"Model","Trim");
                break;
            case "Toyota":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                //ToDO <Turbo>Yes</Turbo>
                break;
            case "Volkswagen":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","Trim");
                        break;
                    default:
                        validateVinTrim(car,"Model","Trim");
                        break;
                }
                break;
            case "Volvo":
                switch(car.getTrim()){
                    case "Base":
                        validateVinGeneral(car,"Model","OtherEngineInfo");
                        break;
                    default:
                        validateVinTrim(car,"Model","OtherEngineInfo");
                        break;
                }
                break;

            default:
                Toast.makeText(getActivity(),"Error occurred please check your VIN",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void validateVinGeneral(final Car car, final String modelKey, String trimKey) {
        final String vinUrl1= getResources().getString(R.string.verify_vin_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,vinUrl1 +"editextvin"+vinUrl2+car.getYear(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("Results");
                            JSONObject vehicle = jsonArray.getJSONObject(0);
                            String year = vehicle.getString("Year");
                            String make = vehicle.getString("Make");
                            String model = vehicle.getString(modelKey);

                            if(car.getYear().equals(year) && car.getMake().equals(make) && model.contains(car.getModel())){
                                car.setVerified(true);
                                userRef.child(car.getId()).setValue(car);
                            }
                            else{
                                Toast.makeText(getActivity(),"Unable to verify vehicle, Check VIN",Toast.LENGTH_SHORT).show();}


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void validateVinTrim(final Car car, final String modelKey, final String trimKey) {
        final String vinUrl1= getResources().getString(R.string.verify_vin_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,vinUrl1 +"editextvin"+vinUrl2+car.getYear(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("Results");
                            JSONObject vehicle = jsonArray.getJSONObject(0);
                            String year = vehicle.getString("Year");
                            String make = vehicle.getString("Make");
                            String model = vehicle.getString(modelKey);
                            String trim = vehicle.getString(trimKey);

                            if(car.getYear().equals(year) && car.getMake().equals(make) && model.contains(car.getModel()) && trim.contains(car.getTrim())){
                                car.setVerified(true);
                                userRef.child(car.getId()).setValue(car);
                            }
                            else{Toast.makeText(getActivity(),"Unable to verify vehicle, Check VIN",Toast.LENGTH_SHORT).show();}


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void validateVinBuick(final Car car, final String modelKey, final String trimKey) {
        final String vinUrl1= getResources().getString(R.string.verify_vin_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,vinUrl1 +"editextvin"+vinUrl2+car.getYear(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("Results");
                            JSONObject vehicle = jsonArray.getJSONObject(0);
                            String year = vehicle.getString("Year");
                            String make = vehicle.getString("Make");
                            String model = vehicle.getString(modelKey);
                            String trim = vehicle.getString(trimKey);

                            if(car.getYear().equals(year) && car.getMake().equals(make) && model.contains(car.getModel()) && trim.contains("3.8")){
                                car.setVerified(true);
                                userRef.child(car.getId()).setValue(car);
                            }
                            else{Toast.makeText(getActivity(),"Unable to verify vehicle, Check VIN",Toast.LENGTH_SHORT).show();}


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void validateVinSubaru(final Car car, final String modelKey, final String trimKey) {
        final String vinUrl1= getResources().getString(R.string.verify_vin_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,vinUrl1 +"editextvin"+vinUrl2+car.getYear(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("Results");
                            JSONObject vehicle = jsonArray.getJSONObject(0);
                            String year = vehicle.getString("Year");
                            String make = vehicle.getString("Make");
                            String model = vehicle.getString(modelKey);
                            String trim = vehicle.getString(trimKey);
                            String tempMake = car.getMake();

                            if(tempMake.equals("WRX") || tempMake.equals("Impreza")){tempMake="Impreza WRX";}

                            if(car.getYear().equals(year) && car.getMake().equals(make) && tempMake.contains(model)){
                                car.setVerified(true);
                                userRef.child(car.getId()).setValue(car);
                            }
                            else{Toast.makeText(getActivity(),"Unable to verify vehicle, Check VIN",Toast.LENGTH_SHORT).show();}


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void validateVinSubaruSTI(final Car car, final String modelKey, final String trimKey) {
        final String vinUrl1= getResources().getString(R.string.verify_vin_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,vinUrl1 +"editextvin"+vinUrl2+car.getYear(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("Results");
                            JSONObject vehicle = jsonArray.getJSONObject(0);
                            String year = vehicle.getString("Year");
                            String make = vehicle.getString("Make");
                            String model = vehicle.getString(modelKey);
                            String trim = vehicle.getString(trimKey);
                            String tempMake = car.getMake();

                            if(tempMake.equals("WRX") || tempMake.equals("Impreza")){tempMake="Impreza WRX";}

                            if(car.getYear().equals(year) && car.getMake().equals(make) && tempMake.contains(model) && trim.contains(car.getTrim())){
                                car.setVerified(true);
                                userRef.child(car.getId()).setValue(car);
                            }
                            else{Toast.makeText(getActivity(),"Unable to verify vehicle, Check VIN",Toast.LENGTH_SHORT).show();}


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


}

