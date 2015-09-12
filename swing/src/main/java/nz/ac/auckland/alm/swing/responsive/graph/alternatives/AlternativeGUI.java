package nz.ac.auckland.alm.swing.responsive.graph.alternatives;

import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.swing.responsive.ALMPanel;
import nz.ac.auckland.alm.swing.responsive.Algebra;

import java.awt.*;

/**
 * Created by Marc Janﬂen on 12.08.2015.
 */
public class AlternativeGUI {

    private static final String DIREC_TOP = "^";
    private static final String DIREC_BOT = "v";
    private static final String DIREC_LEFT = "<";
    private static final String DIREC_RIGHT= ">";
    private static final String DIREC_NONE = "#none#";

    private final Algebra m_algebra;
    private final IArea m_area;
    private final AreaInfo m_info;

    public AlternativeGUI(Algebra mainGUI, IArea areaToRemove, AreaInfo area_information) {
        m_algebra = mainGUI;
        m_area = areaToRemove;
        m_info = area_information;
    }

    public String getDescription() {
        return prepareDescription(m_info.getDescription(), m_area.getId());
    }

    private String prepareDescription(String description, String areaID) {
        return description.replace(AreaInfo.S_AREA_PLACEHOLDER, areaID);
    }

    public void configurePanel(ALMPanel panel) {
        panel.reset_Complete();

        String id = m_area.getId();
        panel.change_Button_Color(id, Color.RED);
        switch (m_info.getImplodeState()) {
            case Horizontal_Implode:
            case Horizontal_Implode_Middle:
                panel.change_Button_Text(id, DIREC_RIGHT, DIREC_LEFT);
                break;
            case Horizontal_Implode_Left:
            case Horizontal_Implode_Middle_Left:
                panel.change_Button_Text(id, DIREC_LEFT, DIREC_LEFT);
                break;
            case Horizontal_Implode_Right:
            case Horizontal_Implode_Middle_Right:
                panel.change_Button_Text(id, DIREC_RIGHT, DIREC_RIGHT);
                break;
            case Horizontal_Implode_None:
            case Vertical_Implode_None:
                panel.change_Button_Text(id, DIREC_NONE, DIREC_NONE);
                break;
            case Vertical_Implode_Middle_Bottom:
            case Vertical_Implode_Bottom:
                panel.change_Button_Text(id, DIREC_BOT, DIREC_BOT);
                break;
            case Vertical_Implode:
            case Vertical_Implode_Middle:
                panel.change_Button_Text(id, DIREC_TOP, DIREC_BOT);
                break;
            case Vertical_Implode_Top:
            case Vertical_Implode_Middle_Top:
                panel.change_Button_Text(id, DIREC_TOP, DIREC_TOP);
                break;
            default:

                break;
        }
    }

    public String toString() {
        return "Remove Area \""+m_area.getId()+"\"";
    }
}
