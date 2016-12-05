package com.seeapenny.client.sound;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import com.seeapenny.client.SeeapennyApp;

import java.util.HashMap;
import java.util.Map;

public class SoundPlayerMediaPlayer implements SoundPlayer {

    private boolean isSoundOn = false;
    private boolean loaded = false;

    private Activity activity;
    private Map<Integer, MediaPlayerSoundPool> soundPoolMap;

    private MediaPlayer mediaPlayerAmbience;
    private float ambienceVolume = 1f;

    private float volume = 1f;

    public synchronized void initSounds(Activity activity) {
        this.activity = activity;

        SeeapennyApp economyApp= SeeapennyApp.getInstance();
        isSoundOn = economyApp.isSound();
        if (!isSoundOn) return;

        try {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

            soundPoolMap = new HashMap<Integer, MediaPlayerSoundPool>();
            loaded = true;
        } catch (Exception ex) {
//            Logger.error("Sound init", ex);
        }
    }

    public synchronized void play(int sound, float sampleVolume) {
        if (!isSoundOn || !loaded || sampleVolume < 0.3f) return;

        try {
            MediaPlayerSoundPool mediaPlayerSoundPool = soundPoolMap.get(sound);
            if (mediaPlayerSoundPool == null) {
                mediaPlayerSoundPool = new MediaPlayerSoundPool(activity, sound, 4);
                soundPoolMap.put(sound, mediaPlayerSoundPool);
            }

            mediaPlayerSoundPool.setVolume(sampleVolume * volume, sampleVolume * volume);
            mediaPlayerSoundPool.play();
        } catch (Exception ex) {
//            Logger.error("Sound " + sample, ex, false);
        }
    }

    @Override
    public void ambience(int sound, float ambienceVolume) {
        if (!isSoundOn || !loaded) return;

        if (mediaPlayerAmbience != null && mediaPlayerAmbience.isPlaying()) {
            mediaPlayerAmbience.stop();
            mediaPlayerAmbience.release();
            mediaPlayerAmbience = null;
        }

        mediaPlayerAmbience = MediaPlayer.create(activity.getApplicationContext(), sound);
        if (mediaPlayerAmbience != null) {
            this.ambienceVolume = ambienceVolume;
            mediaPlayerAmbience.setVolume(volume * ambienceVolume, volume * ambienceVolume);
            mediaPlayerAmbience.setLooping(true);
            mediaPlayerAmbience.start();
        }
    }

    public synchronized void stop() {
        try {
            if (loaded) {
                loaded = false;
                for (MediaPlayerSoundPool mediaPlayerSoundPool : soundPoolMap.values()) {
                    mediaPlayerSoundPool.stop();
                }
                soundPoolMap.clear();
            }
        } catch (Exception ex) {
//            Logger.error("Sound stop", ex);
        }

        try {
            if (mediaPlayerAmbience != null && mediaPlayerAmbience.isPlaying()) {
                mediaPlayerAmbience.stop();
                mediaPlayerAmbience.release();
                mediaPlayerAmbience = null;
            }
        } catch (Exception ex) {
//            Logger.error("Ambience stop", ex);
        }
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;

        if (mediaPlayerAmbience != null && mediaPlayerAmbience.isPlaying()) {
            mediaPlayerAmbience.setVolume(volume * ambienceVolume, volume * ambienceVolume);
        }
    }

    @Override
    public float getVolume() {
        return volume;
    }
}
