package com.penguinsonabeach.tuun.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.penguinsonabeach.tuun.Adapter.LeaderRecycleViewAdapter;
import com.penguinsonabeach.tuun.Object.User;
import com.penguinsonabeach.tuun.R;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {

    TextView title;
    private RecyclerView localLeadersRecycleView;
    private RecyclerView.Adapter speedAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseDatabase firebaseDatabase;
    private Query speedRankingQuery, pointsRankingQuery;
    private DatabaseReference rankingReference;
    private final ArrayList<User> speedUsers = new ArrayList<>();
    private final ArrayList<User> pointsUsers = new ArrayList<>();
    private User gUser;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_leaderboards);

        //Adding toolbar to the activity
        Toolbar toolbar = findViewById(R.id.toolbarLeaderboards);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        title = findViewById(R.id.leaderboardTitle);

        Typeface customFont = Typeface.createFromAsset(this.getAssets(),"fonts/Capture_it.ttf");
        title.setTypeface(customFont);

        localLeadersRecycleView = findViewById(R.id.speedLeaderRV);

        localLeadersRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        localLeadersRecycleView.setLayoutManager(mLayoutManager);

        speedAdapter = new LeaderRecycleViewAdapter(speedUsers,this);
        localLeadersRecycleView.setAdapter(speedAdapter);

        setUpFirebase();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        rankingReference = firebaseDatabase.getReference("users");
        speedRankingQuery = rankingReference.orderByChild("topSpeed").limitToLast(10);
        pointsRankingQuery = rankingReference.orderByChild("points").limitToLast(10);

        speedRankingQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                gUser = dataSnapshot.getValue(User.class);
                speedUsers.add(gUser);
                sortArray();
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
        pointsRankingQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                gUser = dataSnapshot.getValue(User.class);
                pointsUsers.add(gUser);
                //sortArray();
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

    private void sortArray(){

        Collections.sort(speedUsers);
        speedAdapter.notifyDataSetChanged();
    }

}
