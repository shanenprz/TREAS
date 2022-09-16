package com.axcleviray.treas_ocr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    VoiceOver voiceOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String stringToSpeak = getString(R.string.main_welcome);

        voiceOver = new VoiceOver(getApplicationContext(), stringToSpeak);
        voiceOver.speakVoiceOver();
    }

    @Override
    protected void onPause() {
        if(voiceOver.tts != null){
            voiceOver.tts.stop();
            voiceOver.tts.shutdown();
        }
        super.onPause();
    }
}