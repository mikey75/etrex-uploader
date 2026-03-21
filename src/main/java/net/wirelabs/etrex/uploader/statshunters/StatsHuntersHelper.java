package net.wirelabs.etrex.uploader.statshunters;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.statshunters.model.Square;
import net.wirelabs.etrex.uploader.statshunters.model.Tile;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.apache.logging.log4j.util.Strings;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StatsHuntersHelper {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Converts a StatsHunter index tile (basically x,y coord) into
     * QuadVertexPolygon (i.e 4 vertices with lat/lon coords)
     *
     * @param x x index of tile
     * @param y y index of tile
     * @return Polygon of the tile
     */
    public QuadVertexPolygon tileToSquare(int x, int y) {

        final int zoom = 14;  // The StatsHunters indexes/tiles are based on standard WebMercator/OpenStreetMap grid at zoom 14.
        final double n = Math.pow(2, zoom);

        double lonW = (x / n) * 360.0 - 180.0;
        double lonE = ((x + 1) / n) * 360.0 - 180.0;

        double latN = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * y) / n)));
        double latS = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * (y + 1)) / n)));

        Coordinate topLeft = new Coordinate(lonW, latN);
        Coordinate topRight = new Coordinate(lonE, latN);
        Coordinate bottomLeft = new Coordinate(lonW, latS);
        Coordinate bottomRight = new Coordinate(lonE, latS);

        return new QuadVertexPolygon(topLeft, topRight, bottomLeft, bottomRight);

    }

    /**
     * Get max statshunters square
     *
     * @param square statshunters json response element square
     * @return QuadVertexPolygon of the max square
     */
    public QuadVertexPolygon convertSquareToGeoMaxSquare(Square square) {

        Point p1 = new Point(square.getX1(), square.getY1());
        Point p2 = new Point(square.getX2(), square.getY2());

        QuadVertexPolygon topLeft = tileToSquare(p1.x, p1.y);
        QuadVertexPolygon bottomRight = tileToSquare(p2.x, p2.y);
        return squareFromDiagonal(topLeft.topLeft(), bottomRight.bottomRight());

    }

    /**
     * Creates a square from only topLeft and bottomRight coordinates.
     * Uses math that finds missing vertices by rotating the diagonal
     *
     * @param topLeft     topLeft coordinate
     * @param bottomRight bottomRight coordinate
     * @return the QuadVertexPolygon of all four coordinates
     */
    public QuadVertexPolygon squareFromDiagonal(Coordinate topLeft, Coordinate bottomRight) {

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
     * Gets statshunters json from configured url
     *
     * @param url configured url from app config
     * @return string representing json response
     */
    public String getStatsHuntersJson(String url) {
        HttpResponse<String> response;
        try {
            log.info("[StatsHunters] Downloading tiles");
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().timeout(Duration.ofSeconds(10)).build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Strings.EMPTY; // !200 = something went wrong, so return empty.
        } catch (IOException | IllegalArgumentException ex) {
            log.error("Could not get your StatsHunters stats: {}", ex.getMessage());
            return Strings.EMPTY;
        } catch (InterruptedException ex) {
            log.error("Http client thread interrupted");
            Thread.currentThread().interrupt();
            return Strings.EMPTY;
        }
        return response.body();
    }

    /**
     * Convert statshunters tiles to 4vertex polygons
     *
     * @param allTiles list of tile indexes
     * @return List of 4vertex polygons
     */
    public List<QuadVertexPolygon> convertTilesToGeoSquares(List<Tile> allTiles) {

        List<QuadVertexPolygon> geoSquares = new ArrayList<>();

        for (Tile point : allTiles) {
            QuadVertexPolygon poly = tileToSquare(point.getX(), point.getY());
            geoSquares.add(poly);
        }
        return geoSquares;
    }

    /**
     * Checks if a square is inside the calculated max square
     *
     * @param polygon   given square in polygon format
     * @param maxSquare calculated maxSquare
     * @return true if square inside, false otherwise
     */
    public boolean isSquareInsideMaxSquare(QuadVertexPolygon polygon, QuadVertexPolygon maxSquare) {

        boolean loninside = (polygon.topLeft().getLongitude() >= maxSquare.topLeft().getLongitude() &&
                polygon.bottomRight().getLongitude() <= maxSquare.bottomRight().getLongitude());

        boolean latinside = polygon.topLeft().getLatitude() <= maxSquare.topLeft().getLatitude() &&
                polygon.bottomRight().getLatitude() >= maxSquare.bottomRight().getLatitude();

        return (loninside && latinside);
    }
}


