package com.penguinsonabeach.tuun.Fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.penguinsonabeach.tuun.R;

public class ProfileKillsFragment extends Fragment {

    public static ProfileKillsFragment newInstance(Bundle arguments) {
        ProfileKillsFragment userTab2 = new ProfileKillsFragment();
        if (arguments != null) {
            userTab2.setArguments(arguments);
        }
        return userTab2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");
        View rootView = inflater.inflate(R.layout.fragment_profile_kills,container,false);




        return rootView;
    }
}
