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

    private final AppDatabase db;                      // Veritabanı örneği
    private final LiveData<List<Brand>> allBrands;     // Tüm markaları gözlemlemek için LiveData
    private final ExecutorService executorService;     // Arka plan işlemleri için thread havuzu

    // ViewModel yapıcı metodu
    public BrandViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);      // Veritabanı bağlantısı alınır
        allBrands = db.brandDao().getAllBrands();       // Tüm markalar LiveData olarak alınır
        executorService = Executors.newSingleThreadExecutor();  // Arka plan işçisi tanımlanır
    }

    // Markaların tamamını döndürür
    public LiveData<List<Brand>> getAllBrands() {
        return allBrands;
    }

    // Yeni bir marka ekler — bu işlem Room nedeniyle arka planda yapılmalıdır
    public void insert(Brand brand) {
        executorService.execute(() -> db.brandDao().insert(brand));
    }
}
