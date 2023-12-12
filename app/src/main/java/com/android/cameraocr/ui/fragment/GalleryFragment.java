package com.android.cameraocr.ui.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cameraocr.R;
import com.android.cameraocr.databinding.FragmentGalleryBinding;
import com.android.cameraocr.util.AllergyAnalyzing;
import com.android.cameraocr.viewmodel.UserViewModel;
import com.bumptech.glide.Glide;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.IOException;
import java.util.List;


public class GalleryFragment extends Fragment {
    private FragmentGalleryBinding galleryBinding;
    private TextRecognizer recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

    private UserViewModel userViewModel;
    private ImageView galleryImageView;
    private Button imagePickButton;
    private TextView eatableText;
    private TextView allergyListText;
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
        if (galleryBinding == null) {
            galleryBinding = FragmentGalleryBinding.inflate(inflater, container, false);
            galleryBinding.setViewModel(new ViewModelProvider(requireActivity()).get(UserViewModel.class));
            galleryBinding.setFragment(this);
            galleryBinding.setLifecycleOwner(getViewLifecycleOwner());
        }
        return galleryBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        initViews(view);
        initButton();
    }

    private void initViews(View view) {
        eatableText = galleryBinding.eatableText;
        allergyListText = galleryBinding.allergyListText;
        galleryImageView = galleryBinding.galleryImageview;
        imagePickButton = galleryBinding.imagePickButton;
        ocrButton = galleryBinding.ocrButton;
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
                if (selectedImageUri != null) {
                    processSelectedImage(selectedImageUri);
                    if (userViewModel.getAllergies() == null) {
                        Toast.makeText(requireContext(), "설정에서 알러지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("사진 분석 버튼", userViewModel.getAllergies().toString());
                        List<String> resultAllergiesFromPicture = startAllergyAnalyzing();
                        showResultsOnFragment(resultAllergiesFromPicture);
                    }
                } else {
                    Toast.makeText(requireContext(), "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<String> startAllergyAnalyzing() {
        AllergyAnalyzing allergyAnalyzing = new AllergyAnalyzing(userViewModel);
        List<String> resultAllergiesFromPicture = allergyAnalyzing.analyzingAllergiesFromPicture(fullOcrText);
        Log.d("사진 분석 버튼 결과", resultAllergiesFromPicture.toString());
        return resultAllergiesFromPicture;
    }

    private void showResultsOnFragment(List<String> resultAllergiesFromPicture) {
        if (resultAllergiesFromPicture.isEmpty()) {
            Log.d("사진 분석 버튼", "섭취 가능  " + resultAllergiesFromPicture);
            eatableText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            eatableText.setText("섭취가 가능해요!");
            allergyListText.setText("감지된 알러지 성분이 없습니다.");
        } else {
            Log.d("사진 분석 버튼", "섭취 불가  " + resultAllergiesFromPicture);
            eatableText.setText("섭취가 불가해요!");
            eatableText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            StringBuilder allergiesStringBuilder = new StringBuilder();
            for (String allergy : resultAllergiesFromPicture) {
                allergiesStringBuilder.append(allergy).append(", ");
            }

            if (allergiesStringBuilder.length() > 0) {
                allergiesStringBuilder.setLength(allergiesStringBuilder.length() - 2);
            }
            allergiesStringBuilder.append("이 감지 되었어요!");
            allergyListText.setText(allergiesStringBuilder.toString());
        }
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