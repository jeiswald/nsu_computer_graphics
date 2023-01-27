package ru.nsu.fit.ejsvald.filters.pencil;

import java.awt.image.BufferedImage;
import java.util.List;

public class StrokeMapGenerateThread extends Thread {
    private BufferedImage image;
    private int x0, x1, y0, y1;
    private final List<BufferedImage> strokeMaps;
    private List<double[][]> strokes;
    private final int responseParam;
    private final int strokeLength;
    private int strokeStep;

    public StrokeMapGenerateThread(BufferedImage image, int x0, int y0, int x1, int y1,
                                   List<BufferedImage> strokeMaps, List<double[][]> strokes,
                                   int responseParam, int strokeLength, int strokeStep) {
        this.image = image;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.strokeMaps = strokeMaps;
        this.strokes = strokes;
        this.responseParam = responseParam;
        this.strokeLength = strokeLength;
        this.strokeStep = strokeStep;
    }

    @Override
    public void run() {
        for (int x = x0; x < x1; x += strokeStep) {
            for (int y = y0; y < y1; y += strokeStep) {
                int sum = 0;
                int maxSum = 0;
                int maxN = -1;

                for (int xInFil = 0; xInFil < strokeLength; xInFil++) {
                    for (int yInFil = 0; yInFil < strokeLength; yInFil++) {
                        int p = image.getRGB(x + (xInFil - strokeLength / 2),
                                y + (yInFil - strokeLength / 2)) & 0x000000ff;
                        sum += p;
                    }
                }
                if (sum < responseParam) continue;

                //calculate which stroke has the greatest response
                for (int strokeN = 0; strokeN < strokes.size(); strokeN++) {
                    double[][] stroke = strokes.get(strokeN);
                    sum = 0;
                    for (int xInFil = 0; xInFil < strokeLength; xInFil++) {
                        for (int yInFil = 0; yInFil < strokeLength; yInFil++) {
                            if (stroke[xInFil][yInFil] == 0) continue;
                            int p = image.getRGB(x + (xInFil - strokeLength / 2),
                                    y + (yInFil - strokeLength / 2)) & 0x000000ff;
                            sum += p * stroke[xInFil][yInFil];
                        }
                    }
                    if (sum >= maxSum) {
                        maxSum = sum;
                        maxN = strokeN;
                    }
                }
                if (maxN != -1) {
                    synchronized (strokeMaps) {
                        if (maxSum > responseParam) {
                            strokeMaps.get(maxN).setRGB(x - strokeLength / 2,
                                    y - strokeLength / 2, image.getRGB(x, y));
                        }
                    }
                }
            }
        }
    }
}
