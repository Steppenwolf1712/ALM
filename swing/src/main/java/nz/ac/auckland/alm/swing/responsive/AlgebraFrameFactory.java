package nz.ac.auckland.alm.swing.responsive;

import nz.ac.auckland.alm.Area;
import nz.ac.auckland.alm.HorizontalAlignment;
import nz.ac.auckland.alm.IArea;
import nz.ac.auckland.alm.VerticalAlignment;
import nz.ac.auckland.alm.algebra.BottomDirection;
import nz.ac.auckland.alm.algebra.LeftDirection;
import nz.ac.auckland.alm.algebra.RightDirection;
import nz.ac.auckland.alm.algebra.TopDirection;
import nz.ac.auckland.alm.swing.ALMLayout;
import nz.ac.auckland.alm.swing.ALMResponsiveLayout;
import nz.ac.auckland.alm.swing.responsive.widgets.JFrameCloser;
import nz.ac.auckland.alm.swing.responsive.widgets.JFrameResizer;
import nz.ac.auckland.alm.swing.responsive.widgets.WidgetFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
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
            IArea area = null;
            while (iter_Areas.hasNext()) {
                area = iter_Areas.next();
                Component comp = factory.getWidget(area.getId(), erg, layout);
                erg.add(comp, new ALMLayout.LayoutParams(
                        area.getLeft(),
                        area.getTop(),
                        area.getRight(),
                        area.getBottom()));
                Area realArea = layout.areaOf(comp);
                realArea.setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
            }

            setUndecoratorForFrame(erg);
        }

        return erg;
    }

    private static void setUndecoratorForFrame(JFrame erg) {
        erg.setUndecorated(true);
        erg.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        erg.getRootPane().registerKeyboardAction(new JFrameCloser(erg),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        erg.getRootPane().registerKeyboardAction(new JFrameResizer(erg, new RightDirection(), false),
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        erg.getRootPane().registerKeyboardAction(new JFrameResizer(erg, new LeftDirection(), false),
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        erg.getRootPane().registerKeyboardAction(new JFrameResizer(erg, new TopDirection(), false),
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        erg.getRootPane().registerKeyboardAction(new JFrameResizer(erg, new BottomDirection(), false),
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        erg.getRootPane().registerKeyboardAction(new JFrameResizer(erg, new RightDirection(), true),
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        erg.getRootPane().registerKeyboardAction(new JFrameResizer(erg, new LeftDirection(), true),
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        erg.getRootPane().registerKeyboardAction(new JFrameResizer(erg, new TopDirection(), true),
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        erg.getRootPane().registerKeyboardAction(new JFrameResizer(erg, new BottomDirection(), true),
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static ALMPanel createTestPanel(Algebra algebra) {
        ALMPanel erg = new ALMPanel();

        ALMLayout alm = algebra.getLayout();
        erg.setLayout(alm);

        Iterator<IArea> iter_Areas = algebra.getAreas().iterator();

        IArea area = null;
        while (iter_Areas.hasNext()) {
            area = iter_Areas.next();
            Component comp = new JButton(area.getId());
            comp.setEnabled(false);
            comp.setForeground(Color.black);
            erg.add(comp, new ALMLayout.LayoutParams(
                    area.getLeft(),
                    area.getTop(),
                    area.getRight(),
                    area.getBottom()
            ));
            Area realArea = alm.areaOf(comp);
            realArea.setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        }

        return erg;
    }

    public static JFrame createResponsiveJFrame(String title, IResponsivePart[] parts, WidgetFactory factory) {
        JFrame erg = new JFrame(title);

        ALMResponsiveLayout layout = new ALMResponsiveLayout(factory);
        for (IResponsivePart part: parts) {
            layout.addResponsivePart(part);
        }

//        erg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        erg.setLayout(layout);
        erg.setSize(new Dimension(500, 500));
        erg.doLayout();

        setUndecoratorForFrame(erg);

        return erg;
    }

}
