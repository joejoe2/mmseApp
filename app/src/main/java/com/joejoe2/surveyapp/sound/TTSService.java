package com.joejoe2.surveyapp.sound;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

public class TTSService {
    public static SpeechSynthesisResult speak(String text){
        SpeechConfig speechConfig = SpeechConfig.fromSubscription("dafb684869f84f77b2aa2546b6066b49", "southcentralus");
        speechConfig.setSpeechSynthesisVoiceName("zh-TW-HsiaoChenNeural");
        AudioConfig audioConfig = AudioConfig.fromDefaultSpeakerOutput();
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig, audioConfig);
        return synthesizer.SpeakText(text);
    }
}
