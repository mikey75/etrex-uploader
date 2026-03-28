package net.wirelabs.etrex.uploader.statshunters;

import com.google.gson.Gson;
import net.wirelabs.etrex.uploader.statshunters.model.Square;
import net.wirelabs.etrex.uploader.statshunters.model.Tile;
import net.wirelabs.etrex.uploader.statshunters.model.TileData;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.StatsHuntersEmulator;
import net.wirelabs.etrex.uploader.utils.NetworkingUtils;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StatsHuntersHelperTest extends BaseTest {

    final StatsHuntersHelper helper = new StatsHuntersHelper(NetworkingUtils.getBasicHttpClient());

    @Test
    void shouldConvertTileToSquare() {

        // we'll use the top left vertex of my max square from my own stats,
        // so I can check the longitudes and latitudes of a square on my map to verify
        // the expected longitudes and attitudes I got from real map by pointing
        // at all four vertices of the very first square of my max square
        //
        // the real top square json that comes from my statshunters is:
        //    "x1": 9205,
        //    "y1": 5461,
        //    "x2": 9220,
        //    "y2": 5476
        //
        // so we use 9205,5461 as args

        QuadVertexPolygon p = QuadVertexPolygonFactory.tileToGeoPolygon(9205,5461);
        checkConversion(p);
    }


    @Test
    void shouldConvertAListOfTilesToListOfGeoSquares() {
        // we'll use  the same tile as before in a 1-element list ;)
        List<Tile> tiles = new ArrayList<>();
        Tile tile = new Tile();
        tile.setX(9205);
        tile.setY(5461);
        tiles.add(tile);

        List<QuadVertexPolygon> polyList = helper.convertTilesToGeoSquares(tiles);

        for (QuadVertexPolygon poly: polyList) {
            checkConversion(poly);
        }
    }
    @Test
    void shouldBuildFullSquareFromDiagonal() {

        Coordinate topLeft= new Coordinate(10,10);
        Coordinate bottomRight = new Coordinate(20,20);
        // easy math - square defined by diagoonal of 10,10-20,20
        // would have (10,10) (20,10) (20,20) (10,20) vertices
        QuadVertexPolygon result = QuadVertexPolygonFactory.geoDiagonalToGeoPolygon(topLeft,bottomRight);
        assertThat(result.getTopLeft().getLongitude()).isEqualTo(10);
        assertThat(result.getTopRight().getLongitude()).isEqualTo(20);
        assertThat(result.getBottomLeft().getLongitude()).isEqualTo(10);
        assertThat(result.getBottomRight().getLongitude()).isEqualTo(20);
    }

    @Test
    void shouldDownloadJson() throws IOException {
        StatsHuntersEmulator emulator = new StatsHuntersEmulator();
        emulator.start();

        Optional<String> json = helper.getStatsHuntersJson("http://localhost:"+ emulator.getListeningPort() +"/good");
        assertThat(json).isPresent();

        TileData tileData = new Gson().fromJson(json.get(), TileData.class);
        assertThat(tileData.getTiles()).isNotEmpty();
        assertThat(tileData.getSquare()).isNotNull();
        assertThat(tileData.getCluster()).isNotEmpty();
        assertThat(tileData.getRestCluster()).isNotEmpty();
        verifyLogged("[StatsHunters] Downloading tiles");
        emulator.stop();

    }

    @Test
    void isSquareInsideMaxSquare() {
        // this basically tests both the isInside... method, and convertToGeoMaxSquare
        // given max square of these
        Square maxSquare = new Square();
        maxSquare.setX1(9205);
        maxSquare.setY1(5461);
        maxSquare.setX2(9220);
        maxSquare.setY2(5476);

        // (3000,4500) should be outside,
        // but (9216,5466) should be inside
        QuadVertexPolygon outside = QuadVertexPolygonFactory.tileToGeoPolygon(3000,4500);
        QuadVertexPolygon inside = QuadVertexPolygonFactory.tileToGeoPolygon(9216, 5466);

        QuadVertexPolygon max = QuadVertexPolygonFactory.squareToMaxGeoSquare(maxSquare);
        assertThat(inside.isInside(max)).isTrue();
        assertThat(outside.isInside(max)).isFalse();
    }

    private void checkConversion(QuadVertexPolygon p) {

        // since it is (yet) a mathematically speaking square,
        // longitudes for top and bottom point pairs should be the same,
        // and latitudes for left and right points pairs should also be the same

        // allow some very tiny a-few-pixel-like error
        // (I was reading the real values from a map by mouse clicking on the vertices)
        Offset<Double> precision = Offset.offset(0.0001);
        // longitudes first
        double expectedLongitude = 22.2584;
        // left
        assertThat(p.getTopLeft().getLongitude()).isEqualTo(expectedLongitude, precision);
        assertThat(p.getBottomLeft().getLongitude()).isEqualTo(expectedLongitude, precision);
        // right
        expectedLongitude = 22.2803;
        assertThat(p.getTopRight().getLongitude()).isEqualTo(expectedLongitude, precision);
        assertThat(p.getBottomRight().getLongitude()).isEqualTo(expectedLongitude, precision);

        // now latitudes
        double expectedLatitude = 51.3306;
        // top
        assertThat(p.getTopLeft().getLatitude()).isEqualTo(expectedLatitude, precision);
        assertThat(p.getTopRight().getLatitude()).isEqualTo(expectedLatitude, precision);
        // bottom
        expectedLatitude = 51.3168;
        assertThat(p.getBottomLeft().getLatitude()).isEqualTo(expectedLatitude, precision);
        assertThat(p.getBottomRight().getLatitude()).isEqualTo(expectedLatitude, precision);
    }

}