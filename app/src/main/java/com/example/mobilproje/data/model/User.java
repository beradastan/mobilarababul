package com.example.mobilproje.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobilproje.data.database.Converters;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;


    public String username;
    public String password;
    public String firstName;
    public String lastName;
    public String phone;
    public String email;

    public List<Integer> getFavoriteCarIds() {
        return favoriteCarIds;
    }

    public void setFavoriteCarIds(List<Integer> favoriteCarIds) {
        this.favoriteCarIds = favoriteCarIds;
    }

    @TypeConverters(Converters.class)
    public List<Integer> favoriteCarIds = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
}
