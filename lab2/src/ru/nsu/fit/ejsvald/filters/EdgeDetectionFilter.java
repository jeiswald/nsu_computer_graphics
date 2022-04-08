package ru.nsu.fit.ejsvald.filters;

import ru.nsu.fit.ejsvald.setting.EdgeDetectionSetPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EdgeDetectionFilter extends Filter {
    public static final String BINARY_PAR_SETTING = "Binary parameter";
    public static final String NAME = "Edge detection";
    private int binaryPar = 70;
    private boolean bin = false;
    private boolean blackWhite = false;
    private boolean roberts = true;
    private boolean sobel = false;

    public EdgeDetectionFilter(int binaryPar) {
        this.binaryPar = binaryPar;
        bin = true;
        settingsPanel = new EdgeDetectionSetPanel(this);
    }

    public EdgeDetectionFilter() {
        settingsPanel = new EdgeDetectionSetPanel(this);
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        if (roberts) {
            return applyRoberts(image);
        } else if (sobel) {
            return applySobel(image);
        }
        return image;
    }

    private BufferedImage applyRoberts(BufferedImage image) {
        BufferedImage toReturn = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < image.getWidth() - 1; i++) {
            for (int j = 0; j < image.getHeight() - 1; j++) {
                int p1 = image.getRGB(i, j);
                int p2 = image.getRGB(i + 1, j);
                int p3 = image.getRGB(i, j + 1);
                int p4 = image.getRGB(i + 1, j + 1);
                int valR = (int) Math.sqrt(Math.pow((p1 >> 16) & 0x000000ff - (p4 >> 16) & 0x000000ff, 2)
                        + Math.pow((p3 >> 16) & 0x000000ff - (p2 >> 16) & 0x000000ff, 2));
                int valG = (int) Math.sqrt(Math.pow(((p1 >> 8) & 0x000000ff - (p4 >> 8) & 0x000000ff), 2)
                        + Math.pow((p3 >> 8) & 0x000000ff - (p2 >> 8) & 0x000000ff, 2));
                int valB = (int) Math.sqrt(Math.pow(p1 & 0x000000ff - p4 & 0x000000ff, 2)
                        + Math.pow(p3 & 0x000000ff - p2 & 0x000000ff, 2));

                if (bin & !blackWhite) {
                    if (valR < binaryPar) valR = 0;
                    else valR = 255;
                    if (valG < binaryPar) valG = 0;
                    else valG = 255;
                    if (valB < binaryPar) valB = 0;
                    else valB = 255;
                } else {
                    if (valR < 0) valR = 0;
                    else if (valR > 255) valR = 255;
                    if (valG < 0) valG = 0;
                    else if (valG > 255) valG = 255;
                    if (valB < 0) valB = 0;
                    else if (valB > 255) valB = 255;
                }
                int resultColor = (valR << 16) | (valG << 8) | (valB);
                toReturn.setRGB(i, j, resultColor);
            }
        }
        for (int i = 0; i < image.getWidth(); i++) {
            toReturn.setRGB(i, toReturn.getHeight() - 1, toReturn.getRGB(i, toReturn.getHeight() - 2));
        }
        for (int j = 0; j < image.getHeight(); j++) {
            toReturn.setRGB(toReturn.getWidth() - 1, j, toReturn.getRGB(toReturn.getWidth() - 2, j));
        }
        if (bin & blackWhite) {
            toReturn = binBWImage(toReturn);
        } else if (blackWhite) {
            BlackWhiteFilter blackWhiteFilter = new BlackWhiteFilter();
            toReturn = blackWhiteFilter.apply(toReturn);
        }
        return toReturn;
    }

    private BufferedImage applySobel(BufferedImage image) {
        BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage extendedImage = getExtendedImage(image, 1);
        int[][] xMatrix = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] yMatrix = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        for (int i = 1; i < extendedImage.getWidth() - 1; i++) {
            for (int j = 1; j < extendedImage.getHeight() - 1; j++) {
                int xr = 0, xg = 0, xb = 0, yr = 0, yg = 0, yb = 0;
                for (int xInFil = 0; xInFil < 3; xInFil++) {
                    for (int yInFil = 0; yInFil < 3; yInFil++) {
                        int color = extendedImage.getRGB(i + (xInFil - 1), j + (yInFil - 1));
                        xr += ((color >> 16) & 0x000000ff) * xMatrix[xInFil][yInFil];
                        xg += ((color >> 8) & 0x000000ff) * xMatrix[xInFil][yInFil];
                        xb += ((color) & 0x000000ff) * xMatrix[xInFil][yInFil];
                        yr += ((color >> 16) & 0x000000ff) * yMatrix[xInFil][yInFil];
                        yg += ((color >> 8) & 0x000000ff) * yMatrix[xInFil][yInFil];
                        yb += ((color) & 0x000000ff) * yMatrix[xInFil][yInFil];
                    }
                }
                int valR = (int) Math.sqrt(Math.pow(xr, 2) + Math.pow(yr, 2));
                int valG = (int) Math.sqrt(Math.pow(xg, 2) + Math.pow(yg, 2));
                int valB = (int) Math.sqrt(Math.pow(xb, 2) + Math.pow(yb, 2));
                if (bin & !blackWhite) {
                    if (valR < binaryPar) valR = 0;
                    else valR = 255;
                    if (valG < binaryPar) valG = 0;
                    else valG = 255;
                    if (valB < binaryPar) valB = 0;
                    else valB = 255;
                } else {
                    if (valR < 0) valR = 0;
                    else if (valR > 255) valR = 255;
                    if (valG < 0) valG = 0;
                    else if (valG > 255) valG = 255;
                    if (valB < 0) valB = 0;
                    else if (valB > 255) valB = 255;
                }
                int resultColor = (valR << 16) | (valG << 8) | (valB);
                imageCopy.setRGB(i - 1, j - 1, resultColor);
            }
        }
        if (bin & blackWhite) {
            imageCopy = binBWImage(imageCopy);
        } else if (blackWhite) {
            BlackWhiteFilter blackWhiteFilter = new BlackWhiteFilter();
            imageCopy = blackWhiteFilter.apply(imageCopy);
        }
        return imageCopy;
    }

    private BufferedImage binBWImage(BufferedImage image) {
        BlackWhiteFilter blackWhiteFilter = new BlackWhiteFilter();
        BufferedImage imageCopy = blackWhiteFilter.apply(image);
        for (int i = 0; i < image.getWidth() - 1; i++) {
            for (int j = 0; j < image.getHeight() - 1; j++) {
                int color = imageCopy.getRGB(i, j);
                int colorPar = (color >> 16) & 0x000000ff;
                if (colorPar > binaryPar) {
                    color = 0x00ffffff;
                } else {
                    color = 0;
                }
                imageCopy.setRGB(i, j, color);
            }
        }
        return imageCopy;
    }

    public int getBinaryPar() {
        return binaryPar;
    }

    public void setBinaryPar(int binaryPar) {
        this.binaryPar = binaryPar;
    }

    public void setBin(boolean bin) {
        this.bin = bin;
    }

    public boolean isBin() {
        return bin;
    }

    public boolean isBlackWhite() {
        return blackWhite;
    }

    public void setBlackWhite(boolean blackWhite) {
        this.blackWhite = blackWhite;
    }

    public boolean isSobel() {
        return sobel;
    }

    public void setSobel(boolean sobel) {
        this.sobel = sobel;
    }

    public boolean isRoberts() {
        return roberts;
    }

    public void setRoberts(boolean roberts) {
        this.roberts = roberts;
    }
}
