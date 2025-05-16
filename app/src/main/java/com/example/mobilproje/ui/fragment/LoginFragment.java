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

    private FragmentLoginBinding binding;
    private UserViewModel userViewModel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SharedPreferences prefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String savedUsername = prefs.getString("username", "");

        if (isLoggedIn) {
            if ("admin".equalsIgnoreCase(savedUsername)) {
                NavHostFragment.findNavController(this).navigate(
                        R.id.action_loginFragment_to_adminFragment);
            } else {
                NavHostFragment.findNavController(this).navigate(
                        R.id.action_loginFragment_to_carListFragment);
            }
            return;
        }

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.execute(() -> {
                User user = userViewModel.login(username, password);

                requireActivity().runOnUiThread(() -> {
                    if (user != null) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("username", username);
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        if (user.username.equalsIgnoreCase("admin")) {
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_loginFragment_to_adminFragment);
                        } else {
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_loginFragment_to_carListFragment);
                        }
                    } else {
                        Toast.makeText(getContext(), "Kullanıcı adı veya şifre hatalı", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        binding.btnRegister.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Kayıt için tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.equalsIgnoreCase("admin")) {
                Toast.makeText(getContext(), "Bu kullanıcı adı rezerve edilmiştir!", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.execute(() -> {
                User existing = userViewModel.getUserByUsername(username);

                requireActivity().runOnUiThread(() -> {
                    if (existing != null) {
                        Toast.makeText(getContext(), "Bu kullanıcı adı zaten alınmış", Toast.LENGTH_SHORT).show();
                    } else {
                        userViewModel.insert(new User(username, password));
                        Toast.makeText(getContext(), "Kayıt başarılı, şimdi giriş yapabilirsiniz", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
    }
}
