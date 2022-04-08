package ru.nsu.fit.ejsvald.filters;

import ru.nsu.fit.ejsvald.setting.AquarelleSetPanel;

import java.awt.image.BufferedImage;

public class AquarelleFilter extends Filter{
    public static final String NAME = "Aquarelle";
    private int param = 5;
    public AquarelleFilter() {
        settingsPanel = new AquarelleSetPanel(this);
    }
    @Override
    public BufferedImage apply(BufferedImage image) {
        MedianFilter medianFilter = new MedianFilter();
        medianFilter.setMatrixSize(param);
        KernelFilter kernerlFilter = new KernelFilter();
        double[][] matrix = {{-1,-1,-1}, {-1,9,-1}, {-1,-1,-1}};
        kernerlFilter.setMatrix(matrix, 3);
        BufferedImage newImage = medianFilter.apply(image);
        newImage = kernerlFilter.apply(newImage);
        return newImage;
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }
}
