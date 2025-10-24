package net.wirelabs.etrex.uploader.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.miginfocom.layout.CC;

/**
 * Wraps CC to avoid calling new CC() in constraints setting
 * while constructing/populating components
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MigComponentConstraintsWrapper {

    /**
     * Most if not all component constraints start with cell (good practice - location first)
     * so this should only method should suffice for most cases, without increasing the code size too much by overriding
     * all methods from CC()
     * <p>
     * But if you start your constraint from something different than cell (which is possible but not good practice)
     * then your starting call should be overriden here in the same way as cell() is.
     * It is just an override of original CC() method with new CC().method so it should be easy to find and override.
     */

    public static CC cell(int... colRowWidthHeight) {
        return new CC().cell(colRowWidthHeight);
    }
}