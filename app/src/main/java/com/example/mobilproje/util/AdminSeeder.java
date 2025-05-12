package com.example.mobilproje.util;

import android.content.Context;

import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.User;

import java.util.concurrent.Executors;

public class AdminSeeder {

    // Uygulama başlarken çağrılacak olan metot
    // Eğer veritabanında "admin" kullanıcı adı yoksa otomatik olarak ekler
    public static void seedAdminIfNotExists(Context context) {
        // Singleton olarak AppDatabase örneği alınır
        AppDatabase db = AppDatabase.getInstance(context);

        // Arka planda çalışması için executor kullanılır
        Executors.newSingleThreadExecutor().execute(() -> {

            // Veritabanında "admin" adlı kullanıcı var mı kontrol edilir
            User existing = db.userDao().getUserByUsername("admin");

            // Yoksa yeni bir admin kullanıcı eklenir
            if (existing == null) {
                db.userDao().insert(new User("admin", "1234"));  // Varsayılan şifre: 1234
            }
        });
    }
}
