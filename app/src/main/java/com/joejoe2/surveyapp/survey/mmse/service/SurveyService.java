package com.joejoe2.surveyapp.survey.mmse.service;

import com.joejoe2.surveyapp.util.OnCompleteCallable;

public class SurveyService {
    public static void getSurvey(String surveyType, String userId, OnCompleteCallable onCompleteCallable){
        new SurveyProvider(surveyType, userId, onCompleteCallable).execute();
    }
}
