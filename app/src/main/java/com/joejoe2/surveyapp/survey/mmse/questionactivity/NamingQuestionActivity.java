package com.joejoe2.surveyapp.survey.mmse.questionactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joejoe2.surveyapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import androidx.annotation.Nullable;

public class NamingQuestionActivity extends QuestionActivity {
    //data
    private int[] correctOptionsIndex;
    private String[] options;
    private String[] answer;
    private HashSet<String> userOptions;
    //ui
    private TextView questionHintTextView;
    private Button[] optionsButton;
    private TextView leftTimeTextView;
    private ImageView leftImageView, rightImageView;
    private HashMap<String, Integer> imgMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //choose layout
        setContentView(R.layout.activity_four_options_with_img_question);
        //init
        init();
        //start question flow
        startQuestion();
    }

    @Override
    void initQuestion() {
        super.initQuestion();
        correctOptionsIndex=generateCorrectOptionIndex();
        answer=getAnswer();
        options=makeOptions(correctOptionsIndex, answer);
        userOptions=new HashSet<>();
        imgMap=new HashMap<>();
        imgMap.put("耳朵", R.drawable.ear);
        imgMap.put("鼻子", R.drawable.nose);
        imgMap.put("眼睛", R.drawable.eye);
        imgMap.put("星星", R.drawable.star);
        imgMap.put("月亮", R.drawable.moon);
        imgMap.put("太陽", R.drawable.sun);
    }

    private int[] generateCorrectOptionIndex(){
        ArrayList<Integer> list=new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        int[] res=new int[2];
        Random r=new Random();
        for (int i=0;i<res.length;i++){
            res[i]=list.remove(r.nextInt(list.size()));
        }
        return res;
    }

    private String[] getAnswer(){
        String[] res=new String[3];
        try {
            JSONArray array=question.getData().getJSONArray("objects");
            res[0]=array.getString(0);
            res[1]=array.getString(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String[] makeOptions(int[] correctOptionsIndex, String[] answer){
        String[] res=new String[4];
        String[] possible=getPossibleOptions(answer);
        for (int i=0;i<2;i++){
            res[correctOptionsIndex[i]]=answer[i];
        }
        for (int i=2;i<4;i++){
            int left=0;
            while (left<4){
                if (res[left]==null){
                    res[left]=possible[i];
                    break;
                }
                left++;
            }
        }
        return res;
    }

    /**
     * @param answer - the answer will always be placed at index 0,1,2
     * @return
     */
    private String[] getPossibleOptions(String[] answer){
        String[] possible=new String[4];
        possible[0]=answer[0];
        possible[1]=answer[1];
        possible[2]="物品";
        possible[3]="東西";
        return possible;
    }

    @Override
    String getQuestionHint() {
        if (question.getType().equals("naming")){
            return "請選出下圖的兩樣東西叫做什麼?";
        }else {
            return "";
        }
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
        leftImageView=findViewById(R.id.leftImage);
        rightImageView=findViewById(R.id.rightImage);
        try {
            leftImageView.setImageResource(imgMap.get(question.getData().getJSONArray("objects").get(0)));
            rightImageView.setImageResource(imgMap.get(question.getData().getJSONArray("objects").get(1)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    void setListeners() {
        for (int i=0;i<optionsButton.length;i++){
            final int index = i;
            optionsButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSpeaking)return;
                    if (userOptions.add(options[index])){
                        optionsButton[index].setVisibility(View.INVISIBLE);
                        if (userOptions.size()==2){
                            completeQuestion();
                        }
                    }
                }
            });
        }
    }

    @Override
    void calculateResult() {
        int score=0;
        JSONObject ans=new JSONObject();
        JSONArray objects=new JSONArray();
        for (String s : answer) {
            if (userOptions.contains(s)){
                score++;
                objects.put(s);
            }
        }
        try {
            ans.put("objects", objects);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        question.setUserScore(score);
        question.setUserAnswer(ans);
        survey.updateSummary();
    }
}
