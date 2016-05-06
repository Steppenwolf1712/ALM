package nz.ac.auckland.alm.swing.responsive.widgets;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Marc Janﬂen on 02.05.2016.
 */

public class JFrameCloser implements ActionListener {

    private final JFrame m_owner;

    public JFrameCloser(JFrame owner) {
        m_owner = owner;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        m_owner.setVisible(false);
        m_owner.dispose();
    }
}