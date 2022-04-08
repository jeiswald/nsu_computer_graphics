package ru.nsu.fit.ejsvald.filters;

import ru.nsu.fit.ejsvald.setting.FitInSetPanel;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageScaler extends Filter {
    public static final String NAME = "Image scaler";

    private int transformType = AffineTransformOp.TYPE_BILINEAR;

    public ImageScaler() {
        settingsPanel = new FitInSetPanel(this);
    }

    public BufferedImage apply(BufferedImage image, int maxWidth, int maxHeight) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        imageCopy.getGraphics().drawImage(image, 0, 0, null);
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        if (imageWidth > maxWidth || imageHeight > maxHeight) {
            AffineTransform at = new AffineTransform();
            double scale;
            double scaleW = 1;
            double scaleH = 1;
            if (imageWidth > maxWidth) {
                scaleW = (double) maxWidth / imageWidth;
            }
            if (imageHeight > maxHeight) {
                scaleH = (double) maxHeight / imageHeight;
            }
            scale = Math.min(scaleW, scaleH);
            at.scale(scale, scale);
            AffineTransformOp scaleOp = new AffineTransformOp(at, transformType);
            newImage = new BufferedImage((int) (imageWidth * scale), (int) (imageHeight * scale), BufferedImage.TYPE_INT_ARGB);
            newImage = scaleOp.filter(imageCopy, newImage);
            return newImage;
        }
        newImage.getGraphics().drawImage(image, 0, 0, null);
        return newImage;
    }

    public int getTransformType() {
        return transformType;
    }

    public void setTransformType(int transformType) throws IOException {
        if (transformType != AffineTransformOp.TYPE_BICUBIC &&
                transformType != AffineTransformOp.TYPE_BILINEAR &&
                transformType != AffineTransformOp.TYPE_NEAREST_NEIGHBOR) {
            throw new IOException("Wrong type");
        }
        this.transformType = transformType;
    }
}
