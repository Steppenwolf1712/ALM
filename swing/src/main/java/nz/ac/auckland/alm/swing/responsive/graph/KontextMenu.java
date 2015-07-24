package nz.ac.auckland.alm.swing.responsive.graph;


import nz.ac.auckland.alm.swing.responsive.graph.points.Abstract_Graph_Point;
import nz.ac.auckland.alm.swing.responsive.graph.points.AssemblyPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Marc Janßen on 24.06.2015.
 */
public class KontextMenu extends JPopupMenu implements ActionListener {

    private JMenuItem btn_remove = new JMenuItem("Remove GUI");
    private JMenuItem btn_addViewPoint = new JMenuItem("Add View Point");
    private final Abstract_Graph_Point m_point;
    private final ResponsiveGUIGraph m_graph;

    private Point m_callPoint = null;

    public KontextMenu(ResponsiveGUIGraph graph, Abstract_Graph_Point point) {
        super();
        m_graph = graph;
        m_point = point;

        init();
    }

    private  void init() {
        btn_addViewPoint.addActionListener(this);
        add(btn_addViewPoint);
        btn_remove.addActionListener(this);//;MouseListener(this);
        add(btn_remove);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(btn_remove)) {
            if (m_point instanceof AssemblyPoint) {
                int answer = JOptionPane.showConfirmDialog(m_graph, "Do you really intend to delete all alternative GUIs saved in the AssemblyPoint?",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.CANCEL_OPTION)
                    return;
            }

            m_graph.remove(m_point);
        } else if (e.getSource().equals(btn_addViewPoint)) {
            m_graph.addViewPoint(m_callPoint);
        }
        this.setVisible(false);
    }

    public void show(Component parent, int x, int y) {
        m_callPoint = new Point(x,y);
        super.show(parent, x, y);

    }
}
