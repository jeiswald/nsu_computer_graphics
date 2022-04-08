package ru.nsu.fit.ejsvald.filters;

import ru.nsu.fit.ejsvald.setting.GammaSetPanel;

import java.awt.image.BufferedImage;

public class GammaCorrection extends Filter {
    public static String NAME = "Gamma";
    private double gamma = 2.2;

    public GammaCorrection() {
        settingsPanel = new GammaSetPanel(this);
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int color = image.getRGB(i, j);
                int r = (color >> 16) & 0x000000ff;
                int g = (color >> 8) & 0x000000ff;
                int b = (color) & 0x000000ff;
                r = (int)(255 * Math.pow((double) r / 255, 1 / gamma));
                g = (int)(255 * Math.pow((double) g / 255, 1 / gamma));
                b = (int)(255 * Math.pow((double) b / 255, 1 / gamma));
                int resultColor = (r << 16) | (g << 8) | (b);
                newImage.setRGB(i, j, resultColor);
            }
        }
        return newImage;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }
}
