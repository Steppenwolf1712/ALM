/*
 * Copyright 2015.
 * Distributed under the terms of the GPLv3 License.
 *
 * Authors:
 *      Clemens Zeidler <czei002@aucklanduni.ac.nz>
 */
package nz.ac.auckland.alm.swing.test;

import nz.ac.auckland.alm.*;
import nz.ac.auckland.alm.swing.ALMLayout;
import nz.ac.auckland.linsolve.OperatorType;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;


public class SwingTests {
    List<JDialog> dialogList = new ArrayList<JDialog>();

    private void addDialog(final JDialog dialog) {
        dialogList.add(dialog);

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                dialogList.remove(dialog);
                if (dialogList.size() == 0)
                    System.exit(0);
            }
        });
    }

    public void testComplexBehaviour() {
        JDialog dialog = new JDialog();
        addDialog(dialog);
        dialog.setTitle("ComplexWindow");

        ALMLayout alm = new ALMLayout();
        dialog.setLayout(alm);

        XTab left = alm.getLeft();
        YTab top = alm.getTop();
        XTab right = alm.getRight();
        YTab bottom = alm.getBottom();

        XTab x1 = new XTab();
        XTab x2 = new XTab();

        YTab y1 = new YTab();

        JButton btn1 = new JButton("Topleft");
        JButton btn2 = new JButton("TopRight");

        dialog.add(btn1, new ALMLayout.LayoutParams(left, top, x1, y1));
        dialog.add(btn2, new ALMLayout.LayoutParams(x1, top, right, y1));

        JLabel lab1 = new JLabel("Input:");
        JTextField txt1 = new JTextField();

        dialog.add(lab1, new ALMLayout.LayoutParams(left, y1, x2, bottom));
        dialog.add(txt1, new ALMLayout.LayoutParams(x2, y1, right, bottom));

        alm.areaOf(btn1).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        alm.areaOf(btn2).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        alm.areaOf(txt1).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);

        Area area = alm.areaOf(txt1);

        area.setBottomInset(5);
        area.setRightInset(5);
        area.setTopInset(5);

        LayoutSpec spec = alm.getLayoutSpec();
        spec.addConstraint(1, x1, -0.5, right, OperatorType.EQ, 0);
        spec.addConstraint(1, x2, OperatorType.EQ, 100);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

    }

    public void testThreeButtons() {
        JDialog dialog = new JDialog();
        addDialog(dialog);
        dialog.setTitle("Three Buttons");

        ALMLayout almLayout = new ALMLayout();
        dialog.setLayout(almLayout);

        XTab left = almLayout.getLeft();
        YTab top = almLayout.getTop();
        XTab right = almLayout.getRight();
        YTab bottom = almLayout.getBottom();

        XTab x1 = new XTab();
        XTab x2 = new XTab();

        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");
        JButton button3 = new JButton("Button 3");

        dialog.add(button1, new ALMLayout.LayoutParams(left, top, x1, bottom));
        dialog.add(button2, new ALMLayout.LayoutParams(x1, top, x2, bottom));
        dialog.add(button3, new ALMLayout.LayoutParams(x2, top, right, bottom));

        dialog.setMinimumSize(almLayout.minimumLayoutSize(dialog));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        // test remove all and re-add
        dialog.getContentPane().removeAll();

        dialog.add(button1, new ALMLayout.LayoutParams(left, top, x1, bottom));
        dialog.add(button2, new ALMLayout.LayoutParams(x1, top, x2, bottom));
        dialog.add(button3, new ALMLayout.LayoutParams(x2, top, right, bottom));

        almLayout.areaOf(button1).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        almLayout.areaOf(button2).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        almLayout.areaOf(button3).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
    }

    public void testPinWheel() {
        JDialog dialog = new JDialog();
        addDialog(dialog);
        dialog.setTitle("Pin Wheel");

        ALMLayout almLayout = new ALMLayout();
        dialog.setLayout(almLayout);

        XTab left = almLayout.getLeft();
        YTab top = almLayout.getTop();
        XTab right = almLayout.getRight();
        YTab bottom = almLayout.getBottom();

        XTab x1 = new XTab();
        XTab x2 = new XTab();

        YTab y1 = new YTab();
        YTab y2 = new YTab();

        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");
        JButton button3 = new JButton("Button 3");
        JButton button4 = new JButton("Button 4");
        JButton buttonMiddle = new JButton("Middle");

        dialog.add(button1, new ALMLayout.LayoutParams(left, top, x2, y1));
        dialog.add(button2, new ALMLayout.LayoutParams(x2, top, right, y2));
        dialog.add(button3, new ALMLayout.LayoutParams(x1, y2, right, bottom));
        dialog.add(button4, new ALMLayout.LayoutParams(left, y1, x1, bottom));
        dialog.add(buttonMiddle, new ALMLayout.LayoutParams(x1, y1, x2, y2));

        almLayout.areaOf(button1).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        almLayout.areaOf(button2).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        almLayout.areaOf(button3).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        almLayout.areaOf(button4).setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        almLayout.areaOf(buttonMiddle).setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        LayoutSpec spec = almLayout.getLayoutSpec();

        spec.addConstraint(2, y1, -1, bottom,  OperatorType.EQ, 0);

        dialog.setMinimumSize(almLayout.minimumLayoutSize(dialog));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        // test removing and re-adding
        dialog.remove(buttonMiddle);
        dialog.invalidate();

        dialog.add(buttonMiddle, new ALMLayout.LayoutParams(x1, y1, x2, y2));
        dialog.invalidate();
    }

    public void testInsetsAndSpacing() {
        JDialog dialog = new JDialog();
        addDialog(dialog);
        dialog.setTitle("Insets And Spacing");

        ALMLayout almLayout = new ALMLayout();
        dialog.setLayout(almLayout);

        almLayout.setSpacing(5, 10);
        almLayout.setInset(10, 20, 30, 40);

        XTab left = almLayout.getLeft();
        YTab top = almLayout.getTop();
        XTab right = almLayout.getRight();
        YTab bottom = almLayout.getBottom();

        XTab x1 = new XTab();

        YTab y1 = new YTab();

        JButton button1 = new JButton("Left Inset");
        JButton button2 = new JButton("Right Inset");
        JButton button3 = new JButton("Top Inset");
        JButton button4 = new JButton("Bottom Inset");

        dialog.add(button1, new ALMLayout.LayoutParams(left, top, x1, y1));
        dialog.add(button2, new ALMLayout.LayoutParams(x1, top, right, y1));
        dialog.add(button3, new ALMLayout.LayoutParams(left, y1, x1, bottom));
        dialog.add(button4, new ALMLayout.LayoutParams(x1, y1, right, bottom));

        Area area1 = almLayout.areaOf(button1);
        area1.setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        area1.setLeftInset(10);
        Area area2 = almLayout.areaOf(button2);
        area2.setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        area2.setRightInset(10);
        Area area3 = almLayout.areaOf(button3);
        area3.setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        area3.setTopInset(10);
        Area area4 = almLayout.areaOf(button4);
        area4.setAlignment(HorizontalAlignment.FILL, VerticalAlignment.FILL);
        area4.setBottomInset(10);

        dialog.setMinimumSize(almLayout.minimumLayoutSize(dialog));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingTests swingTests = new SwingTests();
        swingTests.testThreeButtons();
        swingTests.testPinWheel();
        swingTests.testInsetsAndSpacing();
        swingTests.testComplexBehaviour();
    }
}