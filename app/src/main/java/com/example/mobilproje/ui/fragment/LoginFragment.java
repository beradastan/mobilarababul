package com.example.mobilproje.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.mobilproje.R;
import com.example.mobilproje.data.model.User;
import com.example.mobilproje.databinding.FragmentLoginBinding;
import com.example.mobilproje.viewmodel.UserViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;  // ViewBinding nesnesi
    private UserViewModel userViewModel;  // Kullanıcı işlemleri için ViewModel
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Arka plan işlemleri için thread havuzu

    // Fragment'ın görünümünü oluşturur
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);  // ViewBinding ile layout'u şişir
        return binding.getRoot();
    }

    // UI bileşenleri ve tıklama olayları burada tanımlanır
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UserViewModel örneği alınır
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Giriş butonuna tıklanma olayı
        binding.btnLogin.setOnClickListener(v -> {
            // EditText'lerden kullanıcı adı ve şifre alınır
            String username = binding.etUsername.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            // Alanlardan biri boşsa uyarı ver
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            // Giriş işlemini arka planda çalıştır
            executor.execute(() -> {
                User user = userViewModel.login(username, password); // Giriş kontrolü yapılır

                // Sonuç UI thread üzerinde gösterilir
                requireActivity().runOnUiThread(() -> {
                    if (user != null) {
                        // Başarılı giriş → kullanıcı bilgisi SharedPreferences'a kaydedilir
                        SharedPreferences prefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                        prefs.edit().putString("username", username).apply();

                        // Eğer kullanıcı adı "admin" ise admin paneline yönlendir
                        if (user.username.equalsIgnoreCase("admin")) {
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_loginFragment_to_adminFragment);
                        } else {
                            // Normal kullanıcı ise araç liste ekranına yönlendir
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_loginFragment_to_carListFragment);
                        }
                    } else {
                        // Giriş başarısızsa uyarı ver
                        Toast.makeText(getContext(), "Kullanıcı adı veya şifre hatalı", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        // Kayıt butonuna tıklanma olayı
        binding.btnRegister.setOnClickListener(v -> {
            // Kullanıcı adı ve şifre alınır
            String username = binding.etUsername.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            // Alanlar boşsa uyarı göster
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Kayıt için tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            // "admin" kullanıcı adı engellenmiştir
            if (username.equalsIgnoreCase("admin")) {
                Toast.makeText(getContext(), "Bu kullanıcı adı rezerve edilmiştir!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Arka planda kayıt kontrolü yapılır
            executor.execute(() -> {
                User existing = userViewModel.getUserByUsername(username); // Aynı kullanıcı adı var mı?

                requireActivity().runOnUiThread(() -> {
                    if (existing != null) {
                        // Aynı kullanıcı varsa uyarı göster
                        Toast.makeText(getContext(), "Bu kullanıcı adı zaten alınmış", Toast.LENGTH_SHORT).show();
                    } else {
                        // Yeni kullanıcı veritabanına eklenir
                        userViewModel.insert(new User(username, password));
                        Toast.makeText(getContext(), "Kayıt başarılı, şimdi giriş yapabilirsiniz", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    // Fragment kapatıldığında kaynaklar temizlenir
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown(); // Thread havuzu kapatılır
    }
}
