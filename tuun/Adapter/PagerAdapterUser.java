package com.penguinsonabeach.tuun.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.penguinsonabeach.tuun.Fragment.UserInfoCarsFragment;
import com.penguinsonabeach.tuun.Fragment.UserInfoFragment;


public class PagerAdapterUser extends FragmentStatePagerAdapter {
    Bundle bundle;
    //integer to count number of tabs
    int tabCount;
    UserInfoFragment userInfoFragment;
    UserInfoCarsFragment userInfoCarsFragment;

    //Constructor to the class
    public PagerAdapterUser(FragmentManager fm, int tabCount, Bundle bundle) {
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
                userInfoFragment = new UserInfoFragment();
                return userInfoFragment.newInstance(bundle);

            case 1:
                userInfoCarsFragment = new UserInfoCarsFragment();
                return userInfoCarsFragment.newInstance(bundle);

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
