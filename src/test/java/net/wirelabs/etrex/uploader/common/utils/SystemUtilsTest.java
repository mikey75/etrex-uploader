package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemUtilsTest extends BaseTest {

    @Test
    void getJmapsVersionTest() {

        String ver = SystemUtils.getJmapsVersionUsed();

        assertThat(ver)
                .isNotNull()
                .containsPattern("[0-9]\\.[0-9]");  //as of now version is in a format x.y where x and y are digits

    }

}