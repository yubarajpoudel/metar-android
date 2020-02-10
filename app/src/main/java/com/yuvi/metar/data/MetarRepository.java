package com.yuvi.metar.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.yuvi.metar.data.local.MetarDao;
import com.yuvi.metar.data.local.MetarDatabase;

public class MetarRepository {

    private MetarDao metarDao;

    private LiveData<Metar[]> metarLiveData;

    public MetarRepository(Application application) {
        MetarDatabase db = MetarDatabase.getDatabase(application);
        metarDao = db.metarDao();
    }

    public LiveData<Metar[]> getMetar(String station) {
        metarLiveData = metarDao.getMetar(station);
        return metarLiveData;
    }
    public void insert (Metar metar) {
        new InsertAsync(metarDao).execute(metar);
    }

    public static class InsertAsync extends AsyncTask<Metar, Void, Void> {
        private MetarDao metarDao;
        public InsertAsync(MetarDao metarDao) {
            this.metarDao = metarDao;
        }
        @Override
        protected Void doInBackground(Metar... metars) {
            metarDao.insert(metars[0]);
            return null;
        }
    }
}
