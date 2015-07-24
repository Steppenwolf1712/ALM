package nz.ac.auckland.alm.swing.responsive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by Marc Janßen on 30.06.2015.
 */
public class ResponsiveGUIFrame extends JFrame {

    private IResponsivePart[] m_guis;
    IResponsivePart m_currentLayout = null;

    public ResponsiveGUIFrame(IResponsivePart[] guis) {
        super();
        m_guis = guis;
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setBounds(150, 200, 300, 300);

        Container mainPanel = this.getContentPane();
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                Component c = (Component) e.getSource();

                changeLayout(c.getSize());
            }
        });

        changeLayout(mainPanel.getSize());

        this.setVisible(true);
    }

    private void changeLayout(Dimension size) {
//        double test = Double.MAX_VALUE;
//        IResponsivePart toChoose = null;
//        for (IResponsivePart part: m_guis) {
////            if (part.compareToSize(size)<test) {
////                test = part.compareToSize(size);
////                toChoose = part;
//            }
//
//        }
//        if (toChoose == null) {
//            System.out.println("ResponsiveGUIFrame: Something went terrible wrong!");
//            return;
//        }
//        if (m_currentLayout != null) {
//            if (m_currentLayout.equals(toChoose))
//                return;
//            this.getContentPane().removeAll();
//        }
//        m_currentLayout = toChoose;
//        //this.setLayout(null);
//        //this.setLayout(toChoose.getLayout(this));
//        this.validate();
//        this.getContentPane().setSize(m_currentLayout.getDesiredSize());
    }

}
