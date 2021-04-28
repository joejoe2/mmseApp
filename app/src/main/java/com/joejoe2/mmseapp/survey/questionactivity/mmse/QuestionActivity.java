package com.joejoe2.mmseapp.survey.questionactivity.mmse;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.joejoe2.mmseapp.activity.MainActivity;
import com.joejoe2.mmseapp.sound.TTSService;
import com.joejoe2.mmseapp.survey.data.Question;
import com.joejoe2.mmseapp.survey.data.Survey;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public abstract class QuestionActivity extends AppCompatActivity {
    Survey survey;
    Question question;
    int questionIndex;
    int timeLimitInSec=60;
    Timer readingTimer, countDownTimer;
    TimerTask readingTask, countDownTask;
    boolean needVoiceHint;
    boolean isSpeaking;
    String questionHint="";

    /**
     * run all setup task(call getBundle(), initQuestion(), initUI() in order)
     */
    void init(){
        getBundle();
        initQuestion();
        initUI();
    }

    /**
     * receive params(survey, question, needVoiceHint) from bundle
     */
    void getBundle(){
        try {
            Bundle bundle = this.getIntent().getExtras();
            survey=new Survey(bundle.getString("survey", "{}"));
            questionIndex=bundle.getInt("question index", 0);
            question=survey.getQuestion(questionIndex);
            needVoiceHint=bundle.getBoolean("voice hint", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * setup data related to the question
     */
    void initQuestion(){
        questionHint=getQuestionHint();
    };

    /**
     * generate Question Hint text
     * @return
     */
    abstract String getQuestionHint();

    /**
     * setup ui for this question activity
     */
    abstract void initUI();

    abstract void setListeners();

    /**
     * start question flow
     */
    void startQuestion(){
        if (needVoiceHint) {
            readQuestion();
        }
        //enable ui component event for user to answer the question
        setListeners();
        //start to count remain time
        startTimer();
    };

    /**
     * read the question via speaker
     */
    void readQuestion(){
        isSpeaking=true;
        readingTimer= new Timer();
        readingTask=new TimerTask() {
            @Override
            public void run() {
                TTSService.speak(questionHint);
                isSpeaking=false;
            }
        };
        readingTimer.schedule(readingTask, 1500);
    };

    /**
     * start to count down timeLimitInSec
     */
    void startTimer(){
        countDownTimer=new Timer();
        countDownTask=new TimerTask() {
            @Override
            public void run() {
                onTimeOut();
            }
        };
        countDownTimer.schedule(countDownTask, timeLimitInSec*1000);
    }

    /**
     * use to define event when timeLimitInSec become zero
     */
    void onTimeOut(){
        completeQuestion();
    };

    /**
     *invoke this when question has been completed,this make question's activity to perform calculateResult, cleanQuestion, and toNextQuestion in order
     */
    void completeQuestion(){
        if (isSpeaking)return;
        calculateResult();
        cleanQuestion();
        toNextQuestion();
    };

    /**
     *check the user answer, calculate total scores, and put result back to question
     */
    abstract void calculateResult();

    /**
     *clean task before jump to next activity
     */
    void cleanQuestion(){
        if (readingTimer!=null){
            readingTimer.cancel();
            readingTask.cancel();
            readingTimer=null;
            readingTask=null;
        }
        if(countDownTimer!=null){
            countDownTimer.cancel();
            countDownTask.cancel();
            countDownTimer=null;
            countDownTask=null;
        }
    };

    /**
     *go to next question's activity, or result's activity when this is last question
     */
    void toNextQuestion(){
        ComponentName nextActivity;
        if (questionIndex<survey.getQuestionNum()){
            nextActivity= MMSEActivitySelector.getQuestionActivity(survey.getQuestion(questionIndex+1));
        }else {
            nextActivity=MMSEActivitySelector.getResultActivity();
        }
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putString("survey", survey.toString());
        bundle.putInt("question index", questionIndex+1);
        bundle.putBoolean("voice hint", needVoiceHint);
        intent.putExtras(bundle);
        intent.setComponent(nextActivity);
        startActivity(intent);
    };

    /**
     *set back button pressed event
     */
    @Override
    public void onBackPressed() {
        tryCancelSurVey();
    }

    /**
     *prompt up a dialog to ask user whether to cancel the current survey, if yes will go back to MainActivity
     */
    void tryCancelSurVey(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("取消作答?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                cancelSurvey();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    };

    void cancelSurvey(){
        cleanQuestion();
        goBackToMainActivity();
    }

    /**
     * go back to MainActivity
     */
    void goBackToMainActivity(){
        //back to MainActivity and clear history
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }
}
