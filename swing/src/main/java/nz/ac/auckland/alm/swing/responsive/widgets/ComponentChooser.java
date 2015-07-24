package nz.ac.auckland.alm.swing.responsive.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Marc Janßen on 08.06.2015.
 */
public class ComponentChooser extends JDialog implements ActionListener,
            PropertyChangeListener {

    private JComponent choosen_Component = null;
    private final String m_elementID;

    private JComboBox cb_ElementType = null;
    //private String sElementType = null;

    //private JTextField txt_width = null;
    //private String s_width = null;
    //private JTextField txt_height = null;
    //private String s_height = null;

    private JButton btn_Choose = null;
    private JButton btn_Cancel = null;

    private JOptionPane optionPane;

    private String s1 = "Choose a ComponentType for the Area: ";
    //private String s2 = "Width: ";
    //private String s3 = "Height: ";

    private String s_btn_Choose = "Choose";
    private String s_btn_Cancel = "Cancel";

    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public JComponent getChoosenComponent() {
        return choosen_Component;
    }

    /** Creates the reusable dialog. */
    public ComponentChooser(Frame aFrame, String id) {
        super(aFrame, "Choose a Component", true);
        m_elementID = id;

        cb_ElementType = new JComboBox(AreaElementTypes.values());
        //txt_height = new JTextField("100");
        //txt_width = new JTextField("100");

        Object[] array = {s1, cb_ElementType}; //s2, txt_width, s3, txt_height};

        //Create an array specifying the number of dialog buttons
        //and their text.
        btn_Choose = new JButton(s_btn_Choose);
        btn_Cancel = new JButton(s_btn_Cancel);

        Object[] options = {btn_Choose, btn_Cancel};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_OPTION,
                    null,
                    options,
                    options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we) {
                optionPane.setValue(new Integer(
                       JOptionPane.CLOSED_OPTION));
            }
        });

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {

            public void componentShown(ComponentEvent ce) {
                cb_ElementType.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        btn_Choose.addActionListener(this);
        btn_Cancel.addActionListener(this);


        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);

        pack();
    }

    /** This method handles events for the text field. */
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(e.getSource());
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
                JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            if (btn_Choose.equals(value)) {

                try {
                    AreaElementTypes type = (AreaElementTypes) cb_ElementType.getSelectedItem();
                    switch (type) {
                        case AreaType_Button:
                            choosen_Component = new JButton("Button\n" + m_elementID);
                            break;
                        case AreaType_ComboBox:
                            Object[] temp = {"Auswahl1 " + m_elementID, "Auswahl2 " +
                                    m_elementID, "Auswahl3 " +
                                    m_elementID};
                            choosen_Component = new JComboBox(temp);
                            break;
                        case AreaType_Label:
                            choosen_Component = new JLabel("Label\n" +
                                    m_elementID);
                            break;
                        case AreaType_Panel:
                            choosen_Component = new JPanel();
                            break;
                        case AreaType_TextArea:
                            choosen_Component = new JTextArea("TextArea\n" +
                                    m_elementID);
                            break;
                        case AreaType_TextField:
                            choosen_Component = new JTextField("TextField\n" +
                                    m_elementID);
                            break;
                    }
                } catch (NumberFormatException nfe) {
                    //System.out.println("ComponentChooser: The definition of the size of the JComponent wasn't successful, because of the format of numbers:\n\tWidth= "+txt_width.getText()+"\tHeight= "+txt_height.getText());
                    System.err.println(nfe.getMessage());
                }
            }
            clearAndHide();

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        this.cb_ElementType.setSelectedIndex(0);
        //this.txt_height.setText("100");
        //this.txt_width.setText("100");
        setVisible(false);
    }

}
