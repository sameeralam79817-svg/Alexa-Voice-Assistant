package com.sameer.alexa;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 80, 40, 40);

        TextView title = new TextView(this);
        title.setText("Alexa Voice Assistant");
        title.setTextSize(26);

        Button button = new Button(this);
        button.setText("Talk to Alexa");

        layout.addView(title);
        layout.addView(button);

        setContentView(layout);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });

        button.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak(
                    "नमस्ते! मैं एलेक्सा हूँ। बताइए, मैं आपकी क्या मदद कर सकती हूँ?",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "alexa_speech"
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop
