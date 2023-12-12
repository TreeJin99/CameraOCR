package com.android.cameraocr.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.android.cameraocr.model.UserModel;

@Dao
public interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void createUser(UserModel userModel);

    @Query("SELECT * FROM UserModel WHERE nickname = :nickName AND password = :password")
    UserModel isSignIn(String nickName, String password);

    @Query("SELECT * FROM UserModel WHERE nickname = :nickName AND password = :password")
    UserModel getUserInfo(String nickName, String password);

}
