package com.seeapenny.client.sound;

import android.os.Build;

public class SoundPlayerFactory {

    public static SoundPlayer createSoundPlayer() {
        if ((
                "GT-I9100".equals(Build.MODEL)
                        || "GT-I9103".equals(Build.MODEL)
                        || "GT-S5670".equals(Build.MODEL)
                        || "ST27i".equals(Build.MODEL)
                        || "ST25i".equals(Build.MODEL)
                        || "MT27i".equals(Build.MODEL)
        )
                && Build.VERSION.RELEASE.startsWith("2")) {
            return new SoundPlayerMediaPlayer();
        } else if ((
                "chagall".equals(Build.MODEL)
                        || "MZ604".equals(Build.MODEL)
                        || "A100".equals(Build.MODEL)
        )
                && Build.VERSION.RELEASE.startsWith("4")) {
            return new SoundPlayerMediaPlayer();
        } else {
            return new SoundPlayerSoundPool();
        }
    }
}
