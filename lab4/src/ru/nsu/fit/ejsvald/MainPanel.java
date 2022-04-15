package ru.nsu.fit.ejsvald;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MainPanel extends JPanel implements MouseListener, MouseMotionListener {
    private BufferedImage image;
    private final ArrayList<Coordinates> points;
    private final ArrayList<Coordinates> centerPoints;
    private int xImCenter;
    private int yImCenter;
    private final double[][] matrix = {{-1, 3, -3, 1}, {3, -6, 3, 0}, {-3, 0, 3, 0}, {1, 4, 1, 0}};
    private final int matrixDiv = 6;
    private int approxNum = 5;
    private final int bigPointRad = 11;
    private final int littlePointRad = 5;
    private int activePoint = -1;


    public MainPanel(Dimension dimension) {
        setPreferredSize(dimension);
        addMouseListener(this);
        addMouseMotionListener(this);
        setImage(new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_RGB));
        points = new ArrayList<>();
        centerPoints = new ArrayList<>();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public void drawSpline() {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.drawLine(image.getWidth() / 2, 0, image.getWidth() / 2, image.getHeight());
        g.drawLine(0, image.getHeight() / 2, image.getWidth(), image.getHeight() / 2);

        drawPoints(image);

        if (points.size() < 4) {
            return;
        }

        g.setColor(Color.WHITE);

        for (int i = 1; i < points.size() - 2; i++) {
            Coordinates p1 = points.get(i - 1);
            Coordinates p2 = points.get(i);
            Coordinates p3 = points.get(i + 1);
            Coordinates p4 = points.get(i + 2);
            double[] xPoints = {p1.x + xImCenter, p2.x + xImCenter, p3.x + xImCenter, p4.x + xImCenter};
            double[] yPoints = {p1.y + yImCenter, p2.y + yImCenter, p3.y + yImCenter, p4.y + yImCenter};
            double t = 0;
            double step = 1.0 / approxNum;

            double[] tArr0 = {t * t * t, t * t, t, 1};
            double[] TM0 = mulVectorToMatrix(tArr0, matrix, 4);
            int xPrev = (int) Math.floor(mulVectorToVector(TM0, xPoints, 4) / matrixDiv);
            int yPrev = (int) Math.floor(mulVectorToVector(TM0, yPoints, 4) / matrixDiv);
            t += step;

            for (int k = 1; k < approxNum + 1; k++) {
                double[] tArr = {t * t * t, t * t, t, 1};
                double[] TM = mulVectorToMatrix(tArr, matrix, 4);
                int x = (int) Math.floor(mulVectorToVector(TM, xPoints, 4) / matrixDiv);
                int y = (int) Math.floor(mulVectorToVector(TM, yPoints, 4) / matrixDiv);
                g.drawLine(xPrev, yPrev, x, y);
                xPrev = x;
                yPrev = y;
                t += step;
            }
        }
    }

    private double[] mulVectorToMatrix(double[] vector, double[][] matrix, int size) {
        double[] vectorToReturn = new double[size];
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                vectorToReturn[j] += matrix[i][j] * vector[i];
            }
        }
        return vectorToReturn;
    }

    private double mulVectorToVector(double[] vector1, double[] vector2, int size) {
        double toReturn = 0;
        for (int i = 0; i < size; i++) {
            toReturn += vector1[i] * vector2[i];
        }
        return toReturn;
    }

    private void drawPoints(BufferedImage image) {
        if (points.size() == 0) return;
        centerPoints.clear();
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLUE);
        Coordinates prevPoint = points.get(0);
        int prevX = prevPoint.x + xImCenter;
        int prevY = prevPoint.y + yImCenter;
        g.drawOval(prevX - 5, prevY - 5, bigPointRad, bigPointRad);
        for (int i = 1; i < points.size(); i++) {
            Coordinates point = points.get(i);
            int x = point.x + xImCenter;
            int y = point.y + yImCenter;
            g.drawOval(x - 5, y - 5, bigPointRad, bigPointRad);
            int centerX = prevX + (x - prevX) / 2;
            int centerY = prevY + (y - prevY) / 2;
            centerPoints.add(new Coordinates(centerX - xImCenter, centerY - yImCenter, 0));
            g.drawOval(centerX - 2, centerY - 2, littlePointRad, littlePointRad);
            g.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    private void setImage(BufferedImage image) {
        xImCenter = image.getHeight() / 2;
        yImCenter = image.getWidth() / 2;
        this.image = image;
    }

    public void resizePanel(Dimension dimension) {
        setImage(new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB));
        setPreferredSize(dimension);
        drawSpline();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() - xImCenter;
        int y = e.getY() - yImCenter;
        if (SwingUtilities.isRightMouseButton(e)) {
            for (int i = 0; i < points.size(); i++) {
                Coordinates point = points.get(i);
                if (isOverPoint(x, y, point, bigPointRad)) {
                    points.remove(i);
                    break;
                }
            }
        } else if (points.size() < 4) {
            points.add(new Coordinates(x, y, 0));
        } else {
            for (int i = 0; i < centerPoints.size(); i++) {
                Coordinates point = centerPoints.get(i);
                if (isOverPoint(x, y, point, littlePointRad)) {
                    points.add(i + 1, new Coordinates(x, y, 0));
                    break;
                }
            }
        }

        drawSpline();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() - xImCenter;
        int y = e.getY() - yImCenter;
        for (int i = 0; i < points.size(); i++) {
            Coordinates point = points.get(i);

            if (isOverPoint(x, y, point, bigPointRad)) {
                activePoint = i;
            }
        }
    }

    private boolean isOverPoint(int x, int y, Coordinates point, int pointSize) {
        return Math.abs(x - point.x) < pointSize && Math.abs(y - point.y) < pointSize;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        activePoint = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (activePoint != -1) {
            Coordinates point = points.get(activePoint);
            point.x = e.getX() - xImCenter;
            point.y = e.getY() - yImCenter;
            drawSpline();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
