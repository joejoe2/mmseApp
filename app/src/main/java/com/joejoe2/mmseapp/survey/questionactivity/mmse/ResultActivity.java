package com.joejoe2.mmseapp.survey.questionactivity.mmse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joejoe2.mmseapp.R;
import com.joejoe2.mmseapp.activity.MainActivity;
import com.joejoe2.mmseapp.survey.data.Question;
import com.joejoe2.mmseapp.survey.data.Survey;

import org.json.JSONException;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    //data
    Survey survey;
    //ui
    TextView surveyTextView;
    TextView scoreTextView;
    Button completeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getBundle();
        initUI();
        setListener();
    }

    /**
     * receive params(survey) from bundle
     */
    void getBundle(){
        try {
            Bundle bundle = this.getIntent().getExtras();
            survey=new Survey(bundle.getString("survey", "{}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        surveyTextView=findViewById(R.id.surveyTextView);
        surveyTextView.setText(survey.getType()+" survey");
        scoreTextView=findViewById(R.id.scoreTextView);
        scoreTextView.setText("分數: "+survey.getTotalScore()+"/"+survey.getFullScore());
        completeButton=findViewById(R.id.completeButton);
    }

    private void setListener() {
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToMainActivity();
            }
        });
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
