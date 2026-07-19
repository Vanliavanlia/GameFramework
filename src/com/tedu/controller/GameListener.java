package com.tedu.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tedu.element.ElementObj;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;

public class GameListener implements KeyListener {

    private ElementManager em = ElementManager.getManager();
    private Set<Integer> keys = new HashSet<Integer>();

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (keys.contains(key)) return;
        keys.add(key);

        List<ElementObj> players = em.getElementsByKey(GameElement.PLAY);
        for (ElementObj player : players) {
            player.keyClick(true, key);
        }
    }

    @Override//
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (!keys.contains(key)) return;
        keys.remove(key);

        List<ElementObj> players = em.getElementsByKey(GameElement.PLAY);
        for (ElementObj player : players) {
            player.keyClick(false, key);
        }
    }
}
