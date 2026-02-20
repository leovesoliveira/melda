package com.leoves.main;

import com.leoves.world.World;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import javax.imageio.ImageIO;

public class Menu {

  public static boolean isPaused = false;
  public static boolean saveExists = false;
  public static boolean savingGame = false;
  public String[] options = {"NEW_GAME", "LOAD_GAME", "EXIT"};
  public int currentOption = 0;
  public int maxOption = options.length - 1;
  public boolean up, down, enter;
  private BufferedImage logo;
  private BufferedImage background;
  private BufferedImage buttonsSpritesheet;
  private BufferedImage[] newGameButton;
  private BufferedImage[] loadGameButton;
  private BufferedImage[] exitButton;
  private BufferedImage[] continueButton;
  private int bw = 131;
  private int bh = 54;

  public Menu() {
    try {
      logo = ImageIO.read(getClass().getResource("/logo.png"));
      background = ImageIO.read(getClass().getResource("/menu-background.png"));
      buttonsSpritesheet = ImageIO.read(getClass().getResource("/buttons-spritesheet.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    newGameButton = new BufferedImage[2];
    newGameButton[0] = buttonsSpritesheet.getSubimage(bw * 0, bh * 0, bw, bh);
    newGameButton[1] = buttonsSpritesheet.getSubimage(bw * 1, bh * 0, bw, bh);

    loadGameButton = new BufferedImage[2];
    loadGameButton[0] = buttonsSpritesheet.getSubimage(bw * 0, bh * 1, bw, bh);
    loadGameButton[1] = buttonsSpritesheet.getSubimage(bw * 1, bh * 1, bw, bh);

    exitButton = new BufferedImage[2];
    exitButton[0] = buttonsSpritesheet.getSubimage(bw * 0, bh * 2, bw, bh);
    exitButton[1] = buttonsSpritesheet.getSubimage(bw * 1, bh * 2, bw, bh);

    continueButton = new BufferedImage[2];
    continueButton[0] = buttonsSpritesheet.getSubimage(bw * 0, bh * 3, bw, bh);
    continueButton[1] = buttonsSpritesheet.getSubimage(bw * 1, bh * 3, bw, bh);
  }

  public static void applySave(String str) {
    String[] spl = str.split("/");

    for (int i = 0; i < spl.length; i++) {
      String[] spl2 = spl[i].split(":");

      switch (spl2[0]) {
        case "level":
          World.restartGame("level" + spl2[1] + ".png");
          Game.state = "DEFAULT";
          isPaused = false;
          break;
      }
    }
  }

  public static String loadGame(int encode) {
    String line = "";
    File file = new File("save.txt");

    if (file.exists()) {
      try {
        String singleLine = null;

        BufferedReader reader = new BufferedReader(new FileReader("save.txt"));

        try {
          while ((singleLine = reader.readLine()) != null) {
            String[] trans = singleLine.split(":");
            char[] val = trans[1].toCharArray();
            trans[1] = "";

            for (int i = 0; i < val.length; i++) {
              val[i] -= encode;
              trans[1] += val[i];
            }

            line += trans[0];
            line += ":";
            line += trans[1];
            line += "/";
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    return line;
  }

  public static void saveGame(String[] val1, int[] val2, int encode) {
    BufferedWriter write = null;

    try {
      write = new BufferedWriter(new FileWriter("save.txt"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    for (int i = 0; i < val1.length; i++) {
      String current = val1[i];
      current += ":";

      char[] value = Integer.toString(val2[i]).toCharArray();

      for (int n = 0; n < value.length; n++) {
        value[n] += encode;
        current += value[n];
      }

      try {
        write.write(current);
        if (i < val1.length - 1) write.newLine();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    try {
      write.flush();
      write.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void tick() {
    File file = new File("save.txt");
    saveExists = file.exists();

    if (up) {
      up = false;
      currentOption--;
      if (currentOption < 0) {
        currentOption = maxOption;
      }
    }
    if (down) {
      down = false;
      currentOption++;
      if (currentOption > maxOption) {
        currentOption = 0;
      }
    }
    if (enter) {
      enter = false;
      isPaused = false;

      if (Objects.equals(options[currentOption], "NEW_GAME")) {
        Game.state = "DEFAULT";
        file = new File("save.txt");
        file.delete();
      }
      if (Objects.equals(options[currentOption], "LOAD_GAME")) {
        file = new File("save.txt");
        if (file.exists()) {
          String saver = loadGame(10);
          applySave(saver);
        }
      }
      if (Objects.equals(options[currentOption], "EXIT")) {
        System.exit(1);
      }
    }
  }

  public void render(Graphics g) {
    if (!isPaused) {
      g.setColor(Color.white);
      g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);

      g.drawImage(background, 0, 0, null);
    } else {
      Graphics2D g2 = (Graphics2D) g;

      g2.setColor(new Color(0, 0, 0, 75));
      g2.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
    }

    g.drawImage(logo, 0, 0, null);

    int bx = 415;
    int by = 405;
    int bs = 10;
    if (!isPaused) {
      g.drawImage(newGameButton[0], bx, by, null);
    } else {
      g.drawImage(continueButton[0], bx, by, null);
    }
    g.drawImage(loadGameButton[0], bx, by + (bh * 1) + (bs * 1), null);
    g.drawImage(exitButton[0], bx, by + (bh * 2) + (bs * 2), null);

    if (Objects.equals(options[currentOption], "NEW_GAME")) {
      if (!isPaused) {
        g.drawImage(newGameButton[1], bx, by, null);
      } else {
        g.drawImage(continueButton[1], bx, by, null);
      }

    } else if (Objects.equals(options[currentOption], "LOAD_GAME")) {
      g.drawImage(loadGameButton[1], bx, by + (bh * 1) + (bs * 1), null);
    } else if (Objects.equals(options[currentOption], "EXIT")) {
      g.drawImage(exitButton[1], bx, by + (bh * 2) + (bs * 2), null);
    }
  }
}
