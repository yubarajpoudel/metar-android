package com.yuvi.metar.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.yuvi.metar.data.Metar;

@Dao
public interface MetarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Metar metar);

    @Query("DELETE FROM metar")
    void deleteAll();

    @Query("SELECT * FROM metar WHERE airport_tag=:station")
    LiveData<Metar[]> getMetar(String station);
}
