package com.example.mobilproje.ui.adapter;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.databinding.ItemCarBinding;
import com.example.mobilproje.viewmodel.CarViewModel;

import java.util.List;

public class FavoriteCarAdapter extends RecyclerView.Adapter<FavoriteCarAdapter.FavoriteCarViewHolder> {

    private final List<Car> carList;
    private final OnCarClickListener listener;

    public interface OnCarClickListener {
        void onCarClick(Car car);
    }

    public FavoriteCarAdapter(List<Car> carList, OnCarClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteCarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarBinding binding = ItemCarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FavoriteCarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCarViewHolder holder, int position) {
        holder.bind(carList.get(position));
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    class FavoriteCarViewHolder extends RecyclerView.ViewHolder {
        private final ItemCarBinding binding;

        FavoriteCarViewHolder(ItemCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Car car) {
            binding.tvBrandModel.setText("Marka ID: " + car.brandId + " - " + car.model);
            binding.tvTitle.setText(car.getTitle());
            binding.tvPrice.setText(car.getPrice() + " ₺");
            binding.tvCity.setText(car.getCity());

            if (car.getImageBase64List() != null && !car.getImageBase64List().isEmpty()) {
                byte[] decodedBytes = Base64.decode(car.getImageBase64List().get(0), Base64.DEFAULT);
                Glide.with(binding.getRoot().getContext())
                        .asBitmap()
                        .load(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length))
                        .into(binding.imgCar);
            }

            CarViewModel carViewModel = new ViewModelProvider((FragmentActivity) binding.getRoot().getContext()).get(CarViewModel.class);
            carViewModel.getBrandById(car.getBrandId()).observe((LifecycleOwner) binding.getRoot().getContext(), brand -> {
                if (brand != null) {
                    binding.tvBrandModel.setText(brand.name + " - " + car.model);
                } else {
                    binding.tvBrandModel.setText("Bilinmiyor - " + car.model);
                }
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCarClick(car);
                }
            });
        }
    }
}
