package com.joejoe2.mmseapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.joejoe2.mmseapp.R;
import com.joejoe2.mmseapp.survey.data.Survey;
import com.joejoe2.mmseapp.survey.questionactivity.mmse.MMSEActivitySelector;
import com.joejoe2.mmseapp.survey.service.SurveyService;
import com.joejoe2.mmseapp.util.OnCompleteCallable;
import com.joejoe2.mmseapp.util.ToastLogger;

import org.json.JSONException;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    //data and flag
    private boolean needVoiceHint;
    //ui
    Button startMMSESurveyButton;
    CheckBox voiceHintCheckBox;
    private ProgressDialog loadingProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        checkPermission();
        setListener();
    }

    private void initUI() {
        startMMSESurveyButton=findViewById(R.id.start_mmse_survey_button);
        voiceHintCheckBox=findViewById(R.id.voice_hint_checkBox);
    }

    private void checkPermission() {
        ArrayList<String> permissions=new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        for (String permission : permissions) {
            if(ActivityCompat.checkSelfPermission(MainActivity.this, permission)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission},1);
            }
        }
    }

    private void setListener(){
        voiceHintCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                needVoiceHint=b;
                if (needVoiceHint){
                    openSpeaker();
                }
            }
        });

        startMMSESurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingProgressDialog = new ProgressDialog(MainActivity.this);
                loadingProgressDialog.setMessage("loading...");
                loadingProgressDialog.show();
                SurveyService.getSurvey("mmse", "test", new OnCompleteCallable() {
                    @Override
                    public void doOnComplete(String msg, boolean success) {
                        if(success) {
                            try {
                                Survey survey = new Survey(msg);
                                ComponentName nextActivity= MMSEActivitySelector.getQuestionActivity(survey.getQuestion(0));
                                Intent intent=new Intent();
                                Bundle bundle=new Bundle();
                                bundle.putString("survey", survey.toString());
                                bundle.putInt("question index", 0);
                                bundle.putBoolean("voice hint", needVoiceHint);
                                intent.putExtras(bundle);
                                intent.setComponent(nextActivity);
                                loadingProgressDialog.dismiss();
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void openSpeaker(){
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }

    private boolean hasNetWork(){
        ConnectivityManager mConnectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetWorkInfo =mConnectivityManager.getActiveNetworkInfo();
        if(mNetWorkInfo!=null&&mNetWorkInfo.isConnected())return true;
        else
        {
            ToastLogger.logOnActivity(MainActivity.this, "無網路連線");
            return false;
        }
    }
}
