package com.example.mobilproje.ui.fragment;

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

import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.databinding.FragmentAddBrandBinding;
import com.example.mobilproje.viewmodel.BrandViewModel;

public class AddBrandFragment extends Fragment {

    private FragmentAddBrandBinding binding;
    private BrandViewModel brandViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddBrandBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        brandViewModel = new ViewModelProvider(this).get(BrandViewModel.class);

        binding.btnSaveBrand.setOnClickListener(v -> {
            String name = binding.etBrandName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Marka adı zorunludur", Toast.LENGTH_SHORT).show();
                return;
            }

            Brand brand = new Brand(name);
            brandViewModel.insert(brand);

            Toast.makeText(getContext(), "Marka eklendi", Toast.LENGTH_SHORT).show();

            NavHostFragment.findNavController(this).navigateUp();
        });
    }
}