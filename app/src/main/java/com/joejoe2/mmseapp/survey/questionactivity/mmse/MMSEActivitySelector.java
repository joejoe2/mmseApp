package com.joejoe2.mmseapp.survey.questionactivity.mmse;

import android.content.ComponentName;

import com.joejoe2.mmseapp.survey.data.Question;

public class MMSEActivitySelector {

    public static ComponentName getQuestionActivity(Question question){
        if(question.getCategory().equals("time")){
            return ImplementedActivities.getTimeQuestionActivity();
        }else if(question.getCategory().equals("location")){
            return ImplementedActivities.getLocationQuestionActivity();
        }else if (question.getCategory().equals("memory")){
            return ImplementedActivities.getMemoryQuestionActivity();
        }else if (question.getCategory().equals("attention")&&question.getType().equals("sub_seven")){
            return ImplementedActivities.getSubSevenQuestionActivity();
        }else if (question.getCategory().equals("language")&&question.getType().equals("naming")){
            return ImplementedActivities.getNamingQuestionActivity();
        }else if (question.getCategory().equals("language")&&question.getType().equals("repeat")){
            return ImplementedActivities.getNotImplemented();
        }else if (question.getCategory().equals("language")&&question.getType().equals("make_sentence")){
            return ImplementedActivities.getNotImplemented();
        }else if (question.getCategory().equals("action")){
            return ImplementedActivities.getNotImplemented();
        }else if (question.getCategory().equals("space")){
            return ImplementedActivities.getNotImplemented();
        }else {
            return ImplementedActivities.getNotImplemented();
        }
    }

    public static ComponentName getResultActivity(){
        return ImplementedActivities.getResultActivity();
    }
}
