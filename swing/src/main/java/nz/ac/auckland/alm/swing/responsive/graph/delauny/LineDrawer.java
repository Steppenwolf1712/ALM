package nz.ac.auckland.alm.swing.responsive.graph.delauny;

import nz.ac.auckland.alm.swing.responsive.graph.Vector2D;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Marc Janßen on 27.06.2015.
 */
public class LineDrawer {

    private Graphics2D m_graphic = null;
    private Vector2D m_relativePos = null;
    private Vector2D m_Scale;
    private Vector2D m_MaxValues;
    private ArrayList<HelpLine2D> m_lookUp = null;


    public LineDrawer(Graphics2D g2, Vector2D relativPosition, Vector2D scale, Vector2D MaxValues) {
        this.m_graphic = g2;
        this.m_relativePos = relativPosition;
        this.m_Scale = scale;
        this.m_MaxValues = MaxValues;
        this.m_lookUp = new ArrayList<HelpLine2D>();
    }

    public void refreshGraphicsObject(Graphics2D g2) {
        this.m_graphic = g2;
        this.m_lookUp = new ArrayList<HelpLine2D>();
    }

    public void setScale(Vector2D vector2D) {
        this.m_Scale = vector2D;
    }

    public void setMaxValues(Vector2D mult) {
        this.m_MaxValues = mult;
    }

    /**
     * This Method draws a line, with a Graphics2D object which was given first.
     * If the line described by these two Vector2D-objects is already drawn, it returns false. True if the Line has to be drawn.
     *
     * @param startingPosition
     * @param endingPosition
     * @return
     */
    public boolean drawLine(Vector2D startingPosition, Vector2D endingPosition) {
        HelpLine2D toCheck = new HelpLine2D(startingPosition,endingPosition);
        if (m_lookUp.contains(toCheck))
            return false;
        else
            m_lookUp.add(toCheck);

        Vector2D sorted_one, sorted_two, xProj = new Vector2D(1.0,0.0);
        if (startingPosition.mult(xProj).smallerEquals(endingPosition.mult(xProj))) {
            sorted_one = startingPosition;
            sorted_two = endingPosition;
        } else {
            sorted_one = endingPosition;
            sorted_two = startingPosition;
        }
        double slope = sorted_two.sub(sorted_one).getSlope();

        if (Double.NEGATIVE_INFINITY == slope || Double.POSITIVE_INFINITY == slope || slope == 0.0) {
            sorted_one = sorted_one.getMax(new Vector2D()).getMin(m_MaxValues);
            sorted_two = sorted_two.getMax(new Vector2D()).getMin(m_MaxValues);
            draw(sorted_one, sorted_two);
            return true;
        }


        if (sorted_one.getX()>m_MaxValues.getX() || sorted_two.getX()<0) {
            return true;//Can be Ignored
        }

        if (sorted_one.greaterEquals(new Vector2D()) && m_MaxValues.getY()>=sorted_one.getY()) {
            if (m_MaxValues.greaterEquals(sorted_two)&&sorted_two.getY()>=0) {
                //Vector2D transformed1 = sorted_one.mult(m_Scale).add(m_relativePos);
                //Vector2D transformed2 = sorted_two.mult(m_Scale).add(m_relativePos);
                draw(sorted_one, sorted_two);
            } else {
                Vector2D cutAtRight = new Vector2D(m_MaxValues.getX(),(sorted_one.getY()+slope*(m_MaxValues.getX()-sorted_one.getX())));

                if (cutAtRight.getY()>m_MaxValues.getY()) {
                    Vector2D cutAtTop = new Vector2D(sorted_one.getX()+(m_MaxValues.getY()-sorted_one.getY())/slope,m_MaxValues.getY());
                    if (cutAtTop.getX()>m_MaxValues.getX()) {
                        return true;
                    }
                    draw(sorted_one, cutAtTop);
                } else if (cutAtRight.getY()<0) {
                    Vector2D cutAtBottom = new Vector2D(sorted_one.getX()-sorted_one.getY()/slope, 0.0);
                    if (cutAtBottom.getX()>m_MaxValues.getX())
                        return true;
                    draw(sorted_one, cutAtBottom);
                } else {
                    draw(sorted_one, cutAtRight);
                }

            }
        } else {
            if (m_MaxValues.greaterEquals(sorted_two) && sorted_two.getY()>0.0) {
                Vector2D cutAtLeft = new Vector2D(0.0, sorted_one.getY()-slope*sorted_one.getX());

                if (cutAtLeft.getY()>m_MaxValues.getY()) {
                    Vector2D cutAtTop = new Vector2D(sorted_two.getX()+(m_MaxValues.getY()-sorted_two.getY())/slope,m_MaxValues.getY());
                    if (cutAtTop.getX()<0.0)
                        return true;
                    draw(cutAtTop, sorted_two);
                } else if (cutAtLeft.getY()<0.0) {
                    Vector2D cutAtBottom = new Vector2D(sorted_two.getX()-sorted_two.getY()/slope, 0.0);
                    if (cutAtBottom.getX()<0.0)
                        return true;
                    draw(cutAtBottom, sorted_two);
                } else
                    draw(cutAtLeft, sorted_two);

            } else {
                Vector2D cutAtLeft = new Vector2D(0.0, sorted_one.getY()-slope*sorted_one.getX()), finalPoint1;
                if (cutAtLeft.getY()>m_MaxValues.getY()) {
                    finalPoint1 = new Vector2D(sorted_one.getX()+(m_MaxValues.getY()-sorted_one.getY())/slope,m_MaxValues.getY());
                } else if (cutAtLeft.getY()<0.0) {
                    finalPoint1 = new Vector2D(sorted_one.getX()-sorted_one.getY()/slope, 0.0);
                } else
                    finalPoint1 = cutAtLeft;

                Vector2D cutAtRight = new Vector2D(m_MaxValues.getX(),(sorted_one.getY()+slope*(m_MaxValues.getX()-sorted_one.getX()))), finalPoint2;
                if (cutAtRight.getY()>m_MaxValues.getY()) {
                    finalPoint2 = new Vector2D(sorted_one.getX()+(m_MaxValues.getY()-sorted_one.getY())/slope,m_MaxValues.getY());
                } else if (cutAtRight.getY()<0.0) {
                    finalPoint2 = new Vector2D(sorted_one.getX()-sorted_one.getY()/slope, 0.0);
                } else
                    finalPoint2 = cutAtRight;

                draw(finalPoint1,finalPoint2);
                //Possible, that the line doesn't have to be drawn, so I ignore this case
                //Especially because one of the Points lies probably outside of the graph
            }
        }
        return true;
    }

    private void draw(Vector2D vector1, Vector2D vector2) {
        Vector2D vec1 = vector1.divide(m_Scale).mult(new Vector2D(1.0,-1.0)).add(m_relativePos);
        Vector2D vec2 = vector2.divide(m_Scale).mult(new Vector2D(1.0, -1.0)).add(m_relativePos);
        //System.out.println("Try to draw Line from \n\t"+vec1+" to \n\t"+vec2);
        /*GeneralPath path = new GeneralPath();
        path.moveTo(vec1.getX(), vec1.getY());
        path.lineTo(vec2.getX(), vec2.getY());
        path.closePath();
        m_graphic.draw(path);*/
        m_graphic.drawLine((int)vec1.getX(), (int)vec1.getY(), (int)vec2.getX(), (int)vec2.getY());
    }

    public void drawPoint(Vector2D midPointOfOuterCircle) {
        Vector2D temp = midPointOfOuterCircle.divide(m_Scale).mult(new Vector2D(1.0,-1.0)).add(m_relativePos);
        //System.out.println("Draw Midpoint of outer Circle: "+temp);
        m_graphic.drawOval((int) temp.getX(), (int) temp.getY(), 5, 5);
    }

    public void changeColor(Color color) {
        this.m_graphic.setColor(color);
    }


    private class HelpLine2D {
        private Vector2D point1 = null;
        private Vector2D point2 = null;

        HelpLine2D(Vector2D point1, Vector2D point2) {
            this.point1 = point1;
            this.point2 = point2;
        }
        public boolean equals(Object o) {
            if (o instanceof HelpLine2D) {
                HelpLine2D line2 = (HelpLine2D) o;
                if (this.point1.equals(line2.point1)) {
                    return this.point2.equals(line2.point2);
                } else {
                    return (this.point1.equals(line2.point2) && this.point2.equals(line2.point1));
                }
            }
            return false;
        }
    }
}
