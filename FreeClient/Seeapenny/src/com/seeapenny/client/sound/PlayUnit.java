package com.seeapenny.client.sound;

import android.media.MediaPlayer;

public class PlayUnit {

    private MediaPlayer mp;
    private boolean state;

    public PlayUnit(MediaPlayer mediaPlayer, Boolean state) {
        this.setMp(mediaPlayer);
        this.setState(state);
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public void setMp(MediaPlayer mp) {
        this.mp = mp;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
