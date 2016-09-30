package nz.ac.auckland.alm.swing.responsive.layoutswitcher;

import nz.ac.auckland.alm.swing.responsive.graph.Vector2D;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Marc Janﬂen on 29.09.2016.
 */
public class DelaunySwitcher implements LayoutSwitcher {

    private ArrayList<Dimension> points = null;

    public DelaunySwitcher() {
        points = new ArrayList<Dimension>();
    }


    @Override
    public void addPoint(Dimension prefSize) {
        points.add(prefSize);
    }

    @Override
    public Dimension getMinimum() {
        return new Dimension(0, 0);
    }

    @Override
    public Dimension calcIdealPreferredSize(Dimension toCompare) {
        Dimension erg = null;
        double distance = Double.MAX_VALUE, tempDistance;
        for (Dimension entry: points) {
            if (erg == null) {
                distance = new Vector2D(toCompare).distance(new Vector2D(entry));
                erg = entry;
            } else {
                tempDistance = new Vector2D(toCompare).distance(new Vector2D(entry));
                if (tempDistance<distance) {
                    distance = tempDistance;
                    erg = entry;
                }
            }
        }
        return erg;
    }

    @Override
    public String getDescribtion() {
        return "Normal-Voronoi";
    }

    public String toString() {
        return getDescribtion();
    }
}
