package net.wirelabs.etrex.uploader.gui.map.custom;

/*
 * Created 12/22/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public enum TFMapType {

    TF_CYCLE("https://tile.thunderforest.com/cycle/"),
    TF_LANDSCAPE("https://tile.thunderforest.com/landscape/"),
    TF_OUTDOORS("https://tile.thunderforest.com/outdoors/");

    final String baseUrl;

    TFMapType(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
