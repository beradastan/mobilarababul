package com.example.mobilproje.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.databinding.FragmentCarDetailBinding;

public class CarDetailFragment extends Fragment {

    private FragmentCarDetailBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            Car car = (Car) getArguments().getSerializable("car");
            if (car != null) {
                binding.tvBrand.setText("Marka ID: " + car.brandId);
                binding.tvModel.setText("Model: " + car.model);
                binding.tvYear.setText("Yıl: " + car.year);
                binding.tvKm.setText("KM: " + car.km);
                binding.tvPrice.setText("Fiyat: " + car.price + " ₺");
                binding.tvDescription.setText("Açıklama: " + car.description);

                if (car.imageUri != null) {
                    Glide.with(this).load(Uri.parse(car.imageUri)).into(binding.imgCar);
                }
            }
        }
    }
}
