package nz.ac.auckland.alm.swing.responsive;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Marc Janﬂen on 04.09.2015.
 */
public class ALMPanel extends JPanel {

    private Map<String, JButton> m_parts = null;

    public ALMPanel() {
        super();
        m_parts = new HashMap<String, JButton>();
    }

    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);

        if (comp instanceof JButton) {
            JButton tempPointer = (JButton) comp;
            m_parts.put(tempPointer.getText(), tempPointer);
        }
    }

    public void change_Button_Text(String id, String prefix, String postfix) {
        JButton temp = m_parts.get(id);

        temp.setText(prefix + temp.getText() + postfix);
    }

    public void change_Button_Color(String id, Color c) {
        JButton temp = m_parts.get(id);

        temp.setBorder(BorderFactory.createLineBorder(c));
    }

    public void reset_Button(String id) {
        JButton temp = m_parts.get(id);

        temp.setText(id);
        temp.setBorder(BorderFactory.createLineBorder(Color.gray));
    }

    public void reset_Complete() {
        Set<String> keys = m_parts.keySet();

        for (String id: keys) {
            reset_Button(id);
        }
    }
}
