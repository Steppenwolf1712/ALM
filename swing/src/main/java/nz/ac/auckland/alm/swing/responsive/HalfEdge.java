package nz.ac.auckland.alm.swing.responsive;

import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.algebra.IDirection;
import nz.ac.auckland.linsolve.Variable;

/**
 * Created by Marc Janﬂen on 09.11.2015.
 */
public class HalfEdge {
    private final IArea m_Item;
    private final Variable m_ItemTab;
    private final IDirection m_direc;

    public HalfEdge(IArea item, IDirection direc) {
        m_Item = item;
        m_ItemTab = direc.getTab(item);
        m_direc = direc;
    }

    public HalfEdge(IArea item, Variable itemTab, IDirection direc) {
        m_Item = item;
        m_ItemTab = itemTab;
        m_direc = direc;
    }

    public boolean equals(Object obj) {
        if (obj instanceof HalfEdge) {
            HalfEdge he = (HalfEdge) obj;
            if (this.m_ItemTab.equals(he.m_ItemTab))
                if (this.m_Item.equals(he.m_Item))
                    return true;
            return false;
        } else
            return false;
    }

    public IDirection getDirection() {
        return this.m_direc;
    }

    public IArea getArea() {
        return m_Item;
    }

    public Variable getTab() {
        return m_ItemTab;
    }
}