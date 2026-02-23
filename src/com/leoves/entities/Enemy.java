package com.leoves.entities;

import com.leoves.main.Game;
import com.leoves.main.Sound;
import com.leoves.world.AStar;
import com.leoves.world.Camera;
import com.leoves.world.Vector2i;
import com.leoves.world.World;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy extends Entity {

  private double speed = 0.4;
  private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
  private BufferedImage[] sprites;
  private int life = 3;
  private boolean isDamaged = false;
  private int damageFrames = 0;
  private int randomDir = Game.rand.nextInt(5);
  private int framesRandomDir = 0;

  public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
    super(x, y, width, height, null);
    sprites = new BufferedImage[2];
    sprites[0] = Game.spritesheet.getSprite(7 * 16, 16, 16, 16);
    sprites[1] = Game.spritesheet.getSprite(8 * 16, 16, 16, 16);
    setMask(0, 0, 16, 16);
  }

  public void tick() {
    depth = 0;

    double distanceFromPlayer =
        this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY());

    if (distanceFromPlayer < 70) {
      if (!isCollidingWithPlayer()) {
        if (path == null || path.isEmpty() || Game.player.moved) {
          Vector2i start = new Vector2i((int) (x + 8) / 16, (int) (y + 8) / 16);
          Vector2i end =
              new Vector2i((int) (Game.player.x + 8) / 16, (int) (Game.player.y + 8) / 16);
          path = AStar.findPath(Game.world, start, end);
        }
      } else {
        if (Game.rand.nextInt(100) < 5) {
          Game.player.life -= Game.rand.nextInt(4) + 1;
          Game.player.isDamaged = true;
          Sound.hurtEffect.play();
        }
      }

    } else {
      framesRandomDir++;
      if (framesRandomDir == 40) {
        framesRandomDir = 0;
        randomDir = Game.rand.nextInt(5);
      }

      if (randomDir == 1 && World.isFree((int) (x + 1), this.getY())) {
        if (Game.rand.nextInt(100) < (speed * 100 / 2)) x++;
      } else if (randomDir == 2 && World.isFree((int) (x - 1), this.getY())) {
        if (Game.rand.nextInt(100) < (speed * 100 / 2)) x--;
      } else if (randomDir == 3 && World.isFree(this.getX(), (int) (y + 1))) {
        if (Game.rand.nextInt(100) < ((speed * 100) / 2)) y++;
      } else if (randomDir == 4 && World.isFree(this.getX(), (int) (y - 1))) {
        if (Game.rand.nextInt(100) < (speed * 100 / 2)) y--;
      }

      path = null;
    }

    if (Game.rand.nextInt(100) < (speed * 100)) followPath(path);

    frames++;
    if (frames == maxFrames) {
      frames = 0;
      index++;
      if (index > maxIndex) {
        index = 0;
      }
    }

    checkCollidingWithArrow();

    if (life <= 0) {
      destroySelf();
      return;
    }

    if (isDamaged) {
      this.damageFrames++;
      if (damageFrames == 8) {
        damageFrames = 0;
        isDamaged = false;
      }
    }
  }

  public void destroySelf() {
    Game.enemies.remove(this);
    Game.entities.remove(this);
  }

  public void checkCollidingWithArrow() {
    for (int i = 0; i < Game.arrows.size(); i++) {
      Entity e = Game.arrows.get(i);
      if (e instanceof Arrow) {
        if (Entity.isColliding(this, e)) {
          isDamaged = true;
          life--;
          Game.arrows.remove(i);
          Sound.damageEffect.play();
          return;
        }
      }
    }
  }

  public boolean isCollidingWithPlayer() {
    Rectangle currentEnemy =
        new Rectangle(this.getX() + maskX, this.getY() + maskY, maskWidth, maskHeight);
    Rectangle player =
        new Rectangle(
            Game.player.getX() + Game.player.maskX,
            Game.player.getY() + Game.player.maskY,
            Game.player.maskWidth,
            Game.player.maskHeight);

    return currentEnemy.intersects(player);
  }

  public void render(Graphics g) {
    if (!isDamaged) {
      g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
    } else {
      g.drawImage(Entity.ENEMY_DAMAGED_EN, this.getX() - Camera.x, this.getY() - Camera.y, null);
    }
  }
}
