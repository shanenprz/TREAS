package com.axcleviray.treas_ocr;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class VoiceOver {
    TextToSpeech tts;
    Context context;

    String toSpeak;

    public VoiceOver(Context context, String toSpeak) {
        this.context = context;
        this.toSpeak = toSpeak;
    }

    public String getToSpeak() {
        return toSpeak;
    }

    public void speakVoiceOver(){
        tts = new TextToSpeech(this.context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.ENGLISH);
                }

                tts.speak(getToSpeak(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }
}
