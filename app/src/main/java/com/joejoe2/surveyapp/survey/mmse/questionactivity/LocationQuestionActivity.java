package com.joejoe2.surveyapp.survey.mmse.questionactivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joejoe2.surveyapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class LocationQuestionActivity extends QuestionActivity {
    //data
    private int correctOption;
    private String[] options;
    private String answer;
    private List<Address> addresses;
    private String userOption="";
    //ui
    private TextView questionHintTextView;
    private Button[] optionsButton;
    private TextView leftTimeTextView;
    private ProgressDialog loadingProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //choose layout
        setContentView(R.layout.activity_four_options_question);
        //init and start question flow
        loadingProgressDialog = new ProgressDialog(this);
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.setMessage("loading...");
        loadingProgressDialog.show();
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        Geocoder geocoder;
                        geocoder = new Geocoder(LocationQuestionActivity.this, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        loadingProgressDialog.dismiss();
                        //init
                        init();
                        //start question flow
                        startQuestion();
                    }
                });
    }

    private void turnGPSOn() {
        LocationManager locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // show open gps message
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("warning");
            builder.setMessage("?????????????????????");
            builder.setCancelable(false);
            builder.setPositiveButton("???", new
                    android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // jump to setting
                            Intent enableGPSIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(enableGPSIntent);
                        }
                    });
            AlertDialog dialog=builder.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check gps
        turnGPSOn();
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
        if (question.getType().equals("city")){
            return "???????????????????????????????????????";
        }else if (question.getType().equals("district")){
            return "??????????????????????????????????????????";
        }else if (question.getType().equals("road")){
            return "???????????????????????????????????????";
        }else {
            return "";
        }
    }

    private int generateCorrectOptionIndex(){
        return new Random().nextInt(4);
    }

    private String getAnswer(){
        String res=null;
        if (question.getType().equals("city")){
            res=addresses.get(0).getAdminArea();
        }else if (question.getType().equals("district")){
            res=addresses.get(0).getLocality();
        }else if (question.getType().equals("road")){
            res=addresses.get(0).getThoroughfare();
        }

        return res!=null?res:"unavailable";
    }

    private String[] makeOptions(int correctOption, String answer){
        String[] res=getPossibleOptions(answer);
        res[0]=res[correctOption];
        res[correctOption]=answer;
        return res;
    }

    /**
     * @param answer - the answer will always be placed at index 0
     * @return
     */
    private String[] getPossibleOptions(String answer){
        String[] possible=new String[4];
        possible[0]=answer;
        if (question.getType().equals("city")){
            //possible[0]=today.getYear()+"";
            possible[1]="?????????";
            possible[2]="?????????";
            possible[3]="??????";
        }else if (question.getType().equals("district")){
            //possible[0]=today.getMonthValue()+"";
            possible[1]="?????????";
            possible[2]="?????????";
            possible[3]="?????????";
        }else if (question.getType().equals("road")){
            //possible[0]=today.getDayOfMonth()+"";
            possible[1]="??????";
            possible[2]="?????????";
            possible[3]="???????????????";
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
