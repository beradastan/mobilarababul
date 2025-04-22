// CarDao.java
package com.example.mobilproje.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;

import java.util.List;

@Dao
public interface CarDao {
    @Insert
    void insert(Car car);

    @Query("SELECT * FROM cars ORDER BY id DESC")
    LiveData<List<Car>> getAllCars();

    @Query("SELECT * FROM brands WHERE id = :brandId")
    LiveData<Brand> getBrandById(int brandId);



}