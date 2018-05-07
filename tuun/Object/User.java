package com.penguinsonabeach.tuun.Object;

import android.location.Location;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User implements Comparable<User>{

    private String email;
    private String name;
    private Location location;
    private String photoUrl;
    private int points;
    private String date;
    private String lastlogin;
    private int topSpeed;
    private String username;
    private String club;
    private String clubrank;
    private Boolean hazard;


    // Constructors

    public User() {}
    public User(String email, String name, Location location, String photoUrl, String date, String club){
        this.email = email;
        this.name = name;
        this.location = location;
        this.photoUrl = photoUrl;
        this.points = 10;
        this.date = date;
        this.club = club;
        this.clubrank = "N/A";
        this.topSpeed = 0;
        this.hazard = false;
        }

    // Getters and Setters

    public String getEmail() { return email;}

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName(){ return name;}

    public void setName(String name){this.name = name;}

    public String getUserName(){ return username;}

    public void setUserName(String userName){this.username = username;}

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

    public String getLastlogin(){return lastlogin;}

    public void setLastlogin(String lastlogin){this.lastlogin = lastlogin;}

    public String getClub(){return club;}

    public void setClub(String club){this.club = club;}

    public int getTopSpeed(){return topSpeed;}

    public void setTopSpeed(int topSpeed){this.topSpeed=topSpeed;}

    public String getClubrank(){return clubrank;}

    public void setClubrank(String clubrank){this.clubrank = clubrank;}

    public Boolean getHazard() {return hazard; }

    public void setHazard(Boolean hazard) {this.hazard = hazard; }

    public String toString(){String userString = "User toString : " + email + " , " + name +  " , " + location + " , " + photoUrl + " , " + points;
        return userString;}

    @Override
    public int compareTo(User compareUser) {

        int compareSpeed = compareUser.getTopSpeed();

        return compareSpeed - this.topSpeed;
    }

}