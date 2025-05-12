package com.example.mobilproje.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobilproje.data.dao.UserDao;
import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.User;

import java.util.concurrent.Executors;

public class UserViewModel extends AndroidViewModel {

    private final AppDatabase db;      // Room veritabanı nesnesi
    private final UserDao userDao;     // Kullanıcılar için DAO (veritabanı işlemleri)

    // ViewModel oluşturulurken AppDatabase ve DAO ayarlanır
    public UserViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application); // Singleton veritabanı örneği
        userDao = db.userDao();                   // User DAO örneği alınır
    }

    // Kullanıcı ekleme işlemi (arka planda çalıştırılır)
    public void insert(User user) {
        Executors.newSingleThreadExecutor().execute(() -> db.userDao().insert(user));
    }

    // Kullanıcı adı ve şifre ile giriş (Senkron - dikkatli kullanılmalı)
    public User login(String username, String password) {
        return db.userDao().login(username, password);
    }

    // Kullanıcı adı ile kullanıcıyı getirir (Senkron kullanım için)
    public User getUserByUsername(String username) {
        return db.userDao().getUserByUsername(username);
    }

    // Kullanıcı adı ile kullanıcıyı LiveData olarak getirir (UI gözlemi için uygundur)
    public LiveData<User> getUserByUsernameLive(String username) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByUsername(username); // Arka planda veriyi al
            userLiveData.postValue(user);                    // UI thread'e gönder
        });
        return userLiveData;
    }

    // ID ile kullanıcıyı LiveData olarak getirir
    public LiveData<User> getUserById(int userId) {
        return db.userDao().getUserById(userId);
    }

    // Kullanıcı bilgilerini güncelle (arka planda çalıştırılır)
    public void update(User user) {
        Executors.newSingleThreadExecutor().execute(() -> db.userDao().update(user));
    }
}
