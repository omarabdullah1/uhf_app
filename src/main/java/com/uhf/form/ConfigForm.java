/*
 * Created by JFormDesigner on Mon Oct 17 17:18:30 CST 2022
 */

package com.uhf.form;

import com.rscja.deviceapi.entity.*;
import com.rscja.deviceapi.interfaces.IUHF;
import com.uhf.UHFMainForm;
import com.uhf.utils.StringUtils;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author zp
 */
public class ConfigForm extends JPanel {
    public ConfigForm() {
        initComponents();
        initUI();
        panel5.setVisible(false);

    }

    private int ANT_NUMBER=16;
    /**
     * 获取天线功率
     * @param e
     */
    private void btnGetPowerActionPerformed(ActionEvent e) {
        List<AntennaPowerEntity> list=UHFMainForm.ur4.getPowerAll();
        if(list==null){
            JOptionPane.showMessageDialog(this,  UHFMainForm.isEnglish()?"Get Fail!":"获取失败","", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for(AntennaPowerEntity entity : list){
            if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT1){
                int ant1Power= entity.getPower();
                cmbAnt1Power.setSelectedIndex(ant1Power-1);
                System.out.println("ant1Power="+ant1Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT2){
                int ant2Power= entity.getPower();
                cmbAnt2Power.setSelectedIndex(ant2Power-1);
                System.out.println("ant2Power="+ant2Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT3){
                int ant3Power= entity.getPower();
                cmbAnt3Power.setSelectedIndex(ant3Power-1);
                System.out.println("ant3Power="+ant3Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT4){
                int ant4Power= entity.getPower();
                cmbAnt4Power.setSelectedIndex(ant4Power-1);
                System.out.println("ant4Power="+ant4Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT5){
                int ant5Power= entity.getPower();
                cmbAnt5Power.setSelectedIndex(ant5Power-1);
                System.out.println("ant5Power="+ant5Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT6){
                int ant6Power= entity.getPower();
                cmbAnt6Power.setSelectedIndex(ant6Power-1);
                System.out.println("ant6Power="+ant6Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT7){
                int ant7Power= entity.getPower();
                cmbAnt7Power.setSelectedIndex(ant7Power-1);
                System.out.println("ant7Power="+ant7Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT8){
                int ant8Power= entity.getPower();
                cmbAnt8Power.setSelectedIndex(ant8Power-1);
                System.out.println("ant8Power="+ant8Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT9){
                int ant9Power= entity.getPower();
                cmbAnt9Power.setSelectedIndex(ant9Power-1);
                System.out.println("ant9Power="+ant9Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT10){
                int ant10Power= entity.getPower();
                cmbAnt10Power.setSelectedIndex(ant10Power-1);
                System.out.println("ant10Power="+ant10Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT11){
                int ant11Power= entity.getPower();
                cmbAnt11Power.setSelectedIndex(ant11Power-1);
                System.out.println("ant11Power="+ant11Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT12){
                int ant12Power= entity.getPower();
                cmbAnt12Power.setSelectedIndex(ant12Power-1);
                System.out.println("ant12Power="+ant12Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT13){
                int ant13Power= entity.getPower();
                cmbAnt13Power.setSelectedIndex(ant13Power-1);
                System.out.println("ant13Power="+ant13Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT14){
                int ant14Power= entity.getPower();
                cmbAnt14Power.setSelectedIndex(ant14Power-1);
                System.out.println("ant14Power="+ant14Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT15){
                int ant15Power= entity.getPower();
                cmbAnt15Power.setSelectedIndex(ant15Power-1);
                System.out.println("ant15Power="+ant15Power);
            }else if(entity.getAntennaNameEnum() ==AntennaNameEnum.ANT16){
                int ant16Power= entity.getPower();
                cmbAnt16Power.setSelectedIndex(ant16Power-1);
                System.out.println("ant16Power="+ant16Power);
            }
        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * 设置天线功率
     * @param e
     */
    private void btnSetPoerActionPerformed(ActionEvent e) {
        boolean result= UHFMainForm.ur4.setPower(AntennaNameEnum.ANT1,cmbAnt1Power.getSelectedIndex()+1);
        if(!result){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Failed to set antenna 1 !":"设置天线1失败!","", JOptionPane.ERROR_MESSAGE);
            return;
        }
        result=UHFMainForm.ur4.setPower(AntennaNameEnum.ANT2,cmbAnt2Power.getSelectedIndex()+1);
        if(!result){
            JOptionPane.showMessageDialog(this,  UHFMainForm.isEnglish()?"Failed to set antenna 2 !":"设置天线2失败!","", JOptionPane.ERROR_MESSAGE);
            return;
        }
        result= UHFMainForm.ur4.setPower(AntennaNameEnum.ANT3,cmbAnt3Power.getSelectedIndex()+1);
        if(!result){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Failed to set antenna 3 !":"设置天线3失败!","", JOptionPane.ERROR_MESSAGE);
            return;
        }
        result=  UHFMainForm.ur4.setPower(AntennaNameEnum.ANT4,cmbAnt4Power.getSelectedIndex()+1);
        if(!result){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Failed to set antenna 4 !":"设置天线4失败!","", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(ANT_NUMBER>4) {
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT5, cmbAnt5Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 5 !" : "设置天线5失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }


            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT6, cmbAnt6Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 6 !" : "设置天线6失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }

            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT7, cmbAnt7Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 7 !" : "设置天线7失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT8, cmbAnt8Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 8 !" : "设置天线8失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }

        }
        if(ANT_NUMBER>8) {
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT9, cmbAnt9Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 9 !" : "设置天线9失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT10, cmbAnt10Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 10 !" : "设置天线10失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT11, cmbAnt11Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 11 !" : "设置天线11失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT12, cmbAnt12Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 12 !" : "设置天线12失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT13, cmbAnt13Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 13 !" : "设置天线13失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT14, cmbAnt14Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 14 !" : "设置天线14失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT15, cmbAnt15Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 15 !" : "设置天线15失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = UHFMainForm.ur4.setPower(AntennaNameEnum.ANT16, cmbAnt16Power.getSelectedIndex() + 1);
            if (!result) {
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish() ? "Failed to set antenna 16 !" : "设置天线16失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set antenna success !":"设置天线成功!", "", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 获取天线
     * @param e
     */
    private void btnGetANTActionPerformed(ActionEvent e) {
        List<AntennaState>  list =UHFMainForm.ur4.getAntenna();
        if(list==null){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cbANT1.setSelected(false);
        cbANT2.setSelected(false);
        cbANT3.setSelected(false);
        cbANT4.setSelected(false);

        cbANT5.setSelected(false);
        cbANT6.setSelected(false);
        cbANT7.setSelected(false);
        cbANT8.setSelected(false);

        cbANT9.setSelected(false);
        cbANT10.setSelected(false);
        cbANT11.setSelected(false);
        cbANT12.setSelected(false);

        cbANT13.setSelected(false);
        cbANT14.setSelected(false);
        cbANT15.setSelected(false);
        cbANT16.setSelected(false);
        for (AntennaState ant:list){
            if(ant.getAntennaName()==AntennaNameEnum.ANT1 && ant.isEnable()){
                cbANT1.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT2 && ant.isEnable()){
                cbANT2.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT3 && ant.isEnable()){
                cbANT3.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT4 && ant.isEnable()){
                cbANT4.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT5 && ant.isEnable()){
                cbANT5.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT6 && ant.isEnable()){
                cbANT6.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT7 && ant.isEnable()){
                cbANT7.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT8 && ant.isEnable()){
                cbANT8.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT9 && ant.isEnable()){
                cbANT9.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT10 && ant.isEnable()){
                cbANT10.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT11 && ant.isEnable()){
                cbANT11.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT12 && ant.isEnable()){
                cbANT12.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT13 && ant.isEnable()){
                cbANT13.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT14 && ant.isEnable()){
                cbANT14.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT15 && ant.isEnable()){
                cbANT15.setSelected(true);
            }else if(ant.getAntennaName()==AntennaNameEnum.ANT16 && ant.isEnable()){
                cbANT16.setSelected(true);
            }
        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);

    }
    /**
     * 设置天线
     * @param e
     */
    private void btnSetANTActionPerformed(ActionEvent e) {
        List<AntennaState> list=new ArrayList<>();
        list.add(new AntennaState(AntennaNameEnum.ANT1,cbANT1.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT2,cbANT2.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT3,cbANT3.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT4,cbANT4.isSelected()));

        list.add(new AntennaState(AntennaNameEnum.ANT5,cbANT5.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT6,cbANT6.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT7,cbANT7.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT8,cbANT8.isSelected()));

        list.add(new AntennaState(AntennaNameEnum.ANT9,cbANT9.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT10,cbANT10.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT11,cbANT11.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT12,cbANT12.isSelected()));

        list.add(new AntennaState(AntennaNameEnum.ANT13,cbANT13.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT14,cbANT14.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT15,cbANT15.isSelected()));
        list.add(new AntennaState(AntennaNameEnum.ANT16,cbANT16.isSelected()));
        boolean result=UHFMainForm.ur4.setAntenna(list);
        if(!result){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    /**
     * 获取频段
     * @param e
     */
    private void btnGetFrequencyBandActionPerformed(ActionEvent e) {
        int region=UHFMainForm.ur4.getFrequencyMode();
        switch (region) {
            case 0x01:
                cbFrequencyBand.setSelectedIndex(0);
                break;
            case 0x02:
                cbFrequencyBand.setSelectedIndex(1);
                break;
            case 0x04:
                cbFrequencyBand.setSelectedIndex(2);
                break;
            case 0x08:
                cbFrequencyBand.setSelectedIndex(3);
                break;
            case 0x16:
                cbFrequencyBand.setSelectedIndex(4);
                break;
            case 0x32:
                cbFrequencyBand.setSelectedIndex(5);
                break;
            case 0x34:
                cbFrequencyBand.setSelectedIndex(6);
                break;
            case 0x33:
                cbFrequencyBand.setSelectedIndex(7);
                break;
            case 0x36:
                cbFrequencyBand.setSelectedIndex(8);
                break;
            case 0x37:
                cbFrequencyBand.setSelectedIndex(9);
                break;
            default:
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
                break;

        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * 设置频段
     * @param e
     */
    private void btnSetFrequencyBandActionPerformed(ActionEvent e) {

        int region=-1;
        switch (cbFrequencyBand.getSelectedIndex())
        {
            case 0:
                region = 0x01;
                break;
            case 1:
                region = 0x02;
                break;
            case 2:
                region = 0x04;
                break;
            case 3:
                region = 0x08;
                break;
            case 4:
                region = 0x16;
                break;
            case 5:
                region = 0x32;
                break;
            case 6:
                region = 0x34;
                break;
            case 7:
                region = 0x33;
                break;
            case 8:
                region = 0x36;
                break;
            case 9:
                region = 0x37;
                break;

        }
        if(region==-1){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean result=UHFMainForm.ur4.setFrequencyMode( (byte) region);
        if(result){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
        }else {
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 设置协议
     * @param e
     */
    private void btnSetProtocolActionPerformed(ActionEvent e) {
        int p=cmbProtocol.getSelectedIndex();
        if(p<=0){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean result= UHFMainForm.ur4.setProtocol(p);
        if(!result){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * 获取协议
     * @param e
     */
    private void btnGetProtocolActionPerformed(ActionEvent e) {
        int result= UHFMainForm.ur4.getProtocol();
        if(result==-1){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cmbProtocol.setSelectedIndex(result);
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * 获取链路
     * @param e
     */
    private void btnGetLinkActionPerformed(ActionEvent e) {
        int result = UHFMainForm.ur4.getRFLink();
        if(result==-1){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        switch (result)
        {
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                result = result-4;
                break;
        }
        cmbRFLink.setSelectedIndex(result);
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * 设置链路
     * @param e
     */
    private void btnSetLinkActionPerformed(ActionEvent e) {
        int l =cmbRFLink.getSelectedIndex();
        if(l<0){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (l)
        {
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                l = l+4;
                break;
        }
        boolean result= UHFMainForm.ur4.setRFLink(l);
        if(!result){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * 获取工作模式
     * @param e
     */
    private void btnGetFrequencyModeActionPerformed(ActionEvent e) {
        int result = UHFMainForm.ur4.getWorkMode();
        if(result < 0){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cmbWorkMode.setSelectedIndex(result);
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * 设置工作模式
     * @param e
     */
    private void btnSetFrequencyModeActionPerformed(ActionEvent e) {
        int w = cmbWorkMode.getSelectedIndex();
        if(w<0){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean result = UHFMainForm.ur4.setWorkMode(w);
        if(!result){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * 获取盘点模式
     * @param e
     */
    private void btnGetEPCAndTIDUserModeActionPerformed(ActionEvent e) {
        InventoryModeEntity result = UHFMainForm.ur4.getEPCAndTIDUserMode();
        int User_prt = result.getUserOffset();
        int User_len = result.getUserLength();
        if(result == null){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cmbEPCAndTIDUserMode.setSelectedIndex(result.getMode());
        if(result.getMode()==2) {
            tf_user_prt.setText(String.valueOf(User_prt));
            tf_user_len.setText(String.valueOf(User_len));
        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * 设置盘点模式
     * @param e
     */
    private void btnSetEPCAndTIDUserModeActionPerformed(ActionEvent e) {
        if (cmbEPCAndTIDUserMode.getSelectedIndex()==0){
            boolean result1 = UHFMainForm.ur4.setEPCMode();
            if(!result1){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }
        }else if (cmbEPCAndTIDUserMode.getSelectedIndex()==1){
            boolean result2 = UHFMainForm.ur4.setEPCAndTIDMode();
            if(!result2){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }
        }else if(cmbEPCAndTIDUserMode.getSelectedIndex()==2) {
            String User_Prt = tf_user_prt.getText();
            String User_Len = tf_user_len.getText();


            if (StringUtils.isEmpty(User_Prt)){
                JOptionPane.showMessageDialog(this,  UHFMainForm.isEnglish()?"Start address cannot be empty":"起始地址不能为空", "", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (StringUtils.isEmpty(User_Len)){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"SLength cannot be empty":"长度不能为空", "", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean result3 = UHFMainForm.ur4.setEPCAndTIDUserMode(Integer.parseInt(User_Prt),Integer.parseInt(User_Len));
            if(result3){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
     *
     * 获取蜂鸣器
     * @param e
     */
    private void btnGetBeepActionPerformed(ActionEvent e) {
        char[] result = UHFMainForm.ur4.getBeep();
        if (result == null) {
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }else {
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     *
     * 设置蜂鸣器
     * @param e
     */
    private void btnSetBeepActionPerformed(ActionEvent e) {
        int mode = -1;
        if(rbtn_Beep_On.isSelected()){
            mode = 1;
        }else if(rbtn_Beep_Off.isSelected()){
            mode = 0;
        }
       boolean result = UHFMainForm.ur4.setBeep(mode);
        if(!result){
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);

    }
    /**
     *蜂鸣器 开
     * @param e
     */
    private void rbtn_Beep_OnActionPerformed(ActionEvent e) {
        rbtn_Beep_On.setSelected(true);
        rbtn_Beep_Off.setSelected(false);
    }
    /**
     *蜂鸣器 关
     * @param e
     */
    private void rbtn_Beep_OffActionPerformed(ActionEvent e) {
        rbtn_Beep_On.setSelected(false);
        rbtn_Beep_Off.setSelected(true);
    }
    /**
     *获取Gen2
     * @param e
     */
    private void btnGetGen2ActionPerformed(ActionEvent e) {
        Gen2Entity getGen2 = UHFMainForm.ur4.getGen2();
        if (getGen2 == null) {
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
            int session = getGen2.getQuerySession();
            int target = getGen2.getQueryTarget();
            cmb_Gen2_session.setSelectedIndex(session);
            cmb_Gen2_target.setSelectedIndex(target);
        JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }
    /**
     *设置Gen2
     * @param e
     */
        private void btnSetGen2ActionPerformed(ActionEvent e) {
            Gen2Entity entity = UHFMainForm.ur4.getGen2();
            int session = cmb_Gen2_session.getSelectedIndex();
            int target = cmb_Gen2_target.getSelectedIndex();
            entity.setQuerySession(session);
            entity.setQueryTarget(target);

            boolean result = UHFMainForm.ur4.setGen2(entity);
            if (!result){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    /**
     *获取本地IP
     * @param e
     */
        private void btnGetLocal_IPActionPerformed(ActionEvent e) {
            ReaderIPEntity result = UHFMainForm.ur4.getIPAndPort();
            String ip = result.getIp();
            String SubNetMask = result.getSubnetMask();
            String GateWay = result.getGateway();
            int Port = result.getPort();

            textFieldIP.setText(ip);
            textFieldSubNetMask.setText(SubNetMask);
            textFieldGateWay.setText(GateWay);
            textFieldPort.setText(String.valueOf(Port));

            if(result == null){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    /**
     *设置本地IP
     * @param e
     */
        private void btnSetLocal_IPActionPerformed(ActionEvent e) {
            String Ip = textFieldIP.getText();
            String SubNetMask = textFieldSubNetMask.getText();
            String GateWay = textFieldGateWay.getText();
            String Port = textFieldPort.getText();

            if(StringUtils.isEmpty(Ip)){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Invalid IP Address!":"IP地址无效!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else if (StringUtils.isEmpty(SubNetMask)){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Invalid subnet mask!":"子网掩码无效!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else if (StringUtils.isEmpty(GateWay)){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Invalid gateway!":"网关无效!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else if (StringUtils.isEmpty(Port)){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Invalid Port!":"端口无效!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ReaderIPEntity uhfIpConfig = new  ReaderIPEntity();
            uhfIpConfig.setIp(Ip);
            uhfIpConfig.setSubnetMask(SubNetMask);
            uhfIpConfig.setGateway(GateWay);
            uhfIpConfig.setPort(Integer.parseInt(Port));

            boolean result = UHFMainForm.ur4.setIPAndPort(uhfIpConfig);
            if(!result){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    /**
     *获取目标IP
     * @param e
     */
        private void btnGetDestinationIPActionPerformed(ActionEvent e) {
            ReaderIPEntity result = UHFMainForm.ur4.getDestinationIPAndPort();
            String DIp = result.getIp();
            int DPort = result.getPort();

            textFieldDestinationIP.setText(DIp);
            textFieldDestinationPort.setText(String.valueOf(DPort));

            if(result == null){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    /**
     *设置目标IP
     * @param e
     */
        private void btnSetDestinationPortActionPerformed(ActionEvent e) {
            String DIp = textFieldDestinationIP.getText();
            String DPort = textFieldDestinationPort.getText();
            if(StringUtils.isEmpty(DIp)){
                JOptionPane.showMessageDialog(this,"目标IP地址不能为空","",JOptionPane.ERROR_MESSAGE);
                return;
            }else if (StringUtils.isEmpty(DPort)){
                JOptionPane.showMessageDialog(this,"目标端口不能为空","",JOptionPane.ERROR_MESSAGE);
                return;
            }
            ReaderIPEntity uhfIpConfig = new  ReaderIPEntity();
            uhfIpConfig.setIp(DIp);
            uhfIpConfig.setPort(Integer.parseInt(DPort));

            boolean result = UHFMainForm.ur4.setDestinationIPAndPort(uhfIpConfig);
            if(!result){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    /**
     *设置GPIO
     * @param e
     */
        private void btnSetGPIOActionPerformed(ActionEvent e) {
            byte relay = (byte) cmbRelay.getSelectedIndex();

            if(relay < 0 ){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean result = UHFMainForm.ur4.setGPO((byte) 0,(byte) 0,relay);
            if(!result){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    /**
     *获取GPIO
     * @param e
     */
        private void btnGetGPIOActionPerformed(ActionEvent e) {
            List<GPIStateEntity> list =  UHFMainForm.ur4.getGPI();
            if(list == null){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for(GPIStateEntity entity :list){
                switch (entity.getGPIName()){
                    case GPIStateEntity.GPI1:
                        cmbInput1.setSelectedIndex(entity.getGPIState());
                        break;
                    case GPIStateEntity.GPI2:
                        cmbInput2.setSelectedIndex(entity.getGPIState());
                        break;
                }
            }
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);

        }

        private void btnUR1AGetGPIOActionPerformed(ActionEvent e) {
            List<GPIStateEntity> list =  UHFMainForm.ur4.getGPI();
            if(list == null){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Fail!":"获取失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for(GPIStateEntity entity :list){
                switch (entity.getGPIName()){
                    case GPIStateEntity.GPI1:
                        cbUR1AInput1.setSelectedIndex(entity.getGPIState());
                        break;
                    case GPIStateEntity.GPI2:
                        cbUR1AInput2.setSelectedIndex(entity.getGPIState());
                        break;
                }
            }
            JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Get Success!":"获取成功!", "", JOptionPane.INFORMATION_MESSAGE);

        }

        private void btnUR1ASetGPIOActionPerformed(ActionEvent e) {

            byte output1 = (byte) cbOutput1.getSelectedIndex();
            byte output2 = (byte) cbOutput2.getSelectedIndex();
            if(output1<0 || output2<0){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean result = UHFMainForm.ur4.setGpoOnUR1A(output1,output2);
            if(!result){
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Fail!":"设置失败!", "", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                JOptionPane.showMessageDialog(this, UHFMainForm.isEnglish()?"Set Success!":"设置成功!", "", JOptionPane.INFORMATION_MESSAGE);
            }
        }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        label1 = new JLabel();
        cmbAnt1Power = new JComboBox();
        cmbAnt2Power = new JComboBox();
        label2 = new JLabel();
        cmbAnt3Power = new JComboBox();
        label3 = new JLabel();
        cmbAnt4Power = new JComboBox();
        label4 = new JLabel();
        btnGetPower = new JButton();
        btnSetPoer = new JButton();
        label8 = new JLabel();
        cmbAnt5Power = new JComboBox();
        cmbAnt6Power = new JComboBox();
        label9 = new JLabel();
        cmbAnt7Power = new JComboBox();
        label10 = new JLabel();
        cmbAnt8Power = new JComboBox();
        label11 = new JLabel();
        cmbAnt9Power = new JComboBox();
        label12 = new JLabel();
        cmbAnt10Power = new JComboBox();
        label13 = new JLabel();
        cmbAnt11Power = new JComboBox();
        label14 = new JLabel();
        label15 = new JLabel();
        cmbAnt12Power = new JComboBox();
        cmbAnt14Power = new JComboBox();
        label16 = new JLabel();
        cmbAnt13Power = new JComboBox();
        label17 = new JLabel();
        cmbAnt16Power = new JComboBox();
        label18 = new JLabel();
        cmbAnt15Power = new JComboBox();
        label19 = new JLabel();
        panel2 = new JPanel();
        cbANT1 = new JCheckBox();
        cbANT2 = new JCheckBox();
        cbANT3 = new JCheckBox();
        cbANT4 = new JCheckBox();
        btnSetANT = new JButton();
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
        panel3 = new JPanel();
        label5 = new JLabel();
        cbFrequencyBand = new JComboBox();
        btnGetFrequencyBand = new JButton();
        btnSetFrequencyBand = new JButton();
        panel5 = new JPanel();
        label6 = new JLabel();
        cmbProtocol = new JComboBox();
        btnSetProtocol = new JButton();
        btnGetProtocol = new JButton();
        panel17 = new JPanel();
        label22 = new JLabel();
        cmbEPCAndTIDUserMode = new JComboBox();
        btnGetEPCAndTIDUserMode = new JButton();
        btnSetEPCAndTIDUserMode = new JButton();
        label23 = new JLabel();
        tf_user_prt = new JTextField();
        label24 = new JLabel();
        tf_user_len = new JTextField();
        panel18 = new JPanel();
        label25 = new JLabel();
        cmb_Gen2_session = new JComboBox();
        label26 = new JLabel();
        cmb_Gen2_target = new JComboBox();
        btnSetGen2 = new JButton();
        btnGetGen2 = new JButton();
        panel19 = new JPanel();
        label27 = new JLabel();
        rbtn_Beep_On = new JRadioButton();
        rbtn_Beep_Off = new JRadioButton();
        btnGetBeep = new JButton();
        btnSetBeep = new JButton();
        panel20 = new JPanel();
        label28 = new JLabel();
        textFieldIP = new JTextField();
        label29 = new JLabel();
        label30 = new JLabel();
        textFieldSubNetMask = new JTextField();
        textFieldGateWay = new JTextField();
        btnGetLocal_IP = new JButton();
        btnSetLocal_IP = new JButton();
        label33 = new JLabel();
        textFieldPort = new JTextField();
        panel21 = new JPanel();
        label31 = new JLabel();
        textFieldDestinationIP = new JTextField();
        label32 = new JLabel();
        textFieldDestinationPort = new JTextField();
        btnSetDestinationPort = new JButton();
        btnGetDestinationIP = new JButton();
        panel22 = new JPanel();
        label34 = new JLabel();
        cmbWorkMode = new JComboBox();
        btnSetWorkMode = new JButton();
        btnGetWorkMode = new JButton();
        panel23 = new JPanel();
        cmbRFLink = new JComboBox();
        label7 = new JLabel();
        btnSetRFLink = new JButton();
        btnGetRFLink = new JButton();
        panel24 = new JPanel();
        btnSetGPIO = new JButton();
        cmbRelay = new JComboBox();
        label35 = new JLabel();
        label36 = new JLabel();
        label37 = new JLabel();
        btnGetGPIO = new JButton();
        cmbInput1 = new JComboBox();
        cmbInput2 = new JComboBox();
        panel25 = new JPanel();
        btnUR1ASetGPIO = new JButton();
        cbOutput1 = new JComboBox();
        label38 = new JLabel();
        label39 = new JLabel();
        label40 = new JLabel();
        btnUR1AGetGPIO = new JButton();
        cbUR1AInput1 = new JComboBox();
        cbUR1AInput2 = new JComboBox();
        label41 = new JLabel();
        cbOutput2 = new JComboBox();

        //======== this ========
        setLayout(null);

        //======== panel1 ========
        {
            panel1.setPreferredSize(new Dimension(42, 42));
            panel1.setBorder(new TitledBorder("\u529f\u7387"));
            panel1.setLayout(null);

            //---- label1 ----
            label1.setText("ANT1:");
            panel1.add(label1);
            label1.setBounds(15, 25, 40, label1.getPreferredSize().height);
            panel1.add(cmbAnt1Power);
            cmbAnt1Power.setBounds(60, 20, 80, cmbAnt1Power.getPreferredSize().height);
            panel1.add(cmbAnt2Power);
            cmbAnt2Power.setBounds(220, 20, 80, 30);

            //---- label2 ----
            label2.setText("ANT2:");
            panel1.add(label2);
            label2.setBounds(175, 25, 40, 17);
            panel1.add(cmbAnt3Power);
            cmbAnt3Power.setBounds(60, 50, 80, 30);

            //---- label3 ----
            label3.setText("ANT3:");
            panel1.add(label3);
            label3.setBounds(15, 55, 40, 17);
            panel1.add(cmbAnt4Power);
            cmbAnt4Power.setBounds(220, 50, 80, 30);

            //---- label4 ----
            label4.setText("ANT4:");
            panel1.add(label4);
            label4.setBounds(175, 55, 40, 17);

            //---- btnGetPower ----
            btnGetPower.setText("\u83b7\u53d6");
            btnGetPower.addActionListener(e -> btnGetPowerActionPerformed(e));
            panel1.add(btnGetPower);
            btnGetPower.setBounds(50, 265, 85, 30);

            //---- btnSetPoer ----
            btnSetPoer.setText("\u8bbe\u7f6e");
            btnSetPoer.addActionListener(e -> btnSetPoerActionPerformed(e));
            panel1.add(btnSetPoer);
            btnSetPoer.setBounds(170, 265, 80, 30);

            //---- label8 ----
            label8.setText("ANT5:");
            panel1.add(label8);
            label8.setBounds(15, 85, 40, 17);
            panel1.add(cmbAnt5Power);
            cmbAnt5Power.setBounds(60, 80, 80, 30);
            panel1.add(cmbAnt6Power);
            cmbAnt6Power.setBounds(220, 80, 80, 30);

            //---- label9 ----
            label9.setText("ANT6:");
            panel1.add(label9);
            label9.setBounds(175, 85, 40, 17);
            panel1.add(cmbAnt7Power);
            cmbAnt7Power.setBounds(60, 110, 80, 30);

            //---- label10 ----
            label10.setText("ANT7:");
            panel1.add(label10);
            label10.setBounds(15, 115, 40, 17);
            panel1.add(cmbAnt8Power);
            cmbAnt8Power.setBounds(220, 110, 80, 30);

            //---- label11 ----
            label11.setText("ANT8:");
            panel1.add(label11);
            label11.setBounds(175, 115, 40, 17);
            panel1.add(cmbAnt9Power);
            cmbAnt9Power.setBounds(60, 140, 80, 30);

            //---- label12 ----
            label12.setText("ANT9:");
            panel1.add(label12);
            label12.setBounds(15, 145, 40, 17);
            panel1.add(cmbAnt10Power);
            cmbAnt10Power.setBounds(220, 140, 80, 30);

            //---- label13 ----
            label13.setText("ANT10:");
            panel1.add(label13);
            label13.setBounds(175, 145, 55, 17);
            panel1.add(cmbAnt11Power);
            cmbAnt11Power.setBounds(60, 170, 80, 30);

            //---- label14 ----
            label14.setText("ANT11:");
            panel1.add(label14);
            label14.setBounds(10, 175, 60, 17);

            //---- label15 ----
            label15.setText("ANT12:");
            panel1.add(label15);
            label15.setBounds(170, 175, 60, 17);
            panel1.add(cmbAnt12Power);
            cmbAnt12Power.setBounds(220, 170, 80, 30);
            panel1.add(cmbAnt14Power);
            cmbAnt14Power.setBounds(220, 200, 80, 30);

            //---- label16 ----
            label16.setText("ANT14:");
            panel1.add(label16);
            label16.setBounds(170, 205, 60, 17);
            panel1.add(cmbAnt13Power);
            cmbAnt13Power.setBounds(60, 200, 80, 30);

            //---- label17 ----
            label17.setText("ANT13:");
            panel1.add(label17);
            label17.setBounds(10, 205, 60, 17);
            panel1.add(cmbAnt16Power);
            cmbAnt16Power.setBounds(220, 230, 80, 30);

            //---- label18 ----
            label18.setText("ANT16:");
            panel1.add(label18);
            label18.setBounds(170, 235, 60, 17);
            panel1.add(cmbAnt15Power);
            cmbAnt15Power.setBounds(60, 230, 80, 30);

            //---- label19 ----
            label19.setText("ANT15:");
            panel1.add(label19);
            label19.setBounds(10, 235, 60, 17);

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
        panel1.setBounds(5, 10, 325, 310);

        //======== panel2 ========
        {
            panel2.setPreferredSize(new Dimension(42, 42));
            panel2.setBorder(new TitledBorder("\u5929\u7ebf"));
            panel2.setLayout(null);

            //---- cbANT1 ----
            cbANT1.setText("ANT1");
            panel2.add(cbANT1);
            cbANT1.setBounds(15, 25, 70, cbANT1.getPreferredSize().height);

            //---- cbANT2 ----
            cbANT2.setText("ANT2");
            panel2.add(cbANT2);
            cbANT2.setBounds(85, 25, 75, 22);

            //---- cbANT3 ----
            cbANT3.setText("ANT3");
            panel2.add(cbANT3);
            cbANT3.setBounds(160, 25, 75, 22);

            //---- cbANT4 ----
            cbANT4.setText("ANT4");
            panel2.add(cbANT4);
            cbANT4.setBounds(235, 25, 80, 22);

            //---- btnSetANT ----
            btnSetANT.setText("\u8bbe\u7f6e");
            btnSetANT.addActionListener(e -> btnSetANTActionPerformed(e));
            panel2.add(btnSetANT);
            btnSetANT.setBounds(175, 130, 80, 30);

            //---- btnGetANT ----
            btnGetANT.setText("\u83b7\u53d6");
            btnGetANT.addActionListener(e -> btnGetANTActionPerformed(e));
            panel2.add(btnGetANT);
            btnGetANT.setBounds(60, 130, 85, 30);

            //---- cbANT5 ----
            cbANT5.setText("ANT5");
            panel2.add(cbANT5);
            cbANT5.setBounds(15, 50, 70, 22);

            //---- cbANT6 ----
            cbANT6.setText("ANT6");
            panel2.add(cbANT6);
            cbANT6.setBounds(85, 50, 70, 22);

            //---- cbANT7 ----
            cbANT7.setText("ANT7");
            panel2.add(cbANT7);
            cbANT7.setBounds(160, 50, 70, 22);

            //---- cbANT8 ----
            cbANT8.setText("ANT8");
            panel2.add(cbANT8);
            cbANT8.setBounds(235, 50, 70, 22);

            //---- cbANT9 ----
            cbANT9.setText("ANT9");
            panel2.add(cbANT9);
            cbANT9.setBounds(15, 75, 70, 22);

            //---- cbANT10 ----
            cbANT10.setText("ANT10");
            panel2.add(cbANT10);
            cbANT10.setBounds(85, 75, 70, 22);

            //---- cbANT11 ----
            cbANT11.setText("ANT11");
            panel2.add(cbANT11);
            cbANT11.setBounds(160, 75, 70, 22);

            //---- cbANT12 ----
            cbANT12.setText("ANT12");
            panel2.add(cbANT12);
            cbANT12.setBounds(235, 75, 70, 22);

            //---- cbANT13 ----
            cbANT13.setText("ANT13");
            panel2.add(cbANT13);
            cbANT13.setBounds(15, 100, 70, 22);

            //---- cbANT14 ----
            cbANT14.setText("ANT14");
            panel2.add(cbANT14);
            cbANT14.setBounds(85, 100, 70, 22);

            //---- cbANT15 ----
            cbANT15.setText("ANT15");
            panel2.add(cbANT15);
            cbANT15.setBounds(160, 100, 70, 22);

            //---- cbANT16 ----
            cbANT16.setText("ANT16");
            panel2.add(cbANT16);
            cbANT16.setBounds(235, 100, 70, 22);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel2.getComponentCount(); i++) {
                    Rectangle bounds = panel2.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel2.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel2.setMinimumSize(preferredSize);
                panel2.setPreferredSize(preferredSize);
            }
        }
        add(panel2);
        panel2.setBounds(5, 325, 325, 165);

        //======== panel3 ========
        {
            panel3.setPreferredSize(new Dimension(42, 42));
            panel3.setBorder(new TitledBorder("\u9891\u6bb5"));
            panel3.setLayout(null);

            //---- label5 ----
            label5.setText("\u9891\u6bb5:");
            panel3.add(label5);
            label5.setBounds(10, 35, 90, 17);
            panel3.add(cbFrequencyBand);
            cbFrequencyBand.setBounds(90, 30, 185, 30);

            //---- btnGetFrequencyBand ----
            btnGetFrequencyBand.setText("\u83b7\u53d6");
            btnGetFrequencyBand.addActionListener(e -> btnGetFrequencyBandActionPerformed(e));
            panel3.add(btnGetFrequencyBand);
            btnGetFrequencyBand.setBounds(55, 65, 85, 30);

            //---- btnSetFrequencyBand ----
            btnSetFrequencyBand.setText("\u8bbe\u7f6e");
            btnSetFrequencyBand.addActionListener(e -> btnSetFrequencyBandActionPerformed(e));
            panel3.add(btnSetFrequencyBand);
            btnSetFrequencyBand.setBounds(175, 65, 80, 30);

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
        panel3.setBounds(5, 495, 325, 110);

        //======== panel5 ========
        {
            panel5.setPreferredSize(new Dimension(42, 42));
            panel5.setBorder(new TitledBorder("\u534f\u8bae"));
            panel5.setLayout(null);

            //---- label6 ----
            label6.setText("\u534f\u8bae:");
            panel5.add(label6);
            label6.setBounds(15, 35, 65, 17);
            panel5.add(cmbProtocol);
            cmbProtocol.setBounds(80, 30, 200, 30);

            //---- btnSetProtocol ----
            btnSetProtocol.setText("\u8bbe\u7f6e");
            btnSetProtocol.addActionListener(e -> btnSetProtocolActionPerformed(e));
            panel5.add(btnSetProtocol);
            btnSetProtocol.setBounds(160, 65, 80, 30);

            //---- btnGetProtocol ----
            btnGetProtocol.setText("\u83b7\u53d6");
            btnGetProtocol.addActionListener(e -> btnGetProtocolActionPerformed(e));
            panel5.add(btnGetProtocol);
            btnGetProtocol.setBounds(40, 65, 85, 30);

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
        add(panel5);
        panel5.setBounds(715, 570, 325, 105);

        //======== panel17 ========
        {
            panel17.setPreferredSize(new Dimension(42, 42));
            panel17.setBorder(new TitledBorder("\u76d8\u70b9\u6a21\u5f0f"));
            panel17.setLayout(null);

            //---- label22 ----
            label22.setText("\u6a21\u5f0f:");
            panel17.add(label22);
            label22.setBounds(20, 30, 40, 17);
            panel17.add(cmbEPCAndTIDUserMode);
            cmbEPCAndTIDUserMode.setBounds(55, 25, 235, 30);

            //---- btnGetEPCAndTIDUserMode ----
            btnGetEPCAndTIDUserMode.setText("\u83b7\u53d6");
            btnGetEPCAndTIDUserMode.addActionListener(e -> btnGetEPCAndTIDUserModeActionPerformed(e));
            panel17.add(btnGetEPCAndTIDUserMode);
            btnGetEPCAndTIDUserMode.setBounds(55, 95, 85, 30);

            //---- btnSetEPCAndTIDUserMode ----
            btnSetEPCAndTIDUserMode.setText("\u8bbe\u7f6e");
            btnSetEPCAndTIDUserMode.addActionListener(e -> btnSetEPCAndTIDUserModeActionPerformed(e));
            panel17.add(btnSetEPCAndTIDUserMode);
            btnSetEPCAndTIDUserMode.setBounds(170, 95, 80, 30);

            //---- label23 ----
            label23.setText("User\u8d77\u59cb\u5730\u5740:");
            panel17.add(label23);
            label23.setBounds(new Rectangle(new Point(25, 65), label23.getPreferredSize()));
            panel17.add(tf_user_prt);
            tf_user_prt.setBounds(105, 60, 60, tf_user_prt.getPreferredSize().height);

            //---- label24 ----
            label24.setText("User\u957f\u5ea6:");
            panel17.add(label24);
            label24.setBounds(165, 65, 78, 17);
            panel17.add(tf_user_len);
            tf_user_len.setBounds(225, 60, 65, tf_user_len.getPreferredSize().height);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel17.getComponentCount(); i++) {
                    Rectangle bounds = panel17.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel17.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel17.setMinimumSize(preferredSize);
                panel17.setPreferredSize(preferredSize);
            }
        }
        add(panel17);
        panel17.setBounds(350, 10, 325, 140);

        //======== panel18 ========
        {
            panel18.setPreferredSize(new Dimension(42, 42));
            panel18.setBorder(new TitledBorder("Gen2"));
            panel18.setLayout(null);

            //---- label25 ----
            label25.setText("Session:");
            panel18.add(label25);
            label25.setBounds(10, 30, 75, 17);
            panel18.add(cmb_Gen2_session);
            cmb_Gen2_session.setBounds(70, 25, 85, 30);

            //---- label26 ----
            label26.setText("Target");
            panel18.add(label26);
            label26.setBounds(165, 30, 75, 17);
            panel18.add(cmb_Gen2_target);
            cmb_Gen2_target.setBounds(215, 25, 85, 30);

            //---- btnSetGen2 ----
            btnSetGen2.setText("\u8bbe\u7f6e");
            btnSetGen2.addActionListener(e -> btnSetGen2ActionPerformed(e));
            panel18.add(btnSetGen2);
            btnSetGen2.setBounds(175, 70, 80, 30);

            //---- btnGetGen2 ----
            btnGetGen2.setText("\u83b7\u53d6");
            btnGetGen2.addActionListener(e -> btnGetGen2ActionPerformed(e));
            panel18.add(btnGetGen2);
            btnGetGen2.setBounds(55, 70, 85, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel18.getComponentCount(); i++) {
                    Rectangle bounds = panel18.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel18.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel18.setMinimumSize(preferredSize);
                panel18.setPreferredSize(preferredSize);
            }
        }
        add(panel18);
        panel18.setBounds(350, 160, 325, 115);

        //======== panel19 ========
        {
            panel19.setPreferredSize(new Dimension(42, 42));
            panel19.setBorder(new TitledBorder("\u8702\u9e23\u5668"));
            panel19.setLayout(null);

            //---- label27 ----
            label27.setText("\u8702\u9e23\u5668:");
            panel19.add(label27);
            label27.setBounds(15, 35, 55, 17);

            //---- rbtn_Beep_On ----
            rbtn_Beep_On.setText("\u5f00");
            rbtn_Beep_On.addActionListener(e -> rbtn_Beep_OnActionPerformed(e));
            panel19.add(rbtn_Beep_On);
            rbtn_Beep_On.setBounds(75, 35, 50, rbtn_Beep_On.getPreferredSize().height);

            //---- rbtn_Beep_Off ----
            rbtn_Beep_Off.setText("\u5173");
            rbtn_Beep_Off.addActionListener(e -> rbtn_Beep_OffActionPerformed(e));
            panel19.add(rbtn_Beep_Off);
            rbtn_Beep_Off.setBounds(140, 35, 55, 21);

            //---- btnGetBeep ----
            btnGetBeep.setText("\u83b7\u53d6");
            btnGetBeep.addActionListener(e -> btnGetBeepActionPerformed(e));
            panel19.add(btnGetBeep);
            btnGetBeep.setBounds(60, 65, 85, 30);

            //---- btnSetBeep ----
            btnSetBeep.setText("\u8bbe\u7f6e");
            btnSetBeep.addActionListener(e -> btnSetBeepActionPerformed(e));
            panel19.add(btnSetBeep);
            btnSetBeep.setBounds(180, 65, 80, 30);

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
        panel19.setBounds(350, 290, 325, 100);

        //======== panel20 ========
        {
            panel20.setPreferredSize(new Dimension(42, 42));
            panel20.setBorder(new TitledBorder("URx IP"));
            panel20.setLayout(null);

            //---- label28 ----
            label28.setText("IP:");
            panel20.add(label28);
            label28.setBounds(15, 20, 29, label28.getPreferredSize().height);
            panel20.add(textFieldIP);
            textFieldIP.setBounds(90, 15, 245, textFieldIP.getPreferredSize().height);

            //---- label29 ----
            label29.setText("\u5b50\u7f51\u63a9\u7801:");
            panel20.add(label29);
            label29.setBounds(10, 50, 85, label29.getPreferredSize().height);

            //---- label30 ----
            label30.setText("\u7f51\u5173:");
            panel20.add(label30);
            label30.setBounds(10, 80, 65, label30.getPreferredSize().height);
            panel20.add(textFieldSubNetMask);
            textFieldSubNetMask.setBounds(90, 45, 245, textFieldSubNetMask.getPreferredSize().height);
            panel20.add(textFieldGateWay);
            textFieldGateWay.setBounds(90, 75, 245, textFieldGateWay.getPreferredSize().height);

            //---- btnGetLocal_IP ----
            btnGetLocal_IP.setText("\u83b7\u53d6");
            btnGetLocal_IP.addActionListener(e -> btnGetLocal_IPActionPerformed(e));
            panel20.add(btnGetLocal_IP);
            btnGetLocal_IP.setBounds(85, 145, 85, 30);

            //---- btnSetLocal_IP ----
            btnSetLocal_IP.setText("\u8bbe\u7f6e");
            btnSetLocal_IP.addActionListener(e -> btnSetLocal_IPActionPerformed(e));
            panel20.add(btnSetLocal_IP);
            btnSetLocal_IP.setBounds(205, 145, 80, 30);

            //---- label33 ----
            label33.setText("\u7aef\u53e3:");
            panel20.add(label33);
            label33.setBounds(10, 115, 65, 17);
            panel20.add(textFieldPort);
            textFieldPort.setBounds(90, 110, 245, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel20.getComponentCount(); i++) {
                    Rectangle bounds = panel20.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel20.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel20.setMinimumSize(preferredSize);
                panel20.setPreferredSize(preferredSize);
            }
        }
        add(panel20);
        panel20.setBounds(710, 15, 380, 190);

        //======== panel21 ========
        {
            panel21.setPreferredSize(new Dimension(42, 42));
            panel21.setBorder(new TitledBorder("\u81ea\u52a8\u6a21\u5f0f\u4e0b\u8fdc\u7a0bIP"));
            panel21.setLayout(null);

            //---- label31 ----
            label31.setText("IP:");
            panel21.add(label31);
            label31.setBounds(40, 30, 30, 17);
            panel21.add(textFieldDestinationIP);
            textFieldDestinationIP.setBounds(100, 20, 245, 30);

            //---- label32 ----
            label32.setText("\u7aef\u53e3:");
            panel21.add(label32);
            label32.setBounds(40, 60, 45, 17);
            panel21.add(textFieldDestinationPort);
            textFieldDestinationPort.setBounds(100, 55, 245, 30);

            //---- btnSetDestinationPort ----
            btnSetDestinationPort.setText("\u8bbe\u7f6e");
            btnSetDestinationPort.addActionListener(e -> btnSetDestinationPortActionPerformed(e));
            panel21.add(btnSetDestinationPort);
            btnSetDestinationPort.setBounds(215, 95, 80, 30);

            //---- btnGetDestinationIP ----
            btnGetDestinationIP.setText("\u83b7\u53d6");
            btnGetDestinationIP.addActionListener(e -> btnGetDestinationIPActionPerformed(e));
            panel21.add(btnGetDestinationIP);
            btnGetDestinationIP.setBounds(95, 95, 85, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel21.getComponentCount(); i++) {
                    Rectangle bounds = panel21.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel21.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel21.setMinimumSize(preferredSize);
                panel21.setPreferredSize(preferredSize);
            }
        }
        add(panel21);
        panel21.setBounds(710, 210, 380, 140);

        //======== panel22 ========
        {
            panel22.setPreferredSize(new Dimension(42, 42));
            panel22.setBorder(new TitledBorder("\u5de5\u4f5c\u6a21\u5f0f"));
            panel22.setLayout(null);

            //---- label34 ----
            label34.setText("\u6a21\u5f0f:");
            panel22.add(label34);
            label34.setBounds(10, 25, 75, 17);
            panel22.add(cmbWorkMode);
            cmbWorkMode.setBounds(70, 20, 235, 30);

            //---- btnSetWorkMode ----
            btnSetWorkMode.setText("\u8bbe\u7f6e");
            btnSetWorkMode.addActionListener(e -> btnSetFrequencyModeActionPerformed(e));
            panel22.add(btnSetWorkMode);
            btnSetWorkMode.setBounds(180, 65, 80, 30);

            //---- btnGetWorkMode ----
            btnGetWorkMode.setText("\u83b7\u53d6");
            btnGetWorkMode.addActionListener(e -> btnGetFrequencyModeActionPerformed(e));
            panel22.add(btnGetWorkMode);
            btnGetWorkMode.setBounds(60, 65, 85, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel22.getComponentCount(); i++) {
                    Rectangle bounds = panel22.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel22.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel22.setMinimumSize(preferredSize);
                panel22.setPreferredSize(preferredSize);
            }
        }
        add(panel22);
        panel22.setBounds(350, 395, 325, 105);

        //======== panel23 ========
        {
            panel23.setPreferredSize(new Dimension(42, 42));
            panel23.setBorder(new TitledBorder("\u94fe\u8def"));
            panel23.setLayout(null);
            panel23.add(cmbRFLink);
            cmbRFLink.setBounds(80, 30, 195, 30);

            //---- label7 ----
            label7.setText("\u94fe\u8def:");
            panel23.add(label7);
            label7.setBounds(15, 35, 70, 17);

            //---- btnSetRFLink ----
            btnSetRFLink.setText("\u8bbe\u7f6e");
            btnSetRFLink.addActionListener(e -> btnSetLinkActionPerformed(e));
            panel23.add(btnSetRFLink);
            btnSetRFLink.setBounds(155, 65, 80, 30);

            //---- btnGetRFLink ----
            btnGetRFLink.setText("\u83b7\u53d6");
            btnGetRFLink.addActionListener(e -> btnGetLinkActionPerformed(e));
            panel23.add(btnGetRFLink);
            btnGetRFLink.setBounds(35, 65, 85, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel23.getComponentCount(); i++) {
                    Rectangle bounds = panel23.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel23.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel23.setMinimumSize(preferredSize);
                panel23.setPreferredSize(preferredSize);
            }
        }
        add(panel23);
        panel23.setBounds(350, 505, 325, 110);

        //======== panel24 ========
        {
            panel24.setPreferredSize(new Dimension(42, 42));
            panel24.setBorder(new TitledBorder("UR4-GPIO"));
            panel24.setLayout(null);

            //---- btnSetGPIO ----
            btnSetGPIO.setText("\u8bbe\u7f6e");
            btnSetGPIO.addActionListener(e -> btnSetGPIOActionPerformed(e));
            panel24.add(btnSetGPIO);
            btnSetGPIO.setBounds(310, 20, 60, 30);
            panel24.add(cmbRelay);
            cmbRelay.setBounds(70, 20, 235, 30);

            //---- label35 ----
            label35.setText("\u7ee7\u7535\u5668:");
            panel24.add(label35);
            label35.setBounds(10, 25, 65, 17);

            //---- label36 ----
            label36.setText("\u8f93\u51651:");
            panel24.add(label36);
            label36.setBounds(10, 65, 70, 17);

            //---- label37 ----
            label37.setText("\u8f93\u51652:");
            panel24.add(label37);
            label37.setBounds(170, 65, 70, 17);

            //---- btnGetGPIO ----
            btnGetGPIO.setText("\u83b7\u53d6");
            btnGetGPIO.addActionListener(e -> btnGetGPIOActionPerformed(e));
            panel24.add(btnGetGPIO);
            btnGetGPIO.setBounds(310, 60, 60, 30);
            panel24.add(cmbInput1);
            cmbInput1.setBounds(70, 60, 95, cmbInput1.getPreferredSize().height);
            panel24.add(cmbInput2);
            cmbInput2.setBounds(220, 60, 83, cmbInput2.getPreferredSize().height);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel24.getComponentCount(); i++) {
                    Rectangle bounds = panel24.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel24.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel24.setMinimumSize(preferredSize);
                panel24.setPreferredSize(preferredSize);
            }
        }
        add(panel24);
        panel24.setBounds(710, 355, 380, 100);

        //======== panel25 ========
        {
            panel25.setPreferredSize(new Dimension(42, 42));
            panel25.setBorder(new TitledBorder("UR1A-GPIO"));
            panel25.setLayout(null);

            //---- btnUR1ASetGPIO ----
            btnUR1ASetGPIO.setText("\u8bbe\u7f6e");
            btnUR1ASetGPIO.addActionListener(e -> btnUR1ASetGPIOActionPerformed(e));
            panel25.add(btnUR1ASetGPIO);
            btnUR1ASetGPIO.setBounds(310, 20, 60, 30);
            panel25.add(cbOutput1);
            cbOutput1.setBounds(70, 20, 85, 30);

            //---- label38 ----
            label38.setText("\u8f93\u51fa1:");
            panel25.add(label38);
            label38.setBounds(10, 25, 75, 17);

            //---- label39 ----
            label39.setText("\u8f93\u51651:");
            panel25.add(label39);
            label39.setBounds(10, 65, 75, 17);

            //---- label40 ----
            label40.setText("\u8f93\u51652:");
            panel25.add(label40);
            label40.setBounds(165, 65, 75, 17);

            //---- btnUR1AGetGPIO ----
            btnUR1AGetGPIO.setText("\u83b7\u53d6");
            btnUR1AGetGPIO.addActionListener(e -> btnUR1AGetGPIOActionPerformed(e));
            panel25.add(btnUR1AGetGPIO);
            btnUR1AGetGPIO.setBounds(310, 60, 60, 30);
            panel25.add(cbUR1AInput1);
            cbUR1AInput1.setBounds(70, 60, 85, cbUR1AInput1.getPreferredSize().height);
            panel25.add(cbUR1AInput2);
            cbUR1AInput2.setBounds(220, 60, 83, cbUR1AInput2.getPreferredSize().height);

            //---- label41 ----
            label41.setText("\u8f93\u51fa2:");
            panel25.add(label41);
            label41.setBounds(165, 25, 65, 17);
            panel25.add(cbOutput2);
            cbOutput2.setBounds(215, 20, 83, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel25.getComponentCount(); i++) {
                    Rectangle bounds = panel25.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel25.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel25.setMinimumSize(preferredSize);
                panel25.setPreferredSize(preferredSize);
            }
        }
        add(panel25);
        panel25.setBounds(710, 465, 380, 100);

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

    private void initUI() {
        for (int k = 1; k <= 30; k++) {
            cmbAnt1Power.addItem(k);
            cmbAnt2Power.addItem(k);
            cmbAnt3Power.addItem(k);
            cmbAnt4Power.addItem(k);

            cmbAnt5Power.addItem(k);
            cmbAnt6Power.addItem(k);
            cmbAnt7Power.addItem(k);
            cmbAnt8Power.addItem(k);

            cmbAnt9Power.addItem(k);
            cmbAnt10Power.addItem(k);
            cmbAnt11Power.addItem(k);
            cmbAnt12Power.addItem(k);

            cmbAnt13Power.addItem(k);
            cmbAnt14Power.addItem(k);
            cmbAnt15Power.addItem(k);
            cmbAnt16Power.addItem(k);
        }
        cbFrequencyBand.addItem("China1(840~845MHz)");
        cbFrequencyBand.addItem("China2(920~925MHz)");
        cbFrequencyBand.addItem("Europe(865~868MHz)");
        cbFrequencyBand.addItem("USA(902~928MHz)");
        cbFrequencyBand.addItem("Korea(917~923MHz)");
        cbFrequencyBand.addItem("Japan(952~953MHz)");
        cbFrequencyBand.addItem("Taiwan(920~928Mhz)");
        cbFrequencyBand.addItem("South Africa(915~919MHz)");
        cbFrequencyBand.addItem("Peru(915-928 MHz)");
        cbFrequencyBand.addItem("Russia(860MHz-867.6MHz)");

        cmbProtocol.addItem("ISO18000-6C");
        cmbProtocol.addItem("GB/T 29768");
        cmbProtocol.addItem("GJB 7377.1");
        cmbProtocol.addItem("ISO18000-6B");

        cmbRFLink.addItem("0-PR ASK/Miller8/160KHz");
        cmbRFLink.addItem("1-PR ASK /Miller4/250KHz");
        cmbRFLink.addItem("2-PR ASK/Miller4/ 320KHz");
        cmbRFLink.addItem("3-PR ASK/Miller4/640KHz");
        cmbRFLink.addItem("4-PR ASK/Miller2/320KHz");
        cmbRFLink.addItem("5-PR ASK/Miller2/640KHz");
        cmbRFLink.addItem("A-Gen2X/Miller8/ 160KHz");
        cmbRFLink.addItem("B-Gen2X/Miller4/ 250KHz");
        cmbRFLink.addItem("C-Gen2X/Miller4/320KHz");
        cmbRFLink.addItem("D-Gen2x/Miller4/ 640KHz");
        cmbRFLink.addItem("E-Gen2X/Miller2/320KHz");
        cmbRFLink.addItem("F-Gen2X/Miller2/ 640KHz");


        cmbEPCAndTIDUserMode.addItem("EPC");
        cmbEPCAndTIDUserMode.addItem("EPC+TID");
        cmbEPCAndTIDUserMode.addItem("EPC+TID+USER");



        cmb_Gen2_session.addItem("S0");
        cmb_Gen2_session.addItem("S1");
        cmb_Gen2_session.addItem("S2");
        cmb_Gen2_session.addItem("S3");
        cmb_Gen2_target.addItem("A");
        cmb_Gen2_target.addItem("B");

        rbtn_Beep_On.setSelected(true);
        rbtn_Beep_Off.setSelected(false);

        cmbRelay.addItem("断开");
        cmbRelay.addItem("闭合");
        cmbInput1.addItem("低电平");
        cmbInput1.addItem("高电平");
        cmbInput2.addItem("低电平");
        cmbInput2.addItem("高电平");


        cbOutput1.addItem("低电平");
        cbOutput1.addItem("高电平");
        cbOutput2.addItem("低电平");
        cbOutput2.addItem("高电平");

        cbUR1AInput1.addItem("低电平");
        cbUR1AInput1.addItem("高电平");
        cbUR1AInput2.addItem("低电平");
        cbUR1AInput2.addItem("高电平");

        if(UHFMainForm.isEnglish()){
            btnGetPower.setText("GET");
            btnGetANT.setText("GET");
            btnSetANT.setText("SET");
            btnGetFrequencyBand.setText("GET");
            btnSetFrequencyBand.setText("SET");
            label5.setText("Frequency band:");
            btnGetProtocol.setText("GET");
            btnSetProtocol.setText("SET");
            label6.setText("Protocol:");
            label7.setText("Link:");
            btnGetRFLink.setText("GET");
            btnSetRFLink.setText("SET");
            btnGetGen2.setText("GET");
            btnSetGen2.setText("SET");
            label27.setText("Buzzer:");
            rbtn_Beep_On.setText("On");
            rbtn_Beep_Off.setText("Off");
            btnGetBeep.setText("GET");
            btnSetBeep.setText("SET");
            label22.setText("Mode");
            label23.setText("UserOffset:");
            label24.setText("UserLength:");
            btnGetEPCAndTIDUserMode.setText("GET");
            btnSetEPCAndTIDUserMode.setText("SET");

            btnGetWorkMode.setText("GET");
            btnSetWorkMode.setText("SET");

            label29.setText("SubnetMask:");
            label30.setText("Gateway:");
            label33.setText("Port:");
            TitledBorder titledBorder=new TitledBorder("ANT Power");
            panel1.setBorder(titledBorder);
            TitledBorder titledBorder2=new TitledBorder("ANT");
            panel2.setBorder(titledBorder2);
            TitledBorder titledBorder3=new TitledBorder("FrequencyBand");
            panel3.setBorder(titledBorder3);
            TitledBorder titledBorder9=new TitledBorder("Buzzer");
            panel19.setBorder(titledBorder9);
            TitledBorder titledBorder23=new TitledBorder("RFLink");
            panel23.setBorder(titledBorder23);
            TitledBorder titledBorder17=new TitledBorder("Inventory Mode");
            panel17.setBorder(titledBorder17);

            TitledBorder titledBorder5=new TitledBorder("Protocol");
            panel5.setBorder(titledBorder17);
            TitledBorder titledBorder20=new TitledBorder("URx IP");
            panel20.setBorder(titledBorder20);

            TitledBorder titledBorder22=new TitledBorder("Work mode:");
            panel22.setBorder(titledBorder22);

            TitledBorder titledBorder21=new TitledBorder("Remote ip address in automatic mode");
            panel21.setBorder(titledBorder21);



            cmbWorkMode.removeAllItems();
            cmbWorkMode.addItem("command mode");
            cmbWorkMode.addItem("auto mode");

            cmbRelay.removeAllItems();
            cmbRelay.addItem("Disconnect");
            cmbRelay.addItem("Close");

            cmbInput1.removeAllItems();
            cmbInput1.addItem("low voltage");
            cmbInput1.addItem("high voltage");

            cmbInput2.removeAllItems();
            cmbInput2.addItem("low voltage");
            cmbInput2.addItem("high voltage");

            cbOutput1.removeAllItems();
            cbOutput2.removeAllItems();
            cbOutput1.addItem("low voltage");
            cbOutput1.addItem("high voltage");
            cbOutput2.addItem("low voltage");
            cbOutput2.addItem("high voltage");
            cbUR1AInput1.removeAllItems();
            cbUR1AInput2.removeAllItems();
            cbUR1AInput1.addItem("low voltage");
            cbUR1AInput1.addItem("high voltage");
            cbUR1AInput2.addItem("low voltage");
            cbUR1AInput2.addItem("high voltage");


            btnSetPoer.setText("Set");
            label35.setText("Relay:");
            btnGetDestinationIP.setText("Get");
            btnSetDestinationPort.setText("Set");
            label34.setText("Mode");
            btnGetLocal_IP.setText("Get");
            btnSetLocal_IP.setText("Set");
            label32.setText("Port:");
            label36.setText("input1:");
            label37.setText("input2:");
            btnGetGPIO.setText("Get");
            btnSetGPIO.setText("Set");
            label38.setText("Output1:");
            label41.setText("Output2:");
            label39.setText("input1:");
            label40.setText("input2:");

            btnUR1ASetGPIO.setText("Set");
            btnUR1AGetGPIO.setText("Get");
        }else {
            cmbWorkMode.removeAllItems();
            cmbWorkMode.addItem("命令工作模式");
            cmbWorkMode.addItem("自动工作模式");
        }

        label8.setVisible(ANT_NUMBER>4?true:false);
        label9.setVisible(ANT_NUMBER>4?true:false);
        label10.setVisible(ANT_NUMBER>4?true:false);
        label11.setVisible(ANT_NUMBER>4?true:false);
        cmbAnt5Power.setVisible(ANT_NUMBER>4?true:false);
        cmbAnt6Power.setVisible(ANT_NUMBER>4?true:false);
        cmbAnt7Power.setVisible(ANT_NUMBER>4?true:false);
        cmbAnt8Power.setVisible(ANT_NUMBER>4?true:false);

        label12.setVisible(ANT_NUMBER>8?true:false);
        label13.setVisible(ANT_NUMBER>8?true:false);
        label14.setVisible(ANT_NUMBER>8?true:false);
        label15.setVisible(ANT_NUMBER>8?true:false);
        label16.setVisible(ANT_NUMBER>8?true:false);
        label17.setVisible(ANT_NUMBER>8?true:false);
        label18.setVisible(ANT_NUMBER>8?true:false);
        label19.setVisible(ANT_NUMBER>8?true:false);
        cmbAnt9Power.setVisible(ANT_NUMBER>8?true:false);
        cmbAnt10Power.setVisible(ANT_NUMBER>8?true:false);
        cmbAnt11Power.setVisible(ANT_NUMBER>8?true:false);
        cmbAnt12Power.setVisible(ANT_NUMBER>8?true:false);
        cmbAnt13Power.setVisible(ANT_NUMBER>8?true:false);
        cmbAnt14Power.setVisible(ANT_NUMBER>8?true:false);
        cmbAnt15Power.setVisible(ANT_NUMBER>8?true:false);
        cmbAnt16Power.setVisible(ANT_NUMBER>8?true:false);

        cbANT5.setVisible(ANT_NUMBER>4?true:false);
        cbANT6.setVisible(ANT_NUMBER>4?true:false);
        cbANT7.setVisible(ANT_NUMBER>4?true:false);
        cbANT8.setVisible(ANT_NUMBER>4?true:false);

        cbANT9.setVisible(ANT_NUMBER>8?true:false);
        cbANT10.setVisible(ANT_NUMBER>8?true:false);
        cbANT11.setVisible(ANT_NUMBER>8?true:false);
        cbANT12.setVisible(ANT_NUMBER>8?true:false);
        cbANT13.setVisible(ANT_NUMBER>8?true:false);
        cbANT14.setVisible(ANT_NUMBER>8?true:false);
        cbANT15.setVisible(ANT_NUMBER>8?true:false);
        cbANT16.setVisible(ANT_NUMBER>8?true:false);
    }
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel label1;
    private JComboBox cmbAnt1Power;
    private JComboBox cmbAnt2Power;
    private JLabel label2;
    private JComboBox cmbAnt3Power;
    private JLabel label3;
    private JComboBox cmbAnt4Power;
    private JLabel label4;
    private JButton btnGetPower;
    private JButton btnSetPoer;
    private JLabel label8;
    private JComboBox cmbAnt5Power;
    private JComboBox cmbAnt6Power;
    private JLabel label9;
    private JComboBox cmbAnt7Power;
    private JLabel label10;
    private JComboBox cmbAnt8Power;
    private JLabel label11;
    private JComboBox cmbAnt9Power;
    private JLabel label12;
    private JComboBox cmbAnt10Power;
    private JLabel label13;
    private JComboBox cmbAnt11Power;
    private JLabel label14;
    private JLabel label15;
    private JComboBox cmbAnt12Power;
    private JComboBox cmbAnt14Power;
    private JLabel label16;
    private JComboBox cmbAnt13Power;
    private JLabel label17;
    private JComboBox cmbAnt16Power;
    private JLabel label18;
    private JComboBox cmbAnt15Power;
    private JLabel label19;
    private JPanel panel2;
    private JCheckBox cbANT1;
    private JCheckBox cbANT2;
    private JCheckBox cbANT3;
    private JCheckBox cbANT4;
    private JButton btnSetANT;
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
    private JPanel panel3;
    private JLabel label5;
    private JComboBox cbFrequencyBand;
    private JButton btnGetFrequencyBand;
    private JButton btnSetFrequencyBand;
    private JPanel panel5;
    private JLabel label6;
    private JComboBox cmbProtocol;
    private JButton btnSetProtocol;
    private JButton btnGetProtocol;
    private JPanel panel17;
    private JLabel label22;
    private JComboBox cmbEPCAndTIDUserMode;
    private JButton btnGetEPCAndTIDUserMode;
    private JButton btnSetEPCAndTIDUserMode;
    private JLabel label23;
    private JTextField tf_user_prt;
    private JLabel label24;
    private JTextField tf_user_len;
    private JPanel panel18;
    private JLabel label25;
    private JComboBox cmb_Gen2_session;
    private JLabel label26;
    private JComboBox cmb_Gen2_target;
    private JButton btnSetGen2;
    private JButton btnGetGen2;
    private JPanel panel19;
    private JLabel label27;
    private JRadioButton rbtn_Beep_On;
    private JRadioButton rbtn_Beep_Off;
    private JButton btnGetBeep;
    private JButton btnSetBeep;
    private JPanel panel20;
    private JLabel label28;
    private JTextField textFieldIP;
    private JLabel label29;
    private JLabel label30;
    private JTextField textFieldSubNetMask;
    private JTextField textFieldGateWay;
    private JButton btnGetLocal_IP;
    private JButton btnSetLocal_IP;
    private JLabel label33;
    private JTextField textFieldPort;
    private JPanel panel21;
    private JLabel label31;
    private JTextField textFieldDestinationIP;
    private JLabel label32;
    private JTextField textFieldDestinationPort;
    private JButton btnSetDestinationPort;
    private JButton btnGetDestinationIP;
    private JPanel panel22;
    private JLabel label34;
    private JComboBox cmbWorkMode;
    private JButton btnSetWorkMode;
    private JButton btnGetWorkMode;
    private JPanel panel23;
    private JComboBox cmbRFLink;
    private JLabel label7;
    private JButton btnSetRFLink;
    private JButton btnGetRFLink;
    private JPanel panel24;
    private JButton btnSetGPIO;
    private JComboBox cmbRelay;
    private JLabel label35;
    private JLabel label36;
    private JLabel label37;
    private JButton btnGetGPIO;
    private JComboBox cmbInput1;
    private JComboBox cmbInput2;
    private JPanel panel25;
    private JButton btnUR1ASetGPIO;
    private JComboBox cbOutput1;
    private JLabel label38;
    private JLabel label39;
    private JLabel label40;
    private JButton btnUR1AGetGPIO;
    private JComboBox cbUR1AInput1;
    private JComboBox cbUR1AInput2;
    private JLabel label41;
    private JComboBox cbOutput2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
