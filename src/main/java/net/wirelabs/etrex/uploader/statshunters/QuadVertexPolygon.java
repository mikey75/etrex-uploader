package net.wirelabs.etrex.uploader.statshunters;

import lombok.*;
import net.wirelabs.jmaps.map.geo.Coordinate;


@Getter
@AllArgsConstructor
public class QuadVertexPolygon {

    private Coordinate topLeft;
    private Coordinate topRight;
    private Coordinate bottomLeft;
    private Coordinate bottomRight;

    /**
     * Checks if a QVPolygon is inside another
     *
     * @param another another square
     * @return true if square inside, false otherwise
     */
    public boolean isInside(QuadVertexPolygon another) {

        boolean loninside = (topLeft.getLongitude() >= another.getTopLeft().getLongitude() &&
                bottomRight.getLongitude() <= another.getBottomRight().getLongitude());

        boolean latinside = topLeft.getLatitude() <= another.getTopLeft().getLatitude() &&
                bottomRight.getLatitude() >= another.getBottomRight().getLatitude();

        return (loninside && latinside);
    }
}
