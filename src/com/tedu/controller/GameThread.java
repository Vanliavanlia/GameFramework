package com.tedu.controller;

import java.util.List;
import java.util.Map;

import com.tedu.element.ElementObj;
import com.tedu.element.Item;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;

public class GameThread extends Thread {

    private ElementManager em;
    private int level = 1;
    private long gameTime = 0;
    private long startTime = 0;
    private long enemyLastSpawn = 0;
    private long enemySpawnInterval = 1500;
    private boolean bossSpawned = false;
    private boolean running = true;
    private int killCount = 0;

    public GameThread() {
        em = ElementManager.getManager();
    }

    @Override
    public void run() {
        GameLoad.loadImages();
        GameLoad.loadObj();

        while (running) {
            gameLoad();
            startTime = System.currentTimeMillis();
            gameTime = startTime;
            enemyLastSpawn = startTime;
            bossSpawned = false;
            killCount = 0;

            boolean levelRunning = true;
            while (levelRunning && running) {
                gameTime = System.currentTimeMillis();
                levelRunning = gameRun();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        gameOver();
    }

    private void gameLoad() {
        GameLoad.loadBackground(level);
        GameLoad.loadPlayer(1);
    }

    private boolean gameRun() {
        Map<GameElement, List<ElementObj>> all = em.getGameElements();

        for (GameElement ge : GameElement.values()) {
            List<ElementObj> list = all.get(ge);
            for (int i = list.size() - 1; i >= 0; i--) {
                ElementObj obj = list.get(i);
                if (!obj.isLive()) {
                    obj.die();
                    list.remove(i);
                    continue;
                }
                obj.model(gameTime);
            }
        }

        List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
        List<ElementObj> enemys = em.getElementsByKey(GameElement.ENEMY);
        List<ElementObj> bosses = em.getElementsByKey(GameElement.BOSS);
        List<ElementObj> playBullets = em.getElementsByKey(GameElement.PLAYFILE);
        List<ElementObj> enemyBullets = em.getElementsByKey(GameElement.ENEMYFILE);
        List<ElementObj> items = em.getElementsByKey(GameElement.ITEM);

        for (ElementObj bullet : playBullets) {
            for (ElementObj enemy : enemys) {
                if (bullet.isLive() && enemy.isLive() && bullet.pk(enemy)) {
                    int beforeHp = enemy.getHp();
                    enemy.onHit(bullet.getAttack());
                    bullet.setLive(false);
                    if (beforeHp > 0 && !enemy.isLive()) {
                        killCount++;
                    }
                }
            }
            for (ElementObj boss : bosses) {
                if (bullet.isLive() && boss.isLive() && bullet.pk(boss)) {
                    boss.onHit(bullet.getAttack());
                    bullet.setLive(false);
                }
            }
        }

        for (ElementObj bullet : enemyBullets) {
            for (ElementObj player : plays) {
                if (bullet.isLive() && player.isLive() && bullet.pk(player)) {
                    player.onHit(bullet.getAttack());
                    bullet.setLive(false);
                }
            }
        }

        for (ElementObj enemy : enemys) {
            for (ElementObj player : plays) {
                if (enemy.isLive() && player.isLive() && enemy.pk(player)) {
                    player.onHit(enemy.getAttack());
                    enemy.setLive(false);
                    killCount++;
                }
            }
        }
        for (ElementObj boss : bosses) {
            for (ElementObj player : plays) {
                if (boss.isLive() && player.isLive() && boss.pk(player)) {
                    player.onHit(boss.getAttack());
                    boss.setLive(false);
                }
            }
        }

        for (ElementObj item : items) {
            for (ElementObj player : plays) {
                if (item.isLive() && player.isLive() && item.pk(player)) {
                    if (item instanceof Item) {
                        ((Item) item).applyEffect(player);
                    }
                    item.setLive(false);
                }
            }
        }

        if (gameTime - enemyLastSpawn > enemySpawnInterval) {
            enemyLastSpawn = gameTime;
            int randomType = (int)(Math.random() * 12) + 1;
            GameLoad.spawnEnemy(randomType);
        }

        if (!bossSpawned && killCount >= 20) {
            bossSpawned = true;
            GameLoad.spawnBoss(1);
        }

        boolean playerAlive = false;
        for (ElementObj player : plays) {
            if (player.isLive()) {
                playerAlive = true;
                break;
            }
        }
        if (!playerAlive) {
            running = false;
            return false;
        }

        if (bossSpawned) {
            boolean bossDead = true;
            for (ElementObj b : bosses) {
                if (b.isLive()) {
                    bossDead = false;
                    break;
                }
            }
            if (bossDead && enemys.isEmpty()) {
                level++;
                if (level > 3) {
                    System.out.println("恭喜通关！");
                    running = false;
                    return false;
                }
                em.clearAllElements();
                gameLoad();
                return false;
            }
        }

        return true;
    }

    private void gameOver() {
        System.out.println("游戏结束");
    }
}
