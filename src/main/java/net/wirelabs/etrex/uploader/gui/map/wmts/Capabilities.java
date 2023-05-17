package net.wirelabs.etrex.uploader.gui.map.wmts;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import java.beans.Transient;
import java.util.regex.Pattern;

@XmlRootElement(name = "Capabilities", namespace = Capabilities.WMTS)
public class Capabilities {

	static final String WMTS = "http://www.opengis.net/wmts/1.0";
	static final String OWS = "http://www.opengis.net/ows/1.1";

	@XmlElement(namespace = WMTS)
	public Contents Contents;

	public static class Contents {
		@XmlElement(namespace = WMTS)
		public Layer Layer;
		@XmlElement(namespace = WMTS)
		public TileMatrixSet[] TileMatrixSet;

		@Transient
		public TileMatrixSet getTileMatrixSetByCRS(Pattern crs) {
			for (Capabilities.TileMatrixSet set : TileMatrixSet) {
				if (crs.matcher(set.SupportedCRS).matches()) {
					return set;
				}
			}
			throw new IllegalArgumentException("CRS not supported:" + crs);
		}
	}
	/*
	<ows:WGS84BoundingBox>
				<ows:LowerCorner>13.800 48.800</ows:LowerCorner>
				<ows:UpperCorner>24.400 55.000</ows:UpperCorner>
			</ows:WGS84BoundingBox>
	 */
	public static class WGS84BoundingBox {
		@XmlElement(namespace = OWS)
		String LowerCorner;
		@XmlElement(namespace = OWS)
		String UpperCorner;
	}

	public static class Layer {
		@XmlElement(namespace = OWS)
		public String Identifier;
		@XmlElement(namespace = WMTS)
		public TileMatrixSetLink[] TileMatrixSetLink;
		@XmlElement(namespace = OWS)
		public WGS84BoundingBox WGS84BoundingBox;
		public TileMatrixSetLink getTileMatrixSet(String set) {
			for (TileMatrixSetLink link : TileMatrixSetLink) {
				if (set.equals(link.TileMatrixSet)) {
					return link;
				}
			}
			throw new IllegalArgumentException("TileMatrixSetLink not found: " + set);
		}
	}

	public static class TileMatrixSetLink {
		@XmlElement(namespace = WMTS)
		public String TileMatrixSet;
		@XmlElement(namespace = WMTS)
		public TileMatrixSetLimits TileMatrixSetLimits;

	}

	public static class TileMatrixSetLimits {
		@XmlElement(namespace = WMTS)
		public TileMatrixLimits[] TileMatrixLimits;
	}

	public static class TileMatrixLimits {
		@XmlElement(namespace = WMTS)
		public String TileMatrix;
		@XmlElement(namespace = WMTS)
		public int MinTileRow;
		@XmlElement(namespace = WMTS)
		public int MaxTileRow;
		@XmlElement(namespace = WMTS)
		public int MinTileCol;
		@XmlElement(namespace = Capabilities.WMTS)
		public int MaxTileCol;
	}

	public static class TileMatrixSet {
		@XmlElement(namespace = OWS)
		public String Identifier;
		@XmlElement(namespace = OWS)
		public String SupportedCRS;
		@XmlElement(namespace = WMTS)
		public TileMatrix[] TileMatrix;
	}

	public static class TileMatrix {
		@XmlElement(namespace = OWS)
		public String Identifier;
		@XmlElement(namespace = WMTS)
		public double ScaleDenominator;
		@XmlElement(namespace = WMTS)
		@XmlList
		public double[] TopLeftCorner;
		@XmlElement(namespace = WMTS)
		public int TileWidth;
		@XmlElement(namespace = WMTS)
		public int TileHeight;
		@XmlElement(namespace = WMTS)
		public int MatrixWidth;
		@XmlElement(namespace = WMTS)
		public int MatrixHeight;
	}
}