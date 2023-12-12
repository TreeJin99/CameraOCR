package com.android.cameraocr.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.cameraocr.databinding.FragmentSettingBinding;
import com.android.cameraocr.viewmodel.UserViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding settingBinding;
    private UserViewModel userViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingBinding = FragmentSettingBinding.inflate(inflater, container, false);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        settingBinding.setViewModel(userViewModel);
        settingBinding.setLifecycleOwner(getViewLifecycleOwner());
        return settingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initButton();
    }

    private void initButton() {
        settingBinding.allergyChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            List<String> allergies = new ArrayList<>();
            for (int i = 0; i < group.getChildCount(); i++) {
                Chip chip = (Chip) group.getChildAt(i);
                if (chip.isChecked()) {
                    allergies.add(chip.getText().toString());
                }
            }
            userViewModel.setAllergies(allergies);
        });

    }
}