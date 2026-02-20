package com.leoves.entities;

import com.leoves.main.Game;
import com.leoves.main.Sound;
import com.leoves.world.Camera;
import com.leoves.world.World;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

  public double life = 50, maxLife = 50;
  public boolean right, up, left, down;
  public double speed = 1.2;
  public int rightDir = 0, leftDir = 1;
  public int dir = rightDir;
  public int ammo = 0;
  public boolean isDamaged = false;
  public boolean hasWeapon = false;
  public boolean isShooting = false;
  private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
  private boolean moved = false;
  private BufferedImage[] playerRight;
  private BufferedImage[] playerLeft;
  private BufferedImage[] playerDamaged;
  private int damageFrames = 0;
  private int shootFrames = 0;
  private int maxShootFrames = 30;

  public Player(int x, int y, int width, int height, BufferedImage sprite) {
    super(x, y, width, height, sprite);

    playerRight = new BufferedImage[4];
    for (int i = 0; i < 4; i++) {
      playerRight[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
    }

    playerLeft = new BufferedImage[4];
    for (int i = 0; i < 4; i++) {
      playerLeft[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
    }

    playerDamaged = new BufferedImage[2];
    playerDamaged[0] = Game.spritesheet.getSprite(0, 16, 16, 16);
    playerDamaged[1] = Game.spritesheet.getSprite(16, 16, 16, 16);

    setMask(0, 0, 19, 16);
  }

  public void tick() {

    moved = false;
    shootFrames++;

    if (right && World.isFree((int) (x + speed), this.getY())) {
      moved = true;
      dir = rightDir;
      x += speed;
    } else if (left && World.isFree((int) (x - speed), this.getY())) {
      moved = true;
      dir = leftDir;
      x -= speed;
    }

    if (up && World.isFree(this.getX(), (int) (y - speed))) {
      moved = true;
      y -= speed;
    } else if (down && World.isFree(this.getX(), (int) (y + speed))) {
      moved = true;
      y += speed;
    }

    if (moved) {
      frames++;
      if (frames == maxFrames) {
        frames = 0;
        index++;
        if (index > maxIndex) {
          index = 0;
        }
      }
    }

    checkCollisionWithAmmo();
    checkCollisionWithPotion();
    checkCollisionWithWeapon();
    checkCollisionWithArrowWall();

    if (isDamaged) {
      this.damageFrames++;
      if (damageFrames == 8) {
        damageFrames = 0;
        isDamaged = false;
      }
    }

    if (isShooting && hasWeapon && ammo > 0 && shootFrames > maxShootFrames) {
      shootFrames = 0;
      ammo--;
      isShooting = false;
      int dx = 0;
      int px = 0;
      int py = 9;
      if (dir == rightDir) {
        px = 12;
        dx = 1;
      }

      if (dir == leftDir) {
        px = -2;
        dx = -1;
      }

      Arrow arrow = new Arrow(this.getX() + px, this.getY() + py, width, height, null, dx, 0);
      Game.arrows.add(arrow);
      Sound.arrowEffect.play();
    }

    if (life <= 0) {
      Game.state = "GAME_OVER";
      Sound.gameOver.loop();
    }

    updateCamera();
  }

  public void updateCamera() {
    int currentX = this.getX() - (Game.WIDTH / 2) + 8;
    int minX = 0;
    int maxX = World.WIDTH * 16 - Game.WIDTH;
    Camera.x = Camera.clamp(currentX, minX, maxX);

    int currentY = this.getY() - (Game.HEIGHT / 2) + 8;
    int minY = 0;
    int maxY = World.HEIGHT * 16 - Game.HEIGHT;
    Camera.y = Camera.clamp(currentY, minY, maxY);
  }

  public void checkCollisionWithArrowWall() {
    for (int i = 0; i < World.arrows.size(); i++) {
      Arrow arrow = World.arrows.get(i);

      if (Entity.isColliding(this, arrow)) {
        ammo += 1;
        World.arrows.remove(arrow);
        Sound.itemEffect.play();
      }
    }
  }

  public void checkCollisionWithWeapon() {
    for (int i = 0; i < Game.entities.size(); i++) {
      Entity entity = Game.entities.get(i);

      if (!(entity instanceof Weapon)) continue;

      if (Entity.isColliding(this, entity)) {
        hasWeapon = true;
        Game.entities.remove(entity);
        Sound.itemEffect.play();
      }
    }
  }

  public void checkCollisionWithAmmo() {
    for (int i = 0; i < Game.entities.size(); i++) {
      Entity entity = Game.entities.get(i);

      if (!(entity instanceof Ammo)) continue;

      if (Entity.isColliding(this, entity)) {
        ammo += 10;
        Game.entities.remove(entity);
        Sound.itemEffect.play();
      }
    }
  }

  public void checkCollisionWithPotion() {
    if (Game.player.life == Game.player.maxLife) return;

    for (int i = 0; i < Game.entities.size(); i++) {
      Entity entity = Game.entities.get(i);

      if (!(entity instanceof Potion)) continue;

      if (Entity.isColliding(this, entity)) {
        life += 10;
        Game.entities.remove(entity);
        Sound.itemEffect.play();
      }

      if (life > maxLife) life = maxLife;
    }
  }

  public void render(Graphics g) {
    if (!isDamaged) {
      if (dir == rightDir) {
        g.drawImage(playerRight[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
        if (hasWeapon) {
          g.drawImage(Entity.WEAPON_RIGHT_EN, this.getX() - Camera.x, this.getY() - Camera.y, null);
        }
      } else if (dir == leftDir) {
        g.drawImage(playerLeft[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
        if (hasWeapon) {
          g.drawImage(Entity.WEAPON_LEFT_EN, this.getX() - Camera.x, this.getY() - Camera.y, null);
        }
      }
    } else {
      if (dir == rightDir) {
        g.drawImage(playerDamaged[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
        if (hasWeapon) {
          g.drawImage(Entity.WEAPON_RIGHT_EN, this.getX() - Camera.x, this.getY() - Camera.y, null);
        }
      } else if (dir == leftDir) {
        g.drawImage(playerDamaged[1], this.getX() - Camera.x, this.getY() - Camera.y, null);
        if (hasWeapon) {
          g.drawImage(Entity.WEAPON_LEFT_EN, this.getX() - Camera.x, this.getY() - Camera.y, null);
        }
      }
    }
  }
}
