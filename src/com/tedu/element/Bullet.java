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

    /**
     * 设置子弹类型
     * @param type  1 = 玩家子弹（固定101）
     *              2 = 保留（现在较少直接使用，但可由 Enemy 传入随机1~11）
     *              114 = 超级子弹
     *              其他1~11 = 敌人子弹（随机）
     */
    public void setBulletType(int type) {
        String key = null;
        if (type == 1) {               // 普通玩家子弹
            key = "fire/101";
            dir = -1;
            moveSpeed = 8;
            attack = 1;
            backupColor = Color.GREEN;
        } else if (type >= 1 && type <= 11) { // 敌人子弹（1~11 随机）
            key = "fire/" + type;
            dir = 1;                   // 向下
            moveSpeed = 4;
            attack = 1;
            backupColor = Color.RED;
        } else if (type == 114) {      // 超级子弹
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
