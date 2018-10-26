package com.liteteam.mahabaliuser;

public class UserLocation {

    public double latitude;
    public double longitude;
    public String rescueTeamId;

    public UserLocation(double latitude, double longitude, String rescueTeamId) {
        this(latitude, longitude);
        this.rescueTeamId = rescueTeamId;
    }

    public UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.rescueTeamId = null;
    }

    public UserLocation() {
        //necessary empty constructor
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

    public String getRescueTeamId() {
        return rescueTeamId;
    }

    public void setRescueTeamId(String rescueTeamId) {
        this.rescueTeamId = rescueTeamId;
    }
}
