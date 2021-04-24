package com.joejoe2.mmseapp.survey.service;

import com.joejoe2.mmseapp.util.OnCompleteCallable;

public class SurveyService {
    public static void getSurvey(String surveyType, String userId, OnCompleteCallable onCompleteCallable){
        new SurveyProvider(surveyType, userId, onCompleteCallable).execute();
    }
}
