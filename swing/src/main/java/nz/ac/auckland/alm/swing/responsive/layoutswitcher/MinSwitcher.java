package nz.ac.auckland.alm.swing.responsive.layoutswitcher;

import nz.ac.auckland.alm.swing.responsive.graph.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc Janﬂen on 29.09.2016.
 */
public class MinSwitcher implements LayoutSwitcher {

    private ArrayList<Integer> widths;
    private Map<Integer, ArrayList<Dimension>> dimensions;

    private int min_X = Integer.MAX_VALUE;
    private int min_Y = Integer.MAX_VALUE;


    public MinSwitcher() {
        widths = new ArrayList<Integer>();
        dimensions = new HashMap<Integer, ArrayList<Dimension>>();
    }

    @Override
    public void addPoint(Dimension prefSize) {
        int x = prefSize.width;
        int y = prefSize.height;
        if (dimensions.containsKey(x)) {
            ArrayList<Dimension> temp = dimensions.get(x);
            temp.add(prefSize);
            dimensions.put(x, temp);
        } else {
            if (min_X > x)
                min_X = x;
            if (min_Y > y)
                min_Y = y;

            widths.add(x);
            widths.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            ArrayList<Dimension> temp = new ArrayList<Dimension>();
            temp.add(prefSize);
            dimensions.put(x, temp);
        }
    }

    @Override
    public Dimension getMinimum() {
       return new Dimension(min_X, min_Y);
    }

    private Dimension getMinimumDimension() {
        ArrayList<Dimension> temp = dimensions.get(min_X);

        Dimension minima = temp.get(0), pointer = null;
        for (int i = 1; i<temp.size(); i++) {
            pointer = temp.get(i);
            if (minima.height > pointer.height)
                minima = pointer;
        }

        return minima;
    }

    @Override
    public Dimension calcIdealPreferredSize(Dimension toCompare) {
        int x = toCompare.width;
        int y = toCompare.height;
        Dimension erg = null;

        ArrayList<Dimension> save = new ArrayList<Dimension>();
        for (int i = 0; i<widths.size() && widths.get(i)<x; i++) {

            for (Dimension dim: dimensions.get(widths.get(i))){
                if (dim.height < y)
                    save.add(dim);
            }
        }

        Vector2D toComp = new Vector2D(toCompare), pointer = null;
        double distance = Double.MAX_VALUE, temp_distance;
        for (Dimension dim: save) {
            pointer = new Vector2D(dim);
            temp_distance = toComp.distance(pointer);
            if (distance>temp_distance) {
                distance = temp_distance;
                erg = dim;
            }
            if (distance == temp_distance) {
                if (erg.width < dim.width)
                    erg = dim;
            }
        }

        if (erg == null)
            return getMinimumDimension();
        return erg;
    }

    @Override
    public String getDescribtion() {
        return "Minimal-Voronoi";
    }

    public String toString() {
        return getDescribtion();
    }
}
