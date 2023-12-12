package com.android.cameraocr.ui.fragment.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.cameraocr.dao.AuthDao;
import com.android.cameraocr.database.AppDatabase;
import com.android.cameraocr.databinding.FragmentSignUpBinding;
import com.android.cameraocr.model.UserModel;
import com.android.cameraocr.ui.activity.MainActivity;
import com.android.cameraocr.util.AuthValidator;
import com.android.cameraocr.viewmodel.AuthViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding fragmentSignUpBinding;
    private AuthViewModel authViewModel;
    private AuthValidator authValidator = new AuthValidator();
    private AuthDao authDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentSignUpBinding = FragmentSignUpBinding.inflate(inflater, container, false);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        fragmentSignUpBinding.setViewModel(authViewModel);
        fragmentSignUpBinding.setLifecycleOwner(getViewLifecycleOwner());

        if(authDao == null){
            AppDatabase db = AppDatabase.getInstance(requireActivity().getApplication());
            authDao = db.userInfoDao();
        }

        return fragmentSignUpBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initButton();
    }

    private void initButton() {
        fragmentSignUpBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        fragmentSignUpBinding.signUpDoneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String nickName = fragmentSignUpBinding.signUpNickNameEditText.getText().toString();
                String password = fragmentSignUpBinding.signUpPasswordEditView.getText().toString();

                if(authValidator.authenticateSignUp(nickName, password)){
                    authViewModel.setNickName(nickName);
                    authViewModel.setPassword(password);

                    executor.submit(()->{
                        try{
                            UserModel newUser = new UserModel(nickName, password);
                            authDao.createUser(newUser);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    });

                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }else{
                    Toast.makeText(requireContext(), "값이 비어있거나, 비밀번호가 6자 미만 입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}