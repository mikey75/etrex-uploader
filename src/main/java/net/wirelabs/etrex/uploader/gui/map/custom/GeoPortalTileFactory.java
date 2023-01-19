package net.wirelabs.etrex.uploader.gui.map.custom;

import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

import java.awt.Dimension;
import java.awt.geom.Point2D;

/**
 * Created 1/2/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class GeoPortalTileFactory extends DefaultTileFactory {
    /**
     * Creates a new instance of DefaultTileFactory using the spcified TileFactoryInfo
     *
     * @param info a TileFactoryInfo to configure this TileFactory
     */
    public GeoPortalTileFactory(TileFactoryInfo info) {
        super(info);

    }



    @Override
    public int getTileSize(int zoom) {
        return 512;
    }

    @Override
    public Dimension getMapSize(int zoom) {
        return new Dimension(getInfo().getMapWidthInTilesAtZoom(zoom),getInfo().getMapWidthInTilesAtZoom(zoom));
    }

    @Override
    public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom) {

        double x = pixelCoordinate.getX() * ((GeoportalMapFactoryInfo)getInfo()).getMetersPerPixelAtZoom(zoom);
        double y = pixelCoordinate.getY() * ((GeoportalMapFactoryInfo)getInfo()).getMetersPerPixelAtZoom(zoom);

        x = Math.abs(x - 100000 - getMapSize(zoom).width);//-100000);
        y = Math.abs(y - 850000 - getMapSize(zoom).height);//-850000);

        ProjCoordinate src = new ProjCoordinate(x,y);
        ProjCoordinate dst = new ProjCoordinate();
        CoordinateTransform transformer = ctFactory.createTransform(EPSG2180,WGS84);
        transformer.transform(src,dst);
        return new GeoPosition(dst.x,dst.y); //super.pixelToGeo(pixelCoordinate, zoom);
    }

    @Override
    public Point2D geoToPixel(GeoPosition c, int zoomLevel) {

        ProjCoordinate src = new ProjCoordinate(c.getLongitude(),c.getLatitude());
        ProjCoordinate dst = new ProjCoordinate();
        CoordinateTransform transformer = ctFactory.createTransform(WGS84, EPSG2180);
        transformer.transform(src,dst);

        double x = Math.abs(dst.x-100000) / ((GeoportalMapFactoryInfo)getInfo()).getMetersPerPixelAtZoom(zoomLevel);
        double y = Math.abs(dst.y-850000) / ((GeoportalMapFactoryInfo)getInfo()).getMetersPerPixelAtZoom(zoomLevel);
        return new Point2D.Double(x,y);
       // return transform(ersuper.geoToPixel(c, zoomLevel);
    }

    CRSFactory csFactory = new CRSFactory();
    CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    CoordinateReferenceSystem EPSG2180 = csFactory.createFromName("EPSG:2180");
    CoordinateReferenceSystem WGS84 = csFactory.createFromName("EPSG:4326");

    //CoordinateTransform transformer = ctFactory.createTransform(WGS84, EPSG2180);
    /*
    Point2D.Double transform(double x, double y, int zoomLevel) {

        ProjCoordinate src = new ProjCoordinate(x,y);
        ProjCoordinate dst = new ProjCoordinate();
        transformer.transform(src,dst);

        double tileWidthInMeters = ((GeoportalMapFactoryInfo)getInfo()).getTileSizeInMetersAtZoom(zoomLevel);
        double metersToTilesx = (dst.x/tileWidthInMeters);
        double metersToTilesy = (dst.y/tileWidthInMeters);
        double finalX= metersToTilesx*512d;
        double finalY = metersToTilesy*512d;
        Point2D.Double pixel = new Point2D.Double(finalX,finalY);
        return pixel;
    }*/
}
