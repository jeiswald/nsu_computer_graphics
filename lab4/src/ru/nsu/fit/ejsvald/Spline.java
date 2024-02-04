package ru.nsu.fit.ejsvald;

import java.util.ArrayList;
import java.util.List;

public class Spline {
    private final ArrayList<Coordinates> mainPoints;
    private final ArrayList<Coordinates> splinePoints;
    private final ArrayList<Coordinates> splineEdgesCoordinates;
    private final int approxNum;
    private final double[][] matrix = {{-1, 3, -3, 1}, {3, -6, 3, 0}, {-3, 0, 3, 0}, {1, 4, 1, 0}};
    private static final int MATRIX_DIV = 6;

    public Spline(List<Coordinates> mainPoints, int approxNum) {
        this.mainPoints = (ArrayList<Coordinates>) mainPoints;
        this.approxNum = approxNum;
        splinePoints = new ArrayList<>();
        splineEdgesCoordinates = new ArrayList<>();
    }

    public List<Coordinates> calculateSpline() {
        if (mainPoints.size() < 4 ) return new ArrayList<>();
        for (int k = 1; k < mainPoints.size() - 2; k++) {
            Coordinates p1 = mainPoints.get(k - 1);
            Coordinates p2 = mainPoints.get(k);
            Coordinates p3 = mainPoints.get(k + 1);
            Coordinates p4 = mainPoints.get(k + 2);

            ArrayList<Coordinates> splinePart = calculateSplinePart(p1, p2, p3, p4);
            splineEdgesCoordinates.add(new Coordinates(splinePart.get(0)));
            splinePoints.addAll(splinePart);
        }
        splineEdgesCoordinates.add(splinePoints.get(splinePoints.size() - 1));
        return splinePoints;
    }

    private ArrayList<Coordinates> calculateSplinePart(Coordinates p1, Coordinates p2, Coordinates p3, Coordinates p4) {
        double[] xPoints = {p1.getX(), p2.getX(), p3.getX(), p4.getX()};
        double[] yPoints = {p1.getY(), p2.getY(), p3.getY(), p4.getY()};
        ArrayList<Coordinates> toReturn = new ArrayList<>();
        double t = 0;
        double step = 1.0 / approxNum;
        for (int k = 0; k < approxNum + 1; k++) {
            double[] tArr = {t * t * t, t * t, t, 1};
            double[] tm = mulVectorToMatrix(tArr, matrix, 4);
            int x = (int) Math.floor(mulVectorToVector(tm, xPoints, 4) / MATRIX_DIV);
            int y = (int) Math.floor(mulVectorToVector(tm, yPoints, 4) / MATRIX_DIV);
            toReturn.add(new Coordinates(x, y, 0));
            t += step;
        }
        return toReturn;
    }

    public int calcMaxSplineSize() {
        int xMax = (int) splinePoints.get(0).getX();
        int xMin = (int) splinePoints.get(0).getX();
        int yMax = (int) splinePoints.get(0).getY();
        int yMin = (int) splinePoints.get(0).getY();
        for (Coordinates point : splinePoints) {
            xMax = Math.max((int) point.getX(), xMax);
            yMax = Math.max((int) point.getY(), yMax);
            xMin = Math.min((int) point.getX(), xMin);
            yMin = Math.min((int) point.getY(), yMin);
        }
        return Math.max(xMax - xMin, (yMax - yMin) * 2);
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

    public List<Coordinates> getSplinePoints() {
        return splinePoints;
    }

    public List<Coordinates> getSplineEdgesCoordinates() {
        return splineEdgesCoordinates;
    }

    public List<Coordinates> getMainPoints() {
        return mainPoints;
    }
}
