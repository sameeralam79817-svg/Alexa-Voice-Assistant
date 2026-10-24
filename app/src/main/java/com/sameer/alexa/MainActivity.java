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

import com.openwakeword.OpenWakeWord;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech tts;
    private TextView statusText;
    private OpenWakeWord detector;

    private boolean wakeWordActive = false;
    private boolean startWakeAfterPermission = false;

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

        Button startButton = new Button(this);
        startButton.setText("Start Alexa");

        Button stopButton = new Button(this);
        stopButton.setText("Stop Alexa");

        Button talkButton = new Button(this);
        talkButton.setText("🎤 Alexa से बात करें");

        layout.addView(title);
        layout.addView(statusText);
        layout.addView(startButton);
        layout.addView(stopButton);
        layout.addView(talkButton);

        setContentView(layout);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });

        startButton.setOnClickListener(v -> {
            checkMicrophonePermission(true);
        });

        stopButton.setOnClickListener(v -> {
            stopWakeWord();
            statusText.setText("Alexa बंद है।");
        });

        talkButton.setOnClickListener(v -> {
            stopWakeWord();
            checkMicrophonePermission(false);
        });
    }

    private void checkMicrophonePermission(
            boolean forWakeWord) {

        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            if (forWakeWord) {
                startWakeWord();
            } else {
                startListening();
            }

        } else {

            startWakeAfterPermission = forWakeWord;

            requestPermissions(
                new String[]{
                    Manifest.permission.RECORD_AUDIO
                },
                MIC_PERMISSION_REQUEST
            );
        }
    }

    private void startWakeWord() {

        if (wakeWordActive) {
            return;
        }

        try {

            statusText.setText(
                "Alexa शुरू हो रही है..."
            );

            if (detector == null) {

                detector = new OpenWakeWord.Builder(this)
                    .setModel(
                        OpenWakeWord.BuiltInModel.ALEXA
                    )
                    .setThreshold(0.5f)
                    .build();
            }

            wakeWordActive = true;

            detector.start(score -> {

                if (!wakeWordActive) {
                    return;
                }

                stopWakeWord();

                statusText.setText(
                    "Alexa ने आपकी आवाज पहचान ली!"
                );

                if (tts != null) {
                    tts.speak(
                        "जी, बोलिए!",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "alexa_wake_reply"
                    );
                }
            });

            statusText.setText(
                "Alexa सुन रही है..."
            );

        } catch (Exception e) {

            wakeWordActive = false;

            statusText.setText(
                "Wake Word Error: " + e.getMessage()
            );
        }
    }

    private void stopWakeWord() {

        wakeWordActive = false;

        if (detector != null) {
            detector.stop();
        }
    }

    private void startListening() {

        Intent intent = new Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        );

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "hi-IN"
        );

        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "बोलिए..."
        );

        try {

            startActivityForResult(
                intent,
                VOICE_REQUEST
            );

        } catch (Exception e) {

            statusText.setText(
                "Voice Recognition उपलब्ध नहीं है।"
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        );

        if (requestCode == MIC_PERMISSION_REQUEST) {

            if (grantResults.length > 0
