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
    private final long normalShootInterval = 200;   // 普通射击间隔

    // 道具状态
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
            case 65: left = pressed; break;   // A
            case 87: up = pressed; break;     // W
            case 68: right = pressed; break;  // D
            case 83: down = pressed; break;   // S
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
        // 计算当前射击间隔（超级子弹时减半）
        long interval = superBulletActive ? normalShootInterval / 2 : normalShootInterval;
        if (gameTime - lastShootTime < interval) return;

        lastShootTime = gameTime;

        ElementObj bulletObj = GameLoad.getObj("bullet");
        if (bulletObj instanceof Bullet) {
            Bullet bullet = (Bullet) bulletObj;
            // 根据当前状态设置子弹类型（仅一种，不会同时发射两种）
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
        // 超级子弹时间检查
        if (superBulletActive && now > superBulletEndTime) {
            superBulletActive = false;
            // 强制重置射击计时，避免因高速期间刚发射过导致下一次发射等待过久
            lastShootTime = 0;
        }
        // 加速时间检查
        if (speedBoostActive && now > speedBoostEndTime) {
            speedBoostActive = false;
        }
        // 无敌时间检查
        if (invincibleActive && now > invincibleEndTime) {
            invincibleActive = false;
        }
    }

    // ---- 道具触发 ----
    public void addRevive() { revive++; }
    public void addDiamond(int count) { diamond += count; }

    public void activateSuperBullet(long durationMs) {
        superBulletActive = true;
        superBulletEndTime = System.currentTimeMillis() + durationMs;
        // 激活时重置上次射击时间，让玩家立即可以快速射击
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

    // ---- 倒计时显示 ----
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
