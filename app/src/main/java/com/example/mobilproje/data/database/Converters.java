package com.example.mobilproje.data.database;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class Converters {

    @TypeConverter
    public String fromList(List<String> list) {
        // List<String> → String
        // Örnek: ["a", "b", "c"] → "a,b,c"
        return String.join(",", list);
    }

    @TypeConverter
    public List<String> toList(String data) {
        // String → List<String>
        // Örnek: "a,b,c" → ["a", "b", "c"]
        return Arrays.asList(data.split(","));
    }

    @TypeConverter
    public String fromIntegerList(List<Integer> list) {
        // List<Integer> → String
        // Örnek: [1, 2, 3] → "1,2,3"
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Integer value : list) {
            sb.append(value).append(",");
        }
        sb.setLength(sb.length() - 1); // Son eklenen virgülü sil
        return sb.toString();
    }

    @TypeConverter
    public List<Integer> toIntegerList(String data) {
        // String → List<Integer>
        // Örnek: "1,2,3" → [1, 2, 3]
        if (data == null || data.isEmpty()) return new java.util.ArrayList<>();
        List<Integer> list = new java.util.ArrayList<>();
        for (String s : data.split(",")) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }
}
