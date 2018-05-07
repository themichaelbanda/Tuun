package com.penguinsonabeach.tuun.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.penguinsonabeach.tuun.Fragment.ProfileInfoFragment;
import com.penguinsonabeach.tuun.Fragment.ProfileKillsFragment;
import com.penguinsonabeach.tuun.Fragment.UserInfoCarsFragment;
import com.penguinsonabeach.tuun.Fragment.UserInfoFragment;


public class PagerAdapterMyProfile extends FragmentStatePagerAdapter {
    Bundle bundle;
    //integer to count number of tabs
    int tabCount;
    ProfileInfoFragment profileInfoFragment;
    ProfileKillsFragment profileKillsFragment;

    //Constructor to the class
    public PagerAdapterMyProfile(FragmentManager fm, int tabCount, Bundle bundle) {
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
                profileInfoFragment = new ProfileInfoFragment();
                return profileInfoFragment.newInstance(bundle);

            case 1:
                profileKillsFragment = new ProfileKillsFragment();
                profileKillsFragment.setArguments(bundle);
                return profileKillsFragment.newInstance(bundle);

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

