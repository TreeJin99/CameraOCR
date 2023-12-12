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
import com.android.cameraocr.util.AllergyAnalyzing;
import com.android.cameraocr.viewmodel.UserViewModel;
import com.bumptech.glide.Glide;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GalleryFragment extends Fragment {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextRecognizer recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

    private UserViewModel userViewModel;
    private ImageView galleryImageView;
    private Button imagePickButton;
    private Button ocrButton;
    private Uri selectedImageUri;
    private String fullOcrText;

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
        galleryImageView = view.findViewById(R.id.gallery_Imageview);
        imagePickButton = view.findViewById(R.id.imagePickButton);
        ocrButton = view.findViewById(R.id.ocrButton);

        initButton();
    }

    private void initButton() {
        imagePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        ocrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userViewModel.getAllergies() == null){
                    Toast.makeText(requireContext(), "설정에서 알러지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else{
                    Log.d("사진 분석 버튼", userViewModel.getAllergies().toString());

                    AllergyAnalyzing allergyAnalyzing = new AllergyAnalyzing(userViewModel);

                    List<String> resultAllergiesFromPicture  =
                            allergyAnalyzing.analyzingAllergiesFromPicture(fullOcrText);

                    Log.d("사진 분석 버튼 결과", resultAllergiesFromPicture.toString());

                }

                if (selectedImageUri != null) {
                    processSelectedImage(selectedImageUri);
                } else {
                    Toast.makeText(requireContext(), "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(), result -> {
                if (result == null) {
                    Toast.makeText(requireContext(), "No image Selected", Toast.LENGTH_SHORT).show();
                } else {
                    Glide.with(requireContext()).load(result).into(galleryImageView);
                    selectedImageUri = result;
                }
            }
    );

    private void processSelectedImage(Uri selectedImageUri) {
        InputImage image;
        try {
            image = InputImage.fromFilePath(requireContext(), selectedImageUri);
            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        fullOcrText = visionText.getText().replaceAll("\\n", "");
                        Log.d("OCR 결과: ", fullOcrText);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}