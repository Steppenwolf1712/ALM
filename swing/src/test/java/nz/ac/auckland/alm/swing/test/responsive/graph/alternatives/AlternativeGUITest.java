package nz.ac.auckland.alm.swing.test.responsive.graph.alternatives;

import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.swing.responsive.Algebra;
import nz.ac.auckland.alm.swing.responsive.graph.alternatives.AlternativeGUI;
import nz.ac.auckland.alm.swing.responsive.graph.alternatives.AreaInfo;

import javax.swing.*;

/**
 * Created by Marc Janßen on 10.11.2015.
 */
public class AlternativeGUITest extends AreaInfoTest {

    public static void main(String[] args) {
        AlternativeGUITest tester = new AlternativeGUITest();

        boolean considerNoneImplodes;
        int answer = JOptionPane.showConfirmDialog(null, "Shall the search for Alternative GUIs consider areas which are difficult to remove?",
                "Search for alternative GUIS", JOptionPane.YES_NO_CANCEL_OPTION);

        if (answer == JOptionPane.CANCEL_OPTION)
            return;
        else if (answer == JOptionPane.OK_OPTION)
            considerNoneImplodes = true;
        else //if (answer == JOptionPane.NO_OPTION)
            considerNoneImplodes = false;

        Algebra algebra = tester.testMidLeft();
        for (IArea area: algebra.getAreas()) {
            AreaInfo info = algebra.getAreaInfo(area);

            if (!considerNoneImplodes && info.getImplodeState().getCompareValue()>=AreaInfo.ImplodeState.Horizontal_Implode_None.getCompareValue())
                continue;

            AlternativeGUI gui = new AlternativeGUI(algebra, area, info);

            System.out.println("AlternativeGUI (Without "+area.getId()+")\n");

            System.out.println("\tResulting string := "+gui.getAlternativeGUIString());
        }

    }
}
