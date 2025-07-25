package net.wirelabs.etrex.uploader.strava.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

@Slf4j
@Getter
public abstract class LocalWebServer {

    protected final int listeningPort;
    protected final HttpServer serverInstance;
    protected final String documentRoot;

    // default - server on random port, default executor, root on /
    protected LocalWebServer() throws IOException {
        this(NetworkingUtils.getRandomFreeTcpPort(),"/",null);
    }
    // server on specific port, default executor, root on /
    protected LocalWebServer(int port) throws IOException {
        this(port, "/",  null);
    }
    // fully custom webserver, with specified port, document root, and executor
    protected LocalWebServer(int port, String documentRoot, Executor executor) throws IOException {
        this.listeningPort = port;
        this.documentRoot = documentRoot;

        serverInstance = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        serverInstance.createContext(documentRoot, this::handleRequest);
        serverInstance.setExecutor(executor);
        log.info("Document root: {}", documentRoot);
    }

    public void start() {
        serverInstance.start();
        log.info("Local http server started on port {}", listeningPort);
    }

    public synchronized void stop()  {
        if (serverInstance!=null) {
            serverInstance.stop(0);
            log.info("Local http server terminated!");
        }
    }

    // default request handler does nothing
    protected void handleRequest(HttpExchange exchange) throws IOException {}


}
