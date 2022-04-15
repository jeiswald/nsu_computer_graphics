package ru.nsu.fit.ejsvald;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame implements ComponentListener {
    private final MainPanel mainPanel;
    public MainFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addComponentListener(this);
        mainPanel = new MainPanel(new Dimension(500, 400));
        add(mainPanel);
        pack();
    }
    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent e) {
//        if (this.getWidth() < mainPanel.getImage().getWidth()
//                && this.getHeight() < mainPanel.getImage().getHeight()) {
//            return;
//        }
//        int newWidth = Math.max(mainPanel.getImage().getWidth(), getWidth());
//        int newHeight = Math.max(mainPanel.getImage().getHeight(), getHeight());

//        BufferedImage newSizeImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g = newSizeImage.createGraphics();
//        g.setColor(Color.BLACK);
//        g.fillRect(0, 0, newWidth, newHeight);
        mainPanel.resizePanel(new Dimension(getWidth(), getHeight()));
//        scrollPane.revalidate();
    }


    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
