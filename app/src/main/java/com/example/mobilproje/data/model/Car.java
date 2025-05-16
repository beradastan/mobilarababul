package com.example.mobilproje.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobilproje.data.database.Converters;

import java.io.Serializable;
import java.util.List;

@Entity(
        tableName = "cars",
        foreignKeys = @ForeignKey(
                entity = Brand.class,
                parentColumns = "id",
                childColumns = "brandId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("brandId")}
)
@TypeConverters(Converters.class)

public class Car implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int brandId;
    public String model;
    public int year;
    public int km;
    public int price;
    public String description;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String city;

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String transmissionType;
    public String fuelType;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String color;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String title;

    public List<String> getImageBase64List() {
        return imageBase64List;
    }

    public void setImageBase64List(List<String> imageBase64List) {
        this.imageBase64List = imageBase64List;
    }

    public List<String> imageBase64List;
    public int userId;

    public Car(int brandId, String model, int year, int km, int price, String description, List<String> imageBase64List, int userId , String title , String color , String transmissionType, String fuelType , String city) {
        this.brandId = brandId;
        this.model = model;
        this.year = year;
        this.km = km;
        this.price = price;
        this.description = description;
        this.imageBase64List = imageBase64List;
        this.userId = userId;
        this.title = title;
        this.color = color;
        this.transmissionType = transmissionType;
        this.fuelType = fuelType;
        this.city = city;
    }


    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public int getKm() {
        return km;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }



    public int getBrandId() {
        return brandId;
    }

    public int getUserId(){
        return userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }
}
