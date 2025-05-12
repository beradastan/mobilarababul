package com.example.mobilproje.ui.fragment;

import android.content.Context;
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

import com.example.mobilproje.R;
import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.User;
import com.example.mobilproje.databinding.FragmentProfileBinding;
import com.example.mobilproje.ui.adapter.FavoriteCarAdapter;
import com.example.mobilproje.ui.adapter.MyCarAdapter;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.viewmodel.UserViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;             // ViewBinding ile layout erişimi
    private UserViewModel userViewModel;                // Kullanıcı verileri için ViewModel
    private CarViewModel carViewModel;                  // Araç verileri için ViewModel
    private ExecutorService executor;                   // Arka plan işlemleri için thread havuzu
    private MyCarAdapter myCarAdapter;                  // Kendi araçlarını listeleyen adapter

    private User currentUser;                           // Oturum açan kullanıcı bilgisi

    // Layout şişirme işlemi
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // Fragment görünümü oluşturulduktan sonra
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel ve Executor tanımlamaları
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        executor = Executors.newSingleThreadExecutor();

        // Kullanıcı bilgilerini ve favori araçlarını yükle
        loadUserData();
        listFavoriteCars();

        // Butonlar için click işlemleri
        binding.btnSaveProfile.setOnClickListener(v -> saveUserData());
        binding.btnLogout.setOnClickListener(v -> logout());
    }

    // Kullanıcının profil bilgilerini yükle
    private void loadUserData() {
        executor.execute(() -> {
            // SharedPreferences'tan username çekilir
            String username = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                    .getString("username", "");

            // Kullanıcı verisi veritabanından alınır
            currentUser = userViewModel.getUserByUsername(username);

            // UI thread üzerinde TextView'lara veriler doldurulur
            requireActivity().runOnUiThread(() -> {
                if (currentUser != null) {
                    binding.etFirstName.setText(currentUser.getFirstName());
                    binding.etLastName.setText(currentUser.getLastName());
                    binding.etPhone.setText(currentUser.getPhone());
                    binding.etEmail.setText(currentUser.getEmail());

                    // Kullanıcının ilanlarını listele
                    listMyCars(currentUser.id);
                }
            });
        });
    }

    // Kullanıcı bilgilerini güncelleme
    private void saveUserData() {
        // EditText'lerden veri alınır
        String name = binding.etFirstName.getText().toString().trim();
        String surname = binding.etLastName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();

        // Boş alan kontrolü
        if (name.isEmpty() || surname.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Ad, Soyad, Telefon ve Mail boş bırakılamaz!", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            if (currentUser != null) {
                // Kullanıcı nesnesi güncellenir
                currentUser.setFirstName(name);
                currentUser.setLastName(surname);
                currentUser.setPhone(phone);
                currentUser.setEmail(email);

                // Veritabanına kaydedilir
                userViewModel.update(currentUser);

                // Başarı mesajı
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Profil güncellendi", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // Kullanıcının ilanlarını listeleme
    private void listMyCars(int userId) {
        // ViewModel üzerinden kendi ilanlarını çek
        carViewModel.getCarsByUserId(userId).observe(getViewLifecycleOwner(), cars -> {
            if (cars != null && !cars.isEmpty()) {
                // Adapter tanımlanır
                myCarAdapter = new MyCarAdapter(cars, car -> {
                    // Araca tıklandığında güncelleme ekranına git
                    Bundle bundle = new Bundle();
                    bundle.putInt("carId", car.getId());
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_profileFragment_to_addCarFragment, bundle);
                }, AppDatabase.getInstance(requireContext())); // Room DB nesnesi gönderilir

                // RecyclerView ayarları
                binding.rvMyCars.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.rvMyCars.setAdapter(myCarAdapter);
            } else {
                Toast.makeText(requireContext(), "Henüz hiç ilanınız yok.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Kullanıcının favori ilanlarını listeleme
    private void listFavoriteCars() {
        // SharedPreferences'tan giriş yapan kullanıcı alınır
        String username = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                .getString("username", "");

        // ViewModel'den kullanıcı verisi alınır
        userViewModel.getUserByUsernameLive(username).observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getFavoriteCarIds() != null && !user.getFavoriteCarIds().isEmpty()) {
                // Favori araç ID'lerine göre araç listesi alınır
                carViewModel.getCarsByIds(user.getFavoriteCarIds()).observe(getViewLifecycleOwner(), favoriteCars -> {
                    if (favoriteCars != null && !favoriteCars.isEmpty()) {
                        // Adapter ile veriler bağlanır
                        FavoriteCarAdapter favoriteCarAdapter = new FavoriteCarAdapter(favoriteCars, car -> {
                            Bundle bundle = new Bundle();
                            bundle.putInt("carId", car.getId());
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_profileFragment_to_carDetailFragment, bundle);
                        });

                        binding.rvFavoriteCars.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.rvFavoriteCars.setAdapter(favoriteCarAdapter);
                    } else {
                        Toast.makeText(requireContext(), "Henüz favori eklediğiniz bir ilan yok.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Çıkış yapıldığında SharedPreferences temizlenir ve login ekranına dönülür
    private void logout() {
        requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_profileFragment_to_loginFragment);
    }

    // Fragment kapatılırken executor kapatılır ve binding sıfırlanır
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executor != null) {
            executor.shutdown();
        }
        binding = null;
    }
}
