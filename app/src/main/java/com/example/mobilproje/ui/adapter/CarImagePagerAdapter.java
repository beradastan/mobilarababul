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

public class CarImagePagerAdapter extends RecyclerView.Adapter<CarImagePagerAdapter.ImageViewHolder> {
    private final List<Bitmap> bitmapList;

    public CarImagePagerAdapter(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pager_car_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.imageView.setImageBitmap(bitmapList.get(position));
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgPagerCar);
        }
    }
}
