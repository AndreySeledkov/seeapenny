package com.seeapenny.client.sound;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import com.seeapenny.client.SeeapennyApp;

import java.util.HashMap;
import java.util.Map;

public class SoundPlayerSoundPool implements SoundPlayer {

    private boolean isSoundOn = false;
    private boolean loaded = false;

    private Activity activity;
    private SoundPool soundPool;
    private Map<Integer, Integer> soundPoolMap;

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

            soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
            soundPoolMap = new HashMap<Integer, Integer>();
            if (Build.VERSION.SDK_INT > 7) {
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        loaded = true;
                    }
                });
            } else {
                loaded = true;
            }
            loaded = true;
        } catch (Throwable ex) {
//            Logger.error("Sound init", ex, false);
        }
    }

    public synchronized void play(int sound, final float sampleVolume) {
        if (!isSoundOn || !loaded || sampleVolume < 0.3f) return;

        try {
            Integer sID = soundPoolMap.get(sound);
            if (sID == null) {
                final Object loadMonitor = new Object();
                final Integer soundID = soundPool.load(activity,sound, 1);
                soundPoolMap.put(sound, soundID);
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                        synchronized (loadMonitor) {
                            loadMonitor.notifyAll();
                        }
                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (loadMonitor) {
                            try {
                                loadMonitor.wait(1000);
                                soundPool.play(soundID, sampleVolume * volume, sampleVolume * volume, 1, 0, 1);
                            } catch (InterruptedException e) {
//                                Logger.error("new Thread Sound: ", e, false);
                            }
                        }        
                    }
                }).start();
                
            } else {
                soundPool.play(sID, sampleVolume * volume, sampleVolume * volume, 1, 0, 1);
            }
            
        } catch (Throwable ex) {
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
                for (Integer soundID : soundPoolMap.values()) {
                    soundPool.stop(soundID);
                }
                soundPoolMap.clear();
                soundPool.release();
            }
        } catch (Throwable ex) {
//            Logger.error("Sound stop", ex, false);
        }

        try {
            if (mediaPlayerAmbience != null && mediaPlayerAmbience.isPlaying()) {
                mediaPlayerAmbience.stop();
                mediaPlayerAmbience.release();
                mediaPlayerAmbience = null;
            }
        } catch (Throwable ex) {
//            Logger.error("Ambience stop", ex, false);
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
