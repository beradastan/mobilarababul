package com.example.mobilproje.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mobilproje.data.model.CarImage;

import java.util.List;

@Dao
public interface CarImageDao {

    @Insert
    void insertImage(CarImage image);

    @Query("SELECT * FROM car_images WHERE carId = :carId")
    List<CarImage> getImagesForCar(int carId);
}
