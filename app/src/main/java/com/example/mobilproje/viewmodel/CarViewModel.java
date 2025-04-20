// CarViewModel.java
package com.example.mobilproje.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.Car;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final LiveData<List<Car>> allCars;
    private final ExecutorService executorService;

    public CarViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        allCars = db.carDao().getAllCars();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Car>> getAllCars() {
        return allCars;
    }

    public void insert(Car car) {
        executorService.execute(() -> db.carDao().insert(car));
    }
}