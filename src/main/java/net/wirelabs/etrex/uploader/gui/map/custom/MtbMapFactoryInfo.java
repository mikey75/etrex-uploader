package net.wirelabs.etrex.uploader.gui.map.custom;

import org.jxmapviewer.viewer.TileFactoryInfo;

/**
 * Created 8/5/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class MtbMapFactoryInfo extends TileFactoryInfo {
    private static final int MAX_ZOOM = 17;
    /*
	<url>http://tile.mtbmap.cz/mtbmap_tiles/$z/$x/$y.png</url>
    <copyright>Map data: OpenStreetMap, USGS | Rendering: Martin Tesař</copyright>
    */


    public MtbMapFactoryInfo() {
        this("MTB.cz Map", "http://tile.mtbmap.cz/mtbmap_tiles");
    }

    /**
     * @param name    the name of the factory
     * @param baseURL the base URL to load tiles from
     */
    public MtbMapFactoryInfo(String name, String baseURL) {
        super(name,
                0, MAX_ZOOM, MAX_ZOOM,
                256, true, true,                     // tile size is 256 and x/y orientation is normal
                baseURL,
                "x", "y", "z");                        // 5/15/10.png
    }

    @Override
    public String getTileUrl(int x, int y, int zoom) {
        int invZoom = MAX_ZOOM - zoom;
        return this.baseURL + "/" + invZoom + "/" + x + "/" + y + ".png";
    }

    @Override
    public String getAttribution() {
        return "Map data \u00A9 OpenStreetMap, USGS | Rendering: Martin Tesař";
    }

    @Override
    public String getLicense() {
        return " CC-BY-SA";
    }
}
