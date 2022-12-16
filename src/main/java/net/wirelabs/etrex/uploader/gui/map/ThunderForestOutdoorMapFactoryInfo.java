package net.wirelabs.etrex.uploader.gui.map;

import net.wirelabs.etrex.uploader.common.Constants;
import org.jxmapviewer.viewer.TileFactoryInfo;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */


    //https://tile.thunderforest.com/outdoors/{z}/{x}/{y}.png?apikey=???

/**
 * Uses OpenStreetMap
 */
public class ThunderForestOutdoorMapFactoryInfo extends TileFactoryInfo
{
    private static final int MAX_ZOOM = 19;
    private String apiKey = Constants.EMPTY_STRING;

    /**
     * Default constructor
     */
    public ThunderForestOutdoorMapFactoryInfo()
    {
        this("ThunderForest", "https://tile.thunderforest.com/outdoors");
    }

    public ThunderForestOutdoorMapFactoryInfo withApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * @param name the name of the factory
     * @param baseURL the base URL to load tiles from
     */
    public ThunderForestOutdoorMapFactoryInfo(String name, String baseURL)
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
        return "\u00A9 ThunderForest contributors";
    }

    @Override
    public String getLicense() {
        return "Creative Commons Attribution-ShareAlike 2.0";
    }



}
