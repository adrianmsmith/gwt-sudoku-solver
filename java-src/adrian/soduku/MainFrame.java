package adrian.soduku;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends javax.swing.JFrame {
    
    JTextField[][] fields = new JTextField[9][9];  // [y][x]

    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
        clearButtonActionPerformed(null);
        pack();
    }

    private void initComponents() {//GEN-BEGIN:initComponents
        fieldGridPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        outputLabel = new javax.swing.JLabel();
        startButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        
        fieldGridPanel.setLayout(new java.awt.GridLayout(9, 0, 0, 9));
        
        getContentPane().add(fieldGridPanel, java.awt.BorderLayout.CENTER);
        
        bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        
        outputLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        outputLabel.setPreferredSize(new java.awt.Dimension(200, 20));
        bottomPanel.add(outputLabel);
        
        startButton.setText("Start");
        startButton.setPreferredSize(new java.awt.Dimension(80, 23));
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        
        bottomPanel.add(startButton);
        
        clearButton.setText("Clear");
        clearButton.setPreferredSize(new java.awt.Dimension(80, 23));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        
        bottomPanel.add(clearButton);
        
        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);
        
        pack();
    }//GEN-END:initComponents

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        fieldGridPanel.removeAll();
        for (int y=0; y<9; y++)
            for (int x=0; x<9; x++) {
                JTextField f = fields[y][x] = new JTextField("");
                f.setPreferredSize(new Dimension(25,25));
                fieldGridPanel.add(f);
            }
        outputLabel.setText("");
        startButton.setEnabled(true);
        fieldGridPanel.revalidate();
        
        ((JTextField) fieldGridPanel.getComponent(0*9 + 0)).setText("6");
        ((JTextField) fieldGridPanel.getComponent(1*9 + 1)).setText("3");
        ((JTextField) fieldGridPanel.getComponent(1*9 + 2)).setText("7");
        ((JTextField) fieldGridPanel.getComponent(2*9 + 0)).setText("8");
        ((JTextField) fieldGridPanel.getComponent(2*9 + 1)).setText("5");
        
        ((JTextField) fieldGridPanel.getComponent(0*9 + 3)).setText("1");
        ((JTextField) fieldGridPanel.getComponent(1*9 + 5)).setText("4");
        ((JTextField) fieldGridPanel.getComponent(2*9 + 4)).setText("3");
        
        ((JTextField) fieldGridPanel.getComponent(0*9 + 6)).setText("7");
        ((JTextField) fieldGridPanel.getComponent(1*9 + 7)).setText("1");
        //--
        ((JTextField) fieldGridPanel.getComponent(3*9 + 0)).setText("3");
        ((JTextField) fieldGridPanel.getComponent(4*9 + 0)).setText("5");
        ((JTextField) fieldGridPanel.getComponent(4*9 + 2)).setText("2");
        ((JTextField) fieldGridPanel.getComponent(5*9 + 1)).setText("4");
        
        ((JTextField) fieldGridPanel.getComponent(3*9 + 7)).setText("5");
        ((JTextField) fieldGridPanel.getComponent(4*9 + 6)).setText("8");
        ((JTextField) fieldGridPanel.getComponent(4*9 + 8)).setText("1");
        ((JTextField) fieldGridPanel.getComponent(5*9 + 8)).setText("2");
        //--
        ((JTextField) fieldGridPanel.getComponent(7*9 + 1)).setText("8");
        ((JTextField) fieldGridPanel.getComponent(8*9 + 2)).setText("6");
        
        ((JTextField) fieldGridPanel.getComponent(6*9 + 4)).setText("4");
        ((JTextField) fieldGridPanel.getComponent(7*9 + 3)).setText("6");
        ((JTextField) fieldGridPanel.getComponent(8*9 + 5)).setText("7");
        
        ((JTextField) fieldGridPanel.getComponent(6*9 + 7)).setText("7");
        ((JTextField) fieldGridPanel.getComponent(6*9 + 8)).setText("8");
        ((JTextField) fieldGridPanel.getComponent(7*9 + 6)).setText("4");
        ((JTextField) fieldGridPanel.getComponent(7*9 + 7)).setText("2");
        ((JTextField) fieldGridPanel.getComponent(8*9 + 8)).setText("9");
    }//GEN-LAST:event_clearButtonActionPerformed

        private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
            int x,y;
            int[][] input = new int[9][9];
            for (y=0; y<9; y++) for (x=0; x<9; x++)
                try {
                    Component c = fieldGridPanel.getComponent(y*9+x);
                    if (c instanceof JTextField) input[y][x] = Integer.parseInt(((JTextField) c).getText()) - 1;
                    else if (c instanceof JLabel) input[y][x] = Integer.parseInt(((JLabel) c).getText()) - 1;
                    else throw new RuntimeException("Unexpected component "+c+" found in grid");
                } catch (NumberFormatException e) {
                    input[y][x] = -1;
                }
            SodukuSolvingProcess p = new SodukuSolvingProcess(input);
            p.process();
     //       if (p.isUnsolvable()) { outputLabel.setText("No solution exists."); return; }
            for (y=0; y<9; y++) for (x=0; x<9; x++)
                if (p.isCellCalculated(x, y)) {
                    JLabel l = new JLabel("" + (p.getCellValue(x, y)+1));
                    l.setForeground(Color.red);
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                    fieldGridPanel.remove(y*9+x);
                    fieldGridPanel.add(l, y*9+x);
                }
            outputLabel.setText("Solution found."); // in " + (1.0/1000*p.getDurationMilliseconds()) + " seconds.");
//            startButton.setEnabled(false);
            if (p.isNonUniqueSolution()) outputLabel.setText("No unique solution.");
            fieldGridPanel.revalidate();
        }//GEN-LAST:event_startButtonActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        new MainFrame().show();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel fieldGridPanel;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JLabel outputLabel;
    private javax.swing.JButton startButton;
    private javax.swing.JButton clearButton;
    // End of variables declaration//GEN-END:variables

}
