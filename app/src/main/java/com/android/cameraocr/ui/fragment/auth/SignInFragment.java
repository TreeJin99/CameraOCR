package com.android.cameraocr.ui.fragment.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.cameraocr.R;
import com.android.cameraocr.dao.AuthDao;
import com.android.cameraocr.database.AppDatabase;
import com.android.cameraocr.databinding.FragmentSignInBinding;
import com.android.cameraocr.ui.activity.MainActivity;
import com.android.cameraocr.viewmodel.AuthViewModel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SignInFragment extends Fragment {
    private FragmentSignInBinding fragmentSignInBinding;
    private AuthViewModel authViewModel;
    private AuthDao authDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentSignInBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        fragmentSignInBinding.setLifecycleOwner(getViewLifecycleOwner());

        if (authDao == null) {
            AppDatabase db = AppDatabase.getInstance(requireActivity().getApplication());
            authDao = db.userInfoDao();
        }

        return fragmentSignInBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SignInButtonEvent();
        SignUpButtonEvent();
    }

    /**
     * NavGraph를 사용하였으나, Java Android 에서는 이상하게 적용이 잘 안되었음.
     * 해결을 시도하려 하였으나, 빠른 과제 수행을 위해 차라리 transaction을 통해 간단히 구현하는 것을 선택
     */
    private void SignInButtonEvent() {
        // 로그인 버튼 클릭 이벤트 처리
        fragmentSignInBinding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = fragmentSignInBinding.nickNameEditTextView.getText().toString();
                String password = fragmentSignInBinding.passwordEditText.getText().toString();

                CountDownLatch latch = new CountDownLatch(1);

                executor.submit(() -> {
                    try {
                        authDao.isSignIn(nickName, password);
                        authViewModel.setIsAuthenticated(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                if (authViewModel.getIsAuthenticated()) {
                    Toast.makeText(requireContext(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void SignUpButtonEvent() {
        fragmentSignInBinding.goToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment signUpFragment = new SignUpFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.auth_fragment_container, signUpFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}