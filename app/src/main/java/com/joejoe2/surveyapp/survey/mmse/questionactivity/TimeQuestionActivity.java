package com.joejoe2.surveyapp.survey.mmse.questionactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joejoe2.surveyapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class TimeQuestionActivity extends QuestionActivity {
    //data
    private int correctOption;
    private String[] options;
    private String answer;
    LocalDate today=LocalDate.now();
    private String userOption="";
    //ui
    private TextView questionHintTextView;
    private Button[] optionsButton;
    private TextView leftTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        if (question.getType().equals("year")){
            return "請選出現在是哪一年";
        }else if (question.getType().equals("month")){
            return "請選出現在是幾月";
        }else if (question.getType().equals("day")){
            return "請選出現在是幾號";
        }else if (question.getType().equals("week")){
            return "請選出現在是星期幾";
        }else if (question.getType().equals("season")){
            return "請選出現在是甚麼季節";
        }else {
            return "";
        }
    }

    private int generateCorrectOptionIndex(){
        return new Random().nextInt(4);
    }

    private String getAnswer(){
        if (question.getType().equals("year")){
            return today.getYear()+"";
        }else if (question.getType().equals("month")){
            return today.getMonthValue()+"";
        }else if (question.getType().equals("day")){
            return today.getDayOfMonth()+"";
        }else if (question.getType().equals("week")){
            return today.getDayOfWeek().getValue()+"";
        }else if (question.getType().equals("season")){
            switch (today.getMonthValue()/4){
                case 1:return "春";
                case 2:return "夏";
                case 3:return "秋";
                case 4:return "冬";
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
        if (question.getType().equals("year")){
            //possible[0]=today.getYear()+"";
            possible[1]=today.getYear()-5+"";
            possible[2]=today.getYear()+5+"";
            possible[3]=today.getYear()-10+"";
        }else if (question.getType().equals("month")){
            //possible[0]=today.getMonthValue()+"";
            possible[1]=(today.getMonthValue()+2)%12+1+"";
            possible[2]=(today.getMonthValue()+5)%12+1+"";
            possible[3]=(today.getMonthValue()+10)%12+1+"";
        }else if (question.getType().equals("day")){
            //possible[0]=today.getDayOfMonth()+"";
            possible[1]=(today.plusDays(2).getDayOfMonth())+"";
            possible[2]=(today.plusDays(5).getDayOfMonth())+"";
            possible[3]=(today.plusDays(10).getDayOfMonth())+"";
        }else if (question.getType().equals("week")){
            //possible[0]=today.getDayOfWeek()+"";
            possible[1]=(today.getDayOfWeek().plus(2).getValue())+"";
            possible[2]=(today.getDayOfWeek().plus(5).getValue())+"";
            possible[3]=(today.getDayOfWeek().plus(10).getValue())+"";
        }else if (question.getType().equals("season")){
            ArrayList<String> seasons=new ArrayList<>();
            seasons.add("春");
            seasons.add("夏");
            seasons.add("秋");
            seasons.add("冬");
            seasons.remove(answer);
            possible[1]=(seasons.get(0))+"";
            possible[2]=(seasons.get(1))+"";
            possible[3]=(seasons.get(2))+"";
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
    void setListeners(){
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
