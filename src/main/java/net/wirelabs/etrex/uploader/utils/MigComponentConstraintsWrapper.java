package net.wirelabs.etrex.uploader.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.miginfocom.layout.CC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MigComponentConstraintsWrapper {

    public static CC cc() {
        return new CC();
    }
}