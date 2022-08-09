package ru.nsu.fit.ejsvald.filters.pencil;

import ru.nsu.fit.ejsvald.filters.*;
import ru.nsu.fit.ejsvald.setting.PencilSetPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PencilTool extends Filter {
    private List<double[][]> strokes;
    private final int IMAGE_SPLIT_STEP = 256;
    public static String NAME = "Pencil";

    private int strokeLength = 12;
    private boolean isDefaultEdgeDet = true;
    private boolean isRobertsEdgeDet = false;
    private int gradGap = 1;
    private int binParam = 40;
    private int maxGrayLevel = 70;
    private int strokeStep = 1;

    public PencilTool() {
        settingsPanel = new PencilSetPanel(this);
    }

    public BufferedImage apply(BufferedImage image) {
        BufferedImage imageToReturn;
        BlackWhiteFilter blackWhiteFilter = new BlackWhiteFilter();
        BlurFilter blurFilter = new BlurFilter();
        blurFilter.setGauss(true, 1.4);
        EdgeDetectionFilter edgeDetectionFilter = new EdgeDetectionFilter();

        strokeLength = Math.min(image.getHeight(), image.getWidth()) / 50;
        generateStrokes();

        imageToReturn = blurFilter.apply(image);
        imageToReturn = blackWhiteFilter.apply(imageToReturn);

        if (isDefaultEdgeDet) {
            imageToReturn = edgeDetection(imageToReturn, gradGap);
        } else if (isRobertsEdgeDet) {
            imageToReturn = edgeDetectionFilter.apply(imageToReturn);
        } else {
            throw new RuntimeException();
        }

        imageToReturn = binImage(imageToReturn, binParam, maxGrayLevel);

        imageToReturn = applyStrokes(imageToReturn, strokeStep);
        NegativeFilter negativeFilter = new NegativeFilter();
        imageToReturn = negativeFilter.apply(imageToReturn);
        return imageToReturn;
    }

    private BufferedImage applyStrokes(BufferedImage image, int strokesStep) {
        if (strokes == null) return null;
        ArrayList<BufferedImage> C = (ArrayList<BufferedImage>) generateStrokeMaps(image, strokesStep);
        if (C == null) return null;
        BufferedImage imageToReturn = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int strokeN = 0; strokeN < strokes.size(); strokeN++) {
//        for (int strokeN = 2; strokeN < 3; strokeN++) {
            KernelFilter kernelFilter = new KernelFilter(strokes.get(strokeN), strokeLength);
            BufferedImage tmpImage = kernelFilter.apply(C.get(strokeN));
            for (int x = 0; x < imageToReturn.getWidth(); x++) {
                for (int y = 0; y < imageToReturn.getHeight(); y++) {
                    int addColor = tmpImage.getRGB(x, y);
                    int existingColor = imageToReturn.getRGB(x, y);
                    int comp = ((addColor >> 16) & 0x000000ff) + ((existingColor >> 16) & 0x000000ff);
                    if (comp != 255) {
                        comp *= 0.8;
                    }
                    if (comp > 180) comp = 180;//todo const
                    int resultColor = (comp << 16) | (comp << 8) | (comp);
                    imageToReturn.setRGB(x, y, resultColor);
                }
            }
        }
        return imageToReturn;
    }

    private List<BufferedImage> generateStrokeMaps(BufferedImage image, int strokesStep) {
        if (strokes == null || strokes.isEmpty()) return null;

        BufferedImage extendedImage = getExtendedImage(image, strokeLength / 2);
        ArrayList<BufferedImage> C = new ArrayList<>();
        int responseParam = strokeLength * 30;
        for (int i = 0; i < strokes.size(); i++) {
            C.add(i, new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB));
        }

        List<Integer> xCoords = new ArrayList<>();
        List<Integer> yCoords = new ArrayList<>();
        xCoords.add(strokeLength / 2);
        yCoords.add(strokeLength / 2);
        for (int i = strokeLength / 2; i < extendedImage.getWidth() - strokeLength / 2; i += IMAGE_SPLIT_STEP) {
            xCoords.add(Math.min(i + IMAGE_SPLIT_STEP, extendedImage.getWidth() - strokeLength / 2));
        }
        for (int i = strokeLength / 2; i < extendedImage.getHeight() - strokeLength / 2; i += IMAGE_SPLIT_STEP) {
            yCoords.add(Math.min(i + IMAGE_SPLIT_STEP, extendedImage.getHeight() - strokeLength / 2));
        }

        List<StrokeMapGenerateThread> threads = new LinkedList<>();
        for (int i = 0; i < xCoords.size() - 1; i++) {
            for (int j = 0; j < yCoords.size() - 1; j++) {
                StrokeMapGenerateThread thread = new StrokeMapGenerateThread(extendedImage, xCoords.get(i), yCoords.get(j),
                        xCoords.get(i + 1), yCoords.get(j + 1), C, strokes, responseParam, strokeLength, strokesStep);
                threads.add(thread);
                thread.start();
            }
        }
        System.out.println(threads.size());
        try {
            for (StrokeMapGenerateThread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return C;
    }

    private BufferedImage edgeDetection(BufferedImage image, int gradGap) {//todo принимает чб, че-то где-то надо с этим сделать
        BufferedImage imageToReturn = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth();
        int height = image.getHeight();

        for (int i = 0; i < width - gradGap; i++) {
            for (int j = 0; j < height - gradGap; j++) {
                int p1 = image.getRGB(i, j);
                int p2 = image.getRGB(i + gradGap, j);
                int p3 = image.getRGB(i, j + gradGap);
                int valR = (int) Math.sqrt(Math.pow(p2 & 0x000000ff - p1 & 0x000000ff, 2)
                        + Math.pow(p3 & 0x000000ff - p1 & 0x000000ff, 2));
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
        strokes = new ArrayList<>();
        double angle = 22.5;
        int x1 = strokeLength / 2, x2 = strokeLength / 2, y1 = 0, y2 = strokeLength - 1;
        for (int i = 0; i < 3; i++) {
//            int xStep, yStep;
//            int dx = abs(x2 - x1);
//            int dy = abs(y2 - y1);
//            xStep = x2 >= x1 ? 1 : -1;
//            yStep = y2 >= y1 ? 1 : -1;

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

    private BufferedImage expandAndBinImage(BufferedImage image, int binParam) {
        BufferedImage imageToReturn = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
//        imageToReturn.getGraphics().drawImage(image, 0, 0, null);
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i = 0; i < width - 1; i++) {
            for (int j = 1; j < height; j++) {
                int p1 = image.getRGB(i, j);
                int p1b = p1 & 0x0000ff;
                if (p1b > binParam) {
                    p1 = (new Color(128, 128, 128)).getRGB();
                    imageToReturn.setRGB(i, j, p1);
//                    if ((image.getRGB(i + 1, j) & 0x0000ff) == 0) {
                    imageToReturn.setRGB(i + 1, j, p1);
//                    }
//                    if ((image.getRGB(i, j - 1)& 0x0000ff) == 0) {
                    imageToReturn.setRGB(i, j - 1, p1);
//                    }
                }
            }
        }
        return imageToReturn;
    }

    private BufferedImage binImage(BufferedImage image, int binParam, int grayLevel) {
        BufferedImage imageToReturn = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i = 0; i < width - 1; i++) {
            for (int j = 1; j < height; j++) {
                int p1 = image.getRGB(i, j);
                int p1b = p1 & 0x0000ff;
                if (p1b > binParam) {
                    p1 = (grayLevel << 16) | (grayLevel << 8) | (grayLevel);
                    imageToReturn.setRGB(i, j, p1);
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
            float grad = (float) (y1 - y0) / (x1 - x0);
            //Промежуточная переменная для Y
            float intery = y0 + grad;
            matrix[x0][y0] = 1;

            for (int x = x0 + 1; x < x1; x++) {
                matrix[x][(int) intery] = (255 - (intery - (int) intery) * 255) / 255;
                matrix[x][(int) intery + 1] = ((intery - (int) intery) * 255) / 255;
                //Изменение координаты Y
                intery += grad;
            }
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
            float grad = (float) (x1 - x0) / (y1 - y0);
            //Промежуточная переменная для X
            float interx = x0 + grad;
            matrix[x0][y0] = 1;

            for (int y = y0 + 1; y < y1; y++) {
                matrix[(int) interx][y] = (255 - (interx - (int) interx) * 255) / 255;
                matrix[(int) interx + 1][y] = interx - (int) interx;
                //Изменение координаты X
                interx += grad;
            }
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

    public boolean isDefaultEdgeDet() {
        return isDefaultEdgeDet;
    }

    public boolean isRobertsEdgeDet() {
        return isRobertsEdgeDet;
    }

    public void setDefaultEdgeDet(boolean defaultEdgeDet) {
        isDefaultEdgeDet = defaultEdgeDet;
    }

    public void setRobertsEdgeDet(boolean robertsEdgeDet) {
        isRobertsEdgeDet = robertsEdgeDet;
    }

    public void setGradGap(int gradGap) {
        this.gradGap = gradGap;
    }

    public void setBinParam(int binParam) {
        this.binParam = binParam;
    }

    public void setMaxGrayLevel(int maxGrayLevel) {
        this.maxGrayLevel = maxGrayLevel;
    }

    public void setStrokeStep(int strokeStep) {
        this.strokeStep = strokeStep;
    }

    public int getGradGap() {
        return gradGap;
    }

    public int getBinParam() {
        return binParam;
    }

    public int getMaxGrayLevel() {
        return maxGrayLevel;
    }

    public int getStrokeStep() {
        return strokeStep;
    }
}
