package com.penguinsonabeach.tuun;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Phoenix on 1/16/2018.
 */

public class TuunUsers implements ClusterItem {

    final String title;
    final LatLng latLng;
    final String uid;
    final String URL;
    Bitmap bitmap;

    public TuunUsers(LatLng latLng, String title, String uid, String URL) {
        this.title = title;
        this.latLng = latLng;
        this.uid = uid;
        this.URL = URL;
    }

    public Bitmap getBitmap(){return bitmap;}

    public void setBitmap(Bitmap bitmap){this.bitmap = bitmap;}

    @Override public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
