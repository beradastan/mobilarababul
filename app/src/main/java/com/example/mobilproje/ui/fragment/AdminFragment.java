package com.example.mobilproje.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobilproje.databinding.FragmentAdminBinding;

public class AdminFragment extends Fragment {

    private FragmentAdminBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnGoToAddBrand.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(com.example.mobilproje.R.id.action_adminFragment_to_addBrandFragment);
        });

        binding.btnGoToAllCars.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(com.example.mobilproje.R.id.action_adminFragment_to_carListFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}