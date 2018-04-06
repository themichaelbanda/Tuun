package com.penguinsonabeach.tuun.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.penguinsonabeach.tuun.Object.Car;
import com.penguinsonabeach.tuun.R;

import java.util.ArrayList;

/**
 * Created by Michael Banda
 *  Adapter for RecycleView
 */

public class CarsRecycleViewAdapter extends RecyclerView.Adapter<CarsRecycleViewAdapter.CarViewHolder> {

    private final ArrayList<Car> cars;
    private final Context mContext;
    private CustomClickListener onClick;


    public interface CustomClickListener {
        void onCarClicked(int position);
    }


    public CarsRecycleViewAdapter(ArrayList<Car> cars, Context mContext){
        this.cars = cars;
        this.mContext = mContext;
    }

    @Override
    public CarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_card, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CarViewHolder holder, int position) {
        Car currentCar = cars.get(holder.getAdapterPosition());
        Boolean vehicleVerified = currentCar.getVerified();
        holder.vehicle.setText(currentCar.getYear()+" "+currentCar.getMake()+" "+currentCar.getModel());
        holder.trim.setText(currentCar.getTrim());
        holder.mods.setText(currentCar.getMods());
        if(!vehicleVerified){holder.verifiedIcon.setVisibility(View.GONE);}

        if(currentCar.getPhotoUrl() != null){
        Glide.with(mContext)
                .load(currentCar.getPhotoUrl())
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .apply(RequestOptions.circleCropTransform())
                        .override(75, 75))
                .into(holder.vehicleThumbnail);}

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClick.onCarClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {

        final TextView vehicle, trim, mods;
        final ImageView vehicleThumbnail;
        final ImageView verifiedIcon;

        CarViewHolder(View itemView) {
            super(itemView);
            vehicle = itemView.findViewById(R.id.Vehicle);
            trim = itemView.findViewById(R.id.VehicleTrim);
            mods = itemView.findViewById(R.id.VehicleMods);
            vehicleThumbnail =itemView.findViewById(R.id.car_thumbnail);
            verifiedIcon = itemView.findViewById(R.id.verified);
        }

    }

    public void setOnClick(CustomClickListener onClick)
    {
        this.onClick=onClick;
    }


}