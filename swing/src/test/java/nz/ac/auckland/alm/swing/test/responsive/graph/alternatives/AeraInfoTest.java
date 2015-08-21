package nz.ac.auckland.alm.swing.test.responsive.graph.alternatives;


import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.swing.responsive.Algebra;
import nz.ac.auckland.alm.swing.responsive.graph.alternatives.AreaInfo;

import java.util.List;

/**
 * Created by Marc Janﬂen on 21.08.2015.
 */
public class AeraInfoTest {

    private final String PINWHEEL = "(A|(B/{index1}E))/D*B/{index1}C*(E/D)|C";
    private final String THREEBUTTON = "A|B|C";
    private final String GRID = "(A1|{index1}B1|{index2}C1)/(A2|{index1}B2|{index2}C2)/(A3|{index1}B3|{index2}C3)";

    public void testPinWheel() {
        Algebra test = new Algebra(PINWHEEL);

        System.out.println("Test about \""+PINWHEEL+"\" :");
        
        List<IArea> areas = test.getAreas();
        for (IArea area: areas) {
            System.out.println(readInfos(test.getAreaInfo(area), area));
        }
    }

    public void testThreeButton() {
        Algebra test = new Algebra(THREEBUTTON);
        System.out.println("Test about \""+THREEBUTTON+"\" :");

        List<IArea> areas = test.getAreas();
        for (IArea area: areas) {
            System.out.println(readInfos(test.getAreaInfo(area), area));
        }

    }

    public void testGrid() {
        Algebra test = new Algebra(GRID);
        System.out.println("Test about \""+GRID+"\" :");

        List<IArea> areas = test.getAreas();
        for (IArea area: areas) {
            System.out.println(readInfos(test.getAreaInfo(area), area));
        }
    }

    private String readInfos(AreaInfo info, IArea area) {
        return "Priorized Implode-State of "+area.getId()+":\n\t"+info.getImplodeState().toString();
    }

    public static void main(String[] args) {
        AeraInfoTest tester = new AeraInfoTest();
        tester.testGrid();
        tester.testPinWheel();
        tester.testThreeButton();
    }
}
