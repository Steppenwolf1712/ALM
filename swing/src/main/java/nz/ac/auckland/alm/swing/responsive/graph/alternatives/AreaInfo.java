package nz.ac.auckland.alm.swing.responsive.graph.alternatives;

import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.XTab;
import nz.ac.auckland.alm.YTab;
import nz.ac.auckland.alm.algebra.*;

import java.util.*;

/**
 * Created by Marc Janﬂen on 12.08.2015.
 */
public class AreaInfo {

    List<XTab> m_LeftVisible;
    List<XTab> m_RightVisible;

    List<YTab> m_TopVisible;
    List<YTab> m_BottomVisible;

    private final AlgebraData m_data;
    private ImplodeState horiState;
    private ImplodeState vertiState;

    public AreaInfo(AlgebraData data, IArea area) {
        m_data = data;

        initData();

        readLeftVisible(area);
        readRightVisible(area);

        readTopVisible(area);
        readBottomVisible(area);
    }

    private void initData() {
        m_BottomVisible = new ArrayList<YTab>();
        m_LeftVisible = new ArrayList<XTab>();
        m_RightVisible = new ArrayList<XTab>();
        m_TopVisible = new ArrayList<YTab>();
    }

    private void readBottomVisible(IArea area) {
        readVertical(new BottomDirection(), area, m_BottomVisible);
    }

    private void readTopVisible(IArea area) {
        readVertical(new TopDirection(), area, m_TopVisible);
    }

    private void readRightVisible(IArea area) {
        readHorizontal(new RightDirection(), area, m_RightVisible);
    }

    private void readLeftVisible(IArea area) {
        readHorizontal(new LeftDirection(), area, m_LeftVisible);
    }

    private void readHorizontal(AbstractHorizontalDirection direction, IArea area, List<XTab> xTabList) {
        xTabList.add(direction.getTab(area));
        for (IArea neighbor: getNeighborAreas(direction, area))
            readHorizontal(direction, neighbor, xTabList);
    }

    private void readVertical(AbstractVerticalDirection direction, IArea area, List<YTab> yTabList) {
        yTabList.add(direction.getTab(area));
        for (IArea neighbor: getNeighborAreas(direction, area))
            readVertical(direction, neighbor, yTabList);
    }

    private List<IArea> getNeighborAreas(IDirection direction, IArea area) {
        return direction.getAreas(direction.getEdge(area, direction.getTabEdgeMap(m_data)));
    }

    public void calcRemoveOp(IArea area, Map<IArea, AreaInfo> m_areaInformation) {
        horiState = calcRemoveHorizontalImplodeState(area, m_areaInformation);
        vertiState = calcRemoveVerticalImplodeState(area, m_areaInformation);
    }

    public ImplodeState getImplodeState() {
        if (horiState.priorize(vertiState)<0) {
            return horiState;
        } else
            return vertiState;
    }

    private ImplodeState calcRemoveVerticalImplodeState(IArea area, Map<IArea, AreaInfo> m_areaInformation) {
        IDirection topDirection = new TopDirection();
        List<IArea> topNeighbors = getNeighbors(topDirection, area);
        boolean topIsEmpty = topNeighbors.size() == 0;

        IDirection leftDirection = new LeftDirection();
        IDirection rightDirection = new RightDirection();

        IDirection bottomDirection = new BottomDirection();
        List<IArea> bottomNeighbors = getNeighbors(bottomDirection, area);
        boolean bottomIsEmpty = bottomNeighbors.size() == 0;
        if (topIsEmpty) {
            if (bottomIsEmpty)
                return ImplodeState.Vertical_Implode;
            else
                return ImplodeState.Vertical_Implode_Bottom;
        } else if (bottomIsEmpty)
            return ImplodeState.Vertical_Implode_Top;
        else {
            boolean verticalObstacle = searchForObstacles(topDirection.getOppositeDirection(), topNeighbors, getEdge(topDirection.getOppositeDirection(), area));

            if (verticalObstacle) {
                return ImplodeState.Vertical_Implode_None;
            }

            ArrayList<IArea> leftNeighbors = new ArrayList<IArea>(), rightNeighbors = new ArrayList<IArea>();

            ArrayList<IArea> sum = new ArrayList<IArea>();
            sum.addAll(topNeighbors);
            sum.addAll(bottomNeighbors);

            for (IArea test: sum) {
                rightNeighbors.addAll(getNeighborOf(area, leftDirection, test));
                leftNeighbors.addAll(getNeighborOf(area, rightDirection, test));
            }

            Edge topEdge = getEdge(topDirection, area), botEdge = getEdge(bottomDirection, area);
            if (rightNeighbors.size() > 1) {
                if (leftNeighbors.size() == 1) {
                    if (getEdge(topDirection, leftNeighbors.get(0)).equals(topEdge))
                        return ImplodeState.Vertical_Implode_Middle_Top;
//                    if (getEdge(bottomDirection, leftNeighbors.get(0)).equals(botEdge))
                    else
                        return ImplodeState.Vertical_Implode_Middle_Bottom;
                } else {
                    return ImplodeState.Vertical_Implode_Middle;
                }
            } else if (rightNeighbors.size() == 0) {
                if (leftNeighbors.size() == 1) {
                    if (getEdge(topDirection, leftNeighbors.get(0)).equals(topEdge))
                        return ImplodeState.Vertical_Implode_Top;
//                    if (getEdge(bottomDirection, leftNeighbors.get(0)).equals(botEdge))
                    else
                        return ImplodeState.Vertical_Implode_Bottom;
                } else if (leftNeighbors.size() == 0)
                    return ImplodeState.Vertical_Implode;
                else
                    return ImplodeState.Vertical_Implode_Middle;
            } else {
                if (leftNeighbors.size() == 1) {
                    if (getEdge(topDirection, leftNeighbors.get(0)).equals(topEdge))
                        if (getEdge(topDirection, rightNeighbors.get(0)).equals(topEdge))
                            return ImplodeState.Vertical_Implode_Top;
                        else
                            return ImplodeState.Vertical_Implode_Middle;
                    else
//                    if (getEdge(bottomDirection, leftNeighbors.get(0)).equals(botEdge))
                        if (getEdge(topDirection, rightNeighbors.get(0)).equals(topEdge))
                            return ImplodeState.Vertical_Implode_Middle;
                        else
                            return ImplodeState.Vertical_Implode_Bottom;
//                        return ImplodeState.Vertical_Implode_Middle_Bottom;
//                    else
//                        return ImplodeState.Vertical_Implode_Middle;
                } else if (leftNeighbors.size() == 0)
                    if (getEdge(topDirection, rightNeighbors.get(0)).equals(topEdge))
                        return ImplodeState.Vertical_Implode_Top;
                    else
                        return ImplodeState.Vertical_Implode_Bottom;
                else if (getEdge(topDirection, rightNeighbors.get(0)).equals(topEdge))
                    return ImplodeState.Vertical_Implode_Middle_Top;
                else
                    return ImplodeState.Vertical_Implode_Middle_Bottom;
            }

//            return ImplodeState.Vertical_Implode_Middle;
        }
    }

    private Collection<IArea> getNeighborOf(IArea source, IDirection directionToSource, IArea test) {
        Collection<IArea> erg = new HashSet<IArea>();

        List<IArea> test_List = getNeighborAreas(directionToSource, test);
        for (IArea left: test_List)
            if (left.equals(source))
                erg.add(test);

        return erg;
    }

    private ImplodeState calcRemoveHorizontalImplodeState(IArea area, Map<IArea, AreaInfo> m_areaInformation) {
        IDirection leftDirection = new LeftDirection();
        List<IArea> leftNeighbors = getNeighbors(leftDirection, area);
        boolean leftIsEmpty = leftNeighbors.size() == 0;

        IDirection topDirection = new TopDirection();
        IDirection bottomDirection = new BottomDirection();

        IDirection rightDirection = new RightDirection();
        List<IArea> rightNeighbors = getNeighbors(rightDirection, area);
        boolean rightIsEmpty = rightNeighbors.size() == 0;
        if (leftIsEmpty) {
            if (rightIsEmpty)
                return ImplodeState.Horizontal_Implode;
            else
                return ImplodeState.Horizontal_Implode_Right;
        } else if (rightIsEmpty)
            return ImplodeState.Horizontal_Implode_Left;
        else {
            boolean horizontalObstacle = searchForObstacles(leftDirection.getOppositeDirection(), leftNeighbors, getEdge(leftDirection.getOppositeDirection(), area));

            if (horizontalObstacle) {
                return ImplodeState.Horizontal_Implode_None;
            }

            ArrayList<IArea> topNeighbors = new ArrayList<IArea>(), bottomNeighbors = new ArrayList<IArea>();

            ArrayList<IArea> sum = new ArrayList<IArea>();
            sum.addAll(leftNeighbors);
            sum.addAll(rightNeighbors);

            for (IArea test: sum) {
                topNeighbors.addAll(getNeighborOf(area, bottomDirection, test));
                bottomNeighbors.addAll(getNeighborOf(area, topDirection, test));
            }

            Edge leftEdge = getEdge(leftDirection, area), rightEdge = getEdge(rightDirection, area);
            if (topNeighbors.size() > 1) {
                if (bottomNeighbors.size() == 1) {
                    if (getEdge(leftDirection, bottomNeighbors.get(0)).equals(leftEdge))
                        return ImplodeState.Horizontal_Implode_Middle_Left;
//                    if (getEdge(rightDirection, bottomNeighbors.get(0)).equals(rightEdge))
                    else
                        return ImplodeState.Horizontal_Implode_Middle_Right;
                } else {
                    return ImplodeState.Horizontal_Implode_Middle;
                }
            } else if (topNeighbors.size() == 0) {
                if (bottomNeighbors.size() == 1) {
                    if (getEdge(leftDirection, bottomNeighbors.get(0)).equals(leftEdge))
                        return ImplodeState.Horizontal_Implode_Left;
//                    if (getEdge(bottomDirection, leftNeighbors.get(0)).equals(botEdge))
                    else
                        return ImplodeState.Horizontal_Implode_Right;
                } else if (bottomNeighbors.size() == 0)
                    return ImplodeState.Horizontal_Implode;
                else
                    return ImplodeState.Horizontal_Implode_Middle;
            } else {
                if (bottomNeighbors.size() == 1) {
                    if (getEdge(leftDirection, bottomNeighbors.get(0)).equals(leftEdge))
                        if (getEdge(leftDirection, topNeighbors.get(0)).equals(leftEdge))
                            return ImplodeState.Horizontal_Implode_Left;
                        else
                            return ImplodeState.Horizontal_Implode_Middle;
                    else
//                    if (getEdge(rightDirection, topNeighbors.get(0)).equals(rightEdge))
                        if (getEdge(leftDirection, topNeighbors.get(0)).equals(leftEdge))
                            return ImplodeState.Horizontal_Implode_Middle;
                        else
                            return ImplodeState.Horizontal_Implode_Right;
//                        return ImplodeState.Vertical_Implode_Middle_Bottom;
//                    else
//                        return ImplodeState.Vertical_Implode_Middle;
                } else if (bottomNeighbors.size() == 0)
                    if (getEdge(leftDirection, topNeighbors.get(0)).equals(leftEdge))
                        return ImplodeState.Horizontal_Implode_Left;
                    else
                        return ImplodeState.Horizontal_Implode_Right;
                else if (getEdge(leftDirection, topNeighbors.get(0)).equals(leftEdge))
                    return ImplodeState.Horizontal_Implode_Middle_Left;
                else
                    return ImplodeState.Horizontal_Implode_Middle_Right;
            }

//            return ImplodeState.Vertical_Implode_Middle;
        }
    }

    private boolean searchForObstacles(IDirection direction, List<IArea> areas, Edge edgeToCheck) {
        for (IArea area: areas) {
            Edge toCompare = getEdge(direction, area);
            if (toCompare.equals(edgeToCheck)) {
                return true;
            } else {
                List<IArea> nextAreas = direction.getAreas(toCompare);
                if (searchForObstacles(direction, nextAreas, edgeToCheck))
                    return true;
            }
        }
        return false;
    }


    /**
     * This Method returns all IArea Objects which are on the same side of an certain Tabstop than the area parameter.
     * The parameter Area, will not be mentioned at the result list.
     *
     * @param direc
     * @param area
     * @return
     */
    private List<IArea> getNeighbors(IDirection direc, IArea area) {
        Edge topEdge = getEdge(direc, area);
        List<IArea> allAreas = direc.getOppositeAreas(topEdge);
        List<IArea> erg = new ArrayList<IArea>();
        for (IArea temp: allAreas)
            if (!temp.equals(area))
                erg.add(temp);
        return  erg;
    }

    private Edge getEdge(IDirection direc, IArea area) {
        return direc.getEdge(area, direc.getTabEdgeMap(m_data));
    }

    public enum ImplodeState {
        Vertical_Implode(1), Vertical_Implode_Top(3), Vertical_Implode_Bottom(3),
        Vertical_Implode_Middle(5), Vertical_Implode_Middle_Top(7), Vertical_Implode_Middle_Bottom(7), Vertical_Implode_None(9),
        Horizontal_Implode(0), Horizontal_Implode_Left(2), Horizontal_Implode_Right(2),
        Horizontal_Implode_Middle(4), Horizontal_Implode_Middle_Left(6), Horizontal_Implode_Middle_Right(6), Horizontal_Implode_None(8);

        private int m_compareValue;

        ImplodeState(int compareValue) {
            m_compareValue = compareValue;
        }

        public int getCompareValue() {
            return m_compareValue;
        }

        public int priorize(ImplodeState state) {
            return Integer.compare(this.m_compareValue, state.getCompareValue());
        }

        public String toString() {
            return this.name();
        }
    }
}
