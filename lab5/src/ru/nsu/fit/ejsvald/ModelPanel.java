package ru.nsu.fit.ejsvald;

import ru.nsu.fit.ejsvald.figures.Cube;
import ru.nsu.fit.ejsvald.figures.Figure;
import ru.nsu.fit.ejsvald.figures.Sphere;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ModelPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener,
        MouseWheelListener, KeyListener {
    private BufferedImage image;

    private LinkedList<Figure> figures = new LinkedList<>();

    private Camera camera;
    private int boxSize = 80;
    private Coordinates viewPoint = new Coordinates(0, 0, 0);

    private int xPressed = 0;
    private int yPressed = 0;
    private boolean ctrlPressed = false;

    private int xImCenter;
    private int yImCenter;

    private double xAngle = 0;
    private double yAngle = 0;

    private Color background = new Color(0, 0, 0);

    public ModelPanel() {
        setPreferredSize(new Dimension(1000, 600));
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        addMouseWheelListener(this);

        setFocusable(true);
        addKeyListener(this);

        double camDist = (boxSize / Math.sin(15 * Math.PI / 180)) / 2;
        camera = new Camera(new Coordinates(-camDist, 0, 0), viewPoint,
                1000.0 / 2, 600 / 1.2);

        addFigures();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public void addFigures() {
        figures.add(new Sphere(new Coordinates(0, 0, 0), 40, 10));
//        figures.add(new Cube(new Coordinates(-60, 0,-60), 20));
    }

    private void setImage(BufferedImage image) {
        xImCenter = image.getWidth() / 2;
        yImCenter = image.getHeight() / 2;
        this.image = image;
    }

    public void setPanelSize(Dimension dimension) {
        setSize(dimension);
        setImage(new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_RGB));
    }

    public void drawModel() {
        requestFocus();
        Graphics2D g = image.createGraphics();
        g.setColor(background);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);

        Coordinates p0 = new Coordinates(0, 0, 0);
        Coordinates px = new Coordinates(100, 0, 0);
        Coordinates py = new Coordinates(0, 100, 0);
        Coordinates pz = new Coordinates(0, 0, 100);

        p0 = camera.translateToCamCoordinates(p0);
        px = camera.translateToCamCoordinates(px);
        py = camera.translateToCamCoordinates(py);
        pz = camera.translateToCamCoordinates(pz);

        draw3DLine(p0, px, g);
        draw3DLine(p0, py, g);
        draw3DLine(p0, pz, g);

        for (var f : figures) {
            drawFigure(f, g);
        }
    }

    private void drawFigure(Figure figure, Graphics g) {
        List<Polygon4> polygons = figure.getPolygons();
        Color color = null;
        for (Polygon4 polygon : polygons) {
            draw3DPolygon4(polygon, color, g);
        }
    }


    private void drawWiredSphere(Coordinates center, int rad, int linesNum, Graphics g) {
        Coordinates horizontalCircleRad = new Coordinates(0, rad, 0);
        for (int i = 0; i < linesNum; i++) {
            drawCircle(new Coordinates(center.x, center.y, center.z), rad,
                    0, (Math.PI / linesNum) * i, 0); //vertical circle
            drawCircle(new Coordinates(center.x, center.y + (int) horizontalCircleRad.y, center.z), (int) horizontalCircleRad.x,
                    Math.PI / 2, Math.PI / 2, 0);//horizontal circle, starting from top
            horizontalCircleRad.rotateZ(Math.PI / linesNum);
        }
    }

    private void drawCircle(Coordinates coordinates, int rad, double xRot, double yRot, double zRot) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);

        double angle = 360.0 / 100 * Math.PI / 180;

        Coordinates prevPoint = new Coordinates(0, rad, 0);
        prevPoint.rotateY(yRot).rotateZ(zRot).rotateX(xRot)
                .translate(coordinates.x, coordinates.y, coordinates.z);
        prevPoint = camera.translateToCamCoordinates(prevPoint);

        for (int i = 1; i < 100 + 1; i++) {
            Coordinates point = new Coordinates(0, rad, 0);
            point.rotateX(i * angle).rotateY(yRot).rotateZ(zRot).rotateX(xRot)
                    .translate(coordinates.x, coordinates.y, coordinates.z);
            point = camera.translateToCamCoordinates(point);
            draw3DLine(prevPoint, point, g);
            prevPoint = point;
        }
    }

    private void draw3DLine(Coordinates p1, Coordinates p2, Graphics g) {
        Coordinates p1Proj = camera.calcRealSizeProjection(p1, boxSize);
        Coordinates p2Proj = camera.calcRealSizeProjection(p2, boxSize);

        p1Proj.translate(xImCenter, yImCenter, 0);
        p2Proj.translate(xImCenter, yImCenter, 0);

        g.drawLine((int) p1Proj.x, (int) p1Proj.y, (int) p2Proj.x, (int) p2Proj.y);
    }

    private void fill3DPolygon4(Polygon4 polygon, Graphics g) {
        Color prevColor = g.getColor();
        Coordinates p1Proj = camera.calcRealSizeProjection(polygon.p1, boxSize);
        Coordinates p2Proj = camera.calcRealSizeProjection(polygon.p2, boxSize);
        Coordinates p3Proj = camera.calcRealSizeProjection(polygon.p3, boxSize);
        Coordinates p4Proj = camera.calcRealSizeProjection(polygon.p4, boxSize);

        p1Proj.translate(xImCenter, yImCenter, 0);
        p2Proj.translate(xImCenter, yImCenter, 0);
        p3Proj.translate(xImCenter, yImCenter, 0);
        p4Proj.translate(xImCenter, yImCenter, 0);

        g.setColor(polygon.getColor());

        g.fillPolygon(new int[]{(int) p1Proj.x, (int) p2Proj.x, (int) p3Proj.x, (int) p4Proj.x},
                new int[]{(int) p1Proj.y, (int) p2Proj.y, (int) p3Proj.y, (int) p4Proj.y}, 4);

        g.setColor(prevColor);
    }

    private void draw3DPolygon4(Polygon4 polygon, Color edgeColor, Graphics g) {
        Coordinates p1Cam = camera.translateToCamCoordinates(polygon.p1);
        Coordinates p2Cam = camera.translateToCamCoordinates(polygon.p2);
        Coordinates p3Cam = camera.translateToCamCoordinates(polygon.p3);
        Coordinates p4Cam = camera.translateToCamCoordinates(polygon.p4);

        if (Coordinates.calcInnerProduct(new Coordinates(0, 0, 1),
                new Polygon4(p1Cam, p2Cam, p3Cam, p4Cam).getOrientation()) > 0) {
            fill3DPolygon4(new Polygon4(p1Cam, p2Cam, p3Cam, p4Cam), g);

            if (edgeColor == null) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(edgeColor);
            }

            draw3DLine(p1Cam, p2Cam, g);
            draw3DLine(p2Cam, p3Cam, g);
            draw3DLine(p3Cam, p4Cam, g);
            draw3DLine(p1Cam, p4Cam, g);
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
        double xShift = (e.getX() - xPressed) * 0.5;
        double yShift = (e.getY() - yPressed) * 0.5;
        yAngle = (yAngle - xShift) * Math.PI / 180;
        xAngle = (xAngle + yShift) * Math.PI / 180;
        camera.rotate(-xAngle, -yAngle, 0);
        xPressed = e.getX();
        yPressed = e.getY();
        drawModel();
        repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        setImage(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB));
        drawModel();
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            if (ctrlPressed) {
                camera.scaleZf(0.9);
                camera.scaleZb(0.9);
            } else {
                camera.shiftTowardViewPoint(Math.abs(e.getWheelRotation()) * 0.9);
            }
        } else {
            if (ctrlPressed) {
                camera.scaleZf(1.2);
                camera.scaleZb(1.2);
            } else {
                camera.shiftTowardViewPoint(1 + e.getWheelRotation() * 0.2);
            }
        }

        drawModel();
        repaint();
    }

    public BufferedImage getImage() {
        return image;
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

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                camera.translate(0, -10, 0);
                break;
            case KeyEvent.VK_S:
                camera.translate(0, 10, 0);
                break;
            case KeyEvent.VK_A:
                camera.translate(-10, 0, 0);
                break;
            case KeyEvent.VK_D:
                camera.translate(10, 0, 0);
                break;
            case KeyEvent.VK_CONTROL:
                ctrlPressed = true;
                break;
            default:
        }
        drawModel();
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_CONTROL:
                ctrlPressed = false;
                break;
            default:
        }
    }
}
