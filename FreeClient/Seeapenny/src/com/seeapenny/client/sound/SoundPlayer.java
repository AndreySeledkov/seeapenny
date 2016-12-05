package com.seeapenny.client.sound;

import android.app.Activity;

public interface SoundPlayer {

    public void initSounds(Activity activity);

    public void play(int sound, float volume);

    public void ambience(int sound, float volume);

    public void stop();

    public void setVolume(float volume);

    public float getVolume();
}
