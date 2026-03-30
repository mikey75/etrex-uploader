package net.wirelabs.etrex.uploader.statshunters;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.statshunters.model.Tile;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
public class StatsHuntersHelper {

    private final HttpClient httpClient;

    public StatsHuntersHelper(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    /**
     * Gets statshunters json from configured url
     *
     * @param url configured url from app config
     * @return Optional of string representing json response
     */
    public Optional<String> getStatsHuntersJson(String url) {
        HttpResponse<String> response;
        try {
            log.info("[StatsHunters] Downloading tiles");
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().timeout(Duration.ofSeconds(10)).build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Optional.empty(); // !200 = something went wrong, so return empty.
        } catch (IOException | IllegalArgumentException ex) {
            log.error("Could not get your StatsHunters stats: {}", ex.getMessage());
            return Optional.empty();
        } catch (InterruptedException ex) {
            log.error("Http client thread interrupted");
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
        return Optional.of(response.body());
    }

    /**
     * Convert statshunters tiles to 4vertex polygons
     *
     * @param allTiles list of tile indexes
     * @return List of 4vertex polygons
     */
    public List<QuadVertexPolygon> convertTilesToGeoSquares(List<Tile> allTiles) {
        return allTiles.stream().map(tile -> QuadVertexPolygonFactory.tileToGeoPolygon(tile.getX(), tile.getY())).toList();
    }
}


