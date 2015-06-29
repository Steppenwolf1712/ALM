/*
 * Copyright 2015.
 * Distributed under the terms of the GPLv3 License.
 *
 * Authors:
 *      Clemens Zeidler <czei002@aucklanduni.ac.nz>
 */
package nz.ac.auckland.alm.algebra;

import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.XTab;
import nz.ac.auckland.alm.YTab;
import nz.ac.auckland.linsolve.Variable;

import java.util.*;


public class DensenessChecker {
    /**
     * Consider an object like: E /_e (A_0 / ... / A_n) |_{edge} (B_0 / ... / B_n) /_f F
     * Then the edge chain of |_{edge} contains /_e and /_f
     */
    static class EdgeChain {
        public Variable tab1;
        public Variable tab2;
    }

    static private List<IArea> takeChain(List<IArea> areas, IDirection direction) {
        List<IArea> chain = new ArrayList<IArea>();
        chain.add(areas.remove(0));

        IDirection orth1 = direction.getOrthogonalDirection1();
        IDirection orth2 = direction.getOrthogonalDirection2();

        boolean addedDirection1 = true;
        boolean addedDirection2 = true;
        while ((addedDirection1 || addedDirection2) && areas.size() > 0) {
            for (int i = 0; i < areas.size(); i++) {
                IArea area = areas.get(i);
                if (addedDirection1) {
                    addedDirection1 = false;
                    IArea area1 = chain.get(0);
                    if (orth1.getTab(area1) == orth1.getOppositeTab(area)) {
                        chain.add(0, area);
                        areas.remove(area);
                        addedDirection1 = true;
                        break;
                    }
                }
                if (addedDirection2) {
                    addedDirection2 = false;
                    IArea area2 = chain.get(chain.size() - 1);
                    if (orth2.getTab(area2) == orth2.getOppositeTab(area)) {
                        chain.add(area);
                        areas.remove(area);
                        addedDirection2 = true;
                        break;
                    }
                }
            }
        }
        return chain;
    }

    static private List<List<IArea>> takeChains(List<IArea> areas, IDirection direction) {
        List<List<IArea>> chains = new ArrayList<List<IArea>>();
        while (areas.size() > 0)
            chains.add(takeChain(areas, direction));
        return chains;
    }

    static private List<EdgeChain> getEdgeChains(Edge edge, IDirection direction) {
        List<EdgeChain> edgeChains = new ArrayList<EdgeChain>();

        List<List<IArea>> chains1 = takeChains(new ArrayList<IArea>(edge.areas1), direction);
        List<List<IArea>> chains2 = takeChains(new ArrayList<IArea>(edge.areas2), direction);

        if (chains1.size() != chains2.size())
            return null;

        IDirection orth1 = direction.getOrthogonalDirection1();
        IDirection orth2 = direction.getOrthogonalDirection2();

        // find matching chains
        for (List<IArea> chain1 : chains1) {
            boolean found = false;
            for (List<IArea> chain2 : chains2) {
                if ((orth1.getTab(chain1.get(0)) == orth1.getTab(chain2.get(0)))
                    && (orth2.getTab(chain1.get(chain1.size() - 1)) == orth2.getTab(chain2.get(chain2.size() - 1)))) {
                    found = true;
                    EdgeChain edgeChain = new EdgeChain();
                    edgeChain.tab1 = orth1.getTab(chain1.get(0));
                    edgeChain.tab2 = orth2.getTab(chain1.get(chain1.size() - 1));
                    edgeChains.add(edgeChain);
                    break;
                }
            }
            if (!found)
                return null;
        }
        return edgeChains;
    }

    static private boolean fillEdgeChains(Collection<Edge> edges, Map<Edge, List<EdgeChain>> map,
                                          IDirection direction) {
        for (Edge edge : edges) {
            List<EdgeChain> chains = getEdgeChains(edge, direction);
            if (chains == null || chains.size() == 0)
                return false;
            map.put(edge, chains);
        }
        return true;
    }

    static private <Tab extends Variable, OrthTab extends Variable>
    boolean checkEdge(IArea area, IDirection direction, Map<Tab, Edge> edgeMap, Map<OrthTab, Edge> orthEdgeMap,
                      Map<Edge, List<EdgeChain>> orthEdgeListMap) {
        Edge edge = direction.getEdge(area, edgeMap);
        for (EdgeChain chain : orthEdgeListMap.get(edge)) {
            if (Edge.isInChain(direction.getOrthogonalTab1(area), chain.tab1, orthEdgeMap,
                    direction.getOrthogonalDirection1())
                || Edge.isInChain(direction.getOrthogonalTab2(area), chain.tab2, orthEdgeMap,
                    direction.getOrthogonalDirection2()))
                return true;
        }
        return false;
    }

    static public boolean isDense(LayoutStructure layoutStructure) {
        Map<Edge, List<EdgeChain>> xEdgeChainMap = new HashMap<Edge, List<EdgeChain>>();
        Map<Edge, List<EdgeChain>> yEdgeChainMap = new HashMap<Edge, List<EdgeChain>>();
        IDirection left = new LeftDirection();
        IDirection top = new TopDirection();
        IDirection right = new RightDirection();
        IDirection bottom = new BottomDirection();

        Map<XTab, Edge> xTabEdgeMap = new HashMap<XTab, Edge>(layoutStructure.getXTabEdges());
        xTabEdgeMap.remove(layoutStructure.getLeft());
        xTabEdgeMap.remove(layoutStructure.getRight());
        Map<YTab, Edge> yTabEdgeMap = new HashMap<YTab, Edge>(layoutStructure.getYTabEdges());
        yTabEdgeMap.remove(layoutStructure.getTop());
        yTabEdgeMap.remove(layoutStructure.getBottom());
        if (!fillEdgeChains(xTabEdgeMap.values(), xEdgeChainMap, right))
            return false;
        if (!fillEdgeChains(yTabEdgeMap.values(), yEdgeChainMap, bottom))
            return false;

        for (IArea area : layoutStructure.getAllAreas()) {
            if (area.getLeft() != layoutStructure.getLeft()
                && !checkEdge(area, left, layoutStructure.getXTabEdges(), layoutStructure.getYTabEdges(),
                    xEdgeChainMap))
                return false;
            if (area.getRight() != layoutStructure.getRight()
                    && !checkEdge(area, right, layoutStructure.getXTabEdges(), layoutStructure.getYTabEdges(),
                    xEdgeChainMap))
                return false;
            if (area.getTop() != layoutStructure.getTop()
                    && !checkEdge(area, top, layoutStructure.getYTabEdges(), layoutStructure.getXTabEdges(),
                    yEdgeChainMap))
                return false;
            if (area.getBottom() != layoutStructure.getBottom()
                    && !checkEdge(area, bottom, layoutStructure.getYTabEdges(), layoutStructure.getXTabEdges(),
                    yEdgeChainMap))
                return false;
        }
        return true;
    }
}