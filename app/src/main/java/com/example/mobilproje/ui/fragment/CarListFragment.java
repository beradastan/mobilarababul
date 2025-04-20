package com.example.mobilproje.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.mobilproje.R;
import com.example.mobilproje.databinding.FragmentCarListBinding;
import com.example.mobilproje.databinding.ItemCarBinding;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.data.model.Car;

import java.util.List;

public class CarListFragment extends Fragment {

    private FragmentCarListBinding binding;
    private CarViewModel carViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));

        carViewModel.getAllCars().observe(getViewLifecycleOwner(), this::showCars);
    }

    private void showCars(List<Car> cars) {
        binding.recyclerViewCars.removeAllViews();

        for (Car car : cars) {
            ItemCarBinding itemBinding = ItemCarBinding.inflate(getLayoutInflater());

            // bilgileri doldur
            itemBinding.tvBrand.setText("Marka ID: " + car.brandId);
            itemBinding.tvModel.setText("Model: " + car.model);
            itemBinding.tvYear.setText("Yıl: " + car.year);
            itemBinding.tvKm.setText("KM: " + car.km);
            itemBinding.tvPrice.setText("Fiyat: " + car.price + " ₺");

            // FOTOĞRAF
            if (car.imageUri != null) {
                Glide.with(requireContext())
                        .load(Uri.parse(car.imageUri))
                        .into(itemBinding.imgCar);

            }

            // 🚨 TAM BURAYA TIKLANINCA GEÇİŞİ YAZ
            itemBinding.getRoot().setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("car", car);

                NavHostFragment.findNavController(CarListFragment.this)
                        .navigate(R.id.action_carListFragment_to_carDetailFragment, bundle);
            });

            binding.recyclerViewCars.addView(itemBinding.getRoot());
        }
    }

}
