package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemUtilsTest extends BaseTest {

    @Test
    void getJmapsVersionTest() {

        String ver = SystemUtils.getJmapsVerion();

        assertThat(ver)
                .isNotNull()
                .isEqualTo("1.2");
    }

}