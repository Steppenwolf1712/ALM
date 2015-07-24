package nz.ac.auckland.alm.swing.responsive.widgets;

/**
 * Created by Marc Janßen on 08.06.2015.
 */
public enum AreaElementTypes {

    AreaType_Label("Label"), AreaType_Button("Button"), AreaType_Panel("Panel"),
    AreaType_ComboBox("ComboBox"), AreaType_TextField("TextField"), AreaType_TextArea("TextArea");

    private String s_elementType = null;

    private AreaElementTypes(String elementType) {
        this.s_elementType = elementType;
    }

    public String toString() {
        return this.s_elementType;
    }
}
