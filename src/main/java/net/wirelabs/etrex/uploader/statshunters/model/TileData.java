package net.wirelabs.etrex.uploader.statshunters.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TileData {

    @SerializedName("tiles")
    private List<Tile> tiles;

    @SerializedName("square")
    private Square square;

    @SerializedName("cluster")
    private List<Tile> cluster;

    @SerializedName("restCluster")
    private List<Tile> restCluster;

    public List<Tile> getTiles() { return tiles; }
    public void setTiles(List<Tile> tiles) { this.tiles = tiles; }

    public Square getSquare() { return square; }
    public void setSquare(Square square) { this.square = square; }

    public List<Tile> getCluster() { return cluster; }
    public void setCluster(List<Tile> cluster) { this.cluster = cluster; }

    public List<Tile> getRestCluster() { return restCluster; }
    public void setRestCluster(List<Tile> restCluster) { this.restCluster = restCluster; }

    @Override
    public String toString() {
        return "TileData{" +
                "tiles=" + tiles +
                ", square=" + square +
                ", cluster=" + cluster +
                ", restCluster=" + restCluster +
                "}";
    }
}
