package net.wirelabs.etrex.uploader.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class NetworkingUtils {

    public static List<InetAddress> getAllIpsForHost(String host) throws UnknownHostException {
        if (host == null || host.trim().isEmpty()) {
            log.error("Host cannot be null or empty");
            return Collections.emptyList();
        }
        return Arrays.stream(InetAddress.getAllByName(host))
                .filter(Inet4Address.class::isInstance)
                .toList();
    }

    public static boolean isHostTcpPortReachable(String host, int port, int timeOutMillis)  {
        if (port < 1 || port > 65535) {
            log.error("Port number illegal. Must be between 1 and 65535. Was: " + port);
            return false;
        }
        try (Socket soc = new Socket()) {
            soc.connect(new InetSocketAddress(host, port), timeOutMillis);
        } catch (IOException e) {
            log.warn("{}:{} is unreachable", host, port);
            return false;
        }
        return true;
    }

    public static int getRandomFreeTcpPort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
