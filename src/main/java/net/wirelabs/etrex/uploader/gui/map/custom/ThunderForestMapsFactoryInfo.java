package net.wirelabs.etrex.uploader.gui.map.custom;

import net.wirelabs.etrex.uploader.common.Constants;
import org.jxmapviewer.viewer.TileFactoryInfo;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

/**
 * Uses OpenStreetMap
 */
public class ThunderForestMapsFactoryInfo extends TileFactoryInfo
{
    private static final int MAX_ZOOM = 22;
    private String apiKey = Constants.EMPTY_STRING;

    /**
     * Default constructor
     */
    public ThunderForestMapsFactoryInfo(TFMapType tfMapType, String apiKey)
    {
        this("ThunderForest", tfMapType.baseUrl);
        this.apiKey = apiKey;
    }

    /**
     * @param name the name of the factory
     * @param baseURL the base URL to load tiles from
     */
    public ThunderForestMapsFactoryInfo(String name, String baseURL)
    {
        super(name,
                0, MAX_ZOOM, MAX_ZOOM,
                256, true, true,                     // tile size is 256 and x/y orientation is normal
                baseURL,
                "x", "y", "z");                        // 5/15/10.png
    }

    @Override
    public String getTileUrl(int x, int y, int zoom)
    {
        int invZoom = MAX_ZOOM - zoom;
        return this.baseURL + "/" + invZoom + "/" + x + "/" + y + ".png?apikey=" + apiKey;
    }

    @Override
    public String getAttribution() {
        return "Maps \u00A9 Thunderforest, Data \u00A9 OpenStreetMap contributors";
    }

    @Override
    public String getLicense() {
        return " CC-BY-SA 2.0";
    }



}
