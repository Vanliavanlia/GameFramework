package com.tedu.element;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;

public class Boss extends ElementObj {

    private int bossType;
    private int moveSpeed;
    private long lastShootTime = 0;
    private long shootInterval = 1200;
    private int direction = 1;
    private int bulletType;

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(), getX(), getY(), getW(), getH(), null);
    }

    @Override
    public ElementObj createElement(String str) {
        String[] parts = str.split(",");
        if (parts.length == 3) {
            setX(Integer.parseInt(parts[0]));
            setY(50);
            bossType = Integer.parseInt(parts[2]);
            ImageIcon icon = GameLoad.imgMap.get("boss/" + bossType);
            if (icon != null) {
                setIcon(icon);
                setW(icon.getIconWidth());
                setH(icon.getIconHeight());
            }
            hp = 30;
            attack = 2;
            moveSpeed = 2;
            bulletType = (int)(Math.random() * 7) + 1;
        }
        return this;
    }

    @Override
    protected void move() {
        setX(getX() + direction * moveSpeed);
        if (getX() <= 0 || getX() + getW() >= GameJFrame.GameX) {
            direction *= -1;
        }
    }

    @Override
    protected void add(long gameTime) {
        if (gameTime - lastShootTime > shootInterval) {
            lastShootTime = gameTime;
            for (int i = 0; i < 6; i++) {
                ElementObj bullet = GameLoad.getObj("bullet");
                if (bullet != null) {
                    ((Bullet) bullet).setBulletType(bulletType);
                    int bx = (int) (getX() + getW() / 2 - bullet.getW() / 2 + (i - 2.5) * 12);
                    int by = getY() + getH();
                    bullet.setX(bx);
                    bullet.setY(by);
                    ElementManager.getManager().addElement(bullet, GameElement.ENEMYFILE);
                }
            }
        }
    }
}
