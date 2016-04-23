package nz.ac.auckland.alm.swing.responsive.graph.points;

import nz.ac.auckland.alm.swing.responsive.graph.ResponsiveGUIGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Created by Marc Janßen on 10.07.2015.
 */
public class VariantManager extends JFrame implements ActionListener {

    private static final String S_CHOOSE = "Select";
    private static final String S_CANCEL = "Cancel";
    private ResponsiveGUIGraph m_parent = null;
    private AssemblyPoint m_ap = null;
    private ResponsiveGUIGraph_Point[] m_variants = null;
    private JTable m_contentTable = null;
    private JButton btn_choose = null;
    private JButton btn_cancel = null;
    private VariantManager_Model m_model = null;

    public VariantManager(ResponsiveGUIGraph parent, AssemblyPoint ap, ResponsiveGUIGraph_Point... points) {
        m_parent = parent;
        m_ap = ap;
        m_variants = points;
        init();
        pack();
    }

    private void init() {
        this.setLayout(new BorderLayout(10,5));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel lab = new JLabel("<html><h3>VariantenManager<h3><p>Please choose, which GUI definition shall be selected!<br>" +
                "On top of that, it is possible to show those GUIs and remove them separately!</p></html>");
        lab.setPreferredSize(new Dimension(500, 120));
        this.add(lab, BorderLayout.NORTH);

        m_model = new VariantManager_Model(m_variants);

//        Object[] header = {"a","b","c","d"};
//        Object[][] data = {{"a","b","c","d"},{"a","b","c","d"},{"a","b","c","d"}};
//
//        m_model = new DefaultTableModel(data, header);

        m_contentTable = new JTable(m_model);
        //m_contentTable.setDefaultRenderer(String.class, new VariantManager_Renderer());

        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                ((VariantManager_Model)table.getModel()).removeVariant(modelRow);//removeRow(modelRow);//removeVariant(modelRow);
            }
        };
        new VariantManager_Renderer(m_contentTable, delete, 3);

        Action show = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                ResponsiveGUIGraph_Point point = ((VariantManager_Model) table.getModel()).getVariant(modelRow);//removeRow(modelRow);//removeVariant(modelRow);
                point.showGUI(null);
            }
        };
        new VariantManager_Renderer(m_contentTable, show, 2);
//        ButtonColumn buttonColumn = new ButtonColumn(table, delete, 2);
//        buttonColumn.setMnemonic(KeyEvent.VK_D);

        //m_contentTable.setDefaultRenderer(ResponsiveGUIGraph_Point.class, );
        m_contentTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        m_contentTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        m_contentTable.getColumnModel().getColumn(1).setMaxWidth(90);
        m_contentTable.getColumnModel().getColumn(1).setMinWidth(45);
        m_contentTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        m_contentTable.getColumnModel().getColumn(2).setMaxWidth(70);
        m_contentTable.getColumnModel().getColumn(1).setMinWidth(35);
        m_contentTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        m_contentTable.getColumnModel().getColumn(3).setMaxWidth(85);
        m_contentTable.getColumnModel().getColumn(1).setMinWidth(35);

        Dimension tableSize = new Dimension(500, 100);
        //m_contentTable.setPreferredSize(tableSize);

        JScrollPane sc_pane = new JScrollPane(m_contentTable);
        sc_pane.setMaximumSize(new Dimension(1000, 120));
        sc_pane.setPreferredSize(tableSize);

        m_contentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.add(sc_pane, BorderLayout.CENTER);


        JPanel hori = new JPanel();
        hori.setPreferredSize(new Dimension(500, 35));
        btn_choose = new JButton(S_CHOOSE);
        btn_choose.addActionListener(this);
        hori.add(btn_choose);

        btn_cancel = new JButton(S_CANCEL);
        btn_cancel.addActionListener(this);
        hori.add(btn_cancel);

        this.add(hori, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btn_choose)) {
            if (m_model.getSize() == 1) {
                m_parent.remove(m_ap);
                m_parent.addPoint(m_model.getVariant(0), true);
            } else {
                int i = m_contentTable.getSelectedRow();
                if (i>=0) {
                    m_parent.remove(m_ap);
                    m_parent.addPoint(m_model.getVariant(i), true);
                } else {
                    m_ap.resetVariants(m_model.getVariants());
                }
            }
            dispose();
        } else if (e.getSource().equals(btn_cancel)) {
            this.dispose();
        }
    }
}
