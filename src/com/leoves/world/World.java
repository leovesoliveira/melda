package com.leoves.world;

import com.leoves.entities.*;
import com.leoves.graphics.Spritesheet;
import com.leoves.main.Game;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class World {
  public static final int TILE_SIZE = 16;
  public static int WIDTH, HEIGHT;
  public static int FLOOR_COLOR = 0xFF000000;
  public static int WALL_COLOR = 0xFFFFFFFF;
  public static int WEAPON_COLOR = 0xFFA34E00;
  public static int AMMO_COLOR = 0xFF3600A3;
  public static int POTION_COLOR = 0xFF41A300;
  public static int PLAYER_COLOR = 0xFF8E00A3;
  public static int ENEMY_COLOR = 0xFFA30000;
  public static Tile[] tiles;

  public World(String path) {
    try {
      BufferedImage map = ImageIO.read(getClass().getResource(path));
      WIDTH = map.getWidth();
      HEIGHT = map.getHeight();
      int[] pixels = new int[map.getWidth() * map.getHeight()];
      tiles = new Tile[map.getWidth() * map.getHeight()];
      map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
      for (int xx = 0; xx < map.getWidth(); xx++) {
        for (int yy = 0; yy < map.getHeight(); yy++) {
          int currentPixel = pixels[xx + (yy * map.getWidth())];
          tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);

          if (currentPixel == FLOOR_COLOR) {
            tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
          } else if (currentPixel == WALL_COLOR) {
            tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL);
          } else if (currentPixel == PLAYER_COLOR) {
            Game.player.setX(xx * 16);
            Game.player.setY(yy * 16);
          } else if (currentPixel == ENEMY_COLOR) {
            Enemy enemy = new Enemy(xx * 16, yy * 16, 16, 16, Entity.ENEMY_EN);
            Game.entities.add(enemy);
            Game.enemies.add(enemy);
          } else if (currentPixel == WEAPON_COLOR) {
            Game.entities.add(new Weapon(xx * 16, yy * 16, 16, 16, Entity.WEAPON_FLOOR_EN));
          } else if (currentPixel == POTION_COLOR) {
            Potion potion = new Potion(xx * 16, yy * 16, 16, 16, Entity.POTION_EN);
            potion.setMask(4, 6, 8, 9);
            Game.entities.add(potion);
          } else if (currentPixel == AMMO_COLOR) {
            Game.entities.add(new Ammo(xx * 16, yy * 16, 16, 16, Entity.AMMO_EN));
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean isFree(int nextX, int nextY) {
    int x1 = nextX / TILE_SIZE;
    int y1 = nextY / TILE_SIZE;
    boolean isWall1 = tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile;

    int x2 = (nextX + TILE_SIZE - 1) / TILE_SIZE;
    int y2 = nextY / TILE_SIZE;
    boolean isWall2 = tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile;

    int x3 = nextX / TILE_SIZE;
    int y3 = (nextY + TILE_SIZE - 1) / TILE_SIZE;
    boolean isWall3 = tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile;

    int x4 = (nextX + TILE_SIZE - 1) / TILE_SIZE;
    int y4 = (nextY + TILE_SIZE - 1) / TILE_SIZE;
    boolean isWall4 = tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile;

    return !(isWall1 || isWall2 || isWall3 || isWall4);
  }

  public static void restartGame(String level) {
    Game.entities.clear();
    Game.enemies.clear();

    Game.entities = new ArrayList<>();
    Game.enemies = new ArrayList<>();
    Game.spritesheet = new Spritesheet("/spritesheet.png");

    Game.player = new Player(0, 0, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
    Game.entities.add(Game.player);

    Game.world = new World("/" + level);
  }

  public void render(Graphics g) {
    int xstart = Camera.x >> 4;
    int ystart = Camera.y >> 4;
    int xfinal = xstart + (Game.WIDTH >> 4);
    int yfinal = ystart + (Game.HEIGHT >> 4);

    for (int xx = xstart; xx <= xfinal; xx++) {
      for (int yy = ystart; yy <= yfinal; yy++) {
        if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) continue;
        Tile tile = tiles[xx + (yy * WIDTH)];
        tile.render(g);
      }
    }
  }
}
