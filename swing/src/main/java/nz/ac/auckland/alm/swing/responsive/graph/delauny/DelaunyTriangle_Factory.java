package nz.ac.auckland.alm.swing.responsive.graph.delauny;


import nz.ac.auckland.alm.swing.responsive.graph.ResponsiveGUIGraph;
import nz.ac.auckland.alm.swing.responsive.graph.Vector2D;

import java.awt.*;

/**
 * Created by Marc Janßen on 25.06.2015.
 */
public class DelaunyTriangle_Factory {

    public static DelaunyTriangle createDelaunyTriangle(Point a, Point b) {

        Vector2D dim = new Vector2D(ResponsiveGUIGraph.DIM_SIZE);
        double minDistance = dim.getLength()*2* ResponsiveGUIGraph.MAX_SCALE;

        /*Vector2D baseVec = new Vector2D(-1.0,-1.0).mult(minDistance);

        double longerSide = (dim.getX()<dim.getY()?dim.getY(): dim.getX());

        Vector2D baseTop = new Vector2D(baseVec.getX()+10, (longerSide*2)-baseVec.getY());
        Vector2D baseRight = new Vector2D((longerSide*2)-baseVec.getX(), baseVec.getY()+10);
        System.out.println("DelaunyTriangle:\n\tBase= "+baseVec+"\n\tTop= "+baseTop+"\n\tRight= "+baseRight);*/


        Vector2D[] new_vertices = new Vector2D[3];
        Vector2D thirdPoint = new Vector2D(a).add(b).mult(0.5);
        Vector2D normal = new Vector2D(a).sub(b).getNormalVector();
        thirdPoint = thirdPoint.add(normal);
        Vector2D[] orig_vertices = {new Vector2D(a),new Vector2D(b),thirdPoint};
        //new Vector2D(c)};//Thats not working, if the three Points are on a Line. So there has to be a third Point derived from the first two
        Vector2D direc, middle = new Vector2D(a).add(b).add(thirdPoint);
        middle = middle.mult(1.0/3);
        for (int i = 0; i<3; i++) {
            direc = orig_vertices[i].sub(middle).getNormalisation();
            new_vertices[i] = middle.add(direc.mult(minDistance));
        }

        //DelaunyTriangle erg = new DelaunyTriangle(baseVec,baseTop,baseRight);
        DelaunyTriangle erg = new DelaunyTriangle(new_vertices[0], new_vertices[1], new_vertices[2]);

        erg.addPoint(new Vector2D(a));
        erg.addPoint(new Vector2D(b));

        return erg;
    }

    public static DelaunyTriangle createDelaunyTriangle(Point a, Point b, Point c) {

        Vector2D dim = new Vector2D(ResponsiveGUIGraph.DIM_SIZE);
        double minDistance = dim.getLength()*2*ResponsiveGUIGraph.MAX_SCALE;

        /*Vector2D baseVec = new Vector2D(-1.0,-1.0).mult(minDistance);

        double longerSide = (dim.getX()<dim.getY()?dim.getY(): dim.getX());

        Vector2D baseTop = new Vector2D(baseVec.getX()+10, (longerSide*2)-baseVec.getY());
        Vector2D baseRight = new Vector2D((longerSide*2)-baseVec.getX(), baseVec.getY()+10);
        System.out.println("DelaunyTriangle:\n\tBase= "+baseVec+"\n\tTop= "+baseTop+"\n\tRight= "+baseRight);*/


        Vector2D[] new_vertices = new Vector2D[3];
        Vector2D thirdPoint = new Vector2D(a).add(b).mult(0.5);
        Vector2D normal = new Vector2D(a).sub(b).getNormalVector();
        thirdPoint = thirdPoint.add(normal);
        Vector2D[] orig_vertices = {new Vector2D(a),new Vector2D(b),thirdPoint};
        //new Vector2D(c)};//Thats not working, if the three Points are on a Line. So there has to be a third Point derived from the first two
        Vector2D direc, middle = new Vector2D(a).add(b).add(thirdPoint);
        middle = middle.mult(1.0/3);
        for (int i = 0; i<3; i++) {
            direc = orig_vertices[i].sub(middle).getNormalisation();
            new_vertices[i] = middle.add(direc.mult(minDistance));
        }

        //DelaunyTriangle erg = new DelaunyTriangle(baseVec,baseTop,baseRight);
        DelaunyTriangle erg = new DelaunyTriangle(new_vertices[0], new_vertices[1], new_vertices[2]);

        erg.addPoint(new Vector2D(a));
        erg.addPoint(new Vector2D(b));
        erg.addPoint(new Vector2D(c));

        return erg;
    }
}
