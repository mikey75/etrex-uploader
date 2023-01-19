package net.wirelabs.etrex.uploader.gui.map.custom;

import org.jxmapviewer.viewer.TileFactoryInfo;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;

/**
 * Uses OpenStreetMap
 */
public class GeoportalMapFactoryInfo extends TileFactoryInfo {

    private static final int MAX_ZOOM = 15;
    private static final int TILE_SIZE = 512;

    // max row/col numbers - taken from service's schema xsd
    private  int[] heightInTilesAtZoom; // = {14539, 7270, 3635, 1454, 727, 291, 146, 73, 30, 15, 8, 4, 2, 1, 1, 1};
    private  int[] widthInTilesAtZoom; // = {16828, 8414, 4207, 1683, 842, 337, 169, 85, 34, 17, 9, 5, 3, 2, 1, 1};
    private  double[] metersPerPixelAtZoom; //scale = {};


    private final double[] longitudeDegrees = new double[MAX_ZOOM + 1];
    private final double[] longitudeRadians = new double[MAX_ZOOM + 1];

    private final Point2D[] mapCentersInPixels = new Point2D.Double[MAX_ZOOM + 1];

    /**
     * Default constructor
     */
    public GeoportalMapFactoryInfo() {
        this("Geoportal - Topograficzna mapa podkładowa", "http://mapy.geoportal.gov.pl/wss/service/WMTS/guest/wmts/G2_MOBILE_500?" +
                "SERVICE=WMTS&REQUEST=GetTile" +
                /* "&VERSION=1.0.0" +*/
               /* "&LAYER=G2_MOBILE_500" +
                "&STYLE=default" +
                "&FORMAT=image/png" +*/
                "&TILEMATRIXSET=EPSG:2180");
    }

    /**
     * @param name    the name of the factory
     * @param baseURL the base URL to load tiles from
     */
    public GeoportalMapFactoryInfo(String name, String baseURL) {

        super(name,
                1, MAX_ZOOM, MAX_ZOOM,
                TILE_SIZE, true, true,
                baseURL,
                "x", "y", "z");

        Capabilities c = getWMTSCapabilities();

        if (c != null) {
            Capabilities.TileMatrix[] tileMatrix = c.Contents.TileMatrixSet[0].TileMatrix;

            widthInTilesAtZoom = new int[tileMatrix.length];
            heightInTilesAtZoom = new int[tileMatrix.length];
            metersPerPixelAtZoom = new double[tileMatrix.length];
            for (int i = 0; i < tileMatrix.length; i++) {
                widthInTilesAtZoom[tileMatrix.length -1 -i] = tileMatrix[i].MatrixWidth;
                heightInTilesAtZoom[tileMatrix.length -1 -i] = tileMatrix[i].MatrixHeight;
                metersPerPixelAtZoom[tileMatrix.length-1 -i] = tileMatrix[i].ScaleDenominator * 0.00028;
            }

            for (int z = 0; z <= MAX_ZOOM; z++) {

                double xx = ((widthInTilesAtZoom[z]) * TILE_SIZE) / 2d;
                double yy = ((heightInTilesAtZoom[z]) * TILE_SIZE) / 2d;

                //longitudeDegrees[z] = ((maxCols[z])*TILE_SIZE)/360.0;
                //longitudeRadians[z] = ((maxCols[z])*TILE_SIZE)/(2.0 * Math.PI);
                mapCentersInPixels[z] = new Point2D.Double(xx, yy);
            }


        }
    }

    @Override
    public int getMapWidthInTilesAtZoom(int zoom) {
        return widthInTilesAtZoom[zoom];
    }

    @Override
    public double getLongitudeDegreeWidthInPixels(int zoom) {
        return longitudeDegrees[zoom];
    }

    @Override
    public double getLongitudeRadianWidthInPixels(int zoom) {
        return longitudeRadians[zoom];
    }

    @Override
    public Point2D getMapCenterInPixelsAtZoom(int zoom) {
        return mapCentersInPixels[zoom];
    }

    @Override
    public String getTileUrl(int x, int y, int zoom) {

        int invZoom = MAX_ZOOM - zoom;

        return this.baseURL +
                "&TILEMATRIX=EPSG:2180:" + invZoom
                +
                "&TILEROW=" + y
                +
                "&TILECOL=" + x;
    }

    @Override
    public String getAttribution() {
        return "\u00A9 Podkładowa Mapa Topograficzna, geoportal.gov.pl";
    }

    @Override
    public String getLicense() {
        return "OpenData Gov.pl";
    }




    private Capabilities getWMTSCapabilities() {
        try {
            URL url = new URL("http://mapy.geoportal.gov.pl/wss/service/WMTS/guest/wmts/G2_MOBILE_500?service=WMTS&request=GetCapabilities");
            Capabilities c = Capabilities.parse(url.openStream());
            return c;
        } catch (IOException e) {

        }
        return null;
    }


    public double getMetersPerPixelAtZoom(int zoomLevel) {
        return metersPerPixelAtZoom[zoomLevel];
    }
}