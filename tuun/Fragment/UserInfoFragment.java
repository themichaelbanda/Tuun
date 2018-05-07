package com.penguinsonabeach.tuun.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.penguinsonabeach.tuun.R;

/**
 * Created by Phoenix on 3/4/2018.
 */

public class UserInfoFragment extends Fragment {

    public static UserInfoFragment newInstance(Bundle arguments) {
        UserInfoFragment userTab1 = new UserInfoFragment();
        if (arguments != null) {
            userTab1.setArguments(arguments);
        }
        return userTab1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        String lPoints = String.valueOf(this.getArguments().getInt("points"));
        String lDate = String.valueOf(this.getArguments().getString("date"));
        String lClub = String.valueOf(this.getArguments().getString("club"));
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");
        View rootView = inflater.inflate(R.layout.fragment_user_home,container,false);

        TextView pTv = rootView.findViewById(R.id.infoPointsTitle);
        TextView pointsTv = rootView.findViewById(R.id.infoPointsValue);
        TextView cTv = rootView.findViewById(R.id.infoClubTitle);
        TextView clubTv = rootView.findViewById(R.id.infoClubValue);
        TextView dateTv = rootView.findViewById(R.id.infoJoinTitle);
        TextView joinDate = rootView.findViewById(R.id.infoJoinValue);
        pTv.setTypeface(customFont);
        cTv.setTypeface(customFont);
        dateTv.setTypeface(customFont);

        setPointsText(pointsTv, lPoints);
        clubTv.setText(lClub);
        joinDate.setText(lDate);

        return rootView;
    }

    protected void setPointsText(TextView tv, String lPoints){
        tv.setText(" " + lPoints);
        if(Integer.parseInt(lPoints) >= 1000){
            tv.setTextColor(ContextCompat.getColor(this.getContext(),R.color.gold));
        }
        if(Integer.parseInt(lPoints) >= 200 && Integer.parseInt(lPoints) < 1000){
            tv.setTextColor(ContextCompat.getColor(this.getContext(),R.color.silver));
        }
        else if(Integer.parseInt(lPoints) < 200){
            tv.setTextColor(ContextCompat.getColor(this.getContext(),R.color.bronze));
        }
    }
}
