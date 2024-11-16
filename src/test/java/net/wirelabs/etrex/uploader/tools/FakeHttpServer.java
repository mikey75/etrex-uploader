package net.wirelabs.etrex.uploader.tools;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

public class FakeHttpServer extends NanoHTTPD {
    public FakeHttpServer(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }
}

