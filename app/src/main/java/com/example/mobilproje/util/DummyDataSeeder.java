package com.example.mobilproje.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;

import java.util.Collections;
import java.util.concurrent.Executors;

public class DummyDataSeeder {

    public static void seed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("dummy", Context.MODE_PRIVATE);
        boolean alreadySeeded = prefs.getBoolean("dummy_seeded", false);

        if (alreadySeeded) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);

            // Veritabanındaki mevcut markaları sil
            db.brandDao().deleteAllBrands();

            // Dummy markalar
            Brand renault = new Brand("Renault");
            db.brandDao().insert(renault);

            Brand toyota = new Brand("Toyota");
            db.brandDao().insert(toyota);

            Brand ford = new Brand("Ford");
            db.brandDao().insert(ford);

            // Dummy araçlar (markaları zaten veritabanında mevcut olanları kullanıyoruz)
            db.carDao().insert(new Car(
                    renault.getId(),
                    "Clio",
                    2020,
                    50000,
                    450000,
                    "Temiz aile arabası",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            db.carDao().insert(new Car(
                    toyota.getId(),
                    "Corolla",
                    2019,
                    60000,
                    500000,
                    "Dizel, sorunsuz",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            db.carDao().insert(new Car(
                    ford.getId(),
                    "Focus",
                    2017,
                    98000,
                    375000,
                    "Yeni muayene, masrafsız",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            // Bir daha çalışmasın diye işaretle
            prefs.edit().putBoolean("dummy_seeded", true).apply();
        });
    }
}
