package net.wirelabs.etrex.uploader.strava.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class NetworkingUtils {

    public static List<InetAddress> getAllIpsForHost(String host) throws UnknownHostException {
        return Arrays.stream(InetAddress.getAllByName(host))
                .filter(Inet4Address.class::isInstance)
                .collect(Collectors.toList());
    }

    public static boolean isHostTcpPortReachable(String host, int port, int timeOutMillis)  {

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
