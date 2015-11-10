package nz.ac.auckland.alm.swing.responsive;

import nz.ac.auckland.alm.Area;
import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.XTab;
import nz.ac.auckland.alm.algebra.*;
import nz.ac.auckland.alm.algebra.string.Parser;
import nz.ac.auckland.alm.algebra.string.StringReader;
import nz.ac.auckland.alm.swing.ALMLayout;
import nz.ac.auckland.alm.swing.responsive.graph.alternatives.AlternativeGUI;
import nz.ac.auckland.alm.swing.responsive.graph.alternatives.AreaInfo;
import nz.ac.auckland.linsolve.Variable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marc Janßen on 16.07.2015.
 */
public class Algebra {

    private final String m_UIAstring;
    private final AlgebraData m_Algebra;
    private ALMLayout m_layout = null;

    private Map<IArea, AreaInfo> m_AreaInformation = null;

    public Algebra(String definition) {
        m_UIAstring = definition;

        m_layout = new ALMLayout();

        m_Algebra = StringReader.read(definition,
                m_layout.getLeft(),
                m_layout.getTop(),
                m_layout.getRight(),
                m_layout.getBottom(),
                Parser.getDefaultAreaFactory());

        createAbstraction();
    }

    private void createAbstraction() {
        m_AreaInformation = new HashMap<IArea, AreaInfo>();

        List<IArea> areas = getAreas();

        for (IArea area: areas) {
            m_AreaInformation.put(area, new AreaInfo(m_Algebra, area));
        }

        for (IArea area: areas) {
            m_AreaInformation.get(area).initiateInformation(area, m_AreaInformation);
        }
    }

    public AreaInfo getAreaInfo(IArea about) {
        return m_AreaInformation.get(about);
    }

    public AlgebraData getAlgebraDataCopy(ALMLayout layout) {
        return StringReader.read(m_UIAstring,
                layout.getLeft(),
                layout.getTop(),
                layout.getRight(),
                layout.getBottom(),
                Parser.getDefaultAreaFactory());
    }

    public String getM_UIAstring() {
        return m_UIAstring;
    }

    public List<IArea> getAreas() {
        return m_Algebra.getAllAreasList();
    }

    ALMLayout getLayout() {
        return m_layout;
    }

    public Dimension getPreferredSize(Container con) {
        return m_layout.preferredLayoutSize(con);
    }

    public AlternativeGUI[] getAlternatives() {
        ArrayList<AlternativeGUI> erg = new ArrayList<AlternativeGUI>();

        for (Map.Entry<IArea, AreaInfo> set: m_AreaInformation.entrySet()){
            erg.add(new AlternativeGUI(this, set.getKey(), set.getValue()));
        }

        return erg.toArray(new AlternativeGUI[erg.size()]);
    }

    /**
     * Returns all HalfEdges without those of the frame.
     *
     * @return
     */
    public ArrayList<HalfEdge> getHalfEdges() {
        ArrayList<HalfEdge> halfEdges = new ArrayList<HalfEdge>();

        //IDirection topDirec = new TopDirection(), botDirec = new BottomDirection(),
        //       leftDirec = new LeftDirection(), rightDirec = new RightDirection();

        Variable topTab = m_Algebra.getTop(), botTab = m_Algebra.getBottom(),
                leftTab = m_Algebra.getLeft(), rightTab = m_Algebra.getRight(),
                temp = null;

        Iterable<IArea> iter = m_Algebra.getAllAreas();
        for (IArea area: iter) {
            temp = area.getTop();
            if (!temp.equals(topTab))
                halfEdges.add(new HalfEdge(area, new TopDirection()));
            temp = area.getBottom();
            if (!temp.equals(botTab))
                halfEdges.add(new HalfEdge(area, new BottomDirection()));
            temp = area.getLeft();
            if (!temp.equals(leftTab))
                halfEdges.add(new HalfEdge(area, new LeftDirection()));
            temp = area.getRight();
            if (!temp.equals(rightTab))
                halfEdges.add(new HalfEdge(area, new RightDirection()));
        }

        return halfEdges;
    }

    public Edge getEdge(IDirection direc, IArea area) {
        return direc.getEdge(area, direc.getTabEdgeMap(m_Algebra));
    }



    //TODO: There is maybe the need of an intelligent Equals method to evaluate whether the AlgebraFrameFactory has initiate a new JFrame or not
}
