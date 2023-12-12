package com.android.cameraocr.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class AuthViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>();
    private MutableLiveData<String> nickName = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();


    public void setNickName(String nickName){
        this.nickName.setValue(nickName);
    }

    public void setPassword(String password){
        this.password.setValue(password);
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        this.isAuthenticated.postValue(isAuthenticated);
    }

    public String getNickName(){
        return nickName.getValue();
    }

    public String getPassword(){
        return password.getValue();
    }

    public Boolean getIsAuthenticated() {
        return isAuthenticated.getValue();
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        isAuthenticated.setValue(false);
    }
}
