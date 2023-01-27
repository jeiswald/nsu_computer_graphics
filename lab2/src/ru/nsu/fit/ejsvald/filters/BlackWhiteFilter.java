package ru.nsu.fit.ejsvald.filters;

import java.awt.image.BufferedImage;

public class BlackWhiteFilter extends Filter {
    public BlackWhiteFilter() {
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < newImage.getWidth(); i++) {
            for (int j = 0; j < newImage.getHeight(); j++) {
                int color = image.getRGB(i, j);
                double r = ((color >> 16) & 0x000000ff) * 0.299;
                double g = ((color >> 8) & 0x000000ff) * 0.587;
                double b = ((color) & 0x000000ff) * 0.114;
                int component = (int) (r + g + b);
                int gray = (component << 16) | (component << 8) | (component);
                newImage.setRGB(i, j, gray);
            }
        }
        return newImage;
    }
}
