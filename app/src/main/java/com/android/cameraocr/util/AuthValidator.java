package com.android.cameraocr.util;

public class AuthValidator {

    // 로그인 인증을 수행하는 메서드
    public boolean authenticateSignIn(String nickName, String password) {
        boolean isAuthenticated = false;

        if (isValidEmail(nickName) && isValidPassword(password)) {
            isAuthenticated = true;
        } else {
            isAuthenticated = false;
        }

        return isAuthenticated;
    }


    public boolean authenticateSignUp(String name, String password) {
        boolean isAuthenticated = false;

        if (isValidName(name) && isValidPassword(password)) {
            isAuthenticated = true;
        } else {
            isAuthenticated = false;
        }

        return isAuthenticated;
    }

    private boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }

    // 입력된 이메일이 유효한지 확인하는 메서드
    private boolean isValidEmail(String email) {
        return email != null && !email.isEmpty();
    }

    // 입력된 비밀번호가 유효한지 확인하는 메서드
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6; // 예: 최소 6자리 필요
    }
}