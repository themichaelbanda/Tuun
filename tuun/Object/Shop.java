package com.penguinsonabeach.tuun.Object;

import android.location.Location;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by Phoenix on 9/3/2017.
 */

@IgnoreExtraProperties
public class Shop {

    private GeoLocation location;
    private String name;
    private int points;
    private String specialty;
    private String id;


    // Constructors

    public Shop() {}
    public Shop(GeoLocation location, String name, String specialty, String id){
        this.location = location;
        this.points = 0;
        this.name = name;
        this.specialty = specialty;
        this.id = id;
    }

    // Getters and Setters

    public GeoLocation getLocation(){return location;}

    public void setLocation(GeoLocation location){this.location = location;}

    public String getName(){return name;}

    public void setName(String name){this.name = name;}

    public int getPoints(){return points;}

    public void setPoints(int points){this.points = points;}

    public String getSpecialty(){return specialty;}

    public void setSpecialty(String specialty){this.specialty = specialty;}

    public String toString(){String userString = "Shop toString : " + location;
        return userString;}

}