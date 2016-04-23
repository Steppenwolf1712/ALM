package nz.ac.auckland.alm.swing.responsive.graph.alternatives;

import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.algebra.*;

import java.util.*;

/**
 * Created by Marc Janï¿½en on 12.08.2015.
 */
public class AreaInfo {

    private final IArea m_area;
    List<Edge> m_LeftVisible;
    List<Edge> m_RightVisible;

    List<Edge> m_TopVisible;
    List<Edge> m_BottomVisible;

    List<IArea> m_leftNeighbors;
//    List<IArea> m_localLeftNeighbors;
    List<IArea> m_rightNeighbors;
//    List<IArea> m_localRightNeighbors;

    List<IArea> m_topNeighbors;
//    List<IArea> m_localTopNeighbors;
    List<IArea> m_botNeighbors;
//    List<IArea> m_localBotNeighbors;

    private boolean verticalObstacle;
    private boolean horizontalObstacle;

    public List<IArea> getLeftNeighbors() {
        IDirection direc = new LeftDirection();
        return getNeighborAreas(direc, m_area);
    }
    public List<IArea> getLocalLeftNeighbors() {
        return m_leftNeighbors;
    }

    public List<IArea> getRightNeighbors() {
        IDirection direc = new RightDirection();
        return getNeighborAreas(direc, m_area);
    }
    public List<IArea> getLocalRightNeighbors() {
        return m_rightNeighbors;
    }

    public List<IArea> getTopNeighbors() {
        IDirection direc = new TopDirection();
        return getNeighborAreas(direc, m_area);
    }
    public List<IArea> getLocalTopNeighbors() {
        return m_topNeighbors;
    }

    public List<IArea> getBottomNeighbors() {
        IDirection direc = new BottomDirection();
        return getNeighborAreas(direc, m_area);
    }
    public List<IArea> getLocalBottomNeighbors() {
        return m_botNeighbors;
    }

    private final AlgebraData m_data;
    private ImplodeState horiState;
    private ImplodeState vertiState;

    public static final String S_AREA_PLACEHOLDER = "§§§";


    public AreaInfo(AlgebraData data, IArea area) {
        m_data = data;
        m_area = area;

        initData();

        readLeftVisible(area);
        readRightVisible(area);

        readTopVisible(area);
        readBottomVisible(area);
    }

    private void initData() {
        m_BottomVisible = new ArrayList<Edge>();
        m_LeftVisible = new ArrayList<Edge>();
        m_RightVisible = new ArrayList<Edge>();
        m_TopVisible = new ArrayList<Edge>();

        m_leftNeighbors = new ArrayList<IArea>();
        m_rightNeighbors = new ArrayList<IArea>();
        m_topNeighbors = new ArrayList<IArea>();
        m_botNeighbors = new ArrayList<IArea>();
    }

    public IArea getAreaOfConcern() {
        return m_area;
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

    private void readHorizontal(AbstractHorizontalDirection direction, IArea area, List<Edge> xTabList) {
        xTabList.add(getEdge(direction, area));
        for (IArea neighbor: getNeighborAreas(direction, area))
            readHorizontal(direction, neighbor, xTabList);
    }

    private void readVertical(AbstractVerticalDirection direction, IArea area, List<Edge> yTabList) {
        yTabList.add(getEdge(direction, area));
        for (IArea neighbor: getNeighborAreas(direction, area))
            readVertical(direction, neighbor, yTabList);
    }

    private List<IArea> getNeighborAreas(IDirection direction, IArea area) {
        return direction.getAreas(direction.getEdge(area, direction.getTabEdgeMap(m_data)));
    }

    public boolean hasHorizontalObstacles() {
        return horizontalObstacle;
    }

    public boolean hasVerticalObstacles() {
        return verticalObstacle;
    }

    public void initiateInformation(IArea area, Map<IArea, AreaInfo> m_areaInformation) {
        calcNeighbors(area, m_areaInformation);

        horiState = calcRemoveHorizontalImplodeState(area, m_areaInformation);
        vertiState = calcRemoveVerticalImplodeState(area, m_areaInformation);
    }

    private void calcNeighbors(IArea area, Map<IArea, AreaInfo> m_areaInformation) {
        Collection<IArea> horizontal = new HashSet<IArea>(), vertical = new HashSet<IArea>();

        IDirection topDirec = new TopDirection();

        horizontal.addAll(topDirec.getOppositeDirection().getAreas(getEdge(topDirec, area)));
        //horizontal.addAll(topDirec.getAreas(getEdge(topDirec.getOppositeDirection(), area)));//Not necessary...
        // to catch all neighbors that touches the same side of the observed area and ONLY this side,
        // there has to be an Area which shares the same top AND Bottom tabstop. Otherwise the Area Combination is just to big
        // and touches also others areas.

        IDirection leftDirec = new LeftDirection();

        vertical.addAll(leftDirec.getOppositeDirection().getAreas(getEdge(leftDirec, area)));
        //vertical.addAll(leftDirec.getAreas(getEdge(leftDirec.getOppositeDirection(), area)));

        Edge leftSide = getEdge(leftDirec, area), rightSide = getEdge(leftDirec.getOppositeDirection(), area),
                topSide = getEdge(topDirec, area), botSide = getEdge(topDirec.getOppositeDirection(), area);

        IArea leftTopNeighbor = null, rightTopNeighbor = null, space = null;
        for (IArea hori: horizontal) {
            space = isNeighborOf(area, leftDirec, hori);
            if (space != null)
                rightTopNeighbor = space;
            space = isNeighborOf(area, leftDirec.getOppositeDirection(), hori);
            if (space != null)
                leftTopNeighbor = space;
        }

        IArea topLeftNeighbor = null, botLeftNeighbor = null;
        for (IArea verti: vertical) {
            space = isNeighborOf(area, topDirec, verti);
            if (space != null)
                botLeftNeighbor = space;
            space = isNeighborOf(area, topDirec.getOppositeDirection(), verti);
            if (space != null)
                topLeftNeighbor = space;
        }

        ArrayList<IArea> temp = null;
        if (rightTopNeighbor != null && m_areaInformation.get(rightTopNeighbor).m_BottomVisible.contains(botSide)) {
            temp = getNeighborsAlongArea(area, rightTopNeighbor, botSide, topDirec.getOppositeDirection(), leftDirec);
            if (temp != null && temp.size() != 0)
                m_rightNeighbors.addAll(temp);
        }
        if (leftTopNeighbor != null && m_areaInformation.get(leftTopNeighbor).m_BottomVisible.contains(botSide)) {
            temp = getNeighborsAlongArea(area, leftTopNeighbor, botSide, topDirec.getOppositeDirection(), leftDirec.getOppositeDirection());
            if (temp != null && temp.size() != 0)
                m_leftNeighbors.addAll(temp);
        }
        if (topLeftNeighbor != null && m_areaInformation.get(topLeftNeighbor).m_RightVisible.contains(rightSide)) {
            temp = getNeighborsAlongArea(area, topLeftNeighbor, rightSide, leftDirec.getOppositeDirection(), topDirec.getOppositeDirection());
            if (temp != null && temp.size() != 0)
                m_topNeighbors.addAll(temp);
        }
        if (botLeftNeighbor != null && m_areaInformation.get(botLeftNeighbor).m_RightVisible.contains(rightSide)) {
            temp = getNeighborsAlongArea(area, botLeftNeighbor, rightSide, leftDirec.getOppositeDirection(), topDirec);
            if (temp != null && temp.size() != 0)
                m_botNeighbors.addAll(temp);
        }
    }

    /**
     * This method returns a set of areas which are local neighbors to an area, starting with the startArea and searching
     * in a certain direction. While searching the areas will be proved as neighbors with the information about the direction of
     * the neighborship (along shall be the direction from the neighbors to the main area)
     *
     * @param area
     * @param startArea
     * @param endEdge
     * @param searchDirection
     * @param along
     * @return
     */
    private ArrayList<IArea> getNeighborsAlongArea(IArea area, IArea startArea, Edge endEdge, IDirection searchDirection, IDirection along) {
        ArrayList<IArea> rNeighbors = new ArrayList<IArea>();

        IArea space = startArea;
        rNeighbors.add(space);
        List<IArea> tempList;

        while (!getEdge(searchDirection, space).equals(endEdge)) {
            tempList = getNeighborAreas(searchDirection, space);
            if (tempList.isEmpty())
                    return null;
            for (IArea test : tempList) {
                space = isNeighborOf(area, along, test);
                if (space != null)
                    break;
            }
            if (space == null)
                return null;

            rNeighbors.add(space);
        }

        return rNeighbors;
    }

    public ImplodeState getImplodeState() {
        ImplodeState erg = null;

        if (horiState.priority(vertiState)<0) {
           erg = horiState;
        } else
            erg = vertiState;

        if (erg.m_compareValue == ImplodeState.Horizontal_Implode_local_LeftBot.m_compareValue) {
//               && erg.m_compareValue <= ImplodeState.Vertical_Implode_local_TopRight.m_compareValue) {
            if (erg.equals(ImplodeState.Horizontal_Implode_local_RightBot)) {
                if (getVertiImplodeState().equals(ImplodeState.Vertical_Implode_local_TopRight))
                    return ImplodeState.Vertical_Implode_local_Top;
                return ImplodeState.Horizontal_Implode_Right;
            }
            if (erg.equals(ImplodeState.Horizontal_Implode_local_RightTop)) {
                if (getVertiImplodeState().equals(ImplodeState.Vertical_Implode_local_BottomRight))
                    return ImplodeState.Vertical_Implode_Bottom;
                return ImplodeState.Horizontal_Implode_Right;
            }
            if (erg.equals(ImplodeState.Horizontal_Implode_local_LeftTop)) {
                if (getVertiImplodeState().equals(ImplodeState.Vertical_Implode_local_BottomLeft))
                    return ImplodeState.Vertical_Implode_Bottom;
                return ImplodeState.Horizontal_Implode_Left;
            }
            if (erg.equals(ImplodeState.Horizontal_Implode_local_LeftBot)) {
                if (getVertiImplodeState().equals(ImplodeState.Vertical_Implode_local_TopLeft))
                    return ImplodeState.Vertical_Implode_local_Top;
                return ImplodeState.Horizontal_Implode_Left;
            }
        }
        if (erg.m_compareValue == ImplodeState.Vertical_Implode_local_BottomLeft.m_compareValue) {
            if (erg.equals(ImplodeState.Vertical_Implode_local_BottomLeft)
                    || erg.equals(ImplodeState.Vertical_Implode_local_BottomRight))
                return ImplodeState.Vertical_Implode_local_Bottom;
            else
                return ImplodeState.Vertical_Implode_local_Top;
        }
        return erg;
    }

    public ImplodeState getHoriImplodeState() {
        return horiState;
    }

    public ImplodeState getVertiImplodeState() {
        return vertiState;
    }

    private ImplodeState calcRemoveVerticalImplodeState(IArea area, Map<IArea, AreaInfo> areaInformation) {
        IDirection topDirection = new TopDirection();
        List<IArea> topNeighbors = getOrthoNeighbors(topDirection, area);
        boolean topIsEmpty = topNeighbors.size() == 0;

        IDirection leftDirection = new LeftDirection();
        IDirection rightDirection = new RightDirection();

        IDirection bottomDirection = new BottomDirection();
        List<IArea> bottomNeighbors = getOrthoNeighbors(bottomDirection, area);
        boolean bottomIsEmpty = bottomNeighbors.size() == 0;
        if (topIsEmpty) {
            if (bottomIsEmpty)
                return ImplodeState.Vertical_Implode;
            else
                return ImplodeState.Vertical_Implode_Bottom;
        } else if (bottomIsEmpty)
            return ImplodeState.Vertical_Implode_Top;
        else {
            verticalObstacle = false;//searchForObstacles(topDirection.getOppositeDirection(), topNeighbors, getEdge(topDirection.getOppositeDirection(), area));
            Edge toCheck = getEdge(topDirection.getOppositeDirection(), area);
            for (IArea temp: topNeighbors)
                if (areaInformation.get(temp).m_BottomVisible.contains(toCheck)) {
                    verticalObstacle = true;
                    break;
                }

//            This Code has to be removed, while it destroys nearly every check and most of the restults wuold be none!
//            if (verticalObstacle) {
//                return ImplodeState.Vertical_Implode_None;
//            }

            ArrayList<IArea> leftNeighbors = new ArrayList<IArea>(), rightNeighbors = new ArrayList<IArea>();

            ArrayList<IArea> sum = new ArrayList<IArea>();
            sum.addAll(topNeighbors);
            sum.addAll(bottomNeighbors);

            IArea space = null;
            for (IArea test: sum) {
                space = isNeighborOf(area, leftDirection, test);
                if (space != null)
                    rightNeighbors.add(space);
                space = isNeighborOf(area, rightDirection, test);
                if (space != null)
                    leftNeighbors.add(space);
            }

            Edge topEdge = getEdge(topDirection, area), botEdge = getEdge(bottomDirection, area);
            if (rightNeighbors.size() > 1) {
                if (leftNeighbors.size() == 1) {
                    if (getEdge(topDirection, leftNeighbors.get(0)).equals(topEdge))
                        return ImplodeState.Vertical_Implode_local_TopRight;

                    else
                        return ImplodeState.Vertical_Implode_local_BottomRight;
                } else {
                    return ImplodeState.Vertical_Implode_None;
//                    return ImplodeState.Vertical_Implode_Middle;//First version
                }
            } else if (rightNeighbors.size() == 0) {
                if (leftNeighbors.size() == 1) {
                    if (getEdge(topDirection, leftNeighbors.get(0)).equals(topEdge))

                        if (verticalObstacle)
                            return ImplodeState.Vertical_Implode_local_Top;
                        else
                            return ImplodeState.Vertical_Implode_Top;

                    else
                        if (verticalObstacle)
                            return ImplodeState.Vertical_Implode_local_Bottom;
                        else
                            return ImplodeState.Vertical_Implode_Bottom;

                } else if (leftNeighbors.size() == 0)

                    if (verticalObstacle)
                        return ImplodeState.Vertical_Implode_local_Top;
                    else
                        return ImplodeState.Vertical_Implode;

                else
                    return ImplodeState.Vertical_Implode_None;
//                    return ImplodeState.Vertical_Implode_Middle;((First Version
            } else {
                boolean rightTop = getEdge(topDirection, rightNeighbors.get(0)).equals(topEdge);
                if (leftNeighbors.size() == 1) {
                    if (getEdge(topDirection, leftNeighbors.get(0)).equals(topEdge))
                        if (rightTop)
                            if (verticalObstacle)
                                return ImplodeState.Vertical_Implode_local_Top;
                            else
                                 return ImplodeState.Vertical_Implode_Top;
                    else
                        if (!rightTop)
                            if (verticalObstacle)
                                return ImplodeState.Vertical_Implode_local_Bottom;
                            else
                                return ImplodeState.Vertical_Implode_Bottom;

                    if (!verticalObstacle)
                        return ImplodeState.Vertical_Implode_Middle;//First Version;
                    else
                        return ImplodeState.Implode_None;
                } else if (leftNeighbors.size() == 0)
                    if (rightTop)
                        if (verticalObstacle)
                            return ImplodeState.Vertical_Implode_local_Top;
                        else
                            return ImplodeState.Vertical_Implode_Top;
                    else
                        if (verticalObstacle)
                            return ImplodeState.Vertical_Implode_local_Bottom;
                        else
                            return ImplodeState.Vertical_Implode_Bottom;
                else if (rightTop)
                    return ImplodeState.Vertical_Implode_local_TopLeft;
                else
                    return ImplodeState.Vertical_Implode_local_BottomLeft;
            }
        }
    }

    /**
     * This Method returns the IArea test, if source is a Neighbor of the IArea-Object in the given IDirection.
     * Null otherwise!
     *
     * @param source
     * @param directionToSource
     * @param test
     * @return
     */
    private IArea isNeighborOf(IArea source, IDirection directionToSource, IArea test) {
        IArea erg = null;

        List<IArea> test_List = getNeighborAreas(directionToSource, test);
        for (IArea item: test_List)
            if (item.equals(source)) {
                return test;//erg.add(test);
                //break;
            }

        return erg;
    }

    private ImplodeState calcRemoveHorizontalImplodeState(IArea area, Map<IArea, AreaInfo> areaInformation) {
        IDirection leftDirection = new LeftDirection();
        List<IArea> leftNeighbors = getOrthoNeighbors(leftDirection, area);
        boolean leftIsEmpty = leftNeighbors.size() == 0;

        IDirection topDirection = new TopDirection();
        IDirection bottomDirection = new BottomDirection();

        IDirection rightDirection = new RightDirection();
        List<IArea> rightNeighbors = getOrthoNeighbors(rightDirection, area);
        boolean rightIsEmpty = rightNeighbors.size() == 0;
        if (leftIsEmpty) {
            if (rightIsEmpty)
                return ImplodeState.Horizontal_Implode;
            else
                return ImplodeState.Horizontal_Implode_Right;
        } else if (rightIsEmpty)
            return ImplodeState.Horizontal_Implode_Left;
        else {
            horizontalObstacle = false;//searchForObstacles(leftDirection.getOppositeDirection(), leftNeighbors, getEdge(leftDirection.getOppositeDirection(), area));
            Edge toCheck = getEdge(leftDirection.getOppositeDirection(), area);
            for (IArea temp: leftNeighbors)
                if (areaInformation.get(temp).m_RightVisible.contains(toCheck)) {
                    horizontalObstacle = true;
                    break;
                }


//            This Code has to be removed, while it destroys nearly every check and most of the restults wuold be none!
//            if (horizontalObstacle) {
//                return ImplodeState.Horizontal_Implode_None;
//            }

            ArrayList<IArea> topNeighbors = new ArrayList<IArea>(), bottomNeighbors = new ArrayList<IArea>();

            ArrayList<IArea> sum = new ArrayList<IArea>();
            sum.addAll(leftNeighbors);
            sum.addAll(rightNeighbors);

            IArea space = null;
            for (IArea test: sum) {
                space = isNeighborOf(area, bottomDirection, test);
                if (space != null)
                    topNeighbors.add(space);
                space = isNeighborOf(area, topDirection, test);
                if (space != null)
                    bottomNeighbors.add(space);
            }

            Edge leftEdge = getEdge(leftDirection, area), rightEdge = getEdge(rightDirection, area);
            if (topNeighbors.size() > 1) {
                if (bottomNeighbors.size() == 1) {
                    if (getEdge(leftDirection, bottomNeighbors.get(0)).equals(leftEdge))
                        return ImplodeState.Horizontal_Implode_local_LeftTop;
                    else
                        return ImplodeState.Horizontal_Implode_local_RightTop;
                } else {
                    return ImplodeState.Horizontal_Implode_None;
                }
            } else if (topNeighbors.size() == 0) {
                if (bottomNeighbors.size() == 1) {
                    if (getEdge(leftDirection, bottomNeighbors.get(0)).equals(leftEdge))
                        if (horizontalObstacle)
                            return ImplodeState.Horizontal_Implode_local_Left;
                        else
                            return ImplodeState.Horizontal_Implode_Left;

                    else
                        if (horizontalObstacle)
                            return ImplodeState.Horizontal_Implode_local_Right;
                        else
                            return ImplodeState.Horizontal_Implode_Right;

                } else if (bottomNeighbors.size() == 0)

                    if (horizontalObstacle)
                        return ImplodeState.Horizontal_Implode_local_Left;
                    else
                        return ImplodeState.Horizontal_Implode;

                else
                    return ImplodeState.Horizontal_Implode_None;

            } else {
                boolean leftTop = getEdge(leftDirection, topNeighbors.get(0)).equals(leftEdge);
                if (bottomNeighbors.size() == 1) {
                    if (getEdge(leftDirection, bottomNeighbors.get(0)).equals(leftEdge))
                        if (leftTop)
                            if (horizontalObstacle)
                                return ImplodeState.Horizontal_Implode_local_Left;
                            else
                                return ImplodeState.Horizontal_Implode_Left;
                    else
                        if (!leftTop)
                            if (horizontalObstacle)
                                return ImplodeState.Horizontal_Implode_local_Right;
                            else
                                return ImplodeState.Horizontal_Implode_Right;

                    if (!horizontalObstacle)
                        return ImplodeState.Horizontal_Implode_Middle;
                    else
                        return ImplodeState.Implode_None;
                } else if (bottomNeighbors.size() == 0)
                    if (leftTop)
                        if (horizontalObstacle)
                            return ImplodeState.Horizontal_Implode_local_Left;
                        else
                            return ImplodeState.Horizontal_Implode_Left;
                    else
                        if (horizontalObstacle)
                            return ImplodeState.Horizontal_Implode_local_Right;
                        else
                            return ImplodeState.Horizontal_Implode_Right;
                else if (leftTop)
                    return ImplodeState.Horizontal_Implode_local_LeftBot;
                else
                    return ImplodeState.Horizontal_Implode_local_RightBot;
            }
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
    private List<IArea> getOrthoNeighbors(IDirection direc, IArea area) {
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

    public String getDescription() {
        return getImplodeState().getDescription();
    }

    public enum ImplodeState {
        Vertical_Implode(1, "(Vertical-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed and the two tabstops on top and under the area, will be merged together. There are no obstacles, between the affected tabstops."),
        Vertical_Implode_Top(3, "(Top-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed and the tabstop at the bottom of the area will be merged into the top-tabstop. There are no obstacles, between the affected tabstops."),
        Vertical_Implode_Bottom(3, "(Bottom-Merge)The Area "+S_AREA_PLACEHOLDER+" will be removed and the tabstop at the top of the area will be merged into the bottom-tabstop. There are no obstacles between the affected tabstops."),
        Vertical_Implode_Middle(5, "(Local-Vertical-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict vertical merge. Because of that, only the local, horizontal neighbors of the area shall get a new tabstop. The new tabstop lies between the old top and bottom tabstops."),
        Vertical_Implode_local_Top(7, "(Local-Top-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict vertical merge. Because of that, only the local, horizontal neighbors of the area shall get connected to the top-tabstop."),
        Vertical_Implode_local_Bottom(7, "(Local-Bottom-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict vertical merge. Because of that, only the local, horizontal neighbors of the area shall get connected to the bottom-tabstop."),
        Vertical_Implode_local_TopLeft(9, "(Local-Top-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict vertical merge. Because of that, only the local, horizontal neighbors of the area shall get connected to the top-tabstop."),
        Vertical_Implode_local_TopRight(9, "(Local-Top-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict vertical merge. Because of that, only the local, horizontal neighbors of the area shall get connected to the top-tabstop."),
        Vertical_Implode_local_BottomLeft(9, "(Local-Bottom-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict vertical merge. Because of that, only the local, horizontal neighbors of the area shall get connected to the bottom-tabstop."),
        Vertical_Implode_local_BottomRight(9, "(Local-Bottom-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict vertical merge. Because of that, only the local, horizontal neighbors of the area shall get connected to the bottom-tabstop."),
        Vertical_Implode_None(11, "(No operation) There were only obstacles found, while searching for a possibility to remove Area "+S_AREA_PLACEHOLDER),
        Horizontal_Implode(0, "(Horizontal-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed and the two tabstops on the left and one the right side of the area, will be merged together. There are no obstacles, between the affected tabstops."),
        Horizontal_Implode_Left(2, "(Left-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed and the tabstop at the right side of the area will be merged into the left-tabstop. There are no obstacles, between the affected tabstops."),
        Horizontal_Implode_Right(2, "(Right-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed and the tabstop at the left of the area will be merged into the right-tabstop. There are no obstacles between the affected tabstops."),
        Horizontal_Implode_Middle(4, "(Local-Horizontal-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict horizontal merge. Because of that, only the local, vertical neighbors of the area shall get a new tabstop. The new tabstop lies between the old left and right tabstops."),
        Horizontal_Implode_local_Left(6, "(Local-Left-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict horizontal merge. Because of that, only the local, vertical neighbors of the area shall get connected to the left-tabstop."),
        Horizontal_Implode_local_Right(6, "(Local-Right-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict horizontal merge. Because of that, only the local, vertical neighbors of the area shall get connected to the right-tabstop."),
        Horizontal_Implode_local_LeftTop(8, "(Local-Left-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict horizontal merge. Because of that, only the local, vertical neighbors of the area shall get connected to the left-tabstop."),
        Horizontal_Implode_local_LeftBot(8, "(Local-Left-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict horizontal merge. Because of that, only the local, vertical neighbors of the area shall get connected to the left-tabstop."),
        Horizontal_Implode_local_RightTop(8, "(Local-Right-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict horizontal merge. Because of that, only the local, vertical neighbors of the area shall get connected to the right-tabstop."),
        Horizontal_Implode_local_RightBot(8, "(Local-Right-Merge) The Area "+S_AREA_PLACEHOLDER+" will be removed. There are affected areas which can scale up the whole GUI while a strict horizontal merge. Because of that, only the local, vertical neighbors of the area shall get connected to the right-tabstop."),
        Horizontal_Implode_None(10, "(No operation) There were only obstacles found, while searching for a possibility to remove the Area "+S_AREA_PLACEHOLDER),
        Implode_None(12, "(No operation) There were only obstacles found, while searching for a possibility to remove the Area "+S_AREA_PLACEHOLDER);

        private int m_compareValue;
        private String m_description;

        ImplodeState(int compareValue, String description) {
            m_compareValue = compareValue;
            m_description = description;
        }

        public int getCompareValue() {
            return m_compareValue;
        }

        public int priority(ImplodeState state) {
            return Integer.compare(this.m_compareValue, state.getCompareValue());
        }

        public String getDescription() {
            return this.m_description;
        }

        public String toString() {
            return this.name();
        }
    }
}
