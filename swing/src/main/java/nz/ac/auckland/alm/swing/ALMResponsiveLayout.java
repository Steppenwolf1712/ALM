package nz.ac.auckland.alm.swing;

import nz.ac.auckland.alm.*;
import nz.ac.auckland.alm.algebra.AlgebraData;
import nz.ac.auckland.alm.swing.responsive.IResponsivePart;
import nz.ac.auckland.alm.swing.responsive.graph.Vector2D;
import nz.ac.auckland.alm.swing.responsive.widgets.WidgetFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc Janßen on 22.07.2015.
 */
public class ALMResponsiveLayout implements LayoutManager2 {

    private Map<Dimension, LayoutSpec> m_layouts;
    private Map<Dimension, AlgebraData> m_AlgebraData;

    private WidgetFactory m_factory;

    private boolean activated = true;

    /**
     * The possibilities for adjusting a GUI's layout.
     * Either change the child controls so that they fit into their parent control,
     * or adjust the size of the parent control so that the children have as much space
     * as they want.
     */
    enum LayoutStyleType {
        FIT_TO_SIZE, ADJUST_SIZE
    }

    /**
     * The manner in which the GUI is dynamically adjusted. The default is to
     * fit the child controls into their parent.
     */
    private LayoutStyleType LayoutStyle = LayoutStyleType.FIT_TO_SIZE;

    public ALMResponsiveLayout(WidgetFactory factory) {
        m_factory = factory;
        m_AlgebraData = new HashMap<Dimension, AlgebraData>();
        m_layouts = new HashMap<Dimension, LayoutSpec>();
    }

    public void addResponsivePart(IResponsivePart part) {//Dimension prefSize, LayoutSpec spec) {
        Dimension prefSize = part.getDesiredSize();
        ALMLayout layout = new ALMLayout();
        AlgebraData data = part.getAlgebra().getAlgebraDataCopy(layout);
        LayoutSpec spec = layout.getLayoutSpec();
        m_layouts.put(prefSize, spec);
        m_AlgebraData.put(prefSize, data);

        for (IArea area: data.getAllAreas()) {
            //Add the Area to the Layout Spec and add the ID
            spec.addArea(area.getLeft(), area.getTop(), area.getRight(), area.getBottom()).setId(area.getId());

        }
    }

    public LayoutSpec getLayoutSpec(Dimension prefSize) {
        return m_layouts.get(prefSize);
    }

    @Override
    public void addLayoutComponent(Component comp, Object cookie) {
        //While I have to add Components to the Container, I cant implement this Method with a Exception
//        if (!(cookie instanceof ResponsiveLayoutParams))
//            throw new RuntimeException("ALMResponsiveLayout.addComponent: bad layout parameters");
//        ResponsiveLayoutParams params = (ResponsiveLayoutParams)cookie;
//        addComponent(comp, params.prefSize, params.left, params.top, params.right, params.bottom);

    }

//    private Area addComponent(Component comp, Dimension prefSize, String id, XTab left, YTab top, XTab right, YTab bottom) {
//        LayoutSpec layoutSpec = m_layouts.get(prefSize);
//        if (layoutSpec==null)
//            throw new RuntimeException("ALMResponsiveLayout.addComponent: no matching layout for prefSize("+prefSize.toString()+")");
//        Area area = layoutSpec.addArea(left, top, right, bottom);
//
//        updateArea(area, comp);
//
//        m_ComponentMaps.get(prefSize).put(comp, area);
//        return area;
//    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        //Without Information about the Layout, where this Component shall be added its implossible to implement this method
        //While I have to add Components to the Container, I cant implement this Method with a Exception
//        throw new RuntimeException("ALMResponsiveLayout.addLayoutComponent: This method is not implemented");
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        //While I have to delete Components from the Container, I cant implement this Method with a Exception
//        throw new RuntimeException("ALMResponsiveLayout.addLayoutComponent: This method is not implemented");
    }

    private void updateArea(Area area, Component component) {
        Dimension minSize = component.getMinimumSize();
        Dimension prefSize = component.getPreferredSize();
        Dimension maxSize = component.getMaximumSize();

        area.setMinSize(minSize.getWidth(), minSize.getHeight());
        area.setPreferredSize(prefSize.getWidth(), prefSize.getHeight());
        area.setMaxSize(maxSize.getWidth(), maxSize.getHeight());
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0,0);
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        throw new RuntimeException("ALMResponsiveLayout.maximumLayoutSize: This method is not implemented");
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    public Dimension getSimilarPreferredSize(Dimension toCompare) {
        Dimension erg = null;
        double distance = Double.MAX_VALUE, tempDistance;
        for (Map.Entry<Dimension, LayoutSpec> entry: m_layouts.entrySet()) {
            if (erg == null) {
                distance = new Vector2D(toCompare).distance(new Vector2D(entry.getKey()));
                erg = entry.getKey();
            } else {
                tempDistance = new Vector2D(toCompare).distance(new Vector2D(entry.getKey()));
                if (tempDistance<distance) {
                    distance = tempDistance;
                    erg = entry.getKey();
                }
            }
        }
        return erg;
    }


    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Area.Size size = m_layouts.get(parent).getPreferredSize();
        return new Dimension((int) size.getWidth(), (int) size.getHeight());
    }

    private Dimension currentDim = null;
    private LayoutSpec currentSpec = null;

    @Override
    public void layoutContainer(Container parent) {
        // make sure that layout events occurring during layout are ignored
        // i.e. activated is set to false during layout calculation
        if (!activated)
            return;
        activated = false;

        Dimension size = parent.getSize();//m_parent.getContentPane().getSize();
        Dimension currentPreferredSize = getSimilarPreferredSize(size);

        LayoutSpec layoutSpec = m_layouts.get(currentPreferredSize);
        if (currentSpec == null) {
            currentSpec = layoutSpec;
            currentDim = currentPreferredSize;
            rebuildLayout(parent);
        } else if(!(currentPreferredSize.equals(currentDim))) {//currentSpec == layoutSpec)) {//.equals(layoutSpec)) {
            removeLayout(parent);
            currentSpec = layoutSpec;
            currentDim = currentPreferredSize;
            rebuildLayout(parent);
            System.out.println("Layout4");
        }

        //Change the LayoutStyle flag to ADJUST_SIZE if the width or height of the container is 0
        if (parent.getBounds().getWidth() == 0 || parent.getBounds().getHeight() == 0) {
            LayoutStyle = LayoutStyleType.ADJUST_SIZE;
        }

        // if the layout engine is set to fit the GUI to the given size,
        // then the given size is enforced by setting absolute positions for
        // Right and Bottom
        // (Default)
        if (LayoutStyle == LayoutStyleType.FIT_TO_SIZE) {
            currentSpec.setRight(parent.getBounds().getWidth());
            currentSpec.setBottom(parent.getBounds().getHeight());
        }

        currentSpec.solve();

        // change the size of the GUI according to the calculated size
        // if the layout engine was configured to do so
//        if (LayoutStyle == LayoutStyleType.ADJUST_SIZE) {
//            parent.setSize(preferredLayoutSize(parent));
//            LayoutStyle = LayoutStyleType.FIT_TO_SIZE;
//        }
        // set the calculated positions and sizes for every area
        for (IArea area : currentSpec.getAreas()) {
            Component component = m_factory.getOrigin(area.getId());
            layoutComponent(component, (Area)area);
        }
        activated = true; // now layout calculation is allowed to run again
    }

    @Override
    public void invalidateLayout(Container target) {
        Dimension matchingPrefSize = getSimilarPreferredSize(target.getSize());

        LayoutSpec layoutSpec = m_layouts.get(matchingPrefSize);
        if (currentSpec == null) {
            currentSpec = layoutSpec;
            rebuildLayout(target);
        } else if(!currentSpec.equals(layoutSpec)) {
            removeLayout(target);
            currentSpec = layoutSpec;
            rebuildLayout(target);
        }

        for (IArea area : currentSpec.getAreas()) {
            Component component = m_factory.getOrigin(area.getId());
            updateArea((Area)area, component);
        }

    }

    private void removeLayout(Container con) {
        for (IArea iArea: currentSpec.getAreas()) {
            Area area = (Area) iArea;
            Component comp = m_factory.getOrigin(area.getId());
            con.remove(comp);
            //TODO Perhaps I have to save the Component at the Factory
//            m_factory.setWidget(area.getId(), comp);
        }
    }

    private void rebuildLayout(Container con) {
        for (IArea iArea: currentSpec.getAreas()) {
            Area area = (Area)iArea;
            Component comp = m_factory.getOrigin(area.getId());
            con.add(comp);
            updateArea(area, comp);
//            layoutComponent(comp, area);//This method will only be called at the layout mehtods
        }
    }

    /**
     * Place the area content accordantly to the solved layout specifications.
     * You should never need to call it by your self.
     */
    void layoutComponent(Component component, Area area) {
        Area.Rect frame = area.getContentRect();

        // set content location and size
        component.setLocation(new Point(Math.round(frame.left), Math.round(frame.top)));
        component.setSize(Math.round(frame.getWidth()), Math.round(frame.getHeight()));
    }

    public class ResponsiveLayoutParams extends ALMLayout.LayoutParams {
        final public Dimension prefSize;
        final String m_id;

        public ResponsiveLayoutParams(Dimension prefSize, IArea area) {
            super(area.getLeft(), area.getTop(), area.getRight(), area.getBottom());
            this.prefSize = prefSize;
            m_id = area.getId();
        }

        public String getId() {
            return m_id;
        }

        public XTab getLeft() {
            return this.left;
        }

        public YTab getTop() {
            return this.top;
        }

        public XTab getRight() {
            return this.getRight();
        }

        public YTab getBottom() {
            return this.bottom;
        }
    }
}
