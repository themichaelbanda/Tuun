package com.penguinsonabeach.tuun.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.penguinsonabeach.tuun.Network.NetworkChangeReceiver;
import com.penguinsonabeach.tuun.R;
import com.penguinsonabeach.tuun.Object.Shop;
import com.penguinsonabeach.tuun.Object.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.pawelkleczkowski.customgauge.CustomGauge;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final String gUserDetailsRef = "users";
    private static final String gShopDetailsRef = "shops";
    private static final String gUserOnlineGeoRef = "online-users";
    private static final String gUserPhotosStorageRef = "user_photos";
    private static final String gShopGeoRef = "shop-location";
    private final static String KEY_LOCATION = "location";
    private Boolean centerEnabled = TRUE;
    private int updateInterval=0;
    private final double mphMultiplier =  2.23694;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser gUser;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_PHOTO_PICKER = 2;
    private ListView mDrawerList;
    private TextView mTitle;
    private TextView gSpeed;
    private CustomGauge gGauge;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private final String default_img = "https://firebasestorage.googleapis.com/v0/b/tuun-67689.appspot.com/o/user_photos%2Fno_icon.png?alt=media&token=85744938-bef8-4e56-bbbb-ab357393f8ae";
    private final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    private GoogleMap mMap;
    private User uUser;
    private ImageView profilePicture;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private DatabaseReference myRef;
    private DatabaseReference shopsRef;
    private DatabaseReference geoRefUser;
    private DatabaseReference geoRefShop;
    private GeoFire geoFireUser, geoFireShop;
    private GeoQuery geoQueryUser, geoQueryShop;
    private StorageReference gPhotoStorageRef;
    private ValueEventListener userRefListener;
    private GeoQueryEventListener userQueryListener, shopQueryListener;
    private final HashMap<String, Marker> hashMapUserMarker = new HashMap<>();
    private final HashMap<String, Marker> hashMapShopMarker = new HashMap<>();
    private final HashMap<String, Bitmap> hashMapBitmap = new HashMap<>();
    private final HashMap<String, Shop> hashMapShopObjects = new HashMap<>();
    private final HashMap<String, User> hashMapUserObjects = new HashMap<>();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNavBarLayout();

        //Set view variables
        profilePicture = findViewById(R.id.profilePictureImg);
        mTitle = this.findViewById(R.id.hNameTextView);
        gSpeed = this.findViewById(R.id.speed);
        gGauge = this.findViewById(R.id.gauge);
        Typeface customFont = Typeface.createFromAsset(this.getAssets(),"fonts/Capture_it.ttf");
        gSpeed.setTypeface(customFont);



        // Initialize Firebase Database and Storage tools
        setUpFirebase();

        //Check if first time user
        createUser();

        //Remove on disconnect
        removeGeoOnDiscon();

        // Database filtering
        //usersRef.limitToFirst(1000);

        //User Object Initialization
        userInit(savedInstanceState);

        /* Initialize UI with user data and update if data changes in db */
        attachUserDatabaseReadListener();

        // Build the map
        setUpMap();

        //admin function to add shops to map todo add to a javascript based webpage
        //setShops();

        // Google Auth function to initialize data import for user
        setGoogleAuthDetails();
        setUserActive();


    }

    protected void checkConnection(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            signOut();
            Toast.makeText(this,"You Are Not Connected to a network!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Get a reference to store file at user_photos/<FILENAME> TODO
            StorageReference photoRef = gPhotoStorageRef.child(gUser.getUid().concat(".jpg"));

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    uUser.setPhotoUrl(downloadUrl.toString());
                    myRef.child("photoUrl").setValue(uUser.getPhotoUrl());
                    progressDialog.dismiss();

                    Glide.with(profilePicture.getContext())
                            .load(uUser.getPhotoUrl())
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilePicture);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Error uploading image. Please try again",Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //create navigation bar layout
    private void createNavBarLayout() {
        mDrawerList = findViewById(R.id.navList);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    //Set up drawer
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Choose an Option!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

    }

    //Fill drawer with labels/selections
    private void addDrawerItems() {
        View header = getLayoutInflater().inflate(R.layout.navigation_header, null);
        String[] osArray = {"Garage", "Club", "Shops", "Leaderboards", "E85", "Customer Support", "Sign Out"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.addHeaderView(header);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        photoIntent.setType("image/jpeg");
                        photoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        startActivityForResult(Intent.createChooser(photoIntent, "Complete action using"), RC_PHOTO_PICKER);
                        break;
                    case 1:
                        Intent garageIntent = new Intent(MainActivity.this,GarageActivity.class);
                        startActivity(garageIntent);
                        break;
                    case 6:
                        sendCustSuppEmail();
                        break;
                    case 7:
                        signOut();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mMap != null) {
            savedInstanceState.putParcelable(KEY_LOCATION, mLastLocation);
            super.onSaveInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStop() {
        super.onStop();
        detachDatabaseReadListener();
        detachAuthStateListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        attachAuthStateListener();

    }

    private void sendCustSuppEmail() {

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"customersupport@penguinsonabeach.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TuuN Customer Support");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "What can we help you with?");

        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    // Method to handle appropriate actions when user signs out
    private void signOut() {
        myRef.child("online").setValue("False");
        geoFireUser.removeLocation(gUser.getUid());
        if(!getFusedLocationProviderClient(this).equals(null)){
        getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);}
        FirebaseAuth.getInstance().signOut();
        Intent sOut = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(sOut);
    }

    //Method called when permissions are denied in order to force user out of application
    private void forceOut(){
        myRef.child("online").setValue("False");
        geoFireUser.removeLocation(gUser.getUid());
        FirebaseAuth.getInstance().signOut();
        Intent sOut = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(sOut);
    }

    /******************************/
    /*
    /*    Google Auth Functions
    /*
    /******************************/

    private void setGoogleAuthDetails(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    /******************************/
    /*
    /*    Firebase Functions
    /*
    /******************************/

    private void setUpFirebase(){

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        gUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        usersRef = database.getReference(gUserDetailsRef);
        shopsRef = database.getReference(gShopDetailsRef);
        myRef = usersRef.child(mAuth.getCurrentUser().getUid());
        geoRefUser = database.getReference(gUserOnlineGeoRef);
        geoRefShop = database.getReference(gShopGeoRef);

        // Initialize Geofire references
        geoFireUser = new GeoFire(geoRefUser);
        geoFireShop = new GeoFire(geoRefShop);

        // Storage reference instantiation for Image URL
        gPhotoStorageRef = mFirebaseStorage.getReference().child(gUserPhotosStorageRef).child(gUser.getUid());
    }

    private void createUser(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("name").exists()){
                    //User does not exist
                    usersRef.child(gUser.getUid()).setValue(new User(gUser.getEmail(), gUser.getDisplayName(),null, default_img,getDate()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void attachUserDatabaseReadListener() {

            userRefListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        uUser = dataSnapshot.getValue(User.class);
                        mTitle.setText(uUser.getName());

                        if (uUser.getPhotoUrl() != null) {
                            Glide.with(profilePicture.getContext())
                                    .load(uUser.getPhotoUrl())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profilePicture);
                        } else {
                            profilePicture.setImageResource(R.drawable.no_icon);
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error" + databaseError);
                }
            };

            myRef.addValueEventListener(userRefListener);
    }

    private void detachDatabaseReadListener() {
        if (userRefListener != null) {
            myRef.removeEventListener(userRefListener);
            userRefListener = null;
        }
    }

    private void attachAuthStateListener() {
        if (mAuthStateListener == null) {
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    private void detachAuthStateListener() {
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
            mAuthStateListener = null;
        }
    }

    private void setUserActive() {
        myRef.child("online").setValue("True");
        myRef.child("online").onDisconnect().setValue("False");
    }

    private void removeGeoOnDiscon(){
        geoRefUser.child(gUser.getUid()).onDisconnect().removeValue();
    }

    private void setGeoQueryUser(Location location){
        if(geoQueryUser == null){
            geoQueryUser = geoFireUser.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()),25);
        }
        else{
            geoQueryUser.setCenter(new GeoLocation(location.getLatitude(),location.getLongitude()));
        }
    }

    private void setGeoQueryShop(Location location){
        if(geoQueryShop == null){
            geoQueryShop = geoFireShop.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()),50);
        }
        else{
            geoQueryShop.setCenter(new GeoLocation(location.getLatitude(),location.getLongitude()));
        }
    }


    /******************************/
    /*
    /*    Google Map Functions
    /*
    /******************************/

    //Setup map
    private void setUpMap() {

           SupportMapFragment mapFragment =
                   (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
           mapFragment.getMapAsync(this);

       }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapLongClickListener(this);

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    centerEnabled=FALSE;
                    Log.d("Center", centerEnabled.toString());
                }
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION){
                    centerEnabled=TRUE;
                    Log.d("Center", centerEnabled.toString());
                }
            }
        });

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
                mMap.setMaxZoomPreference(15.0f);
                mMap.setMinZoomPreference(10.0f);
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                UiSettings settings = mMap.getUiSettings();
                settings.setZoomControlsEnabled(false);
                centerMap();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        getLocationPermission();
        setInfoWindow();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
            Log.d(TAG, "Issue 1: Tag 1: permission granted");
        } else {
            Log.d(TAG, "Issue 1: Tag 2: permission not granted Request Started");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //TODO LIMIT TO ONE MEET WITHIN USERS RADIUS
       /* if (uUser.getLocation() != null) {
            LatLng lLatLng = new LatLng(uUser.getLocation().getLatitude(), uUser.getLocation().getLongitude());
            if ((latLng.latitude < (lLatLng.latitude + 0.02)) && (latLng.latitude>(lLatLng.latitude - 0.02)) && (latLng.longitude < (lLatLng.longitude + 0.02))&&(latLng.longitude>(lLatLng.longitude - 0.02)))
                mMap.addMarker(new MarkerOptions().position(latLng).title("Car Meet").icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(this, R.drawable.meet, "Meet"))));
        }*/
    }


    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Issue 1: Tag 3: permission granted");
                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Issue 1: Tag 4: permission granted");
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        setUpMap();
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Issue 1: Tag 5: permission not granted");
                    forceOut();
                }
            }

        }
    }

    // Trigger new location updates at interval/newest version
    private void startLocationUpdates() {

        // Create the location request to start receiving updates
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (android.support.v4.app.ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && android.support.v4.app.ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            Log.d(TAG,"OnLocationChanged : Location is Null");
            return;
        }

        // New location has now been determined
        mLastLocation = location;
        String speed = convertToMPH(location.getSpeed());
        gSpeed.setText(speed + " MPH");
        gGauge.setValue(Integer.valueOf(speed));
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        if(updateInterval % 2 == 0){
        uUser.setLocation(mLastLocation);
        myRef.child("latitude").setValue(location.getLatitude());
        myRef.child("longitude").setValue(location.getLongitude());
        geoFireUser.setLocation(mAuth.getCurrentUser().getUid(),new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        //geoFireShop.setLocation(mAuth.getCurrentUser().getUid(),new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        setGeoQueryUser(location);
        setGeoQueryShop(location);
        fetchNearbyUsers();
        fetchNearbyShops();
        }
        updateInterval++;

        //if flag is set to true follow user
        if(centerEnabled) {
            centerMap();
        }
    }

    //method to center camera on current user
    private void centerMap() {


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 11));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(13)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private String convertToMPH(float speed){
        double mph;
        mph = speed * mphMultiplier;
        return String.valueOf(Math.round(mph));
    }


    /******************************/
    /*
    /*    Marker Related Functions
    /*
    /******************************/

    private static Bitmap createCustomMarker(Context context, String URL, String _name, Bitmap urlBitmap) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        //RelativeLayout relativeLayout = (RelativeLayout) marker.findViewById(R.id.markerBG);
        //relativeLayout.setBackgroundResource(R.drawable.marker_image);
        CircleImageView markerImage = marker.findViewById(R.id.user_dp);
        markerImage.setImageBitmap(urlBitmap);

        TextView txt_name = marker.findViewById(R.id.name);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    private static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        TextView txt_name = marker.findViewById(R.id.name);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    // Update existing marker with icon from URL
    private void setCustomIcon(Context context, String URL, String _name, Marker userMarker) {
        final Context mContext = context;
        final String mURL = URL;
        final String m_name = _name;
        final Marker mUserMarker = userMarker;

        Glide.with(mContext.getApplicationContext())
                .asBitmap()
                .load(mURL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> transition) {
                        Log.d(TAG," Might be Null if no further log message");
                        if (mUserMarker != null) {
                            mUserMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(mContext,mURL,m_name, bitmap)));
                            Log.d(TAG,"Custom Marker Set!!!! ");
                            hashMapBitmap.put(mUserMarker.getSnippet(),bitmap);

                        }
                    }
                });

    }

    //Function to run GeoQuery to find users within radius
    private void fetchNearbyUsers() {
        if (userQueryListener == null){
            userQueryListener = new GeoQueryEventListener() {

                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    getUserMarkerDetails(key);
                }

                @Override
                public void onKeyExited(String key) {
                    if(hashMapUserMarker.get(key)!= null){
                        Marker marker = hashMapUserMarker.get(key);
                        marker.remove();
                        hashMapUserMarker.remove(key);
                        hashMapUserObjects.remove(key);
                    }
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    if(hashMapUserMarker.get(key)!= null) {
                        LatLng latLng = new LatLng(location.latitude, location.longitude);
                        hashMapUserMarker.get(key).setPosition(latLng);
                    }
                }

                @Override
                public void onGeoQueryReady() {
                    // ...
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    // ...
                }

            };
            geoQueryUser.addGeoQueryEventListener(userQueryListener);
        }
    }

    //Function to run GeoQuery to find shops within radius
    private void fetchNearbyShops() {
        if (shopQueryListener == null) {
            shopQueryListener = new GeoQueryEventListener() {

                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    getShopMarkerDetails(key);
                }

                @Override
                public void onKeyExited(String key) {
                    // Remove any old marker
                    Marker marker = hashMapShopMarker.get(key);
                    if (marker != null) {
                        marker.remove();
                        hashMapShopMarker.remove(key);
                        hashMapShopObjects.remove(key);
                    }

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {
                    // ...
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    // ...
                }

            };
            geoQueryShop.addGeoQueryEventListener(shopQueryListener);
        }
    }

    /* admin level function */
    private void setShops(){
        GeoLocation shopLoc = new GeoLocation(33.044959,-96.973157);
        DatabaseReference shopRef = database.getReference("shops");
        String lKey = shopRef.child("shops").push().getKey();
        Shop shop = new Shop(shopLoc, "Evolution Dynamics", "tuner", lKey);
        shopRef.child(lKey).setValue(shop);

        geoFireShop.setLocation(lKey,shopLoc);
    }

    //Function to get user details from Firebase DB
    private void getUserMarkerDetails(final String key){

        Query query = usersRef.orderByKey().equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot person : dataSnapshot.getChildren()){
                    setUserMarker(person);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO handle if canceled
            }
        });
    }

    //Function to get shop details from Firebase DB
    private void getShopMarkerDetails(final String key){

        Query query = shopsRef.orderByKey().equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shop : dataSnapshot.getChildren()){
                    setShopMarker(shop);
                    Log.d("Details", key);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO handle if canceled
            }
        });
    }

    //Create the user marker from data retrieved and set to map
    private void setUserMarker(DataSnapshot dataSnapshot) {

        // When a location update is received, put or update
        // its value in hashMapUserMarker, which contains all the markers
        // for locations received
        String key = dataSnapshot.getKey();
        double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
        double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
        LatLng location = new LatLng(lat, lng);


        // If condition to check that User data loaded is not your own/this is your first time being loaded to map/User is online :: Add marker to map
        if ((!hashMapUserMarker.containsKey(key)) && (!key.equals(mAuth.getCurrentUser().getUid())) && ((dataSnapshot.child("online").getValue().equals("True")))) {
            hashMapUserMarker.put(key, mMap.addMarker(new MarkerOptions().title(dataSnapshot.child("name").getValue().toString()).position(location).snippet(key).icon(BitmapDescriptorFactory.fromBitmap(
                    createCustomMarker(this,R.drawable.no_icon,dataSnapshot.child("name").getValue().toString())))));
            setCustomIcon(this,dataSnapshot.child("photoUrl").getValue().toString(),dataSnapshot.child("name").getValue().toString(), hashMapUserMarker.get(key));
            convertSnapshotToUser(dataSnapshot);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : hashMapUserMarker.values()) {
            builder.include(marker.getPosition());
        }
    }

    //Create the shop marker from data retrieved and set to map
    private void setShopMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in hashMapShopMarker, which contains all the markers
        // for locations received
        String key = dataSnapshot.getKey();
        String lName = dataSnapshot.child("name").getValue().toString();
        double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
        double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
        String lSpecialty = dataSnapshot.child("specialty").getValue().toString();
        LatLng location = new LatLng(lat, lng);
        Log.d("SetShopMarker",key);
        // If condition to check that User data loaded is not your own/this is your first time being loaded to map/User is online :: Add marker to map
        if (lSpecialty.equals("tuner")) {
            hashMapShopMarker.put(key, mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).title(lName).snippet(key).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MainActivity.this, R.drawable.shop, lName)))));
            hashMapBitmap.put(key,BitmapFactory.decodeResource(getResources(),R.drawable.shop));
            convertSnapshotToShop(dataSnapshot);
        }
        if(lSpecialty.equals("wraps")){
            hashMapShopMarker.put(key,mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).title(lName).snippet(key).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MainActivity.this,R.drawable.wrench,lName)))));
            hashMapBitmap.put(key,BitmapFactory.decodeResource(getResources(),R.drawable.wrench));
            convertSnapshotToShop(dataSnapshot);
        }

    LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : hashMapShopMarker.values()) {
        builder.include(marker.getPosition());
    }
    }

    //Function to initialize user Object
    private void userInit(Bundle savedInstanceState){
        //User Object Initialization
            uUser = new User(gUser.getEmail(), gUser.getDisplayName(), null, default_img, getDate());
            Log.d(TAG, " USER OBJECT CREATED : "+ gUser.toString());


    }

    //Create custom InfoWindow
    private void setInfoWindow(){
        if(mMap != null){
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){
                @Override
                public View getInfoWindow(Marker marker) {

                    View v = getLayoutInflater().inflate(R.layout.custom_info_win,null);
                    final TextView tvUserName = v.findViewById(R.id.userName);
                    final TextView profileText = v.findViewById(R.id.pointsText);
                    final CircleImageView userImage = v.findViewById(R.id.userImg);
                    String lPoints = "0";

                    tvUserName.setText(marker.getTitle());
                    userImage.setImageBitmap(hashMapBitmap.get(marker.getSnippet()));
                    if (hashMapUserObjects.containsKey(marker.getSnippet())){
                        lPoints = String.valueOf(hashMapUserObjects.get(marker.getSnippet()).getPoints());
                    } else if(hashMapShopObjects.containsKey(marker.getSnippet())){
                        lPoints = String.valueOf(hashMapShopObjects.get(marker.getSnippet()).getPoints());
                    }
                    profileText.setText(lPoints);
                    if(Integer.parseInt(lPoints) >= 1000){
                        profileText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.gold));
                    }
                    if(Integer.parseInt(lPoints) >= 200 && Integer.parseInt(lPoints) < 1000){
                        profileText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.silver));
                    }
                    else if(Integer.parseInt(lPoints) < 200){
                        profileText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.bronze));
                    }

                    return v;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }});
        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Bundle bundle = new Bundle();
                if(hashMapUserObjects.containsKey(marker.getSnippet())) {
                    bundle.putString("userImageUrl", hashMapUserObjects.get(marker.getSnippet()).getPhotoUrl());
                    bundle.putString("name", hashMapUserObjects.get(marker.getSnippet()).getName());
                    bundle.putString("key", marker.getSnippet().toString());
                    bundle.putInt("points", hashMapUserObjects.get(marker.getSnippet()).getPoints());
                    bundle.putString("date",hashMapUserObjects.get(marker.getSnippet()).getDate());
                    Intent loadDetails = new Intent(MainActivity.this, UserProfileActivity.class);
                    loadDetails.putExtras(bundle);
                    startActivity(loadDetails);
                }

                if(hashMapShopObjects.containsKey(marker.getSnippet())){
                    //launch shop profile
                }
            }
        });
    }

    //function to convert snapshot to User
    private void convertSnapshotToUser(DataSnapshot dataSnapshot){
        User lConvertUser = new User();
        lConvertUser.setName(dataSnapshot.child("name").getValue().toString());
        lConvertUser.setEmail(dataSnapshot.child("email").getValue().toString());
        lConvertUser.setPoints(Integer.parseInt(dataSnapshot.child("points").getValue().toString()));
        lConvertUser.setPhotoUrl(dataSnapshot.child("photoUrl").getValue().toString());
        hashMapUserObjects.put(dataSnapshot.getKey(),lConvertUser);
    }


    //function to convert snapshot to Shop
    private void convertSnapshotToShop(DataSnapshot dataSnapshot){
        Shop lConvertShop = new Shop();
        lConvertShop.setName(dataSnapshot.child("name").getValue().toString());
        lConvertShop.setPoints(Integer.parseInt(dataSnapshot.child("points").getValue().toString()));
        hashMapShopObjects.put(dataSnapshot.getKey(),lConvertShop);
    }

    private String getDate(){
        Date dateObject = new Date();
        String date = new SimpleDateFormat("MM/dd/yyyy").format(dateObject);
        return date;
    }

}
