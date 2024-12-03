package net.wirelabs.etrex.uploader.tools;

import fi.iki.elonen.NanoHTTPD;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.utils.NetworkingUtils;

import java.io.IOException;

@Slf4j
public class FakeHttpServer extends NanoHTTPD {

    // fake server on random port
    public FakeHttpServer() throws IOException {
        this(NetworkingUtils.getRandomFreeTcpPort());
    }

    // fake server on specific port
    public FakeHttpServer(int port) throws IOException {
        super(port);
        startServer();
    }

    private void startServer() throws IOException {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        log.info("Fake http server started on port {}", getListeningPort());
    }

    public synchronized void terminate() {
        closeAllConnections();
    }
}

