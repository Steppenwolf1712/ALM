package nz.ac.auckland.alm.swing.responsive;

/**
 * Created by Marc Janßen on 17.07.2015.
 */
public class ResponsiveGUIEditorTest {
    public static void main(String[] args) {
        ResponsiveGUIEditor editor = new ResponsiveGUIEditor("ResponsiveGUI - Editor",
                "Z1/(((A|{testIndex}B)/C)|D)/E*E/(Z21|{testIndex}Z22)");

        editor.pack();
        editor.setResizable(false);
        editor.setVisible(true);
    }
}
