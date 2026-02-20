package com.leoves.entities;

import com.leoves.main.Game;
import com.leoves.world.Camera;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {

  public static BufferedImage POTION_EN = Game.spritesheet.getSprite(6 * 16, 0, 16, 16);
  public static BufferedImage WEAPON_FLOOR_EN = Game.spritesheet.getSprite(7 * 16, 0, 16, 16);
  public static BufferedImage WEAPON_RIGHT_EN = Game.spritesheet.getSprite(8 * 16, 0, 16, 16);
  public static BufferedImage WEAPON_LEFT_EN = Game.spritesheet.getSprite(9 * 16, 0, 16, 16);
  public static BufferedImage AMMO_EN = Game.spritesheet.getSprite(6 * 16, 16, 16, 16);
  public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7 * 16, 16, 16, 16);
  public static BufferedImage ENEMY_DAMAGED_EN = Game.spritesheet.getSprite(9 * 16, 16, 16, 16);

  protected double x;
  protected double y;
  protected int width;
  protected int height;
  protected BufferedImage sprite;
  protected int maskX, maskY, maskWidth, maskHeight;

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
