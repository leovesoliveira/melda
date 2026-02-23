package com.leoves.entities;

import com.leoves.main.Game;
import com.leoves.main.Sound;
import com.leoves.world.Camera;
import com.leoves.world.Tile;
import com.leoves.world.WallTile;
import com.leoves.world.World;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Arrow extends Entity {

  private int dx;
  private int dy;
  private double speed = 2.5;
  private int finalDistance = 20, currentDistance = 0;

  public Arrow(int x, int y, int width, int height, BufferedImage sprite, int dx, int dy) {
    super(x, y, 6, 2, sprite);
    this.dx = dx;
    this.dy = dy;
  }

  public void tick() {
    x += dx * speed;
    y += dy * speed;
    currentDistance++;

    checkCollisionWithWall();

    if (currentDistance == finalDistance) {
      Game.arrows.remove(this);
      return;
    }
  }

  public void checkCollisionWithWall() {
    for (int i = 0; i < World.tiles.length; i++) {
      Tile tile = World.tiles[i];

      if (tile instanceof WallTile) {
        Rectangle wall = new Rectangle(tile.getX(), tile.getY(), World.TILE_SIZE, World.TILE_SIZE);
        Rectangle arrow =
            new Rectangle(
                this.getX() + this.maskX,
                this.getY() + this.maskY,
                this.maskWidth,
                this.maskHeight);

        boolean isColliding = wall.intersects(arrow);

        if (isColliding) {
          World.arrows.add(this);
          Game.arrows.remove(this);
          Sound.arrowWallEffect.play();
          return;
        }
      }
    }
  }

  public void render(Graphics g) {
    g.setColor(new Color(92, 73, 48));
    g.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, 6, 2);
  }
}
