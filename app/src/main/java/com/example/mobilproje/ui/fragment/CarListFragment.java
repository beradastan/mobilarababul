package com.example.mobilproje.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilproje.R;
import com.example.mobilproje.databinding.FragmentCarListBinding;
import com.example.mobilproje.databinding.ItemCarBinding;
import com.example.mobilproje.viewmodel.BrandViewModel;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.viewmodel.UserViewModel;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarListFragment extends Fragment {

    private FragmentCarListBinding binding;      // ViewBinding sınıfı
    private BrandViewModel brandViewModel;       // Marka verisi için ViewModel
    private CarViewModel carViewModel;           // Araç verisi için ViewModel
    private UserViewModel userViewModel;         // Kullanıcı bilgileri için ViewModel

    // Layout bağlanıyor
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // Sayı içeren EditText'leri güvenli şekilde integer'a dönüştürür
    private Integer parseInteger(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) return null;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Araç listesini RecyclerView'a bağlayan fonksiyon
    private void updateRecyclerView(List<Car> cars) {
        if (cars != null && !cars.isEmpty()) {
            CarListAdapter adapter = new CarListAdapter(cars , userViewModel);
            binding.recyclerViewCars.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "İlan bulunamadı", Toast.LENGTH_SHORT).show();
        }
    }

    // Fragment yüklendiğinde yapılacak işlemler
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sıralama butonu tıklanınca popup menü gösterilir
        binding.btnSort.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), binding.btnSort);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, 0, "Fiyata Göre Azalan");
            menu.add(Menu.NONE, 1, 1, "Fiyata Göre Artan");
            menu.add(Menu.NONE, 2, 2, "Kilometreye Göre Azalan");
            menu.add(Menu.NONE, 3, 3, "Kilometreye Göre Artan");
            menu.add(Menu.NONE, 4, 4, "Yıla Göre Azalan");
            menu.add(Menu.NONE, 5, 5, "Yıla Göre Artan");

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        carViewModel.getSortedCarsByPrice(false).observe(getViewLifecycleOwner(), this::updateRecyclerView);
                        return true;
                    case 1:
                        carViewModel.getSortedCarsByPrice(true).observe(getViewLifecycleOwner(), this::updateRecyclerView);
                        return true;
                    case 2:
                        carViewModel.getSortedCarsByKm(false).observe(getViewLifecycleOwner(), this::updateRecyclerView);
                        return true;
                    case 3:
                        carViewModel.getSortedCarsByKm(true).observe(getViewLifecycleOwner(), this::updateRecyclerView);
                        return true;
                    case 4:
                        carViewModel.getSortedCarsByYear(false).observe(getViewLifecycleOwner(), this::updateRecyclerView);
                        return true;
                    case 5:
                        carViewModel.getSortedCarsByYear(true).observe(getViewLifecycleOwner(), this::updateRecyclerView);
                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        });

        // ViewModel tanımlamaları
        brandViewModel = new ViewModelProvider(this).get(BrandViewModel.class);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Filtre spinner'larının veri kaynakları
        setupSpinner(binding.spinnerColorFilter, Constants.COLOR_OPTIONS);
        setupSpinner(binding.spinnerTransmissionFilter, Constants.TRANSMISSION_OPTIONS);
        setupSpinner(binding.spinnerFuelFilter, Constants.FUEL_OPTIONS);
        setupSpinner(binding.spinnerCityFilter, Constants.CITY_OPTIONS);

        // Filtre panelini aç/kapa
        binding.btnFilter.setOnClickListener(v -> {
            binding.filterLayout.setVisibility(
                    binding.filterLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
            );
        });

        // Araç ekle butonu
        binding.fabAddCar.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_carListFragment_to_addCarFragment);
        });

        // Filtreyi uygula
        binding.btnApplyFilters.setOnClickListener(v -> {
            Brand selectedBrand = (Brand) binding.spinnerBrands.getSelectedItem();
            int selectedBrandId = (selectedBrand != null && !selectedBrand.getName().equals("Hepsi")) ? selectedBrand.getId() : -1;

            Integer minYear = parseInteger(binding.etMinYear);
            Integer maxYear = parseInteger(binding.etMaxYear);
            Integer minPrice = parseInteger(binding.etMinPrice);
            Integer maxPrice = parseInteger(binding.etMaxPrice);
            Integer minKm = parseInteger(binding.etMinKm);
            Integer maxKm = parseInteger(binding.etMaxKm);

            String selectedColor = binding.spinnerColorFilter.getSelectedItem().toString();
            String selectedTransmission = binding.spinnerTransmissionFilter.getSelectedItem().toString();
            String selectedFuel = binding.spinnerFuelFilter.getSelectedItem().toString();
            String selectedCity = binding.spinnerCityFilter.getSelectedItem().toString();

            carViewModel.getFilteredCars(
                    selectedBrandId, minYear, maxYear, minPrice, maxPrice, minKm, maxKm,
                    selectedColor, selectedTransmission, selectedFuel, selectedCity
            ).observe(getViewLifecycleOwner(), this::updateRecyclerView);

            binding.filterLayout.setVisibility(View.GONE);
        });

        // Profil sayfasına git
        binding.btnProfile.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_carListFragment_to_profileFragment);
        });

        // Marka spinner'ını güncelle
        brandViewModel.getAllBrands().observe(getViewLifecycleOwner(), brands -> {
            if (brands != null && !brands.isEmpty()) {
                List<Brand> allBrands = new ArrayList<>();
                allBrands.add(new Brand("Hepsi"));
                allBrands.addAll(brands);

                ArrayAdapter<Brand> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allBrands);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerBrands.setAdapter(adapter);
            }
        });

        // RecyclerView ayarları ve veri gözlemi
        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));
        carViewModel.getAllCars().observe(getViewLifecycleOwner(), this::updateRecyclerView);
    }

    // Spinner kurulumunu yapan yardımcı metot
    private void setupSpinner(android.widget.Spinner spinner, String[] options) {
        List<String> allOptions = new ArrayList<>();
        allOptions.add("Hepsi");
        allOptions.addAll(Arrays.asList(options));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, allOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // RecyclerView adapter sınıfı
    private class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarViewHolder> {
        private final List<Car> carList;
        private final UserViewModel userViewModel;

        public CarListAdapter(List<Car> carList, UserViewModel userViewModel) {
            this.carList = carList;
            this.userViewModel = userViewModel;
        }

        @NonNull
        @Override
        public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemCarBinding itemBinding = ItemCarBinding.inflate(inflater, parent, false);
            return new CarViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
            holder.bind(carList.get(position));
        }

        @Override
        public int getItemCount() {
            return carList.size();
        }

        // ViewHolder sınıfı
        class CarViewHolder extends RecyclerView.ViewHolder {
            private final ItemCarBinding itemBinding;

            CarViewHolder(ItemCarBinding binding) {
                super(binding.getRoot());
                this.itemBinding = binding;
            }

            void bind(Car car) {
                itemBinding.tvTitle.setText(car.title);
                itemBinding.tvCity.setText(car.city);
                itemBinding.tvPrice.setText(car.price + " ₺");

                // Marka adı getiriliyor
                carViewModel.getBrandById(car.brandId).observe(getViewLifecycleOwner(), brand -> {
                    if (brand != null) {
                        itemBinding.tvBrandModel.setText(brand.name + " - " + car.model);
                    }
                });

                // Görseli Glide ile yükle
                if (car.imageBase64List != null && !car.imageBase64List.isEmpty()) {
                    byte[] decodedBytes = Base64.decode(car.imageBase64List.get(0), Base64.DEFAULT);
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(decodedBytes)
                            .into(itemBinding.imgCar);
                }

                // Kart tıklanınca detay sayfasına git
                itemBinding.getRoot().setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("carId", car.getId());
                    NavHostFragment.findNavController(CarListFragment.this)
                            .navigate(R.id.action_carListFragment_to_carDetailFragment, bundle);
                });
            }
        }
    }
}
