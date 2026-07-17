package com.tedu.manager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.tedu.element.ElementObj;
import com.tedu.show.GameJFrame;

public class GameLoad {

    private static ElementManager em = ElementManager.getManager();

    public static Map<String, ImageIcon> imgMap = new HashMap<>();
    private static Map<String, Class<?>> objMap = new HashMap<>();
    private static final String IMAGE_PATH = "image";

    // ---------- 加载类映射（硬编码，无需配置文件）----------
    public static void loadObj() {
        try {
            objMap.put("player", Class.forName("com.tedu.element.PlayerPlane"));
            objMap.put("enemy", Class.forName("com.tedu.element.Enemy"));
            objMap.put("boss", Class.forName("com.tedu.element.Boss"));
            objMap.put("bullet", Class.forName("com.tedu.element.Bullet"));
            objMap.put("item", Class.forName("com.tedu.element.Item"));
            objMap.put("explode", Class.forName("com.tedu.element.Explosion"));
            objMap.put("background", Class.forName("com.tedu.element.Background"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // ---------- 加载图片 ----------
    public static void loadImages() {
        File root = new File(IMAGE_PATH);
        if (!root.exists()) {
            System.out.println("图片目录不存在：" + root.getAbsolutePath());
            return;
        }
        loadImagesFromDir(root, "");
    }

    private static void loadImagesFromDir(File dir, String prefix) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                loadImagesFromDir(f, prefix + (prefix.isEmpty() ? "" : "/") + f.getName());
            } else if (f.getName().toLowerCase().endsWith(".png")) {
                String name = f.getName();
                int dotIndex = name.lastIndexOf('.');
                String num = name.substring(0, dotIndex);
                String key = (prefix.isEmpty() ? "" : prefix + "/") + num;
                ImageIcon icon = new ImageIcon(f.getPath());
                imgMap.put(key, icon);
            }
        }
    }

    public static ElementObj getObj(String key) {
        try {
            Class<?> clazz = objMap.get(key);
            if (clazz != null) {
                Object instance = clazz.newInstance();
                if (instance instanceof ElementObj) {
                    return (ElementObj) instance;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---------- 背景 ----------
    public static void loadBackground(int level) {
        String key = "background/" + level;
        ImageIcon bgIcon = imgMap.get(key);
        if (bgIcon == null) {
            System.out.println("背景图不存在：" + key);
            return;
        }
        ElementObj bg = getObj("background");
        if (bg != null) {
            String config = "0,0," + key;
            bg = bg.createElement(config);
            em.addElement(bg, GameElement.MAPS);
        }
    }

    // ---------- 玩家 ----------
    public static void loadPlayer(int planeType) {
        String key = "play/" + planeType;
        ImageIcon icon = imgMap.get(key);
        if (icon == null) {
            System.out.println("玩家飞机图片不存在：" + key);
            return;
        }
        ElementObj player = getObj("player");
        if (player != null) {
            int x = (GameJFrame.GameX - icon.getIconWidth()) / 2;
            int y = GameJFrame.GameY - icon.getIconHeight() - 50;
            String config = x + "," + y + "," + planeType;
            player = player.createElement(config);
            em.addElement(player, GameElement.PLAY);
        }
    }

    // ---------- 敌人生成（修改：随机范围1~12）----------
    public static void spawnEnemy(int enemyType) {
        String key = "enemy/" + enemyType;
        ImageIcon icon = imgMap.get(key);
        if (icon == null) {
            System.out.println("敌人图片不存在：" + key);
            return;
        }
        ElementObj enemy = getObj("enemy");
        if (enemy != null) {
            int x = (int)(Math.random() * (GameJFrame.GameX - icon.getIconWidth()));
            int y = -icon.getIconHeight();
            String config = x + "," + y + "," + enemyType;
            enemy = enemy.createElement(config);
            em.addElement(enemy, GameElement.ENEMY);
        }
    }

    // ---------- Boss生成 ----------
    public static void spawnBoss(int bossType) {
        ElementObj boss = getObj("boss");
        if (boss != null) {
            int x = GameJFrame.GameX / 2 - 50;
            int y = -150;
            String config = x + "," + y + "," + bossType;
            boss = boss.createElement(config);
            em.addElement(boss, GameElement.BOSS);
        }
    }

    // ---------- 道具生成 ----------
    public static void spawnItem(int x, int y, int itemType) {
        ElementObj item = getObj("item");
        if (item != null) {
            String config = x + "," + y + "," + itemType;
            item = item.createElement(config);
            em.addElement(item, GameElement.ITEM);
        }
    }
}