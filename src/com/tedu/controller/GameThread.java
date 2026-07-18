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
    private int killCount = 0;                // 本关击杀计数

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

        // 1. 移动和过期清理
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

        // 2. 玩家子弹 vs 敌人/Boss
        for (ElementObj bullet : playBullets) {
            for (ElementObj enemy : enemys) {
                if (bullet.isLive() && enemy.isLive() && bullet.pk(enemy)) {
                    int beforeHp = enemy.getHp();
                    enemy.onHit(bullet.getAttack());
                    bullet.setLive(false);
                    // 如果敌人被打死，计入击杀数
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

        // 3. 敌人子弹 vs 玩家
        for (ElementObj bullet : enemyBullets) {
            for (ElementObj player : plays) {
                if (bullet.isLive() && player.isLive() && bullet.pk(player)) {
                    player.onHit(bullet.getAttack());
                    bullet.setLive(false);
                }
            }
        }

        // 4. 敌人/Boss 撞玩家
        for (ElementObj enemy : enemys) {
            for (ElementObj player : plays) {
                if (enemy.isLive() && player.isLive() && enemy.pk(player)) {
                    player.onHit(enemy.getAttack());
                    enemy.setLive(false);
                    // 撞死也算击杀（如果之前未死）
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

        // 5. 玩家 vs 道具
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

        // 6. 生成敌人
        if (gameTime - enemyLastSpawn > enemySpawnInterval) {
            enemyLastSpawn = gameTime;
            int randomType = (int)(Math.random() * 12) + 1;
            GameLoad.spawnEnemy(randomType);
        }

        // 7. Boss 生成条件：击杀数 >= 30 且未生成过
        if (!bossSpawned && killCount >= 20) {
            bossSpawned = true;
            GameLoad.spawnBoss(1);
        }

        // 8. 玩家存活检查
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

        // 9. 关卡结束条件：Boss已生成且已死亡，且没有敌人
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
                return false; // 重新进入当前循环，重置计时和击杀
            }
        }

        return true;
    }

    private void gameOver() {
        System.out.println("游戏结束");
    }
}
