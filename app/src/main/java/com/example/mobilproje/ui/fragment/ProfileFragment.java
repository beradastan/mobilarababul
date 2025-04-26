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
import com.example.mobilproje.ui.adapter.MyCarAdapter;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.viewmodel.UserViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;
    private CarViewModel carViewModel;
    private ExecutorService executor;
    private MyCarAdapter myCarAdapter;

    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        executor = Executors.newSingleThreadExecutor();

        loadUserData();

        binding.btnSaveProfile.setOnClickListener(v -> saveUserData());
        binding.btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserData() {
        executor.execute(() -> {
            String username = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                    .getString("username", "");

            currentUser = userViewModel.getUserByUsername(username);

            requireActivity().runOnUiThread(() -> {
                if (currentUser != null) {
                    binding.etFirstName.setText(currentUser.getFirstName());
                    binding.etLastName.setText(currentUser.getLastName());
                    binding.etPhone.setText(currentUser.getPhone());
                    binding.etEmail.setText(currentUser.getEmail());
                    binding.etCity.setText(currentUser.getCity());

                    listMyCars(currentUser.id);  // Kullanıcı yüklendikten sonra arabaları listele
                }
            });
        });
    }

    private void saveUserData() {
        String name = binding.etFirstName.getText().toString().trim();
        String surname = binding.etLastName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Ad, Soyad, Telefon ve Mail boş bırakılamaz!", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            if (currentUser != null) {
                currentUser.setFirstName(name);
                currentUser.setLastName(surname);
                currentUser.setPhone(phone);
                currentUser.setEmail(email);
                currentUser.setCity(city);

                userViewModel.update(currentUser);

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Profil güncellendi", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void listMyCars(int userId) {
        carViewModel.getCarsByUserId(userId).observe(getViewLifecycleOwner(), cars -> {
            if (cars != null && !cars.isEmpty()) {
                myCarAdapter = new MyCarAdapter(cars, car -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("carId", car.getId());
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_profileFragment_to_addCarFragment, bundle);
                }, AppDatabase.getInstance(requireContext())); // Buraya db gönderiyoruz
                binding.rvMyCars.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.rvMyCars.setAdapter(myCarAdapter);
            } else {
                Toast.makeText(requireContext(), "Henüz hiç ilanınız yok.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void logout() {
        requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_profileFragment_to_loginFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executor != null) {
            executor.shutdown();
        }
        binding = null;
    }
}
