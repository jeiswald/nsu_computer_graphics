package ru.nsu.fit.ejsvald;

import java.util.List;

public abstract class Figure {
    protected List<Polygon4> polygons;

    public abstract List<Polygon4> getPolygons();
}
