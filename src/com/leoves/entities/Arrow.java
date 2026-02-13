package com.leoves.entities;

import com.leoves.main.Game;
import com.leoves.world.Camera;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Arrow extends Entity {

  private int dx;
  private int dy;
  private double speed = 2.5;
  private int finalDistance = 60, currentDistance = 0;

  public Arrow(int x, int y, int width, int height, BufferedImage sprite, int dx, int dy) {
    super(x, y, width, height, sprite);
    this.dx = dx;
    this.dy = dy;
  }

  public void tick() {
    x += dx * speed;
    y += dy * speed;
    currentDistance++;
    if (currentDistance == finalDistance) {
      Game.arrows.remove(this);
      return;
    }
  }

  public void render(Graphics g) {
    g.setColor(new Color(92, 73, 48));
    g.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, 6, 2);
  }
}
