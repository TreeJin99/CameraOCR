package com.android.cameraocr.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.android.cameraocr.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottomNav);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new CameraFragment())
                    .commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.menu_camera:
                    selectedFragment = new CameraFragment();
                    break;
                case R.id.menu_gallery:
                    selectedFragment = new GalleryFragment();
                    break;
                case R.id.menu_settings:
                    selectedFragment = new SettingFragment();
                    break;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}