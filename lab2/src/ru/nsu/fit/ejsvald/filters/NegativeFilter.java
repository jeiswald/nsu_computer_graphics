package ru.nsu.fit.ejsvald.filters;

import java.awt.image.BufferedImage;

public class NegativeFilter extends Filter {
    @Override
    public BufferedImage apply(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int color = image.getRGB(i, j);
                int r = 255 - ((color >> 16) & 0x000000ff);
                int g = 255 - ((color >> 8) & 0x000000ff);
                int b = 255 - ((color) & 0x000000ff);
                int invers = (r << 16) | (g << 8) | (b);
                newImage.setRGB(i, j, invers);
            }
        }
        return newImage;
    }
}
