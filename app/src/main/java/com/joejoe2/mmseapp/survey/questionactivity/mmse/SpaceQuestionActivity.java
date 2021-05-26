package com.joejoe2.mmseapp.survey.questionactivity.mmse;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joejoe2.mmseapp.R;
import com.joejoe2.mmseapp.util.CanvasVertex;
import com.joejoe2.mmseapp.util.DrawingView;
import com.joejoe2.mmseapp.util.GraphTools;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;

public class SpaceQuestionActivity extends QuestionActivity {
    //data
    private String answer;
    private String userOption="fail";
    private Graph<CanvasVertex, DefaultEdge> userDrawing;
    //ui
    private Button completeButton, clearButton;
    private TextView questionHintTextView;
    private TextView leftTimeTextView;
    private ImageView actionImage;
    private DrawingView canvas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //choose layout
        setContentView(R.layout.activity_space_question);
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

    private String getAnswer(){
        if (question.getType().equals("draw_intersections")){
            return "success";
        }
        return "";
    }

    @Override
    String getQuestionHint() {
        if (question.getType().equals("draw_intersections")){
            return "請在下方畫出相同的圖形";
        }
        return "";
    }

    @Override
    void initUI() {
        questionHintTextView=findViewById(R.id.question_hint);
        questionHintTextView.setText(question.getId()+":"+questionHint);
        actionImage=findViewById(R.id.action_image);
        if (question.getType().equals("draw_intersections")){
            actionImage.setImageResource(R.drawable.overlap);
        }
        canvas=findViewById(R.id.canvas);
        clearButton=findViewById(R.id.clear_button);
        completeButton=findViewById(R.id.complete_button);
        leftTimeTextView = findViewById(R.id.left_time);
        leftTimeTextView.setText(timeLimitInSec+"");
    }

    @Override
    void setListeners() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.clear();
            }
        });
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeQuestion();
            }
        });
    }

    @Override
    void calculateResult() {
        userDrawing=canvas.getDrawing();
        List<Graph<CanvasVertex, DefaultEdge>> pentagons= GraphTools.findPurePentagonsInGraph(userDrawing);
        if (isExactTwoPentagonsOverlapQuadratically(pentagons)){
            //success
            userOption="success";
        }else {
            //fail
            userOption="fail";
        }

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

    private boolean isExactTwoPentagonsOverlapQuadratically(List<Graph<CanvasVertex, DefaultEdge>> pentagons){
        return pentagons.size()==2&&isExactContainOnePointInEach2DEnclosure(pentagons.get(0).vertexSet(), pentagons.get(1).vertexSet());
    }

    private boolean isExactContainOnePointInEach2DEnclosure(Set<CanvasVertex> graph1, Set<CanvasVertex> graph2){
        int in1=0, in2=0;
        ArrayList<float[]> poly1=new ArrayList<>();
        ArrayList<float[]> poly2=new ArrayList<>();
        for (CanvasVertex vertex:graph1){
            poly1.add(new float[]{vertex.getX(), vertex.getY()});
        }
        for (CanvasVertex vertex:graph2){
            poly2.add(new float[]{vertex.getX(), vertex.getY()});
        }

        for (CanvasVertex vertex:graph1){
            if(GraphTools.isPointInPolygons(vertex.getX(), vertex.getY(), poly2)){
                in2++;
            }
        }
        for (CanvasVertex vertex:graph2){
            if(GraphTools.isPointInPolygons(vertex.getX(), vertex.getY(), poly1)){
                in1++;
            }
        }
        return in1==1&&in2==1;
    }
}
