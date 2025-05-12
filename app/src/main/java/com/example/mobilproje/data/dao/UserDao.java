package com.example.mobilproje.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobilproje.data.model.User;

@Dao // Room'a bu arayüzün veri erişim nesnesi (DAO) olduğunu bildirir
public interface UserDao {

    @Insert
        // Yeni bir kullanıcıyı veritabanına ekler
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
        // Giriş işlemi için kullanıcı adı ve şifre eşleşen bir kullanıcı arar
        // Eşleşen kullanıcı varsa döner, yoksa null
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
        // Belirli kullanıcı adına sahip kullanıcıyı getirir (giriş kontrolü veya favori işlemleri için kullanılır)
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
        // ID'si verilen kullanıcıyı LiveData olarak döner (UI'da kullanıcı bilgisi göstermek için kullanılır)
    LiveData<User> getUserById(int userId);

    @Update
        // Kullanıcı verisini günceller (örneğin favori araç listesi, telefon bilgisi vb.)
    void update(User user);
}
