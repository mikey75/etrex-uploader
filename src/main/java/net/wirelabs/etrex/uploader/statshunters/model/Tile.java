package net.wirelabs.etrex.uploader.statshunters.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Tile {

    @SerializedName("x")
    private int x;

    @SerializedName("y")
    private int y;

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile)) return false;
        Tile p = (Tile) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
