package net.wirelabs.etrex.uploader.gui.map;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

import lombok.Getter;

@Getter
public enum MapType {

    OPENSTREETMAP("OpenStreetMap", false),
    OSM_TOPO("OpenTopoMap", false), //disabled, very slow
    TF_OUTDOOR("Outdoor" ,true),
    TF_CYCLE("Cycling" ,true),
    TF_LANDSCAPE("Landscape",true),
    GEOPORTAL("GeoPortal Topo (Poland only)", false),
    VIRTEARTH("Virtual Earth Satellite", false),
    MTB_CZ("MTB.cz", false);

    @Override
    public String toString() {
        return desc;
    }

    private final String desc;
    private final boolean requiresKey;

    MapType(String desc, boolean requiresKey) {

        this.desc = desc;
        this.requiresKey = requiresKey;
    }
}
