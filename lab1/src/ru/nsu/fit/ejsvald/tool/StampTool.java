package ru.nsu.fit.ejsvald.tool;

import ru.nsu.fit.ejsvald.setting.SettingsPanel;
import ru.nsu.fit.ejsvald.setting.StampSettingsPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StampTool implements Tool {
    private final static String name = "Stamp";
    private final BufferedImage image;
    private State state;
    private final SettingsPanel settings;

    public final static String VERTEXES_SETTING = "number of vertexes";
    public final static String THICKNESS_SETTING = "thickness";
    public final static String ROTATION_SETTING = "rotation angle";
    public final static String SIZE_SETTING = "size";

    private int vertexes = 5;
    private int thickness = 2;
    private int rotation = 0;
    private int size = 100;

    public String setVertexes(int vertexes) {
        if (vertexes < 3 || vertexes > 16) {
            return ("Wrong vertexes number value. Enter number between 3 and 16\n");
        }
        this.vertexes = vertexes;
        return null;
    }

    public String setThickness(int thickness) {
        if (thickness < 1 || thickness > 10) {
            return ("Wrong thickness value. Enter number between 1 and 10\n");
        }
        this.thickness = thickness;
        return null;
    }

    public String setRotation(int rotation) {
        if (rotation < -180 || rotation > 180) {
            return ("Wrong rotation value. Enter number between -180 and 180\n");
        }
        this.rotation = rotation;
        return null;
    }

    public String setSize(int size) {
        if (size < 10 || size > 250) {
            return ("Wrong size value. Enter number between 10 and 250");
        }
        this.size = size;
        return null;
    }


    public int getVertexes() {
        return vertexes;
    }

    public int getThickness() {
        return thickness;
    }

    public int getRotation() {
        return rotation;
    }

    public int getSize() {
        return size;
    }


    public enum State {
        NGON,
        STAR
    }

    public StampTool(BufferedImage image, State state) {
        settings = new StampSettingsPanel(this);
        this.image = image;
        this.state = state;
    }

    @Override
    public SettingsPanel getSettings() {
        return settings;
    }

    @Override
    public String getName() {
        return name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void doJob(int x, int y, Color color, EventType eventType) {
        if (eventType == EventType.PRESSED || eventType == EventType.RELEASED) return;
        try {
            LineTool lineTool = new LineTool(image);
            lineTool.setThickness(thickness);
            double angle = Math.PI * 2 / vertexes;
            int workingVertexesNum = vertexes;
            double rotationRad = rotation * Math.PI / 180;
            double k;
            k = 0.5;
            if (vertexes % 2 == 1) {
                k = Math.cos((Math.PI * Math.floor((double) vertexes / 2)) / vertexes)
                        / Math.cos((Math.PI * (Math.floor((double) vertexes / 2) - 1)) / vertexes);
            }
            if (state == State.STAR) {
                angle /= 2;
                workingVertexesNum = vertexes * 2;
            }
            int[] xPoints = new int[workingVertexesNum];
            int[] yPoints = new int[workingVertexesNum];
            double workingAngle = rotationRad - (Math.PI / 2);
            int workingRadius;
            switch (state) {
                case NGON: {

                    for (int i = 0; i < workingVertexesNum; i++) {
                        xPoints[i] = (int) (x + Math.cos(workingAngle) * size);
                        yPoints[i] = (int) (y + Math.sin(workingAngle) * size);
                        workingAngle += angle;
                    }
                    break;
                }
                case STAR: {
                    for (int i = 0; i < workingVertexesNum; i++) {
                        workingRadius = (i % 2 == 1) ? size : (int) (size * k);
                        xPoints[i] = (int) (x + Math.cos(workingAngle) * workingRadius);
                        yPoints[i] = (int) (y + Math.sin(workingAngle) * workingRadius);
                        workingAngle += angle;
                    }
                    break;
                }
            }

            for (int i = 0; i < workingVertexesNum; i++) {
                lineTool.doJob(xPoints[i], yPoints[i], color, EventType.PRESSED);
                lineTool.doJob(xPoints[(i + 1) % workingVertexesNum],
                        yPoints[(i + 1) % workingVertexesNum], color, EventType.RELEASED);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
