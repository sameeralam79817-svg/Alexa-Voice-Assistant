package com.sameer.alexa;

import android.content.Context;
import java.io.File;

public class VoiceTraining {

    private final File trainingDirectory;

    public VoiceTraining(Context context) {
        trainingDirectory = new File(
            context.getFilesDir(),
            "voice_training"
        );

        if (!trainingDirectory.exists()) {
            trainingDirectory.mkdirs();
        }
    }

    public File getTrainingDirectory() {
        return trainingDirectory;
    }
}
