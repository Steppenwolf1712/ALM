package nz.ac.auckland.alm.swing.responsive.graph.points;


import nz.ac.auckland.alm.swing.responsive.graph.ResponsiveGUIGraph;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marc Janßen on 09.07.2015.
 */
public class View_Point extends Abstract_Graph_Point {

    private Dimension m_dimension = null;
    private ResponsiveGUIGraph m_parent = null;

    public View_Point(ResponsiveGUIGraph parent, Dimension dim) {
        m_parent = parent;
        m_dimension = dim;

        this.color_Point = new Color(150,150,150);
        this.color_Point_Highlighted = new Color(180,180,180);
    }

    @Override
    public Dimension getDesiredSize() {
        return m_dimension;
    }

    @Override
    public JFrame showGUI(Point p) {
        JFrame erg = m_parent.getGUIAtSize(p);
        erg.setVisible(false);
        erg.getContentPane().setPreferredSize(m_dimension);
        erg.pack();
        int x = (int)(p.getX() - (erg.getWidth() / 2)<=0?0:p.getX() - (erg.getWidth() / 2));
        int y = (int)(p.getY() - (erg.getHeight() / 2)<=0?0:p.getY() - (erg.getHeight() / 2));
        erg.setLocation(x, y);
        erg.setResizable(false);
        erg.setVisible(true);
        return erg;
    }

    public JFrame getGUI() {
        return null;
    }
}
