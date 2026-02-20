package com.leoves.graphics;

import com.leoves.main.Game;
import java.awt.*;

public class UI {

  public void render(Graphics g) {
    int lifeBarX = 20;
    int lifeBarY = 20;
    int lifeBarWidth = 180;
    int lifeBarHeight = 25;

    g.setColor(Color.red);
    g.fillRect(lifeBarX, lifeBarY, lifeBarWidth, lifeBarHeight);

    int lifeBarGreenWidth = (int) ((Game.player.life / Game.player.maxLife) * lifeBarWidth);

    if (lifeBarGreenWidth > lifeBarWidth) {
      lifeBarGreenWidth = lifeBarWidth;
    }

    if (lifeBarGreenWidth < 0) {
      lifeBarGreenWidth = 0;
    }

    g.setColor(Color.green);
    g.fillRect(lifeBarX, lifeBarY, lifeBarGreenWidth, lifeBarHeight);

    int playerLife = (int) Game.player.life;

    if (playerLife < 0) {
      playerLife = 0;
    }

    g.setColor(Color.white);
    g.setFont(Game.fontMd.deriveFont(Font.PLAIN, 31));
    g.drawString(playerLife + " / " + (int) Game.player.maxLife, 26, 40);

    if (Game.player.ammo > 0) {
      if (Game.player.hasWeapon) g.setColor(Color.black);
      if (!Game.player.hasWeapon) g.setColor(Color.gray);

      g.fillRect(lifeBarX, lifeBarY + lifeBarHeight, 101, lifeBarHeight);

      g.setColor(Color.white);
      g.setFont(Game.fontMd.deriveFont(Font.PLAIN, 24));
      g.drawString("FLECHAS: " + Game.player.ammo, 26, 64);
    }
  }
}
