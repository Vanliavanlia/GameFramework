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
    private long shootInterval = 1200;      // 攻击性提高：射击间隔减半（原1600）
    private int direction = 1;
    private int bulletType;                // 固定子弹类型（1~7）

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(), getX(), getY(), getW(), getH(), null);
    }

    @Override
    public ElementObj createElement(String str) {
        // 格式：x,y,bossType  （y会被强制覆盖为顶部固定位置）
        String[] parts = str.split(",");
        if (parts.length == 3) {
            setX(Integer.parseInt(parts[0]));
            // 忽略传入的y，强制设置为固定Y位置（例如距离顶部50像素）
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
            moveSpeed = 2;                 // 增加移动速度
            bulletType = (int)(Math.random() * 7) + 1;
        }
        return this;
    }

    @Override
    protected void move() {
        // 只在屏幕上方左右移动，Y固定不变
        setX(getX() + direction * moveSpeed);
        if (getX() <= 0 || getX() + getW() >= GameJFrame.GameX) {
            direction *= -1;
        }
        // 不再向下移动
    }

    @Override
    protected void add(long gameTime) {
        if (gameTime - lastShootTime > shootInterval) {
            lastShootTime = gameTime;
            // 发射6颗子弹，提高攻击性，扇形散开
            for (int i = 0; i < 6; i++) {
                ElementObj bullet = GameLoad.getObj("bullet");
                if (bullet != null) {
                    ((Bullet) bullet).setBulletType(bulletType);
                    // 水平偏移：以中心为基准左右各30像素，间隔12像素
                    int bx = (int) (getX() + getW() / 2 - bullet.getW() / 2 + (i - 2.5) * 12);
                    int by = getY() + getH(); // 从Boss底部发出
                    bullet.setX(bx);
                    bullet.setY(by);
                    ElementManager.getManager().addElement(bullet, GameElement.ENEMYFILE);
                }
            }
        }
    }
}
