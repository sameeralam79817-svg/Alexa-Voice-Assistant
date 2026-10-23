package com.sameer.alexa;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech tts;
    private TextView statusText;

    private static final int VOICE_REQUEST = 100;
    private static final int MIC_PERMISSION_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 80, 40, 40);

        TextView title = new TextView(this);
        title.setText("Alexa Voice Assistant");
        title.setTextSize(26);

        statusText = new TextView(this);
        statusText.setText("Alexa तैयार है!");
        statusText.setTextSize(18);

        Button button = new Button(this);
        button.setText("🎤 Alexa से बात करें");

        layout.addView(title);
        layout.addView(statusText);
        layout.addView(button);

        setContentView(layout);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });

        button.setOnClickListener(v -> checkMicrophonePermission());
    }

    private void checkMicrophonePermission() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else {
            requestPermissions(
                new String[]{Manifest.permission.RECORD_AUDIO},
                MIC_PERMISSION_REQUEST
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == MIC_PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            } else {
                statusText.setText("Microphone की अनुमति चाहिए।");
            }
        }
    }

    private void startListening() {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");

        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT, "बोलिए...");

        try {
            startActivityForResult(intent, VOICE_REQUEST);
        } catch (Exception e) {
            statusText.setText("Voice Recognition उपलब्ध नहीं है।");
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_REQUEST &&
                resultCode == RESULT_OK && data != null) {

            ArrayList<String> results =
                    data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {
                String speech = results.get(0);
                statusText.setText("आपने कहा: " + speech);
                respondToSpeech(speech);
            }
        }
    }

    private void respondToSpeech(String speech) {
        String reply;

        if (speech.contains("नमस्ते") ||
                speech.toLowerCase().contains("hello")) {
            reply = "नमस्ते! मैं आपकी क्या मदद कर सकती हूँ?";
        } else if (speech.contains("नाम")) {
            reply = "मेरा नाम एलेक्सा है।";
        } else {
            reply = "मैंने आपकी बात सुनी: " + speech;
        }

        if (tts != null) {
            tts.speak(reply, TextToSpeech.QUEUE_FLUSH,
                    null, "alexa_reply");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
