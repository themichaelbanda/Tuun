package com.penguinsonabeach.tuun;

import android.location.Location;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Phoenix on 9/3/2017.
 */

@IgnoreExtraProperties
public class User {

    private String email;
    private String name;
    private Location location;
    private String photoUrl;

    // Constructors

    public User() {}
    public User(String email, String name, Location location, String photoUrl){
        this.email = email;
        this.name = name;
        this.location = location;
        this.photoUrl = photoUrl;
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

}