package nz.ac.auckland.alm.swing.responsive.graph.points;


import nz.ac.auckland.alm.swing.responsive.graph.ResponsiveGUIGraph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Marc Janßen on 09.07.2015.
 */
public class AssemblyPoint extends Abstract_Graph_Point {

    private ArrayList<ResponsiveGUIGraph_Point> m_points = null;
    private Dimension m_startingDimension = null;
    private ResponsiveGUIGraph m_parent;

    public AssemblyPoint(ResponsiveGUIGraph parent, ResponsiveGUIGraph_Point... points) {
        m_parent = parent;
        m_points = new ArrayList<ResponsiveGUIGraph_Point>();
        m_startingDimension = points[0].getDesiredSize();
        for (ResponsiveGUIGraph_Point point: points)
            m_points.add(point);

        this.color_Point = new Color(190,0,0);
        this.color_Point_Highlighted = new Color(240,0,0);
    }

    public void addPoint(ResponsiveGUIGraph_Point point) {
        m_points.add(point);
    }

    @Override
    public Dimension getDesiredSize() {
        return m_startingDimension;
//        Vector2D erg = new Vector2D();
//
//        for (ResponsiveGUIGraph_Point point: m_points) {
//            erg.add(new Vector2D(point.getDesiredSize()));
//        }
//        erg.mult(1.0/m_points.size());
//        return new Dimension((int)erg.getX(), (int)erg.getY());
    }

    @Override
    public JFrame showGUI(Point e) {
        VariantManager vm = new VariantManager(m_parent, this, m_points.toArray(new ResponsiveGUIGraph_Point[m_points.size()]));
        vm.setVisible(true);
        return vm;
    }

    void resetVariants(ResponsiveGUIGraph_Point[] variants) {
        m_points = new ArrayList<ResponsiveGUIGraph_Point>();
        for (ResponsiveGUIGraph_Point point: variants)
            m_points.add(point);
    }

    public JFrame getGUI() {
        return null;
    }
}
