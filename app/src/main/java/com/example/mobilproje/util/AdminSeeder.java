package com.example.mobilproje.util;

import android.content.Context;

import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.User;

import java.util.concurrent.Executors;

public class AdminSeeder {

    public static void seedAdminIfNotExists(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        Executors.newSingleThreadExecutor().execute(() -> {

            User existing = db.userDao().getUserByUsername("admin");

            if (existing == null) {
                db.userDao().insert(new User("admin", "1234"));
            }
        });
    }
}
