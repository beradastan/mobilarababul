package com.example.mobilproje.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.mobilproje.R;
import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.Brand;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.databinding.FragmentAdminBinding;
import com.example.mobilproje.databinding.ItemCarAdminBinding;
import com.example.mobilproje.util.Constants;
import com.example.mobilproje.viewmodel.BrandViewModel;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class AdminFragment extends Fragment {
    private FragmentAdminBinding binding;
    private CarViewModel carViewModel;
    private BrandViewModel brandViewModel;
    private UserViewModel userViewModel;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private Integer parseInteger(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) return null;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void updateRecyclerView(List<Car> cars) {
        if (cars != null && !cars.isEmpty()) {
            AdminCarListAdapter adapter = new AdminCarListAdapter(userViewModel, cars);
            binding.recyclerViewAdminCars.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "İlan bulunamadı", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "car_database")
                .allowMainThreadQueries()
                .build();

        brandViewModel = new ViewModelProvider(this).get(BrandViewModel.class);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.recyclerViewAdminCars.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> colorOptionsAdmin = new ArrayList<>();
        colorOptionsAdmin.add("Hepsi");
        colorOptionsAdmin.addAll(Arrays.asList(Constants.COLOR_OPTIONS));
        ArrayAdapter<String> colorAdapterAdmin = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, colorOptionsAdmin);
        colorAdapterAdmin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerColorFilterAdmin.setAdapter(colorAdapterAdmin);

        List<String> transmissionOptionsAdmin = new ArrayList<>();
        transmissionOptionsAdmin.add("Hepsi");
        transmissionOptionsAdmin.addAll(Arrays.asList(Constants.TRANSMISSION_OPTIONS));
        ArrayAdapter<String> transmissionAdapterAdmin = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, transmissionOptionsAdmin);
        transmissionAdapterAdmin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTransmissionFilterAdmin.setAdapter(transmissionAdapterAdmin);

        List<String> fuelOptionsAdmin = new ArrayList<>();
        fuelOptionsAdmin.add("Hepsi");
        fuelOptionsAdmin.addAll(Arrays.asList(Constants.FUEL_OPTIONS));
        ArrayAdapter<String> fuelAdapterAdmin = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, fuelOptionsAdmin);
        fuelAdapterAdmin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFuelFilterAdmin.setAdapter(fuelAdapterAdmin);

        List<String> cityOptions = new ArrayList<>();
        cityOptions.add("Hepsi");
        cityOptions.addAll(Arrays.asList(Constants.CITY_OPTIONS));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cityOptions);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCityAdmin.setAdapter(cityAdapter);

        binding.btnSortAdmin.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), binding.btnSortAdmin);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, 0, "Fiyata Göre Azalan");
            menu.add(Menu.NONE, 1, 1, "Fiyata Göre Artan");
            menu.add(Menu.NONE, 2, 2, "Kilometreye Göre Azalan");
            menu.add(Menu.NONE, 3, 3, "Kilometreye Göre Artan");
            menu.add(Menu.NONE, 4, 4, "Yıla Göre Azalan");
            menu.add(Menu.NONE, 5, 5, "Yıla Göre Artan");

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0: carViewModel.getSortedCarsByPrice(false).observe(getViewLifecycleOwner(), this::updateRecyclerView); return true;
                    case 1: carViewModel.getSortedCarsByPrice(true).observe(getViewLifecycleOwner(), this::updateRecyclerView); return true;
                    case 2: carViewModel.getSortedCarsByKm(false).observe(getViewLifecycleOwner(), this::updateRecyclerView); return true;
                    case 3: carViewModel.getSortedCarsByKm(true).observe(getViewLifecycleOwner(), this::updateRecyclerView); return true;
                    case 4: carViewModel.getSortedCarsByYear(false).observe(getViewLifecycleOwner(), this::updateRecyclerView); return true;
                    case 5: carViewModel.getSortedCarsByYear(true).observe(getViewLifecycleOwner(), this::updateRecyclerView); return true;
                    default: return false;
                }
            });
            popupMenu.show();
        });

        binding.btnFilterAdmin.setOnClickListener(v -> {
            binding.filterLayoutAdmin.setVisibility(
                    binding.filterLayoutAdmin.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
            );
        });

        binding.btnApplyFiltersAdmin.setOnClickListener(v -> {
            Brand selectedBrand = (Brand) binding.spinnerBrandsAdmin.getSelectedItem();
            int selectedBrandId = (selectedBrand != null && !"Hepsi".equals(selectedBrand.getName())) ? selectedBrand.getId() : -1;
            Integer minYear = parseInteger(binding.etMinYearAdmin);
            Integer maxYear = parseInteger(binding.etMaxYearAdmin);
            Integer minPrice = parseInteger(binding.etMinPriceAdmin);
            Integer maxPrice = parseInteger(binding.etMaxPriceAdmin);
            Integer minKm = parseInteger(binding.etMinKmAdmin);
            Integer maxKm = parseInteger(binding.etMaxKmAdmin);

            String selectedColor = binding.spinnerColorFilterAdmin.getSelectedItem().toString();
            String selectedTransmission = binding.spinnerTransmissionFilterAdmin.getSelectedItem().toString();
            String selectedFuel = binding.spinnerFuelFilterAdmin.getSelectedItem().toString();
            String selectedCity = binding.spinnerCityAdmin.getSelectedItem().toString();

            carViewModel.getFilteredCars(selectedBrandId, minYear, maxYear, minPrice, maxPrice, minKm, maxKm,
                            selectedColor, selectedTransmission, selectedFuel , selectedCity)
                    .observe(getViewLifecycleOwner(), this::updateRecyclerView);

            binding.filterLayoutAdmin.setVisibility(View.GONE);
        });

        binding.btnGoToAdd.setOnClickListener(v -> {
            binding.addFormLayout.setVisibility(
                    binding.addFormLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
            );
        });

        binding.btnSaveBrand.setOnClickListener(v -> {
            String name = binding.etBrandName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Marka adı zorunludur", Toast.LENGTH_SHORT).show();
                return;
            }
            Brand brand = new Brand(name);
            brandViewModel.insert(brand);
            binding.etBrandName.setText("");
            Toast.makeText(getContext(), "Marka eklendi", Toast.LENGTH_SHORT).show();
        });

        brandViewModel.getAllBrands().observe(getViewLifecycleOwner(), brands -> {
            if (brands != null && !brands.isEmpty()) {
                List<Brand> allBrands = new ArrayList<>();
                allBrands.add(new Brand("Hepsi"));
                allBrands.addAll(brands);

                ArrayAdapter<Brand> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allBrands);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerBrandsAdmin.setAdapter(adapter);
            }
        });

        carViewModel.getAllCars().observe(getViewLifecycleOwner(), this::updateRecyclerView);


        binding.btnLogout.setOnClickListener(v -> {
            requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_adminFragment_to_loginFragment);
        });

    }

    private class AdminCarListAdapter extends RecyclerView.Adapter<AdminCarListAdapter.AdminCarViewHolder> {
        private final List<Car> carList;
        private final UserViewModel userViewModel;

        AdminCarListAdapter(UserViewModel userViewModel, List<Car> carList) {
            this.userViewModel = userViewModel;
            this.carList = carList;
        }

        @NonNull
        @Override
        public AdminCarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemCarAdminBinding binding = ItemCarAdminBinding.inflate(inflater, parent, false);
            return new AdminCarViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull AdminCarViewHolder holder, int position) {
            holder.bind(carList.get(position));
        }

        @Override
        public int getItemCount() {
            return carList.size();
        }

        class AdminCarViewHolder extends RecyclerView.ViewHolder {
            private final ItemCarAdminBinding binding;

            AdminCarViewHolder(ItemCarAdminBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            void bind(Car car) {
                binding.tvPrice.setText(car.price + " ₺");
                binding.tvTitle.setText(car.title);
                binding.tvUserId.setText("Kullanıcı ID: " + car.userId);
                binding.tvCity.setText(car.city);

                carViewModel.getBrandById(car.brandId).observe(getViewLifecycleOwner(), brand -> {
                    if (brand != null) {
                        binding.tvBrandModel.setText(brand.name + " - " + car.model);
                    }
                });

                userViewModel.getUserById(car.userId).observeForever(user -> {
                    if (user != null) {
                        String userInfo = "Kullanıcı Adı : " + user.getUsername();
                        binding.tvUsername.setText(userInfo);
                    } else {
                        binding.tvUserId.setText("Kullanıcı bilgisi yok");
                    }
                });

                if (car.imageBase64List != null && !car.imageBase64List.isEmpty()) {
                    byte[] decodedBytes = Base64.decode(car.imageBase64List.get(0), Base64.DEFAULT);
                    Glide.with(binding.getRoot().getContext())
                            .asBitmap()
                            .load(decodedBytes)
                            .into(binding.imgCar);
                }
            }
        }
    }
}

