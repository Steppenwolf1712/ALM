package nz.ac.auckland.alm.swing.responsive.graph.delauny;

import nz.ac.auckland.alm.swing.responsive.graph.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc Janßen on 24.06.2015.
 */
public class DelaunyTriangle {

    private ArrayList<DelaunyTriangle> m_listOfReplacements;

    final Vector2D m_vec_a, m_vec_b, m_vec_c;

    Map<Vector2D, Vector2D> m_thirdPointHelper = null;
    Map<Vector2D,DelaunyTriangle> m_NeighborTriangle = null;
    private Vector2D m_Midpoint = null;

    public DelaunyTriangle getNeighbor(Vector2D point) {
        return m_NeighborTriangle.get(point);
    }

    public void setNeighbor(Vector2D key, DelaunyTriangle neighbor) {
        this.m_NeighborTriangle.put(key, neighbor);
    }

    DelaunyTriangle(Vector2D a, Vector2D b, Vector2D c) {
        init();
        m_vec_a = a;
        m_vec_b = b;
        m_vec_c = c;
        m_thirdPointHelper.put(m_vec_a.add(m_vec_b), m_vec_c);
        m_thirdPointHelper.put(m_vec_c.add(m_vec_a), m_vec_b);
        m_thirdPointHelper.put(m_vec_b.add(m_vec_c), m_vec_a);
    }

    private void init() {
        m_listOfReplacements = new ArrayList<DelaunyTriangle>();
        m_NeighborTriangle = new HashMap<Vector2D, DelaunyTriangle>();
        m_thirdPointHelper = new HashMap<Vector2D, Vector2D>();
    }

    public boolean is_Final() {
        return m_listOfReplacements.isEmpty();
    }

    /**
     * This method adds a point to the System of DelaunyTriangles. The most important thing is, that the point has to lie
     * in the triangle. If this Triangle is final, it will be splitted and some necesary swaps will be done.
     * if this DelaunyTriangle was already splitted, it will be calculated to which of its sibblings the point belongs to
     * and will be added there instead.
     *
     * @param orig_vertic
     */
    public void addPoint(Vector2D orig_vertic) {
        if (is_Final()) {
//            System.out.println("DelaunyTriangle: add point to final triangle");
            if (pointOnEdge(orig_vertic)) {
                return;
            }

            DelaunyTriangle ABZ = new DelaunyTriangle(m_vec_a, m_vec_b, orig_vertic);
            ABZ.setNeighbor(orig_vertic, getNeighbor(m_vec_c));
            m_listOfReplacements.add(ABZ);

            DelaunyTriangle BZC = new DelaunyTriangle(m_vec_b, orig_vertic, m_vec_c);
            BZC.setNeighbor(orig_vertic, getNeighbor(m_vec_a));
            m_listOfReplacements.add(BZC);

            BZC.setNeighbor(m_vec_c, ABZ);
            ABZ.setNeighbor(m_vec_a, BZC);

            DelaunyTriangle ZCA = new DelaunyTriangle(orig_vertic, m_vec_c, m_vec_a);
            ZCA.setNeighbor(orig_vertic, getNeighbor(m_vec_b));
            m_listOfReplacements.add(ZCA);

            ZCA.setNeighbor(m_vec_a, BZC);
            BZC.setNeighbor(m_vec_b, ZCA);
            ZCA.setNeighbor(m_vec_c, ABZ);
            ABZ.setNeighbor(m_vec_b, ZCA);

            //Test for possible Swaps:
            ABZ.swapTestOn_At(m_vec_a, m_vec_b,orig_vertic);
            BZC.swapTestOn_At(m_vec_b, m_vec_c,orig_vertic);
            ZCA.swapTestOn_At(m_vec_c, m_vec_a,orig_vertic);

        } else {
            int counter = 0;
            for (DelaunyTriangle tri: m_listOfReplacements) {
                if (tri.pointInTriangle(orig_vertic)) {
                    tri.addPoint(orig_vertic);
                    return;
                } else {
//                    System.out.println("DelaunyTriangle: "+(++counter)+" attempt of checking point in triangle fails");
                }
            }
        }
    }

    private boolean pointOnEdge(Vector2D toAdd) {
        Vector2D AB = m_vec_b.sub(m_vec_a).getNormalisation();
        Vector2D BC = m_vec_c.sub(m_vec_b).getNormalisation();
        Vector2D CA = m_vec_a.sub(m_vec_c).getNormalisation();

        Vector2D AO = toAdd.sub(m_vec_a).getNormalisation();
        Vector2D BO = toAdd.sub(m_vec_b).getNormalisation();
        Vector2D CO = toAdd.sub(m_vec_c).getNormalisation();

        if (AB.equals(AO)) {
            DelaunyTriangle ACZ = new DelaunyTriangle(m_vec_a, m_vec_c, toAdd);
            //ACZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            ACZ.setNeighbor(toAdd, getNeighbor(m_vec_b));
            m_listOfReplacements.add(ACZ);

            DelaunyTriangle BCZ = new DelaunyTriangle(m_vec_b, m_vec_c, toAdd);
            //BCZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            BCZ.setNeighbor(m_vec_b, ACZ);
            BCZ.setNeighbor(toAdd, getNeighbor(m_vec_a));
            m_listOfReplacements.add(BCZ);

            ACZ.setNeighbor(m_vec_a, BCZ);

            DelaunyTriangle realNeighbor = getNeighbor(m_vec_c).lookForTriangleWith(m_vec_a, m_vec_b);
            Vector2D lastPoint = realNeighbor.getDifferentPointTo(m_vec_a,m_vec_b);

            DelaunyTriangle BZL = new DelaunyTriangle(m_vec_b, toAdd, lastPoint);
            //ACZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            BZL.setNeighbor(lastPoint, BCZ);
            BZL.setNeighbor(toAdd, realNeighbor.getNeighbor(m_vec_a));
            realNeighbor.m_listOfReplacements.add(BZL);

            BCZ.setNeighbor(m_vec_c, BZL);

            DelaunyTriangle AZL = new DelaunyTriangle(m_vec_a, toAdd, lastPoint);
            //ACZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            AZL.setNeighbor(lastPoint, ACZ);
            AZL.setNeighbor(toAdd, realNeighbor.getNeighbor(m_vec_b));
            AZL.setNeighbor(m_vec_a, BZL);
            realNeighbor.m_listOfReplacements.add(AZL);

            BZL.setNeighbor(m_vec_b, AZL);
            ACZ.setNeighbor(m_vec_c, AZL);

            BZL.swapTestOn_At(m_vec_b, lastPoint, toAdd);
            BCZ.swapTestOn_At(m_vec_b, m_vec_c, toAdd);
            AZL.swapTestOn_At(m_vec_a, lastPoint, toAdd);
            ACZ.swapTestOn_At(m_vec_a, m_vec_c, toAdd);

            return true;
        } else if (BC.equals(BO)) {
            DelaunyTriangle BAZ = new DelaunyTriangle(m_vec_b, m_vec_a, toAdd);
            //ACZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            BAZ.setNeighbor(toAdd, getNeighbor(m_vec_c));
            m_listOfReplacements.add(BAZ);

            DelaunyTriangle CAZ = new DelaunyTriangle(m_vec_c, m_vec_a, toAdd);
            //BCZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            CAZ.setNeighbor(m_vec_c, BAZ);
            CAZ.setNeighbor(toAdd, getNeighbor(m_vec_b));
            m_listOfReplacements.add(CAZ);

            BAZ.setNeighbor(m_vec_b, CAZ);

            DelaunyTriangle realNeighbor = getNeighbor(m_vec_a).lookForTriangleWith(m_vec_b, m_vec_c);
            Vector2D lastPoint = realNeighbor.getDifferentPointTo(m_vec_b,m_vec_c);

            DelaunyTriangle CZL = new DelaunyTriangle(m_vec_c, toAdd, lastPoint);
            //ACZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            CZL.setNeighbor(lastPoint, CAZ);
            CZL.setNeighbor(toAdd, realNeighbor.getNeighbor(m_vec_b));
            realNeighbor.m_listOfReplacements.add(CZL);
            CAZ.setNeighbor(m_vec_a, CZL);

            DelaunyTriangle BZL = new DelaunyTriangle(m_vec_b, toAdd, lastPoint);
            //ACZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            BZL.setNeighbor(lastPoint, BAZ);
            BZL.setNeighbor(toAdd, realNeighbor.getNeighbor(m_vec_c));
            BZL.setNeighbor(m_vec_b, CZL);
            realNeighbor.m_listOfReplacements.add(BZL);

            CZL.setNeighbor(m_vec_c, BZL);
            BAZ.setNeighbor(m_vec_a, BZL);

            BZL.swapTestOn_At(m_vec_b, lastPoint, toAdd);
            CAZ.swapTestOn_At(m_vec_c, m_vec_a, toAdd);
            CZL.swapTestOn_At(m_vec_c, lastPoint, toAdd);
            BAZ.swapTestOn_At(m_vec_b, m_vec_a, toAdd);

            return true;
        } else if (CA.equals(CO)) {
            DelaunyTriangle CBZ = new DelaunyTriangle(m_vec_c, m_vec_b, toAdd);
            //ACZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            CBZ.setNeighbor(toAdd, getNeighbor(m_vec_a));
            m_listOfReplacements.add(CBZ);

            DelaunyTriangle ABZ = new DelaunyTriangle(m_vec_a, m_vec_b, toAdd);
            //BCZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            ABZ.setNeighbor(m_vec_a, CBZ);
            ABZ.setNeighbor(toAdd, getNeighbor(m_vec_c));
            m_listOfReplacements.add(ABZ);

            CBZ.setNeighbor(m_vec_c, ABZ);

            DelaunyTriangle realNeighbor = getNeighbor(m_vec_b).lookForTriangleWith(m_vec_a, m_vec_c);
            Vector2D lastPoint = realNeighbor.getDifferentPointTo(m_vec_a,m_vec_c);

            DelaunyTriangle AZL = new DelaunyTriangle(m_vec_a, toAdd, lastPoint);
            //ACZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            AZL.setNeighbor(lastPoint, ABZ);
            AZL.setNeighbor(toAdd, realNeighbor.getNeighbor(m_vec_c));
            realNeighbor.m_listOfReplacements.add(AZL);
            ABZ.setNeighbor(m_vec_b, AZL);

            DelaunyTriangle CZL = new DelaunyTriangle(m_vec_c, toAdd, lastPoint);
            //ACZ.setNeighbor(m_vec_c, getNeighbor(m_vec_c));
            CZL.setNeighbor(lastPoint, CBZ);
            CZL.setNeighbor(toAdd, realNeighbor.getNeighbor(m_vec_a));
            CZL.setNeighbor(m_vec_c, AZL);
            realNeighbor.m_listOfReplacements.add(CZL);

            AZL.setNeighbor(m_vec_a, CZL);
            CBZ.setNeighbor(m_vec_b, CZL);

            AZL.swapTestOn_At(m_vec_a, lastPoint, toAdd);
            ABZ.swapTestOn_At(m_vec_a, m_vec_b, toAdd);
            CZL.swapTestOn_At(m_vec_c, lastPoint, toAdd);
            CBZ.swapTestOn_At(m_vec_c, m_vec_b, toAdd);

            return true;
        }
        return false;
    }

    /**
     * This Method tests whether there has to be a swap between two Triangles and returns
     * whether a swap was necessary or not.
     *
     * @param shared_point1
     * @param shared_point2
     * @param last_edge
     * @return
     */
    private boolean swapTestOn_At(Vector2D shared_point1, Vector2D shared_point2, Vector2D last_edge) {
        //Identify Neighbors
        DelaunyTriangle toSwap = getNeighbor(last_edge);

        if (toSwap == null)
            return false;

        DelaunyTriangle realNeighbor = toSwap.lookForTriangleWith(shared_point1, shared_point2);
        Vector2D opposingPoint = realNeighbor.getDifferentPointTo(shared_point1, shared_point2);

        //Finally the Real Swaptest:
        Vector2D midPoint = getMidPointOfOuterCircle();
        if (midPoint.distance(m_vec_a)>midPoint.distance(opposingPoint)) {
            //Here we have to swap
            DelaunyTriangle[] erg = {new DelaunyTriangle(opposingPoint, last_edge, shared_point1),
                    new DelaunyTriangle(opposingPoint, last_edge, shared_point2)};

            //TODO: perhaps the order of neighbors should be corrected
            erg[0].setNeighbor(shared_point1, erg[1]);
            erg[1].setNeighbor(shared_point2, erg[0]);

            erg[0].setNeighbor(opposingPoint, getNeighbor(shared_point2));
            erg[1].setNeighbor(opposingPoint, getNeighbor(shared_point1));

            erg[0].setNeighbor(last_edge, realNeighbor.getNeighbor(shared_point2));
            erg[1].setNeighbor(last_edge, realNeighbor.getNeighbor(shared_point1));

            this.m_listOfReplacements.add(erg[0]);
            this.m_listOfReplacements.add(erg[1]);

            realNeighbor.m_listOfReplacements.add(erg[0]);
            realNeighbor.m_listOfReplacements.add(erg[1]);

            //Perhaps, the swaped triangles has to be swaped again!(not back, but it has tob checked,
            // whether the neighbors are still DelaunyTriangle in kombination with those new ones)
            erg[0].recursiveSwap();
            erg[1].recursiveSwap();

            return true;
        } else {
            return false;
        }
    }

    private void recursiveSwap() {
        Vector2D[] vecs = {m_vec_a,m_vec_b,m_vec_c};
        this.swapTestOn_At(vecs[0],vecs[1],vecs[2]);
        this.swapTestOn_At(vecs[1],vecs[2],vecs[0]);
        this.swapTestOn_At(vecs[2],vecs[0],vecs[1]);
    }

    /**
     * Returns the first founded Vector2D of the three corners, which has not the same coordinates than the parameters.
     * There is no check, whether the Triangle possesses the other Points as Corners.
     * That has to be checked before using this method.
     *
     * @param point_A
     * @param point_B
     * @return
     */
    private Vector2D getDifferentPointTo(Vector2D point_A, Vector2D point_B) {
        return m_thirdPointHelper.get(point_A.add(point_B));
    }

    /**
     * Returns the final DelaunyTrianlge, which shares the same edge described by the two points.
     *
     * If there is no such Triangle in the DelaunyTriangle-Instance, it returns null;
     *
     * @param sharedPoint1
     * @param sharedPoint2
     * @return
     */
    private DelaunyTriangle lookForTriangleWith(Vector2D sharedPoint1, Vector2D sharedPoint2) {
        DelaunyTriangle erg = null;
        if (is_Final() && this.hasVertex(sharedPoint1) && this.hasVertex(sharedPoint2))
            return this;
        for (DelaunyTriangle tri: m_listOfReplacements) {
            if (tri.hasVertex(sharedPoint1)) {
                if (tri.hasVertex(sharedPoint2)) {
                    erg = tri;
                    break;
                }
            }
        }
        if (erg == null)
            return null;
        if (erg.is_Final())
            return erg;
        else
            return erg.lookForTriangleWith(sharedPoint1,sharedPoint2);
    }

    /**
     * Tests whether a certain Vertex has the same coordinates than one of the corners, of the Triangle.
     * @param vert
     * @return
     */
    private boolean hasVertex(Vector2D vert) {
        Vector2D[] temp = {m_vec_a,m_vec_b,m_vec_c};
        for (int i = 0; i<3; i++)
            if (vert.equals(temp[i]))
                return true;
        return false;
    }

    /**
     * Calculate the Midpoint of the Outer Circle of this Triangle.
     * If this point was calculated before, this method will just return the saved value.
     *
     * @return
     */
    public Vector2D getMidPointOfOuterCircle() {
        //Sort by x
        if (m_Midpoint!=null)
            return m_Midpoint;

        Vector2D[] sortedVertices = getSortedVertices();//Slopes are in X-Direction
        Vector2D b_minus_a = sortedVertices[1].sub(sortedVertices[0]);
        Vector2D c_minus_b = sortedVertices[2].sub(sortedVertices[1]);
        Vector2D mid_AB = sortedVertices[0].add(sortedVertices[1]).mult(0.5);
        Vector2D mid_BC = sortedVertices[1].add(sortedVertices[2]).mult(0.5);

//        System.out.println("Ausgabe des Midpoints: ");
//        for (Vector2D vec: sortedVertices) {
//            System.out.println("\t"+vec);
//        }
//        System.out.println("\t"+b_minus_a);
//        System.out.println("\t"+c_minus_b);
//        System.out.println("\tTest: "+b_minus_a.getNormalisation()+"="+c_minus_b.getNormalisation()+":"+b_minus_a.getNormalisation().equals(c_minus_b.getNormalisation()));

        //The 3 points are on the same line
        if (b_minus_a.getNormalisation().equals(c_minus_b.getNormalisation())) {
            m_Midpoint = new Vector2D();
            return m_Midpoint;
        }

        double slope1 = b_minus_a.getSlope(), slope2 = c_minus_b.getSlope();

        if (slope1 == Double.NEGATIVE_INFINITY || slope1 == Double.POSITIVE_INFINITY) {
            slope1 = 0.0;
        } else
            slope1 = -1.0/slope1;
        if (slope2 == Double.NEGATIVE_INFINITY || slope2 == Double.POSITIVE_INFINITY) {
            slope2 = 0.0;
        } else
            slope2 = -1.0/slope2;

        //Second Check, necessary because the normal-vector can be vertical too
        if (slope1 == Double.NEGATIVE_INFINITY || slope1 == Double.POSITIVE_INFINITY) {
            m_Midpoint = new Vector2D(mid_AB.getX(), mid_BC.getY()+slope2*(mid_AB.getX()-mid_BC.getX()));
            return m_Midpoint;
        }
        if (slope2 == Double.NEGATIVE_INFINITY || slope2 == Double.POSITIVE_INFINITY) {
            m_Midpoint = new Vector2D(mid_BC.getX(), mid_AB.getY()+slope1*(mid_BC.getX()-mid_AB.getX()));
            return m_Midpoint;
        }


        double b1 = mid_AB.getY()-mid_AB.getX()*slope1;
        double b2 = mid_BC.getY()-mid_BC.getX()*slope2;

        double x = (b1-b2)/(slope2-slope1);
        double y = slope1*x+b1;

        m_Midpoint = new Vector2D(x,y);
        return m_Midpoint;
    }

    /**
     * Returns the Vertices in an Array sorted by X
     * @return
     */
    private Vector2D[] getSortedVertices() {
        Vector2D[] erg = {m_vec_a,m_vec_b,m_vec_c};
        if (!(erg[0].getX()<=erg[1].getX())) {
            Vector2D temp = erg[0];
            erg[0] = erg[1];
            erg[1] = temp;
        }
        if (!(erg[1].getX()<=erg[2].getX())) {
            Vector2D temp = erg[1];
            erg[1] = erg[2];
            erg[2] = temp;
            if (!(erg[0].getX()<=erg[1].getX())) {
                Vector2D temp2 = erg[0];
                erg[0] = erg[1];
                erg[1] = temp2;
            }
        }
        return erg;
    }

    /**
     * Detect for the DelaunyTriangle-Instance whether a certain Point lies in it or not
     *
     * @param vector
     * @return
     */
    public boolean pointInTriangle(Vector2D vector) {
        Vector2D vec1 = m_vec_b.sub(m_vec_a), vec2, z;

        if (vec1.getX() != 0.0) {
            vec2 = m_vec_c.sub(m_vec_a);

            if (vec2.getX() != 0.0)
                z = vector.sub(m_vec_a);
            else {
                vec1 = m_vec_a.sub(m_vec_b);
                vec2 = m_vec_c.sub(m_vec_b);
                z = vector.sub(m_vec_b);
            }
        } else {
            vec1 = m_vec_b.sub(m_vec_c);
            vec2 = m_vec_a.sub(m_vec_c);
            z = vector.sub(m_vec_c);
        }
        //if (b_Minus_c.getX() == 0.0)
        //    System.out.println("Unexpected Case: A DelaunyTriangle is not Sound!!!");
        //A Special Case that should not been possible

//        double q_part = (z.getX() * vec2.getY()) / vec2.getX();
//        (q_part - z.getY()) / (q_part - vec1.getY());
        double p = (z.getY()*vec2.getX()-z.getX()*vec2.getY())/(vec1.getY()*vec2.getX()-vec1.getX()*vec2.getY());
//        System.out.println("DelaunyTrianlge: P = "+p);
        if (p < 0 || 1 < p)
            return false;

        double q = (z.getX() - (p * vec1.getX())) / vec2.getX();
//        System.out.println("DelaunyTriangle: Q = "+q);
        if (p + q <= 1 && 0 <= q) //TODO: TO CHECK
            return true;
        return false;
    }
// Test
    public void drawVoronoiDiagram(LineDrawer drawer) {
        if (!this.is_Final()) {
            m_listOfReplacements.get(0).drawVoronoiDiagram(drawer);
        } else {//TODO Refactorn: Create a Method instead of doing the same three times
            Vector2D mid_point = this.getMidPointOfOuterCircle();
            DelaunyTriangle neighbor = m_NeighborTriangle.get(m_vec_a), temp;
            if (neighbor!=null) {
                temp = neighbor.lookForTriangleWith(m_vec_b, m_vec_c);
                if (temp != null && drawer.drawLine(mid_point, temp.getMidPointOfOuterCircle())) {
                    //If the Line has to be drawn, this Triangle has to go on with the drawing
                    temp.drawVoronoiDiagram(drawer);// TODO: Marker for Stack-Overflow
                }
            }

            neighbor = m_NeighborTriangle.get(m_vec_c);
            if (neighbor!=null) {
                temp = neighbor.lookForTriangleWith(m_vec_a, m_vec_b);
                if (temp != null && drawer.drawLine(mid_point, temp.getMidPointOfOuterCircle())) {// TODO: Marker for Stack-Overflow-Start
                    temp.drawVoronoiDiagram(drawer);// TODO: Marker for Stack-Overflow
                }
            }

            neighbor = m_NeighborTriangle.get(m_vec_b);
            if (neighbor!=null) {
                temp = neighbor.lookForTriangleWith(m_vec_c, m_vec_a);
                if (temp != null && drawer.drawLine(mid_point, temp.getMidPointOfOuterCircle())) {
                    temp.drawVoronoiDiagram(drawer);
                }
            }
        }
    }

    public void drawDelauny(LineDrawer drawer) {

        if (is_Final()) {
            drawer.drawLine(m_vec_a, m_vec_b);
            drawer.drawLine(m_vec_b, m_vec_c);
            drawer.drawLine(m_vec_c, m_vec_a);

            drawer.drawPoint(getMidPointOfOuterCircle());
            return;
        }
        //for (DelaunyTriangle tri: m_NeighborTriangle.values()) {
        //    if (tri != null)
        //        tri.drawDelauny(drawer);
        //}

        for (DelaunyTriangle tri: m_listOfReplacements) {
            if (tri != null) {
                tri.drawDelauny(drawer);
            }
        }
    }
}
