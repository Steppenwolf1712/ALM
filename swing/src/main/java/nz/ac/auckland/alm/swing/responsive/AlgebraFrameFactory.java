package nz.ac.auckland.alm.swing.responsive;

import nz.ac.auckland.alm.Area;
import nz.ac.auckland.alm.HorizontalAlignment;
import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.VerticalAlignment;
import nz.ac.auckland.alm.swing.ALMLayout;
import nz.ac.auckland.alm.swing.ALMResponsiveLayout;
import nz.ac.auckland.alm.swing.responsive.widgets.WidgetFactory;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Marc Janßen on 17.07.2015.
 */
public class AlgebraFrameFactory {
    private static Map<Algebra, JFrame> m_frames = new HashMap<Algebra, JFrame>();

    public static JFrame createFrame(String title, Algebra algebra, WidgetFactory factory) {
        JFrame erg = null;


        if (m_frames.containsKey(algebra)) {
            erg = m_frames.get(algebra);
        } else {
            erg = new JFrame(title);


            ALMLayout layout = algebra.getLayout();
            erg.setLayout(layout);

            Iterator<IArea> iter_Areas = algebra.getAreas().iterator();
//            IArea initArea = iter_Areas.next();
//            if (initArea == null)
//                return null;
//            IArea area = initArea;
            while (iter_Areas.hasNext()) {
                IArea area = iter_Areas.next();
                Component comp = factory.getWidget(area.getId(), erg, layout);
                erg.add(comp, new ALMLayout.LayoutParams(
                        area.getLeft(),
                        area.getTop(),
                        area.getRight(),
                        area.getBottom()));
                Area realArea = layout.areaOf(comp);
                realArea.setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
            }
        }

        return erg;
    }

    public static JFrame createResponsiveJFrame(String title, IResponsivePart[] parts, WidgetFactory factory) {
        JFrame erg = new JFrame(title);

        ALMResponsiveLayout layout = new ALMResponsiveLayout(factory);
        for (IResponsivePart part: parts) {
            layout.addResponsivePart(part);
        }

        erg.setLayout(layout);
        erg.setSize(new Dimension(500, 500));
        erg.doLayout();

        return erg;
    }
}
