package com.joejoe2.surveyapp.sound;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;

import org.junit.Test;

public class TTSServiceTest {

    @Test
    public void speak() {
        try {
            // Note: this will block the UI thread, so eventually, you want to register for the event
            SpeechSynthesisResult result = TTSService.speak("你好");
            assert(result != null);

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Speech synthesis succeeded.");
            }
            else if (result.getReason() == ResultReason.Canceled) {
                String cancellationDetails =
                        SpeechSynthesisCancellationDetails.fromResult(result).toString();
                System.out.println(("Error synthesizing. Error detail: " +
                        System.lineSeparator() + cancellationDetails +
                        System.lineSeparator() + "Did you update the subscription info?"));
            }

            result.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            assert(false);
        }
    }
}