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

import com.example.mobilproje.R;
import com.example.mobilproje.databinding.FragmentCarDetailBinding;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.viewmodel.UserViewModel;
import com.example.mobilproje.ui.adapter.CarImagePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CarDetailFragment extends Fragment {

    private FragmentCarDetailBinding binding; // ViewBinding
    private CarViewModel carViewModel;        // Araç verisi için ViewModel
    private int carId;                        // Detayı gösterilecek aracın ID'si

    // Layout şişirme işlemi
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // View oluşturulduktan sonra çalışır
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gönderilen argümandan carId alınır
        if (getArguments() != null) {
            carId = getArguments().getInt("carId", -1);
        }

        // ViewModel tanımlanır
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);

        // carId geçerliyse veritabanından o araç çağrılır
        if (carId != -1) {
            carViewModel.getCarById(carId).observe(getViewLifecycleOwner(), car -> {
                if (car != null) {
                    bindCarData(car); // Veriler ekrana basılır
                }
            });
        }
    }

    // Verileri UI'ya bağlayan metot
    private void bindCarData(Car car) {
        // Açıklama direkt gösterilir
        binding.tvDescription.setText(car.getDescription());

        // Marka adı getirilir
        carViewModel.getBrandById(car.brandId).observe(getViewLifecycleOwner(), brand -> {
            if (brand != null) {
                binding.tvBrand.setText(brand.name);
            }
        });

        // Diğer bilgiler set edilir
        binding.tvModel.setText(String.valueOf(car.getModel()));
        binding.tvYear.setText(String.valueOf(car.getYear()));
        binding.tvKm.setText(String.valueOf(car.getKm()));
        binding.tvPrice.setText(car.getPrice() + " ₺");
        binding.tvCarTitle.setText(car.getTitle());
        binding.tvColor.setText(car.getColor());
        binding.tvTransmission.setText(car.getTransmissionType());
        binding.tvFuel.setText(car.getFuelType());
        binding.tvOwnerCity.setText(car.getCity());

        // Fotoğraflar decode edilip bitmap'e çevrilir
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

        // Görseller varsa ViewPager2'ye bağlanır
        if (!bitmapList.isEmpty()) {
            CarImagePagerAdapter adapter = new CarImagePagerAdapter(bitmapList);
            binding.viewPagerCarImages.setAdapter(adapter);
        }

        // Favori kontrolü için kullanıcı adı alınır
        String username = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("username", "");
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Kullanıcının favori listesi getirilir
        userViewModel.getUserByUsernameLive(username).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                List<Integer> favorites = user.getFavoriteCarIds();
                if (favorites == null) {
                    favorites = new ArrayList<>();
                    user.setFavoriteCarIds(favorites);
                    userViewModel.update(user); // Favori listesi null ise oluşturulur
                }

                final List<Integer> finalFavorites = favorites;

                // Favori ikonu duruma göre gösterilir
                boolean isFavorite = finalFavorites.contains(car.getId());
                binding.imgFavoriteStar.setImageResource(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_border);

                // Favoriye ekle/çıkar işlemi
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

        // Aracı ekleyen kullanıcının bilgileri getirilir
        userViewModel.getUserById(car.getUserId()).observe(getViewLifecycleOwner(), owner -> {
            if (owner.getFirstName() == null && owner.getLastName() == null) {
                binding.tvOwnerName.setText("Bilinmiyor");
            }
            if (owner.getPhone() == null) {
                binding.tvOwnerPhone.setText("Bilinmiyor");
            } else {
                binding.tvOwnerName.setText(owner.getFirstName() + " " + owner.getLastName());
                binding.tvOwnerPhone.setText(owner.getPhone());
            }
        });
    }
}
