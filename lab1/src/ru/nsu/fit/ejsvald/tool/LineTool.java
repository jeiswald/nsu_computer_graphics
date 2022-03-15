package ru.nsu.fit.ejsvald.tool;

import ru.nsu.fit.ejsvald.setting.LineSettingsPanel;
import ru.nsu.fit.ejsvald.setting.SettingsPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static java.lang.Math.abs;

public class LineTool implements Tool {
    private LineSettingsPanel settings;
    private final String name = "Line";
    private String description = "Drawing a line of given thickness";
    private int xPressed, yPressed;
    private final BufferedImage image;
    private int thickness;
    public final static String THICKNESS_SETTING = "thickness";

    public LineTool(BufferedImage image) {
        this.image = image;
        thickness = 2;
    }

    @Override
    public SettingsPanel getSettings() {
        settings = new LineSettingsPanel(this);
        return settings;
    }

    public String setThickness(int thickness) throws IOException {
        if (thickness < 1 || thickness > 10) {
            return ("Wrong thickness value. Enter number between 1 and 10");
        }
        this.thickness = thickness;
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void doJob(int x, int y, Color color, EventType eventType) {
        if (eventType == EventType.PRESSED) {
            xPressed = x;
            yPressed = y;
            return;
        }
        int xStep, yStep;
        int x1 = xPressed, y1 = yPressed;
        int dx = abs(x - x1);
        int dy = abs(y - y1);
        xStep = x >= x1 ? 1 : -1;
        yStep = y >= y1 ? 1 : -1;
        if (thickness == 1) {
            if (dx < dy) {
                drawLineBresAbove45(x1, y1, dx, dy, xStep, yStep, color);
            } else {
                drawLineBresUnder45(x1, y1, dx, dy, xStep, yStep, color);
            }
        } else {
            Graphics2D g = image.createGraphics();
            g.setColor(color);
            g.setStroke(new BasicStroke(thickness));
            g.drawLine(xPressed, yPressed, x, y);
        }
    }

    private void drawLineBresUnder45(int x1, int y1, int dx, int dy, int xStep, int yStep, Color color) {
//        if (xStep > 0) {
//            dx = Math.max(x1 + dx, image.getWidth() - x1);
//        } else {
//            dx = Math.min(dx, x1);
//        }
        int err = 0;
        image.setRGB(x1, y1, color.getRGB());
        for (int i = 0; i < dx; ++i) {
            x1 += xStep;
            err += 2 * dy;
            if (err > dx) {
                err -= 2 * dx;
                y1 += yStep;
            }
            if (x1 < image.getWidth() && x1 > 0 && y1 > 0 && y1 < image.getHeight()) {
                image.setRGB(x1, y1, color.getRGB());
            }
        }
    }

    private void drawLineBresAbove45(int x1, int y1, int dx, int dy, int xStep, int yStep, Color color) {
        int err = 0;
        for (int i = 0; i < dy; ++i) {
            y1 += yStep;
            err += 2 * dx;
            if (err > dy) {
                err -= 2 * dy;
                x1 += xStep;
            }
            if (x1 < image.getWidth() && x1 > 0 && y1 > 0 && y1 < image.getHeight()) {
                image.setRGB(x1, y1, color.getRGB());
            }
        }
    }

    public int getThickness() {
        return thickness;
    }

}
