package ru.nsu.fit.ejsvald;

import ru.nsu.fit.ejsvald.editor.EditorSettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ModelPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener,
        MouseWheelListener {
    private static final int DEFAULT_X_ANGLE = 45;
    private static final int DEFAULT_Y_ANGLE = 45;
    private static final int DEFAULT_Z_ANGLE = 0;
    private BufferedImage image;

    private Spline spline;
    private ArrayList<Coordinates> points;
    private int xPressed = 0;
    private int yPressed = 0;
    private final int mLines;
    private final int lineApproxNum;
    private final int circleApproxNum;
    private int xImCenter;
    private int yImCenter;
    private boolean fitInScreen = true;

    private int shift;
    private static final int SW = 2;
    private static final int SH = 2;
    private static final int CAM_ANGLE = 15;
    private int Zf;
    private int Zb = 100000;

    private int xAngle = DEFAULT_X_ANGLE;
    private int yAngle = DEFAULT_Y_ANGLE;
    private int zAngle = DEFAULT_Z_ANGLE;

    public ModelPanel() {
        setPreferredSize(new Dimension(1000, 600));
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        addMouseWheelListener(this);
        mLines = EditorSettingsPanel.DEFAULT_M_LINES_NUM;
        lineApproxNum = EditorSettingsPanel.DEFAULT_LINE_APPROX_NUM;
        circleApproxNum = EditorSettingsPanel.DEFAULT_CIRCLE_APPROX_NUM;

        Zf = shift - 20 / 2;
    }

    public ModelPanel(List<Coordinates> points, int mLines, int lineApproxNum, int circleApproxNum) {
        setPreferredSize(new Dimension(1000, 600));
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        addMouseWheelListener(this);

        this.mLines = mLines;
        this.lineApproxNum = lineApproxNum;
        this.circleApproxNum = circleApproxNum;
        spline = new Spline(points, lineApproxNum);
        spline.calculateSpline();
        this.points = (ArrayList<Coordinates>) points;

        int cubSize = spline.calcMaxSplineSize() * 2;

        shift = (int) ((double) cubSize / 2 / Math.sin(CAM_ANGLE * Math.PI / 180));
        Zf = shift - cubSize / 2;
    }

    public ModelPanel(List<Coordinates> points, int Zf, int xAngle, int yAngle, int zAngle,
                      int mLines, int lineApproxNum, int circleApproxNum) {
        setPreferredSize(new Dimension(1000, 600));
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        addMouseWheelListener(this);
        spline = new Spline(points, lineApproxNum);
        spline.calculateSpline();
        this.points = (ArrayList<Coordinates>) points;
        this.mLines = mLines;
        this.lineApproxNum = lineApproxNum;
        this.circleApproxNum = circleApproxNum;

        int cubSize = spline.calcMaxSplineSize() * 2;

        this.shift = (int) ((double) cubSize / 2 / Math.sin(CAM_ANGLE * Math.PI / 180));
        this.Zf = Zf;

        this.xAngle = xAngle;
        this.yAngle = yAngle;
        this.zAngle = zAngle;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    private void setImage(BufferedImage image) {
        xImCenter = image.getWidth() / 2;
        yImCenter = image.getHeight() / 2;
        this.image = image;
    }

    public void setPanelSize(Dimension dimension) {
        setSize(dimension);
        setImage(new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_RGB));
        if (fitInScreen) {
            fitInScreen();
            fitInScreen = false;
        }
    }

    public void drawModel() {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.drawLine(xImCenter, 0, xImCenter, image.getHeight());
        g.drawLine(0, yImCenter, image.getWidth(), yImCenter);

        drawCub(20, g);
//        if (spline == null || spline.getSplinePoints().isEmpty()) return;
//
//        double xAngleRad = xAngle * Math.PI / 180;
//        double yAngleRad = yAngle * Math.PI / 180;
//        double zAngleRad = zAngle * Math.PI / 180;
//
//        int maxSplineSize = spline.calcMaxSplineSize();
//
//        ArrayList<Coordinates> splinePoints = (ArrayList<Coordinates>) spline.getSplinePoints();
//        ArrayList<Coordinates> edges = (ArrayList<Coordinates>) spline.getSplineEdgesCoordinates();
//
//        for (Coordinates point : edges) {
//            drawCircle((int) point.x, (int) point.y);
//        }
//        double angle = (360.0 / mLines) * Math.PI / 180;
//        for (int i = 0; i < mLines; i++) {
//            Coordinates prevPoint = new Coordinates(splinePoints.get(0));
//            prevPoint.x = prevPoint.x / maxSplineSize;
//            prevPoint.y = prevPoint.y / maxSplineSize;
//            prevPoint.z = prevPoint.z / maxSplineSize;
//            prevPoint.rotateX(i * angle).rotateY(yAngleRad).rotateZ(zAngleRad).rotateX(xAngleRad).
//                    translate(0, 0, shift).project(SW, SH, Zf, Zb).normalize(prevPoint.w);
//            for (int l = 1; l < splinePoints.size(); l++) {
//                Coordinates point = new Coordinates(splinePoints.get(l));
//                point.x = point.x / maxSplineSize;
//                point.y = point.y / maxSplineSize;
//                point.z = point.z / maxSplineSize;
//
//                point.rotateX(i * angle).rotateY(yAngleRad).rotateZ(zAngleRad).rotateX(xAngleRad).
//                        translate(0, 0, shift).project(SW, SH, Zf, Zb).normalize(point.w);
//                g.drawLine((int) (prevPoint.x * maxSplineSize) + xImCenter, (int) (prevPoint.y * maxSplineSize) + yImCenter,
//                        (int) (point.x * maxSplineSize) + xImCenter, (int) (point.y * maxSplineSize) + yImCenter);
//                prevPoint = point;
//            }
//        }
    }

    private void drawCub(int sideLength, Graphics g) {
        shift = (int) ((double) sideLength / 2 / Math.sin(CAM_ANGLE * Math.PI / 180));

        ArrayList<Coordinates> points = new ArrayList<>();
        points.add(new Coordinates(-sideLength, -sideLength, -sideLength));
        points.add(new Coordinates(-sideLength, sideLength, -sideLength));
        points.add(new Coordinates(sideLength, sideLength, -sideLength));
        points.add(new Coordinates(sideLength, -sideLength, -sideLength));

        points.add(new Coordinates(-sideLength, -sideLength, sideLength));
        points.add(new Coordinates(-sideLength, sideLength, sideLength));
        points.add(new Coordinates(sideLength, sideLength, sideLength));
        points.add(new Coordinates(sideLength, -sideLength, sideLength));

        ArrayList<Map.Entry<Integer, Integer>> lines = new ArrayList<>();
        lines.add(new AbstractMap.SimpleEntry<>(0,1));
        lines.add(new AbstractMap.SimpleEntry<>(1,2));
        lines.add(new AbstractMap.SimpleEntry<>(2,3));
        lines.add(new AbstractMap.SimpleEntry<>(0,3));

        lines.add(new AbstractMap.SimpleEntry<>(4,5));
        lines.add(new AbstractMap.SimpleEntry<>(5,6));
        lines.add(new AbstractMap.SimpleEntry<>(6,7));
        lines.add(new AbstractMap.SimpleEntry<>(4,7));

        lines.add(new AbstractMap.SimpleEntry<>(0,4));
        lines.add(new AbstractMap.SimpleEntry<>(1,5));
        lines.add(new AbstractMap.SimpleEntry<>(2,6));
        lines.add(new AbstractMap.SimpleEntry<>(3,7));

        for (Map.Entry<Integer, Integer> line : lines) {
            draw3DLine(points.get(line.getKey()), points.get(line.getValue()), g);
        }
    }

    private void draw3DLine(Coordinates p1, Coordinates p2, Graphics g) {
        int maxSplineSize = 20;
        Coordinates p1Norm = new Coordinates(p1);
        p1Norm.x = p1Norm.x / maxSplineSize;
        p1Norm.y = p1Norm.y / maxSplineSize;
        p1Norm.z = p1Norm.z / maxSplineSize;

        Coordinates p2Norm = new Coordinates(p2);
        p2Norm.x = p2Norm.x / maxSplineSize;
        p2Norm.y = p2Norm.y / maxSplineSize;
        p2Norm.z = p2Norm.z / maxSplineSize;

        double xAngleRad = xAngle * Math.PI / 180;
        double yAngleRad = yAngle * Math.PI / 180;
        double zAngleRad = zAngle * Math.PI / 180;

        p1Norm.rotateY(yAngleRad).rotateZ(zAngleRad).rotateX(xAngleRad).
                translate(0, 0, shift).project(SW, SH, Zf, Zb).normalize(p1Norm.w);
        p2Norm.rotateY(yAngleRad).rotateZ(zAngleRad).rotateX(xAngleRad).
                translate(0, 0, shift).project(SW, SH, Zf, Zb).normalize(p2Norm.w);

        g.drawLine((int) (p1Norm.x * maxSplineSize) + xImCenter, (int) (p1Norm.y * maxSplineSize) + yImCenter,
                (int) (p2Norm.x * maxSplineSize) + xImCenter, (int) (p2Norm.y * maxSplineSize) + yImCenter);
    }

    private void drawCircle(int xCoord, int rad) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);

        double angle = 360.0 / mLines / circleApproxNum * Math.PI / 180;
        double xAngleRad = xAngle * Math.PI / 180;
        double yAngleRad = yAngle * Math.PI / 180;
        double zAngleRad = zAngle * Math.PI / 180;

        int maxSplineSize = spline.calcMaxSplineSize();

        Coordinates prevPoint = new Coordinates(xCoord, rad, 0);
        prevPoint.x = prevPoint.x / maxSplineSize;
        prevPoint.y = prevPoint.y / maxSplineSize;
        prevPoint.z = prevPoint.z / maxSplineSize;

        prevPoint.rotateY(yAngleRad).rotateZ(zAngleRad).rotateX(xAngleRad).translate(0, 0, shift)
                .project(SW, SH, Zf, Zb).normalize(prevPoint.w);

        for (int i = 1; i < mLines * circleApproxNum + 1; i++) {
            Coordinates point = new Coordinates(xCoord, rad, 0);
            point.x = point.x / maxSplineSize;
            point.y = point.y / maxSplineSize;
            point.z = point.z / maxSplineSize;

            point.rotateX(i * angle).rotateY(yAngleRad).rotateZ(zAngleRad).rotateX(xAngleRad).
                    translate(0, 0, shift).project(SW, SH, Zf, Zb).normalize(point.w);

            g.drawLine((int) (prevPoint.x * maxSplineSize) + xImCenter, (int) (prevPoint.y * maxSplineSize) + yImCenter,
                    (int) (point.x * maxSplineSize) + xImCenter, (int) (point.y * maxSplineSize) + yImCenter);
            prevPoint = point;
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {
        xPressed = e.getX();
        yPressed = e.getY();
        drawModel();
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        double xShift = (e.getX() - xPressed) * 2.0 / 10;
        double yShift = (e.getY() - yPressed) * 2.0 / 10;

        yAngle = (int) (yAngle - xShift) % 360;
        xAngle = (int) (xAngle + yShift) % 360;

        xPressed = e.getX();
        yPressed = e.getY();
        drawModel();
        repaint();
    }


    public void fitInScreen() {
        Zf = calcFitInZf();
        drawModel();
    }

    public int calcFitInZf() {
        int minPanelDim = Math.min(getWidth(), getHeight());
        return (int) ((double) minPanelDim / 2 / Math.sin(CAM_ANGLE * Math.PI / 180));
    }

    @Override
    public void componentResized(ComponentEvent e) {
        setImage(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB));
        if (fitInScreen) {
            fitInScreen();
            fitInScreen = false;
        }
        drawModel();
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Zf -= e.getWheelRotation() * 4;
        drawModel();
        repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    public List<Coordinates> getPoints() {
        return points;
    }

    public int getmLines() {
        return mLines;
    }

    public int getLineApproxNum() {
        return lineApproxNum;
    }

    public int getCircleApproxNum() {
        return circleApproxNum;
    }

    public int getShift() {
        return shift;
    }

    public int getSw() {
        return SW;
    }

    public int getSh() {
        return SH;
    }

    public int getZf() {
        return Zf;
    }

    public int getxAngle() {
        return xAngle;
    }

    public int getyAngle() {
        return yAngle;
    }

    public int getzAngle() {
        return zAngle;
    }

    public void resetAngles() {
        xAngle = DEFAULT_X_ANGLE;
        yAngle = DEFAULT_Y_ANGLE;
        zAngle = DEFAULT_Z_ANGLE;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
