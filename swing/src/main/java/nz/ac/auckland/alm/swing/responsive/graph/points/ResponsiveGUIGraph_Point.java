package nz.ac.auckland.alm.swing.responsive.graph.points;

import nz.ac.auckland.alm.swing.responsive.Algebra;
import nz.ac.auckland.alm.swing.responsive.AlgebraFrameFactory;
import nz.ac.auckland.alm.swing.responsive.IResponsivePart;
import nz.ac.auckland.alm.swing.responsive.ResponsiveGUIEditor;
import nz.ac.auckland.alm.swing.responsive.graph.ResponsiveGUIGraph;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marc Janßen on 20.06.2015.
 */
public class ResponsiveGUIGraph_Point extends Abstract_Graph_Point implements IResponsivePart {

    private final ResponsiveGUIGraph m_parent_Graph;
    private Algebra m_algebra;
    private JFrame m_view;

    public ResponsiveGUIGraph_Point(ResponsiveGUIGraph parent, Algebra algebra) {
        this(parent, algebra, null);
    }

    public ResponsiveGUIGraph_Point(ResponsiveGUIGraph parent, Algebra algebra, JFrame frame) {
        m_parent_Graph = parent;
        m_algebra = algebra;

        if (frame == null) {
            m_view = AlgebraFrameFactory.createFrame(ResponsiveGUIGraph.S_UIALGEBRA_FRAME_NAME, m_algebra, ResponsiveGUIEditor.m_factory);
        } else
            m_view = frame;

        this.color_Point = new Color(0, 0, 180, 180);
        this.color_Point_Highlighted = new Color(150, 130, 0, 180);

        m_view.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        m_view.pack();
    }

    public Dimension getDesiredSize() {
        // TODO: Here it is to test, whether the preferredSize of the ContentPane satisfies my needs
        //m_view.pack();
        return m_algebra.getPreferredSize(m_view.getContentPane());
    }

    String getStringDefinition() {
        return this.m_algebra.getM_UIAstring();
    }


    public JFrame showGUI(Point p) {
        this.m_view.setLocation((int) p.getX() - (this.m_view.getWidth() / 2), (int) p.getY() - (this.m_view.getHeight() / 2));
        this.m_view.pack();
        this.m_view.setResizable(true);
        this.m_view.setVisible(true);
        this.m_view.pack();
        return m_view;
    }

    @Override
    public Algebra getAlgebra() {
        return this.m_algebra;
    }
}
