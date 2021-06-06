package com.joejoe2.surveyapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.joejoe2.surveyapp.R;
import com.joejoe2.surveyapp.survey.mmse.data.Survey;
import com.joejoe2.surveyapp.survey.mmse.questionactivity.MMSEActivitySelector;
import com.joejoe2.surveyapp.survey.mmse.service.SurveyService;
import com.joejoe2.surveyapp.util.OnCompleteCallable;
import com.joejoe2.surveyapp.util.ToastLogger;

import org.json.JSONException;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    //data and flag
    private boolean needVoiceHint;
    private static final int START_FROM_QUESTION=0;
    //ui
    Button startMMSESurveyButton;
    CheckBox voiceHintCheckBox;
    private ProgressDialog loadingProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        requestPermissions(checkPermissions());
        setListener();
    }

    private void initUI() {
        startMMSESurveyButton=findViewById(R.id.start_mmse_survey_button);
        voiceHintCheckBox=findViewById(R.id.voice_hint_checkBox);
    }

    private ArrayList<String> checkPermissions() {
        String[] requiredPermissions=new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        ArrayList<String> lackingPermissions=new ArrayList<>();
        for (String permission : requiredPermissions) {
            if(ActivityCompat.checkSelfPermission(MainActivity.this, permission)!=PackageManager.PERMISSION_GRANTED){
                lackingPermissions.add(permission);
            }
        }
        return lackingPermissions;
    }

    private void requestPermissions(ArrayList<String> permissions){
        if (permissions!=null&&permissions.size()>0){
            ActivityCompat.requestPermissions(MainActivity.this, permissions.toArray(new String[]{}),1);
        }
    }

    private void requestPermissionsInSetting(ArrayList<String> permissions){
        if (permissions!=null&&permissions.size()>0){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("請在設定中許可權限");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    openSettingPage();
                }
            });
            alertDialogBuilder.create().show();
        }
    }

    private void openSettingPage(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
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
                ArrayList<String> lackingPermissions=checkPermissions();
                if (lackingPermissions.size()>0){
                    requestPermissionsInSetting(lackingPermissions);
                    return;
                }

                loadingProgressDialog = new ProgressDialog(MainActivity.this);
                loadingProgressDialog.setCancelable(false);
                loadingProgressDialog.setMessage("loading...(may take up to 60 sec)");
                loadingProgressDialog.show();
                SurveyService.getSurvey("mmse", "test", new OnCompleteCallable() {
                    @Override
                    public void doOnComplete(String msg, boolean success) {
                        loadingProgressDialog.dismiss();
                        if(success) {
                            try {
                                Survey survey = new Survey(msg);
                                ComponentName nextActivity= MMSEActivitySelector.getQuestionActivity(survey.getQuestion(START_FROM_QUESTION));
                                Intent intent=new Intent();
                                Bundle bundle=new Bundle();
                                bundle.putString("survey", survey.toString());
                                bundle.putInt("question index", START_FROM_QUESTION);
                                bundle.putBoolean("voice hint", needVoiceHint);
                                intent.putExtras(bundle);
                                intent.setComponent(nextActivity);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            ToastLogger.logOnActivity(MainActivity.this, "網路錯誤");
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
