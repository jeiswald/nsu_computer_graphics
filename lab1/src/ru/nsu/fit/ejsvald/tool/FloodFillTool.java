package ru.nsu.fit.ejsvald.tool;

import ru.nsu.fit.ejsvald.setting.Setting;
import ru.nsu.fit.ejsvald.setting.SettingsPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class FloodFillTool implements Tool {
    private final String name = "FloodFill";
    private String description = "Filling one colored area";
    private final BufferedImage image;

    public FloodFillTool(BufferedImage image) {
        this.image = image;
    }

//    @Override
//    public HashMap<String, Setting> getSettings() {
//        return null;
//    }


    @Override
    public SettingsPanel getSettings() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void doJob(int xSeed, int ySeed, Color color, EventType eventType) {
        if (eventType != EventType.CLICKED) {
            return;
        }
        int oldVal = image.getRGB(xSeed, ySeed);
        int newVal = color.getRGB();
        LinkedList<Span> stack = new LinkedList<>();
        stack.add(new Span(xSeed, ySeed));
        while (!stack.isEmpty()) {
            Span span = stack.pollFirst();
            int x = span.getX();
            int y = span.getY();
            int xLeftBorder = span.getX();
            while (xLeftBorder > 0 && image.getRGB(xLeftBorder - 1, y) == oldVal) {
                image.setRGB(xLeftBorder - 1, y, newVal);
                xLeftBorder--;
            }
            while (x < image.getWidth() && image.getRGB(x, y) == oldVal) {
                image.setRGB(x, y, newVal);
                x++;
            }
            if (y > 0) {
                scanForSpans(xLeftBorder, x - 1, y - 1, oldVal, stack);
            }
            if (y < image.getHeight() - 1) {
                scanForSpans(xLeftBorder, x - 1, y + 1, oldVal, stack);
            }
        }
    }

    private void scanForSpans(int xBegin, int xEnd, int y, int oldVal, LinkedList<Span> stack) {
        boolean isSpan = false;
        for (int i = xBegin; i <= xEnd; i++) {
            if (image.getRGB(i, y) != oldVal) {
                isSpan = false;
            } else if (!isSpan) {
                stack.add(new Span(i, y));
                isSpan = true;
            }
        }
    }
}
