package net.wirelabs.etrex.uploader.strava.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkingUtils {

    public static List<InetAddress> getAllIpsForHost(String host) throws UnknownHostException {
        return Arrays.stream(InetAddress.getAllByName(host))
                .filter(Inet4Address.class::isInstance)
                .collect(Collectors.toList());
    }

    public static boolean isHostTcpHttpReachable(String host, int timeOutMillis) throws IOException {

        try (Socket soc = new Socket()) {
            soc.connect(new InetSocketAddress(host, 80), timeOutMillis);
        }
        return true;
    }
}
