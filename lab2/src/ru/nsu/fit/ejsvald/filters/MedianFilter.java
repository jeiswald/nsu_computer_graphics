package ru.nsu.fit.ejsvald.filters;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class MedianFilter extends Filter {
    private int matrixSize = 5;

    @Override
    public BufferedImage apply(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage extendedImage = getExtendedImage(image, matrixSize / 2);
        int[] orderedRed = new int[matrixSize * matrixSize - 1];
        int[] orderedGreen = new int[matrixSize * matrixSize - 1];
        int[] orderedBlue = new int[matrixSize * matrixSize - 1];
        for (int i = matrixSize / 2; i < extendedImage.getWidth() - matrixSize / 2; i++) {
            for (int j = matrixSize / 2; j < extendedImage.getHeight() - matrixSize / 2; j++) {
                int position = 0;
                for (int xInFil = 0; xInFil < matrixSize; xInFil++) {
                    for (int yInFil = 0; yInFil < matrixSize; yInFil++) {
                        if (xInFil == matrixSize / 2 && yInFil == matrixSize / 2) continue;
                        int color = extendedImage.getRGB(i + (xInFil - matrixSize / 2), j + (yInFil - matrixSize / 2));
                        orderedRed[position] = ((color >> 16) & 0x000000ff);
                        orderedGreen[position] = ((color >> 8) & 0x000000ff);
                        orderedBlue[position] = ((color) & 0x000000ff);
                        position++;
                    }
                }
                Arrays.sort(orderedRed);
                Arrays.sort(orderedGreen);
                Arrays.sort(orderedBlue);
                int r = orderedRed[(matrixSize * matrixSize - 1) / 2];
                int g = orderedGreen[(matrixSize * matrixSize - 1) / 2];
                int b = orderedBlue[(matrixSize * matrixSize - 1) / 2];
                int resultColor = (r << 16) | (g << 8) | (b);
                newImage.setRGB(i - matrixSize / 2, j - matrixSize / 2, resultColor);
            }
        }
        return newImage;
    }

    public void setMatrixSize(int matrixSize) {
        this.matrixSize = matrixSize;
        if (matrixSize % 2 == 0) {
            this.matrixSize++;
        }

    }
}
