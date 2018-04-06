package com.penguinsonabeach.tuun.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.penguinsonabeach.tuun.Adapter.PagerAdapterGarage;
import com.penguinsonabeach.tuun.Adapter.PagerAdapterUser;
import com.penguinsonabeach.tuun.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Phoenix on 3/25/2018.
 */

public class GarageActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    String gName,gPoints,gKey, gUrl;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Bundle gBundle;


    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_garage);

        //Adding toolbar to the activity
        Toolbar toolbar = findViewById(R.id.toolbarGarage);
        setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = findViewById(R.id.tabLayoutGarage);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.car_icon));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.review_icon));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = findViewById(R.id.pagerGarage);
        viewPager.setOffscreenPageLimit(2);

        //Creating our pager adapter
        PagerAdapterGarage adapter = new PagerAdapterGarage(getSupportFragmentManager(), tabLayout.getTabCount(), gBundle);

        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setPageTransformer(true, new RotateUpTransformer());

        //Adding onTabSelectedListener to swipe views
        tabLayout.addOnTabSelectedListener(this);


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

