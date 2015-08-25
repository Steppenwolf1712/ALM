package nz.ac.auckland.alm.swing.test.responsive.graph.alternatives;


import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.swing.responsive.Algebra;
import nz.ac.auckland.alm.swing.responsive.graph.alternatives.AreaInfo;

import java.util.List;

/**
 * Created by Marc Jan�en on 21.08.2015.
 */
public class AeraInfoTest {

    private final String MIDDLE_LEFT = "(A1|{right}C1)/(A2|{left}C2)/(A3|{left}B3|{right}C3)/(A4|{left}C4)";
    private final String PINWHEEL = "(A|(B/{index1}E))/D*B/{index1}C*(E/D)|C";
    private final String THREEBUTTON = "A|B|C";
    private final String GRID = "(A1|{index1}B1|{index2}C1)/(A2|{index1}B2|{index2}C2)/(A3|{index1}B3|{index2}C3)";

    public void testMidLeft() {
        testString(MIDDLE_LEFT);
    }

    public void testPinWheel() {
        testString(PINWHEEL);
    }

    public void testThreeButton() {
        testString(THREEBUTTON);
    }

    public void testGrid() {
        testString(GRID);
    }

    private void testString(String ui) {
        Algebra test = new Algebra(ui);
        System.out.println("Test about \""+ui+"\" :");

        List<IArea> areas = test.getAreas();
        for (IArea area: areas) {
            System.out.println(readInfos(test.getAreaInfo(area), area));
        }
    }

    private String readInfos(AreaInfo info, IArea area) {
        String erg = "Priorized Implode-State of "+area.getId()+":\n\t"+info.getImplodeState().toString();
        erg += "Neighbors:\n\tTop: ";
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
        AeraInfoTest tester = new AeraInfoTest();
        tester.testMidLeft();
        tester.testGrid();
        tester.testPinWheel();
        tester.testThreeButton();
    }
}
