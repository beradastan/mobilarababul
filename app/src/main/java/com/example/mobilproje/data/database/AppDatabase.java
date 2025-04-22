package com.example.mobilproje.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mobilproje.data.dao.BrandDao;
import com.example.mobilproje.data.dao.CarDao;
import com.example.mobilproje.data.dao.UserDao;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.data.model.User;

@Database(entities = {Car.class, Brand.class, User.class}, version = 3)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract UserDao userDao();


    public abstract CarDao carDao();
    public abstract BrandDao brandDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "car_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
