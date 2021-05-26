package com.joejoe2.mmseapp.survey.questionactivity.mmse;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.joejoe2.mmseapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;

public class ActionQuestionActivity extends QuestionActivity {

    //data
    private String answer;
    private String userOption="fail";
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean isMonitoring;
    private int actionNum;
    private int REQUIRE_ACTION_NUM;

    //ui
    private TextView questionHintTextView;
    private TextView leftTimeTextView;
    private ImageView actionImage;
    private TextView actionProgressTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //choose layout
        setContentView(R.layout.activity_action_with_step_question);
        //init
        init();
        //start question flow
        startQuestion();
    }

    @Override
    void initQuestion() {
        super.initQuestion();
        answer=getAnswer();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (question.getType().equals("one_action")&&question.getData().optString("type").equals("shaking device")){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            REQUIRE_ACTION_NUM=100;
        }else if (question.getType().equals("three_action")&&question.getData().optString("type").equals("walk")){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            REQUIRE_ACTION_NUM=10;
        }
    }

    private String getAnswer(){
        if (question.getType().equals("one_action")&&question.getData().optString("type").equals("shaking device")){
            return "success";
        }else if (question.getType().equals("three_action")&&question.getData().optString("type").equals("walk")){
            return "success";
        }
        return "";
    }

    @Override
    String getQuestionHint() {
        if (question.getType().equals("one_action")&&question.getData().optString("type").equals("shaking device")){
            return "請用力左右搖晃手機";
        }else if (question.getType().equals("three_action")&&question.getData().optString("type").equals("walk")){
            return "請原地跑步";
        }else {
            return "";
        }
    }

    @Override
    void initUI() {
        questionHintTextView=findViewById(R.id.question_hint);
        questionHintTextView.setText(question.getId()+":"+questionHint);
        actionImage=findViewById(R.id.action_image);
        if (question.getType().equals("one_action")&&question.getData().optString("type").equals("shaking device")){
            actionImage.setImageResource(R.drawable.shaking_phone);
        }else if (question.getType().equals("three_action")&&question.getData().optString("type").equals("walk")){
            actionImage.setImageResource(R.drawable.walking);
        }
        actionProgressTextView=findViewById(R.id.action_progress_texView);
        actionProgressTextView.setText(actionNum+"/"+REQUIRE_ACTION_NUM);
        leftTimeTextView = findViewById(R.id.left_time);
        leftTimeTextView.setText(timeLimitInSec+"");
    }



    @Override
    void setListeners() {
        if (question.getType().equals("one_action")&&question.getData().optString("type").equals("shaking device")){
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (!isMonitoring) return;
                    float[] linear_acceleration=new float[3];
                    linear_acceleration[0] = sensorEvent.values[0];//x
                    linear_acceleration[1] = sensorEvent.values[1];//y
                    linear_acceleration[2] = sensorEvent.values[2];//z
                    System.out.println(linear_acceleration[0]+", "+linear_acceleration[1]+", "+linear_acceleration[2]);
                    float accX=Math.abs(linear_acceleration[0]);
                    if (accX>=10&&++actionNum==REQUIRE_ACTION_NUM){
                        isMonitoring=false;
                        sensorManager.unregisterListener(this);
                        userOption="success";
                        completeQuestion();
                    }
                    updateProgressOnUI(actionNum, REQUIRE_ACTION_NUM);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }else if (question.getType().equals("three_action")&&question.getData().optString("type").equals("walk")){
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (!isMonitoring) return;
                    int step = (int)sensorEvent.values[0];
                    System.out.println(step);
                    actionNum+=step;
                    if (actionNum>=REQUIRE_ACTION_NUM){
                        isMonitoring=false;
                        sensorManager.unregisterListener(this);
                        userOption="success";
                        completeQuestion();
                    }
                    updateProgressOnUI(actionNum, REQUIRE_ACTION_NUM);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        isMonitoring=true;
    }

    private void updateProgressOnUI(int current, int total){
        actionProgressTextView.setText(current+"/"+total);
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
            ans.put(question.getData().optString("type"), userOption);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        question.setUserAnswer(ans);
        survey.updateSummary();
    }
}
