package com.example.bicycleparkingproject;

public class BikeRack {
    private String location;
    private String id;
    private String address;

    public BikeRack(String id, String location, String address) {
        this.location = location;
        this.id = id;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BikeRack{" +
                "location='" + location + '\'' +
                ", id='" + id + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
