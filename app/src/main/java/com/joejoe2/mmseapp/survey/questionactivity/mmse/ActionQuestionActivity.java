package com.joejoe2.mmseapp.survey.questionactivity.mmse;

import android.os.Bundle;
import android.widget.TextView;

import com.joejoe2.mmseapp.R;

import androidx.annotation.Nullable;

public class ActionQuestionActivity extends QuestionActivity {

    //data
    private String answer;
    private String userInput="";
    //ui
    private TextView questionHintTextView;
    private TextView leftTimeTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //choose layout
        setContentView(R.layout.activity_blank_question);
        //init
        init();
        //start question flow
        startQuestion();
    }

    @Override
    void initQuestion() {
        super.initQuestion();
        answer=getAnswer();
    }

    private String getAnswer(){
        if (question.getType().equals("one_action")&&question.getData().optString("type").equals("shaking device")){
            return "50";
        }
        return "";
    }

    @Override
    String getQuestionHint() {
        if (question.getType().equals("one_action")&&question.getData().optString("type").equals("shaking device")){
            return "請你左右搖晃手機";
        }else {
            return "";
        }
    }

    @Override
    void initUI() {
        questionHintTextView=findViewById(R.id.question_hint);
        questionHintTextView.setText(question.getId()+":"+questionHint);
        leftTimeTextView = findViewById(R.id.left_time);
        leftTimeTextView.setText(timeLimitInSec+"");
    }

    @Override
    void setListeners() {

    }

    @Override
    void calculateResult() {

    }
}
