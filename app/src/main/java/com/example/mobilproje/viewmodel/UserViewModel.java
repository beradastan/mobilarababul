package com.example.mobilproje.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.User;

import java.util.concurrent.Executors;

public class UserViewModel extends AndroidViewModel {

    private final AppDatabase db;


    public UserViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
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
}

