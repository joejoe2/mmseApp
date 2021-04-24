package com.joejoe2.mmseapp.survey.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * to store and manage survey data
 */
public class Survey {
    private String rawTxt;
    private String userId;
    private String type;
    private String time;
    private ArrayList<Question> questions;
    private int totalScore;
    private int fullScore;

    public Survey(String rawTxt) throws JSONException{
        this.rawTxt = rawTxt;
        decode();
    }

    private void decode() throws JSONException {
        JSONObject json = new JSONObject(rawTxt);
        userId=json.getString("user id");
        type=json.getString("survey type");
        time=json.getString("time");
        totalScore=json.getInt("total score");
        fullScore=json.getInt("full score");

        JSONArray jsonQuestions=json.getJSONArray("questions");
        questions=new ArrayList<>();
        for (int i = 0; i < jsonQuestions.length(); i++) {
            questions.add(new Question(jsonQuestions.getJSONObject(i)));
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public Question getQuestion(int index) {
        return questions.get(index);
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public int getQuestionNum() {
        return questions.size();
    }

    public int getTotalScore() {
        updateSummary();
        return totalScore;
    }

    public void updateSummary(){
        totalScore=0;
        for (Question question: questions) {
            totalScore+=question.getUserScore();
        }
    }

    public int getFullScore() {
        return fullScore;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        updateSummary();
        try {
            JSONObject jsonObject=new JSONObject(rawTxt);
            jsonObject.put("time", time);
            jsonObject.put("total score", totalScore);
            JSONArray jsonArray=new JSONArray();
            for (Question question:questions) {
                jsonArray.put(new JSONObject(question.toString()));
            }
            jsonObject.put("questions", jsonArray);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
