package net.wirelabs.etrex.uploader.gui.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import lombok.NoArgsConstructor;

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