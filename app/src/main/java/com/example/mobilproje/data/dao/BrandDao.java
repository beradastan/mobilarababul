// BrandDao.java
package com.example.mobilproje.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mobilproje.data.model.Brand;

import java.util.List;

@Dao
public interface BrandDao {
    @Insert
    void insert(Brand brand);

    @Query("SELECT * FROM brands")
    LiveData<List<Brand>> getAllBrands();

    @Query("SELECT * FROM brands WHERE id = :id")
    Brand getBrandById(int id);

    @Query("DELETE FROM brands")
    void deleteAllBrands();
}