package nz.ac.auckland.alm.swing.test.responsive.graph.alternatives;


import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.swing.responsive.Algebra;
import nz.ac.auckland.alm.swing.responsive.graph.alternatives.AreaInfo;

import java.util.List;

/**
 * Created by Marc Jan�en on 21.08.2015.
 */
public class AreaInfoTest {

    public final String MIDDLE_LEFT = "(A1|{right}C1)/(A2|{left}C2)/(A3|{left}B3|{right}C3)/(A4|{left}C4)";
    public final String PINWHEEL = "(A|(B/{index1}E))/{index2}D*B/{index1}C*(E/{index2}D)|C";
    public final String THREEBUTTON = "A|B|C";
    public final String GRID = "(A1|{index1}B1|{index2}C1)/(A2|{index1}B2|{index2}C2)/(A3|{index1}B3|{index2}C3)";

    public Algebra testMidLeft() {
        return testString(MIDDLE_LEFT);
    }

    public Algebra testPinWheel() {
        return testString(PINWHEEL);
    }

    public Algebra testThreeButton() {
        return testString(THREEBUTTON);
    }

    public Algebra testGrid() {
        return testString(GRID);
    }

    Algebra testString(String ui) {
        Algebra test = new Algebra(ui);
        System.out.println("Test about \""+ui+"\" :");

        List<IArea> areas = test.getAreas();
        for (IArea area: areas) {
            System.out.println(readInfos(test.getAreaInfo(area), area));
        }
        return test;
    }

    String readInfos(AreaInfo info, IArea area) {
        String erg = "Priorized Implode-State of "+area.getId()+":\n\t"+info.getImplodeState().toString();
        erg += "\nNeighbors:\n\tTop: ";
        for (IArea temp: info.getTopNeighbors())
            erg += temp.getId()+",";
        erg += "\n";
        erg += "\tLeft: ";
        for (IArea temp: info.getLeftNeighbors())
            erg += temp.getId()+",";
        erg += "\n";
        erg += "\tBottom: ";
        for (IArea temp: info.getBottomNeighbors())
            erg += temp.getId()+",";
        erg += "\n";
        erg += "\tRight: ";
        for (IArea temp: info.getRightNeighbors())
            erg += temp.getId()+",";
        erg += "\n";
        return erg;
    }

    public static void main(String[] args) {
        AreaInfoTest tester = new AreaInfoTest();
        tester.testMidLeft();
        tester.testGrid();
        tester.testPinWheel();
        tester.testThreeButton();
    }
}
