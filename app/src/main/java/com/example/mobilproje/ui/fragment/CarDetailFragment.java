package com.example.mobilproje.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mobilproje.R;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.databinding.FragmentCarDetailBinding;
import com.example.mobilproje.ui.adapter.CarImagePagerAdapter;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class CarDetailFragment extends Fragment {
    private FragmentCarDetailBinding binding;
    private CarViewModel carViewModel;
    private int carId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            carId = getArguments().getInt("carId", -1);
        }

        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);

        if (carId != -1) {
            carViewModel.getCarById(carId).observe(getViewLifecycleOwner(), car -> {
                if (car != null) {
                    bindCarData(car);
                }
            });
        }
    }

    private void bindCarData(Car car) {
        binding.tvDescription.setText(car.getDescription());

        carViewModel.getBrandById(car.brandId).observe(getViewLifecycleOwner(), brand -> {
            if (brand != null) {
                binding.tvBrand.setText(brand.name);
            }
        });

        binding.tvModel.setText(String.valueOf(car.getModel()));
        binding.tvYear.setText(String.valueOf(car.getYear()));
        binding.tvKm.setText(String.valueOf(car.getKm()));
        binding.tvPrice.setText(car.getPrice() + " ₺");
        binding.tvCarTitle.setText(car.getTitle());
        binding.tvColor.setText(car.getColor());
        binding.tvTransmission.setText(car.getTransmissionType());
        binding.tvFuel.setText(car.getFuelType());
        List<Bitmap> bitmapList = new ArrayList<>();
        if (car.getImageBase64List() != null) {
            for (String base64Image : car.getImageBase64List()) {
                if (base64Image != null && !base64Image.isEmpty()) {
                    byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    if (bitmap != null) {
                        bitmapList.add(bitmap);
                    }
                }
            }
        }

        if (!bitmapList.isEmpty()) {
            CarImagePagerAdapter adapter = new CarImagePagerAdapter(bitmapList);
            binding.viewPagerCarImages.setAdapter(adapter);
        }

        String username = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("username", "");
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUserByUsernameLive(username).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                List<Integer> favorites = user.getFavoriteCarIds();
                if (favorites == null) {
                    favorites = new ArrayList<>();
                    user.setFavoriteCarIds(favorites);
                    userViewModel.update(user);
                }

                final List<Integer> finalFavorites = favorites; // FINAL hale getiriyoruz

                boolean isFavorite = finalFavorites.contains(car.getId());

                // Yıldız ikonunu duruma göre ayarla
                binding.imgFavoriteStar.setImageResource(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_border);

                // Tıklanınca favori durumunu değiştir
                binding.imgFavoriteStar.setOnClickListener(v -> {
                    if (finalFavorites.contains(car.getId())) {
                        finalFavorites.remove(Integer.valueOf(car.getId()));
                        binding.imgFavoriteStar.setImageResource(R.drawable.ic_star_border);
                        Toast.makeText(requireContext(), "Favoriden çıkarıldı", Toast.LENGTH_SHORT).show();
                    } else {
                        finalFavorites.add(car.getId());
                        binding.imgFavoriteStar.setImageResource(R.drawable.ic_star_filled);
                        Toast.makeText(requireContext(), "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                    }

                    user.setFavoriteCarIds(finalFavorites);
                    userViewModel.update(user);
                });
            }
        });

        userViewModel.getUserById(car.getUserId()).observe(getViewLifecycleOwner(), owner -> {
            if (owner != null) {
                binding.tvOwnerName.setText(owner.getFirstName() + " " + owner.getLastName());
                binding.tvOwnerPhone.setText(owner.getPhone());
                binding.tvOwnerCity.setText(owner.getCity());
            } else {
                binding.tvOwnerName.setText("Bilinmiyor");
                binding.tvOwnerPhone.setText("Bilinmiyor");
                binding.tvOwnerCity.setText("Bilinmiyor");
            }
        });




    }
}
