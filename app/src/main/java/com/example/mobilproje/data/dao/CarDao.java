// CarDao.java
// Room veritabanı için araç (Car) verilerini yönetmekle sorumlu DAO (Data Access Object) arayüzü.

package com.example.mobilproje.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;

import java.util.List;

@Dao // Room'a bu arayüzün bir DAO olduğunu bildirir
public interface CarDao {

    @Insert // Yeni bir araç eklemek için kullanılır
    void insert(Car car);

    @Query("SELECT * FROM cars WHERE id = :carId")
        // Belirli bir ID'ye sahip aracı LiveData olarak döner (UI'da gözlemlenebilir)
    LiveData<Car> getCarById(int carId);

    @Query("SELECT * FROM cars ORDER BY id DESC")
        // Tüm araçları son eklenen ilk olacak şekilde sıralayıp döner
    LiveData<List<Car>> getAllCars();

    @Query("SELECT * FROM brands WHERE id = :brandId")
        // Belirli bir ID'ye sahip markayı LiveData olarak döner (Car tablosuyla ilişkili)
    LiveData<Brand> getBrandById(int brandId);

    @Query("SELECT * FROM cars WHERE " +
            "(brandId = :brandId OR :brandId = -1) AND " +
            "(year >= :minYear OR :minYear IS NULL) AND " +
            "(year <= :maxYear OR :maxYear IS NULL) AND " +
            "(price >= :minPrice OR :minPrice IS NULL) AND " +
            "(price <= :maxPrice OR :maxPrice IS NULL) AND " +
            "(km >= :minKm OR :minKm IS NULL) AND " +
            "(km <= :maxKm OR :maxKm IS NULL) AND " +
            "(:color = 'Hepsi' OR color = :color) AND " +
            "(:transmissionType = 'Hepsi' OR transmissionType = :transmissionType) AND " +
            "(:fuelType = 'Hepsi' OR fuelType = :fuelType) AND " +  // ✅ Filtreye yakıt türü dahil edildi
            "(:city = 'Hepsi' OR city = :city) " +
            "ORDER BY year DESC")
        // Filtreleme: Marka, yıl, fiyat, km, renk, vites, yakıt, şehir gibi birçok kritere göre arama yapar
        // Hepsi kelimesi filtre uygulanmayacağını belirtmek için kullanılır
    LiveData<List<Car>> getFilteredCars(
            int brandId,
            Integer minYear,
            Integer maxYear,
            Integer minPrice,
            Integer maxPrice,
            Integer minKm,
            Integer maxKm,
            String color,
            String transmissionType,
            String fuelType,
            String city
    );

    @Query("SELECT * FROM cars ORDER BY price ASC")
        // Fiyatı artan şekilde sıralar
    LiveData<List<Car>> getSortedCarsByPriceAsc();

    @Query("SELECT * FROM cars ORDER BY price DESC")
        // Fiyatı azalan şekilde sıralar
    LiveData<List<Car>> getSortedCarsByPriceDesc();

    @Query("SELECT * FROM cars ORDER BY year ASC")
        // Yılı artan şekilde sıralar
    LiveData<List<Car>> getSortedCarsByYearAsc();

    @Query("SELECT * FROM cars ORDER BY year DESC")
        // Yılı azalan şekilde sıralar
    LiveData<List<Car>> getSortedCarsByYearDesc();

    @Query("SELECT * FROM cars ORDER BY km ASC")
        // Kilometresi artan şekilde sıralar
    LiveData<List<Car>> getSortedCarsByKmAsc();

    @Query("SELECT * FROM cars ORDER BY km DESC")
        // Kilometresi azalan şekilde sıralar
    LiveData<List<Car>> getSortedCarsByKmDesc();

    @Query("DELETE FROM cars WHERE id = :carId")
        // Belirli ID'ye sahip aracı siler
    void deleteCarById(int carId);

    @Query("SELECT * FROM cars WHERE userId = :userId")
        // Belirli bir kullanıcıya ait tüm araçları getirir
    LiveData<List<Car>> getCarsByUserId(int userId);

    @Update
        // Mevcut bir aracı güncellemek için kullanılır
    void update(Car car);

    @Query("SELECT * FROM cars WHERE id IN (:ids)")
        // Belirli ID'lerin bulunduğu listeye göre araçları getirir (örneğin favori araçlar)
    LiveData<List<Car>> getCarsByIds(List<Integer> ids);
}
