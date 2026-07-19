package com.tedu.element;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;

public class PlayerPlane extends ElementObj {

    private int planeType;
    private boolean up, down, left, right;
    private long lastShootTime = 0;
    private final long normalShootInterval = 200;

    private int revive = 0;
    private int diamond = 0;
    private boolean superBulletActive = false;
    private long superBulletEndTime = 0;
    private boolean speedBoostActive = false;
    private long speedBoostEndTime = 0;
    private boolean invincibleActive = false;
    private long invincibleEndTime = 0;

    private static final int MAX_HP = 10;

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
            planeType = Integer.parseInt(parts[2]);
            ImageIcon icon = GameLoad.imgMap.get("play/" + planeType);
            if (icon != null) {
                setIcon(icon);
                setW(icon.getIconWidth());
                setH(icon.getIconHeight());
            }
            hp = MAX_HP;
            attack = 1;
            speed = 7;
        }
        return this;
    }

    @Override
    public void keyClick(boolean pressed, int keyCode) {
        switch (keyCode) {
            case 65: left = pressed; break;
            case 87: up = pressed; break;
            case 68: right = pressed; break;
            case 83: down = pressed; break;
        }
    }

    @Override
    protected void move() {
        float currentMult = speedBoostActive ? 1.5f : 1.0f;
        int dx = 0, dy = 0;
        if (left) dx -= speed * currentMult;
        if (right) dx += speed * currentMult;
        if (up) dy -= speed * currentMult;
        if (down) dy += speed * currentMult;
        if (dx != 0 || dy != 0) {
            int newX = getX() + (int)dx;
            int newY = getY() + (int)dy;
            if (newX < 0) newX = 0;
            if (newY < 0) newY = 0;
            if (newX + getW() > GameJFrame.GameX) newX = GameJFrame.GameX - getW();
            if (newY + getH() > GameJFrame.GameY) newY = GameJFrame.GameY - getH();
            setX(newX);
            setY(newY);
        }
    }

    @Override
    protected void add(long gameTime) {
        shoot(gameTime);
    }

    public void shoot(long gameTime) {
        long interval = superBulletActive ? normalShootInterval / 2 : normalShootInterval;
        if (gameTime - lastShootTime < interval) return;

        lastShootTime = gameTime;

        ElementObj bulletObj = GameLoad.getObj("bullet");
        if (bulletObj instanceof Bullet) {
            Bullet bullet = (Bullet) bulletObj;
            if (superBulletActive) {
                bullet.setBulletType(114);
            } else {
                bullet.setBulletType(1);
            }
            int bx = getX() + getW() / 2 - bullet.getW() / 2;
            int by = getY() - bullet.getH();
            bullet.setX(bx);
            bullet.setY(by);
            ElementManager.getManager().addElement(bullet, GameElement.PLAYFILE);
        }
    }

    @Override
    public void onHit(int damage) {
        if (invincibleActive) return;
        super.onHit(damage);
        if (!isLive() && revive > 0) {
            revive--;
            setLive(true);
            setHp(MAX_HP);
        }
    }

    public void heal(int amount) {
        hp = Math.min(hp + amount, MAX_HP);
    }

    @Override
    protected void updateImage(long time) {
        long now = System.currentTimeMillis();
        if (superBulletActive && now > superBulletEndTime) {
            superBulletActive = false;
            lastShootTime = 0;
        }
        if (speedBoostActive && now > speedBoostEndTime) {
            speedBoostActive = false;
        }
        if (invincibleActive && now > invincibleEndTime) {
            invincibleActive = false;
        }
    }

    public void addRevive() { revive++; }
    public void addDiamond(int count) { diamond += count; }

    public void activateSuperBullet(long durationMs) {
        superBulletActive = true;
        superBulletEndTime = System.currentTimeMillis() + durationMs;
        lastShootTime = 0;
    }

    public void activateSpeedBoost(long durationMs) {
        speedBoostActive = true;
        speedBoostEndTime = System.currentTimeMillis() + durationMs;
    }

    public void activateInvincible(long durationMs) {
        invincibleActive = true;
        invincibleEndTime = System.currentTimeMillis() + durationMs;
    }

    public long getSuperBulletRemaining() {
        if (!superBulletActive) return 0;
        long remaining = superBulletEndTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    public long getSpeedBoostRemaining() {
        if (!speedBoostActive) return 0;
        long remaining = speedBoostEndTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    public long getInvincibleRemaining() {
        if (!invincibleActive) return 0;
        long remaining = invincibleEndTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    public int getDiamond() { return diamond; }
    public int getRevive() { return revive; }
}
