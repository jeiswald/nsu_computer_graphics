package ru.nsu.fit.ejsvald.editor;

import ru.nsu.fit.ejsvald.Coordinates;
import ru.nsu.fit.ejsvald.ModelFrame;
import ru.nsu.fit.ejsvald.Spline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SplinePanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {
    private BufferedImage image;
    private final ModelFrame modelFrame;
    private final ArrayList<Coordinates> points;
    private final ArrayList<Coordinates> centerPoints;
    private int xImCenter;
    private int yImCenter;
    private int approxNum;
    private static final int BIG_POINT_RAD = 15;
    private static final int SMALL_POINT_RAD = 9;
    private int activePoint = -1;
    private boolean pointPressed = false;
    private int xPressed = 0;
    private int yPressed = 0;
    private final EditorSettingsPanel settingsPanel;

    public SplinePanel(ModelFrame modelFrame) {
        setPreferredSize(new Dimension(1000, 500));
        this.modelFrame = modelFrame;
        addMouseListener(this);
        addMouseMotionListener(this);
        centerPoints = new ArrayList<>();

        if (modelFrame == null || modelFrame.getModelPanel().getPoints() == null) {
            points = new ArrayList<>();
        } else {
            points = new ArrayList<>();
            points.addAll(modelFrame.getModelPanel().getPoints());
        }
        addComponentListener(this);
        settingsPanel = new EditorSettingsPanel(this);
        approxNum = settingsPanel.getLineApproxNum();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public void drawSpline() {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.drawLine(xImCenter, 0, xImCenter, image.getHeight());
        g.drawLine(0, yImCenter, image.getWidth(), yImCenter);

        drawPoints(image);

        if (points.size() < 4) {
            return;
        }

        g.setColor(Color.WHITE);

        Spline spline = new Spline(points, approxNum);
        ArrayList<Coordinates> splinePoints = (ArrayList<Coordinates>) spline.calculateSpline();
        int prevX = (int) splinePoints.get(0).getX() + xImCenter;
        int prevY = (int) splinePoints.get(0).getY() + yImCenter;
        g.setColor(Color.RED);

        for (int k = 1; k < splinePoints.size(); k++) {
            int x = (int) splinePoints.get(k).getX() + xImCenter;
            int y = (int) splinePoints.get(k).getY() + yImCenter;

            g.setColor(Color.WHITE);
            g.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }

    }

    private void drawPoints(BufferedImage image) {
        if (points.isEmpty()) return;
        centerPoints.clear();
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLUE);
        Coordinates prevPoint = points.get(0);
        int prevX = (int) prevPoint.getX() + xImCenter;
        int prevY = (int) prevPoint.getY() + yImCenter;
        if (activePoint == 0) g.setColor(Color.RED);
        g.drawOval(prevX - BIG_POINT_RAD / 2, prevY - BIG_POINT_RAD / 2, BIG_POINT_RAD, BIG_POINT_RAD);
        g.setColor(Color.BLUE);
        for (int i = 1; i < points.size(); i++) {
            Coordinates point = points.get(i);
            int x = (int) point.getX() + xImCenter;
            int y = (int) point.getY() + yImCenter;
            if (i == activePoint) {
                g.setColor(Color.RED);
            }
            g.drawOval(x - BIG_POINT_RAD / 2, y - BIG_POINT_RAD / 2, BIG_POINT_RAD, BIG_POINT_RAD);
            g.setColor(Color.BLUE);
            int centerX = prevX + (x - prevX) / 2;
            int centerY = prevY + (y - prevY) / 2;
            centerPoints.add(new Coordinates(centerX - xImCenter, centerY - yImCenter, 0));
            g.drawOval(centerX - SMALL_POINT_RAD / 2, centerY - SMALL_POINT_RAD / 2, SMALL_POINT_RAD, SMALL_POINT_RAD);
            g.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    private void setImage(BufferedImage image) {
        xImCenter = image.getWidth() / 2;
        yImCenter = image.getHeight() / 2;
        this.image = image;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        setImage(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB));
        drawSpline();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        grabFocus();
        settingsPanel.validateActivePointCoordinates();
        int x = e.getX() - xImCenter;
        int y = e.getY() - yImCenter;
        xPressed = e.getX();
        yPressed = e.getY();
        for (int i = 0; i < points.size(); i++) {
            Coordinates point = points.get(i);
            if (isOverPoint(x, y, point, BIG_POINT_RAD)) {
                pointPressed = true;
                setActivePoint(i, (int)point.getX(), (int)point.getY());
            }
        }
        drawSpline();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() - xImCenter;
        int y = e.getY() - yImCenter;

        for (int i = 0; i < points.size(); i++) {
            Coordinates point = points.get(i);
            if (isOverPoint(x, y, point, BIG_POINT_RAD)) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    points.remove(i);
                    drawSpline();
                    repaint();
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }
            }
        }
        if (SwingUtilities.isLeftMouseButton(e) && points.size() < 4) {
            points.add(new Coordinates(x, y, 0));
            setActivePoint(points.size() - 1, x, y);
        } else {
            for (int i = 0; i < centerPoints.size(); i++) {
                Coordinates point = centerPoints.get(i);
                if (isOverPoint(x, y, point, SMALL_POINT_RAD)) {
                    points.add(i + 1, new Coordinates(x, y, 0));
                    setActivePoint(i + 1, x, y);
                    break;
                }
            }
        }
        drawSpline();
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int xShift;
        int yShift;
        if (pointPressed) {
            pointPressed = false;
        } else {
            xShift = e.getX() - xPressed;
            yShift = e.getY() - yPressed;
            xImCenter += xShift;
            yImCenter += yShift;
        }
        drawSpline();
        repaint();
    }

    public void addPointNextToActive() {
        if (points.size() < 2) return;
        if (activePoint == points.size() - 1) {
            addPointAtEnd();
        } else {
            Coordinates point = points.get(activePoint);
            Coordinates nextPoint = points.get(activePoint + 1);
            int dx = (int) nextPoint.getX() - (int) point.getX();
            int dy = (int) nextPoint.getY() - (int) point.getY();
            points.add(activePoint + 1, new Coordinates((int) point.getX() + dx / 2, (int) point.getY() + dy / 2, 0));
        }
        activePoint++;
    }

    public void addPointAtEnd() {
        Coordinates lastPoint = points.get(points.size() - 1);
        Coordinates prevPoint = points.get(points.size() - 2);
        int dx = (int) lastPoint.getX() - (int) prevPoint.getX();
        int dy = (int) lastPoint.getY() - (int) prevPoint.getY();
        points.add(new Coordinates((int) lastPoint.getX() + dx / 2, (int) lastPoint.getY() + dy / 2, 0));
    }

    private boolean isOverPoint(int x, int y, Coordinates point, int pointSize) {
        return Math.abs(x - point.getX()) < pointSize && Math.abs(y - point.getY()) < pointSize;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (pointPressed) {
            Coordinates point = points.get(activePoint);
            point.setX(e.getX() - xImCenter);
            point.setY(e.getY() - yImCenter);
            setActivePoint(activePoint, (int) point.getX(), (int) point.getY());
        } else {
            int xShift = e.getX() - xPressed;
            int yShift = e.getY() - yPressed;
            xImCenter += xShift;
            yImCenter += yShift;
            xPressed = e.getX();
            yPressed = e.getY();
        }
        drawSpline();
        repaint();
    }

    private void setActivePoint(int num, int x, int y) {
        activePoint = num;
        settingsPanel.setActivePoint(num);
        settingsPanel.setActivePointCoordinates(x, y);
    }

    public void setPointCoordinates(int num, int x, int y) {
        points.get(num).setX(x);
        points.get(num).setY(y);
    }

    public int getActivePoint() {
        return activePoint;
    }
    public Coordinates getPoint(int num) {
        return points.get(num);
    }

    public void setActivePoint(int activePoint) {
        this.activePoint = activePoint;
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
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public EditorSettingsPanel getSettingsPanel() {
        return settingsPanel;
    }

    public List<Coordinates> getPoints() {
        return points;
    }

    public ModelFrame getModelFrame() {
        return modelFrame;
    }

    public void setApproxNum(int approxNum) {
        this.approxNum = approxNum;
    }
}
