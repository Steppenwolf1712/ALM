package nz.ac.auckland.alm.swing.responsive.graph.alternatives;

import nz.ac.auckland.alm.swing.responsive.ALMPanel;
import nz.ac.auckland.alm.swing.responsive.Algebra;
import nz.ac.auckland.alm.swing.responsive.AlgebraFrameFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by Marc Janﬂen on 26.08.2015.
 */
public class AlternativeManager extends JFrame implements ActionListener {

    private static final String S_MANUAL_DESCRIPTION = "Choose an Area which shall be removed, to produce an GUI-Alternative:";
    private static final String S_BTN_REMOVE_AREA = "<html>Remove Area/<br>Create Alternative-GUI</html>";
    private final Algebra m_data;

    private JSplitPane split;
    private JComboBox<AlternativeGUI> cbx_alternatives;
    private JButton btn_removeArea;
    private JLabel l_AreaInfoDescribtion;

    private ALMPanel m_preview = null;

    public AlternativeManager(Algebra algebraData) {
        super("Alternative Manager");
        m_data = algebraData;

        init();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void init() {
        setLayout(new BorderLayout());

//        split = new JSplitPane();
//        split.setOrientation(JSplitPane.VERTICAL_SPLIT);
//        add(split);

        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 3));
//        split.setTopComponent(topPanel);
        add(topPanel, BorderLayout.NORTH);


        JPanel botPanel = new JPanel();
        botPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 3));
//        split.setBottomComponent(botPanel);
        add(botPanel, BorderLayout.CENTER);

        initBotPanel(botPanel);
        initTopPanel(topPanel);
    }

    private void initBotPanel(JPanel botPanel) {
        m_preview = AlgebraFrameFactory.createTestPanel(m_data);
        JScrollPane scrollPanel = new JScrollPane(m_preview);
        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPanel.setMaximumSize(new Dimension(500, 500));
        botPanel.add(scrollPanel);
    }

    private void initTopPanel(JPanel topPanel) {
        topPanel.setLayout(new BorderLayout());

        JPanel chooseAreaPanel = new JPanel(new BorderLayout(3,4));
        JLabel description = new JLabel(S_MANUAL_DESCRIPTION);
        chooseAreaPanel.add(description, BorderLayout.NORTH);

        cbx_alternatives = new JComboBox<AlternativeGUI>();
        for (AlternativeGUI gui: m_data.getAlternatives())
            cbx_alternatives.addItem(gui);
        cbx_alternatives.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateDescription();
            }
        });
        chooseAreaPanel.add(cbx_alternatives, BorderLayout.CENTER);

        btn_removeArea = new JButton(S_BTN_REMOVE_AREA);
        btn_removeArea.addActionListener(this);
        chooseAreaPanel.add(btn_removeArea, BorderLayout.EAST);

        topPanel.add(chooseAreaPanel, BorderLayout.NORTH);

        JPanel desc_panel = new JPanel();
        desc_panel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        topPanel.add(desc_panel, BorderLayout.CENTER);

        JPanel desc_contentPanel = new JPanel();
        desc_contentPanel.setPreferredSize(new Dimension(390, 80));
        desc_contentPanel.setBackground(desc_panel.getBackground().brighter());
        desc_contentPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        desc_panel.add(desc_contentPanel);

        l_AreaInfoDescribtion = new JLabel();
        l_AreaInfoDescribtion.setPreferredSize(new Dimension(382, 78));
        updateDescription();
        desc_contentPanel.add(l_AreaInfoDescribtion);
    }

    private void updateDescription() {
        AlternativeGUI temp = cbx_alternatives.getItemAt(cbx_alternatives.getSelectedIndex());
        l_AreaInfoDescribtion.setText(prepareHTML(temp.getDescription()));

        temp.configurePanel(m_preview);
    }

    private String prepareHTML(String description) {
        return "<html><p>Description:<br>"+description+"</html>";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btn_removeArea)) {
            AlternativeGUI alternative = (AlternativeGUI) cbx_alternatives.getSelectedItem();
        }
    }
}
