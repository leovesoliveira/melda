package com.leoves.entities;

import java.awt.image.BufferedImage;

public class Potion extends Entity {
  public Potion(int x, int y, int width, int height, BufferedImage sprite) {
    super(x, y, width, height, sprite);
    depth = 1;
  }
}
