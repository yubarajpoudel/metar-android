package com.yuvi.metar.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.yuvi.metar.data.local.DateTimeTypeConverter;

import java.util.Date;

@Entity(tableName = "metar", primaryKeys = {"airport_tag", "metar_type"})
public class Metar {
    @NonNull
    @ColumnInfo(name = "airport_tag")
    private String airportTag;

    @ColumnInfo(name = "data")
    private String data;

    @NonNull
    @ColumnInfo(name = "metar_type")
    private int metarType;

    @TypeConverters(DateTimeTypeConverter.class)
    Date lastSyncedTime;

    public String getAirportTag() {
        return airportTag;
    }

    public void setAirportTag(String airportTag) {
        this.airportTag = airportTag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getMetarType() {
        return metarType;
    }

    public void setMetarType(int metarType) {
        this.metarType = metarType;
    }

    public Date getLastSyncedTime() {
        return lastSyncedTime;
    }

    public void setLastSyncedTime(Date lastSyncedTime) {
        this.lastSyncedTime = lastSyncedTime;
    }

    @NonNull
    @Override
    public String toString() {
        return " station : " + this.airportTag + ", type : " + this.metarType + ", data : " + this.data;
    }
}
