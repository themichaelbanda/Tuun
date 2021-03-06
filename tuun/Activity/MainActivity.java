package com.penguinsonabeach.tuun.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialize.holder.StringHolder;
import com.penguinsonabeach.tuun.Network.ConnectionLiveData;
import com.penguinsonabeach.tuun.Network.ConnectionModel;
import com.penguinsonabeach.tuun.Object.Meet;
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


@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, LifecycleRegistryOwner {
    public static final int MobileData = 2;
    public static final int WifiData = 1;
    private static final int dailyReward = 10;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int RC_PHOTO_PICKER = 2;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final double userSearchRadius = 60;
    private static final double userAttendRadius = .5;
    private static final String gUserDetailsRef = "users";
    private static final String gShopDetailsRef = "shops";
    private static final String gMeetsDetailsRef = "meets";
    private static final String gMeetsGeoRef = "meets-location";
    private static final String gUserOnlineGeoRef = "online-users";
    private static final String gUserPhotosStorageRef = "user_photos";
    private static final String gShopGeoRef = "shop-location";
    private static final String gUserNameDir = "usernames";
    private final static String KEY_LOCATION = "location";
    private static final String TAG = MainActivity.class.getSimpleName();
    private final String default_img = "https://firebasestorage.googleapis.com/v0/b/tuun-67689.appspot.com/o/user_photos%2Fno_icon.png?alt=media&token=85744938-bef8-4e56-bbbb-ab357393f8ae";
    private final Uri TermsURL= Uri.parse("https://firebasestorage.googleapis.com/v0/b/tuun-67689.appspot.com/o/General%2Ftermsofservice.html?alt=media&token=916b5868-3c2c-4195-8b57-692f1df8ff4e");
    private final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    private Boolean centerEnabled = TRUE;
    private Boolean connected = false;
    private int updateInterval = 0;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    private GeoLocation gLastLocation;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser gUser;
    private GoogleApiClient mGoogleApiClient;
    private ListView mDrawerList;
    private TextView mTitle;
    private TextView gSpeed;
    private CustomGauge gGauge;
    private ImageView gHazard;
    private ImageView gDisconnect;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private GoogleMap mMap;
    private User uUser;
    private ImageView profilePicture;
    private FirebaseDatabase database;
    private DatabaseReference geoRefUser;
    private DatabaseReference myRef;
    private DatabaseReference shopsRef;
    private DatabaseReference usersRef;
    private DatabaseReference meetsRef;
    private GeoFire geoFireUser, geoFireShop, geoFireMeet;
    private GeoQuery geoQueryUser, geoQueryShop, geoQueryMeet, geoQueryMeetAttend;
    private StorageReference gPhotoStorageRef;
    private ValueEventListener userLiveListener, userNamesListener, userDataListener;
    private GeoQueryEventListener userQueryListener, shopQueryListener, meetQueryListener, meetAttendQueryListener;
    private final HashMap<String, Marker> hashMapUserMarker = new HashMap<>();
    private final HashMap<String, Marker> hashMapShopMarker = new HashMap<>();
    private final HashMap<String, Marker> hashMapMeetMarker = new HashMap<>();
    private final HashMap<String, Bitmap> hashMapBitmap = new HashMap<>();
    private final HashMap<String, Shop> hashMapShopObjects = new HashMap<>();
    private final HashMap<String, Meet> hashMapMeetObjects = new HashMap<>();
    private final HashMap<String, User> hashMapUserObjects = new HashMap<>();
    private Typeface customFont;
    private Drawer result = null;
    private ProfileDrawerItem prof1;
    private AccountHeader headerResult;

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

        //Set view/font variables
        profilePicture = findViewById(R.id.profilePictureImg);
        mTitle = this.findViewById(R.id.hNameTextView);
        gSpeed = this.findViewById(R.id.speed);
        gGauge = this.findViewById(R.id.gauge);
        gHazard = this.findViewById(R.id.hazard);
        gDisconnect = this.findViewById(R.id.disconnected);
        customFont = Typeface.createFromAsset(this.getAssets(),"fonts/Capture_it.ttf");
        gSpeed.setTypeface(customFont);

        // Initialize Firebase Database and Storage tools
        setUpFirebase();

        //Set up listener for connection status
        checkConnection(this);

        // Database filtering
        //usersRef.limitToFirst(1000);

        // Build the map
        setUpMap();

        //admin function to add shops to map todo add to a javascript based webpage
        //addShop();

        // Google Auth function to initialize data import for user
        setGoogleAuthDetails();
        setUserActive();
        createDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {

            case R.id.action_support:
                sendCustSuppEmail();
                return true;

            case R.id.action_tos:
                goToTerms();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Listener for Network connection
    private void checkConnection(final Context context){
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(getApplicationContext());
        connectionLiveData.observe(this, new Observer<ConnectionModel>() {
            @Override
            public void onChanged(@Nullable ConnectionModel connection) {
                if (connection.getIsConnected()) {
                    if(!connected){
                    connected = true;
                    gDisconnect.setVisibility(View.INVISIBLE);
                    attachAuthStateListener();
                    checkIfFirstTime();
                    attachUserLiveListener();
                    Log.d("Connect: ","Now");
                    switch (connection.getType()) {
                        case WifiData:
                            launchConnectedToast("WiFi Connected");
                            break;
                        case MobileData:
                            launchConnectedToast("Mobile Network Connected");
                            break;
                    }
                    }
                } else {
                    connected = false;
                    gDisconnect.setVisibility(View.VISIBLE);
                    launchDisconnectedToast();
                    detachDatabaseReadListener();
                    }
            }
        });
    }

    /* required to make activity life cycle owner */
    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
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
            final StorageReference photoRef = gPhotoStorageRef.child(gUser.getUid().concat(".jpg"));

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
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                moveTaskToBack(true);
                return true;
            }
        }
            return super.onKeyDown(keyCode, event);
    }

    private void createDrawer(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

       PrimaryDrawerItem item0 = new PrimaryDrawerItem().withIdentifier(0).withName(R.string.drawer_item_home);
       PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_profile);
       SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_garage);
       SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_messages);
       SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(4).withName(R.string.drawer_item_leaderboard);
       SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.drawer_item_donate);
       SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(6).withName(R.string.drawer_item_support);
       SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(7).withName(R.string.drawer_item_logout);
       prof1 = new ProfileDrawerItem().withIdentifier(8).withName(mAuth.getCurrentUser().getDisplayName()).withEmail(mAuth.getCurrentUser().getEmail()).withIcon(getResources().getDrawable(R.drawable.no_icon));


        headerResult = new AccountHeaderBuilder()
               .withActivity(this)
               .withHeaderBackground(R.drawable.header)
               .addProfiles(
                       prof1
               )
               .build();
       result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        item3,
                        item4,
                        item5,
                        item6,
                        item7
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 0:

                                break;
                            case 1:
                                if(connected){
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name",uUser.getName());
                                    bundle.putString("username",uUser.getUserName());
                                    bundle.putString("joindate",uUser.getDate());
                                    bundle.putString("photoUrl",uUser.getPhotoUrl());
                                    bundle.putInt("points",uUser.getPoints());
                                    bundle.putString("club",uUser.getClub());
                                    Intent profileIntent = new Intent(MainActivity.this, MyProfileActivity.class);
                                    profileIntent.putExtras(bundle);
                                    startActivity(profileIntent);
                                }
                                else{
                                    createNetworkAlert();
                                }
                                break;
                            case 3:
                                if(connected) {
                                    Intent garageIntent = new Intent(MainActivity.this, GarageActivity.class);
                                    startActivity(garageIntent);
                                }else {
                                    createNetworkAlert();
                                }
                                break;
                            case 4:
                                if(connected) {
                                    Intent messageIntent = new Intent(MainActivity.this, MessagesActivity.class);
                                    startActivity(messageIntent);
                                }else {
                                    createNetworkAlert();
                                }
                                break;
                            case 5:
                                if(connected) {
                                    Intent leaderIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
                                    startActivity(leaderIntent);
                                }else {
                                    createNetworkAlert();
                                }
                                break;
                            case 7:
                                if(connected) {
                                    sendCustSuppEmail();
                                }else {
                                    createNetworkAlert();
                                }
                                break;
                            case 8:
                                signOut();
                                break;
                            default:
                                Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                                break;

                        }

                        return true;
                    }
                })
                .build();
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
        //mDrawerToggle.syncState(); ToDo
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //mDrawerToggle.onConfigurationChanged(newConfig); TODO
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

    @Override
    public void onBackPressed() {

        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void sendCustSuppEmail() {

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"customersupport@penguinsonabeach.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TuuN Customer Support");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "What can we help you with?");

        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    //Create Alert Dialog for Network error
    private void createNetworkAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.network_title);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(R.string.network_message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();
    }

    //Call to go to webpage containing Terms of Service
    private void goToTerms(){
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, TermsURL);
        startActivity(launchBrowser);
    }

    // Method to handle appropriate actions when user signs out
    private void signOut() {
        myRef.child("online").setValue("False");
        if(!getFusedLocationProviderClient(this).equals(null)){
        getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback); }
        FirebaseAuth.getInstance().signOut();
        Intent sOut = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(sOut);
    }

    //Method called when permissions are denied in order to force user out of application
    private void forceOut(){
        myRef.child("online").setValue("False");
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
        meetsRef = database.getReference(gMeetsDetailsRef);
        DatabaseReference userNamesRef = database.getReference(gUserNameDir);
        shopsRef = database.getReference(gShopDetailsRef);
        myRef = usersRef.child(mAuth.getCurrentUser().getUid());


        // Initialize Geofire references
        geoRefUser = database.getReference(gUserOnlineGeoRef);
        DatabaseReference geoRefShop = database.getReference(gShopGeoRef);
        DatabaseReference geoRefMeet = database.getReference(gMeetsGeoRef);
        geoFireUser = new GeoFire(geoRefUser);
        geoFireShop = new GeoFire(geoRefShop);
        geoFireMeet = new GeoFire(geoRefMeet);

        // Storage reference instantiation for Image URL
        gPhotoStorageRef = mFirebaseStorage.getReference().child(gUserPhotosStorageRef).child(gUser.getUid());

        // Set up notification subscription for messaging
        FirebaseMessaging.getInstance().subscribeToTopic("notifications" );
    }

    private void checkIfFirstTime(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("name").exists()){
                    //User does not exist
                    usersRef.child(gUser.getUid()).setValue(new User(gUser.getEmail(), gUser.getDisplayName(),null, default_img,getDate(),"N/A"));
                }
                else{
                    //Set Object from DB
                    uUser = dataSnapshot.getValue(User.class);
                    updateLoginDate();

                    //Use gathered data to set items TODO
                    //mTitle.setText(uUser.getName());
                    //mTitle.setTypeface(customFont);
                    /*if (uUser.getPhotoUrl() != null) {
                        Glide.with(profilePicture.getContext())
                                .load(uUser.getPhotoUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePicture);
                    } else {
                        profilePicture.setImageResource(R.drawable.no_icon);
                    }*/
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void attachUserLiveListener(){
        if(userLiveListener == null) {
            userLiveListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //Set Object from DB
                    uUser = dataSnapshot.getValue(User.class);

                    /*if (uUser.getPhotoUrl() != null) {  TODO
                        Glide.with(profilePicture.getContext())
                                .load(uUser.getPhotoUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePicture);
                    } else {
                        profilePicture.setImageResource(R.drawable.no_icon);
                    }*/

                    if (!dataSnapshot.child("username").exists()) {
                        //mTitle.setText(uUser.getName());
                        prof1.withName(uUser.getUserName()).withEmail(uUser.getEmail()).withIcon(uUser.getPhotoUrl());
                        headerResult.updateProfile(prof1);
                        //result.updateName(8,new com.mikepenz.materialdrawer.holder.StringHolder(uUser.getUserName()));
                    }
                    else{
                        //.setText(uUser.getUserName());
                        prof1.withName(uUser.getName()).withEmail(uUser.getEmail()).withIcon(uUser.getPhotoUrl());
                        headerResult.updateProfile(prof1);
                        //result.updateName(8, new com.mikepenz.materialdrawer.holder.StringHolder(uUser.getName()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error" + databaseError);
                }
            };

            myRef.addValueEventListener(userLiveListener);
        }

    }

    private void detachDatabaseReadListener() {
        if (userLiveListener != null) {
            myRef.removeEventListener(userLiveListener);
            userLiveListener = null;
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

    private void attachUserHazardListener(final String key){

        userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hashMapUserObjects.get(key).setHazard((Boolean) dataSnapshot.child("hazard").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersRef.child(key).addValueEventListener(userDataListener);

    }

    private void detachUserHazardListener(final String key){
        if (userDataListener != null) {
            usersRef.removeEventListener(userDataListener);
            userDataListener = null;
        }
    }

    //Set user as active in firebase db
    private void setUserActive() {
        myRef.child("online").setValue("True");
        myRef.child("online").onDisconnect().setValue("False");
        myRef.child("hazard").onDisconnect().setValue(false);
        //geoRefUser.child(mAuth.getUid()).onDisconnect().removeValue();

    }

    // Toggle hazard on and off
    public void toggleHazard(View v){
        if(uUser.getHazard()){
            uUser.setHazard(false);
            myRef.child("hazard").setValue(false);
            gHazard.setVisibility(View.INVISIBLE);
            gHazard.clearAnimation();
        }
        else{
            uUser.setHazard(true);
            myRef.child("hazard").setValue(true);
            gHazard.setVisibility(View.VISIBLE);
            Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(1000);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            gHazard.startAnimation(animation);

        }
    }

    //Set user Location in firebase db
    private void setUserLocation(Location location) {
        if (uUser != null) {
            uUser.setLocation(mLastLocation);
            myRef.child("latitude").setValue(location.getLatitude());
            myRef.child("longitude").setValue(location.getLongitude());
            geoFireUser.setLocation(mAuth.getCurrentUser().getUid(), new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            //geoFireShop.setLocation(mAuth.getCurrentUser().getUid(),new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    }

    //Set user speed in firebase db
    private void setUserSpeed(Location location){
        String speed = convertToMPH(location.getSpeed());
        gSpeed.setText(speed + " MPH");
        gGauge.setValue(Integer.valueOf(speed));
        checkUserTopSpeed(Integer.valueOf(speed));
    }

    //Calculate if new top speed and update firebase db
    private void checkUserTopSpeed(final int speed){
        if(uUser != null) {
            if ((speed > uUser.getTopSpeed()) && (speed < 220)) {
                myRef.child("topSpeed").setValue(speed);
            }
        }
        }

    private void removeGeoOnDiscon(){
        geoRefUser.child(gUser.getUid()).onDisconnect().removeValue();
    }

    private void setGeoQueryUser(Location location){
        if(geoQueryUser == null){
            geoQueryUser = geoFireUser.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()), userSearchRadius);
        }
        else{
            geoQueryUser.setCenter(new GeoLocation(location.getLatitude(),location.getLongitude()));
        }
    }

    private void setGeoQueryShop(Location location){
        if(geoQueryShop == null){
            geoQueryShop = geoFireShop.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()), userSearchRadius);
        }
        else{
            geoQueryShop.setCenter(new GeoLocation(location.getLatitude(),location.getLongitude()));
        }
    }

    private void setGeoQueryMeet(Location location){
        if(geoQueryMeet == null){
            geoQueryMeet = geoFireMeet.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()), userSearchRadius);
        }
        else{
            geoQueryMeet.setCenter(new GeoLocation(location.getLatitude(),location.getLongitude()));
        }
    }

    private void setGeoQueryMeetAttend(Location location){
        if(geoQueryMeetAttend == null){
            geoQueryMeetAttend = geoFireMeet.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()), userAttendRadius);
        }
        else{
            geoQueryMeetAttend.setCenter(new GeoLocation(location.getLatitude(),location.getLongitude()));
        }
    }

    private void updateLoginDate(){
        if(uUser.getLastlogin() != null) {
            if (!uUser.getLastlogin().equals(getDate())) {
                uUser.setLastlogin(getDate());
                myRef.child("lastlogin").setValue(uUser.getLastlogin()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        launchPointsToast();
                    }
                });
            }
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

        //Listen for movement away from center by user
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    centerEnabled=FALSE;
                    Log.d("Center Enabled: ", centerEnabled.toString());
                }
            }
        });
        //Listen for centering button and follow if true
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                centerEnabled = TRUE;
                return false;
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
        }
        else {
            getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    },
                    Looper.myLooper());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        // GPS may be turned off
        if (location == null) {
            Log.d(TAG,"OnLocationChanged : Location is Null");
            Toast.makeText(this,"GPS may be turned off, Turn on GPS.", Toast.LENGTH_SHORT).show();
            return;
        }

        mLastLocation = location;
        //ensure network availability prior to making network calls
        if(connected) {
            if (updateInterval % 2 == 0) {
                //inner slower update interval method calls
                setUserLocation(location);
                setGeoQueryUser(location);
                setGeoQueryShop(location);
                setGeoQueryMeet(location);
                setGeoQueryMeetAttend(location);
                fetchNearbyUsers();
                fetchNearbyShops();
                fetchNearbyMeets();
                attendNearbyMeets();
            }
            //outer faster update interval method calls
            setUserSpeed(location);
            updateInterval++;
        }

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
        double mphMultiplier = 2.23694;
        mph = speed * mphMultiplier;
        return String.valueOf(Math.round(mph));
    }


    /******************************/
    /*
    /*    Marker Related Functions
    /*
    /******************************/

    //Function to run GeoQuery to find users within radius %1
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
                        detachUserHazardListener(key);

                    }
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    if(hashMapUserMarker.get(key)!= null) {
                        LatLng latLng = new LatLng(location.latitude, location.longitude);
                        hashMapUserMarker.get(key).setPosition(latLng);
                        if(hashMapUserObjects.get(key).getHazard()){
                            hashMapUserMarker.get(key).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MainActivity.this,R.drawable.hazard,hashMapUserObjects.get(key).getName())));
                        }
                        else if(!hashMapUserObjects.get(key).getHazard()){
                            setCustomIcon(MainActivity.this, hashMapUserObjects.get(key).getPhotoUrl(), hashMapUserObjects.get(key).getName(), hashMapUserMarker.get(key));
                        }

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

    //Function to run GeoQuery to find shops within radius %1
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

    //Function to run GeoQuery to find meets within radius %1
    private void fetchNearbyMeets(){
        if (meetQueryListener == null) {
            meetQueryListener = new GeoQueryEventListener() {

                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    getMeetMarkerDetails(key);
                    Log.d("Meets Geofire: ", key);
                }

                @Override
                public void onKeyExited(String key) {
                    // Remove any old marker
                    Marker marker = hashMapMeetMarker.get(key);
                    if (marker != null) {
                        marker.remove();
                        hashMapMeetMarker.remove(key);
                        hashMapMeetObjects.remove(key);
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
            geoQueryMeet.addGeoQueryEventListener(meetQueryListener);
        }
    }

    //Function to run GeoQuery to find if within meets range
    private void attendNearbyMeets(){
        if (meetAttendQueryListener == null) {
            meetAttendQueryListener = new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    meetsRef.child(key).child("users").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getDisplayName());
                    myRef.child("meet").child(key).setValue(location);
                    Log.d("Meet attendance: ", key);
                }

                @Override
                public void onKeyExited(String key) {
                    meetsRef.child(key).child("users").child(mAuth.getCurrentUser().getUid()).removeValue();
                    myRef.child("meet").child(key).removeValue();

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
            geoQueryMeetAttend.addGeoQueryEventListener(meetAttendQueryListener);
        }
    }


    /* admin level function */
    private void addShop(){
        GeoLocation shopLoc = new GeoLocation(33.044959,-96.973157);
        DatabaseReference shopRef = database.getReference("shops");
        String lKey = shopRef.child("shops").push().getKey();
        Shop shop = new Shop(shopLoc, "Evolution Dynamics", "tuner", lKey);
        shopRef.child(lKey).setValue(shop);

        geoFireShop.setLocation(lKey,shopLoc);
    }

    //Add meet
    private void addNewMeet(final Meet meet){

        GeoQuery geoQuery = geoFireMeet.queryAtLocation(new GeoLocation(meet.latitude,meet.longitude),10);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            final HashMap<String,GeoLocation> temp = new HashMap<>();
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
            temp.put(dataSnapshot.getKey(),location);
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(temp.isEmpty()){
                    String lmeetId = meetsRef.push().getKey();
                    geoFireMeet.setLocation(lmeetId,new GeoLocation(meet.latitude,meet.longitude));
                    meetsRef.child(lmeetId).setValue(meet);
                    launchMeetNameToast(meet.getName()+" Created!");

                }
                else{
                    launchMeetNameToast("There is already a meet nearby!");
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    //Function to get user details from Firebase DB
    private void getUserMarkerDetails(final String key){

        Query query = usersRef.orderByKey().equalTo(key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot person : dataSnapshot.getChildren()){
                    setUserMarker(person);
                    Log.d("User Details", key);
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
                    Log.d("Shop Details", key);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO handle if canceled
            }
        });
    }

    //Function to get meet details from Firebase DB
    private void getMeetMarkerDetails(final String key){

        Query query = meetsRef.orderByKey().equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot meet : dataSnapshot.getChildren()){
                    setMeetMarker(meet);
                    Log.d("Meet Details", key);
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
        if(mAuth.getCurrentUser() != null) {
            String key = dataSnapshot.getKey();
            String name;
            double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
            double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
            LatLng location = new LatLng(lat, lng);

            if (dataSnapshot.child("username").getValue() == null) {
                name = dataSnapshot.child("name").getValue().toString();
            } else {
                name = dataSnapshot.child("username").getValue().toString();
            }


            // If condition to check that User data loaded is not your own/this is your first time being loaded to map/User is online :: Add marker to map
            if ((!hashMapUserMarker.containsKey(key)) && (!key.equals(mAuth.getCurrentUser().getUid())) && ((dataSnapshot.child("online").getValue().equals("True")))) {
                hashMapUserMarker.put(key, mMap.addMarker(new MarkerOptions().title(name).position(location).snippet(key).icon(BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(this, R.drawable.no_icon, dataSnapshot.child("name").getValue().toString())))));
                setCustomIcon(this, dataSnapshot.child("photoUrl").getValue().toString(), name, hashMapUserMarker.get(key));
                convertSnapshotToUser(dataSnapshot);
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : hashMapUserMarker.values()) {
                builder.include(marker.getPosition());
            }
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

    //Create the meet marker from data retrieved and set to map
    private void setMeetMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in hashMapShopMarker, which contains all the markers
        // for locations received
        String key = dataSnapshot.getKey();
        String mName = dataSnapshot.child("name").getValue().toString();
        double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
        double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
        LatLng location = new LatLng(lat, lng);
        Log.d("SetMeetMarker",key);

            hashMapMeetMarker.put(key, mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).title(mName).snippet(key).icon(BitmapDescriptorFactory.fromBitmap(createCustomMeetMarker(MainActivity.this, mName)))));
            hashMapBitmap.put(key,BitmapFactory.decodeResource(getResources(),R.mipmap.ic_group_blue));
            convertSnapshotToMeet(dataSnapshot);


        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : hashMapMeetMarker.values()) {
            builder.include(marker.getPosition());
        }
    }

    //Create custom InfoWindow
    private void setInfoWindow(){
        if(mMap != null){
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){
                @Override
                public View getInfoWindow(Marker marker) {

                    View v = getLayoutInflater().inflate(R.layout.custom_info_win,null);
                    final TextView tvUserName = v.findViewById(R.id.userName);
                    final TextView pointsText = v.findViewById(R.id.pointsText);
                    final CircleImageView userImage = v.findViewById(R.id.userImg);
                    String lPoints = "0";

                    tvUserName.setText(marker.getTitle());
                    userImage.setImageBitmap(hashMapBitmap.get(marker.getSnippet()));
                    if(hashMapUserObjects.containsKey(marker.getSnippet())){
                        lPoints = String.valueOf(hashMapUserObjects.get(marker.getSnippet()).getPoints());
                        pointsText.setText(lPoints);
                        if(Integer.parseInt(lPoints) >= 1000){
                            pointsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.gold));
                        }
                        if(Integer.parseInt(lPoints) >= 200 && Integer.parseInt(lPoints) < 1000){
                            pointsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.silver));
                        }
                        else if(Integer.parseInt(lPoints) < 200){
                            pointsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.bronze));
                        }

                    }if(hashMapShopObjects.containsKey(marker.getSnippet())){
                        lPoints = String.valueOf(hashMapShopObjects.get(marker.getSnippet()).getPoints());

                        pointsText.setText(lPoints);
                        if(Integer.parseInt(lPoints) >= 1000){
                            pointsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.gold));
                        }
                        if(Integer.parseInt(lPoints) >= 200 && Integer.parseInt(lPoints) < 1000){
                            pointsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.silver));
                        }
                        else if(Integer.parseInt(lPoints) < 200){
                            pointsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.bronze));
                        }
                    }if(hashMapMeetObjects.containsKey(marker.getSnippet())){
                        lPoints = String.valueOf(hashMapMeetObjects.get(marker.getSnippet()).getCount());

                        pointsText.setText(lPoints);
                        if(Integer.parseInt(lPoints) >= 40){
                            pointsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.gold));
                        }
                        if(Integer.parseInt(lPoints) >= 20 && Integer.parseInt(lPoints) < 40){
                            pointsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.silver));
                        }
                        else if(Integer.parseInt(lPoints) < 20){
                            pointsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.bronze));
                        }
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
                    if(hashMapUserObjects.get(marker.getSnippet()).getUserName() != null){
                    bundle.putString("name", hashMapUserObjects.get(marker.getSnippet()).getUserName());}
                    else{bundle.putString("name", hashMapUserObjects.get(marker.getSnippet()).getName());}
                    bundle.putString("key", marker.getSnippet());
                    bundle.putInt("points", hashMapUserObjects.get(marker.getSnippet()).getPoints());
                    bundle.putString("date",hashMapUserObjects.get(marker.getSnippet()).getDate());
                    bundle.putString("club",hashMapUserObjects.get(marker.getSnippet()).getClub());
                    Intent loadDetails = new Intent(MainActivity.this, UserProfileActivity.class);
                    loadDetails.putExtras(bundle);
                    startActivity(loadDetails);
                }

                if(hashMapShopObjects.containsKey(marker.getSnippet())){
                    //launch shop profile
                }

                if(hashMapMeetObjects.containsKey(marker.getSnippet())){
                  //launch meet profile
                }
            }
        });
    }

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

    private static Bitmap createCustomMeetMarker(Context context, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_meet_marker, null);

        CircleImageView markerImage = marker.findViewById(R.id.meet_dp);
        markerImage.setImageResource(R.mipmap.ic_group_blue);
        TextView txt_name = marker.findViewById(R.id.meetname);
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

    //function to convert snapshot to User
    private void convertSnapshotToUser(DataSnapshot dataSnapshot){
        User lConvertUser = new User();
        lConvertUser.setName(dataSnapshot.child("name").getValue().toString());
        lConvertUser.setEmail(dataSnapshot.child("email").getValue().toString());
        lConvertUser.setPoints(Integer.parseInt(dataSnapshot.child("points").getValue().toString()));
        lConvertUser.setPhotoUrl(dataSnapshot.child("photoUrl").getValue().toString());
        lConvertUser.setDate(dataSnapshot.child("date").getValue().toString());
        lConvertUser.setClub(dataSnapshot.child("club").getValue().toString());
        lConvertUser.setHazard(((Boolean) dataSnapshot.child("hazard").getValue()));
        hashMapUserObjects.put(dataSnapshot.getKey(),lConvertUser);
        attachUserHazardListener(dataSnapshot.getKey());
    }

    //function to convert snapshot to Shop
    private void convertSnapshotToShop(DataSnapshot dataSnapshot){
        Shop lConvertShop = new Shop();
        lConvertShop.setName(dataSnapshot.child("name").getValue().toString());
        lConvertShop.setPoints(Integer.parseInt(dataSnapshot.child("points").getValue().toString()));
        hashMapShopObjects.put(dataSnapshot.getKey(),lConvertShop);
    }

    //function to convert snapshot to Meet
    private void convertSnapshotToMeet(DataSnapshot dataSnapshot){
        Meet lConvertMeet = new Meet();
        lConvertMeet.setName(dataSnapshot.child("name").getValue().toString());
        lConvertMeet.setCount((int) dataSnapshot.child("users").getChildrenCount());
        hashMapMeetObjects.put(dataSnapshot.getKey(),lConvertMeet);
    }

    //function to calculate and format current date. Used to set user join date.
    public String getDate(){
        Date dateObject = new Date();
        return new SimpleDateFormat("MM/dd/yyyy").format(dateObject);
    }

    //Application Custom Toasts
    private void launchPointsToast(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout));
        ImageView image = layout.findViewById(R.id.toastimage);
        image.setImageResource(R.drawable.trophy_icon);
        TextView text = layout.findViewById(R.id.toasttext);
        text.setText("Daily reward : "+ MainActivity.dailyReward +" points");
        text.setTypeface(customFont);


        Toast pToast = new Toast(getBaseContext());
        pToast.setGravity(Gravity.BOTTOM, 0, 350);
        pToast.setDuration(Toast.LENGTH_LONG);
        pToast.setView(layout);
        pToast.show();

    }

    private void launchMeetNameToast(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) this.findViewById(R.id.toast_layout));
        layout.setBackgroundResource(R.drawable.borderconnection);
        ImageView image = layout.findViewById(R.id.toastimage);
        image.setImageResource(R.mipmap.ic_group_blue);
        TextView text = layout.findViewById(R.id.toasttext);
        text.setText(message);
        text.setTypeface(customFont);


        Toast pToast = new Toast(this);
        pToast.setGravity(Gravity.TOP, 0, 250);
        pToast.setDuration(Toast.LENGTH_SHORT);
        pToast.setView(layout);
        pToast.show();
    }

    private void launchConnectedToast(String connection){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout));
        layout.setBackgroundResource(R.drawable.borderconnection);
        ImageView image = layout.findViewById(R.id.toastimage);
        image.setImageResource(R.mipmap.ic_connected);
        TextView text = layout.findViewById(R.id.toasttext);
        text.setText(connection);
        text.setTypeface(customFont);


        Toast pToast = new Toast(getBaseContext());
        pToast.setGravity(Gravity.TOP, 0, 250);
        pToast.setDuration(Toast.LENGTH_LONG);
        pToast.setView(layout);
        pToast.show();
    }

    private void launchDisconnectedToast(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout));
        layout.setBackgroundResource(R.drawable.borderconnection);
        ImageView image = layout.findViewById(R.id.toastimage);
        image.setImageResource(R.mipmap.ic_disconnected);
        TextView text = layout.findViewById(R.id.toasttext);
        text.setText("No Network Found!");
        text.setTypeface(customFont);


        Toast pToast = new Toast(getBaseContext());
        pToast.setGravity(Gravity.TOP, 0, 250);
        pToast.setDuration(Toast.LENGTH_LONG);
        pToast.setView(layout);
        pToast.show();
    }

    public void createMeetPopup(View view){

        final PopupWindow mPopupWindow;
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popup_create_meet,null);

        // Get a reference for the layout within popup window
        LinearLayout linearLayout1 = customView.findViewById(R.id.linearLayout1);

        // Get a reference for the layout within popup window
        final EditText meetNameEditText = customView.findViewById(R.id.editTextMeetName);
        final TextView meetNameTextView = customView.findViewById(R.id.meetNameTitleTv);
        meetNameTextView.setTypeface(customFont);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setElevation(5.0f);

        // Get a reference for the custom view close button
        final Button verifyMeetButton =  customView.findViewById(R.id.verifyMeetButton);


        // Set a click listener for the popup window close button
        verifyMeetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(meetNameEditText.getText().toString().length() < 1){
                    launchMeetNameToast("Meet Name Cannot Be Empty!");
                    return;
                }else {
                    GeoLocation geolocation = new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                    Meet meet = new Meet(geolocation.latitude,geolocation.longitude, meetNameEditText.getText().toString());
                    addNewMeet(meet);
                    // Dismiss the popup window
                }
                mPopupWindow.dismiss();

            }
        });
        mPopupWindow.showAtLocation(linearLayout1, Gravity.CENTER,0,0);
    }



}
