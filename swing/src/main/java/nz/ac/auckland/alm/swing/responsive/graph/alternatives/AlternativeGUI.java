package nz.ac.auckland.alm.swing.responsive.graph.alternatives;

import nz.ac.auckland.alm.*;
import nz.ac.auckland.alm.algebra.*;
import nz.ac.auckland.alm.algebra.string.AlgebraSpec;
import nz.ac.auckland.alm.algebra.string.StringWriter;
import nz.ac.auckland.alm.swing.responsive.ALMPanel;
import nz.ac.auckland.alm.swing.responsive.Algebra;
import nz.ac.auckland.alm.swing.responsive.HalfEdge;
import nz.ac.auckland.alm.swing.responsive.graph.Token;
import nz.ac.auckland.linsolve.Variable;

import java.awt.Color;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc Jan�en on 12.08.2015.
 */
public class AlternativeGUI {

    private static final String DIREC_TOP = "^";
    private static final String DIREC_BOT = "v";
    private static final String DIREC_LEFT = "<";
    private static final String DIREC_RIGHT= ">";
    private static final String DIREC_NONE = "#none#";

    public static final String S_IMPLODE_NONE = "Empty-String";

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

    /**
     * This Method just sets Informations about the Changes of this alternative into the text of the certain buttons
     *
     * @param panel
     */
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
            case Horizontal_Implode_local_Left:
                panel.change_Button_Text(id, DIREC_LEFT, DIREC_LEFT);
                break;
            case Horizontal_Implode_Right:
            case Horizontal_Implode_local_Right:
                panel.change_Button_Text(id, DIREC_RIGHT, DIREC_RIGHT);
                break;
            case Horizontal_Implode_None:
            case Vertical_Implode_None:
                panel.change_Button_Text(id, DIREC_NONE, DIREC_NONE);
                break;
            case Vertical_Implode_local_Bottom:
            case Vertical_Implode_Bottom:
                panel.change_Button_Text(id, DIREC_BOT, DIREC_BOT);
                break;
            case Vertical_Implode:
            case Vertical_Implode_Middle:
                panel.change_Button_Text(id, DIREC_TOP, DIREC_BOT);
                break;
            case Vertical_Implode_Top:
            case Vertical_Implode_local_Top:
                panel.change_Button_Text(id, DIREC_TOP, DIREC_TOP);
                break;
            default:

                break;
        }
    }

    /**
     * This method does the "Magic"! It converts the GUI to a String, calculate the necessarily changes to reach the
     * alternative and applies them. The returned String holds a UIA-description of the alternative GUI.
     *
     * @return
     */
    public String getAlternativeGUIString() {
        String erg = "";

        AreaInfo.ImplodeState state = this.m_info.getImplodeState();
        IArea area = this.m_info.getAreaOfConcern();

        ArrayList<HalfEdge> toAdd = new ArrayList<HalfEdge>(), toDel = new ArrayList<HalfEdge>();
        List<IArea> temp_neighbors = null, temp_oppositeNeighbors = null, temp_neighbors2 = null;
        IDirection direc = null;

        LayoutSpec spec = new LayoutSpec();

        Map<Variable,Variable> tabMap = new HashMap<Variable, Variable>();

        tabMap.put(m_algebra.getLeft(), spec.getLeft());
        tabMap.put(m_algebra.getTop(), spec.getTop());
        tabMap.put(m_algebra.getRight(), spec.getRight());
        tabMap.put(m_algebra.getBottom(), spec.getBottom());

        System.out.println("AlternativeGUI: building Alternative for String =\n\t"+m_algebra.getM_UIAstring()+"\n\twithout Area "+
                m_area.getId()+" and ImplodeState "+m_info.getImplodeState());

        switch (state) {
            case Horizontal_Implode:
            case Horizontal_Implode_Left:
                //temp_neighbors = m_info.getRightNeighbors();
                direc = new RightDirection();
                temp_neighbors = m_info.getRightNeighbors();//direc.getAreas(m_algebra.getEdge(direc, area));
                Variable newTab = direc.getOppositeTab(area);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                for (IArea temp: temp_neighbors)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);
                temp_oppositeNeighbors = m_info.getOppositeRightNeighbors();
                temp_neighbors.addAll(temp_oppositeNeighbors);

                toDel.addAll(calcHEtoDel(temp_oppositeNeighbors, direc.getOppositeDirection()));
                toAdd.addAll(calcHEtoAdd(temp_oppositeNeighbors, direc.getOppositeDirection(), newTab));

                for (IArea temp: temp_oppositeNeighbors)
                    addAreaToLayout(tabMap, temp, spec, direc, newTab);
                break;
            case Horizontal_Implode_Right:
                direc = new LeftDirection();
                temp_neighbors = m_info.getLeftNeighbors();
                newTab = direc.getOppositeTab(area);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                for (IArea temp: temp_neighbors)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);

                temp_oppositeNeighbors = m_info.getOppositeLeftNeighbors();
                temp_neighbors.addAll(temp_oppositeNeighbors);

                toDel.addAll(calcHEtoDel(temp_oppositeNeighbors, direc.getOppositeDirection()));
                toAdd.addAll(calcHEtoAdd(temp_oppositeNeighbors, direc.getOppositeDirection(), newTab));

                for (IArea temp: temp_oppositeNeighbors)
                    addAreaToLayout(tabMap, temp, spec, direc, newTab);
                break;
            case Horizontal_Implode_Middle:
                direc = new RightDirection();
                temp_neighbors = m_info.getRightNeighbors();
                newTab = new XTab();

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                for (IArea temp: temp_neighbors)
                        addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);

                direc = direc.getOppositeDirection();
                temp_neighbors2 = m_info.getLocalLeftNeighbors();

                for (IArea temp: temp_neighbors2)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                temp_neighbors.addAll(temp_neighbors2);

                if (m_info.getVertiImplodeState().equals(AreaInfo.ImplodeState.Vertical_Implode_Middle)) {
                    direc = new BottomDirection();
                    temp_neighbors2 = m_info.getBottomNeighbors();
                    temp_neighbors.addAll(temp_neighbors2);
                    newTab = new YTab();

                    toDel.addAll(calcHEtoDel(temp_neighbors2, direc));
                    toDel.addAll(getHEofArea(area));
                    toAdd.addAll(calcHEtoAdd(temp_neighbors2, direc, newTab));

                    for (IArea temp: temp_neighbors2)
                        addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);

                    direc = direc.getOppositeDirection();
                    temp_neighbors2 = m_info.getTopNeighbors();
                    temp_neighbors.addAll(temp_neighbors2);

                    for (IArea temp: temp_neighbors2)
                        addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);

                    toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                    toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));
                }
                break;
            case Horizontal_Implode_local_Left:
                direc = new RightDirection();
                temp_neighbors = m_info.getLocalRightNeighbors();//direc.getAreas(m_algebra.getEdge(direc, area));
                newTab = direc.getOppositeTab(area);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

//                Add new HalfEdges of allAreas which are to expand
                for (IArea temp: temp_neighbors)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);
                break;
            case Horizontal_Implode_local_Right:
                direc = new LeftDirection();
                temp_neighbors = m_info.getLocalLeftNeighbors();//direc.getAreas(m_algebra.getEdge(direc, area));
                newTab = direc.getOppositeTab(area);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                for (IArea temp: temp_neighbors)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);
                break;
            case Vertical_Implode:
            case Vertical_Implode_Top:
                //temp_neighbors = m_info.getRightNeighbors();
                direc = new BottomDirection();
                temp_neighbors = m_info.getBottomNeighbors();//direc.getAreas(m_algebra.getEdge(direc, area));
                newTab = direc.getOppositeTab(area);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                for (IArea temp: temp_neighbors)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);
                temp_oppositeNeighbors = m_info.getOppositeBottomNeighbors();
                temp_neighbors.addAll(temp_oppositeNeighbors);

                toDel.addAll(calcHEtoDel(temp_oppositeNeighbors, direc.getOppositeDirection()));
                toAdd.addAll(calcHEtoAdd(temp_oppositeNeighbors, direc.getOppositeDirection(), newTab));

                for (IArea temp: temp_oppositeNeighbors)
                    addAreaToLayout(tabMap, temp, spec, direc, newTab);
                break;
            case Vertical_Implode_Bottom:
                direc = new TopDirection();
                temp_neighbors = m_info.getTopNeighbors();//direc.getAreas(m_algebra.getEdge(direc, area));
                newTab = direc.getOppositeTab(area);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                for (IArea temp: temp_neighbors)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);
                temp_oppositeNeighbors = m_info.getOppositeTopNeighbors();
                temp_neighbors.addAll(temp_oppositeNeighbors);

                toDel.addAll(calcHEtoDel(temp_oppositeNeighbors, direc.getOppositeDirection()));
                toAdd.addAll(calcHEtoAdd(temp_oppositeNeighbors, direc.getOppositeDirection(), newTab));

                for (IArea temp: temp_oppositeNeighbors)
                    addAreaToLayout(tabMap, temp, spec, direc, newTab);
                break;
            case Vertical_Implode_Middle:
                direc = new BottomDirection();
                temp_neighbors = m_info.getLocalBottomNeighbors();//direc.getAreas(m_algebra.getEdge(direc, area));
                newTab = new YTab();

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                for (IArea temp: temp_neighbors)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);

                direc = direc.getOppositeDirection();
                temp_neighbors2 = m_info.getLocalTopNeighbors();

                for (IArea temp: temp_neighbors2)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
//                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                temp_neighbors.addAll(temp_neighbors2);
                //There is no chance that, the vertical and horizontal ImplodeState will be "Middle" here,
                //because if that would be the case, the Horizontal_Implode_State would be the active case.
                break;
            case Vertical_Implode_local_Top:
                direc = new BottomDirection();
                temp_neighbors = m_info.getLocalBottomNeighbors();//direc.getAreas(m_algebra.getEdge(direc, area));
                newTab = direc.getOppositeTab(area);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                for (IArea temp: temp_neighbors)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);
                break;
            case Vertical_Implode_local_Bottom:
                direc = new TopDirection();
                temp_neighbors = m_info.getLocalTopNeighbors();//direc.getAreas(m_algebra.getEdge(direc, area));
                newTab = direc.getOppositeTab(area);

                toDel.addAll(calcHEtoDel(temp_neighbors, direc));
                toDel.addAll(getHEofArea(area));
                toAdd.addAll(calcHEtoAdd(temp_neighbors, direc, newTab));

                for (IArea temp: temp_neighbors)
                    addAreaToLayout(tabMap, temp, spec, direc.getOppositeDirection(), newTab);
                break;
            case Horizontal_Implode_None:
            case Vertical_Implode_None:
                return S_IMPLODE_NONE;
        }
        List<IArea> areas = m_algebra.getAreas();

        List<IArea> set_without_conflict = getAreasWithout(areas, temp_neighbors, area);

        for (IArea part: set_without_conflict) {
            addAreaToLayout(tabMap, spec, part);
        }

        AlgebraData data = new AlgebraData(spec, null);
        AlgebraSpec alSpec = new AlgebraSpec(data);
        alSpec.compress();

        return StringWriter.write(alSpec);
        /*
        ArrayList<HalfEdge> hEdges = this.m_algebra.getHalfEdges(), newSet = new ArrayList<HalfEdge>();

        for (HalfEdge he: hEdges) {
            if (!toDel.contains(he))
                newSet.add(he);
        }
        newSet.addAll(toAdd);

        HashMap<String, CheckEdge> checkEdges = new HashMap<String, CheckEdge>();
        for (HalfEdge temp_he: newSet) {

            String key = temp_he.getTab().getName();
            if (checkEdges.containsKey(key)) {
                checkEdges.get(key).addHalfEdge(temp_he);
            } else {
                checkEdges.put(key, new CheckEdge(temp_he));
            }
        }

        erg = generateGUIAlgebraString(checkEdges.values());

        return erg;*/
    }

    private void addAreaToLayout(Map<Variable, Variable> tabMap, IArea temp, LayoutSpec spec, IDirection direc, Variable newTab) {
        XTab left = null, right = null;
        YTab top = null, bottom = null;
        try {
            if (direc instanceof LeftDirection) {
                left = getXTabOutOfMap(tabMap, (XTab) newTab);
                top = getYTabOutOfMap(tabMap, temp.getTop());
                right = getXTabOutOfMap(tabMap, temp.getRight());
                bottom = getYTabOutOfMap(tabMap, temp.getBottom());
            } else if (direc instanceof TopDirection) {
                left = getXTabOutOfMap(tabMap, temp.getLeft());
                top = getYTabOutOfMap(tabMap, (YTab) newTab);
                right = getXTabOutOfMap(tabMap, temp.getRight());
                bottom = getYTabOutOfMap(tabMap, temp.getBottom());
            } else if (direc instanceof RightDirection) {
                left = getXTabOutOfMap(tabMap, temp.getLeft());
                top = getYTabOutOfMap(tabMap, temp.getTop());
                right = getXTabOutOfMap(tabMap, (XTab) newTab);
                bottom = getYTabOutOfMap(tabMap, temp.getBottom());
            } else {
                left = getXTabOutOfMap(tabMap, temp.getLeft());
                top = getYTabOutOfMap(tabMap, temp.getTop());
                right = getXTabOutOfMap(tabMap, temp.getRight());
                bottom = getYTabOutOfMap(tabMap, (YTab) newTab);
            }
            Area area = spec.addArea(left, top, right, bottom);
            area.setId(temp.getId());
        } catch (RuntimeException ex) {
            System.out.println("AlternativeGUI: Error while adding Area "+temp.getId()+" to Alternative-GUI!\n\t" +
                    "Left := "+left+"\n\t"+
                    "Top := "+top+"\n\t"+
                    "Right := "+right+"\n\t"+
                    "Bottom := "+bottom+"\n\t");
        }
    }

    private void addAreaToLayout(Map<Variable, Variable> tabMap, LayoutSpec spec, IArea part) {
        Area area = spec.addArea(getXTabOutOfMap(tabMap, part.getLeft()),
                getYTabOutOfMap(tabMap, part.getTop()),
                getXTabOutOfMap(tabMap, part.getRight()),
                getYTabOutOfMap(tabMap, part.getBottom()));
        area.setId(part.getId());
    }

    private Variable getTabOutOfMap(Map<Variable, Variable> tabMap, Variable var) {
        if (tabMap.containsKey(var)) {
            return tabMap.get(var);
        } else
            return null;
    }

    private XTab getXTabOutOfMap(Map<Variable, Variable> tabMap, XTab right) {
        Variable erg = getTabOutOfMap(tabMap, right);
        if (erg == null) {
            tabMap.put(right, new XTab());
            return (XTab)tabMap.get(right);
        }
        return (XTab) erg;
    }

    private YTab getYTabOutOfMap(Map<Variable, Variable> tabMap, YTab top) {
        Variable erg = getTabOutOfMap(tabMap, top);
        if (erg == null) {
            tabMap.put(top, new YTab());
            return (YTab)tabMap.get(top);
        }
        return (YTab) erg;
    }

    private List<IArea> getAreasWithout(List<IArea> areas, List<IArea> temp_neighbors, IArea area) {
        List<IArea> erg = new ArrayList<IArea>();
        boolean shallAdd = true;
        for (IArea temp: areas) {
            shallAdd = true;
            if (temp.equals(area))
                continue;
            for (IArea toCompare: temp_neighbors) {
                if (temp.equals(toCompare)) {
                    shallAdd = false;
                    break;
                }
            }
            if (shallAdd)
                erg.add(temp);
        }
        return erg;
    }

    private String generateGUIAlgebraString(Collection<CheckEdge> newSet) {
        String erg = "";

        Iterator<CheckEdge> iter = newSet.iterator();

        CheckEdge temp = iter.next();
        if (temp == null)
            return null;
        erg += temp.toString();

        while (iter.hasNext()) {
            erg += Token.LEXTOKEN_OP_CONCAT;
            erg += iter.next().toString();
        }

        return erg;
    }

    /**
     * This method returns a set of HalfEdges, which consist of all combinations of a new tabstop with a set of Areas.
     * The Area-Objects shall be on a certain side(direc) of the new tabstop.
     *
     * @param neighbors
     * @param direc
     * @param newTab
     * @return
     */
    private Collection<? extends HalfEdge> calcHEtoAdd(List<IArea> neighbors, IDirection direc, Variable newTab) {
        ArrayList<HalfEdge> erg = new ArrayList<HalfEdge>();
        for (IArea temp: neighbors)
            erg.add(new HalfEdge(temp, newTab, direc.getOppositeDirection()));
        return erg;
    }

    /**
     * This method returns a set of HalfEdges, which consist of all Halfedges of a set of areas with a certain direction(!direc).
     *
     * @param neighbors
     * @param direc
     * @return
     */
    private Collection<? extends HalfEdge> calcHEtoDel(List<IArea> neighbors, IDirection direc) {
        ArrayList<HalfEdge> erg = new ArrayList<HalfEdge>();
        for (IArea temp: neighbors)
            erg.add(new HalfEdge(temp, direc.getOppositeDirection()));
        return erg;
    }

    private Collection<? extends HalfEdge> getHEofArea(IArea area) {
        ArrayList<HalfEdge> erg = new ArrayList<HalfEdge>();
        IDirection direc = new TopDirection();
        erg.add(new HalfEdge(area, direc));
        erg.add(new HalfEdge(area, direc.getOppositeDirection()));
        erg.add(new HalfEdge(area, direc.getOrthogonalDirection1()));
        erg.add(new HalfEdge(area, direc.getOrthogonalDirection2()));
        return erg;
    }

    public String toString() {
        return "Remove Area \""+m_area.getId()+"\"";
    }

    private class CheckEdge {
        private final String m_edgeID;
        private final boolean isHorizontal;
        private ArrayList<IArea> top_left_Areas;
        private ArrayList<IArea> bot_right_Areas;

        public CheckEdge(HalfEdge start) {
            IArea start_area = start.getArea();
            IDirection direc = start.getDirection();

            top_left_Areas = new ArrayList<IArea>();
            bot_right_Areas = new ArrayList<IArea>();

            String edgeID = direc.getTab(start_area).getName();

            if (edgeID == null) {
                int index = direc.getTab(start_area).getIndex();
                direc.getTab(start_area).setName(""+index);
                edgeID = ""+index;
            }
            m_edgeID = edgeID;

            if (direc instanceof TopDirection) {
                isHorizontal = false;

                top_left_Areas.add(start_area);
            } else if (direc instanceof BottomDirection) {
                isHorizontal = false;

                bot_right_Areas.add(start_area);
            } else if (direc instanceof LeftDirection) {
                isHorizontal = true;

                top_left_Areas.add(start_area);
            } else {
                isHorizontal = true;

                bot_right_Areas.add(start_area);
            }
        }

        private boolean isCompatible(HalfEdge he) {
            IArea area = he.getArea();
            IDirection direc = he.getDirection();


            String compare = direc.getTab(area).getName();
            if (compare == null)
                compare = ""+direc.getTab(area).getIndex();

            //if (compare == null)
            //    System.out.println("AlternativeGUI.isCompatible: Hier l�uft was schief!!!\n\tDirec := "+direc.toString()+" Area := "+area.getId());

            boolean b1 = compare.equals(m_edgeID);
            boolean b2 = (isHorizontal == directionIsHorizontal(direc));

            return b1 && b2;
        }

        public boolean addHalfEdge(HalfEdge he) {
            if (!isCompatible(he))
                return false;

            boolean he_is_leftSide = heIsLeftUIA(he.getDirection());
            IArea area = he.getArea();

            if (he_is_leftSide)
                top_left_Areas.add(area);
            else
                bot_right_Areas.add(area);

            return true;
        }

        private boolean directionIsHorizontal(IDirection direc) {
            return direc instanceof LeftDirection || direc instanceof RightDirection;
        }

        private boolean heIsLeftUIA(IDirection direc) {
            return direc instanceof TopDirection || direc instanceof LeftDirection;
        }

        public boolean equals(Object obj) {
            if (obj instanceof CheckEdge)
                return m_edgeID.equals(((CheckEdge) obj).m_edgeID);
            return false;
        }

        public String toString() {
            String erg = "";

            if (top_left_Areas.isEmpty() || bot_right_Areas.isEmpty())
                return erg;

            String rightPart = (isHorizontal? ""+Token.LEXTOKEN_OP_HORI_TILING:""+Token.LEXTOKEN_OP_VERT_TILING);
            rightPart += Token.LEXTOKEN_TILING_INDEX_OPEN+m_edgeID+Token.LEXTOKEN_TILING_INDEX_CLOSE;
            String rightString = rightPart + bot_right_Areas.get(0).getId();

            for (IArea leftSideArea: top_left_Areas) {
                erg += leftSideArea.getId()+rightString;
                erg += Token.LEXTOKEN_OP_CONCAT;
            }

            String leftString = top_left_Areas.get(0) + rightPart;

            for (int i = 1; i<bot_right_Areas.size(); i++) {
                erg += leftString+bot_right_Areas.get(i).getId();
                erg += Token.LEXTOKEN_OP_CONCAT;
            }

            return erg.substring(0, erg.length()-1);
        }
    }
}
