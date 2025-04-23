package com.example.mobilproje.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.example.mobilproje.data.database.AppDatabase;
import com.example.mobilproje.data.model.CarImage;
import com.example.mobilproje.databinding.FragmentCarDetailBinding;
import com.example.mobilproje.ui.adapter.CarImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class CarDetailFragment extends Fragment {
    private FragmentCarDetailBinding binding;
    private AppDatabase db;
    private int carId = 1; // geçici olarak örnek carId

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "car_database")
                .allowMainThreadQueries()
                .build();

        List<CarImage> imageList = db.carImageDao().getImagesForCar(carId);
        List<Bitmap> bitmapList = new ArrayList<>();

        for (CarImage image : imageList) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image.imageData, 0, image.imageData.length);
            if (bitmap != null) {
                bitmapList.add(bitmap);
            }
        }

        CarImageAdapter adapter = new CarImageAdapter(requireContext(), bitmapList);
        binding.rvCarImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCarImages.setAdapter(adapter);
    }
}
