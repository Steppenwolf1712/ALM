package nz.ac.auckland.alm.swing.responsive.graph;

import java.awt.*;

/**
 * Created by Marc Janßen on 24.06.2015.
 */
public class Vector2D extends Point {

    private final double m_x;
    private final double m_y;

    @Override
    public double getX() {
        return m_x;
    }

    @Override
    public double getY() {
        return m_y;
    }

    /**
     * This Constructor builds a null-Vector
     */
    public Vector2D() {
        this.m_x = 0.0;
        this.m_y = 0.0;
    }

    public Vector2D(double x, double y) {
        this.m_x = x;
        this.m_y = y;
    }

    public Vector2D(Point p) {
        this(p.getX(), p.getY());
    }
    public Vector2D(Dimension p) {
        this(p.getWidth(), p.getHeight());
    }

    public double getLength() {
        double z = (m_x * m_x) + (m_y * m_y);
        return Math.sqrt(z);
    }

    public Vector2D getNormalVector() {
        return new Vector2D(-this.m_y,this.m_x);
    }

    public Vector2D add(Point vec) {
        return new Vector2D(this.m_x +vec.getX(), this.m_y +vec.getY());
    }

    public boolean equals(Object o) {
        if (o instanceof Vector2D) {
            Vector2D temp = (Vector2D) o;
            return (this.m_x == temp.getX() && this.m_y == temp.getY());
        }
        return false;
    }

    public Vector2D sub(Point vec) {
        return new Vector2D(this.m_x -vec.getX(), this.m_y -vec.getY());
    }

    public Vector2D mult(double mult) {
        return new Vector2D(this.m_x *mult, this.m_y *mult);
    }

    public Vector2D mult(Point vec) {
        return new Vector2D(getX()*vec.getX(), getY()*vec.getY());
    }

    public Vector2D divide(Vector2D para) {
        return new Vector2D(this.m_x/para.getX(), this.m_y/para.getY());
    }

    public Vector2D getNormalisation() {
        return new Vector2D(this.m_x / this.getLength(),this.m_y/this.getLength());
    }

    public String toString() {
        return "Vector2D: x="+m_x+"/y="+m_y;
    }

    public double getSlope() {
        try {
            return getY()/getX();
        } catch (ArithmeticException ex) {
            if (getY() > 0.0)
                return java.lang.Double.POSITIVE_INFINITY;
            else
                if (getY() < 0.0)
                    return java.lang.Double.NEGATIVE_INFINITY;
                else
                    return java.lang.Double.NaN;
        }

    }

    public double distance(Vector2D vec) {
        return this.sub(vec).getLength();
    }

    /**
     * Helpfunktion, that returns, whether the length of this Vector in X-Direction and Y Direction as well, is longer
     * or equal than the Parameter;
     *
     * @param toCompare
     * @return
     */
    public boolean greaterEquals(Vector2D toCompare) {
        return (this.m_x>=toCompare.getX() && this.m_y>=toCompare.getY());
    }

    /**
     * Helpfunktion, that returns, whether the length of this Vector in X-Direction, and Y Direction as well, is shorter
     * or equals than the Parameter;
     *
     * @param toCompare
     * @return
     */
    public boolean smallerEquals(Vector2D toCompare) {
        return (this.m_x<=toCompare.getX() && this.m_y<=toCompare.getY());
    }

    public Vector2D getMax(Vector2D vec) {
        double x = java.lang.Double.max(this.m_x, vec.getX());
        double y = java.lang.Double.max(this.m_y, vec.getY());
        return new Vector2D(x,y);
    }

    public Vector2D getMin(Vector2D vec) {
        double x = java.lang.Double.min(this.m_x, vec.getX());
        double y = java.lang.Double.min(this.m_y, vec.getY());
        return new Vector2D(x,y);
    }

    public double getQuadLength() {
        return (m_x*m_x)+(m_y*m_y);
    }
}
