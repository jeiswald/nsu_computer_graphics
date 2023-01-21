package ru.nsu.fit.ejsvald;

import ru.nsu.fit.ejsvald.tool.EventType;
import ru.nsu.fit.ejsvald.tool.Tool;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class MainPanel extends JPanel implements MouseListener {
    private BufferedImage image;
    private Tool activeTool;
    private Color curColor;


    public MainPanel(Dimension dimension) {
        setPreferredSize(dimension);
        addMouseListener(this);
        image = new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        curColor = new Color(0, 0, 0);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public void clear() {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        repaint();
    }

    public void saveImage(File path) throws IOException {
        ImageIO.write(image, "png", path);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (activeTool == null) {
            return;
        }
        activeTool.doJob(e.getX(), e.getY(), curColor, EventType.CLICKED);
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        if (activeTool == null) {
            return;
        }
        activeTool.doJob(e.getX(), e.getY(), curColor, EventType.PRESSED);
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (activeTool == null) {
            return;
        }
        activeTool.doJob(e.getX(), e.getY(), curColor, EventType.RELEASED);
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void setCurColor(Color curColor) {
        this.curColor = curColor;
    }

    public Tool getActiveTool() {
        return activeTool;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setActiveTool(Tool activeTool) {
        this.activeTool = activeTool;
    }

    public Color getCurColor() {
        return curColor;
    }
}
