package com.example.mobilproje.ui.adapter;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilproje.data.model.Car;
import com.example.mobilproje.databinding.ItemCarBinding;
import com.example.mobilproje.viewmodel.CarViewModel;

import java.util.List;

public class FavoriteCarAdapter extends RecyclerView.Adapter<FavoriteCarAdapter.FavoriteCarViewHolder> {

    private final List<Car> carList; // Gösterilecek favori araç listesi
    private final OnCarClickListener listener; // Araç tıklandığında tetiklenecek dinleyici

    // Tıklama olayını dışarıdan yakalamak için arayüz
    public interface OnCarClickListener {
        void onCarClick(Car car);
    }

    // Adapter constructor: araç listesi ve tıklama dinleyicisi alınır
    public FavoriteCarAdapter(List<Car> carList, OnCarClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteCarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_car.xml dosyasını ViewBinding ile şişiriyoruz
        ItemCarBinding binding = ItemCarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FavoriteCarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCarViewHolder holder, int position) {
        // Her bir satıra ait bind işlemi yapılır
        holder.bind(carList.get(position));
    }

    @Override
    public int getItemCount() {
        return carList.size(); // Toplam öğe sayısı
    }

    // ViewHolder sınıfı: item_car.xml ile eşleşir
    class FavoriteCarViewHolder extends RecyclerView.ViewHolder {
        private final ItemCarBinding binding;

        FavoriteCarViewHolder(ItemCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Her bir araca ait veriler buraya bağlanır
        void bind(Car car) {
            // Marka ismi henüz alınmadı, geçici olarak ID yazılıyor
            binding.tvBrandModel.setText("Marka ID: " + car.brandId + " - " + car.model);
            binding.tvTitle.setText(car.getTitle());
            binding.tvPrice.setText(car.getPrice() + " ₺");
            binding.tvCity.setText(car.getCity());

            // Eğer görsel Base64 formatında varsa ilkini gösteriyoruz
            if (car.getImageBase64List() != null && !car.getImageBase64List().isEmpty()) {
                byte[] decodedBytes = Base64.decode(car.getImageBase64List().get(0), Base64.DEFAULT);
                // Glide ile bitmap olarak imageView'a yüklenir
                Glide.with(binding.getRoot().getContext())
                        .asBitmap()
                        .load(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length))
                        .into(binding.imgCar);
            }

            // Marka adını ID üzerinden çekiyoruz (asenkron işlem)
            CarViewModel carViewModel = new ViewModelProvider((FragmentActivity) binding.getRoot().getContext()).get(CarViewModel.class);
            carViewModel.getBrandById(car.getBrandId()).observe((LifecycleOwner) binding.getRoot().getContext(), brand -> {
                if (brand != null) {
                    // Marka adı geldiyse, güncellenir
                    binding.tvBrandModel.setText(brand.name + " - " + car.model);
                } else {
                    binding.tvBrandModel.setText("Bilinmiyor - " + car.model);
                }
            });

            // Kart tıklandığında dışarıya bildir (detay ekranına gitmek için kullanılır)
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCarClick(car);
                }
            });
        }
    }
}
