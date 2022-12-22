package net.wirelabs.etrex.uploader.gui.map;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

import lombok.Getter;

public enum MapType {

    OPENSTREETMAP("OpenStreetMap"),
    OUTDOOR("Outdoor - requires API key"),
    CYCLE("Cycling - requires API key"),
    GEOPORTAL("GeoPortal Topo (Poland only)"),
    VIRTEARTH("Virtual Earth Satellite");

    @Override
    public String toString() {
        return desc;
    }

    @Getter
    private final String desc;

    MapType(String desc) {

        this.desc = desc;
    }
}
