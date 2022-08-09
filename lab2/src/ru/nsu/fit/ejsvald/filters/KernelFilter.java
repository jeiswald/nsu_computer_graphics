package ru.nsu.fit.ejsvald.filters;

import java.awt.image.BufferedImage;

public class KernelFilter extends Filter {
    protected double[][] matrix = null;
    protected double divider = 1;
    protected int matrixSize;
    protected int offset = 0;

    public KernelFilter(double[][] matrix, double divider, int offset, int size) {
        this.divider = divider;
        this.matrix = matrix;
        this.offset = offset;
        matrixSize = size;
    }

    public KernelFilter(double[][] matrix, int size) {
        this.matrix = matrix;
        matrixSize = size;
    }

    public KernelFilter() {
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        if (matrix == null) return null;
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage extendedImage = getExtendedImage(image, matrixSize / 2);
        for (int i = matrixSize / 2; i < extendedImage.getWidth() - matrixSize / 2; i++) {
            for (int j = matrixSize / 2; j < extendedImage.getHeight() - matrixSize / 2; j++) {
                int resultColor = calcKernel(extendedImage, i, j);
                newImage.setRGB(i - matrixSize / 2, j - matrixSize / 2, resultColor);
            }
        }
        return newImage;
    }

    public int calcKernel(BufferedImage image, int x, int y) throws ArrayIndexOutOfBoundsException {
        double r = 0, g = 0, b = 0;
        for (int xInFil = 0; xInFil < matrixSize; xInFil++) {
            for (int yInFil = 0; yInFil < matrixSize; yInFil++) {
                if (matrix[xInFil][yInFil] == 0) continue;
                int color = image.getRGB(x + (xInFil - matrixSize / 2), y + (yInFil - matrixSize / 2));
                r += (double) ((color >> 16) & 0x000000ff) * matrix[xInFil][yInFil] / divider;
                g += (double) ((color >> 8) & 0x000000ff) * matrix[xInFil][yInFil] / divider;
                b += (double) ((color) & 0x000000ff) * matrix[xInFil][yInFil] / divider;
            }
        }
        r += offset;
        g += offset;
        b += offset;
        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;
        return ((int) r << 16) | ((int) g << 8) | ((int) b);
    }

    public int calcKernelRedChan(BufferedImage image, int x, int y) throws ArrayIndexOutOfBoundsException {
        double r = 0;
        for (int xInFil = 0; xInFil < matrixSize; xInFil++) {
            for (int yInFil = 0; yInFil < matrixSize; yInFil++) {
                if (matrix[xInFil][yInFil] == 0) continue;
                int color = image.getRGB(x + (xInFil - matrixSize / 2), y + (yInFil - matrixSize / 2));
                r += (double) ((color >> 16) & 0x000000ff) * matrix[xInFil][yInFil] / divider;
            }
        }
        r += offset;
        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        return ((int) r << 16) | ((int) r << 8) | ((int) r);
    }

    public int calcKernelGreenChan(BufferedImage image, int x, int y) throws ArrayIndexOutOfBoundsException {
        double g = 0;
        for (int xInFil = 0; xInFil < matrixSize; xInFil++) {
            for (int yInFil = 0; yInFil < matrixSize; yInFil++) {
                if (matrix[xInFil][yInFil] == 0) continue;
                int color = image.getRGB(x + (xInFil - matrixSize / 2), y + (yInFil - matrixSize / 2));
                g += (double) ((color >> 8) & 0x000000ff) * matrix[xInFil][yInFil] / divider;
            }
        }
        g += offset;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        return ((int) g << 16) | ((int) g << 8) | ((int) g);
    }

    public int calcKernelBlueChan(BufferedImage image, int x, int y) throws ArrayIndexOutOfBoundsException {
        double b = 0;
        for (int xInFil = 0; xInFil < matrixSize; xInFil++) {
            for (int yInFil = 0; yInFil < matrixSize; yInFil++) {
                if (matrix[xInFil][yInFil] == 0) continue;
                int color = image.getRGB(x + (xInFil - matrixSize / 2), y + (yInFil - matrixSize / 2));
                b += (double) ((color) & 0x000000ff) * matrix[xInFil][yInFil] / divider;
            }
        }
        b += offset;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;
        return ((int) b << 16) | ((int) b << 8) | ((int) b);
    }


    protected void setMatrix(double[][] matrix, int matrixSize) {
        this.matrix = matrix;
        this.matrixSize = matrixSize;
    }

    protected void setDivider(double divider) {
        this.divider = divider;
    }

    protected void setOffset(int offset) {
        this.offset = offset;
    }
}
