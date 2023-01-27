package ru.nsu.fit.ejsvald.filters;

import ru.nsu.fit.ejsvald.setting.RotationSetPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RotationTool extends Filter {
    public static final String NAME = "Rotation";
    private int angle = 44;

    public RotationTool() {
        settingsPanel = new RotationSetPanel(this);
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        double rad = angle * Math.PI / 180;
        int xSrcCenter = image.getWidth() / 2;
        int ySrcCenter = image.getHeight() / 2;
        double tmpAngle = Math.abs(rad);
        int xC1 = (int) (xSrcCenter * Math.cos(tmpAngle) + ySrcCenter * Math.sin(tmpAngle));
        int xC2 = (int) (-xSrcCenter * Math.cos(tmpAngle) + ySrcCenter * Math.sin(tmpAngle));
        int xDstCenter = Math.max(xC1, xC2);
        int yC1 = (int) (xSrcCenter * Math.sin(tmpAngle) + ySrcCenter * Math.cos(tmpAngle));
        int yC2 = (int) (xSrcCenter * Math.sin(tmpAngle) - ySrcCenter * Math.cos(tmpAngle));
        int yDstCenter = Math.max(yC1, yC2);
        xDstCenter += 1;
        yDstCenter += 1;
        BufferedImage imageCopy = new BufferedImage((xDstCenter) * 2, (yDstCenter) * 2, BufferedImage.TYPE_INT_RGB);
        rad = (180 - angle) * Math.PI / 180;
        Graphics2D g = (Graphics2D) imageCopy.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageCopy.getWidth(), imageCopy.getHeight());
        for (int i = 0; i < imageCopy.getWidth(); i++) {
            for (int j = 0; j < imageCopy.getHeight(); j++) {
                int xSrc = (int) ((i - xDstCenter) * Math.cos(rad) - (j - yDstCenter) * Math.sin(rad));
                int ySrc = (int) ((i - xDstCenter) * Math.sin(rad) + (j - yDstCenter) * Math.cos(rad));
                if (xSrc <= -xSrcCenter || xSrc >= xSrcCenter || ySrc <= -ySrcCenter || ySrc >= ySrcCenter) {
                    continue;
                }
                imageCopy.setRGB(i, j, image.getRGB(xSrcCenter - xSrc, ySrcCenter - ySrc));
            }
        }
        return imageCopy;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }
}
