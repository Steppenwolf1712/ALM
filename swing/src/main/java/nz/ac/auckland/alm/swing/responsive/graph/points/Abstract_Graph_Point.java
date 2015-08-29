package nz.ac.auckland.alm.swing.responsive.graph.points;

import nz.ac.auckland.alm.swing.responsive.Algebra;
import nz.ac.auckland.alm.swing.responsive.graph.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;

/**
 * Created by Marc Janßen on 09.07.2015.
 */
public abstract class Abstract_Graph_Point {

    protected Color color_Point;
    protected Color color_Point_Highlighted;

    private boolean m_selected = false;

    public void setSelected(boolean selected) {
        m_selected = selected;
    }

    public boolean isSelected() {
        return m_selected;
    }

    public abstract Dimension getDesiredSize();

    public void drawPoint(Point loc, Graphics2D g2) {
        Point p1 = new Point((int)loc.getX()-8, (int)loc.getY()),
                p2 = new Point((int)loc.getX(),(int)loc.getY()+8),
                p3 = new Point((int)loc.getX()+8,(int)loc.getY()),
                p4 = new Point((int)loc.getX(),(int)loc.getY()-8);
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(p1.getX(), p1.getY());
        p0.lineTo(p2.getX(), p2.getY());
        p0.lineTo(p3.getX(), p3.getY());
        p0.lineTo(p4.getX(), p4.getY());
        p0.closePath();
        Color tempColor = g2.getColor();
        if (m_selected)
            g2.setColor(color_Point_Highlighted);
        else
            g2.setColor(color_Point);
        g2.fill(p0);
        g2.setColor(tempColor);
        g2.draw(p0);
    }

    public double compareToSize(Dimension dim) {
        Vector2D toCompare = new Vector2D(dim);
        Vector2D ownSize = new Vector2D(getDesiredSize());
        return ownSize.sub(toCompare).getQuadLength();
    }

    public abstract JFrame showGUI(Point e);

    public abstract JFrame getGUI();
}
