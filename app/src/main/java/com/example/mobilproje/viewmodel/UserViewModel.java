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

    private final AppDatabase db;
    private final UserDao userDao;

    public UserViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public void insert(User user) {
        Executors.newSingleThreadExecutor().execute(() -> db.userDao().insert(user));
    }

    public User login(String username, String password) {
        return db.userDao().login(username, password);
    }

    public User getUserByUsername(String username) {
        return db.userDao().getUserByUsername(username);
    }

    public LiveData<User> getUserByUsernameLive(String username) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByUsername(username);
            userLiveData.postValue(user);
        });
        return userLiveData;
    }

    public LiveData<User> getUserById(int userId) {
        return db.userDao().getUserById(userId);
    }

    public void update(User user) {
        Executors.newSingleThreadExecutor().execute(() -> db.userDao().update(user));
    }
}
