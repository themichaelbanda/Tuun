package com.penguinsonabeach.tuun.Object;

import com.firebase.geofire.GeoLocation;

public class Meet {
    public GeoLocation location;
    public double latitude;
    public double longitude;
    public String name;
    public int count;

    public Meet(){}
    public Meet(double latitude, double longitude, String name){
        this.location = location;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoLocation getLocation(){return location;}

    public void setLocation(GeoLocation location){this.location = location;}

    public String getName(){return name;}

    public void setName(String name){this.name = name;}

    public int getCount(){return count;}

    public void setCount(int count){this.count = count;}

}
