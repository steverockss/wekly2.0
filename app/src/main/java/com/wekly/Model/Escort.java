package com.wekly.Model;

import java.util.List;

public class Escort {
    String name, sex , description;
    String image;
   // int image2;
    int weight;
    int height;
    int price;
    int age;
    double lat, lng;

    public Escort(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Escort(String name, String race, int weight, String image,String description, int height, int price, int age, double lat, double lng) {
        this.name = name;
        this.description = description;
        this.sex = race;
        this.weight = weight;
        this.image = image;
        this.height = height;
        this.price = price;
        this.age = age;
        this.lat = lat;
        this.lng = lng;
    }
    public Escort  (){}


    public String getSex() {
        return sex;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }

    public String getRace() {
        return sex;
    }

    public int getWeight() {
        return weight;
    }

    public String getDescription() {
        return description;
    }


    public String getImage() {
        return image;
    }

    public int getHeight() {
        return height;
    }

    public int getPrice() {
        return price;
    }

    public int getAge() {
        return age;
    }


}
