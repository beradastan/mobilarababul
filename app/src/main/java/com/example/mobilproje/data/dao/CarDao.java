
package com.example.mobilproje.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;

import java.util.List;

@Dao
public interface CarDao {

    @Insert
    void insert(Car car);

    @Query("SELECT * FROM cars WHERE id = :carId")
    LiveData<Car> getCarById(int carId);

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
            "(km <= :maxKm OR :maxKm IS NULL) AND " +
            "(:color = 'Hepsi' OR color = :color) AND " +
            "(:transmissionType = 'Hepsi' OR transmissionType = :transmissionType) AND " +
            "(:fuelType = 'Hepsi' OR fuelType = :fuelType) AND " +
            "(:city = 'Hepsi' OR city = :city) " +
            "ORDER BY year DESC")
    LiveData<List<Car>> getFilteredCars(
            int brandId,
            Integer minYear,
            Integer maxYear,
            Integer minPrice,
            Integer maxPrice,
            Integer minKm,
            Integer maxKm,
            String color,
            String transmissionType,
            String fuelType,
            String city
    );

    @Query("SELECT * FROM cars ORDER BY price ASC")
    LiveData<List<Car>> getSortedCarsByPriceAsc();

    @Query("SELECT * FROM cars ORDER BY price DESC")
    LiveData<List<Car>> getSortedCarsByPriceDesc();

    @Query("SELECT * FROM cars ORDER BY year ASC")
    LiveData<List<Car>> getSortedCarsByYearAsc();

    @Query("SELECT * FROM cars ORDER BY year DESC")
    LiveData<List<Car>> getSortedCarsByYearDesc();

    @Query("SELECT * FROM cars ORDER BY km ASC")
    LiveData<List<Car>> getSortedCarsByKmAsc();

    @Query("SELECT * FROM cars ORDER BY km DESC")
    LiveData<List<Car>> getSortedCarsByKmDesc();

    @Query("DELETE FROM cars WHERE id = :carId")
    void deleteCarById(int carId);

    @Query("SELECT * FROM cars WHERE userId = :userId")
    LiveData<List<Car>> getCarsByUserId(int userId);

    @Update
    void update(Car car);

    @Query("SELECT * FROM cars WHERE id IN (:ids)")
    LiveData<List<Car>> getCarsByIds(List<Integer> ids);
}
