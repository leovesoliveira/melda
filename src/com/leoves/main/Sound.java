package com.leoves.main;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

  public static final Sound backgroundMusic = new Sound("/music.wav");
  public static final Sound hurtEffect = new Sound("/hurt_effect.wav");
  public static final Sound arrowEffect = new Sound("/arrow_effect.wav");
  public static final Sound arrowWallEffect = new Sound("/arrow_wall_effect.wav");
  public static final Sound itemEffect = new Sound("/item_effect.wav");
  public static final Sound damageEffect = new Sound("/damage_effect.wav");
  public static final Sound gameOver = new Sound("/game_over.wav");
  public static final Sound levelEffect = new Sound("/level_effect.wav");
  private Clip clip;

  private Sound(String name) {
    try {
      InputStream is = Sound.class.getResourceAsStream(name);
      InputStream bufferedIn = new BufferedInputStream(is);
      AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);

      clip = AudioSystem.getClip();
      clip.open(ais);
    } catch (Exception e) {
      System.err.println("Erro ao carregar o som: " + name);
      e.printStackTrace();
    }
  }

  public void play() {
    if (clip == null) return;

    if (clip.isRunning()) {
      clip.stop();
    }
    clip.setFramePosition(0);
    clip.start();
  }

  public void loop() {
    if (clip == null) return;
    clip.setFramePosition(0);
    clip.loop(Clip.LOOP_CONTINUOUSLY);
  }

  public void stop() {
    if (clip != null && clip.isRunning()) {
      clip.stop();
    }
  }
}
