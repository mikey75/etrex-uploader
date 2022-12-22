package net.wirelabs.etrex.uploader.gui.settings;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.map.MapType;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.Vector;
@Slf4j
public class ApiKeyManager extends JPanel {

    private final JScrollPane scrollPane = new JScrollPane();
    private final AppConfiguration configuration;
    private JTable table = new JTable();


    public ApiKeyManager(AppConfiguration configuration) {
        this.configuration  = configuration;
        setLayout(new MigLayout("insets 0 0 0 0", "[grow]", "[grow]"));
        add(scrollPane, "cell 0 0,grow");

        table.setModel(new DefaultTableModel(new Object[]{"Map","Key"},0));
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        Arrays.stream(MapType.values())
                .filter(MapType::isRequiresKey)
                .forEach(mapType -> model.addRow(new Object[]{mapType, configuration.getProperty("map.api.key."+mapType.name())}));

        scrollPane.setViewportView(table);


        SwingUtils.centerComponent(this);
    }

    public void saveApiKeys() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (Vector<?> v: model.getDataVector()) {
            MapType mapType = (MapType) v.elementAt(0);
            String apiKey = (String) v.elementAt(1);
            configuration.setProperty("map.api.key."+ mapType.name() , apiKey);
        }

    }


}
