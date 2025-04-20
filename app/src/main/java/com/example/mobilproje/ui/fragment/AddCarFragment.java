package com.example.mobilproje.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.mobilproje.R;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.databinding.FragmentAddCarBinding;
import com.example.mobilproje.viewmodel.BrandViewModel;
import com.example.mobilproje.viewmodel.CarViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCarFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AddCarFragment extends Fragment {

    private FragmentAddCarBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private CarViewModel carViewModel;
    private BrandViewModel brandViewModel;
    private List<Brand> brandList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddCarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        brandViewModel = new ViewModelProvider(this).get(BrandViewModel.class);

        // Marka verilerini çek
        brandViewModel.getAllBrands().observe(getViewLifecycleOwner(), brands -> {
            brandList = brands;
            ArrayAdapter<Brand> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, brands);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerBrand.setAdapter(adapter);
        });

        // Fotoğraf seçimi
        binding.imgCar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Kaydet butonu
        binding.btnSave.setOnClickListener(v -> {
            int selectedBrandPosition = binding.spinnerBrand.getSelectedItemPosition();
            if (selectedBrandPosition == -1 || selectedImageUri == null) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun ve fotoğraf seçin", Toast.LENGTH_SHORT).show();
                return;
            }

            Brand selectedBrand = brandList.get(selectedBrandPosition);
            String model = binding.etModel.getText().toString();
            int year = Integer.parseInt(binding.etYear.getText().toString());
            int km = Integer.parseInt(binding.etKm.getText().toString());
            int price = Integer.parseInt(binding.etPrice.getText().toString());
            String description = binding.etDescription.getText().toString();
            String imageUri = selectedImageUri.toString();

            Car car = new Car(selectedBrand.id, model, year, km, price, description, imageUri);
            carViewModel.insert(car);

            Toast.makeText(requireContext(), "Araç eklendi!", Toast.LENGTH_SHORT).show();

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_addCarFragment_to_carListFragment);
        });
    }

    // Galeriden seçilen resmi al
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            binding.imgCar.setImageURI(selectedImageUri);
        }
    }
}
