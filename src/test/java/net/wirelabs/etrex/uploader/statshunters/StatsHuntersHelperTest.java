package net.wirelabs.etrex.uploader.statshunters;

import com.google.gson.Gson;
import net.wirelabs.etrex.uploader.statshunters.model.Square;
import net.wirelabs.etrex.uploader.statshunters.model.Tile;
import net.wirelabs.etrex.uploader.statshunters.model.TileData;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.StatsHuntersEmulator;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StatsHuntersHelperTest extends BaseTest {

    StatsHuntersHelper helper = new StatsHuntersHelper();

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

        QuadVertexPolygon p = helper.tileToSquare(9205,5461);
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
        QuadVertexPolygon result = helper.squareFromDiagonal(topLeft,bottomRight);
        assertThat(result.topLeft().getLongitude()).isEqualTo(10);
        assertThat(result.topRight().getLongitude()).isEqualTo(20);
        assertThat(result.bottomLeft().getLongitude()).isEqualTo(10);
        assertThat(result.bottomRight().getLongitude()).isEqualTo(20);
    }

    @Test
    void shouldDownloadJson() throws IOException {
        StatsHuntersEmulator emulator = new StatsHuntersEmulator();
        emulator.start();

        String json = helper.getStatsHuntersJson("http://localhost:"+ emulator.getListeningPort() +"/good");
        assertThat(json).isNotEmpty();

        TileData tileData = new Gson().fromJson(json, TileData.class);
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
        QuadVertexPolygon outside = helper.tileToSquare(3000,4500);
        QuadVertexPolygon inside = helper.tileToSquare(9216, 5466);

        QuadVertexPolygon max = helper.convertSquareToGeoMaxSquare(maxSquare);
        assertThat(helper.isSquareInsideMaxSquare(inside,max)).isTrue();
        assertThat(helper.isSquareInsideMaxSquare(outside,max)).isFalse();
    }

    private static void checkConversion(QuadVertexPolygon p) {

        // since it is (yet) a mathematically speaking square,
        // longitudes for top and bottom point pairs should be the same,
        // and latitudes for left and right points pairs should also be the same

        // allow some very tiny a-few-pixel-like error
        // (I was reading the real values from a map by mouse clicking on the vertices)
        Offset<Double> precision = Offset.offset(0.0001);
        // longitudes first
        double expectedLongitude = 22.2584;
        // left
        assertThat(p.topLeft().getLongitude()).isEqualTo(expectedLongitude, precision);
        assertThat(p.bottomLeft().getLongitude()).isEqualTo(expectedLongitude, precision);
        // right
        expectedLongitude = 22.2803;
        assertThat(p.topRight().getLongitude()).isEqualTo(expectedLongitude, precision);
        assertThat(p.bottomRight().getLongitude()).isEqualTo(expectedLongitude, precision);

        // now latitudes
        double expectedLatitude = 51.3306;
        // top
        assertThat(p.topLeft().getLatitude()).isEqualTo(expectedLatitude, precision);
        assertThat(p.topRight().getLatitude()).isEqualTo(expectedLatitude, precision);
        // bottom
        expectedLatitude = 51.3168;
        assertThat(p.bottomLeft().getLatitude()).isEqualTo(expectedLatitude, precision);
        assertThat(p.bottomRight().getLatitude()).isEqualTo(expectedLatitude, precision);
    }

}