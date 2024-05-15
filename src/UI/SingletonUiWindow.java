package src.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class SingletonUiWindow extends JFrame {

    private static SingletonUiWindow instance;

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;

    private SingletonUiWindow(){
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    public void clearWindow(){
        getContentPane().removeAll();
        revalidate();
        repaint();
    }

    public static SingletonUiWindow getInstance(){
        if(instance == null){
            instance = new SingletonUiWindow();
        }
        return instance;
    }

    public void refresh(){
        revalidate();
        repaint();
    }

    public int getWidth(){
        return WIDTH;
    }

    public int getHeight(){
        return HEIGHT;
    }
}
