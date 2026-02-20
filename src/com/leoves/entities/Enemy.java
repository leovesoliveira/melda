package com.leoves.entities;

import com.leoves.main.Game;
import com.leoves.main.Sound;
import com.leoves.world.Camera;
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

  public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
    super(x, y, width, height, null);
    sprites = new BufferedImage[2];
    sprites[0] = Game.spritesheet.getSprite(7 * 16, 16, 16, 16);
    sprites[1] = Game.spritesheet.getSprite(8 * 16, 16, 16, 16);
    setMask(3, 5, 10, 9);
  }

  public void tick() {
    double distanceFromPlayer =
        this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY());

    if (distanceFromPlayer < 70) {
      if (isCollidingWithPlayer() == false) {
        if ((int) x < Game.player.getX()
            && World.isFree((int) (x + speed), this.getY())
            && !isColliding((int) (x + speed), this.getY())) {
          x += speed;
        } else if ((int) x > Game.player.getX()
            && World.isFree((int) (x - speed), this.getY())
            && !isColliding((int) (x - speed), this.getY())) {
          x -= speed;
        }

        if ((int) y < Game.player.getY()
            && World.isFree(this.getX(), (int) (y + speed))
            && !isColliding(this.getX(), (int) (y + speed))) {
          y += speed;
        } else if ((int) y > Game.player.getY()
            && World.isFree(this.getX(), (int) (y - speed))
            && !isColliding(this.getX(), (int) (y - speed))) {
          y -= speed;
        }
      } else {
        if (Game.rand.nextInt(100) < 10) {
          Game.player.life -= Game.rand.nextInt(4) + 1;
          Game.player.isDamaged = true;
          Sound.hurtEffect.play();
        }
      }
    }

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

  public boolean isColliding(int nextX, int nextY) {
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

  public void render(Graphics g) {
    if (!isDamaged) {
      g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
    } else {
      g.drawImage(Entity.ENEMY_DAMAGED_EN, this.getX() - Camera.x, this.getY() - Camera.y, null);
    }
  }
}
