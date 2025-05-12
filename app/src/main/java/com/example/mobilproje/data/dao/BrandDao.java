// BrandDao.java
// Room veritabanı için Marka (Brand) verilerini işlemekle sorumlu DAO (Data Access Object) arayüzüdür.

package com.example.mobilproje.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mobilproje.data.model.Brand;

import java.util.List;

@Dao // Bu arayüzün Room DAO olduğunu belirtir
public interface BrandDao {

    @Insert // Room'a bir marka eklemek için kullanılır
    void insert(Brand brand);

    @Query("SELECT * FROM brands")
        // Veritabanındaki tüm markaları (brands tablosundan) alır ve LiveData ile gözlemlenebilir hale getirir
    LiveData<List<Brand>> getAllBrands();

    @Query("SELECT * FROM brands WHERE id = :id")
        // Belirtilen id'ye sahip markayı getirir (LiveData kullanılmadığı için arkaplanda doğrudan çağrılır)
    Brand getBrandById(int id);

    @Query("DELETE FROM brands")
        // Tablodaki tüm marka verilerini siler
    void deleteAllBrands();
}
