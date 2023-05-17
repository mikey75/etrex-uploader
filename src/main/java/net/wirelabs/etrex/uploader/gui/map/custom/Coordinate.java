package net.wirelabs.etrex.uploader.gui.map.custom;

import org.osgeo.proj4j.ProjCoordinate;

/**
 * Created 5/17/23 by Michał Szwaczko (mikey@wirelabs.net)
 * wrapper class to avoid confusion between x,y and lat/lon in conversions
 */
public class Coordinate extends ProjCoordinate {
    public Coordinate() {
        super();
    }

    public Coordinate(double longitude, double lattitude, double altitude) {
        super(longitude, lattitude, altitude);
    }

    public Coordinate(double longitude, double lattitude) {
        super(longitude, lattitude);
    }

    public void setLongitude(double longitude) {
        super.x = longitude;
    }

    public double getLongitude() {
        return super.x;
    }
    public void setLattitude(double lattitude) {
        super.y = lattitude;
    }

    public double getLattitude() {
        return super.y;
    }

}
