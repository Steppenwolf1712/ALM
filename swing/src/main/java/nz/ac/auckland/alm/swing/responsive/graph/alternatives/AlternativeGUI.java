package nz.ac.auckland.alm.swing.responsive.graph.alternatives;

import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.swing.responsive.Algebra;

/**
 * Created by Marc Janﬂen on 12.08.2015.
 */
public class AlternativeGUI {


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

    public String toString() {
        return "Remove Area \""+m_area.getId()+"\"";
    }
}
