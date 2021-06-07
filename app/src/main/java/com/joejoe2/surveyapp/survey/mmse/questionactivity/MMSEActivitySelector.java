package com.joejoe2.surveyapp.survey.mmse.questionactivity;

import com.joejoe2.surveyapp.survey.mmse.data.Question;

public class MMSEActivitySelector {

    public static Class getQuestionActivity(Question question){
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
            return ImplementedActivities.getRepeatQuestionActivity();
        }else if (question.getCategory().equals("language")&&question.getType().equals("make_sentence")){
            return ImplementedActivities.getSentenceQuestionActivity();
        }else if (question.getCategory().equals("action")){
            return ImplementedActivities.getActionQuestionActivity();
        }else if (question.getCategory().equals("space")){
            return ImplementedActivities.getSpaceQuestionActivity();
        }else {
            return ImplementedActivities.getNotImplemented();
        }
    }

    public static Class getResultActivity(){
        return ImplementedActivities.getResultActivity();
    }
}
