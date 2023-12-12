package com.android.cameraocr.util;

import com.android.cameraocr.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class AllergyAnalyzing {
    private UserViewModel userViewModel;

    // 생성자에서 UserViewModel을 받도록 수정
    public AllergyAnalyzing(UserViewModel userViewModel) {
        this.userViewModel = userViewModel;
    }

    public List<String> analyzingAllergiesFromPicture(String fullOcrText){
        List<String> result = new ArrayList<>();

        // fullOcrText가 null이면 빈 리스트 반환
        if (fullOcrText == null) {
            return result;
        }

        List<String> userAllergies = userViewModel.getAllergies();

        for (String allergy : userAllergies) {
            // allergy가 null이 아니고, fullOcrText에 포함되어 있다면 결과에 추가
            if (allergy != null && fullOcrText.contains(allergy)) {
                result.add(allergy);
            }
        }

        return result;
    }
}
