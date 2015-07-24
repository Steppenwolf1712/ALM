package nz.ac.auckland.alm.swing.responsive.widgets;

import nz.ac.auckland.alm.Area;
import nz.ac.auckland.alm.swing.ALMLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Marc Janßen on 16.07.2015.
 */
public class PlaceHolder_Listener implements MouseListener {

    private ALMLayout m_layout = null;
    private JFrame m_parent = null;
    private String m_id = null;

    public PlaceHolder_Listener(JFrame parent, ALMLayout layout, String id) {
        m_layout = layout;
        m_id = id;
        m_parent = parent;
    }

    private PlaceHolder_Listener(PlaceHolder_Listener copy) {
        this(copy.m_parent, copy.m_layout, copy.m_id);
    }

    void setPlaceHolderContent(Component toReplace, Component content) {
        Area temp = m_layout.areaOf(toReplace);
        m_parent.remove(toReplace);

        content.addMouseListener(new PlaceHolder_Listener(this));

        m_parent.add(content, new ALMLayout.LayoutParams(
                temp.getLeft(),
                temp.getTop(),
                temp.getRight(),
                temp.getBottom()
        ));
    }

    public void changeComponent(MouseEvent e) {
        ComponentChooser chooser = new ComponentChooser(m_parent, m_id);

        chooser.setLocation(e.getLocationOnScreen());
        chooser.setVisible(true);

        JComponent temp = chooser.getChoosenComponent();

        if (temp != null) {
            setPlaceHolderContent((Component)e.getSource(), temp);
            m_parent.validate();
        } else {
            System.out.println("UIAPlaceHolder_ActionListener: The choosen element couldn't replace the current content " +
                    "of the area. The cause is either a bad definition at the ComponentChooser or a missing Area-pointer at this Listener.");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            //startEditMode();// TODO: Some mechanism to add constrains
        } else if (e.getButton() == MouseEvent.BUTTON3)
            changeComponent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
