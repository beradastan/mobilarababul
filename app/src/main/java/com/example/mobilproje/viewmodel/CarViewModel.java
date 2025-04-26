// CarViewModel.java
package com.example.mobilproje.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobilproje.data.dao.CarDao;
import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarViewModel extends AndroidViewModel {
    private CarDao carDao;

    private final AppDatabase db;
    private final LiveData<List<Car>> allCars;
    private final ExecutorService executorService;

    public CarViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        allCars = db.carDao().getAllCars();
        executorService = Executors.newSingleThreadExecutor();
        carDao = AppDatabase.getInstance(application).carDao();  // Initialize the carDao

    }

    public LiveData<List<Car>> getAllCars() {
        return allCars;
    }

    public LiveData<Brand> getBrandById(int brandId) {
        return carDao.getBrandById(brandId);  // Directly query the DAO
    }

    public LiveData<Car> getCarById(int carId) {
        return carDao.getCarById(carId);
    }


    public void insert(Car car) {
        executorService.execute(() -> db.carDao().insert(car));
    }


    public LiveData<List<Car>> getFilteredCars(int brandId, Integer minYear, Integer maxYear, Integer minPrice, Integer maxPrice, Integer minKm, Integer maxKm) {
        return carDao.getFilteredCars(brandId, minYear, maxYear, minPrice, maxPrice, minKm, maxKm);
    }



    public LiveData<List<Car>> getSortedCarsByPrice(boolean ascending) {
        if (ascending) {
            return carDao.getSortedCarsByPriceAsc();  // Artan fiyat sıralaması
        } else {
            return carDao.getSortedCarsByPriceDesc(); // Azalan fiyat sıralaması
        }
    }

    public LiveData<List<Car>> getSortedCarsByKm(boolean ascending) {
        if (ascending) {
            return carDao.getSortedCarsByKmAsc();  // Artan kilometre sıralaması
        } else {
            return carDao.getSortedCarsByKmDesc(); // Azalan kilometre sıralaması
        }
    }

    public LiveData<List<Car>> getSortedCarsByYear(boolean ascending) {
        if (ascending) {
            return carDao.getSortedCarsByYearAsc();  // Artan yıl sıralaması
        } else {
            return carDao.getSortedCarsByYearDesc(); // Azalan yıl sıralaması
        }
    }


    // Method to delete a car by ID
    public void deleteCarById(int carId) {
        carDao.deleteCarById(carId); // Calls the repository to delete the car
    }

}