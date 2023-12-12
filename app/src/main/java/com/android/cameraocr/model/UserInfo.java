package com.android.cameraocr.model;


import java.util.List;

public class UserInfo {
    private String email;

    private String password;

    private String name;

    private List<String> allergyList;

    private long createdAt;

    public UserInfo(UserInfo userInfo){

        this.email = userInfo.email;
        this.password = userInfo.password;
        this.name = userInfo.name;
        this.allergyList = userInfo.allergyList;
    }

    public UserInfo(String name, String email, String password, List<String> allergyList) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.allergyList = allergyList;
        this.createdAt = System.currentTimeMillis();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getAllergyList() {
        return allergyList;
    }

    public void setAllergyList(List<String> allergyList) {
        this.allergyList = allergyList;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", allergyList=" + allergyList +
                ", createdAt=" + createdAt +
                '}';
    }

}