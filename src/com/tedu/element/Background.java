package com.tedu.element;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;

public class Background extends ElementObj {

    @Override
    public void showElement(Graphics g) {
        if (getIcon() != null) {
            // 将背景图拉伸到整个窗口大小，覆盖全部
            g.drawImage(getIcon().getImage(), 0, 0, GameJFrame.GameX, GameJFrame.GameY, null);
        }
    }

    @Override
    public ElementObj createElement(String str) {
        // 格式：x,y,key  例如 "0,0,background/1"
        String[] parts = str.split(",");
        if (parts.length == 3) {
            setX(Integer.parseInt(parts[0]));
            setY(Integer.parseInt(parts[1]));
            ImageIcon icon = GameLoad.imgMap.get(parts[2]);
            if (icon != null) {
                setIcon(icon);
                setW(icon.getIconWidth());
                setH(icon.getIconHeight());
            }
        }
        return this;
    }
}
