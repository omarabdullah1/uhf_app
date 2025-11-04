/*
 * Created by JFormDesigner on Mon Oct 17 17:20:06 CST 2022
 */

package com.uhf.form;


import com.uhf.UHFMainForm;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * @author zp
 */
public class FirmwareUpgradeForm extends JPanel {
    public FirmwareUpgradeForm() {
        initComponents();
        initUI();
    }
    private void initUI() {
        if(UHFMainForm.isEnglish()){
            btnSelect.setText("Select File");
            btnUHF.setText("Upgrade");
        }
    }
    private void btnSelectActionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(new File("/"));//FileSystemView.getFileSystemView().getDefaultDirectory()
        chooser.setFileFilter(new BinFilter());
        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            textField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void btnUHFActionPerformed(ActionEvent e) {
        pgbar.setValue(0);
        String filePath = textField.getText();
        if (filePath == null || !filePath.endsWith(".bin")) {
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"File error, please select uhf firmware!":"文件错误，请选择uhf固件!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        btnUHF.setEnabled(false);
        new UHFProgress(pgbar, filePath).start();
    }


    private class BinFilter extends FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            if (f.isFile()) {
                if (f.getName().endsWith(".bin")) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }

        public String getDescription() {
            return "*.bin";
        }
    }

    private class UHFProgress extends Thread {
        JProgressBar progressBar;
        String mFileName;

        UHFProgress(JProgressBar progressBar, String path) {
            this.progressBar = progressBar;
            this.mFileName = path;
        }

        public void run() {
            try {
                // TODO Auto-generated method stub
                boolean result = false;
                File uFile = new File(mFileName);
                if (!uFile.exists()) {
                    System.out.println("fail");
                    JOptionPane.showConfirmDialog(FirmwareUpgradeForm.this, UHFMainForm.isEnglish()?"fail":"失败",
                            "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    btnUHF.setEnabled(true);
                    return;
                }
                long uFileSize = uFile.length();
                System.out.println("uFileSize=" + uFileSize);
                int packageCount = (int) (uFileSize / 64);
                System.out.println("packageCount=" + packageCount);

                RandomAccessFile raf = null;
                try {
                    raf = new RandomAccessFile(mFileName, "r");
                } catch (FileNotFoundException e) {
                }
                if (raf == null) {
                    System.out.println("失败");
                    JOptionPane.showConfirmDialog(FirmwareUpgradeForm.this, UHFMainForm.isEnglish()?"fail":"失败",
                            "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    btnUHF.setEnabled(true);
                    return;
                }

                String version = UHFMainForm.ur4.getVersion();//获取版本号
                System.out.println("UHF 版本号=" + version);
                System.out.println("UHF uhfJump2Boot 开始");
                if (!UHFMainForm.ur4.uhfJump2Boot()) {
                    System.out.println("uhfJump2Boot 失败");
                }
                Thread.sleep(2000);
                System.out.println("UHF开始更新");
                if (!UHFMainForm.ur4.uhfStartUpdate()) {
                    System.out.println("uhf更新失败");
                    JOptionPane.showConfirmDialog(FirmwareUpgradeForm.this, UHFMainForm.isEnglish()?"fail":"失败",
                            "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    btnUHF.setEnabled(true);
                    return;
                }
                Thread.sleep(2000);
                int temp = 0;
                int pakeSize = 64;
                byte[] currData = new byte[(int) uFileSize];
                for (int k = 0; k < packageCount; k++) {
                    int index = k * pakeSize;
                    try {
                        int rsize = raf.read(currData, index, pakeSize);
                        // System.out.println( "beginPack=" + index + " endPack=" + (index + pakeSize - 1) + " rsize=" + rsize);
                    } catch (IOException e) {
                        stopUpgrader();
                        System.out.println("失败!");
                        JOptionPane.showConfirmDialog(FirmwareUpgradeForm.this, UHFMainForm.isEnglish()?"fail":"失败",
                                "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    System.out.println("send : " + (++temp));
                    if (UHFMainForm.ur4.uhfUpdating(Arrays.copyOfRange(currData, index, index + pakeSize))) {
                        result = true;
                        setprogressValue(index + pakeSize, (int) uFileSize);
                        //sleep(10);
                    } else {
                        System.out.println("uhf更新失败");
                        stopUpgrader();
                        JOptionPane.showConfirmDialog(FirmwareUpgradeForm.this, UHFMainForm.isEnglish()?"fail":"失败",
                                "" , JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        btnUHF.setEnabled(true);
                        return;
                    }

                }
                if (uFileSize % pakeSize != 0) {
                    int index = packageCount * pakeSize;
                    int len = (int) (uFileSize % pakeSize);
                    try {
                        int rsize = raf.read(currData, index, len);
                        System.out.println("beginPack=" + index + " countPack=" + len + " rsize=" + rsize);
                    } catch (IOException e) {
                        System.out.println("IOException ");
                        stopUpgrader();
                        JOptionPane.showConfirmDialog(FirmwareUpgradeForm.this, UHFMainForm.isEnglish()?"fail":"失败",
                                "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        btnUHF.setEnabled(true);
                        return;
                    }
                    if (UHFMainForm.ur4.uhfUpdating(Arrays.copyOfRange(currData, index, index + len))) {
                        result = true;
                        setprogressValue((int) uFileSize, (int) uFileSize);
                        JOptionPane.showConfirmDialog(FirmwareUpgradeForm.this,  UHFMainForm.isEnglish()?"success":"成功",
                                "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        System.out.println("uhf更新失败");
                        stopUpgrader();
                        JOptionPane.showConfirmDialog(FirmwareUpgradeForm.this, UHFMainForm.isEnglish()?"fail":"失败",
                                "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        btnUHF.setEnabled(true);
                        return;
                    }
                }
                stopUpgrader();
                JOptionPane.showConfirmDialog(FirmwareUpgradeForm.this, UHFMainForm.isEnglish()?"success":"成功",
                        "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                progressBar.setIndeterminate(false);
            } catch (Exception ex) {

            } finally {
                btnUHF.setEnabled(true);
            }
        }

        private void setprogressValue(int value, int total) {
            //设置进度条的值
            progressBar.setValue(value * 100 / total);
        }

        private void stopUpgrader() {
            UHFMainForm.ur4.uhfStopUpdate();
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        textField = new JTextField();
        btnSelect = new JButton();
        btnUHF = new JButton();
        pgbar = new JProgressBar();

        //======== this ========
        setLayout(null);

        //---- label1 ----
        label1.setText("path\uff1a");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 3f));
        add(label1);
        label1.setBounds(new Rectangle(new Point(115, 85), label1.getPreferredSize()));

        //---- textField ----
        textField.setFont(textField.getFont().deriveFont(textField.getFont().getSize() + 3f));
        add(textField);
        textField.setBounds(160, 80, 490, textField.getPreferredSize().height);

        //---- btnSelect ----
        btnSelect.setText("\u9009\u62e9\u5347\u7ea7\u6587\u4ef6");
        btnSelect.setFont(btnSelect.getFont().deriveFont(btnSelect.getFont().getSize() + 3f));
        btnSelect.addActionListener(e -> btnSelectActionPerformed(e));
        add(btnSelect);
        btnSelect.setBounds(650, 80, 170, btnSelect.getPreferredSize().height);

        //---- btnUHF ----
        btnUHF.setText("\u5347\u7ea7UHF\u56fa\u4ef6");
        btnUHF.setFont(btnUHF.getFont().deriveFont(btnUHF.getFont().getSize() + 3f));
        btnUHF.addActionListener(e -> btnUHFActionPerformed(e));
        add(btnUHF);
        btnUHF.setBounds(380, 170, 185, btnUHF.getPreferredSize().height);

        //---- pgbar ----
        pgbar.setPreferredSize(new Dimension(146, 10));
        add(pgbar);
        pgbar.setBounds(160, 250, 560, 25);

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
    private JLabel label1;
    private JTextField textField;
    private JButton btnSelect;
    private JButton btnUHF;
    private JProgressBar pgbar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
