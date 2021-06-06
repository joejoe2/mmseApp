package com.joejoe2.surveyapp.survey.mmse.service;

import android.os.AsyncTask;

import com.joejoe2.surveyapp.util.OnCompleteCallable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SurveyProvider extends AsyncTask<Void, Void, Boolean> {
    private String surveyType;
    String userId;
    private String result;
    private OnCompleteCallable onCompleteCallable;
    private static final String SERVER ="https://mmse-service.herokuapp.com";

    public SurveyProvider(String surveyType, String userId, OnCompleteCallable onCompleteCallable) {
        this.surveyType = surveyType;
        this.userId=userId;
        this.onCompleteCallable=onCompleteCallable;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(getRequestUrl());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-type", "application/json");
            String json = "{\"user id\": \""+userId+"\", \"survey type\": \""+surveyType+"\", \"time\": \"yyyy/mm/dd\"}";
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + json);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());
            result=response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * build the request url
     * @return the request url which can be sent directly to server
     */
    private String getRequestUrl(){
        return SERVER+"/get_survey";
    }

    @Override
    protected void onPostExecute(Boolean success) {
        onCompleteCallable.doOnComplete(result, success);
    }
}
