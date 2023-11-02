package com.mirusystems.usbsave.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LogDao {
    @Query("SELECT * FROM log;")
    List<LogData> getAll();

    @Insert
    void insert(LogData logData);

    @Query("SELECT * FROM log WHERE datetime >= :start;")
    List<LogData> getLogs(long start);

    @Query("SELECT * FROM log WHERE datetime >= :start AND datetime <= :end;")
    List<LogData> getLogs(long start, long end);

    @Query("SELECT govId, edId FROM log WHERE datetime >= :start GROUP BY govId, edId")
    List<GovEd> getGovEdLogs(long start);

    @Query("SELECT govId, edId FROM log WHERE datetime >= :start AND deviceName=:deviceName GROUP BY govId, edId")
    List<GovEd> getGovEdLogs(long start, String deviceName);

    @Query("SELECT * FROM log WHERE datetime >= :start AND govId=:govId AND edId=:edId;")
    List<LogData> getTodayLogs(long start, int govId, int edId);

    @Query("SELECT * FROM log WHERE datetime >= :start AND govId=:govId AND edId=:edId AND deviceName=:deviceName;")
    List<LogData> getTodayLogs(long start, int govId, int edId, String deviceName);

    @Query("SELECT psId FROM log WHERE datetime >= :start AND govId=:govId AND edId=:edId AND deviceName=:deviceName ORDER BY psId;")
    List<Integer> getTodayPsIds(long start, int govId, int edId, String deviceName);
}
