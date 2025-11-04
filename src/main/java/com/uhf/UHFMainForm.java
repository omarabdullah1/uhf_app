/*
 * Created by JFormDesigner on Mon Oct 17 16:34:19 CST 2022
 */

package com.uhf;

import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

import com.rscja.deviceapi.ConnectionState;
import com.rscja.deviceapi.RFIDWithUHFNetworkUR4;
import com.rscja.deviceapi.RFIDWithUHFSerialPortUR4;
import com.rscja.deviceapi.entity.AntennaState;
import com.rscja.deviceapi.interfaces.ConnectionStateCallback;
import com.rscja.deviceapi.interfaces.IUR4;
import com.rscja.utility.LogUtility;
import com.uhf.form.*;
import com.uhf.utils.StringUtils;
import gnu.io.CommPortIdentifier;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;


/**
 * @author zp
 */
public class UHFMainForm extends JFrame {

    public static void main(String[] args) {
        UHFMainForm frame= new UHFMainForm();
        frame.setVisible(true);

    }
    static {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            File file = new File("");
            File sopath = new File(file.getAbsolutePath(), "libTagReader.so");
            System.out.println("sopath=" + sopath.getAbsolutePath());
            System.load(sopath.getAbsolutePath());
            //  System.loadLibrary("rxtxSerial");
        }
    }
    public static IUR4 ur4=null;
    RFIDWithUHFNetworkUR4 ur4Network=null;
    RFIDWithUHFSerialPortUR4 ur4SerialPort=null;

    ArrayList<JPanel> formList=new ArrayList<JPanel>();
    InventoryForm inventoryForm=new InventoryForm();
    ReadWriteForm readWriteForm=new ReadWriteForm();
    ConfigForm configForm=new ConfigForm();
    ConfigForm2 configForm2=new ConfigForm2();
    LockKillForm lockKillForm=new LockKillForm();
    UHFInfoForm uhfInfoForm=new UHFInfoForm();
    TemperatureForm temperatureForm=new TemperatureForm();
    FirmwareUpgradeForm upgradeForm=new FirmwareUpgradeForm();

    public UHFMainForm() {
        initComponents();
        initUI();
 
    }

    private void initUI(){
        if(isEnglish()){
            label1.setText("Mode:");
            label3.setText("COM:");
            label4.setText("Port:");
            btnConnect.setText("Connect");

            cmbCommunicationMode.removeAllItems();
            cmbCommunicationMode.addItem("serial port");
            cmbCommunicationMode.addItem("network");
        }else {
            cmbCommunicationMode.removeAllItems();
            cmbCommunicationMode.addItem("串口");
            cmbCommunicationMode.addItem("网口");
        }

        tabPane.addTab(isEnglish()?"Inventory":"盘点", null, inventoryForm, null);
        tabPane.addTab(isEnglish()?"Read&Write":"读写", null, readWriteForm, null);
        tabPane.addTab(isEnglish()?"Config":"配置", null, configForm , null);
        tabPane.addTab(isEnglish()?"Config2":"配置2", null, configForm2 , null);
        tabPane.addTab(isEnglish()?"Lock-Kill":"锁-销毁", null, lockKillForm, null);
        tabPane.addTab(isEnglish()?"Info":"设备信息", null, uhfInfoForm, null);
        tabPane.addTab(isEnglish()?"temperature":"温度", null, temperatureForm, null);
        tabPane.addTab(isEnglish()?"Upgrade":"升级", null, upgradeForm, null);

        tabPane.setTitleAt(0, isEnglish()?"Inventory":"盘点");
        tabPane.setTitleAt(1, isEnglish()?"Read&Write":"读写");
        tabPane.setTitleAt(2, isEnglish()?"Config":"配置");
        tabPane.setTitleAt(3, isEnglish()?"Config2":"配置2");
        tabPane.setTitleAt(4, isEnglish()?"Lock-Kill":"锁-销毁");
        tabPane.setTitleAt(5, isEnglish()?"Info":"设备信息");
        tabPane.setTitleAt(6, isEnglish()?"temperature":"温度");
        tabPane.setTitleAt(7, isEnglish()?"Upgrade":"升级");

        formList.add(inventoryForm);
        formList.add(readWriteForm);
        formList.add(configForm);
        formList.add(configForm2);
        formList.add(lockKillForm);
        formList.add(uhfInfoForm);
        formList.add(temperatureForm);
        formList.add(upgradeForm);


        if (System.getProperty("os.name").toLowerCase().contains("win")) {

        } else {
            cmbPortNumber.addItem("/dev/ttyS1");
            cmbPortNumber.addItem("/dev/ttyS2");
            cmbPortNumber.addItem("/dev/ttyS3");
            cmbPortNumber.addItem("/dev/ttyUSB0");
            cmbPortNumber.addItem("/dev/ttyUSB1");
            cmbPortNumber.addItem("/dev/ttyUSB2");
            cmbPortNumber.addItem("/dev/ttyUSB3");
        }

        txtIP.setText("192.168.99.200");
        txtPort.setText("8888");

    }

    /**
     * 选选项卡的选择项发送改变
     * @param e
     */
    private void tabPaneStateChanged(ChangeEvent e) {
        int index=tabPane.getSelectedIndex();
        for (int i = 0; i < formList.size(); i++) {
            if (i == index) {
                formList.get(i).setVisible(true);
            } else {
                formList.get(i).setVisible(false);
            }
        }
    }
    //连接网络或者串口
    private void btnConnectActionPerformed(ActionEvent e) {
        if (btnConnect.getText().equals(isEnglish()?"Connect":"连接")) {
                ur4.setConnectionStateCallback(new ConnectionStateCallback() {
                    @Override
                    public void getState(ConnectionState status, Object device) {

                        System.out.println("getState status="+status);
                      if(status == ConnectionState.DISCONNECTED) {
                            if (inventoryForm.isVisible()) {
                                inventoryForm.stopInventory();
                            }

                            btnConnect.setText(isEnglish() ? "Connect" : "连接");
                            txtIP.setEnabled(true);
                            txtPort.setEnabled(true);
                            cmbPortNumber.setEnabled(true);
                            cmbCommunicationMode.setEnabled(true);
                        }
                    }
                });

               if(ur4 instanceof RFIDWithUHFSerialPortUR4){
                   //串口
                   String com= (String)cmbPortNumber.getSelectedItem();
                   boolean rsult=ur4SerialPort.init(com);
                   if(!rsult){
                       JOptionPane.showMessageDialog(getContentPane(), isEnglish()?"Failed to open the serial port":"打开串口失败!", "", JOptionPane.ERROR_MESSAGE);
                       return;
                   }
                   //连接成功
                   btnConnect.setText(isEnglish()?"Disconnect":"断开");
                   cmbPortNumber.setEnabled(false);
                   cmbCommunicationMode.setEnabled(false);
               }else{
                   //网口
                   String ip=txtIP.getText();
                   String port=txtPort.getText();
                   if(!StringUtils.isIPAddress(ip)){
                       JOptionPane.showMessageDialog(getContentPane(), isEnglish()?"Illegal IP address!":"IP地址非法!", "", JOptionPane.ERROR_MESSAGE);
                       return;
                   }
                   if(StringUtils.isEmpty(port)){
                       JOptionPane.showMessageDialog(getContentPane(), isEnglish()?"Port cannot be empty!":"端口不能为空!", "", JOptionPane.ERROR_MESSAGE);
                       return;
                   }
                   boolean rsult=ur4Network.init(ip,Integer.parseInt(port));
                   if(!rsult){
                       JOptionPane.showMessageDialog(getContentPane(), isEnglish()?"Connect Fail !":"连接失败!", "", JOptionPane.ERROR_MESSAGE);
                       return;
                   }
                   //连接成功
                   btnConnect.setText(isEnglish()?"Disconnect":"断开");
                   txtIP.setEnabled(false);
                   txtPort.setEnabled(false);
               }
           }else{
               if(inventoryForm.isVisible()){
                   inventoryForm.stopInventory();
               }
               ur4.free();
               btnConnect.setText(isEnglish()?"Connect":"连接");
               txtIP.setEnabled(true);
               txtPort.setEnabled(true);
               cmbPortNumber.setEnabled(true);
               cmbCommunicationMode.setEnabled(true);
           }
    }

    private void cmbCommunicationModeActionPerformed(ActionEvent e) {
        if(cmbCommunicationMode.getSelectedIndex()==0){
            System.out.println("serial port");

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                ArrayList<String> comList = getComList();
                cmbPortNumber.removeAllItems();
                if (comList != null) {
                    for (String item : comList) {
                        cmbPortNumber.addItem(item);
                    }
                }
            }


            panelSerialPort.setVisible(true);
            panelNetWork.setVisible(false);
            if(ur4SerialPort==null){
                ur4SerialPort=new RFIDWithUHFSerialPortUR4();
            }
            ur4=ur4SerialPort;

        }else if(cmbCommunicationMode.getSelectedIndex()==1){
            System.out.println("network");
            panelSerialPort.setVisible(false);
            panelNetWork.setVisible(true);
            if(ur4Network==null){
                ur4Network=new RFIDWithUHFNetworkUR4();
            }
            ur4=ur4Network;
        }
    }
    public static boolean isEnglish() {
        Locale l = Locale.getDefault();
        if (l != null) {
            String strLan = l.getLanguage();
            if ("zh".equals(strLan)) {
                return false;
            }
        }
        return true ;
    }

    public ArrayList<String> getComList() {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> list = new ArrayList<String>();
        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                list.add(portId.getName());
                // System.out.println("Find CommPort: " + portId.getName());
            }
        }
        if (list != null && list.size() == 0) {
            list = null;
        }
        return list;
    }

 
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel3 = new JPanel();
        panel1 = new JPanel();
        tabPane = new JTabbedPane();
        panel5 = new JPanel();
        panel6 = new JPanel();
        cmbCommunicationMode = new JComboBox();
        label1 = new JLabel();
        panelSerialPort = new JPanel();
        label3 = new JLabel();
        cmbPortNumber = new JComboBox();
        panelNetWork = new JPanel();
        label2 = new JLabel();
        txtIP = new JTextField();
        label4 = new JLabel();
        txtPort = new JTextField();
        btnConnect = new JButton();

        //======== this ========
        setTitle("UHF(v1.2)");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setBackground(new Color(238, 238, 238));
                contentPanel.setLayout(null);

                //======== panel3 ========
                {
                    panel3.setLayout(null);

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
                contentPanel.add(panel3);
                panel3.setBounds(10, 240, panel3.getPreferredSize().width, 285);

                //======== panel1 ========
                {
                    panel1.setLayout(null);

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
                contentPanel.add(panel1);
                panel1.setBounds(new Rectangle(new Point(300, 40), panel1.getPreferredSize()));

                //======== tabPane ========
                {
                    tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                    tabPane.addChangeListener(e -> tabPaneStateChanged(e));
                }
                contentPanel.add(tabPane);
                tabPane.setBounds(5, 80, 1105, 655);

                //======== panel5 ========
                {
                    panel5.setLayout(null);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < panel5.getComponentCount(); i++) {
                            Rectangle bounds = panel5.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = panel5.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        panel5.setMinimumSize(preferredSize);
                        panel5.setPreferredSize(preferredSize);
                    }
                }
                contentPanel.add(panel5);
                panel5.setBounds(new Rectangle(new Point(25, 90), panel5.getPreferredSize()));

                //======== panel6 ========
                {
                    panel6.setBackground(new Color(138, 154, 249));
                    panel6.setLayout(null);

                    //---- cmbCommunicationMode ----
                    cmbCommunicationMode.addActionListener(e -> cmbCommunicationModeActionPerformed(e));
                    panel6.add(cmbCommunicationMode);
                    cmbCommunicationMode.setBounds(80, 20, 155, cmbCommunicationMode.getPreferredSize().height);

                    //---- label1 ----
                    label1.setText("\u901a\u8baf\u65b9\u5f0f:");
                    panel6.add(label1);
                    label1.setBounds(new Rectangle(new Point(20, 25), label1.getPreferredSize()));

                    //======== panelSerialPort ========
                    {
                        panelSerialPort.setBackground(new Color(138, 154, 249));
                        panelSerialPort.setLayout(null);

                        //---- label3 ----
                        label3.setText("\u4e32\u53e3\u53f7:");
                        panelSerialPort.add(label3);
                        label3.setBounds(new Rectangle(new Point(10, 10), label3.getPreferredSize()));
                        panelSerialPort.add(cmbPortNumber);
                        cmbPortNumber.setBounds(55, 5, 130, 30);

                        {
                            // compute preferred size
                            Dimension preferredSize = new Dimension();
                            for(int i = 0; i < panelSerialPort.getComponentCount(); i++) {
                                Rectangle bounds = panelSerialPort.getComponent(i).getBounds();
                                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                            }
                            Insets insets = panelSerialPort.getInsets();
                            preferredSize.width += insets.right;
                            preferredSize.height += insets.bottom;
                            panelSerialPort.setMinimumSize(preferredSize);
                            panelSerialPort.setPreferredSize(preferredSize);
                        }
                    }
                    panel6.add(panelSerialPort);
                    panelSerialPort.setBounds(235, 15, 190, 45);

                    //======== panelNetWork ========
                    {
                        panelNetWork.setBackground(new Color(138, 154, 249));
                        panelNetWork.setLayout(null);

                        //---- label2 ----
                        label2.setText("IP:");
                        panelNetWork.add(label2);
                        label2.setBounds(new Rectangle(new Point(5, 10), label2.getPreferredSize()));

                        //---- txtIP ----
                        txtIP.setText("192.168.99.202");
                        panelNetWork.add(txtIP);
                        txtIP.setBounds(25, 5, 210, txtIP.getPreferredSize().height);

                        //---- label4 ----
                        label4.setText("\u7aef\u53e3:");
                        panelNetWork.add(label4);
                        label4.setBounds(245, 10, 40, 17);

                        //---- txtPort ----
                        txtPort.setText("8888");
                        panelNetWork.add(txtPort);
                        txtPort.setBounds(275, 5, 75, txtPort.getPreferredSize().height);

                        {
                            // compute preferred size
                            Dimension preferredSize = new Dimension();
                            for(int i = 0; i < panelNetWork.getComponentCount(); i++) {
                                Rectangle bounds = panelNetWork.getComponent(i).getBounds();
                                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                            }
                            Insets insets = panelNetWork.getInsets();
                            preferredSize.width += insets.right;
                            preferredSize.height += insets.bottom;
                            panelNetWork.setMinimumSize(preferredSize);
                            panelNetWork.setPreferredSize(preferredSize);
                        }
                    }
                    panel6.add(panelNetWork);
                    panelNetWork.setBounds(435, 15, 360, 45);

                    //---- btnConnect ----
                    btnConnect.setText("\u8fde\u63a5");
                    btnConnect.setBackground(new Color(238, 238, 238));
                    btnConnect.addActionListener(e -> btnConnectActionPerformed(e));
                    panel6.add(btnConnect);
                    btnConnect.setBounds(820, 15, 115, 40);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < panel6.getComponentCount(); i++) {
                            Rectangle bounds = panel6.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = panel6.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        panel6.setMinimumSize(preferredSize);
                        panel6.setPreferredSize(preferredSize);
                    }
                }
                contentPanel.add(panel6);
                panel6.setBounds(-10, -5, 1115, 80);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < contentPanel.getComponentCount(); i++) {
                        Rectangle bounds = contentPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = contentPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    contentPanel.setMinimumSize(preferredSize);
                    contentPanel.setPreferredSize(preferredSize);
                }
            }
            dialogPane.add(contentPanel, BorderLayout.NORTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel3;
    private JPanel panel1;
    private JTabbedPane tabPane;
    private JPanel panel5;
    private JPanel panel6;
    private JComboBox cmbCommunicationMode;
    private JLabel label1;
    private JPanel panelSerialPort;
    private JLabel label3;
    private JComboBox cmbPortNumber;
    private JPanel panelNetWork;
    private JLabel label2;
    private JTextField txtIP;
    private JLabel label4;
    private JTextField txtPort;
    private JButton btnConnect;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


}
