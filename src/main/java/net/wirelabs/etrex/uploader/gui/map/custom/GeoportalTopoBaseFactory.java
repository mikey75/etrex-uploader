package net.wirelabs.etrex.uploader.gui.map.custom;

import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;

import java.awt.Dimension;
import java.awt.geom.Point2D;

/**
 * Created 1/2/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class GeoportalTopoBaseFactory extends DefaultTileFactory {

    private final CRSFactory csFactory = new CRSFactory();
    private final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    private final CoordinateReferenceSystem epsg2180 = csFactory.createFromName("EPSG:2180");
    private final CoordinateReferenceSystem wgs84 = csFactory.createFromName("EPSG:4326");
    private final Coordinate src = new Coordinate();
    private final Coordinate dst = new Coordinate();
    private final CoordinateTransform epsg2180ToWgs84 = ctFactory.createTransform(epsg2180, wgs84);
    private final CoordinateTransform wgs84ToEpsg2180 = ctFactory.createTransform(wgs84, epsg2180);

    /**
     * Creates a new instance of DefaultTileFactory using the spcified TileFactoryInfo
     *
     * @param info a TileFactoryInfo to configure this TileFactory
     */
    public GeoportalTopoBaseFactory(TileFactoryInfo info) {
        super(info);

    }

    @Override
    public GeoPosition pixelToGeo(Point2D pixel, int zoom) {

        double mpx =((GeoportalTopoBaseFactoryInfo)getInfo()).getMetersPerPixelAtZoom(zoom);

        src.setLongitude(Math.abs(pixel.getX() * mpx + 100000)); // add to meridian 0
        src.setLattitude(Math.abs(pixel.getY() * mpx - 850000));
        epsg2180ToWgs84.transform(src,dst);

        return new GeoPosition(dst.getLattitude(),dst.getLongitude());

    }

    @Override
    public Point2D geoToPixel(GeoPosition c, int zoom) {

        double mpx =((GeoportalTopoBaseFactoryInfo)getInfo()).getMetersPerPixelAtZoom(zoom);

        src.setLongitude(c.getLongitude());
        src.setLattitude(c.getLatitude());
        wgs84ToEpsg2180.transform(src,dst);

        double longitude = Math.abs(dst.getLongitude() - 100000) / mpx;   // substract from meridian 0
        double lattitude = Math.abs(dst.getLattitude() - 850000) / mpx;
        return new Point2D.Double(longitude,lattitude);

    }

    @Override
    public Dimension getMapSize(int zoom) {
        GeoportalTopoBaseFactoryInfo info = (GeoportalTopoBaseFactoryInfo) getInfo();
        return new Dimension(info.getMapWidthInTilesAtZoom(zoom),
                info.getMapHeightInTilesAtZoom(zoom));
    }

}

