package com.example.mobilproje.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilproje.databinding.ItemCarImageBinding;

import java.util.List;

public class CarImageAdapter extends RecyclerView.Adapter<CarImageAdapter.ImageViewHolder> {

    private final List<Bitmap> images;
    private final Context context;

    public CarImageAdapter(Context context, List<Bitmap> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarImageBinding binding = ItemCarImageBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.binding.imgCarImage.setImageBitmap(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ItemCarImageBinding binding;

        public ImageViewHolder(@NonNull ItemCarImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
