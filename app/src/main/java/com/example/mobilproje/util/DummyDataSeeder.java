package com.example.mobilproje.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executors;

public class DummyDataSeeder {

    public static void seed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("dummy", Context.MODE_PRIVATE);
        boolean alreadySeeded = prefs.getBoolean("dummy_seeded", false);

        //if (alreadySeeded) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);

            // Dummy markalar
            db.brandDao().insert(new Brand("Renault"));
            db.brandDao().insert(new Brand("Toyota"));
            db.brandDao().insert(new Brand("Ford"));

            // Dummy araçlar
            db.carDao().insert(new Car(
                    1,
                    "Clio",
                    2020,
                    50000,
                    450000,
                    "Temiz aile arabası",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            db.carDao().insert(new Car(
                    2,
                    "Corolla",
                    2019,
                    60000,
                    500000,
                    "Dizel, sorunsuz",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            db.carDao().insert(new Car(
                    1,
                    "Megane",
                    2018,
                    72000,
                    400000,
                    "Bakımları tam, sigara içilmemiş",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            db.carDao().insert(new Car(
                    2,
                    "Yaris",
                    2022,
                    15000,
                    650000,
                    "Çok az kullanıldı",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            db.carDao().insert(new Car(
                    3,
                    "Focus",
                    2017,
                    98000,
                    375000,
                    "Yeni muayene, masrafsız",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            db.carDao().insert(new Car(
                    2,
                    "Corolla Hybrid",
                    2021,
                    25000,
                    700000,
                    "Hybrid sistem garantili",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            db.carDao().insert(new Car(
                    3,
                    "Fiesta",
                    2015,
                    120000,
                    280000,
                    "Öğrenci arabası",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));

            db.carDao().insert(new Car(
                    1,
                    "Talisman",
                    2019,
                    45000,
                    580000,
                    "Dolu paket, değişensiz",
                    Collections.singletonList("android.resource://com.example.mobilproje/drawable/car_sample"),
                    1
            ));


            // Bir daha çalışmasın diye işaretle
            prefs.edit().putBoolean("dummy_seeded", true).apply();
        });
    }
}
