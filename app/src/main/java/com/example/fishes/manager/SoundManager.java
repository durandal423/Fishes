// SoundManager.java
package com.example.fishes.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.fishes.R;

public class SoundManager {
    private SoundPool soundPool;
    private int soundEatId, soundCrashId, soundLevelUpId;
    private MediaPlayer bgmPlayer;

    public SoundManager(Context context) {
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(attrs)
                .build();
        soundEatId = soundPool.load(context, R.raw.eat_sound, 1);
        soundCrashId = soundPool.load(context, R.raw.crash_sound, 1);
        // 这里改成正确的 R.raw.level_up（资源文件名与之保持一致）
        soundLevelUpId = soundPool.load(context, R.raw.level_up, 1);

        bgmPlayer = MediaPlayer.create(context, R.raw.background_music);
        bgmPlayer.setLooping(true);
    }

    public void playEatSound() {
        soundPool.play(soundEatId, 1f, 1f, 1, 0, 1f);
    }

    public void playCrashSound() {
        soundPool.play(soundCrashId, 1f, 1f, 1, 0, 1f);
    }

    public void playLevelUpSound() {
        soundPool.play(soundLevelUpId, 1f, 1f, 1, 0, 1f);
    }

    public void startBackgroundMusic() {
        if (bgmPlayer != null && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    public void stopBackgroundMusic() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    public void stopAllSounds() {
        if (soundPool != null) {
            soundPool.autoPause();
        }
    }

    public void release() {
        if (soundPool != null) soundPool.release();
        if (bgmPlayer != null) bgmPlayer.release();
    }
}
