package com.joejoe2.mmseapp.survey.questionactivity.mmse;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joejoe2.mmseapp.R;
import com.joejoe2.mmseapp.sound.TTSService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

public class MemoryQuestionActivity extends QuestionActivity {
    //data
    private int[] correctOptionsIndex;
    private String[] options;
    private String[] answer;
    private HashSet<String> userOptions;
    //ui
    private TextView questionHintTextView;
    private Button[] optionsButton;
    private TextView leftTimeTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //choose layout
        setContentView(R.layout.activity_six_options_question);
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
    }

    @Override
    void startQuestion() {
        //enforce reading
        readQuestion();
        //enable ui component event for user to answer the question
        setListeners();
        //start to count remain time
        startTimer();
    }

    @Override
    void readQuestion() {
        isSpeaking=true;
        readingTimer= new Timer();
        readingTask=new TimerTask() {
            @Override
            public void run() {
                TTSService.speak(questionHint+"。。。");
                if(question.getType().equals("mark")){
                    TTSService.speak(answer[0]+"。"+answer[1]+"。"+answer[2]);
                }
                runOnUiThread(()->{for (int i=0;i<optionsButton.length;i++)optionsButton[i].setVisibility(View.VISIBLE);});
                isSpeaking=false;
            }
        };
        readingTimer.schedule(readingTask, 1500);
    }

    private int[] generateCorrectOptionIndex(){
        ArrayList<Integer> list=new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        int[] res=new int[3];
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
            res[2]=array.getString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String[] makeOptions(int[] correctOptionsIndex, String[] answer){
        String[] res=new String[6];
        String[] possible=getPossibleOptions(answer);
        for (int i=0;i<3;i++){
            res[correctOptionsIndex[i]]=answer[i];
        }
        for (int i=3;i<6;i++){
            int left=0;
            while (left<6){
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
        String[] possible=new String[6];
        possible[0]=answer[0];
        possible[1]=answer[1];
        possible[2]=answer[2];
        possible[3]="東西";
        possible[4]="南北";
        possible[5]="物品";
        return possible;
    }



    @Override
    String getQuestionHint() {
        if (question.getType().equals("mark")){
            return "現在我要告訴你三樣東西，請你要注意聽，之後請你把這三樣東西選出來";
        }else if (question.getType().equals("remind")){
            return "剛才我有告訴你三樣東西，請你想想看並且把這三樣東西選出來";
        }else {
            return "";
        }
    }

    @Override
    void initUI() {
        questionHintTextView=findViewById(R.id.question_hint);
        questionHintTextView.setText(questionHint);
        optionsButton=new Button[6];
        optionsButton[0]=findViewById(R.id.option1_button);
        optionsButton[1]=findViewById(R.id.option2_button);
        optionsButton[2]=findViewById(R.id.option3_button);
        optionsButton[3]=findViewById(R.id.option4_button);
        optionsButton[4]=findViewById(R.id.option5_button);
        optionsButton[5]=findViewById(R.id.option6_button);
        for (int i=0;i<optionsButton.length;i++){
            optionsButton[i].setText(options[i]);
            optionsButton[i].setVisibility(View.INVISIBLE);
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
                    if (userOptions.add(options[index])){
                        optionsButton[index].setVisibility(View.INVISIBLE);
                        if (userOptions.size()==3){
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
