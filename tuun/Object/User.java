package com.penguinsonabeach.tuun.Object;

import android.location.Location;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by Phoenix on 9/3/2017.
 */

@IgnoreExtraProperties
public class User {

    private String email;
    private String name;
    private Location location;
    private String photoUrl;
    private int points;
    private String date;
    private String topSpeed;


    // Constructors

    public User() {}
    public User(String email, String name, Location location, String photoUrl, String date){
        this.email = email;
        this.name = name;
        this.location = location;
        this.photoUrl = photoUrl;
        this.points = 10;
        }

    // Getters and Setters

    public String getEmail() { return email;}

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName(){ return name;}

    public void setName(String name){this.name = name;}

    public Location getLocation(){return location;}

    public void setLocation(Location location){this.location = location;}

    public String getPhotoUrl() { return photoUrl;}

    public void setPhotoUrl(String photoUrl){ this.photoUrl = photoUrl;}

    public int getPoints(){return points;}

    public void setPoints(int points){
        this.points = points;
    }

    public String getDate(){return date;}

    public void setDate(String date){this.date=date;}

    public String getTopSpeed(){return topSpeed;}

    public void setTopSpeed(String topSpeed){this.topSpeed=topSpeed;}

    public String toString(){String userString = "User toString : " + email + " , " + name +  " , " + location + " , " + photoUrl + " , " + points;
        return userString;}

}