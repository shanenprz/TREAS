package com.assistant.reader.treas;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // For Device Permissions
    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isRecordAudioPermissionGranted = false;
    private boolean isCameraPermissionGranted = false;
    private boolean isWriteStoragePermissionGranted = false;

    // For Gesture
    private GestureDetector mDetector;

    TextView textView;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View myView = findViewById(R.id.main_view);
        textView = findViewById(R.id.editText);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            if(Boolean.TRUE.equals(result.get(Manifest.permission.RECORD_AUDIO != null))) {
                isRecordAudioPermissionGranted = result.get(Manifest.permission.RECORD_AUDIO);
            }
            if(Boolean.TRUE.equals(result.get(Manifest.permission.CAMERA != null))) {
                isCameraPermissionGranted = result.get(Manifest.permission.CAMERA);
            }
            if(Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE != null))) {
                isWriteStoragePermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
        requestPermission();

        // Initialize Gesture Detector
        mDetector = new GestureDetector(this, new MyGestureListener());

        // Add a touch listener to the View
        // The touch listener passes all its events on to the gesture detector
        myView.setOnTouchListener(touchListener);
    }

    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Toast.makeText(getApplicationContext(),"onDown",Toast.LENGTH_SHORT).show();
            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Toast.makeText(getApplicationContext(),"onSingleTap",Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Toast.makeText(getApplicationContext(),"onLongPress",Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),"Start Speaking",Toast.LENGTH_SHORT).show();
            Intent intent
                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            }
            catch (Exception error) {
                Toast.makeText(MainActivity.this, " " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Toast.makeText(getApplicationContext(),"onDoubleTap",Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    // This touch listener passes everything on to the gesture detector.
    // This will interpret the raw touch events in the app without doing it manually
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // Pass the events to the Gesture Detector
            // A return value of true means the detector is handling it
            // A return value of false means the detector didn't
            // Then recognize the event
            return mDetector.onTouchEvent(event);
        }
    };

    private void requestPermission() {
        isRecordAudioPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        isCameraPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        isWriteStoragePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<String>();

        // Check each permissions if granted
        if(!isRecordAudioPermissionGranted) {
            permissionRequest.add(Manifest.permission.RECORD_AUDIO);
        }
        if(!isCameraPermissionGranted) {
            permissionRequest.add(Manifest.permission.CAMERA);
        }
        if(!isWriteStoragePermissionGranted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!permissionRequest.isEmpty()) {
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                textView.setText(Objects.requireNonNull(result).get(0));
                voiceCommand(Objects.requireNonNull(result).get(0));
            }
        }
    }

    private void voiceCommand(String voiceOutput) {
        Intent recordsActivityIntent = new Intent(this, RecordsActivity.class);

        // voiceOutput is not Capital Letters
        if(voiceOutput.equals("records")) {
            Toast.makeText(getApplicationContext(), "Speech Recognized", Toast.LENGTH_SHORT).show();
            startActivity(recordsActivityIntent);
        }
        else {
            //Toast.makeText(getApplicationContext(),voiceOutput,Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Speech Not Recognized", Toast.LENGTH_SHORT).show();
        }
    }
}