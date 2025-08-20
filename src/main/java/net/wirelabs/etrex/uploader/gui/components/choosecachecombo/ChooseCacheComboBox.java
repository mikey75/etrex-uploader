package net.wirelabs.etrex.uploader.gui.components.choosecachecombo;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.Constants;

import javax.swing.*;

@Getter
public class ChooseCacheComboBox extends JComboBox<String> {

    private final String[] configuredCacheTypes = new String[]{
            Constants.DIR_BASED_CACHE_TYPE,
            Constants.DB_BASED_CACHE_TYPE
    };
    private final DefaultComboBoxModel<String> chooseCacheComboBoxModel = new DefaultComboBoxModel<>(configuredCacheTypes);

    public ChooseCacheComboBox() {
        setModel(chooseCacheComboBoxModel);
    }
}
