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

    public void applyEffect(ElementObj player) {
        if (!(player instanceof PlayerPlane)) return;
        PlayerPlane plane = (PlayerPlane) player;

        switch (itemType) {
        	case 1:
        		plane.heal(2);
        		break;
        	case 2:
        		plane.heal(1);
        		break;
            case 3:
                plane.addRevive();
                break;
            case 4:
                plane.addDiamond(1);
                break;
            case 5:
            	plane.activateInvincible(10000);
                break;
            case 6:
                plane.activateSuperBullet(10000);
                break;
            case 7:
                plane.activateSpeedBoost(15000);
                break;
        }
    }
}