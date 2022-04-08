package ru.nsu.fit.ejsvald.filters;

import java.awt.image.BufferedImage;

public class DitherFloydSteinberg extends Filter {
    private static int R_IND = 0;
    private static int G_IND = 1;
    private static int B_IND = 2;

    private int rColorNum = 3;
    private int gColorNum = 3;
    private int bColorNum = 3;
    int[][] error;
    int[] errorNext;

    public DitherFloydSteinberg() {
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        error = new int[3][image.getWidth()];
        errorNext = new int[3];
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int rQuantum = 256 / (rColorNum - 1);
        int gQuantum = 256 / (gColorNum - 1);
        int bQuantum = 256 / (bColorNum - 1);
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                int color = image.getRGB(i, j);
                int r = ((color >> 16) & 0x000000ff) + errorNext[R_IND];
                int g = ((color >> 8) & 0x000000ff) + errorNext[G_IND];
                int b = ((color) & 0x000000ff) + errorNext[B_IND];

                int newR = getClosestColor(r, rQuantum);
                int newG = getClosestColor(g, gQuantum);
                int newB = getClosestColor(b, bQuantum);

                int rErr = r - newR;
                int gErr = g - newG;
                int bErr = b - newB;

                int resultColor = (newR << 16) | (newG << 8) | (newB);
                newImage.setRGB(i, j, resultColor);

                if (i == 0) {
                    error[R_IND][i] += rErr * 6 / 16;
                    error[G_IND][i] += gErr * 6 / 16;
                    error[B_IND][i] += bErr * 6 / 16;

                    errorNext[R_IND] = (rErr * 8 / 16) + error[R_IND][i + 1];
                    errorNext[G_IND] = (gErr * 8 / 16) + error[G_IND][i + 1];
                    errorNext[B_IND] = (bErr * 8 / 16) + error[B_IND][i + 1];

                    error[R_IND][i + 1] = rErr * 2 / 16;
                    error[G_IND][i + 1] = gErr * 2 / 16;
                    error[B_IND][i + 1] = bErr * 2 / 16;
                }

                if (i != 0 && i != image.getWidth() - 1) {
                    error[R_IND][i - 1] += rErr * 3 / 16;
                    error[G_IND][i - 1] += gErr * 3 / 16;
                    error[B_IND][i - 1] += bErr * 3 / 16;

                    error[R_IND][i] += rErr * 5 / 16;
                    error[G_IND][i] += gErr * 5 / 16;
                    error[B_IND][i] += bErr * 5 / 16;

                    errorNext[R_IND] = (rErr * 7 / 16) + error[R_IND][i + 1];
                    errorNext[G_IND] = (gErr * 7 / 16) + error[G_IND][i + 1];
                    errorNext[B_IND] = (bErr * 7 / 16) + error[B_IND][i + 1];

                    error[R_IND][i + 1] = rErr / 16;
                    error[G_IND][i + 1] = gErr / 16;
                    error[B_IND][i + 1] = bErr / 16;
                }


                if (i == image.getWidth() - 1) {
                    error[R_IND][i - 1] += rErr * 7 / 16;
                    error[G_IND][i - 1] += gErr * 7 / 16;
                    error[B_IND][i - 1] += bErr * 7 / 16;

                    error[R_IND][i] += rErr * 9 / 16;
                    error[G_IND][i] += gErr * 9 / 16;
                    error[B_IND][i] += bErr * 9 / 16;

                    errorNext[R_IND] = error[R_IND][0];
                    errorNext[G_IND] = error[G_IND][0];
                    errorNext[B_IND] = error[B_IND][0];

                    error[R_IND][0] = 0;
                    error[G_IND][0] = 0;
                    error[B_IND][0] = 0;
                }
            }
        }
        return newImage;
    }

    private int getClosestColor(int color, int quantum) {
        int rErr = Math.min(color % quantum, (color / quantum + 1) * quantum - color);
        color = color % quantum == rErr ? color - rErr : color + rErr;
        return Math.min(color, 255);
    }

    public int getRColorNum() {
        return rColorNum;
    }

    public void setRColorNum(int rColorNum) {
        this.rColorNum = rColorNum;
    }

    public int getGColorNum() {
        return gColorNum;
    }

    public void setGColorNum(int gColorNum) {
        this.gColorNum = gColorNum;
    }

    public int getBColorNum() {
        return bColorNum;
    }

    public void setBColorNum(int bColorNum) {
        this.bColorNum = bColorNum;
    }
}
