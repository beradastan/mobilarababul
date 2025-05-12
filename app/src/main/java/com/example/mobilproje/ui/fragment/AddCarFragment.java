package com.example.mobilproje.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import com.example.mobilproje.R;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.data.model.User;
import com.example.mobilproje.databinding.FragmentAddCarBinding;
import com.example.mobilproje.viewmodel.BrandViewModel;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.viewmodel.UserViewModel;
import com.example.mobilproje.util.Constants;

import java.io.IOException;
import java.io.InputStream;
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

    private boolean isEditMode = false;
    private int editCarId = -1;
    private Car existingCar;

    // URI'den Base64 string'e dönüştürmek için kullanılan yardımcı metot
    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // ViewBinding ile layout'u şişiriyoruz
        binding = FragmentAddCarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel'lar başlatılıyor
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        brandViewModel = new ViewModelProvider(this).get(BrandViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Eğer bir carId geldiyse düzenleme moduna giriyoruz
        if (getArguments() != null && getArguments().containsKey("carId")) {
            isEditMode = true;
            editCarId = getArguments().getInt("carId");
        }

        // Spinnerlara sabit veriler atanıyor
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Constants.COLOR_OPTIONS);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.etSpinnerColor.setAdapter(colorAdapter);

        ArrayAdapter<String> transmissionAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Constants.TRANSMISSION_OPTIONS);
        transmissionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.etSpinnerTransmission.setAdapter(transmissionAdapter);

        ArrayAdapter<String> fuelAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Constants.FUEL_OPTIONS);
        fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.etSpinnerFuel.setAdapter(fuelAdapter);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Constants.CITY_OPTIONS);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.etSpinnerCity.setAdapter(cityAdapter);

        // Tüm markalar alınıyor ve spinnera atanıyor
        brandViewModel.getAllBrands().observe(getViewLifecycleOwner(), brands -> {
            brandList = brands;
            ArrayAdapter<Brand> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, brands);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerBrand.setAdapter(adapter);

            // Eğer düzenleme modundaysa, ilgili verileri doldur
            if (isEditMode) {
                loadCarData();
            }
        });

        // Resim seçmek için galeri açılıyor
        binding.imgCar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Fotoğraf Seç"), PICK_IMAGES_REQUEST);
        });

        // Kayıt butonuna tıklandığında
        binding.btnSave.setOnClickListener(v -> {
            int brandPosition = binding.spinnerBrand.getSelectedItemPosition();
            if (brandPosition == -1 || (selectedImageUris.isEmpty() && !isEditMode)) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun ve fotoğraf seçin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Form alanları alınıyor
            Brand selectedBrand = brandList.get(brandPosition);
            String model = binding.etModel.getText().toString();
            int year = Integer.parseInt(binding.etYear.getText().toString());
            int km = Integer.parseInt(binding.etKm.getText().toString());
            int price = Integer.parseInt(binding.etPrice.getText().toString());
            String description = binding.etDescription.getText().toString();
            String title = binding.etTitle.getText().toString();
            String selectedColor = binding.etSpinnerColor.getSelectedItem().toString();
            String username = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("username", "");
            String selectedTransmission = binding.etSpinnerTransmission.getSelectedItem().toString();
            String selectedFuel = binding.etSpinnerFuel.getSelectedItem().toString();
            String selectedCity = binding.etSpinnerCity.getSelectedItem().toString();

            // Arka planda veritabanı işlemleri
            executor.execute(() -> {
                User currentUser = userViewModel.getUserByUsername(username);
                if (currentUser != null) {
                    List<String> imageBase64List = new ArrayList<>();
                    if (!selectedImageUris.isEmpty()) {
                        for (Uri uri : selectedImageUris) {
                            String imageBase64 = uriToBase64(uri);
                            if (imageBase64 != null) {
                                imageBase64List.add(imageBase64);
                            }
                        }
                    } else if (isEditMode) {
                        imageBase64List = existingCar.getImageBase64List(); // Eski fotoğraflar korunur
                    }

                    // Car nesnesi oluşturuluyor
                    Car car = new Car(selectedBrand.id, model, year, km, price, description, imageBase64List, currentUser.id, title , selectedColor , selectedTransmission , selectedFuel , selectedCity);
                    if (isEditMode) {
                        car.setId(editCarId);
                        carViewModel.update(car);
                    } else {
                        carViewModel.insert(car);
                    }

                    // UI tarafına geri dönüş
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), isEditMode ? "Araç güncellendi!" : "Araç eklendi!", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(this).navigateUp();
                    });
                }
            });
        });
    }

    // Mevcut araç verilerini yüklemek için kullanılan metot
    private void loadCarData() {
        carViewModel.getCarById(editCarId).observe(getViewLifecycleOwner(), car -> {
            if (car != null) {
                existingCar = car;
                binding.etModel.setText(car.getModel());
                binding.etYear.setText(String.valueOf(car.getYear()));
                binding.etKm.setText(String.valueOf(car.getKm()));
                binding.etPrice.setText(String.valueOf(car.getPrice()));
                binding.etDescription.setText(car.getDescription());
                binding.etTitle.setText(car.getTitle());

                // Spinner'lar güncelleniyor
                setSpinnerSelection(binding.etSpinnerColor, Constants.COLOR_OPTIONS, car.getColor());
                setSpinnerSelection(binding.etSpinnerTransmission, Constants.TRANSMISSION_OPTIONS, car.getTransmissionType());
                setSpinnerSelection(binding.etSpinnerFuel, Constants.FUEL_OPTIONS, car.getFuelType());
                setSpinnerSelection(binding.etSpinnerCity, Constants.CITY_OPTIONS, car.getCity());

                // İlk resmi göster
                if (car.getImageBase64List() != null && !car.getImageBase64List().isEmpty()) {
                    byte[] decodedString = Base64.decode(car.getImageBase64List().get(0), Base64.DEFAULT);
                    binding.imgCar.setImageBitmap(android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                }

                // Marka spinner'ını güncelle
                for (int i = 0; i < brandList.size(); i++) {
                    if (brandList.get(i).getId() == car.getBrandId()) {
                        binding.spinnerBrand.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    // Spinner'da belirtilen değer seçili olacak şekilde ayarlanır
    private void setSpinnerSelection(android.widget.Spinner spinner, String[] options, String value) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    // Galeriden dönen sonuçlar burada işlenir
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

                // Seçilen ilk resmi göster
                if (!selectedImageUris.isEmpty()) {
                    binding.imgCar.setImageURI(selectedImageUris.get(0));
                }
            }
        }
    }

    // Fragment kapatılırken kaynaklar serbest bırakılır
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        executor.shutdown();
    }
}
