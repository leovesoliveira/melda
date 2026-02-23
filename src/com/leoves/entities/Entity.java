package com.leoves.entities;

import com.leoves.main.Game;
import com.leoves.world.Camera;
import com.leoves.world.Node;
import com.leoves.world.Vector2i;
import com.leoves.world.World;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

public class Entity {

  public static BufferedImage POTION_EN = Game.spritesheet.getSprite(6 * 16, 0, 16, 16);
  public static BufferedImage WEAPON_FLOOR_EN = Game.spritesheet.getSprite(7 * 16, 0, 16, 16);
  public static BufferedImage WEAPON_RIGHT_EN = Game.spritesheet.getSprite(8 * 16, 0, 16, 16);
  public static BufferedImage WEAPON_LEFT_EN = Game.spritesheet.getSprite(9 * 16, 0, 16, 16);
  public static BufferedImage AMMO_EN = Game.spritesheet.getSprite(6 * 16, 16, 16, 16);
  public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7 * 16, 16, 16, 16);
  public static BufferedImage ENEMY_DAMAGED_EN = Game.spritesheet.getSprite(9 * 16, 16, 16, 16);
  public static Comparator<Entity> depthSorter =
      new Comparator<Entity>() {
        @Override
        public int compare(Entity n0, Entity n1) {
          if (n1.depth < n0.depth) return +1;
          if (n1.depth > n0.depth) return -1;
          return 0;
        }
      };
  public int depth;
  protected double x;
  protected double y;
  protected int width;
  protected int height;
  protected BufferedImage sprite;
  protected int maskX, maskY, maskWidth, maskHeight;
  protected List<Node> path;

  public Entity(int x, int y, int width, int height, BufferedImage sprite) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.sprite = sprite;

    this.maskX = 0;
    this.maskY = 0;
    this.maskWidth = width;
    this.maskHeight = height;
  }

  public static boolean isColliding(Entity e1, Entity e2) {
    Rectangle e1Mask =
        new Rectangle(e1.getX() + e1.maskX, e1.getY() + e1.maskY, e1.maskWidth, e1.maskHeight);
    Rectangle e2Mask =
        new Rectangle(e2.getX() + e2.maskX, e2.getY() + e2.maskY, e2.maskWidth, e2.maskHeight);

    return e1Mask.intersects(e2Mask);
  }

  public boolean isCollidingByXY(int nextX, int nextY) {
    Rectangle currentEnemy = new Rectangle(nextX + maskX, nextY + maskY, maskWidth, maskHeight);

    for (int i = 0; i < Game.enemies.size(); i++) {
      Enemy enemy = Game.enemies.get(i);

      if (enemy == this) continue;

      Rectangle targetEnemy =
          new Rectangle(enemy.getX() + maskX, enemy.getY() + maskY, maskWidth, maskHeight);

      if (currentEnemy.intersects(targetEnemy)) {
        return true;
      }
    }

    return false;
  }

  public void followPath(List<Node> path) {
    if (path != null) {
      if (path.size() > 0) {
        Vector2i target = path.get(path.size() - 1).tile;

        if (x < target.x * 16 && World.isFree((int) (x + 1), this.getY())) {
          x++;
        } else if (x > target.x * 16 && World.isFree((int) (x - 1), this.getY())) {
          x--;
        } else if (y < target.y * 16 && World.isFree(this.getX(), (int) (y + 1))) {
          y++;
        } else if (y > target.y * 16 && World.isFree(this.getX(), (int) (y - 1))) {
          y--;
        }

        if (x == target.x * 16 && y == target.y * 16) {
          path.remove(path.size() - 1);
        }
      }
    }
  }

  public void setMask(int x, int y, int width, int height) {
    this.maskX = x;
    this.maskY = y;
    this.maskWidth = width;
    this.maskHeight = height;
  }

  public int getX() {
    return (int) x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public int getY() {
    return (int) y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public BufferedImage getSprite() {
    return sprite;
  }

  public void tick() {}

  public double calculateDistance(int x1, int y1, int x2, int y2) {
    return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
  }

  public void render(Graphics g) {
    g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
  }
}
