package com.penguinsonabeach.tuun;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
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



/**
 * Created by Phoenix on 1/16/2018.
 */

public class CustomClusterRenderer extends DefaultClusterRenderer<TuunUsers> {

    private final Context mContext;
    private final IconGenerator mClusterIconGenerator;
    private final IconGenerator mIconGenerator;
    private final ImageView mImageView;
    private final int mDimension;



    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<TuunUsers> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext.getApplicationContext());
        mIconGenerator = new IconGenerator(mContext.getApplicationContext());

        mImageView = new ImageView(mContext);
        mDimension = (int) mContext.getResources().getDimension(R.dimen.custom_profile_image);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        int padding = (int) mContext.getResources().getDimension(R.dimen.custom_profile_padding);
        mImageView.setPadding(padding, padding, padding, padding);
        mIconGenerator.setContentView(mImageView);
    }
    @Override
    protected boolean shouldRenderAsCluster(Cluster<TuunUsers> cluster) {
        //start clustering if at least 2 items overlap
        //Change your logic here
        return cluster.getSize() > 1;
    }

    /**
     * Called before the marker for a ClusterItem has been added to the map.
     */

    @Override
    protected void onBeforeClusterItemRendered(TuunUsers item, final MarkerOptions markerOptions) {
        Bitmap bitmap = MainActivity.createCustomMarker(this.mContext, R.drawable.no_icon, item.getTitle());
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        markerOptions.icon(descriptor);
    }

    /**
     * Called after the marker for a ClusterItem has been added to the map.
     */

    @Override
    protected void onClusterItemRendered(final TuunUsers item, final Marker marker){
        final Marker markerTemp = marker;
        if(item != null) {
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

    @Override
    protected void onBeforeClusterRendered(Cluster<TuunUsers> cluster, MarkerOptions markerOptions) {

        mClusterIconGenerator.setBackground(ContextCompat.getDrawable(mContext, R.drawable.meetmod));

        mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);

        if(cluster.getSize() <= 9){
        final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));}

        else{final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf("+"));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));}
    }

    /**
     * Called after the marker for a Cluster has been added to the map.
     */
    //@Override
   // protected void onClusterRendered(Cluster<TuunUsers> cluster, Marker marker) {
    //}

}
