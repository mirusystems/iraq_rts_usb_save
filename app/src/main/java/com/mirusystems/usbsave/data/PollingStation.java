package com.mirusystems.usbsave.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.util.Objects;

@Entity(tableName = "PS_INFO")
public class PollingStation {
    private static final String TAG = "PollingStation";
    @ColumnInfo(name = "GOV_ID")
    public Integer govCode;
    @ColumnInfo(name = "GOV_NAME")
    public String govName;
    @ColumnInfo(name = "ED_ID")
    public Integer edCode;
    @ColumnInfo(name = "ED_NAME")
    public String edName;
    @ColumnInfo(name = "VRC_ID")
    public Integer vrcCode;
    @ColumnInfo(name = "VRC_NAME")
    public String vrcName;
    @ColumnInfo(name = "PC_ID")
    public Integer pcCode;
    @ColumnInfo(name = "PC_NAME")
    public String pcName;
    @PrimaryKey
    @ColumnInfo(name = "PS_ID")
    public Integer psCode;
    @ColumnInfo(name = "PSO")
    public Integer pso;
    @ColumnInfo(name = "VVD")
    public Integer vvd;
    @ColumnInfo(name = "PCOS")
    public Integer pcos;
    @ColumnInfo(name = "RTS")
    public Integer rts;

    public PollingStation() {
        govCode = 1;
        govName = "GOV";
        edCode = 6;
        edName = "ED";
        vrcCode = 1052;
        vrcName = "VRC";
        pcCode = 345678;
        pcName = "PC";
        psCode = 34567890;
    }

    public Integer getGovCode() {
        return govCode;
    }

    public String getGovName() {
        return govName;
    }

    public Integer getEdCode() {
        return edCode;
    }

    public String getEdName() {
        return edName;
    }

    public Integer getVrcCode() {
        return vrcCode;
    }

    public String getVrcName() {
        return vrcName;
    }

    public Integer getPcCode() {
        return pcCode;
    }

    public String getPcName() {
        return pcName;
    }

    public Integer getPsCode() {
        return psCode;
    }

    public Integer getPso() {
        return pso;
    }

    public Integer getVvd() {
        return vvd;
    }

    public Integer getPcos() {
        return pcos;
    }

    public Integer getRts() {
        return rts;
    }

    public String getPsoQrString() {
        String psCode = String.valueOf(getPsCode());
        return String.format("%02d%02d-%s-%s-%s", getGovCode(), getEdCode(), psCode.substring(0, 4), psCode.substring(4, 6), psCode.substring(6, 8));
    }

    public String getInfoString() {
        return String.format("GOV:%02d ED:%02d VRC:%d PC:%d PS:%d", getGovCode(), getEdCode(), getVrcCode(), getPcCode(), getPsCode());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getGovCode(), getGovName(), getEdCode(), getEdName(), getVrcCode(), getVrcName(), getPcCode(), getPcName(), getPsCode(), getPso(), getVvd(), getPcos(), getRts());
    }

    @Override
    public String toString() {
        return "PollingStation{" +
                "govCode=" + govCode +
                ", govName='" + govName + '\'' +
                ", edCode=" + edCode +
                ", edName='" + edName + '\'' +
                ", vrcCode=" + vrcCode +
                ", vrcName='" + vrcName + '\'' +
                ", pcCode=" + pcCode +
                ", pcName='" + pcName + '\'' +
                ", psCode=" + psCode +
                ", pso=" + pso +
                ", vvd=" + vvd +
                ", pcos=" + pcos +
                ", rts=" + rts +
                '}';
    }
}
