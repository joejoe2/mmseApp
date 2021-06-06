package com.joejoe2.surveyapp.survey.mmse.questionactivity;

import android.content.ComponentName;

public class ImplementedActivities {
    static ComponentName getTimeQuestionActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.TimeQuestionActivity");
    }

    static ComponentName getLocationQuestionActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.LocationQuestionActivity");
    }

    static ComponentName getMemoryQuestionActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.MemoryQuestionActivity");
    }

    static ComponentName getSubSevenQuestionActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.SubSevenQuestionActivity");
    }

    static ComponentName getNamingQuestionActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.NamingQuestionActivity");
    }

    static ComponentName getRepeatQuestionActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.RepeatQuestionActivity");
    }

    static ComponentName getActionQuestionActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.ActionQuestionActivity");
    }

    static ComponentName getSentenceQuestionActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.SentenceQuestionActivity");
    }

    static ComponentName getSpaceQuestionActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.SpaceQuestionActivity");
    }

    static ComponentName getResultActivity(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.ResultActivity");
    }

    static ComponentName getNotImplemented(){
        return new ComponentName("com.joejoe2.mmseapp", "com.joejoe2.mmseapp.survey.questionactivity.mmse.ResultActivity");
    }
}
