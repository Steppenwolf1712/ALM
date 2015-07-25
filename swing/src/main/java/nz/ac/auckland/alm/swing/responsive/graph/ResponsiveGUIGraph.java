package nz.ac.auckland.alm.swing.responsive.graph;

import nz.ac.auckland.alm.swing.ALMResponsiveLayout;
import nz.ac.auckland.alm.swing.responsive.Algebra;
import nz.ac.auckland.alm.swing.responsive.IResponsivePart;
import nz.ac.auckland.alm.swing.responsive.ResponsiveGUIFrame;
import nz.ac.auckland.alm.swing.responsive.graph.delauny.DelaunyTriangle;
import nz.ac.auckland.alm.swing.responsive.graph.delauny.DelaunyTriangle_Factory;
import nz.ac.auckland.alm.swing.responsive.graph.delauny.LineDrawer;
import nz.ac.auckland.alm.swing.responsive.graph.points.Abstract_Graph_Point;
import nz.ac.auckland.alm.swing.responsive.graph.points.AssemblyPoint;
import nz.ac.auckland.alm.swing.responsive.graph.points.ResponsiveGUIGraph_Point;
import nz.ac.auckland.alm.swing.responsive.graph.points.View_Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 * Created by Marc Janßen on 19.06.2015.
 */
public class ResponsiveGUIGraph extends JPanel {

    public static final String S_UIALGEBRA_FRAME_NAME = "UIAlgebra - View";
    private ArrayList<Abstract_Graph_Point> m_points = null;
    private ArrayList<View_Point> m_views = null;
    private Abstract_Graph_Point m_point_selected = null;

    public static final Dimension DIM_SIZE = new Dimension(750, 550);
    public static final Color COLOR_GRAPH = new Color(0, 0, 0, 180);
    public final Color COLOR_GRAPH_BACK = new Color(this.getBackground().getRed()+8, this.getBackground().getGreen()+8, this.getBackground().getBlue()+8, this.getBackground().getAlpha()-20);
    public static final Color COLOR_LINE = new Color(0, 0, 255, 255);
    public static final Stroke GRAPH_STROKE = new BasicStroke(3f);
    public static final Stroke DELAUNY_STROKE = new BasicStroke(0.8f);
    public static int MAX_SCALE = 5;
    private int m_xScale = 5;
    private int m_yScale = 5;
    private Vector2D m_Scale = new Vector2D(m_xScale, m_yScale);
    private static final double VIEW_SELECT_DISTANCE = 12.0;
    private static final double POINT_MERGE_DISTANCE = 12.0;
    static final int I_BORDER_GAP_X = 30;
    static final int I_BORDER_GAP_Y = 20;
    static final Vector2D BORDER_GAP = new Vector2D(I_BORDER_GAP_X, I_BORDER_GAP_Y);

    private LineDrawer m_LineDrawer = null;

    private DelaunyTriangle m_Delauny = null;

    private boolean m_drawDelauny = false;

    public ResponsiveGUIGraph() {
        init();
    }

    private void init() {
        m_points = new ArrayList<Abstract_Graph_Point>();
        m_views = new ArrayList<View_Point>();

        this.setBounds(0, 0, DIM_SIZE.width, DIM_SIZE.height);
    }


    private ResponsiveGUIGraph_Point[] getPoints() {
        ResponsiveGUIGraph_Point[] erg = new ResponsiveGUIGraph_Point[m_points.size()];
        return m_points.toArray(erg);
    }

    /**
     * This Method adds a specific Point to the ResponsiveGUIGraph, regarding the UIAlgebra-Parameter.
     *
     * @param toAdd
     */
    public void addUIAlgebra(Algebra toAdd) {
        this.addUIAlgebra(toAdd, null);
    }

    /**
     * This Method adds a specific Point to the ResponsiveGUIGraph, regarding the UIAlgebra-Parameter.
     * If there was already an ALMLayout created out of the UIAlgebra, it can be stored with the ResponsiveGUIGraph
     * into the Point-Object to save the collected Constraints collected so far.
     *
     * @param toAdd
     * @param alm_Container
     */
    public void addUIAlgebra(Algebra toAdd, JFrame alm_Container) {
        ResponsiveGUIGraph_Point temp = new ResponsiveGUIGraph_Point(this, toAdd, alm_Container);

        addPoint(temp);
    }


    public void addPoint(ResponsiveGUIGraph_Point point) {
        Abstract_Graph_Point collision = null;
        for (Abstract_Graph_Point iterator: m_points)
            if (iterator.compareToSize(point.getDesiredSize())<=POINT_MERGE_DISTANCE*POINT_MERGE_DISTANCE) {
                collision = iterator;
                break;
            }
        if (collision == null) {
            this.m_points.add(point);
        } else {
            if (collision instanceof AssemblyPoint) {
                AssemblyPoint ap = (AssemblyPoint)collision;
                ap.addPoint(point);
            } else {
                this.m_points.remove(collision);
                AssemblyPoint ap = new AssemblyPoint(this, (ResponsiveGUIGraph_Point)collision, point);
                this.m_points.add(ap);
            }
            repaint();
            return;
        }


        if (m_points.size() == 2 && m_Delauny==null)
            calcDelauny(new Vector2D(m_points.get(0).getDesiredSize()),
                    new Vector2D(m_points.get(1).getDesiredSize()));
        else
        if (m_points.size() >= 2 && !(m_Delauny==null))
            addDelauny(new Vector2D(point.getDesiredSize()));

        this.repaint();
    }

    public boolean remove(Abstract_Graph_Point point) {
        if (point instanceof View_Point) {
            return m_views.remove(point);
        }

        boolean erg = m_points.remove(point);

        if (erg) {
            repaint();
            if (m_point_selected!=null && m_point_selected.equals(point)) {
                m_point_selected = null;
            }
            if (m_points.size()>=2)
                calcDelauny(new Vector2D(m_points.get(0).getDesiredSize()),
                        new Vector2D(m_points.get(1).getDesiredSize()));
            else
                this.m_Delauny = null;
        }
        return erg;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Vector2D relPos = new Vector2D(I_BORDER_GAP_X, getHeight()-I_BORDER_GAP_Y);
        Vector2D scale = new Vector2D(m_xScale,m_yScale);
        Vector2D max_Size = new Vector2D(getWidth(), getHeight()).sub(BORDER_GAP.mult(2)).mult(scale);

        if (m_LineDrawer==null)
            m_LineDrawer = new LineDrawer(g2, relPos, scale, max_Size);
        else
            m_LineDrawer.refreshGraphicsObject(g2);

        GeneralPath content_Graph = new GeneralPath();
        content_Graph.moveTo(I_BORDER_GAP_X, I_BORDER_GAP_Y);
        content_Graph.lineTo(I_BORDER_GAP_X, getHeight() - I_BORDER_GAP_Y);
        content_Graph.lineTo(getWidth() - I_BORDER_GAP_X, getHeight() - I_BORDER_GAP_Y);
        content_Graph.lineTo(getWidth() - I_BORDER_GAP_X, I_BORDER_GAP_Y);
        content_Graph.closePath();

        g2.setColor(COLOR_GRAPH_BACK);
        System.out.println(COLOR_GRAPH_BACK);
        g2.draw(content_Graph);
        g2.fill(content_Graph);
        g2.setColor(COLOR_GRAPH);

        m_LineDrawer.drawLine(max_Size.mult(new Vector2D(0.0,1.0)), new Vector2D());
        m_LineDrawer.drawLine(new Vector2D(), max_Size.mult(new Vector2D(1.0,0.0)));

        //Hatchmarks y-axes
        boolean length_switch = true;
        for (int i = 0; i<14; i++) {
            int x0;
            int y0 = getHeight()-I_BORDER_GAP_Y -(50*i);
            if (length_switch) {
                x0 = I_BORDER_GAP_X - 10;
                String temp = ""+(100*i*m_yScale/2);
                g2.drawString(temp, x0+9-(temp.length()*7), y0-2);
            } else
                x0 = I_BORDER_GAP_X-5;
            length_switch = !length_switch;

            int x1 = I_BORDER_GAP_X;
            int y1 = getHeight()-I_BORDER_GAP_Y -(50*i);
            g2.drawLine(x0, y0, x1, y1);
        }
        //Hatchmarks x-axes
        length_switch = true;
        for (int i = 0; i<14; i++) {
            int x0 = I_BORDER_GAP_X+(50*i);

            int y0;
            if (length_switch) {
                y0 = getHeight() - I_BORDER_GAP_Y + 10;
                String temp = ""+(100*i*m_xScale/2);
                g2.drawString(temp, x0+1, y0+3);
            } else
                y0 = getHeight()-I_BORDER_GAP_Y +5;
            length_switch = !length_switch;

            int x1 = I_BORDER_GAP_X+(50*i);
            int y1 = getHeight()-I_BORDER_GAP_Y;
            g2.drawLine(x0, y0, x1, y1);
        }

        Stroke oldStroke = g2.getStroke();
        g2.setColor(COLOR_GRAPH);
        g2.setStroke(GRAPH_STROKE);

        //relocate all points
        for (Abstract_Graph_Point point: m_points) {
            Vector2D temp_loc = new Vector2D(point.getDesiredSize());
            Vector2D loc = temp_loc.divide(scale).mult(new Vector2D(1.0, -1.0)).add(relPos);//getWidth()/m_xScale+ I_BORDER_GAP_X, this.getHeight()-(temp_loc.getHeight()/m_yScale+I_BORDER_GAP_Y));
            point.drawPoint(loc, g2);
        }

        for (View_Point point: m_views) {
            Vector2D temp_loc = new Vector2D(point.getDesiredSize());
            Vector2D loc = temp_loc.divide(scale).mult(new Vector2D(1.0, -1.0)).add(relPos);//getWidth()/m_xScale+ I_BORDER_GAP_X, this.getHeight()-(temp_loc.getHeight()/m_yScale+I_BORDER_GAP_Y));
            point.drawPoint(loc, g2);
        }

        Color temp = g2.getColor();
        g2.setStroke(DELAUNY_STROKE);
        g2.setColor(COLOR_LINE);

        /*if (m_points.size()==2) {
            drawLineBetween(new Vector2D(m_points.get(0).getDesiredSize()),
                    new Vector2D(m_points.get(1).getDesiredSize()));
        } else {
            if (m_points.size()>2) {
                if (m_Delauny == null)
                    calcDelauny(new Vector2D(m_points.get(0).getDesiredSize()),
                            new Vector2D(m_points.get(1).getDesiredSize()),
                            new Vector2D(m_points.get(2).getDesiredSize()));
                drawDelauny();
            }
        }*/
        if (m_points.size() >= 2) {
            if (m_Delauny == null)
                calcDelauny(new Vector2D(m_points.get(0).getDesiredSize()),
                        new Vector2D(m_points.get(1).getDesiredSize()));
            drawDelauny();
        }
        g2.setColor(temp);
    }

    private void drawLineBetween(Point positionInGraph, Point positionInGraph1) {
        Vector2D point0, point1;
        if (positionInGraph.x<=positionInGraph1.x) {
            point0 = new Vector2D(positionInGraph);
            point1 = new Vector2D(positionInGraph1);
        } else {
            point0 = new Vector2D(positionInGraph1);
            point1 = new Vector2D(positionInGraph);
        }

        Vector2D middle = point0.add(point1);
        middle = middle.mult(0.5);

        double step;

        step = point1.sub(point0).getSlope();//point1.x - point0.x) / (point1.y - point0.y);
        if (Double.NEGATIVE_INFINITY == step || Double.POSITIVE_INFINITY == step) {
            m_LineDrawer.drawLine(middle.mult(new Vector2D(0.0, 1.0)), new Vector2D((getWidth() -(2*I_BORDER_GAP_X))* m_xScale,middle.getY()));
            //g2.drawLine(I_BORDER_GAP_X, getHeight()-I_BORDER_GAP_Y-middle.y, getWidth()-I_BORDER_GAP_X, getHeight()-I_BORDER_GAP_Y-middle.y);
            return;
        }
        Vector2D p1, p2;
        if (step == 0) {
            p1 = new Vector2D(middle.getX(), 0);
            p2 = new Vector2D(middle.getX(), (getHeight()-2*I_BORDER_GAP_Y)*m_yScale);
        } else {
            step = -1 / step;

            double b = middle.getY()-step*middle.getX();
            int maxH = getHeight()-2*I_BORDER_GAP_Y;

            int maxW = getWidth()-2*I_BORDER_GAP_X;
            double b2 = b+step*maxW;

            double x1 = -b/step;
            double x2 = (maxH-b)/step;

            p2 = new Vector2D(maxW, b2);

            if (b<=maxH) {
                if (b>=0) {
                    p1 = new Vector2D(0, b);
                    if (b2<=maxH) {
                        if (b2<0)
                            p2 = new Vector2D(x1, 0);
                    } else
                        p2 = new Vector2D(x2, maxH);
                } else {
                    p1 = new Vector2D(x1, 0);

                    if (b2>maxH)
                        p2 = new Vector2D(x2, maxH);

                }
            } else {
                p1 = new Vector2D(x2, maxH);

                if (b2<0)
                    p2 = new Vector2D(x1, 0);
            }

        }
        m_LineDrawer.drawLine(p1, p2);
        //VectorHelper.drawLineOnGraph(g2, p1, p2);
    }

    public void setScale(int new_scale) {
        if (new_scale>MAX_SCALE || new_scale<0)
            return;
        this.m_xScale = new_scale;
        this.m_yScale = new_scale;
        this.m_Scale = new Vector2D(m_xScale, m_yScale);
        Vector2D scale = new Vector2D(this.m_xScale, this.m_yScale);
        m_LineDrawer.setScale(scale);
        m_LineDrawer.setMaxValues(new Vector2D(DIM_SIZE).sub(BORDER_GAP.mult(2)).mult(scale));
        repaint();
    }


    public Dimension getPreferredSize() {
        return this.DIM_SIZE;
    }

    public Point getPositionInGraph(Point p) {
        return getPositionInGraph((int)p.getX(), (int)p.getY());
    }

    public Point getPositionInGraph(int x, int y) {
        Vector2D relPos = new Vector2D(-I_BORDER_GAP_X,getHeight()-I_BORDER_GAP_Y);
        Vector2D erg = relPos.add(new Point(x, -y)).mult(m_Scale);
        Vector2D maxValues = new Vector2D(getSize()).sub(BORDER_GAP.mult(2)).mult(m_Scale);
        if (new Vector2D().smallerEquals(erg) && maxValues.greaterEquals(erg))
            return erg;
        return new Vector2D();
    }

//    private int calcX(int x) {
//        int erg = (x-I_BORDER_GAP_X)*m_xScale;
//        if (erg<0)
//            return 0;
//        return erg;
//    }
//
//    private int calcY(int y) {
//        int erg = ((getHeight()-y)-I_BORDER_GAP_Y)*m_yScale;
//        if (erg<0)
//            return 0;
//        return erg;
//    }

    public void selectGUI(Point posi) {
        if (m_points.size() == 0)
            return;
        Vector2D test = new Vector2D(posi), test2;
        for (View_Point point: m_views) {
            test2 = new Vector2D(point.getDesiredSize());
            if (test2.distance(test)<=VIEW_SELECT_DISTANCE*m_Scale.distance(new Vector2D())) {
                if (m_point_selected != null) {
                    if (!m_point_selected.equals(point))
                        m_point_selected.setSelected(false);
                    else
                        return;
                }

                m_point_selected = point;
                m_point_selected.setSelected(true);
                repaint();
                return;
            }
        }

        Abstract_Graph_Point temp = getPointAtSize(posi);
        if (m_point_selected!= null) {
            if (!m_point_selected.equals(temp)) {
                m_point_selected.setSelected(false);
            } else
                return;
        }

        m_point_selected = temp;
        m_point_selected.setSelected(true);
        repaint();
    }

    private Abstract_Graph_Point getPointAtSize(Point p) {
        Abstract_Graph_Point erg = null;
        if (m_points.size() == 1) {
            erg = m_points.get(0);
        } else {
            double current = Double.MAX_VALUE, test;
            for (Abstract_Graph_Point point : m_points) {
                test = p.distanceSq(point.getDesiredSize().getWidth(), point.getDesiredSize().getHeight());
                //System.out.println("Distance of "+point.getDesiredSize()+"\n\tto Mouse is : "+test+"\n\twith mouse : "+posi.toString());
                if (test < current) {
                    erg = point;
                    current = test;
                }
            }
        }
        return erg;
    }

    public void deselectGUI() {
        if (m_point_selected==null)
            return;
        m_point_selected.setSelected(false);
        m_point_selected = null;
        repaint();
    }

    public void showSelectedGUI(Point locationOnScreen) {
        if (m_point_selected==null)
            return;
        m_point_selected.showGUI(locationOnScreen);
    }

//    private void calcDelauny(Vector2D point1, Vector2D point2, Vector2D point3) {
//        m_Delauny = DelaunyTriangle_Factory.createDelaunyTriangle(point1, point2, point3);
//    }

    private void calcDelauny(Vector2D point1, Vector2D point2) {
        m_Delauny = DelaunyTriangle_Factory.createDelaunyTriangle(point1, point2);
    }

    private void addDelauny(Vector2D point) {
        if (this.m_Delauny != null)
            this.m_Delauny.addPoint(point);
    }

    private void drawDelauny() {
        //this.m_Delauny.drawDelauny(this.m_LineDrawer);//For testing the DelaunyTriangulation
        this.m_Delauny.drawVoronoiDiagram(this.m_LineDrawer);
        if (m_drawDelauny) {
            m_LineDrawer.changeColor(new Color(255,0,0,255));
            this.m_Delauny.drawDelauny(this.m_LineDrawer);//For testing the DelaunyTriangulation
        }
    }

    public void showKontextMenu(MouseEvent event) {
        //Point locationOnScreen = event.getLocationOnScreen();
        KontextMenu menu = new KontextMenu(this, m_point_selected);
        menu.show(event.getComponent(), event.getX(), event.getY());
        //menu.setLocation(locationOnScreen.x, locationOnScreen.y);
        //menu.setVisible(true);
    }

    public void addViewPoint(Point p) {
//        Vector2D erg_pos = new Vector2D(I_BORDER_GAP_X, getHeight()-I_BORDER_GAP_Y);
//        erg_pos = new Vector2D(p).mult(new Vector2D(m_xScale,-m_yScale)).sub(erg_pos);
        Point loc = getPositionInGraph(p);
        View_Point temp = new View_Point(this, new Dimension((int)loc.getX(), (int)loc.getY()));
        m_views.add(temp);
        repaint();
    }

    /**
     * Opens the GUI, defined for a certain Size described by the parameter.
     * It also returns the Container Object which will be shown, for further manipulation.
     *
     * @param locationOnScreen
     * @return
     */
    public JFrame showGUIAtSize(Point locationOnPanel, Point locationOnScreen) {
        return getPointAtSize(getPositionInGraph(locationOnPanel)).showGUI(locationOnScreen);
    }

    public JFrame getGUIAtSize(Point p) {
        return getPointAtSize(p).getGUI();
    }

    public IResponsivePart[] getResponsiveParts() {
        ArrayList<IResponsivePart> parts = new ArrayList<IResponsivePart>();
        for (Abstract_Graph_Point point: m_points) {
            if (point instanceof AssemblyPoint) {
                return null;
            }
            if (point instanceof  ResponsiveGUIGraph_Point) {
                ResponsiveGUIGraph_Point temp = (ResponsiveGUIGraph_Point) point;
                parts.add(temp);
            }
        }
        IResponsivePart[] erg = new IResponsivePart[parts.size()];
        return parts.toArray(erg);
    }
}
