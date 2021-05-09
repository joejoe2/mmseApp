package com.joejoe2.mmseapp.survey.questionactivity.mmse;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.TextView;

import com.joejoe2.mmseapp.R;
import com.joejoe2.mmseapp.sound.TTSService;
import com.joejoe2.mmseapp.util.ToastLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

public class RepeatQuestionActivity extends QuestionActivity {

    //data
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private String answer;
    private String userSay="";
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

    @Override
    void startQuestion() {
        //enforce reading
        readQuestion();
    }

    @Override
    void readQuestion() {
        isSpeaking=true;
        readingTimer= new Timer();
        readingTask=new TimerTask() {
            @Override
            public void run() {
                TTSService.speak(questionHint);
                TTSService.speak(answer);
                isSpeaking=false;
                //start to count remain time
                startTimer();
                //enable user to answer the question
                startChineseRecognition();
            }
        };
        readingTimer.schedule(readingTask, 1500);
    }

    private String getAnswer(){
        if (question.getType().equals("repeat")){
            return question.getData().optString("sentence");
        }
        return "";
    }

    @Override
    String getQuestionHint() {
        if (question.getType().equals("repeat")){
            return "我要你跟著我說一句話，請你仔細聽完後重複一遍";
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
        if(userSay.equals(answer)){
            question.setUserScore(question.getFullScore());
        }else {
            question.setUserScore(0);
        }
        JSONObject ans=new JSONObject();
        try {
            ans.put(question.getType(), userSay);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        question.setUserAnswer(ans);
        survey.updateSummary();
    }


    private void startChineseRecognition(){
        //use google ChineseRecognition service
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.TAIWAN.toString());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"請重複我剛才說的那句話");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException e)
        {
            ToastLogger.logOnActivity(this, "本裝置不支援語音辨識");
            completeQuestion();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        //on receive ChineseRecognition result
        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT:
            {
                if(resultCode==RESULT_OK&&null!=data){
                    userSay=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                }
                completeQuestion();
                break;
            }
        }
    }
}
