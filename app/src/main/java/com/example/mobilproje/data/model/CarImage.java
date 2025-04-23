package com.example.mobilproje.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "car_images",
        foreignKeys = @ForeignKey(entity = Car.class,
                parentColumns = "id",
                childColumns = "carId",
                onDelete = ForeignKey.CASCADE))
public class CarImage {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int carId;

    @NonNull
    public byte[] imageData;
}
