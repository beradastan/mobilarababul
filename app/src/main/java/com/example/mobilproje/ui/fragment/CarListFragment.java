package com.example.mobilproje.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilproje.R;
import com.example.mobilproje.databinding.FragmentCarListBinding;
import com.example.mobilproje.databinding.ItemCarBinding;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.data.model.Brand;

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

        binding.fabAddCar.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_carListFragment_to_addCarFragment);
        });

        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);

        // RecyclerView için LayoutManager ayarlama
        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));

        // Verileri gözlemle ve RecyclerView adapter'ı bağla
        carViewModel.getAllCars().observe(getViewLifecycleOwner(), cars -> {
            if (cars != null && !cars.isEmpty()) {
                CarListAdapter adapter = new CarListAdapter(cars);
                binding.recyclerViewCars.setAdapter(adapter); // Adapter'ı buraya bağladık
            } else {
                Toast.makeText(getContext(), "İlan bulunamadı", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarViewHolder> {
        private final List<Car> carList;

        CarListAdapter(List<Car> carList) {
            this.carList = carList;
        }

        @NonNull
        @Override
        public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemCarBinding itemBinding = ItemCarBinding.inflate(inflater, parent, false);
            return new CarViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
            Car car = carList.get(position);
            holder.bind(car);
        }

        @Override
        public int getItemCount() {
            return carList.size();
        }

        class CarViewHolder extends RecyclerView.ViewHolder {
            private final ItemCarBinding itemBinding;

            CarViewHolder(ItemCarBinding binding) {
                super(binding.getRoot());
                this.itemBinding = binding;
            }

            void bind(Car car) {
                // Set car details
                itemBinding.tvModel.setText("Model: " + car.model);
                itemBinding.tvYear.setText("Yıl: " + car.year);
                itemBinding.tvKm.setText("KM: " + car.km);
                itemBinding.tvPrice.setText("Fiyat: " + car.price + " ₺");

                // Fetch brand name using brandId
                carViewModel.getBrandById(car.brandId).observe(getViewLifecycleOwner(), brand -> {
                    if (brand != null) {
                        itemBinding.tvBrand.setText("Marka: " + brand.name);  // Set the brand name
                    }
                });

                // Set car image
                if (car.imageUris != null && !car.imageUris.isEmpty()) {
                    Glide.with(requireContext())
                            .load(Uri.parse(car.imageUris.get(0))) // Show the first image
                            .into(itemBinding.imgCar);
                }

                itemBinding.getRoot().setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("car", car);

                    NavHostFragment.findNavController(CarListFragment.this)
                            .navigate(R.id.action_carListFragment_to_carDetailFragment, bundle);
                });
            }
        }
    }
}
