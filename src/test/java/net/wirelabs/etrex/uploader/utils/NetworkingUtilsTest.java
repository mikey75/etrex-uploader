package net.wirelabs.etrex.uploader.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.TestHttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static net.wirelabs.etrex.uploader.utils.NetworkingUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NetworkingUtilsTest extends BaseTest {

    @Test
    void getIps() throws UnknownHostException {
        List<InetAddress> l = getAllIpsForHost("localhost");
        assertThat(l.get(0).getHostAddress()).isEqualTo("127.0.0.1");
    }

    @Test
    void shouldThrowWhenUnknownHost() {
        assertThatThrownBy(() -> getAllIpsForHost("kaka.nonexistent.pl"))
                .isInstanceOf(UnknownHostException.class);
    }

    @Test
    void httpReachable() throws IOException {
        TestHttpServer fakeHttpServer = new TestHttpServer();
        boolean isPortReachable = isHostTcpPortReachable("localhost", fakeHttpServer.getListeningPort(), 1000);
        assertThat(isPortReachable).isTrue();
        fakeHttpServer.stop();
    }

    @Test
    void httpUnreachable()  {
        // no server - no port 80
        boolean isPortReachable = isHostTcpPortReachable("localhost", 80,1000);
        verifyLogged("localhost:80 is unreachable");
        assertThat(isPortReachable).isFalse();
    }

    @Test
    void getRandomTcpPortShouldReturnPortInValidRange() throws IOException {
        int randomPort = getRandomFreeTcpPort();
        assertThat(randomPort).isGreaterThan(1024).isLessThan(65535);
    }
}