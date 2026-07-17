package com.tedu.element;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import com.tedu.manager.GameLoad;

public class Item extends ElementObj {

    private int itemType;
    private int moveSpeed;

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
            itemType = Integer.parseInt(parts[2]);
            ImageIcon icon = GameLoad.imgMap.get("prop/" + itemType);
            if (icon != null) {
                setIcon(icon);
                setW(icon.getIconWidth());
                setH(icon.getIconHeight());
            }
            moveSpeed = 2;
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

    public int getItemType() {
        return itemType;
    }

    /**
     * 拾取道具时的效果
     * @param player 玩家飞机对象
     */
    public void applyEffect(ElementObj player) {
        if (!(player instanceof PlayerPlane)) return;
        PlayerPlane plane = (PlayerPlane) player;

        switch (itemType) {
        	case 1: // 回复 2 点血量
        		plane.heal(2);
        		break;
        	case 2: // 回复 1 点血量
        		plane.heal(1);
        		break;
            case 3: // 增加一次复活机会
                plane.addRevive();   // 需要在PlayerPlane中添加方法
                break;
            case 4: // 钻石（局外购买），暂存数量
                plane.addDiamond(1);
                break;
            case 5: // 护盾，抵挡一次伤害
            	plane.activateInvincible(10000);
                break;
            case 6: // 超级子弹时间：10秒内子弹替换为114，射速加倍
                plane.activateSuperBullet(10000); // 10秒 = 10000毫秒
                break;
            case 7: // 移动速度提升 50%，持续15秒
                plane.activateSpeedBoost(15000);
                break;
        }
    }
}