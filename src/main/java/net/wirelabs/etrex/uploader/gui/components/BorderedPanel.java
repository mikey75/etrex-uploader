package net.wirelabs.etrex.uploader.gui.components;

import lombok.NoArgsConstructor;

import javax.swing.*;
import javax.swing.border.*;

@NoArgsConstructor
public abstract class BorderedPanel extends JPanel {
    
    protected BorderedPanel(String title) {
        setBorderTitle(title);
    }
    
    protected void setBorderTitle(String title) {
        Border border = new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP);
        setBorder(border);
    }

}