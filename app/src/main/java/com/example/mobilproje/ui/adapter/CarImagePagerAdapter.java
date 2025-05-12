package com.example.mobilproje.ui.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilproje.R;

import java.util.List;

// Bu adapter, ViewPager2 içerisinde araç görsellerini göstermek için kullanılır
public class CarImagePagerAdapter extends RecyclerView.Adapter<CarImagePagerAdapter.ImageViewHolder> {

    // Görsellerin bitmap listesi
    private final List<Bitmap> bitmapList;

    // Constructor: Bitmap listesini alır
    public CarImagePagerAdapter(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Her bir sayfa için item_pager_car_image.xml layout'unu şişirir
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pager_car_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Belirtilen sıradaki bitmap görselini ImageView'a atar
        holder.imageView.setImageBitmap(bitmapList.get(position));
    }

    @Override
    public int getItemCount() {
        // Görsel sayısı kadar sayfa oluşturulacak
        return bitmapList.size();
    }

    // ViewHolder: Her bir ViewPager sayfası için görseli tutar
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(View itemView) {
            super(itemView);
            // item_pager_car_image.xml içindeki ImageView'a erişim
            imageView = itemView.findViewById(R.id.imgPagerCar);
        }
    }
}
