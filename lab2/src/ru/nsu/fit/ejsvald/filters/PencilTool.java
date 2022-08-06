package ru.nsu.fit.ejsvald.filters;

import java.awt.*;
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
//        MedianFilter medianFilter = new MedianFilter();
        BlurFilter blurFilter = new BlurFilter();
        blurFilter.setGauss(true, 1.4);
        EdgeDetectionFilter edgeDetectionFilter = new EdgeDetectionFilter();
//        edgeDetectionFilter.setBlackWhite(true);
//        edgeDetectionFilter.setSobel(true);
//        imageToReturn = medianFilter.apply(image);
        imageToReturn = blurFilter.apply(image);

//        imageToReturn = edgeDetection(imageToReturn);
        imageToReturn = edgeDetectionFilter.apply(imageToReturn);

        imageToReturn = blackWhiteFilter.apply(imageToReturn);

        //imageToReturn = expandAndBinImage(imageToReturn);


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

        for (int i = strokeLength / 2; i < extendedImage.getWidth() - strokeLength / 2; i++) {
            for (int j = strokeLength / 2; j < extendedImage.getHeight() - strokeLength / 2; j++) {
                int maxSum = 0;
                int maxN = -1;

                //calculate which stroke has the greatest response
                for (int strokeN = 0; strokeN < strokes.size(); strokeN++) {
                    double[][] stroke = strokes.get(strokeN);
                    int sum = 0;
                    for (int xInFil = 0; xInFil < strokeLength; xInFil++) {
                        for (int yInFil = 0; yInFil < strokeLength; yInFil++) {
                            int p = extendedImage.getRGB(i + (xInFil - strokeLength / 2),
                                    j + (yInFil - strokeLength / 2)) & 0x000000ff;
                            sum += p * stroke[xInFil][yInFil];
                        }
                    }
                    if (sum >= maxSum) {
                        maxSum = sum;
                        maxN = strokeN;
                    }
                }
                if (maxN != -1) {
//                    if (maxSum > strokeLength / 3) {
                    C.get(maxN).setRGB(i - strokeLength / 2,
                            j - strokeLength / 2, extendedImage.getRGB(i, j));
//                    }
                }
            }
        }

        BufferedImage imageToReturn = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int strokeN = 0; strokeN < strokes.size(); strokeN++) {
//        for (int strokeN = 1; strokeN < 2; strokeN++) {
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

        int gradGap = 1;

        for (int i = 0; i < width - gradGap; i++) {
            for (int j = 0; j < height - gradGap; j++) {
                int p1 = image.getRGB(i, j);
                int p2 = image.getRGB(i + gradGap, j);
                int p3 = image.getRGB(i, j + gradGap);
                int valR = (int) Math.sqrt(Math.pow((p2 >> 16) & 0x000000ff - (p1 >> 16) & 0x000000ff, 2)
                        + Math.pow((p3 >> 16) & 0x000000ff - (p1 >> 16) & 0x000000ff, 2));
                if (valR < 50) valR = 0;
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
//            drawLineBresAbove45(newStroke, x1, y1, dx, dy, xStep, yStep, 1);
            drawWuLine(newStroke, x1, y1, x2, y2);
            strokes.add(newStroke);

            if (x1 != y1 || x2 != y2) {
                newStroke = new double[strokeLength][strokeLength];
//                drawLineBresUnder45(newStroke, y1, x1, dy, dx, yStep, xStep, 1);
                drawWuLine(newStroke, y1, x1, y2, x2);
                strokes.add(newStroke);
            }

            if (x1 != strokeLength / 2 || y1 != 0) {
                newStroke = new double[strokeLength][strokeLength];
//                drawLineBresAbove45(newStroke, x1, y2, dx, dy, xStep, -yStep, 1);
                drawWuLine(newStroke, x1, y2, x2, y1);
                strokes.add(newStroke);

                if (x1 != y1 || x2 != y2) {
                    newStroke = new double[strokeLength][strokeLength];
//                    drawLineBresUnder45(newStroke, y1, x2, dy, dx, yStep, -xStep, 1);
                    drawWuLine(newStroke, y1, x2, y2, x1);
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
        }
    }

    private BufferedImage expandAndBinImage(BufferedImage image) {
        BufferedImage imageToReturn = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i = 0; i < width - 1; i++) {
            for (int j = 1; j < height; j++) {
                int p1 = image.getRGB(i, j);
                int p1r = (p1 >> 16) & 0x000000ff;
                if (p1r > 0) {
                    imageToReturn.setRGB(i, j, Color.WHITE.getRGB());
                    imageToReturn.setRGB(i + 1, j, Color.WHITE.getRGB());
                    imageToReturn.setRGB(i, j - 1, Color.WHITE.getRGB());
                }
            }
        }
        return imageToReturn;
    }

    public static void drawWuLine(double[][] matrix, int x0, int y0, int x1, int y1) {
        //Вычисление изменения координат
        int dx = (x1 > x0) ? (x1 - x0) : (x0 - x1);
        int dy = (y1 > y0) ? (y1 - y0) : (y0 - y1);

        if (dx == 0) {
            for (int i = 0; i <= dy; i++) {
                matrix[x0][i] = 1;
            }
            return;
        }
        if (dy == 0) {
            for (int i = 0; i <= dx; i++) {
                matrix[i][y0] = 1;
            }
            return;
        }

        //Для Х-линии (коэффициент наклона < 1)
        if (dy < dx) {
            //Первая точка должна иметь меньшую координату Х
            if (x1 < x0) {
                x1 += x0;
                x0 = x1 - x0;
                x1 -= x0;
                y1 += y0;
                y0 = y1 - y0;
                y1 -= y0;
            }
            //Относительное изменение координаты Y
//            float grad = (float) dy / dx;
            float grad = (float) (y1 - y0) / (x1- x0);
            //Промежуточная переменная для Y
            float intery = y0 + grad;
            //Первая точка
//            PutPixel(g, clr, x0, y0, 255);
            matrix[x0][y0] = 1;

            for (int x = x0 + 1; x < x1; x++) {
                //Верхняя точка
//                PutPixel(g, clr, x, IPart(intery), (int) (255 - FPart(intery) * 255));
                matrix[x][(int) intery] = (255 - (intery - (int) intery) * 255) / 255;
                //Нижняя точка
//                PutPixel(g, clr, x, IPart(intery) + 1, (int) (FPart(intery) * 255));
                matrix[x][(int) intery + 1] = ((intery - (int) intery) * 255) / 255;
                //Изменение координаты Y
                intery += grad;
            }
            //Последняя точка
            matrix[x1][y1] = 1;
        }
        //Для Y-линии (коэффициент наклона > 1)
        else {
            //Первая точка должна иметь меньшую координату Y
            if (y1 < y0) {
                x1 += x0;
                x0 = x1 - x0;
                x1 -= x0;
                y1 += y0;
                y0 = y1 - y0;
                y1 -= y0;
            }
            //Относительное изменение координаты X
//            float grad = (float) dx / dy;
            float grad = (float) (x1- x0) / (y1- y0);
            //Промежуточная переменная для X
            float interx = x0 + grad;
            //Первая точка
//            PutPixel(g, clr, x0, y0, 255);
            matrix[x0][y0] = 1;

            for (int y = y0 + 1; y < y1; y++) {
                //Верхняя точка
//                PutPixel(g, clr, IPart(interx), y, 255 - (int) (FPart(interx) * 255));
                matrix[(int) interx][y] = (255 - (interx - (int) interx) * 255) / 255;
                //Нижняя точка
//                PutPixel(g, clr, IPart(interx) + 1, y, (int) (FPart(interx) * 255));
                matrix[(int) interx + 1][y] = interx - (int) interx;
                //Изменение координаты X
                interx += grad;
            }
            //Последняя точка
            matrix[x1][y1] = 1;
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
