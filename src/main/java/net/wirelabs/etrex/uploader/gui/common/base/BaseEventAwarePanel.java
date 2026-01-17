package net.wirelabs.etrex.uploader.gui.common.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.eventbus.swing.EventAwarePanel;

import javax.swing.border.*;

/**
 * Base class for Swing panels that are event-aware and optionally provide
 * standardized layout and border initialization.
 *
 * <p>
 * This class supports two usage patterns:
 *     - Opt-in convenience initialization via protected constructors that install MigLayout and optionally a titled border.
 *     - Full subclass control via the no-argument constructor, which intentionally leaves layout and border unset.
 *       Subclasses using this constructor are responsible for configuring their own layout and border.
 * <p>
 *
 * The provided instance is a convenience for subclasses that choose to use it;
 * it is not installed automatically when using the no-arg constructor.
 * <p>
 *
 * This class does not add components or behavior beyond initialization support
 * and event awareness. Subclasses are expected to define UI structure and behavior.
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEventAwarePanel extends EventAwarePanel implements LayoutConstraintsSettable {
    // Convenience MigLayout instance for subclasses that opt into it (default layout grid 1x1 fill all space)
    protected final MigLayout layout = new MigLayout("","[grow]","[grow]");

    // no borderTitle present, constraints specified
    protected BaseEventAwarePanel(String layoutConstraints, String columnConstraints, String rowConstraints) {
        setConstraints(layout, layoutConstraints,columnConstraints,rowConstraints);
        setLayout(layout);
    }
    // borderTitle present, constraints specified
    protected BaseEventAwarePanel(String title, String layoutConstraints, String columnConstraints, String rowConstraints) {
        this(layoutConstraints, columnConstraints, rowConstraints);
        setBorderTitle(title);
    }
    // borderTitle present, constraints not specified (means: default constraints)
    protected BaseEventAwarePanel(String title) {
        setBorderTitle(title);
        setLayout(layout);
    }

    protected void setBorderTitle(String title) {
        Border border = new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP);
        setBorder(border);
    }
}
