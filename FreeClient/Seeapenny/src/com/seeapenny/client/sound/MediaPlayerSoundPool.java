package com.seeapenny.client.sound;

import android.app.Activity;
import android.media.MediaPlayer;

public class MediaPlayerSoundPool {

    private int index = 0;
    private MediaPlayer[] mediaPlayers;
    private boolean alive = true;

    public MediaPlayerSoundPool(Activity activity, int sound, int count) {
        assert count > 0;
        mediaPlayers = new MediaPlayer[count];
        for (int i = 0; i < count; i++) {
            mediaPlayers[i] = MediaPlayer.create(activity.getApplicationContext(), sound);
        }
    }

    public synchronized void setVolume(float r, float l) {
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.setVolume(r, l);
        }
    }

    public synchronized void play() {
        ++index;
        if (index >= mediaPlayers.length) {
            index = 0;
        }

        if (alive) {
            mediaPlayers[index].start();
        }
    }

    public synchronized void stop() {
        if (!alive) {
            alive = false;
            for (MediaPlayer mediaPlayer : mediaPlayers) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }

}
