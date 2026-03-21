package net.wirelabs.etrex.uploader.statshunters;

import net.wirelabs.jmaps.map.geo.Coordinate;

public record QuadVertexPolygon(Coordinate topLeft, Coordinate topRight, Coordinate bottomLeft, Coordinate bottomRight) {}
