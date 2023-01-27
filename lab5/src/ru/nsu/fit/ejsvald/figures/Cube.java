package ru.nsu.fit.ejsvald.figures;

import ru.nsu.fit.ejsvald.Coordinates;
import ru.nsu.fit.ejsvald.Polygon4;

import java.util.LinkedList;
import java.util.List;

public class Cube extends Figure {

    private List<Coordinates> points;

    private int edgeLength;

    private Coordinates centerPosition;

    public Cube(Coordinates centerPosition, int edgeLength) {
        points = new LinkedList<>();
        polygons = new LinkedList<>();
        this.edgeLength = edgeLength;
        this.centerPosition = centerPosition;
    }

    public void generateCube() {
        int coordinate = edgeLength / 2;
        points.add(new Coordinates(centerPosition.x - coordinate, centerPosition.y - coordinate, centerPosition.z - coordinate));
        points.add(new Coordinates(centerPosition.x - coordinate, centerPosition.y + coordinate, centerPosition.z - coordinate));
        points.add(new Coordinates(centerPosition.x + coordinate, centerPosition.y + coordinate, centerPosition.z - coordinate));
        points.add(new Coordinates(centerPosition.x + coordinate, centerPosition.y - coordinate, centerPosition.z - coordinate));

        points.add(new Coordinates(centerPosition.x - coordinate, centerPosition.y - coordinate, centerPosition.z + coordinate));
        points.add(new Coordinates(centerPosition.x - coordinate, centerPosition.y + coordinate, centerPosition.z + coordinate));
        points.add(new Coordinates(centerPosition.x + coordinate, centerPosition.y + coordinate, centerPosition.z + coordinate));
        points.add(new Coordinates(centerPosition.x + coordinate, centerPosition.y - coordinate, centerPosition.z + coordinate));

        polygons.add(new Polygon4(points.get(0), points.get(1), points.get(2), points.get(3)));
        polygons.add(new Polygon4(points.get(7), points.get(6), points.get(5), points.get(4)));
        polygons.add(new Polygon4(points.get(3), points.get(2), points.get(6), points.get(7)));
        polygons.add(new Polygon4(points.get(4), points.get(5), points.get(1), points.get(0)));
        polygons.add(new Polygon4(points.get(1), points.get(5), points.get(6), points.get(2)));
        polygons.add(new Polygon4(points.get(4), points.get(0), points.get(3), points.get(7)));
    }

    public List<Polygon4> getPolygons() {
        generateCube();
        return polygons;
    }
}
