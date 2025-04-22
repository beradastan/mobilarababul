// Brand.java
package com.example.mobilproje.data.model;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "brands")
public class Brand {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String logoUrl;

    @Ignore
    public Brand(String name) {
        this.name = name;
        this.logoUrl = "";
    }


    public Brand(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }
}
