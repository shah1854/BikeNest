package com.example.bicycleparkingproject;

public class BikeRack {
    private String location;
    private String id;

    public BikeRack(String id, String location) {
        this.location = location;
        this.id = id;
    }

    public BikeRack() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "BikeRack{" +
                "location='" + location + '\'' +
                ", id=" + id +
                '}';
    }
}
