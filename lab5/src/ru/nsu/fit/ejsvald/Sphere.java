package ru.nsu.fit.ejsvald;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Sphere extends Figure {
    private final int circlesNumber;

    private List<List<Coordinates>> verticalCircles;

    private Coordinates centerPosition;

    private final int rad;

    public Sphere(Coordinates centerPosition, int rad, int circlesNumber) {
        this.circlesNumber = circlesNumber;
        this.centerPosition = centerPosition;
        this.rad = rad;
        verticalCircles = new LinkedList<>();
        polygons = new LinkedList<>();
    }

    private void generateSphere() {
        for (int i = 0; i < circlesNumber + 1; i++) {
            generateCircle(new Coordinates(centerPosition.x, centerPosition.y, centerPosition.z), rad,
                    0, (Math.PI / circlesNumber) * i, 0); //vertical circle
        }
        Iterator<List<Coordinates>> circlesIter = verticalCircles.iterator();
        if (!circlesIter.hasNext()) return;
        List<Coordinates> circle1 = circlesIter.next();
        while (circlesIter.hasNext()) {
            List<Coordinates> circle2 = circlesIter.next();

            Iterator<Coordinates> circle1Iter = circle1.iterator();
            Iterator<Coordinates> circle2Iter = circle2.iterator();

            Coordinates point1, point2, point3, point4;
            point3 = circle1Iter.next();
            point2 = circle2Iter.next();

            for (int i = 0; i < (circle1.size() - 1) / 2; i++) {
                point4 = circle1Iter.next();
                point1 = circle2Iter.next();
                polygons.add(new Polygon4(point1, point2, point3, point4));
                point3 = point4;
                point2 = point1;
            }

            for (int i = 0; i < (circle1.size() - 1) / 2; i++) {
                point1 = circle1Iter.next();
                point4 = circle2Iter.next();
                polygons.add(new Polygon4(point1, point2, point3, point4));
                point2 = point1;
                point3 = point4;
            }
            circle1 = circle2;
        }
    }

    private void generateCircle(Coordinates coordinates, int rad, double xRot, double yRot, double zRot) {
        double angle = 360.0 / (circlesNumber * 2) * Math.PI / 180;
        LinkedList<Coordinates> circle = new LinkedList<>();
        verticalCircles.add(circle);
        Coordinates firstPoint = new Coordinates(0, rad, 0);
        firstPoint.rotateY(yRot).rotateZ(zRot).rotateX(xRot)
                .translate(coordinates.x, coordinates.y, coordinates.z);
        circle.add(firstPoint);

        for (int i = 1; i < (circlesNumber * 2) + 1; i++) {
            Coordinates point = new Coordinates(0, rad, 0);
            circle.add(point);
            point.rotateX(i * angle).rotateY(yRot).rotateZ(zRot).rotateX(xRot)
                    .translate(coordinates.x, coordinates.y, coordinates.z);
        }
    }

    public List<Polygon4> getPolygons() {
        if (polygons.isEmpty()) generateSphere();
        return polygons;
    }
}
