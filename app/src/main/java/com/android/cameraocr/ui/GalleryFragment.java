package com.android.cameraocr.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.cameraocr.R;
import com.android.cameraocr.viewmodel.UserViewModel;
import com.bumptech.glide.Glide;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GalleryFragment extends Fragment {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextRecognizer recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

    private UserViewModel userViewModel;
    private ImageView galleryImageView;
    private Button imagePickButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // 백그라운드 스레드에서 로그 출력
        executor.submit(() -> {

        });

        galleryImageView = view.findViewById(R.id.gallery_Imageview);
        imagePickButton = view.findViewById(R.id.imagePickButton);

        imagePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
    }

    private ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(), result -> {
                if (result == null) {
                    Toast.makeText(requireContext(), "No image Selected", Toast.LENGTH_SHORT).show();
                } else {
                    Glide.with(requireContext()).load(result).into(galleryImageView);
                    processSelectedImage(result);
                }
            }
    );

    private void processSelectedImage(Uri selectedImageUri) {
        InputImage image;
        try {
            image = InputImage.fromFilePath(requireContext(), selectedImageUri);
            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        String fullText = visionText.getText();
                        Log.d("OCR 결과: ", fullText);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}