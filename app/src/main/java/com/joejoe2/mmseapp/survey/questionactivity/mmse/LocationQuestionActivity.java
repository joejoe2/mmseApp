package com.joejoe2.mmseapp.survey.questionactivity.mmse;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.joejoe2.mmseapp.R;

import java.time.LocalDate;

import androidx.annotation.Nullable;

public class LocationQuestionActivity extends QuestionActivity {
    //data
    private int correctOption;
    private String[] options;
    private String answer;

    private String userOption="";
    //ui
    private TextView questionHintTextView;
    private Button completeButton;
    private Button[] optionsButton;
    private TextView leftTimeTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //choose layout
        setContentView(R.layout.activity_four_options_question);
        //init
        init();
        //start question flow
        startQuestion();
    }

    @Override
    void initQuestion() {
        super.initQuestion();
    }

    @Override
    String getQuestionHint() {
        return null;
    }

    @Override
    void initUI() {

    }

    @Override
    void setListeners() {

    }

    @Override
    void calculateResult() {

    }
}
