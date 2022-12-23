package net.wirelabs.etrex.uploader.gui.map.custom;

import org.jxmapviewer.viewer.TileFactoryInfo;

/*
 * Created 12/23/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class OSMTopoMapFactoryInfo extends TileFactoryInfo {
    private static final int MAX_ZOOM = 15;
    /*
    {a|b|c}.tile.opentopomap.org/{z}/{x}/{y}.png
     */

    public OSMTopoMapFactoryInfo()
    {
        this("ThunderForest","https://tile.opentopomap.org");
    }
    /**
     * @param name the name of the factory
     * @param baseURL the base URL to load tiles from
     */
    public OSMTopoMapFactoryInfo(String name, String baseURL)
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
        return this.baseURL + "/" + invZoom + "/" + x + "/" + y + ".png";
    }

    @Override
    public String getAttribution() {
        return "Map data  \u00A9 OpenStreetMap contributors, SRTM | Map style: \u00A9 OpenTopoMap";
    }

    @Override
    public String getLicense() {
        return " CC-BY-SA";
    }
}
