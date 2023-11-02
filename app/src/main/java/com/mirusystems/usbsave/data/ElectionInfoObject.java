/*---------------------------------------------------------------------------------------
 * Copyright 2017, 2018 (c) MIRUSYSTEMS.
 * Copying, distribution, or use of this source code in whole or in part is
 * prohibited without prior permission of MIRUSYSTEMS.
 * (http://www.mirusystems.co.kr)
 *
 * @Author    : Jinyong Kim
 * @Function : class for election information database object
 * @History    :
 *=================================================
 *  Index                Contents                     DATE         AUTHOR     REV.
 *----------------------------------------------------------------------------------------
 *   1                   First created              2017.01.01    J.Y.KIM      1.0
 *----------------------------------------------------------------------------------------
 *   2
 *=================================================*/
package com.mirusystems.usbsave.data;

//import android.database.sqlite.SQLiteDatabase;



import com.mirusystems.usbsave.App;

import java.util.Locale;

public class ElectionInfoObject {
    private String versinfo = "08:08 01/01/2017";
    private String country = "The Republic of Iraq";
    private String organ = "Independent High Electoral Commission";
    private String election_title = "انتخابات مجلس النواب ٢٠١٨ تصويت العام";
    private String election_title_ku = "ههڵبژاردنهكانی ئهنجومهنی نوێنهران ٢٠١٨";
    private String vote_date = "10/10/2021";
    private String open_time = "0700";
    private String close_time = "1800";
    private String gov_name = "Salah AL-Dien";
    private String vrc_name = "Salah AL-Dien";
    private String center_name = "Salah AL-Dien";
    private String station_name = "11000101 Salah AL-Dien No.58";
    private String gov_id = "01";
    private String station_id = "11000101";
    private String election_code="9999";
    private int total_voters = 500;
    private int total_ballots = 550;
    private int max_ballot = 2000;
    private int elec_count = 1;
    private String ed_id = "01";
    private int ed_count = 1;

    private static App mApp;

    public ElectionInfoObject()
    {
        mApp = App.getInstance();
    }

    public String getVersionInfo()
    {
        return versinfo;
    }

    public String getCountryInfo()
    {
        return country;
    }

    public String getOrganInfo()
    {
        return organ;
    }

    public String getElectionTitle()
    {
        return election_title;
    }

    public String getElectionTitleKu()
    {
        return election_title_ku;
    }

    public String getVotingDate()
    {
        return vote_date;
    }

    public String getOpenTime()
    {
        return open_time;
    }

    public String getCloseTime()
    {
        return close_time;
    }

    public String getGovernerateName()
    {
        return gov_name;
    }

    public String getVRCName()
    {
        return vrc_name;
    }

    public String getCenterName()
    {
        return center_name;
    }

    public String getStationName()
    {
        return station_name;
    }

    public String getGovernerateId()
    {
        return gov_id;
    }

    public String getStationId()
    {
        return station_id;
    }

    public String getDbStationId()
    {
        return station_id;
    }

    public String getElectionCode()
    {
        return election_code;
    }

    public int getTotalVoters()
    {
        return total_voters;
    }

    public int getTotalBallots()
    {
        return total_ballots;
    }

    public int getMaxBallots()
    {
        return max_ballot;
    }

    public int getElectionCount()
    {
        return elec_count;
    }

    public String getElectionDistrictId()
    {
        return ed_id;
    }

    public int getElectionDistrictCount()
    {
        return ed_count;
    }

    public void setVersionInfo(String value)
    {
        versinfo = value;
    }

    public void setCountryInfo(String value)
    {
        country = value;
    }

    public void setOrganInfo(String value)
    {
        organ = value;
    }

    public void setElectionTitle(String value)
    {
        election_title = value;
    }

    public void setElectionTitleKu(String value)
    {
        election_title_ku = value;
    }

    public void setVotingDate(String value)
    {
        vote_date = value;
    }

    public void setOpenTime(String value)
    {
        open_time = value;
    }

    public void setCloseTime(String value)
    {
        close_time = value;
    }

    public void setGovernerateName(String value)
    {
        gov_name = value;
    }

    public void setVRCName(String value)
    {
        vrc_name = value;
    }

    public void setCenterName(String value)
    {
        center_name = value;
    }

    public void setStationName(String value)
    {
        station_name = value;
    }

    public void setGovernerateId(String value)
    {
        gov_id = String.format(Locale.US, "%02d", Integer.parseInt(value));
    }

    public void setStationId(String value)
    {
        station_id = value;
    }

    public void setElectionCode(String value)
    {
        election_code = value;
    }

    public void setTotalVoters(int value)
    {
        total_voters = value;
    }

    public void setTotalBallots(int value)
    {
        total_ballots = value;
    }

    public void setMaxBallots(int value)
    {
        max_ballot = value;
    }

    public void setElectionCount(int value)
    {
        elec_count = value;
    }

    public void setElectionDistrictId(String value)
    {
        if(value == null)
            ed_id = "01";
        else
            ed_id = String.format(Locale.US, "%02d", Integer.parseInt(value));
//        ed_id = value;
    }

    public void setElectionDistrictCount(int count)
    {
        ed_count = count;
    }

//    public void updateCloseTime(String value)
//    {
//        SQLiteDatabase db = idb.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//
//        Log.d("CONFIG", "updateStandalone mode="+value);
//        close_time = value;
//        cv.put(DbOpenHelper.COLUMN_ELECTIONINFO.close_time.name(), value);
//        db.update(DbOpenHelper.TABLE_ELECTION_INFO, cv, DbOpenHelper.COLUMN_ELECTIONINFO.idx.name()+" like '1'", null);
//    }

}
