package com.example.mobilproje.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilproje.R;
import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.Car;

import java.util.List;
import java.util.concurrent.Executors;

public class MyCarAdapter extends RecyclerView.Adapter<MyCarAdapter.MyCarViewHolder> {

    // Dışarıya tıklanan aracı bildirmek için kullanılacak arayüz
    public interface OnCarClickListener {
        void onCarClick(Car car);
    }

    private final AppDatabase db; // Veritabanı işlemleri için Room veritabanı referansı
    private final List<Car> carList; // Kullanıcının araç listesi
    private final OnCarClickListener listener; // Tıklama olaylarını dışarıya iletmek için listener

    // Adapter constructor
    public MyCarAdapter(List<Car> carList, OnCarClickListener listener, AppDatabase db) {
        this.carList = carList;
        this.listener = listener;
        this.db = db;
    }

    @NonNull
    @Override
    public MyCarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_my_car.xml dosyasını şişirerek her satır için görünüm oluşturur
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_car, parent, false);
        return new MyCarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCarViewHolder holder, int position) {
        // Her satır için bind işlemi yapılır
        Car car = carList.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return carList.size(); // Liste boyutu kadar öğe gösterilir
    }

    // ViewHolder: Her bir araç kartını temsil eder
    class MyCarViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCarInfo; // Araç bilgilerini gösteren metin
        private final Button btnDelete;   // Silme işlemi için buton

        MyCarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCarInfo = itemView.findViewById(R.id.tvCarInfo);  // XML'deki tvCarInfo ID'li TextView
            btnDelete = itemView.findViewById(R.id.btnDelete);  // XML'deki btnDelete ID'li Button
        }

        // Her bir Car nesnesini satıra bağlar
        void bind(Car car) {
            // Araç modeli, yılı ve fiyatı birleştirilerek gösterilir
            String info = car.model + " (" + car.year + ") : " + car.price + " ₺";
            tvCarInfo.setText(info);

            // Satıra tıklanınca dışarıya bildir (detay ekranı vb. için)
            itemView.setOnClickListener(v -> listener.onCarClick(car));

            // Sil butonuna tıklandığında aracı veritabanından ve listeden kaldır
            btnDelete.setOnClickListener(v -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    db.carDao().deleteCarById(car.id); // Veritabanından sil

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        carList.remove(position); // Listeden de çıkar

                        // UI güncellemesi ana thread'de yapılmalı
                        itemView.post(() -> {
                            notifyItemRemoved(position);
                            Toast.makeText(itemView.getContext(), "Araç silindi", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            });
        }
    }
}
