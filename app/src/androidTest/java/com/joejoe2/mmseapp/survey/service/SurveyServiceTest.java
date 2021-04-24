package com.joejoe2.mmseapp.survey.service;

import com.joejoe2.mmseapp.survey.data.Survey;
import com.joejoe2.mmseapp.util.OnCompleteCallable;

import org.json.JSONException;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SurveyServiceTest {

    @Test
    public void getSurvey() throws Exception{
        final CountDownLatch signal = new CountDownLatch(1);

        SurveyService.getSurvey("mmse", "test", new OnCompleteCallable() {
            @Override
            public void doOnComplete(String msg, boolean success) {
                assertTrue(success);
                try {
                    Survey survey=new Survey(msg);
                    System.out.println(survey.getQuestionNum());
                    assertEquals("mmse", survey.getType());
                    assertEquals(30, survey.getFullScore());
                    assertEquals("mmse-time-day", survey.getQuestion(2).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                signal.countDown();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }
}