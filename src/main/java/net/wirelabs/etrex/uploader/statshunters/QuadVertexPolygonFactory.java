package net.wirelabs.etrex.uploader.statshunters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.statshunters.model.Square;
import net.wirelabs.jmaps.map.geo.Coordinate;

import java.awt.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuadVertexPolygonFactory {

    private static final int DEFAULT_EXPLORER_TILES_WGS84_ZOOM = 14;
    private static final double ALL_TILES = Math.pow(2, DEFAULT_EXPLORER_TILES_WGS84_ZOOM);
    /**
     * Calculate QuadVertexPolygon of StatsHunter/ExplorerTile coords
     * @param x x of tile
     * @param y y of tile
     * @return the resultimng geo polygon
     */
    public static QuadVertexPolygon tileToGeoPolygon(int x, int y) {


        double lonW = (x / ALL_TILES) * 360.0 - 180.0;
        double lonE = ((x + 1) / ALL_TILES) * 360.0 - 180.0;

        double latN = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * y) / ALL_TILES)));
        double latS = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * (y + 1)) / ALL_TILES)));

        Coordinate topLeft = new Coordinate(lonW, latN);
        Coordinate topRight = new Coordinate(lonE, latN);
        Coordinate bottomLeft = new Coordinate(lonW, latS);
        Coordinate bottomRight = new Coordinate(lonE, latS);

        return new QuadVertexPolygon(topLeft, topRight, bottomLeft, bottomRight);
    }

    /**
     * Calculate QuadVertexPolygon from diagonal given in topLeft and bottomRight geo coordinates.
     * Uses math that finds missing vertices by rotating the diagonal
     *
     * @param topLeft     topLeft coordinate
     * @param bottomRight bottomRight coordinate
     * @return the QuadVertexPolygon of all four coordinates
     */
    public static QuadVertexPolygon geoDiagonalToGeoPolygon(Coordinate topLeft, Coordinate bottomRight) {
        // Center of the square (middle of the diagonal)
        double mLon = (topLeft.getLongitude() + bottomRight.getLongitude()) / 2.0;
        double mLat = (topLeft.getLatitude() + bottomRight.getLatitude()) / 2.0;

        // Scale factor: converts lon degrees to same metric length as lat degrees
        double cosLat = Math.cos(Math.toRadians(mLat));

        // Half-diagonal vector
        double dx = (bottomRight.getLatitude() - topLeft.getLatitude()) / 2.0;
        double dy = (bottomRight.getLongitude() - topLeft.getLongitude()) / 2.0 * cosLat;

        // Rotate (dx, dy) by 90° -> (dx, dy) -> (-dy, dx)
        double rx = -dy;

        // Convert rotated vector data back to lon/lat degrees to get missing coords
        Coordinate topRight = new Coordinate(mLon - rx / cosLat, mLat - dx);
        Coordinate bottomLeft = new Coordinate(mLon + rx / cosLat, mLat + dx);

        return new QuadVertexPolygon(topLeft, topRight, bottomLeft, bottomRight);
    }
    /**
     * Get max statshunters square
     *
     * @param square statshunters json response element square
     * @return QuadVertexPolygon of the max square
     */
    public static QuadVertexPolygon squareToMaxGeoSquare(Square square) {

        Point p1 = new Point(square.getX1(), square.getY1());
        Point p2 = new Point(square.getX2(), square.getY2());

        QuadVertexPolygon topLeft = QuadVertexPolygonFactory.tileToGeoPolygon(p1.x, p1.y);
        QuadVertexPolygon bottomRight = QuadVertexPolygonFactory.tileToGeoPolygon(p2.x, p2.y);
        return geoDiagonalToGeoPolygon(topLeft.getTopLeft(), bottomRight.getBottomRight());

    }
}
