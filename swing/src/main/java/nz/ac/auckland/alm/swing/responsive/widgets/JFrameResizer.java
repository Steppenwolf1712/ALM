package nz.ac.auckland.alm.swing.responsive.widgets;

import nz.ac.auckland.alm.XTab;
import nz.ac.auckland.alm.YTab;
import nz.ac.auckland.alm.algebra.*;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Marc Janﬂen on 02.05.2016.
 */
public class JFrameResizer implements ActionListener {

    private static final int I_RESIZE_STEP = 10;
    private static final int I_RELOCATE_STEP = 10;
    private final IDirection m_direc;
    private final boolean m_move;
    private final JFrame m_owner;

    public JFrameResizer(JFrame owner, IDirection direction, boolean move) {
        m_direc = direction;
        m_move = move;
        m_owner = owner;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (m_move) {
            relocate();
        } else {
            resize();
        }
    }

    private void resize() {
        if (m_direc instanceof RightDirection) {
           m_owner.setSize(new Dimension(m_owner.getWidth()+I_RESIZE_STEP, m_owner.getHeight()));
        } else if (m_direc instanceof LeftDirection) {
            m_owner.setSize(new Dimension(m_owner.getWidth()-I_RESIZE_STEP, m_owner.getHeight()));
        } else if (m_direc instanceof TopDirection) {
            m_owner.setSize(new Dimension(m_owner.getWidth(), m_owner.getHeight()-I_RESIZE_STEP));
        } else if (m_direc instanceof BottomDirection) {
            m_owner.setSize(new Dimension(m_owner.getWidth(), m_owner.getHeight()+I_RESIZE_STEP));
        }
    }

    private void relocate() {
        if (m_direc instanceof RightDirection) {
            m_owner.setLocation(m_owner.getX()+I_RELOCATE_STEP, m_owner.getY());
        } else if (m_direc instanceof LeftDirection) {
            m_owner.setLocation(m_owner.getX()-I_RELOCATE_STEP, m_owner.getY());
        } else if (m_direc instanceof TopDirection) {
            m_owner.setLocation(m_owner.getX(), m_owner.getY()-I_RELOCATE_STEP);
        } else if (m_direc instanceof BottomDirection) {
            m_owner.setLocation(m_owner.getX(), m_owner.getY()+I_RELOCATE_STEP);
        }
    }
}
