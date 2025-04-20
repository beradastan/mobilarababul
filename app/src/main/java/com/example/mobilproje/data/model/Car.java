package com.example.mobilproje.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

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
public class Car implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int brandId;
    public String model;
    public int year;
    public int km;
    public int price;
    public String description;
    public String imageUri;

    public Car(int brandId, String model, int year, int km, int price, String description, String imageUri) {
        this.brandId = brandId;
        this.model = model;
        this.year = year;
        this.km = km;
        this.price = price;
        this.description = description;
        this.imageUri = imageUri;
    }
}
