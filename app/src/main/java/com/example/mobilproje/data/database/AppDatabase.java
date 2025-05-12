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
// Room veritabanı tanımı: Kullanılan Entity sınıfları belirtilir (Car, Brand, User)
// Converters: Base64 gibi özel veri tipleri için dönüşüm sınıfı kullanılır

public abstract class AppDatabase extends RoomDatabase {

    // Singleton olarak tutulacak veritabanı örneği
    private static AppDatabase instance;

    // DAO arayüzlerini erişilebilir hale getiriyoruz
    public abstract UserDao userDao();
    public abstract CarDao carDao();
    public abstract BrandDao brandDao();

    // Veritabanı oluşturulduğunda tetiklenecek işlemleri tanımlar
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Veritabanı ilk oluşturulduğunda arka planda çalışacak işlemler
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase database = instance;

                // İlk çalıştırmada örnek veri eklemek istersen burayı açabilirsin
                /*BrandDao brandDao = database.brandDao();
                brandDao.insert(new Brand("Audi"));
                brandDao.insert(new Brand("Bmw"));
                brandDao.insert(new Brand("Fiat"));
                ... */
            });
        }
    };

    // Veritabanı örneğini tek seferlik oluşturup döndüren yöntem (Singleton desenine göre)
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "car_database") // Veritabanı adı: car_database
                    .addCallback(roomCallback) // Oluşturulurken yapılacak işlemleri belirler
                    .fallbackToDestructiveMigration() // Versiyon değişirse veritabanını sıfırla
                    .build();
        }
        return instance;
    }
}
