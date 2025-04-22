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

    @Query("SELECT * FROM cars WHERE " +
            "(brandId = :brandId OR :brandId = -1) AND " +
            "(year >= :minYear OR :minYear IS NULL) AND " +
            "(year <= :maxYear OR :maxYear IS NULL) AND " +
            "(price >= :minPrice OR :minPrice IS NULL) AND " +
            "(price <= :maxPrice OR :maxPrice IS NULL) AND " +
            "(km >= :minKm OR :minKm IS NULL) AND " +
            "(km <= :maxKm OR :maxKm IS NULL) ORDER BY year DESC")
    LiveData<List<Car>> getFilteredCars(
            int brandId,
            Integer minYear,
            Integer maxYear,
            Integer minPrice,
            Integer maxPrice,
            Integer minKm,
            Integer maxKm);




}