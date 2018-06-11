package com.penguinsonabeach.tuun.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.penguinsonabeach.tuun.Adapter.PagerAdapterMyProfile;
import com.penguinsonabeach.tuun.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String URL;
    Bundle gBundle;


    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_myprofile);

        gBundle = getIntent().getExtras();

        CircleImageView circleImageView = findViewById(R.id.myProfileHeaderImg);
        URL = getIntent().getExtras().getString("photoUrl").toString();

        Glide.with(this)
                .load(URL)
                .apply(RequestOptions.circleCropTransform())
                .into(circleImageView);

        //Adding toolbar to the activity
        Toolbar toolbar = findViewById(R.id.toolbarMyProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing the tablayout
        tabLayout = findViewById(R.id.tabLayoutMyProfile);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home_icon));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.racing_icon));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = findViewById(R.id.pagerMyProfile);
        viewPager.setOffscreenPageLimit(2);

        //Creating our pager adapter
        PagerAdapterMyProfile adapter = new PagerAdapterMyProfile(getSupportFragmentManager(), tabLayout.getTabCount(), gBundle);

        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setPageTransformer(true, new RotateUpTransformer());

        //Adding onTabSelectedListener to swipe views
        tabLayout.addOnTabSelectedListener(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
