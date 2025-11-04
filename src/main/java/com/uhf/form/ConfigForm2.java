/*
 * Created by JFormDesigner on Mon Sep 02 14:17:25 CST 2024
 */

package com.uhf.form;

import com.rscja.deviceapi.entity.AntennaConnectState;
import com.rscja.deviceapi.entity.AntennaNameEnum;
import com.rscja.deviceapi.entity.AntennaState;
import com.rscja.deviceapi.entity.ReturnLossEntity;
import com.uhf.UHFMainForm;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Brainrain
 */
public class ConfigForm2 extends JPanel {
    public ConfigForm2() {
        initComponents();
        initUI();
    }
    private void initUI(){
        if(UHFMainForm.isEnglish()) {
            btnGetReturnLoss.setText("GET");
            TitledBorder titledBorder=new TitledBorder("Antenna connection state");
            panel3.setBorder(titledBorder);

            TitledBorder titledBorder1=new TitledBorder("Return loss(dB)");
            panel1.setBorder(titledBorder1);
        }
        cbANT1.setEnabled(false);
        cbANT2.setEnabled(false);
        cbANT3.setEnabled(false);
        cbANT4.setEnabled(false);
        cbANT5.setEnabled(false);
        cbANT6.setEnabled(false);
        cbANT7.setEnabled(false);
        cbANT8.setEnabled(false);
        cbANT9.setEnabled(false);
        cbANT10.setEnabled(false);
        cbANT11.setEnabled(false);
        cbANT12.setEnabled(false);
        cbANT13.setEnabled(false);
        cbANT14.setEnabled(false);
        cbANT15.setEnabled(false);
        cbANT16.setEnabled(false);
    }

    private void btnGetReturnLossActionPerformed(ActionEvent e) {
        List<ReturnLossEntity>  list=UHFMainForm.ur4.getAntennaReturnLoss();
        if(list==null || list.isEmpty()){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for(int k=0;k<list.size();k++){
            ReturnLossEntity entity=list.get(k);
            AntennaNameEnum antennaNameEnum= entity.getAntennaNameEnum();
            int returnLoss =entity.getReturnLoss();
            switch (antennaNameEnum){
                case ANT1:
                    txtReturnLoss1.setText(returnLoss+"");
                    break;
                case ANT2:
                    txtReturnLoss2.setText(returnLoss+"");
                    break;
                case ANT3:
                    txtReturnLoss3.setText(returnLoss+"");
                    break;
                case ANT4:
                    txtReturnLoss4.setText(returnLoss+"");
                    break;
                case ANT5:
                    txtReturnLoss5.setText(returnLoss+"");
                    break;
                case ANT6:
                    txtReturnLoss6.setText(returnLoss+"");
                    break;
                case ANT7:
                    txtReturnLoss7.setText(returnLoss+"");
                    break;
                case ANT8:
                    txtReturnLoss8.setText(returnLoss+"");
                    break;
                case ANT9:
                    txtReturnLoss9.setText(returnLoss+"");
                    break;
                case ANT10:
                    txtReturnLoss10.setText(returnLoss+"");
                    break;
                case ANT11:
                    txtReturnLoss11.setText(returnLoss+"");
                    break;
                case ANT12:
                    txtReturnLoss12.setText(returnLoss+"");
                    break;
                case ANT13:
                    txtReturnLoss13.setText(returnLoss+"");
                    break;
                case ANT14:
                    txtReturnLoss14.setText(returnLoss+"");
                    break;
                case ANT15:
                    txtReturnLoss15.setText(returnLoss+"");
                    break;
                case ANT16:
                    txtReturnLoss16.setText(returnLoss+"");
                    break;
            }



        }
    }

    private void btnGetANTActionPerformed(ActionEvent e) {
        List<AntennaConnectState>  list=UHFMainForm.ur4.getAntennaConnectState();

        if(list==null || list.isEmpty()){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for(AntennaConnectState ant:list){
            switch (ant.getAntennaName().getValue()){
                case 1:
                    cbANT1.setSelected(ant.isConnected());
                    break;
                case 2:
                    cbANT2.setSelected(ant.isConnected());
                    break;
                case 3:
                    cbANT3.setSelected(ant.isConnected());
                    break;
                case 4:
                    cbANT4.setSelected(ant.isConnected());
                    break;
                case 5:
                    cbANT5.setSelected(ant.isConnected());
                    break;
                case 6:
                    cbANT6.setSelected(ant.isConnected());
                    break;
                case 7:
                    cbANT7.setSelected(ant.isConnected());
                    break;
                case 8:
                    cbANT8.setSelected(ant.isConnected());
                    break;
                case 9:
                    cbANT9.setSelected(ant.isConnected());
                    break;
                case 10:
                    cbANT10.setSelected(ant.isConnected());
                    break;
                case 11:
                    cbANT11.setSelected(ant.isConnected());
                    break;
                case 12:
                    cbANT12.setSelected(ant.isConnected());
                    break;
                case 13:
                    cbANT13.setSelected(ant.isConnected());
                    break;
                case 14:
                    cbANT14.setSelected(ant.isConnected());
                    break;
                case 15:
                    cbANT15.setSelected(ant.isConnected());
                    break;
                case 16:
                    cbANT16.setSelected(ant.isConnected());
                    break;
            }
        }

    }



    private void btnGetFastInventoryActionPerformed(ActionEvent e) {
        int mode= UHFMainForm.ur4.getFastInventoryMode();
        if(mode==0){
            rbFalstInventoryClose.setSelected(true);
            rbFalstInventoryOpen.setSelected(false);
        }else if(mode==1){
            rbFalstInventoryOpen.setSelected(true);
            rbFalstInventoryClose.setSelected(false);
        }else {
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private void btnSetFastInventoryActionPerformed(ActionEvent e) {
        boolean isOpen = UHFMainForm.ur4.setFastInventoryMode(rbFalstInventoryOpen.isSelected());
        if (!isOpen){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }else{
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void rbFalstInventoryOpenActionPerformed(ActionEvent e) {
        rbFalstInventoryOpen.setSelected(true);
        rbFalstInventoryClose.setSelected(false);
    }

    private void rbFalstInventoryCloseActionPerformed(ActionEvent e) {
        rbFalstInventoryClose.setSelected(true);
        rbFalstInventoryOpen.setSelected(false);
    }

    private void btnFactoryDataResetActionPerformed(ActionEvent e) {
        boolean isOpen = UHFMainForm.ur4.factoryReset();
        if (!isOpen){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Fail!":"失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }else{
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Success!":"成功!", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }




    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        btnGetReturnLoss = new JButton();
        label8 = new JLabel();
        label9 = new JLabel();
        label10 = new JLabel();
        label11 = new JLabel();
        label12 = new JLabel();
        label13 = new JLabel();
        label14 = new JLabel();
        label15 = new JLabel();
        label16 = new JLabel();
        label17 = new JLabel();
        label18 = new JLabel();
        label19 = new JLabel();
        txtReturnLoss2 = new JTextField();
        txtReturnLoss3 = new JTextField();
        txtReturnLoss4 = new JTextField();
        txtReturnLoss5 = new JTextField();
        txtReturnLoss6 = new JTextField();
        txtReturnLoss7 = new JTextField();
        txtReturnLoss8 = new JTextField();
        txtReturnLoss9 = new JTextField();
        txtReturnLoss10 = new JTextField();
        txtReturnLoss11 = new JTextField();
        txtReturnLoss12 = new JTextField();
        txtReturnLoss13 = new JTextField();
        txtReturnLoss14 = new JTextField();
        txtReturnLoss15 = new JTextField();
        txtReturnLoss16 = new JTextField();
        txtReturnLoss1 = new JTextField();
        panel3 = new JPanel();
        cbANT1 = new JCheckBox();
        cbANT2 = new JCheckBox();
        cbANT3 = new JCheckBox();
        cbANT4 = new JCheckBox();
        btnGetANT = new JButton();
        cbANT5 = new JCheckBox();
        cbANT6 = new JCheckBox();
        cbANT7 = new JCheckBox();
        cbANT8 = new JCheckBox();
        cbANT9 = new JCheckBox();
        cbANT10 = new JCheckBox();
        cbANT11 = new JCheckBox();
        cbANT12 = new JCheckBox();
        cbANT13 = new JCheckBox();
        cbANT14 = new JCheckBox();
        cbANT15 = new JCheckBox();
        cbANT16 = new JCheckBox();
        panel19 = new JPanel();
        rbFalstInventoryOpen = new JRadioButton();
        rbFalstInventoryClose = new JRadioButton();
        btnGetFastInventory = new JButton();
        btnSetFastInventory = new JButton();
        btnFactoryDataReset = new JButton();

        //======== this ========
        setLayout(null);

        //======== panel1 ========
        {
            panel1.setPreferredSize(new Dimension(42, 42));
            panel1.setBorder(new TitledBorder("\u56de\u6ce2\u635f\u8017(dB)"));
            panel1.setLayout(null);

            //---- label1 ----
            label1.setText("ANT1:");
            panel1.add(label1);
            label1.setBounds(15, 25, 40, label1.getPreferredSize().height);

            //---- label2 ----
            label2.setText("ANT2:");
            panel1.add(label2);
            label2.setBounds(175, 25, 40, 17);

            //---- label3 ----
            label3.setText("ANT3:");
            panel1.add(label3);
            label3.setBounds(15, 55, 40, 17);

            //---- label4 ----
            label4.setText("ANT4:");
            panel1.add(label4);
            label4.setBounds(175, 55, 40, 17);

            //---- btnGetReturnLoss ----
            btnGetReturnLoss.setText("\u83b7\u53d6");
            btnGetReturnLoss.addActionListener(e -> btnGetReturnLossActionPerformed(e));
            panel1.add(btnGetReturnLoss);
            btnGetReturnLoss.setBounds(105, 270, 85, 30);

            //---- label8 ----
            label8.setText("ANT5:");
            panel1.add(label8);
            label8.setBounds(15, 85, 40, 17);

            //---- label9 ----
            label9.setText("ANT6:");
            panel1.add(label9);
            label9.setBounds(175, 85, 40, 17);

            //---- label10 ----
            label10.setText("ANT7:");
            panel1.add(label10);
            label10.setBounds(15, 115, 40, 17);

            //---- label11 ----
            label11.setText("ANT8:");
            panel1.add(label11);
            label11.setBounds(175, 115, 40, 17);

            //---- label12 ----
            label12.setText("ANT9:");
            panel1.add(label12);
            label12.setBounds(15, 145, 40, 17);

            //---- label13 ----
            label13.setText("ANT10:");
            panel1.add(label13);
            label13.setBounds(175, 145, 55, 17);

            //---- label14 ----
            label14.setText("ANT11:");
            panel1.add(label14);
            label14.setBounds(10, 175, 60, 17);

            //---- label15 ----
            label15.setText("ANT12:");
            panel1.add(label15);
            label15.setBounds(170, 175, 60, 17);

            //---- label16 ----
            label16.setText("ANT14:");
            panel1.add(label16);
            label16.setBounds(170, 205, 60, 17);

            //---- label17 ----
            label17.setText("ANT13:");
            panel1.add(label17);
            label17.setBounds(10, 205, 60, 17);

            //---- label18 ----
            label18.setText("ANT16:");
            panel1.add(label18);
            label18.setBounds(170, 235, 60, 17);

            //---- label19 ----
            label19.setText("ANT15:");
            panel1.add(label19);
            label19.setBounds(10, 235, 60, 17);
            panel1.add(txtReturnLoss2);
            txtReturnLoss2.setBounds(220, 15, 49, 30);
            panel1.add(txtReturnLoss3);
            txtReturnLoss3.setBounds(60, 50, 49, 30);
            panel1.add(txtReturnLoss4);
            txtReturnLoss4.setBounds(220, 45, 49, 30);
            panel1.add(txtReturnLoss5);
            txtReturnLoss5.setBounds(60, 80, 49, 30);
            panel1.add(txtReturnLoss6);
            txtReturnLoss6.setBounds(220, 75, 49, 30);
            panel1.add(txtReturnLoss7);
            txtReturnLoss7.setBounds(60, 110, 49, 30);
            panel1.add(txtReturnLoss8);
            txtReturnLoss8.setBounds(220, 105, 49, 30);
            panel1.add(txtReturnLoss9);
            txtReturnLoss9.setBounds(60, 140, 49, 30);
            panel1.add(txtReturnLoss10);
            txtReturnLoss10.setBounds(220, 135, 49, 30);
            panel1.add(txtReturnLoss11);
            txtReturnLoss11.setBounds(60, 170, 49, 30);
            panel1.add(txtReturnLoss12);
            txtReturnLoss12.setBounds(220, 165, 49, 30);
            panel1.add(txtReturnLoss13);
            txtReturnLoss13.setBounds(60, 200, 49, 30);
            panel1.add(txtReturnLoss14);
            txtReturnLoss14.setBounds(220, 200, 49, 30);
            panel1.add(txtReturnLoss15);
            txtReturnLoss15.setBounds(60, 230, 49, 30);
            panel1.add(txtReturnLoss16);
            txtReturnLoss16.setBounds(220, 230, 49, 30);
            panel1.add(txtReturnLoss1);
            txtReturnLoss1.setBounds(60, 20, 49, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel1.getComponentCount(); i++) {
                    Rectangle bounds = panel1.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel1.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel1.setMinimumSize(preferredSize);
                panel1.setPreferredSize(preferredSize);
            }
        }
        add(panel1);
        panel1.setBounds(5, 5, 325, 310);

        //======== panel3 ========
        {
            panel3.setPreferredSize(new Dimension(42, 42));
            panel3.setBorder(new TitledBorder("\u5929\u7ebf\u8fde\u63a5\u72b6\u6001"));
            panel3.setLayout(null);

            //---- cbANT1 ----
            cbANT1.setText("ANT1");
            panel3.add(cbANT1);
            cbANT1.setBounds(15, 25, 70, cbANT1.getPreferredSize().height);

            //---- cbANT2 ----
            cbANT2.setText("ANT2");
            panel3.add(cbANT2);
            cbANT2.setBounds(85, 25, 75, 22);

            //---- cbANT3 ----
            cbANT3.setText("ANT3");
            panel3.add(cbANT3);
            cbANT3.setBounds(160, 25, 75, 22);

            //---- cbANT4 ----
            cbANT4.setText("ANT4");
            panel3.add(cbANT4);
            cbANT4.setBounds(235, 25, 80, 22);

            //---- btnGetANT ----
            btnGetANT.setText("\u83b7\u53d6");
            btnGetANT.addActionListener(e -> btnGetANTActionPerformed(e));
            panel3.add(btnGetANT);
            btnGetANT.setBounds(105, 140, 85, 30);

            //---- cbANT5 ----
            cbANT5.setText("ANT5");
            panel3.add(cbANT5);
            cbANT5.setBounds(15, 50, 70, 22);

            //---- cbANT6 ----
            cbANT6.setText("ANT6");
            panel3.add(cbANT6);
            cbANT6.setBounds(85, 50, 70, 22);

            //---- cbANT7 ----
            cbANT7.setText("ANT7");
            panel3.add(cbANT7);
            cbANT7.setBounds(160, 50, 70, 22);

            //---- cbANT8 ----
            cbANT8.setText("ANT8");
            panel3.add(cbANT8);
            cbANT8.setBounds(235, 50, 70, 22);

            //---- cbANT9 ----
            cbANT9.setText("ANT9");
            panel3.add(cbANT9);
            cbANT9.setBounds(15, 75, 70, 22);

            //---- cbANT10 ----
            cbANT10.setText("ANT10");
            panel3.add(cbANT10);
            cbANT10.setBounds(85, 75, 70, 22);

            //---- cbANT11 ----
            cbANT11.setText("ANT11");
            panel3.add(cbANT11);
            cbANT11.setBounds(160, 75, 70, 22);

            //---- cbANT12 ----
            cbANT12.setText("ANT12");
            panel3.add(cbANT12);
            cbANT12.setBounds(235, 75, 70, 22);

            //---- cbANT13 ----
            cbANT13.setText("ANT13");
            panel3.add(cbANT13);
            cbANT13.setBounds(15, 100, 70, 22);

            //---- cbANT14 ----
            cbANT14.setText("ANT14");
            panel3.add(cbANT14);
            cbANT14.setBounds(85, 100, 70, 22);

            //---- cbANT15 ----
            cbANT15.setText("ANT15");
            panel3.add(cbANT15);
            cbANT15.setBounds(160, 100, 70, 22);

            //---- cbANT16 ----
            cbANT16.setText("ANT16");
            panel3.add(cbANT16);
            cbANT16.setBounds(235, 100, 70, 22);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel3.getComponentCount(); i++) {
                    Rectangle bounds = panel3.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel3.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel3.setMinimumSize(preferredSize);
                panel3.setPreferredSize(preferredSize);
            }
        }
        add(panel3);
        panel3.setBounds(340, 5, 325, 185);

        //======== panel19 ========
        {
            panel19.setPreferredSize(new Dimension(42, 42));
            panel19.setBorder(new TitledBorder("Fast Inventory"));
            panel19.setLayout(null);

            //---- rbFalstInventoryOpen ----
            rbFalstInventoryOpen.setText("Open");
            rbFalstInventoryOpen.addActionListener(e -> rbFalstInventoryOpenActionPerformed(e));
            panel19.add(rbFalstInventoryOpen);
            rbFalstInventoryOpen.setBounds(75, 30, 85, rbFalstInventoryOpen.getPreferredSize().height);

            //---- rbFalstInventoryClose ----
            rbFalstInventoryClose.setText("Close");
            rbFalstInventoryClose.addActionListener(e -> rbFalstInventoryCloseActionPerformed(e));
            panel19.add(rbFalstInventoryClose);
            rbFalstInventoryClose.setBounds(175, 30, 80, 21);

            //---- btnGetFastInventory ----
            btnGetFastInventory.setText("Get");
            btnGetFastInventory.addActionListener(e -> btnGetFastInventoryActionPerformed(e));
            panel19.add(btnGetFastInventory);
            btnGetFastInventory.setBounds(60, 60, 85, 30);

            //---- btnSetFastInventory ----
            btnSetFastInventory.setText("Set");
            btnSetFastInventory.addActionListener(e -> btnSetFastInventoryActionPerformed(e));
            panel19.add(btnSetFastInventory);
            btnSetFastInventory.setBounds(180, 60, 80, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel19.getComponentCount(); i++) {
                    Rectangle bounds = panel19.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel19.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel19.setMinimumSize(preferredSize);
                panel19.setPreferredSize(preferredSize);
            }
        }
        add(panel19);
        panel19.setBounds(340, 200, 325, 110);

        //---- btnFactoryDataReset ----
        btnFactoryDataReset.setText("Factory data reset");
        btnFactoryDataReset.addActionListener(e -> btnFactoryDataResetActionPerformed(e));
        add(btnFactoryDataReset);
        btnFactoryDataReset.setBounds(5, 320, 320, 40);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < getComponentCount(); i++) {
                Rectangle bounds = getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            setMinimumSize(preferredSize);
            setPreferredSize(preferredSize);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JButton btnGetReturnLoss;
    private JLabel label8;
    private JLabel label9;
    private JLabel label10;
    private JLabel label11;
    private JLabel label12;
    private JLabel label13;
    private JLabel label14;
    private JLabel label15;
    private JLabel label16;
    private JLabel label17;
    private JLabel label18;
    private JLabel label19;
    private JTextField txtReturnLoss2;
    private JTextField txtReturnLoss3;
    private JTextField txtReturnLoss4;
    private JTextField txtReturnLoss5;
    private JTextField txtReturnLoss6;
    private JTextField txtReturnLoss7;
    private JTextField txtReturnLoss8;
    private JTextField txtReturnLoss9;
    private JTextField txtReturnLoss10;
    private JTextField txtReturnLoss11;
    private JTextField txtReturnLoss12;
    private JTextField txtReturnLoss13;
    private JTextField txtReturnLoss14;
    private JTextField txtReturnLoss15;
    private JTextField txtReturnLoss16;
    private JTextField txtReturnLoss1;
    private JPanel panel3;
    private JCheckBox cbANT1;
    private JCheckBox cbANT2;
    private JCheckBox cbANT3;
    private JCheckBox cbANT4;
    private JButton btnGetANT;
    private JCheckBox cbANT5;
    private JCheckBox cbANT6;
    private JCheckBox cbANT7;
    private JCheckBox cbANT8;
    private JCheckBox cbANT9;
    private JCheckBox cbANT10;
    private JCheckBox cbANT11;
    private JCheckBox cbANT12;
    private JCheckBox cbANT13;
    private JCheckBox cbANT14;
    private JCheckBox cbANT15;
    private JCheckBox cbANT16;
    private JPanel panel19;
    private JRadioButton rbFalstInventoryOpen;
    private JRadioButton rbFalstInventoryClose;
    private JButton btnGetFastInventory;
    private JButton btnSetFastInventory;
    private JButton btnFactoryDataReset;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
