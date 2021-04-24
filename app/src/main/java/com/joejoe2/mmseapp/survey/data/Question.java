package com.joejoe2.mmseapp.survey.data;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * to store and manage question data
 */
public class Question {
    private JSONObject rawQuestion;

    public Question(JSONObject rawQuestion) {
        this.rawQuestion = rawQuestion;
    }

    public String getId(){
        try {
            return  rawQuestion.getString("question id");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCategory(){
        try {
            return  rawQuestion.getString("category");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getType(){
        try {
            return  rawQuestion.getString("type");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getInfo(){
        try {
            return  rawQuestion.getString("info");
        } catch (JSONException e) {
            return "";
        }
    }

    public JSONObject getData(){
        try {
            return rawQuestion.getJSONObject("data");
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public JSONObject getUserAnswer(){
        try {
            return  rawQuestion.getJSONObject("user answer");
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public void setUserAnswer(JSONObject answer){
        try {
            rawQuestion.put("user answer", answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getUserScore(){
        try {
            return  rawQuestion.getInt("user score");
        } catch (JSONException e) {
            return 0;
        }
    }

    public void setUserScore(int score){
        try {
            rawQuestion.put("user score", score);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getFullScore(){
        try {
            return  rawQuestion.getInt("full score");
        } catch (JSONException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return rawQuestion.toString();
    }
}
