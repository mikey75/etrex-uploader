package net.wirelabs.etrex.uploader.gui.components;

import lombok.NoArgsConstructor;

import javax.swing.*;
import javax.swing.border.*;

@NoArgsConstructor
public class BorderedPanel extends JPanel {
    
    public BorderedPanel(String title) {
        setBorderTitle(title);
    }
    
    protected void setBorderTitle(String title) {
        Border border = new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP);
        setBorder(border);
    }

}