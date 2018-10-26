package com.liteteam.mahabali;

public class TeamLocations {

    public double latitude;
    public double longitude;

    public TeamLocations() {
        //necessary empty public constructor
    }

    public TeamLocations(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "TeamLocations: [latitude = " + latitude + ", longitude = " + longitude + "]";
    }
}
