package com.tedu.show;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.tedu.element.ElementObj;
import com.tedu.element.PlayerPlane;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;

public class GameMainJPanel extends JPanel implements Runnable {

    private ElementManager em;

    public GameMainJPanel() {
        init();
    }

    public void init() {
        em = ElementManager.getManager();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Map<GameElement, List<ElementObj>> all = em.getGameElements();

        for (GameElement ge : GameElement.values()) {
            List<ElementObj> list = all.get(ge);
            for (int i = 0; i < list.size(); i++) {
                ElementObj obj = list.get(i);
                if (obj.isLive()) {
                    obj.showElement(g);
                }
            }
        }

        List<ElementObj> players = em.getElementsByKey(GameElement.PLAY);
        if (!players.isEmpty() && players.get(0) instanceof PlayerPlane) {
            PlayerPlane player = (PlayerPlane) players.get(0);
            g.setColor(Color.WHITE);
            g.setFont(new Font("微软雅黑", Font.BOLD, 14));

            int y = 30;
            g.drawString("HP: " + player.getHp(), 20, y); y += 20;
            g.drawString("钻石: " + player.getDiamond(), 20, y); y += 20;
            g.drawString("复活: " + player.getRevive(), 20, y);
            g.drawString("复活: " + player.getRevive(), 20, 70);

            g.drawString("复活: " + player.getRevive(), 20, 70);

            long speedRemaining = player.getSpeedBoostRemaining();
            long bulletRemaining = player.getSuperBulletRemaining();
            long invincibleRemaining = player.getInvincibleRemaining();

            if (speedRemaining > 0) {
                int seconds = (int)(speedRemaining / 1000) + 1;
                g.drawString("加速剩余: " + seconds + "s", 20, 100);
            }
            if (bulletRemaining > 0) {
                int seconds = (int)(bulletRemaining / 1000) + 1;
                g.drawString("超级子弹: " + seconds + "s", 20, 120);
            }
            if (invincibleRemaining > 0) {
                int seconds = (int)(invincibleRemaining / 1000) + 1;
                g.drawString("无敌剩余: " + seconds + "s", 20, 140);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            this.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}