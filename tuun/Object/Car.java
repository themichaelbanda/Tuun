package com.penguinsonabeach.tuun.Object;

/**
 * Created by Phoenix on 3/18/2018.
 */

public class Car {
    String make,model,mods,year,photoUrl,vin,id,trim,series;
    Boolean verified;



    public Car(){}
    public Car(String year, String make, String model){
        this.year = year;
        this.make = make;
        this.model = model;
        verified = false;
    }

    public String getYear(){return year;}
    public void setYear(String year){this.year = year;}
    public String getMake(){return make;}
    public void setMake(String make){this.make = make;}
    public String getModel(){return model;}
    public void setModel(String model){this.model = model;}
    public String getMods(){return mods;}
    public void setMods(String mods){this.mods = mods;}
    public Boolean getVerified(){return verified;}
    public void setVerified(Boolean verified){this.verified = verified;}
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl){this.photoUrl = photoUrl;}
    public String getVin(){return vin;}
    public void setVin(String vin){this.vin = vin;}
    public String getId(){return id;}
    public void setId(String id){this.id = id;}
    public String getTrim(){return trim;}
    public void setTrim(String trim){this.trim = trim;}
    public String getSeries() { return series;}
    public void setSeries(String series){this.series=series;}

    @Override
    public String toString(){return year+" "+make+" "+model;}
}
