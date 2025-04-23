package com.example.mobilproje.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.data.model.User;
import com.example.mobilproje.databinding.FragmentAddCarBinding;
import com.example.mobilproje.viewmodel.BrandViewModel;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddCarFragment extends Fragment {

    private FragmentAddCarBinding binding;
    private CarViewModel carViewModel;
    private BrandViewModel brandViewModel;
    private UserViewModel userViewModel;
    private List<Brand> brandList;
    private static final int PICK_IMAGES_REQUEST = 1;
    private final List<Uri> selectedImageUris = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();





    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddCarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        brandViewModel = new ViewModelProvider(this).get(BrandViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        brandViewModel.getAllBrands().observe(getViewLifecycleOwner(), brands -> {
            brandList = brands;
            ArrayAdapter<Brand> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, brands);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerBrand.setAdapter(adapter);
        });

        binding.imgCar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Fotoğraf Seç"), PICK_IMAGES_REQUEST);
        });

        binding.btnSave.setOnClickListener(v -> {
            int brandPosition = binding.spinnerBrand.getSelectedItemPosition();
            if (brandPosition == -1 || selectedImageUris.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun ve fotoğraf seçin", Toast.LENGTH_SHORT).show();
                return;
            }

            Brand selectedBrand = brandList.get(brandPosition);
            String model = binding.etModel.getText().toString();
            int year = Integer.parseInt(binding.etYear.getText().toString());
            int km = Integer.parseInt(binding.etKm.getText().toString());
            int price = Integer.parseInt(binding.etPrice.getText().toString());
            String description = binding.etDescription.getText().toString();

            // Kullanıcıyı bul (SharedPreferences'tan)
            String username = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("username", "");

            executor.execute(() -> {
                User currentUser = userViewModel.getUserByUsername(username);
                if (currentUser != null) {
                    List<String> imageStrings = new ArrayList<>();
                    for (Uri uri : selectedImageUris) {
                        imageStrings.add(uri.toString());
                    }

                    Car car = new Car(selectedBrand.id, model, year, km, price, description, imageStrings, currentUser.id);
                    carViewModel.insert(car);

                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Araç eklendi!", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(this).navigateUp();
                    });
                }
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUris.clear();
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImageUris.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    selectedImageUris.add(data.getData());
                }

                // İlk resmi imageView'de göster (temsilî amaçlı)
                if (!selectedImageUris.isEmpty()) {
                    binding.imgCar.setImageURI(selectedImageUris.get(0));
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        executor.shutdown();
    }
}
