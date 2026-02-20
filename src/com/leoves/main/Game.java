package com.leoves.main;

import com.leoves.entities.Arrow;
import com.leoves.entities.Enemy;
import com.leoves.entities.Entity;
import com.leoves.entities.Player;
import com.leoves.graphics.Spritesheet;
import com.leoves.graphics.UI;
import com.leoves.world.World;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Game extends Canvas implements Runnable, KeyListener {

  public static final int WIDTH = 240;
  public static final int HEIGHT = 160;
  public static final int SCALE = 4;
  public static JFrame frame;
  public static Spritesheet spritesheet;
  public static World world;
  public static Player player;
  public static List<Entity> entities;
  public static List<Enemy> enemies;
  public static List<Arrow> arrows;
  public static Random rand;
  public static String state = "MENU";
  public static Font fontMd;
  public static Font fontSm;
  public UI ui;
  public Menu menu;
  public boolean savingGame = false;
  public InputStream stream =
      ClassLoader.getSystemClassLoader().getResourceAsStream("Jersey10.ttf");
  public int[] pixels;
  public BufferedImage mapLight;
  public int[] mapLightPixels;
  private boolean isRunning = true;
  private Thread thread;
  private BufferedImage image;
  private int currentLevel = 1, maxLevel = 2;
  private boolean restartOnEnter = false;

  public Game() {
    Sound.backgroundMusic.loop();
    rand = new Random();
    addKeyListener(this);
    setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
    initFrame();

    ui = new UI();
    image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

    try {
      mapLight = ImageIO.read(getClass().getResource("/map-light.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    mapLightPixels = new int[mapLight.getWidth() * mapLight.getHeight()];
    mapLight.getRGB(
        0, 0, mapLight.getWidth(), mapLight.getHeight(), mapLightPixels, 0, mapLight.getWidth());

    pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    entities = new ArrayList<>();
    enemies = new ArrayList<>();
    arrows = new ArrayList<>();

    spritesheet = new Spritesheet("/spritesheet.png");

    player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
    entities.add(player);

    world = new World("/level1.png");

    try {
      fontMd = Font.createFont(Font.TRUETYPE_FONT, stream);
    } catch (FontFormatException | IOException e) {
      throw new RuntimeException(e);
    }

    menu = new Menu();
  }

  public void initFrame() {
    frame = new JFrame("Melda");
    frame.add(this);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  public synchronized void start() {
    thread = new Thread(this);
    isRunning = true;
    thread.start();
  }

  public synchronized void stop() {
    isRunning = false;

    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void tick() {
    if (Objects.equals(state, "DEFAULT")) {
      if (this.savingGame) {
        this.savingGame = false;
        String[] opt1 = {"level"};
        int[] opt2 = {this.currentLevel};
        Menu.saveGame(opt1, opt2, 10);
        System.out.println("Jogo salvo!");
      }

      restartOnEnter = false;
      for (int i = 0; i < entities.size(); i++) {
        Entity e = entities.get(i);
        e.tick();
      }

      for (int i = 0; i < arrows.size(); i++) {
        arrows.get(i).tick();
      }

      if (enemies.isEmpty()) {
        currentLevel++;

        if (currentLevel > maxLevel) {
          currentLevel = 1;
          state = "MENU";
        }

        Sound.levelEffect.play();
        String newWorld = "level" + currentLevel + ".png";
        World.restartGame(newWorld);
      }
    } else if (Objects.equals(state, "GAME_OVER")) {
      if (restartOnEnter) {
        state = "DEFAULT";
        currentLevel = 1;
        restartOnEnter = false;
        World.restartGame("level" + currentLevel + ".png");
      }
    } else if (Objects.equals(state, "MENU")) {
      menu.tick();
    }
  }

  public void applyLight() {
    for (int xx = 0; xx < Game.WIDTH; xx++) {
      for (int yy = 0; yy < Game.HEIGHT; yy++) {
        if (mapLightPixels[xx + (yy * Game.WIDTH)] == 0xFFFFFFFF) {
          pixels[xx + (yy * Game.WIDTH)] = 0xFF000000;
        }
      }
    }
  }

  public void render() {
    BufferStrategy bs = this.getBufferStrategy();

    if (bs == null) {
      this.createBufferStrategy(3);
      return;
    }

    Graphics g = image.getGraphics();
    g.setColor(new Color(0, 0, 0));
    g.fillRect(0, 0, WIDTH, HEIGHT);

    world.render(g);

    for (int i = 0; i < entities.size(); i++) {
      Entity e = entities.get(i);
      e.render(g);
    }

    for (int i = 0; i < arrows.size(); i++) {
      arrows.get(i).render(g);
    }

    applyLight();

    g.dispose();
    g = bs.getDrawGraphics();
    g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);

    ui.render(g);

    player.updateCamera();

    if (Objects.equals(state, "GAME_OVER")) {
      Graphics2D g2 = (Graphics2D) g;

      g2.setColor(new Color(0, 0, 0, 100));
      g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

      g2.setColor(new Color(0, 0, 0, 200));
      g2.fillRect(67 * SCALE, 57 * SCALE, 450, 150);

      g2.setColor(Color.white);
      g2.setFont(new Font("arial", Font.BOLD, 40));
      g2.drawString("GAME OVER", ((WIDTH * SCALE) / 2) - 110, ((HEIGHT * SCALE) / 2) - 20);

      g2.setColor(Color.white);
      g2.setFont(new Font("arial", Font.PLAIN, 20));
      g2.drawString(
          "> Pressione |ENTER| para recomeçar <",
          ((WIDTH * SCALE) / 2) - 160,
          ((HEIGHT * SCALE) / 2) + 20);

    } else if (Objects.equals(state, "MENU")) {
      menu.render(g);
    }

    bs.show();
  }

  @Override
  public void run() {
    long lastTime = System.nanoTime();
    double amountOfTicks = 60.0;
    double ns = 1000000000 / amountOfTicks;
    double delta = 0;
    int frames = 0;
    double timer = System.currentTimeMillis();

    requestFocus();

    while (isRunning) {
      long now = System.nanoTime();
      delta += (now - lastTime) / ns;
      lastTime = now;

      if (delta >= 1) {
        tick();
        render();
        frames++;
        delta--;
      }

      if (System.currentTimeMillis() - timer >= 1000) {
        System.out.println("FPS: " + frames);
        frames = 0;
        timer += 1000;
      }
    }

    stop();
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
      player.right = true;
    } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
      player.left = true;
    }

    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
      player.up = true;

      if (Objects.equals(state, "MENU")) {
        menu.up = true;
      }
    } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
      player.down = true;

      if (Objects.equals(state, "MENU")) {
        menu.down = true;
      }
    }

    if (e.getKeyCode() == KeyEvent.VK_SPACE && player.hasWeapon && player.ammo > 0) {
      player.isShooting = true;
    }

    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      if (Objects.equals(state, "GAME_OVER")) {
        restartOnEnter = true;
      }
      if (Objects.equals(state, "MENU")) {
        menu.enter = true;
      }
    }

    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      if (Objects.equals(state, "GAME_OVER")) {
        restartOnEnter = true;
        Sound.gameOver.stop();
      }
      if (Objects.equals(state, "MENU")) {
        menu.enter = true;
      }
    }

    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
      if (Objects.equals(state, "DEFAULT")) {
        state = "MENU";
        menu.isPaused = true;
      }
    }

    if (e.getKeyCode() == KeyEvent.VK_F1) {
      if (Objects.equals(state, "DEFAULT")) {
        this.savingGame = true;
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
      player.right = false;
    } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
      player.left = false;
    }

    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
      player.up = false;
    } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
      player.down = false;
    }
  }
}
