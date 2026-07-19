package com.tedu.element;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import com.tedu.manager.GameLoad;

public class Bullet extends ElementObj {

    private int dir;
    private int moveSpeed;
    private Color backupColor;

    @Override
    public void showElement(Graphics g) {
        ImageIcon icon = getIcon();
        if (icon != null) {
            g.drawImage(icon.getImage(), getX(), getY(), getW(), getH(), null);
        } else {
            g.setColor(backupColor != null ? backupColor : Color.YELLOW);
            g.fillRect(getX(), getY(), getW() > 0 ? getW() : 6, getH() > 0 ? getH() : 12);
        }
    }

    public void setBulletType(int type) {
        String key = null;
        if (type == 1) {
            key = "fire/101";
            dir = -1;
            moveSpeed = 8;
            attack = 1;
            backupColor = Color.GREEN;
        } else if (type >= 1 && type <= 11) {
            key = "fire/" + type;
            dir = 1;
            moveSpeed = 4;
            attack = 1;
            backupColor = Color.RED;
        } else if (type == 114) {
            key = "fire/114";
            dir = -1;
            moveSpeed = 10;
            attack = 2;
            backupColor = Color.CYAN;
        }

        ImageIcon icon = GameLoad.imgMap.get(key);
        if (icon != null) {
            setIcon(icon);
            setW(icon.getIconWidth());
            setH(icon.getIconHeight());
        } else {
            System.out.println("子弹图片未找到：" + key + "，使用备选矩形");
            setW(6);
            setH(12);
        }
    }

    @Override
    protected void move() {
        setY(getY() + dir * moveSpeed);
        if (getY() < -getH() || getY() > com.tedu.show.GameJFrame.GameY + getH()) {
            setLive(false);
        }
    }
}
