package com.tedu.element;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import com.tedu.manager.GameLoad;

public class Explosion extends ElementObj {

    private int frameIndex = 0;
    private int totalFrames = 8;
    private int frameWidth;
    private int frameHeight;
    private long lastFrameTime = 0;
    private long frameInterval = 100; // ms

    @Override
    public void showElement(Graphics g) {
        if (getIcon() == null) return;
        int sx = frameIndex * frameWidth;
        int sy = 0;
        g.drawImage(getIcon().getImage(),
                getX(), getY(), getX() + getW(), getY() + getH(),
                sx, sy, sx + frameWidth, sy + frameHeight,
                null);
    }

    @Override
    public ElementObj createElement(String str) {
        // 格式：x,y
        String[] parts = str.split(",");
        if (parts.length >= 2) {
            setX(Integer.parseInt(parts[0]));
            setY(Integer.parseInt(parts[1]));
        }
        ImageIcon fullIcon = GameLoad.imgMap.get("bang");
        if (fullIcon != null) {
            setIcon(fullIcon);
            frameWidth = fullIcon.getIconWidth() / totalFrames;
            frameHeight = fullIcon.getIconHeight();
            setW(frameWidth);
            setH(frameHeight);
        }
        return this;
    }

    @Override
    protected void updateImage(long time) {
        if (time - lastFrameTime > frameInterval) {
            lastFrameTime = time;
            frameIndex++;
            if (frameIndex >= totalFrames) {
                setLive(false);
            }
        }
    }
}