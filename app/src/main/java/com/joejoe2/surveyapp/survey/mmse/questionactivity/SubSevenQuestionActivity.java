package com.joejoe2.surveyapp.survey.mmse.questionactivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joejoe2.surveyapp.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;

import androidx.annotation.Nullable;

public class SubSevenQuestionActivity extends QuestionActivity {
    //data
    private int correctOption;
    private String[] options;
    private String answer;
    private boolean isInheritedSub;
    private String userOption="";
    private int lastAns;
    private int subTimes;
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
    void getBundle() {
        super.getBundle();
        Bundle bundle = this.getIntent().getExtras();
        isInheritedSub=bundle.getBoolean("isInheritedSub", false);
        if (isInheritedSub) {
            lastAns =bundle.getInt("lastAns");
            subTimes=bundle.getInt("subTimes");
        }else {
            subTimes=1;
        }
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
        if (question.getType().equals("sub_seven")){
            if (isInheritedSub){
                return "那如果再減"+question.getData().optInt("subNum")+"等於多少?";
            }else {
                return "請問"+question.getData().optInt("startNum")+"減"+question.getData().optInt("subNum")+"等於多少?";
            }
        }
        return "";
    }

    private int generateCorrectOptionIndex(){
        return new Random().nextInt(4);
    }

    private String getAnswer(){
        if (question.getType().equals("sub_seven")){
            if (isInheritedSub){
                return lastAns-question.getData().optInt("subNum")+"";
            }else {
                return question.getData().optInt("startNum")-question.getData().optInt("subNum")+"";
            }
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
        if (question.getType().equals("sub_seven")){
            possible[1]=Integer.parseInt(answer)+5+"";
            possible[2]=Integer.parseInt(answer)-5+"";
            possible[3]=Integer.parseInt(answer)-15+"";
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
            question.setUserScore(question.getUserScore()+question.getFullScore()/5);
        }
        JSONObject ans=question.getUserAnswer();
        try {
            ans.put("ans"+subTimes, userOption);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        question.setUserAnswer(ans);
        survey.updateSummary();
    }

    @Override
    void toNextQuestion() {
        Intent nextActivity=new Intent();
        Bundle bundle=new Bundle();
        bundle.putString("survey", survey.toString());
        bundle.putBoolean("voice hint", needVoiceHint);
        bundle.putBoolean("isInheritedSub", true);
        bundle.putInt("lastAns", userOption.equals("")?Integer.parseInt(answer):Integer.parseInt(userOption));
        bundle.putInt("subTimes", ++subTimes);
        if (questionIndex<survey.getQuestionNum()){
            if (subTimes<=5){
                nextActivity.setClass(this, MMSEActivitySelector.getQuestionActivity(survey.getQuestion(questionIndex)));
                bundle.putInt("question index", questionIndex);
            }else {
                nextActivity.setClass(this, MMSEActivitySelector.getQuestionActivity(survey.getQuestion(questionIndex+1)));
                bundle.putInt("question index", questionIndex+1);
            }
        }else {
            nextActivity.setClass(this, MMSEActivitySelector.getResultActivity());
        }
        nextActivity.putExtras(bundle);
        startActivity(nextActivity);
    }
}
