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
@TypeConverters(Converters.class) // en üstte ekle

public class Car implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int brandId;
    public String model;
    public int year;
    public int km;
    public int price;
    public String description;
    public List<String> imageUris; // Birden fazla fotoğrafın URI'ları
    public int userId;

    public Car(int brandId, String model, int year, int km, int price, String description, List<String> imageUris , int userId) {
        this.brandId = brandId;
        this.model = model;
        this.year = year;
        this.km = km;
        this.price = price;
        this.description = description;
        this.imageUris = imageUris;
        this.userId = userId;
    }


    // Getter methods
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

    public List<String> getImageUris() {
        return imageUris;
    }

    public int getBrandId() {
        return brandId;
    }

    public int getUserId(){
        return userId;
    }

    // You may also want setters if you are modifying these values
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

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }
}
