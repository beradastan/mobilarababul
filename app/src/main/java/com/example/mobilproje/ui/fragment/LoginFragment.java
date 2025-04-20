package com.example.mobilproje.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mobilproje.R;
import com.example.mobilproje.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();

            if (!username.isEmpty()) {
                // Kullanıcıyı SharedPreferences'a kaydet
                SharedPreferences prefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                prefs.edit().putString("username", username).apply();

                // Giriş yaptıktan sonra CarListFragment'e geçiş
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_loginFragment_to_carListFragment);

            } else {
                Toast.makeText(requireContext(), "Lütfen kullanıcı adı girin", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
