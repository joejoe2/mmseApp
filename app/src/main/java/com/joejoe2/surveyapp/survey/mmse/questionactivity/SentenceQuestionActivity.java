package com.joejoe2.surveyapp.survey.mmse.questionactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joejoe2.surveyapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import androidx.annotation.Nullable;

public class SentenceQuestionActivity extends QuestionActivity {

    //data
    private int correctOption;
    private String[] options;
    private String answer;
    private String userOption="";
    //ui
    private TextView questionHintTextView;
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
        correctOption=generateCorrectOptionIndex();
        answer=getAnswer();
        options=makeOptions(correctOption, answer);
    }

    @Override
    String getQuestionHint() {
        if (question.getType().equals("make_sentence")){
            return "請選出最通順的句子";
        }else {
            return "";
        }
    }

    private int generateCorrectOptionIndex(){
        return new Random().nextInt(4);
    }

    private String getAnswer(){
        if (question.getType().equals("make_sentence")){
            String ans="";
            for (int i=0;i<question.getData().optJSONArray("sentence").length();i++){
                ans+=question.getData().optJSONArray("sentence").optString(i);
            }
            return ans;
        }
        return "";
    }

    private String[] makeOptions(int correctOptionIndex, String answer){
        String[] res=getPossibleOptions(answer);
        res[0]=res[correctOptionIndex];
        res[correctOptionIndex]=answer;
        return res;
    }

    /**
     * @param answer - the answer will always be placed at index 0
     * @return
     */
    private String[] getPossibleOptions(String answer){
        String[] possible=new String[4];
        possible[0]=answer;
        if (question.getType().equals("make_sentence")){
            possible[1]=question.getData().optJSONArray("sentence").optString(0)+question.getData().optJSONArray("sentence").optString(1);
            possible[2]=question.getData().optJSONArray("sentence").optString(2)+question.getData().optJSONArray("sentence").optString(3);
            possible[3]=question.getData().optJSONArray("sentence").optString(3)+question.getData().optJSONArray("sentence").optString(2)+question.getData().optJSONArray("sentence").optString(1)+question.getData().optJSONArray("sentence").optString(0);;;
        }
        return possible;
    }

    @Override
    void initUI() {
        questionHintTextView=findViewById(R.id.question_hint);
        questionHintTextView.setText(questionHint);
        optionsButton=new Button[4];
        optionsButton[0]=findViewById(R.id.option1_button);
        optionsButton[1]=findViewById(R.id.option2_button);
        optionsButton[2]=findViewById(R.id.option3_button);
        optionsButton[3]=findViewById(R.id.option4_button);
        for (int i=0;i<optionsButton.length;i++){
            optionsButton[i].setText(options[i]);
        }
        leftTimeTextView = findViewById(R.id.left_time);
        leftTimeTextView.setText(timeLimitInSec+"");
    }

    @Override
    void setListeners() {
        for (int i=0;i<optionsButton.length;i++){
            final int index = i;
            optionsButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userOption=options[index];
                    completeQuestion();
                }
            });
        }
    }

    @Override
    void calculateResult() {
        if(userOption.equals(answer)){
            question.setUserScore(question.getFullScore());
        }else {
            question.setUserScore(0);
        }
        JSONObject ans=new JSONObject();
        try {
            ans.put(question.getType(), userOption);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        question.setUserAnswer(ans);
        survey.updateSummary();
    }
}
