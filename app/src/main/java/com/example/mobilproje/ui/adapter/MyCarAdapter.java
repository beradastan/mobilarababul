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

    public interface OnCarClickListener {
        void onCarClick(Car car);
    }
    private final AppDatabase db;  // yeni

    private final List<Car> carList;
    private final OnCarClickListener listener;

    public MyCarAdapter(List<Car> carList, OnCarClickListener listener, AppDatabase db) {
        this.carList = carList;
        this.listener = listener;
        this.db = db; // yeni
    }

    @NonNull
    @Override
    public MyCarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_car, parent, false);
        return new MyCarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    class MyCarViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCarInfo;
        private final Button btnDelete; // Silme butonu

        MyCarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCarInfo = itemView.findViewById(R.id.tvCarInfo);
            btnDelete = itemView.findViewById(R.id.btnDelete); // XML'de btnDelete id'li bir buton olmalı
        }

        void bind(Car car) {
            String info = car.model + " (" + car.year + ") : " + car.price + " ₺";
            tvCarInfo.setText(info);

            itemView.setOnClickListener(v -> listener.onCarClick(car));

            btnDelete.setOnClickListener(v -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    db.carDao().deleteCarById(car.id);

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        carList.remove(position);

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
