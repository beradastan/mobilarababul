package com.example.mobilproje.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.Brand;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrandViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final LiveData<List<Brand>> allBrands;
    private final ExecutorService executorService;

    public BrandViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        allBrands = db.brandDao().getAllBrands();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Brand>> getAllBrands() {
        return allBrands;
    }

    public void insert(Brand brand) {
        executorService.execute(() -> db.brandDao().insert(brand));
    }
}
