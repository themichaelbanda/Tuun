package com.penguinsonabeach.tuun.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.penguinsonabeach.tuun.Fragment.AddVehicleFragment;
import com.penguinsonabeach.tuun.Fragment.GarageCarsFragment;
import com.penguinsonabeach.tuun.Fragment.UserInfoFragment;


public class PagerAdapterGarage extends FragmentStatePagerAdapter {
    Bundle bundle;
    //integer to count number of tabs
    int tabCount;
    AddVehicleFragment addVehicleFragment;
    GarageCarsFragment garageCarsFragment;

    //Constructor to the class
    public PagerAdapterGarage(FragmentManager fm, int tabCount, Bundle bundle) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
        this.bundle = bundle;

    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                garageCarsFragment = new GarageCarsFragment();
                return garageCarsFragment.newInstance(bundle);

            case 1:
                addVehicleFragment = new AddVehicleFragment();
                return addVehicleFragment.newInstance(bundle);

            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


}