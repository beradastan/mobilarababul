package com.example.mobilproje.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mobilproje.data.dao.BrandDao;
import com.example.mobilproje.data.dao.CarDao;
import com.example.mobilproje.data.dao.UserDao;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.data.model.User;

import java.util.concurrent.Executors;

@Database(entities = {Car.class, Brand.class, User.class }, version = 14)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract CarDao carDao();
    public abstract BrandDao brandDao();

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase database = instance;

            });
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "car_database")
                    .addCallback(roomCallback)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
