package net.wirelabs.etrex.uploader.strava.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.FakeHttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static net.wirelabs.etrex.uploader.strava.utils.NetworkingUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NetworkingUtilsTest extends BaseTest {

    @Test
    void getIps() throws UnknownHostException {
        List<InetAddress> l = getAllIpsForHost("localhost");
        assertThat(l.get(0).getHostAddress()).isEqualTo("127.0.0.1");
    }

    @Test
    void shouldThrowWnenUnknownHost() {
        assertThatThrownBy(() -> getAllIpsForHost("kaka.nonexistent.pl"))
                .isInstanceOf(UnknownHostException.class);
    }

    @Test
    void httpReachable() throws IOException {
        FakeHttpServer fakeHttpServer = new FakeHttpServer();
        boolean b = isHostTcpPortReachable("localhost", fakeHttpServer.getListeningPort(), 1000);
        assertThat(b).isTrue();
    }

    @Test
    void httpUnreachable()  {
        boolean b = isHostTcpPortReachable("localhost", 80,1000);
        verifyLogged("localhost:80 is unreachable");
        assertThat(b).isFalse();
    }

    @Test
    void getRandomTcpPort() throws IOException {
        int port = getRandomFreeTcpPort();
        assertThat(port).isGreaterThan(1024).isLessThan(65535);
    }
}