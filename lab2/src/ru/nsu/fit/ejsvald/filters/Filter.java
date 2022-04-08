package ru.nsu.fit.ejsvald.filters;

import ru.nsu.fit.ejsvald.setting.SettingsPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Filter {
    protected SettingsPanel settingsPanel;

    public BufferedImage apply(BufferedImage image) {
        return null;
    }

    protected BufferedImage getExtendedImage(BufferedImage image, int extPixelNum) {
        BufferedImage extendedImage = new BufferedImage(image.getWidth() + extPixelNum * 2,
                image.getHeight() + extPixelNum * 2, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = extendedImage.createGraphics();
        g.drawImage(image, null, extPixelNum, extPixelNum);
        for (int i = extPixelNum; i < extendedImage.getWidth() - extPixelNum; i++) {
            for (int j = 0; j < extPixelNum; j++) {
                extendedImage.setRGB(i, j, image.getRGB(i - extPixelNum, 0));
            }
        }
        for (int i = extPixelNum; i < extendedImage.getWidth() - extPixelNum; i++) {
            for (int j = extendedImage.getHeight() - extPixelNum; j < extendedImage.getHeight(); j++) {
                extendedImage.setRGB(i, j, image.getRGB(i - extPixelNum, image.getHeight() - 1));
            }
        }
        for (int i = 0; i < extPixelNum; i++) {
            for (int j = extPixelNum; j < extendedImage.getHeight() - extPixelNum; j++) {
                extendedImage.setRGB(i, j, image.getRGB(0, j - extPixelNum));
            }
        }
        for (int i = extendedImage.getWidth() - extPixelNum; i < extendedImage.getWidth(); i++) {
            for (int j = extPixelNum; j < extendedImage.getHeight() - extPixelNum; j++) {
                extendedImage.setRGB(i, j, image.getRGB(image.getWidth() - 1, j - extPixelNum));
            }
        }
        for (int i = 0; i < extPixelNum; i++) {
            for (int j = 0; j < extPixelNum; j++) {
                extendedImage.setRGB(i, j, image.getRGB(0, 0));
            }
        }
        for (int i = extendedImage.getWidth() - extPixelNum; i < extendedImage.getWidth(); i++) {
            for (int j = 0; j < extPixelNum; j++) {
                extendedImage.setRGB(i, j, image.getRGB(image.getWidth() - 1, 0));
            }
        }
        for (int i = 0; i < extPixelNum; i++) {
            for (int j = extendedImage.getHeight() - extPixelNum; j < image.getHeight(); j++) {
                extendedImage.setRGB(i, j, image.getRGB(image.getWidth() - 1, 0));
            }
        }
        for (int i = extendedImage.getWidth() - extPixelNum; i < extendedImage.getWidth(); i++) {
            for (int j = extendedImage.getHeight() - extPixelNum; j < image.getHeight(); j++) {
                extendedImage.setRGB(i, j, image.getRGB(image.getWidth() - 1, 0));
            }
        }
        return extendedImage;
    }

    public SettingsPanel getSettingsPanel() {
        return settingsPanel;
    }
}
