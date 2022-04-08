package ru.nsu.fit.ejsvald.filters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class PencilTool extends Filter {
    private List<double[][]> strokes;
    private int strokeLength = 10;

    public BufferedImage apply(BufferedImage image) {
        strokeLength = Math.min(image.getHeight(), image.getWidth()) / 30;
        strokes = new ArrayList<>();
        generateStrokes();
        BufferedImage imageToReturn;

        BlackWhiteFilter blackWhiteFilter = new BlackWhiteFilter();
        MedianFilter medianFilter = new MedianFilter();
        imageToReturn = medianFilter.apply(image);
        imageToReturn = edgeDetection(imageToReturn);

        imageToReturn = blackWhiteFilter.apply(imageToReturn);
        imageToReturn = applyStrokes(imageToReturn);
        NegativeFilter negativeFilter = new NegativeFilter();
        imageToReturn = negativeFilter.apply(imageToReturn);
        return imageToReturn;
    }

    private BufferedImage applyStrokes(BufferedImage image) {
        BufferedImage extendedImage = getExtendedImage(image, strokeLength / 2);
        ArrayList<BufferedImage> C = new ArrayList<>();
        for (int i = 0; i < strokes.size(); i++) {
            C.add(i, new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB));
        }
        BufferedImage imageToReturn = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = strokeLength / 2; i < extendedImage.getWidth() - strokeLength / 2; i++) {
            for (int j = strokeLength / 2; j < extendedImage.getHeight() - strokeLength / 2; j++) {
                int maxSum = 0;
                int maxN = -1;
                for (int strokeN = 0; strokeN < strokes.size(); strokeN++) {
                    double[][] stroke = strokes.get(strokeN);
                    int sum = 0;
                    for (int xInFil = 0; xInFil < strokeLength; xInFil++) {
                        for (int yInFil = 0; yInFil < strokeLength; yInFil++) {
                            sum += (extendedImage.getRGB(i + (xInFil - strokeLength / 2),
                                    j + (yInFil - strokeLength / 2)) & 0x000000ff) * stroke[xInFil][yInFil];
                        }
                    }
                    if (sum >= maxSum) {
                        maxSum = sum;
                        maxN = strokeN;
                    }
                }
                if (maxN != -1) {
                    if (maxSum > strokeLength / 3) {
                        C.get(maxN).setRGB(i - strokeLength / 2,
                                j - strokeLength / 2, extendedImage.getRGB(i, j));
                    }
                }
            }
        }
        for (int strokeN = 0; strokeN < strokes.size(); strokeN++) {
            KernelFilter kernelFilter = new KernelFilter(strokes.get(strokeN), 1, 0, strokeLength);
            BufferedImage tmpImage = kernelFilter.apply(C.get(strokeN));
            for (int x = 0; x < imageToReturn.getWidth(); x++) {
                for (int y = 0; y < imageToReturn.getHeight(); y++) {
                    int addColor = tmpImage.getRGB(x, y);
                    int color = imageToReturn.getRGB(x, y);
                    int comp = ((addColor >> 16) & 0x000000ff) + ((color >> 16) & 0x000000ff);
                    if (comp != 255) {
                        comp *= 0.8;
                    }
                    if (comp > 255) comp = 255;
                    int resultColor = (comp << 16) | (comp << 8) | (comp);
                    imageToReturn.setRGB(x, y, resultColor);
                }
            }
        }
        return imageToReturn;
    }

    private BufferedImage edgeDetection(BufferedImage image) {
        BufferedImage imageToReturn = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth();
        int height = image.getHeight();

        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                int p1 = image.getRGB(i, j);
                int p2 = image.getRGB(i + 1, j);
                int p3 = image.getRGB(i, j + 1);
                int valR = (int) Math.sqrt(Math.pow((p2 >> 16) & 0x000000ff - (p1 >> 16) & 0x000000ff, 2)
                        + Math.pow((p3 >> 16) & 0x000000ff - (p1 >> 16) & 0x000000ff, 2));
                if (valR < 0) valR = 0;
                else if (valR > 255) valR = 255;
                int resultColor = (valR << 16) | (valR << 8) | (valR);
                imageToReturn.setRGB(i, j, resultColor);
            }
        }
        for (int i = 0; i < width; i++) {
            imageToReturn.setRGB(i, height - 1, imageToReturn.getRGB(i, height - 2));
        }
        for (int j = 0; j < height; j++) {
            imageToReturn.setRGB(width - 1, j, imageToReturn.getRGB(width - 2, j));
        }
        return imageToReturn;
    }

    private void generateStrokes() {
        double angle = 22.5;
        int x1 = strokeLength / 2, x2 = strokeLength / 2, y1 = 0, y2 = strokeLength - 1;
        for (int i = 0; i < 3; i++) {
            int xStep, yStep;
            int dx = abs(x2 - x1);
            int dy = abs(y2 - y1);
            xStep = x2 >= x1 ? 1 : -1;
            yStep = y2 >= y1 ? 1 : -1;

            double[][] newStroke = new double[strokeLength][strokeLength];
            drawLineBresAbove45(newStroke, x1, y1, dx, dy, xStep, yStep, 1);
            strokes.add(newStroke);

            if (x1 != y1 || x2 != y2) {
                newStroke = new double[strokeLength][strokeLength];
                drawLineBresUnder45(newStroke, y1, x1, dy, dx, yStep, xStep, 1);
                strokes.add(newStroke);
            }

            if (x1 != strokeLength / 2 || y1 != 0) {
                newStroke = new double[strokeLength][strokeLength];
                drawLineBresAbove45(newStroke, x1, y2, dx, dy, xStep, -yStep, 1);
                strokes.add(newStroke);

                if (x1 != y1 || x2 != y2) {
                    newStroke = new double[strokeLength][strokeLength];
                    drawLineBresUnder45(newStroke, y1, x2, dy, dx, yStep, -xStep, 1);
                    strokes.add(newStroke);
                }
            }
            int center = strokeLength / 2;
            boolean coor1 = false, coor2 = false;
            double tmpAngle = (180 - angle) * Math.PI / 180;
            for (int x = 0; x < strokeLength; x++) {
                for (int y = 0; y < strokeLength; y++) {
                    int ySrc = (int) ((y - center) * Math.cos(tmpAngle) - (x - center) * Math.sin(tmpAngle));
                    int xSrc = (int) ((y - center) * Math.sin(tmpAngle) + (x - center) * Math.cos(tmpAngle));
                    if (center - xSrc == x1 && center - ySrc == y1) {
                        x1 = x;
                        y1 = y;
                        coor1 = true;
                        if (coor2) break;
                        continue;
                    }
                    if (center - xSrc == x2 && center - ySrc == y2) {
                        x2 = x;
                        y2 = y;
                        coor2 = true;
                        if (coor1) break;
                    }
                }
                if (coor1 && coor2) break;
            }
            System.out.println("1");
        }
    }

    private void drawLineBresUnder45(double[][] matrix, int x1, int y1, int dx, int dy, int xStep, int yStep, int color) {
        int err = 0;
        matrix[x1][y1] = color;
        for (int i = 0; i < dx; ++i) {
            x1 += xStep;
            err += 2 * dy;
            if (err > dx) {
                err -= 2 * dx;
                y1 += yStep;
            }
            matrix[x1][y1] = color;
        }
    }

    private void drawLineBresAbove45(double[][] matrix, int x1, int y1, int dx, int dy, int xStep, int yStep, int color) {
        int err = 0;
        matrix[x1][y1] = color;
        for (int i = 0; i < dy; ++i) {
            y1 += yStep;
            err += 2 * dx;
            if (err > dy) {
                err -= 2 * dy;
                x1 += xStep;
            }
            matrix[x1][y1] = color;
        }
    }


}
