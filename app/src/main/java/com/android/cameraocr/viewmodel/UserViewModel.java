package com.android.cameraocr.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserViewModel extends AndroidViewModel {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 사용자 입력 필드로
    private MutableLiveData<List<String>> allergies = new MutableLiveData<>(Collections.singletonList(""));

    public List<String> getAllergies(){
        return allergies.getValue();
    }
    public void setAllergies(List<String> newAllergies) {
        allergies.setValue(newAllergies);
    }

    public UserViewModel(@NonNull Application application) {
        super(application);
    }

}
