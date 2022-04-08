package ru.nsu.fit.ejsvald.filters;

import java.awt.image.BufferedImage;

public class OrderedDithering extends Filter {
    private static final int[][] matrix4 = {{0, 8, 2, 10}, {12, 4, 14, 6}, {3, 11, 1, 9}, {15, 7, 13, 5}};
    private static final int[][] matrix2 = {{0, 2}, {3, 1}};
    private static final int[][] matrix8 = {{0, 32, 8, 40, 2, 34, 10, 42}, {48, 16, 56, 24, 50, 18, 58, 26},
            {12, 44, 4, 36, 14, 46, 6, 38}, {60, 28, 52, 20, 62, 30, 54, 22}, {3, 35, 11, 43, 1, 33, 9, 41},
            {51, 19, 59, 27, 49, 17, 57, 25}, {15, 47, 7, 39, 13, 45, 5, 37}, {63, 31, 55, 23, 61, 29, 53, 21}};
    private int rColorNum = 2;
    private int gColorNum = 2;
    private int bColorNum = 2;

    @Override
    public BufferedImage apply(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[][] matrix;
        int rQuantum = 256 / (rColorNum - 1);
        int gQuantum = 256 / (gColorNum - 1);
        int bQuantum = 256 / (bColorNum - 1);

        int min = Math.min(256 / rColorNum, 256 / gColorNum);
        min = Math.min(min, 256 / bColorNum);
        int matrixSize;
        if (min <= 4) {
            matrix = matrix2;
            matrixSize = 2;
        } else if (min <= 16) {
            matrix = matrix4;
            matrixSize = 4;
        } else if (min < 64){
            matrix = matrix8;
            matrixSize = 8;
        } else {
            matrix = generateMatrix(matrix8, 8);
            matrixSize = 16;
        }

        double rCoeff = 255.0 / (matrixSize * matrixSize * (rColorNum - 1));
        double gCoeff = 255.0 / (matrixSize * matrixSize * (gColorNum - 1));
        double bCoeff = 255.0 / (matrixSize * matrixSize * (bColorNum - 1));
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int color = image.getRGB(i, j);
                int r = getClosestColor(((color >> 16) & 0x000000ff) + rCoeff * (matrix[j % matrixSize][i % matrixSize] - 0.5), rQuantum);
                int g = getClosestColor(((color >> 8) & 0x000000ff) + gCoeff * (matrix[j % matrixSize][i % matrixSize] - 0.5), gQuantum);
                int b = getClosestColor(((color) & 0x000000ff) + bCoeff * (matrix[j % matrixSize][i % matrixSize] - 0.5), bQuantum);
                int resultColor = (r << 16) | (g << 8) | (b);
                newImage.setRGB(i, j, resultColor);
            }
        }
        return newImage;
    }

    private int getClosestColor(double color, int quantum) {
        double rErr = Math.min(color % quantum, (color / quantum + 1) * quantum - color);
        color = color % quantum == rErr ? color - rErr : color + rErr;
        return (int) Math.min(color, 255);
    }

    private int[][] generateMatrix(int[][] matrix, int size) {
        int[][] basis = {{0, 2}, {3, 1}};
        int newSize = size * 2;
        int[][] newMatrix = new int[newSize][newSize];
        for (int i = 0; i < newSize; i++) {
            for (int j = 0; j < newSize; j++) {
                newMatrix[i][j] = matrix[i % size][j % size] * 4 +
                        basis[i / size][j / size];
            }
        }
        return newMatrix;
    }

    public int getBColorNum() {
        return bColorNum;
    }

    public int getGColorNum() {
        return gColorNum;
    }

    public int getRColorNum() {
        return rColorNum;
    }

    public void setBColorNum(int bColorNum) {
        this.bColorNum = bColorNum;
    }

    public void setGColorNum(int gColorNum) {
        this.gColorNum = gColorNum;
    }

    public void setRColorNum(int rColorNum) {
        this.rColorNum = rColorNum;
    }
}
