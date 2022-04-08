package ru.nsu.fit.ejsvald.filters;

import ru.nsu.fit.ejsvald.setting.BlurSetPanel;

import java.awt.image.BufferedImage;

public class BlurFilter extends KernelFilter {
    public static final String NAME = "Blur";
    private static final double[][] matrix3 = {{0, 1, 0}, {1, 2, 1}, {0, 1, 0}};
    private static final double[][] matrix5 = {{1, 2, 3, 2, 1}, {2, 4, 5, 4, 2}, {3, 5, 6, 5, 3}, {2, 4, 5, 4, 2},
            {1, 2, 3, 2, 1}};
    private double[][] matrix7 = null;
    private double[][] matrix9 = null;
    private double[][] matrix11 = null;
    private int matrixSize = 3;
    private boolean gauss = false;
    private double sigma = 0;

    public BlurFilter() {
        settingsPanel = new BlurSetPanel(this);
    }

    public BlurFilter(BufferedImage image, int matrixSize) {
        setBlurMatrix(matrixSize);
        settingsPanel = new BlurSetPanel(this);
    }

    public void setMatrixSize(int matrixSize) {
        this.matrixSize = matrixSize;
        if (matrixSize % 2 == 0) {
            this.matrixSize++;
        }
        if (!gauss) {
            setBlurMatrix(matrixSize);
        }
    }

    private double[][] fillGaussMatrix() {
        int n = (int) Math.ceil(sigma * 3);
        double[][] matrix = new double[n * 2 + 1][n * 2 + 1];
        double sum = 0;
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < n + 1; j++) {
                double val = Math.exp(-((n - i) * (n - i) + (n - j) * (n - j)) / (2 * sigma * sigma))
                        / (2 * Math.PI * sigma * sigma);
                matrix[i][j] = val;
                matrix[i][n * 2 - j] = val;
                matrix[n * 2 - i][j] = val;
                matrix[n * 2 - i][n * 2 - j] = val;
            }
        }
        for (int i = 0; i < n * 2 + 1; i++) {
            for (int j = 0; j < n * 2 + 1; j++) {
                sum += matrix[i][j];
            }
        }
        setDivider(sum);
        return matrix;
    }

    private double[][] fillMatrixWithOnes(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = 1;
            }
        }
        return matrix;
    }

    private void setBlurMatrix(int matrixSize) {
        switch (matrixSize) {
            case 3: {
                setMatrix(matrix3, 3);
                setDivider(6);
                setOffset(0);
                break;
            }
            case 5: {
                setMatrix(matrix5, 5);
                setDivider(74);
                setOffset(0);
                break;
            }
            case 7: {
                if (matrix7 == null) {
                    matrix7 = fillMatrixWithOnes(7);
                }
                setMatrix(matrix7, 7);
                setDivider(49);
                setOffset(0);
                break;
            }
            case 9: {
                if (matrix9 == null) {
                    matrix9 = fillMatrixWithOnes(9);
                }
                setMatrix(matrix7, 9);
                setDivider(81);
                setOffset(0);
                break;
            }
            case 11: {
                if (matrix11 == null) {
                    matrix11 = fillMatrixWithOnes(11);
                }
                setMatrix(matrix7, 11);
                setDivider(121);
                setOffset(0);
                break;
            }
        }
    }

    public boolean isGauss() {
        return gauss;
    }

    public void setGauss(boolean gauss) {
        this.gauss = gauss;
        if (gauss) {
            setMatrix(fillGaussMatrix(), (int) Math.ceil(sigma * 3) * 2 + 1);
            setOffset(0);
        } else {
            setBlurMatrix(matrixSize);
        }
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public double getSigma() {
        return sigma;
    }

    public int getMatrixSize() {
        return matrixSize;
    }
}
