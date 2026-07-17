package com.tedu.element;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

/**
 * 游戏元素基类，定义通用属性和行为
 */
public abstract class ElementObj {

    private int x;
    private int y;
    private int w;
    private int h;
    private ImageIcon icon;
    private boolean live = true;

    // ========== 新增飞机大战属性 ==========
    protected int hp;          // 血量
    protected int attack;      // 攻击力
    protected int speed;       // 速度（可选）
    // =====================================

    public ElementObj() {
    }

    public ElementObj(int x, int y, int w, int h, ImageIcon icon) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.icon = icon;
    }

    /**
     * 绘制元素自身
     */
    public abstract void showElement(Graphics g);

    /**
     * 键盘事件（按下/松开）
     * 子类选择性重写
     */
    public void keyClick(boolean bl, int key) {
    }

    /**
     * 移动逻辑，由子类实现
     */
    protected void move() {
    }

    /**
     * 射击方法，子类实现后由模板方法 model 调用
     */
    public void shoot(long gameTime) {
    }

    /**
     * 模板方法：元素每帧执行的固定顺序
     * 1. 切换动画帧
     * 2. 移动
     * 3. 发射子弹/添加新元素
     */
    public final void model(long gameTime) {
        updateImage(gameTime);
        move();
        add(gameTime);
    }

    /** 更新动画帧，子类按需重写 */
    protected void updateImage(long time) {}

    /** 添加新元素（如发射子弹），子类按需重写 */
    protected void add(long gameTime) {}

    /**
     * 死亡时调用，可用于播放死亡动画、掉落道具等
     */
    public void die() {
    }

    /**
     * 工厂方法：由字符串解析创建元素
     * @param str 配置字符串，格式由子类定义
     * @return 创建好的元素
     */
    public ElementObj createElement(String str) {
        return null;
    }

    /**
     * 返回元素的碰撞矩形（用于相交检测）
     */
    public Rectangle getRectangle() {
        return new Rectangle(x, y, w, h);
    }

    /**
     * 碰撞检测：判断与另一个元素是否发生矩形重叠
     */
    public boolean pk(ElementObj obj) {
        return this.getRectangle().intersects(obj.getRectangle());
    }

    /**
     * 受到攻击，减少血量，血量<=0则死亡
     * @param damage 受到的伤害值
     */
    public void onHit(int damage) {
        this.hp -= damage;
        if (this.hp <= 0) {
            this.setLive(false);
            die();
        }
    }

    // ========== getters / setters ==========
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getW() {
        return w;
    }
    public void setW(int w) {
        this.w = w;
    }
    public int getH() {
        return h;
    }
    public void setH(int h) {
        this.h = h;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
    public boolean isLive() {
        return live;
    }
    public void setLive(boolean live) {
        this.live = live;
    }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = hp;
    }
    public int getAttack() {
        return attack;
    }
    public void setAttack(int attack) {
        this.attack = attack;
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
}