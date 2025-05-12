package com.example.mobilproje.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilproje.R;
import com.example.mobilproje.data.model.Car;

import java.util.ArrayList;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    // Araç listesini tutan değişken
    private List<Car> carList = new ArrayList<>();

    // Dışarıdan yeni araç listesi atanmasını sağlayan metot
    public void setCarList(List<Car> cars) {
        this.carList = cars;
        notifyDataSetChanged(); // Liste değiştiğinde RecyclerView'ı günceller
    }

    // ViewHolder: RecyclerView'daki her bir öğeyi (satırı) temsil eder
    public static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrand, tvModel, tvYear, tvKm, tvPrice;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_car.xml içindeki TextView bileşenlerini tanımlar
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvKm = itemView.findViewById(R.id.tvKm);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Her bir liste öğesi için item_car.xml dosyasını şişirir (inflate)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        // Listedeki her bir öğe için veriyi view'a bağlar
        Car car = carList.get(position);
        holder.tvBrand.setText("Marka ID: " + car.brandId); // Sadece ID gösteriliyor, gerçek marka adı için Brand tablosu gerekir
        holder.tvModel.setText("Model: " + car.model);
        holder.tvYear.setText("Yıl: " + car.year);
        holder.tvKm.setText("KM: " + car.km);
        holder.tvPrice.setText("Fiyat: " + car.price + " ₺");
    }

    @Override
    public int getItemCount() {
        // Liste boyutunu döner; RecyclerView bu kadar öğe gösterecektir
        return carList.size();
    }
}
