package com.example.mobilproje.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobilproje.data.dao.CarDao;
import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final LiveData<List<Car>> allCars;
    private final ExecutorService executorService;
    private CarDao carDao;

    public CarViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        allCars = db.carDao().getAllCars();
        executorService = Executors.newSingleThreadExecutor();
        carDao = db.carDao();
    }

    public LiveData<List<Car>> getAllCars() {
        return allCars;
    }

    public LiveData<Car> getCarById(int carId) {
        return carDao.getCarById(carId);
    }

    public LiveData<Brand> getBrandById(int brandId) {
        return carDao.getBrandById(brandId);
    }

    public void insert(Car car) {
        executorService.execute(() -> db.carDao().insert(car));
    }

    public LiveData<List<Car>> getFilteredCars(int brandId, Integer minYear, Integer maxYear,
                                               Integer minPrice, Integer maxPrice,
                                               Integer minKm, Integer maxKm,
                                               String color, String transmissionType,
                                               String fuelType, String city) {
        return carDao.getFilteredCars(
                brandId, minYear, maxYear, minPrice, maxPrice, minKm, maxKm,
                color, transmissionType, fuelType, city
        );
    }

    public LiveData<List<Car>> getSortedCarsByPrice(boolean ascending) {
        return ascending ? carDao.getSortedCarsByPriceAsc() : carDao.getSortedCarsByPriceDesc();
    }

    public LiveData<List<Car>> getSortedCarsByKm(boolean ascending) {
        return ascending ? carDao.getSortedCarsByKmAsc() : carDao.getSortedCarsByKmDesc();
    }

    public LiveData<List<Car>> getSortedCarsByYear(boolean ascending) {
        return ascending ? carDao.getSortedCarsByYearAsc() : carDao.getSortedCarsByYearDesc();
    }

    public LiveData<List<Car>> getCarsByUserId(int userId) {
        return db.carDao().getCarsByUserId(userId);
    }

    public void update(Car car) {
        executorService.execute(() -> db.carDao().update(car));
    }

    public LiveData<List<Car>> getCarsByIds(List<Integer> ids) {
        return carDao.getCarsByIds(ids);
    }

    public void deleteCarById(int carId) {
        carDao.deleteCarById(carId);
    }
}
