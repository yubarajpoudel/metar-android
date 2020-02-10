package com.yuvi.metar.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yuvi.metar.data.Metar;
import com.yuvi.metar.data.MetarRepository;

public class MainViewModel extends AndroidViewModel {
    private MetarRepository mRepository;

    private LiveData<Metar[]> metarLiveData;

     LiveData<Metar[]> getMetarLiveData(String station) {
         metarLiveData = mRepository.getMetar(station);
        return metarLiveData;
    }

    public void insert(Metar metar) { mRepository.insert(metar); }

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MetarRepository(application);
    }
}
