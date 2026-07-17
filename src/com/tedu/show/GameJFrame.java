package com.tedu.show;

import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameJFrame extends JFrame {

    // 修改为 2:3 竖屏比例，宽度600，高度900
    public static int GameX = 600;
    public static int GameY = 900;

    private JPanel jPanel = null;
    private KeyListener keyListener = null;
    private Thread thead = null;

    public GameJFrame() {
        init();
    }

    public void init() {
        this.setSize(GameX, GameY);
        this.setTitle("飞机大战");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // 居中
        this.setResizable(false);         // 固定大小
    }

    public void start() {
        if (jPanel != null) {
            this.add(jPanel);
        }
        if (keyListener != null) {
            this.addKeyListener(keyListener);
        }
        if (thead != null) {
            thead.start();
        }
        this.setVisible(true);

        // 启动渲染线程（面板已实现 Runnable）
        if (this.jPanel instanceof Runnable) {
            new Thread((Runnable) this.jPanel).start();
            System.out.println("渲染线程已启动");
        }
    }

    // getter / setter 注入
    public void setjPanel(JPanel jPanel) {
        this.jPanel = jPanel;
    }
    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
    }
    public void setThead(Thread thead) {
        this.thead = thead;
    }
}