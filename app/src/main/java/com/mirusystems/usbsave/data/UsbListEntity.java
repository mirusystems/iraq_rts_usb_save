package com.mirusystems.usbsave.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "usb_list")
public class UsbListEntity {
    @PrimaryKey
    @ColumnInfo(name = "pc_id")
    public Integer pc_id;
    @ColumnInfo(name = "num_ps")
    public Integer num_ps;
    @ColumnInfo(name = "elec_type")
    public Integer elec_type;
    @ColumnInfo(name = "vrc_id")
    public Integer vrc_id;
    @ColumnInfo(name = "gov_id")
    public Integer gov_id;
    @ColumnInfo(name = "pc_name")
    public String pc_name;
    @ColumnInfo(name = "vrc_name")
    public String vrc_name;
    @ColumnInfo(name = "gov_name")
    public String gov_name;
    @ColumnInfo(name = "done")
    public Integer done;

    public UsbListEntity() {
        num_ps = 1;
        elec_type = 1;
        vrc_id = 1052;
        gov_id = 1;
        pc_name = "pc";
        vrc_name = "vrc";
        gov_name = "gov";
        done = 1;
    }


    public Integer getPcId() {
        return pc_id;
    }

    public void setPcId(Integer pc_id) {
        this.pc_id = pc_id;
    }

    public Integer getNumPs() {
        return num_ps;
    }

    public void setNumPs(Integer num_ps) {
        this.num_ps = num_ps;
    }

    public Integer getElecType() {
        return elec_type;
    }

    public void setElecType(Integer elec_type) {
        this.elec_type = elec_type;
    }

    public Integer getVrcId() {
        return vrc_id;
    }

    public void setVrcId(Integer vrc_id) {
        this.vrc_id = vrc_id;
    }

    public Integer getGovId() {
        return gov_id;
    }

    public void setGovId(Integer gov_id) {
        this.gov_id = gov_id;
    }

    public String getPcName() {
        return pc_name;
    }

    public void setPcName(String pc_name) {
        this.pc_name = pc_name;
    }

    public String getVrcName() {
        return vrc_name;
    }

    public void setVrcName(String vrc_name) {
        this.vrc_name = vrc_name;
    }

    public String getGovName() {
        return gov_name;
    }

    public void setGovName(String gov_name) {
        this.gov_name = gov_name;
    }

    public Integer getDone() {
        return done;
    }

    public void setDone(Integer done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "UsbListEntity{" +
                "pc_id=" + pc_id +
                ", num_ps=" + num_ps +
                ", elec_type=" + elec_type +
                ", vrc_id=" + vrc_id +
                ", gov_id=" + gov_id +
                ", pc_name='" + pc_name + '\'' +
                ", vrc_name='" + vrc_name + '\'' +
                ", gov_name='" + gov_name + '\'' +
                ", done=" + done +
                '}';
    }
}
