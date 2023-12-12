package com.android.cameraocr.ui.fragment.main;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.cameraocr.databinding.FragmentCameraBinding;
import com.android.cameraocr.viewmodel.UserViewModel;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment {
    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1001;
    private FragmentCameraBinding cameraBinding;
    private UserViewModel userViewModel;

    private ImageView previewImageView;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Button captureButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraBinding = FragmentCameraBinding.inflate(inflater, container, false);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        cameraBinding.setViewModel(userViewModel);
        cameraBinding.setLifecycleOwner(getViewLifecycleOwner());
        return cameraBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initButton();
        initCamera();
    }

    private void initCamera() {
        captureButton.setOnClickListener(v -> captureImage());

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_CAMERA_PERMISSION);
        }
    }

    private void initButton() {
        previewView = cameraBinding.previewView;
        captureButton = cameraBinding.captureButton;
        previewImageView = cameraBinding.previewImageView;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up the preview use case
                Preview preview = new Preview.Builder().build();

                // Select back camera as a default
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Attach the preview use case to the viewfinder
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Initialize ImageCapture here
                imageCapture = new ImageCapture.Builder().build();

                // Bind use cases to camera
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                Log.e("CameraFragment", "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }



    private void captureImage() {
        if (imageCapture == null) {
            Log.e("CameraFragment", "ImageCapture is not initialized");
            return;
        }

        ImageCapture.OutputFileOptions outputFileOptions = createOutputFileOptions();

        try {
            imageCapture.takePicture(
                    outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            playShutterSound();
                            Bitmap bitmap = getBitmapFromImageUri(outputFileResults.getSavedUri());
                            setBitmapToImageView(bitmap);
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e("CameraFragment", "Error capturing image", exception);
                            showToast("Error capturing image");
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("CameraFragment", "Error taking picture", e);
            showToast("Error taking picture");
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private ImageCapture.OutputFileOptions createOutputFileOptions() {
        ContentValues contentValues = getCurrentFileName();
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/HealthC");

        return new ImageCapture.OutputFileOptions.Builder(
                requireContext().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();
    }

    private void playShutterSound() {
        MediaActionSound sound = new MediaActionSound();
        sound.play(MediaActionSound.SHUTTER_CLICK);
    }

    private void setBitmapToImageView(Bitmap bitmap) {
        getActivity().runOnUiThread(() -> {
            previewImageView.setImageBitmap(bitmap);
            previewImageView.setVisibility(View.VISIBLE);
        });
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    private ContentValues getCurrentFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String name = sdf.format(new Date());

        ContentValues currentFileContentValues = new ContentValues();
        currentFileContentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        currentFileContentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        return currentFileContentValues;
    }

    private Bitmap getBitmapFromImageUri(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        try (InputStream input = contentResolver.openInputStream(uri)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(input, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}