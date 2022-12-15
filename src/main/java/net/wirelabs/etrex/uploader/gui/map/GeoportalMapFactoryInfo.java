package net.wirelabs.etrex.uploader.gui.map;

import org.jxmapviewer.viewer.TileFactoryInfo;

import java.awt.geom.Point2D;

/**
 * Uses OpenStreetMap
 */
public class GeoportalMapFactoryInfo extends TileFactoryInfo {

    private static final int MAX_ZOOM = 15;
    private static final int TILE_SIZE = 512;

    // max row/col numbers - taken from service's schema xsd
    private int[] maxRows={14538,7269,3634,1453,726,290,145,72,29,14,7,3,1,0,0,0};
    private int[] maxCols={16827,8413,4206,1682,841,336,168,84,33,16,8,4,1,0,0,0};

    

    private double[] longitudeDegrees = new double[MAX_ZOOM+1];
    private double[] longitudeRadians = new double[MAX_ZOOM+1];

    private Point2D[] mapCentersInPixels = new Point2D.Double[MAX_ZOOM+1];
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
                1, MAX_ZOOM-2, MAX_ZOOM,
                TILE_SIZE, true, true,
                baseURL,
                "x", "y", "z");


        for (int z=0; z<=MAX_ZOOM; z++) {

            int xx = ((maxCols[z]+1)*TILE_SIZE)/2;
            int yy = ((maxCols[z]+1)*TILE_SIZE)/2;

            longitudeDegrees[z] = ((maxCols[z]+1)*TILE_SIZE)/360.0;
            longitudeRadians[z] = ((maxCols[z]+1)*TILE_SIZE)/(2.0 * Math.PI);
            mapCentersInPixels[z] = new Point2D.Double(xx, yy);
        }



    }



    @Override
    public int getMapWidthInTilesAtZoom(int zoom) {
        return maxCols[zoom]+1;
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
        return "\u00A9 geoportal.gov.pl";
    }

    @Override
    public String getLicense() {
        return "OpenData Gov.pl";
    }


}