package net.wirelabs.etrex.uploader.gui.map.custom;

import com.squareup.okhttp.HttpUrl;
import net.wirelabs.etrex.uploader.gui.map.wmts.Capabilities;
import net.wirelabs.etrex.uploader.gui.map.wmts.WMTSUtil;
import org.jxmapviewer.viewer.TileFactoryInfo;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Uses OpenStreetMap
 */
public class GeoportalTopoBaseFactoryInfo extends TileFactoryInfo {

    private static final String WMTS_SERVICE_URL = "http://mapy.geoportal.gov.pl/wss/service/WMTS/guest/wmts/G2_MOBILE_500";
    public static final Capabilities capabilities = WMTSUtil.getCapabilities(WMTS_SERVICE_URL);

    private static final int MAX_ZOOM = capabilities.Contents.TileMatrixSet[0].TileMatrix.length -1;
    private static final int TILE_SIZE = capabilities.Contents.TileMatrixSet[0].TileMatrix[0].TileWidth;


    ArrayList<Integer> heightInTilesAtZoom = new ArrayList<>();
    ArrayList<Integer> widthInTilesAtZoom = new ArrayList<>();
    ArrayList<Double> metersPerPixelAtZoom = new ArrayList<>();

    private final Point2D[] mapCentersInPixels = new Point2D.Double[MAX_ZOOM + 1];

    /**
     * Default constructor
     */
    public GeoportalTopoBaseFactoryInfo() {
        this("Geoportal - Topograficzna mapa podkładowa",
                HttpUrl.parse(WMTS_SERVICE_URL)
                        .newBuilder()
                        .addQueryParameter("Service", "WMTS")
                        .addQueryParameter("Request", "GetTile")
                        .addQueryParameter("Layer", capabilities.Contents.Layer.Identifier)
                        .addQueryParameter("TileMatrixSet", capabilities.Contents.TileMatrixSet[0].Identifier)
                        .toString());
    }

    /**
     * @param name    the name of the factory
     * @param baseURL the base URL to load tiles from
     */
    public GeoportalTopoBaseFactoryInfo(String name, String baseURL) {

        super(name,1, MAX_ZOOM, MAX_ZOOM,
                TILE_SIZE, true, true,
                baseURL, "x", "y", "z");

        prepareParametersFromCapabilities();
    }

    private void prepareParametersFromCapabilities() {
        if (capabilities != null) {
            Capabilities.TileMatrix[] tileMatrix = capabilities.Contents.TileMatrixSet[0].TileMatrix;

            for (Capabilities.TileMatrix matrixElement : tileMatrix) {
                widthInTilesAtZoom.add(matrixElement.MatrixWidth);
                heightInTilesAtZoom.add(matrixElement.MatrixHeight);
                metersPerPixelAtZoom.add(matrixElement.ScaleDenominator * 0.00028);
            }

            // geoportal has reversed scale - 0 means smallest map, when jxmpaviewer has 0 at biggest
            Collections.reverse(widthInTilesAtZoom);
            Collections.reverse(heightInTilesAtZoom);
            Collections.reverse(metersPerPixelAtZoom);

            for (int z = 0; z <= MAX_ZOOM; z++) {

                double xx = (widthInTilesAtZoom.get(z) * TILE_SIZE) / 2.0;
                double yy = (heightInTilesAtZoom.get(z) * TILE_SIZE) / 2.0;

                mapCentersInPixels[z] = new Point2D.Double(xx, yy);
            }

        }
    }

    @Override
    public int getMapWidthInTilesAtZoom(int zoom) {
        return widthInTilesAtZoom.get(zoom);
    }
    public int getMapHeightInTilesAtZoom(int zoom) {
        return heightInTilesAtZoom.get(zoom);
    }
    @Override
    public Point2D getMapCenterInPixelsAtZoom(int zoom) {
        return mapCentersInPixels[zoom];
    }

    @Override
    public String getTileUrl(int x, int y, int zoom) {

        int invZoom = MAX_ZOOM - zoom;

        return HttpUrl.parse(baseURL)
                .newBuilder()
                .addQueryParameter("TileMatrix", capabilities.Contents.TileMatrixSet[0].Identifier + ":" +invZoom)
                .addQueryParameter("TileRow", String.valueOf(y))
                .addQueryParameter("TileCol", String.valueOf(x))
                .toString();
    }

    @Override
    public String getAttribution() {
        return "\u00A9 Podkładowa Mapa Topograficzna, geoportal.gov.pl";
    }

    @Override
    public String getLicense() {
        return "OpenData Gov.pl";
    }

    public double getMetersPerPixelAtZoom(int zoomLevel) {
        return metersPerPixelAtZoom.get(zoomLevel);
    }

}