package com.example.mobilproje.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.mobilproje.R;
import com.example.mobilproje.databinding.FragmentCarListBinding;
import com.example.mobilproje.databinding.ItemCarBinding;
import com.example.mobilproje.viewmodel.BrandViewModel;
import com.example.mobilproje.viewmodel.CarViewModel;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.data.model.Brand;

import java.util.ArrayList;
import java.util.List;

public class CarListFragment extends Fragment {
    private BrandViewModel brandViewModel;  // BrandViewModel'i ekliyoruz

    private FragmentCarListBinding binding;
    private CarViewModel carViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    private Integer parseInteger(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) {
            return null; // Boş alandan null döndür
        } else {
            try {
                return Integer.parseInt(text); // Sayıya dönüştürme
            } catch (NumberFormatException e) {
                return null; // Hata durumunda null döndür
            }
        }
    }

    // RecyclerView güncelleme işlemi
    private void updateRecyclerView(List<Car> cars) {
        if (cars != null && !cars.isEmpty()) {
            CarListAdapter adapter = new CarListAdapter(cars);
            binding.recyclerViewCars.setAdapter(adapter);  // Veriyi RecyclerView'a bağla
        } else {
            Toast.makeText(getContext(), "İlan bulunamadı", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        binding.btnSort.setOnClickListener(v -> {
            // PopupMenu'yi oluşturuyoruz
            PopupMenu popupMenu = new PopupMenu(getContext(), binding.btnSort);
            Menu menu = popupMenu.getMenu();

            // Menüye sıralama seçeneklerini ekliyoruz
            menu.add(Menu.NONE, 0, 0, "Fiyata Göre Azalan");
            menu.add(Menu.NONE, 1, 1, "Fiyata Göre Artan");
            menu.add(Menu.NONE, 2, 2, "Kilometreye Göre Azalan");
            menu.add(Menu.NONE, 3, 3, "Kilometreye Göre Artan");
            menu.add(Menu.NONE, 4, 4, "Yıla Göre Azalan");
            menu.add(Menu.NONE, 5, 5, "Yıla Göre Artan");

            // Menü öğesine tıklanıldığında yapılacak işlemi belirliyoruz
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:  // Fiyata Göre Azalan
                        carViewModel.getSortedCarsByPrice(false).observe(getViewLifecycleOwner(), cars -> updateRecyclerView(cars));
                        Toast.makeText(getContext(), "Fiyata Göre Azalan", Toast.LENGTH_SHORT).show();
                        return true;
                    case 1:  // Fiyata Göre Artan
                        carViewModel.getSortedCarsByPrice(true).observe(getViewLifecycleOwner(), cars -> updateRecyclerView(cars));
                        Toast.makeText(getContext(), "Fiyata Göre Artan", Toast.LENGTH_SHORT).show();
                        return true;
                    case 2:  // Kilometreye Göre Azalan
                        carViewModel.getSortedCarsByKm(false).observe(getViewLifecycleOwner(), cars -> updateRecyclerView(cars));
                        Toast.makeText(getContext(), "Kilometreye Göre Azalan", Toast.LENGTH_SHORT).show();
                        return true;
                    case 3:  // Kilometreye Göre Artan
                        carViewModel.getSortedCarsByKm(true).observe(getViewLifecycleOwner(), cars -> updateRecyclerView(cars));
                        Toast.makeText(getContext(), "Kilometreye Göre Artan", Toast.LENGTH_SHORT).show();
                        return true;
                    case 4:  // Yıla Göre Azalan
                        carViewModel.getSortedCarsByYear(false).observe(getViewLifecycleOwner(), cars -> updateRecyclerView(cars));
                        Toast.makeText(getContext(), "Yıla Göre Azalan", Toast.LENGTH_SHORT).show();
                        return true;
                    case 5:  // Yıla Göre Artan
                        carViewModel.getSortedCarsByYear(true).observe(getViewLifecycleOwner(), cars -> updateRecyclerView(cars));
                        Toast.makeText(getContext(), "Yıla Göre Artan", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            });

            // Menü gösteriyoruz
            popupMenu.show();
        });


        // BrandViewModel'i enjekte ediyoruz
        brandViewModel = new ViewModelProvider(this).get(BrandViewModel.class);

        // Filtreleme butonuna tıklama işlemi
        binding.btnFilter.setOnClickListener(v -> {
            if (binding.filterLayout.getVisibility() == View.GONE) {
                binding.filterLayout.setVisibility(View.VISIBLE);  // Filtre alanını göster
            } else {
                binding.filterLayout.setVisibility(View.GONE);  // Filtre alanını gizle
            }
        });

        // Araç ekle butonuna tıklama işlemi
        binding.fabAddCar.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_carListFragment_to_addCarFragment);
        });

        binding.btnApplyFilters.setOnClickListener(v -> {
            // Seçilen marka bilgisi
            Brand selectedBrand = (Brand) binding.spinnerBrands.getSelectedItem();
            int selectedBrandId = (selectedBrand != null && !selectedBrand.getName().equals("Hepsi")) ? selectedBrand.getId() : -1; // "Hepsi" seçildiğinde -1 kullan

            // Filtre değerlerini al
            Integer minYear = parseInteger(binding.etMinYear);
            Integer maxYear = parseInteger(binding.etMaxYear);
            Integer minPrice = parseInteger(binding.etMinPrice);
            Integer maxPrice = parseInteger(binding.etMaxPrice);
            Integer minKm = parseInteger(binding.etMinKm);
            Integer maxKm = parseInteger(binding.etMaxKm);

            // Filtreleme işlemi
            carViewModel.getFilteredCars(selectedBrandId, minYear, maxYear, minPrice, maxPrice, minKm, maxKm)
                    .observe(getViewLifecycleOwner(), cars -> {
                        if (cars != null && !cars.isEmpty()) {
                            CarListAdapter adapter = new CarListAdapter(cars);
                            binding.recyclerViewCars.setAdapter(adapter);  // Filtrelenmiş araçları RecyclerView'a bağla
                        } else {
                            Toast.makeText(getContext(), "No cars found with the selected filters", Toast.LENGTH_SHORT).show();
                        }
                    });

            // Filtre alanını gizle
            binding.filterLayout.setVisibility(View.GONE);
        });


        // Marka spinner'ını doldur
        brandViewModel.getAllBrands().observe(getViewLifecycleOwner(), brands -> {
            if (brands != null && !brands.isEmpty()) {
                // "Hepsi" seçeneğini ekliyoruz
                List<Brand> allBrands = new ArrayList<>();
                allBrands.add(new Brand("Hepsi"));  // "Hepsi" seçeneğini başa ekledik
                allBrands.addAll(brands);

                ArrayAdapter<Brand> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allBrands);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerBrands.setAdapter(adapter);
            }
        });


        // CarViewModel için ayarları yapıyoruz
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);

        // RecyclerView için LayoutManager ayarlama
        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));

        // Verileri gözlemle ve RecyclerView adapter'ı bağla
        carViewModel.getAllCars().observe(getViewLifecycleOwner(), cars -> {
            if (cars != null && !cars.isEmpty()) {
                CarListAdapter adapter = new CarListAdapter(cars);
                binding.recyclerViewCars.setAdapter(adapter); // Adapter'ı buraya bağladık
            } else {
                Toast.makeText(getContext(), "İlan bulunamadı", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarViewHolder> {
        private final List<Car> carList;

        CarListAdapter(List<Car> carList) {
            this.carList = carList;
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
            Car car = carList.get(position);
            holder.bind(car);
        }

        @Override
        public int getItemCount() {
            return carList.size();
        }

        class CarViewHolder extends RecyclerView.ViewHolder {
            private final ItemCarBinding itemBinding;




            CarViewHolder(ItemCarBinding binding) {
                super(binding.getRoot());
                this.itemBinding = binding;
            }

            void bind(Car car) {
                // Set car details
                itemBinding.tvModel.setText("Model: " + car.model);
                itemBinding.tvYear.setText("Yıl: " + car.year);
                itemBinding.tvKm.setText("KM: " + car.km);
                itemBinding.tvPrice.setText("Fiyat: " + car.price + " ₺");

                // Fetch brand name using brandId
                carViewModel.getBrandById(car.brandId).observe(getViewLifecycleOwner(), brand -> {
                    if (brand != null) {
                        itemBinding.tvBrand.setText("Marka: " + brand.name);  // Set the brand name
                    }
                });

                // Set car image
                if (car.imageUris != null && !car.imageUris.isEmpty()) {
                    Glide.with(requireContext())
                            .load(Uri.parse(car.imageUris.get(0))) // Show the first image
                            .into(itemBinding.imgCar);
                }

                itemBinding.getRoot().setOnClickListener(v -> {
                    // Pass the car ID to the next fragment
                    Bundle bundle = new Bundle();
                    bundle.putInt("carId", car.getId());  // Pass the car ID

                    // Navigate to CarDetailFragment
                    NavHostFragment.findNavController(CarListFragment.this)
                            .navigate(R.id.action_carListFragment_to_carDetailFragment, bundle);  // Navigate to CarDetailFragment
                });
            }
        }
    }
}
