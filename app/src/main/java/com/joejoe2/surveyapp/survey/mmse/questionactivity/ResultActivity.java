package com.joejoe2.surveyapp.survey.mmse.questionactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.joejoe2.surveyapp.R;
import com.joejoe2.surveyapp.activity.MainActivity;
import com.joejoe2.surveyapp.survey.mmse.data.Question;
import com.joejoe2.surveyapp.survey.mmse.data.Survey;

import org.json.JSONException;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    //data
    Survey survey;
    //ui
    TextView surveyTextView;
    TextView scoreTextView;
    Button completeButton;
    Button detailButton;

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
        detailButton=findViewById(R.id.detailButton);
    }

    private void setListener() {
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToMainActivity();
            }
        });

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailResult();
            }
        });
    }

    private void openDetailResult(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("detail");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getDetailResult());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        builder.show();
    }

    private ArrayList<String> getDetailResult(){
        ArrayList<String> res=new ArrayList<>();
        for (Question question:survey.getQuestions()){
            res.add(question.getId()+": "+question.getUserScore()+"/"+question.getFullScore());
        }
        return res;
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
