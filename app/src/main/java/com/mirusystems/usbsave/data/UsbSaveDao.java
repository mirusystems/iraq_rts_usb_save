package com.mirusystems.usbsave.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UsbSaveDao {
    @Query("SELECT * FROM usb_list")
    List<UsbListEntity> getAll();

    @Insert
    void insert(UsbListEntity usbListEntity);

    @Insert
    void insertAll(UsbListEntity... usbListEntity);

    @Delete
    void delete(UsbListEntity usbListEntity);
    @Query("SELECT * FROM usb_list WHERE gov_id=:govCode ORDER BY vrc_id, pc_id")
    List<UsbListEntity> getPsList(int govCode);

    @Query("SELECT * FROM usb_list WHERE pc_id=:pcCode")
    UsbListEntity getPollingStation(int pcCode);

    @Query("SELECT gov_id AS code, gov_name AS name, COUNT(*) AS totalCount, sum(done) AS completedCount FROM usb_list GROUP BY gov_id")
    List<UsbDistrict> getGovListForSimulation();

    @Query("SELECT COUNT(PC_ID) AS completedCount FROM usb_list WHERE GOV_ID=:govCode AND done > 0;")
    Integer getCompletedPsCountForSimulation(int govCode);


    @Update
    void update(UsbListEntity usbListEntity);
}
