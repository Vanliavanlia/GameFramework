package com.tedu.element;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;

public class Enemy extends ElementObj {

    private int enemyType;
    private int moveSpeed;
    private long lastShootTime = 0;
    private long shootInterval;
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
            setY(Integer.parseInt(parts[1]));
            enemyType = Integer.parseInt(parts[2]);
            ImageIcon icon = GameLoad.imgMap.get("enemy/" + enemyType);
            if (icon != null) {
                setIcon(icon);
                setW(icon.getIconWidth());
                setH(icon.getIconHeight());
            }
            hp = 2;
            attack = 1;
            moveSpeed = 2;
            // 射击间隔增大1.5倍：原3000+随机3000 -> 4500+随机4500
            shootInterval = 4500 + (int)(Math.random() * 4500);
            bulletType = (int)(Math.random() * 7) + 1;
        }
        return this;
    }

    @Override
    protected void move() {
        setY(getY() + moveSpeed);
        if (getY() > com.tedu.show.GameJFrame.GameY) {
            setLive(false);
        }
    }

    @Override
    protected void add(long gameTime) {
        if (gameTime - lastShootTime > shootInterval) {
            lastShootTime = gameTime;
            for (int i = 0; i < 4; i++) {
                ElementObj bullet = GameLoad.getObj("bullet");
                if (bullet != null) {
                    ((Bullet) bullet).setBulletType(bulletType);
                    int bx = getX() + getW() / 2 - bullet.getW() / 2 - 15 + i * 10;
                    int by = getY() + getH() - i * 5;
                    bullet.setX(bx);
                    bullet.setY(by);
                    ElementManager.getManager().addElement(bullet, GameElement.ENEMYFILE);
                }
            }
        }
    }

    @Override
    public void die() {
        if (Math.random() < 0.1) {
            int randomType = (int)(Math.random() * 7) + 1;
            GameLoad.spawnItem(getX(), getY(), randomType);
        }
        ElementObj explode = GameLoad.getObj("explode");
        if (explode != null) {
            explode = explode.createElement(getX() + "," + getY());
            ElementManager.getManager().addElement(explode, GameElement.EXPLODE);
        }
    }
}