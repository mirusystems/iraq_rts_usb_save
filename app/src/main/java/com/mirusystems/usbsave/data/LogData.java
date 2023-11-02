package com.mirusystems.usbsave.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "log")
public class LogData {
    @Ignore
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @PrimaryKey(autoGenerate = true)
    public int id;
    @TypeConverters({Converters.class})
    public Date datetime;
    public String datetimeStr;
    public Integer govId;
    public Integer edId;
    public Integer vrcId;
    public Integer pcId;
    public Integer psId;
    public String deviceName;
    public String result;

    public LogData(Date datetime, Integer govId, Integer edId, Integer vrcId, Integer pcId, Integer psId, String deviceName, String result) {
        this.datetime = datetime;
        this.datetimeStr = SIMPLE_DATE_FORMAT.format(datetime);
        this.govId = govId;
        this.edId = edId;
        this.vrcId = vrcId;
        this.pcId = pcId;
        this.psId = psId;
        this.deviceName = deviceName;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public Date getDatetime() {
        return datetime;
    }

    public String getDatetimeStr() {
        return datetimeStr;
    }

    public Integer getGovId() {
        return govId;
    }

    public Integer getEdId() {
        return edId;
    }

    public Integer getVrcId() {
        return vrcId;
    }

    public Integer getPcId() {
        return pcId;
    }

    public Integer getPsId() {
        return psId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "LogData{" +
                "id=" + id +
                ", datetime=" + datetime +
                ", datetimeStr='" + datetimeStr + '\'' +
                ", govId=" + govId +
                ", edId=" + edId +
                ", vrcId=" + vrcId +
                ", pcId=" + pcId +
                ", psId=" + psId +
                ", deviceName='" + deviceName + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
