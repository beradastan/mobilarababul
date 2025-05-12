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

    private final AppDatabase db;                    // Veritabanı örneği
    private final LiveData<List<Car>> allCars;       // Tüm araçları gözlemleyen LiveData
    private final ExecutorService executorService;   // Arka plan işçileri için thread havuzu
    private CarDao carDao;                           // Araçlara özel DAO

    // Constructor (Yapıcı Metot)
    public CarViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);           // Veritabanı örneği alınır
        allCars = db.carDao().getAllCars();                  // Araç listesi LiveData olarak alınır
        executorService = Executors.newSingleThreadExecutor(); // Arka plan thread havuzu oluşturulur
        carDao = db.carDao();                                // CarDao örneği alınır
    }

    // Tüm araçları döndürür (LiveData)
    public LiveData<List<Car>> getAllCars() {
        return allCars;
    }

    // ID ile bir aracı getirir
    public LiveData<Car> getCarById(int carId) {
        return carDao.getCarById(carId);
    }

    // ID ile markayı getirir
    public LiveData<Brand> getBrandById(int brandId) {
        return carDao.getBrandById(brandId);
    }

    // Yeni araç ekleme (arka planda)
    public void insert(Car car) {
        executorService.execute(() -> db.carDao().insert(car));
    }

    // Araçları filtreleyerek getirir
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

    // Fiyata göre sıralama (artan/azalan)
    public LiveData<List<Car>> getSortedCarsByPrice(boolean ascending) {
        return ascending ? carDao.getSortedCarsByPriceAsc() : carDao.getSortedCarsByPriceDesc();
    }

    // Kilometreye göre sıralama
    public LiveData<List<Car>> getSortedCarsByKm(boolean ascending) {
        return ascending ? carDao.getSortedCarsByKmAsc() : carDao.getSortedCarsByKmDesc();
    }

    // Yıla göre sıralama
    public LiveData<List<Car>> getSortedCarsByYear(boolean ascending) {
        return ascending ? carDao.getSortedCarsByYearAsc() : carDao.getSortedCarsByYearDesc();
    }

    // Belirli kullanıcıya ait araçları getirir
    public LiveData<List<Car>> getCarsByUserId(int userId) {
        return db.carDao().getCarsByUserId(userId);
    }

    // Araç güncelleme işlemi
    public void update(Car car) {
        executorService.execute(() -> db.carDao().update(car));
    }

    // ID listesine göre araçları getirir (favorilerde kullanılır)
    public LiveData<List<Car>> getCarsByIds(List<Integer> ids) {
        return carDao.getCarsByIds(ids);
    }

    // Belirli ID'li aracı siler
    public void deleteCarById(int carId) {
        carDao.deleteCarById(carId);
    }
}
