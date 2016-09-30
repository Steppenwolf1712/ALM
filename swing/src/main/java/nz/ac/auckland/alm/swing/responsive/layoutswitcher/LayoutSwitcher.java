package nz.ac.auckland.alm.swing.responsive.layoutswitcher;

import java.awt.*;

/**
 * Created by Marc Janﬂen on 29.09.2016.
 */
public interface LayoutSwitcher {

    void addPoint(Dimension prefSize);

    Dimension getMinimum();

    Dimension calcIdealPreferredSize(Dimension toCompare);

    String getDescribtion();
}
