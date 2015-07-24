package nz.ac.auckland.alm.swing.responsive.graph.points;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Marc Janßen on 10.07.2015.
 */
public class VariantManager_Model extends AbstractTableModel {

    private ArrayList<ResponsiveGUIGraph_Point> m_variants = null;
    private static final String[] HEADER = {"GUI", "Dimension", "Window", "Remove"};

    public VariantManager_Model(ResponsiveGUIGraph_Point[] variants) {
        m_variants = new ArrayList<ResponsiveGUIGraph_Point>();
        for (ResponsiveGUIGraph_Point variant: variants)
            m_variants.add(variant);
    }

    public ResponsiveGUIGraph_Point getVariant(int variantIndex) {
        return this.m_variants.get(variantIndex);
    }

    public ResponsiveGUIGraph_Point[] getVariants() {
        return this.m_variants.toArray(new ResponsiveGUIGraph_Point[m_variants.size()]);
    }

    @Override
    public int getRowCount() {
        return m_variants.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    public String getColumnName(int column) {
        return HEADER[column];
    }

//    public Class getColumnClass (int columnIndex) {
//        switch (columnIndex) {
//            case 0:
//            case 1:
//                return String.class;
//            case 2:
//            case 3:
//                return String.class;
//            default:
//                return null;
//        }
//    }

    public int getSize() {
        return this.m_variants.size();
    }

    boolean removeVariant(ResponsiveGUIGraph_Point variant) {
        if (m_variants.size() == 1) {
            return false;
        }

        int temp = m_variants.indexOf(variant);
        int answer = JOptionPane.showConfirmDialog(null, "Are you sure about removing this Alternativ:\n\t"+variant.getStringDefinition()+"\nThe Alternative will be deleted!", "Warning", JOptionPane.OK_CANCEL_OPTION);
        if (answer == JOptionPane.OK_OPTION) {
            boolean erg = m_variants.remove(variant);
            if (erg)
                this.fireTableRowsDeleted(temp, temp);
            return erg;
        }
        return false;
    }
    boolean removeVariant(int variantIndex) {
        return removeVariant(this.m_variants.get(variantIndex));
    }

    public void addVariant(ResponsiveGUIGraph_Point variant) {
        this.m_variants.add(variant);
        int i = m_variants.indexOf(variant);
        fireTableCellUpdated(i, i);
    }

    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 2:
            case 3:
                return true;
        }
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ResponsiveGUIGraph_Point temp = m_variants.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return temp.getStringDefinition();
            case 1:
                Dimension dim = temp.getDesiredSize();
                return "["+dim.getWidth()+"x"+dim.getHeight()+"]";
            case 2:
                return "Show";
            case 3:
                return "Remove";
            default:
                return null;
        }
    }
}
