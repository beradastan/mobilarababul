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
import com.example.mobilproje.data.model.User;
import com.example.mobilproje.databinding.FragmentAdminBinding;
import com.example.mobilproje.databinding.ItemCarAdminBinding;
import com.example.mobilproje.viewmodel.BrandViewModel;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {
    private FragmentAdminBinding binding;
    private CarViewModel carViewModel;
    private BrandViewModel brandViewModel;
    private UserViewModel userViewModel;
    private AppDatabase db;
    private List<Car> carList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private Integer parseInteger(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) {
            return null;
        } else {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return null;
            }
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

        binding.btnFilterAdmin.setOnClickListener(v -> {
            if (binding.filterLayoutAdmin.getVisibility() == View.GONE) {
                binding.filterLayoutAdmin.setVisibility(View.VISIBLE);
            } else {
                binding.filterLayoutAdmin.setVisibility(View.GONE);
            }
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

            carViewModel.getFilteredCars(selectedBrandId, minYear, maxYear, minPrice, maxPrice, minKm, maxKm)
                    .observe(getViewLifecycleOwner(), this::updateRecyclerView);

            binding.filterLayoutAdmin.setVisibility(View.GONE);
        });

        binding.btnGoToAddBrand.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_adminFragment_to_addBrandFragment);
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

        carViewModel.getAllCars().observe(getViewLifecycleOwner(), cars -> {
            if (cars != null && !cars.isEmpty()) {
                carList = new ArrayList<>(cars);
                AdminCarListAdapter adapter = new AdminCarListAdapter(userViewModel, carList);
                binding.recyclerViewAdminCars.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "İlan bulunamadı", Toast.LENGTH_SHORT).show();
            }
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
            ItemCarAdminBinding itemBinding = ItemCarAdminBinding.inflate(inflater, parent, false);
            return new AdminCarViewHolder(itemBinding);
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
            private final ItemCarAdminBinding itemBinding;

            AdminCarViewHolder(ItemCarAdminBinding binding) {
                super(binding.getRoot());
                this.itemBinding = binding;
            }

            void bind(Car car) {
                itemBinding.tvModel.setText("Model: " + car.model);
                itemBinding.tvYear.setText("Yıl: " + car.year);
                itemBinding.tvKm.setText("KM: " + car.km);
                itemBinding.tvPrice.setText("Fiyat: " + car.price + " ₺");
                itemBinding.tvDescription.setText(car.description);
                itemBinding.tvUserId.setText("Kullanıcı ID: " + car.userId);

                carViewModel.getBrandById(car.brandId).observe(getViewLifecycleOwner(), brand -> {
                    if (brand != null) {
                        itemBinding.tvBrand.setText("Marka: " + brand.name);
                    }
                });

                userViewModel.getUserById(car.userId).observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        itemBinding.tvUsername.setText("Kullanıcı: " + user.username);
                        itemBinding.tvUserId.setText("Kullanıcı ID: " + user.id);
                    }
                });

                if (car.imageBase64List != null && !car.imageBase64List.isEmpty()) {
                    byte[] decodedBytes = Base64.decode(car.imageBase64List.get(0), Base64.DEFAULT);
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(decodedBytes)
                            .into(itemBinding.imgCar);
                }



                itemBinding.btnDelete.setOnClickListener(v -> {
                    db.carDao().deleteCarById(car.id);
                    int position = getAdapterPosition();
                    carList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(requireContext(), "Araç silindi", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}
