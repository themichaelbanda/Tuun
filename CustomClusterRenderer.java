package com.penguinsonabeach.tuun;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Trace;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import static android.content.ContentValues.TAG;

/**
 * Created by Phoenix on 1/16/2018.
 */

public class CustomClusterRenderer extends DefaultClusterRenderer<TuunUsers> {

    private final Context mContext;

    private final IconGenerator mClusterIconGenerator;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<TuunUsers> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext.getApplicationContext());
    }
    @Override
    protected boolean shouldRenderAsCluster(Cluster<TuunUsers> cluster) {
        //start clustering if at least 2 items overlap
        //Change your logic here
        return cluster.getSize() > 1;
    }

    @Override
    protected void onBeforeClusterItemRendered(TuunUsers item, final MarkerOptions markerOptions) {
        //final BitmapDescriptor markerDescriptor =
        //      BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);

        //markerOptions.icon(markerDescriptor).snippet(item.title);
        Bitmap bitmap = MainActivity.createCustomMarker(this.mContext, R.drawable.no_icon, item.getTitle());
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        markerOptions.icon(descriptor);
    }

    @Override protected void onBeforeClusterRendered(Cluster<TuunUsers> cluster, MarkerOptions markerOptions) {

        mClusterIconGenerator.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_circle));

        mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);

        final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
       markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }


    @Override
    protected void onClusterItemRendered(final TuunUsers item, final Marker marker){
        final Marker markerTemp = marker;
        Glide.with(mContext.getApplicationContext())
                .asBitmap()
                .load(item.URL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            markerTemp.setIcon(BitmapDescriptorFactory.fromBitmap(MainActivity.createCustomMarker(mContext, item.URL, item.getTitle(), bitmap)));
                    }
                });
    }

}
