package com.yuvi.metar.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.yuvi.metar.R;
import com.yuvi.metar.data.Metar;
import com.yuvi.metar.data.network.NetworkCall;
import com.yuvi.metar.databinding.ActivityMainBinding;
import com.yuvi.metar.utils.Constants;
import com.yuvi.metar.utils.Utils;

import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NetworkCall.OnDataReceiveListener, AutoCompleteTextView.Validator, TextWatcher {
    AppCompatAutoCompleteTextView autoCompleteAirports;
    TextView tvRaw, tvDecoded, tvMessage;
    MainViewModel model;
    String[] airportTags;
    Button btnSearch;
    ViewSwitcher switcher;
    LiveData<Metar[]> metarLiveData;
    Observer<Metar[]> metarObservable;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViews();
        airportTags = getResources().getStringArray(R.array.array_airports);
        setupAirportsAutoComplete();
        tvMessage.setText(getString(R.string.mesg_airport));

        model = ViewModelProviders.of(this).get(MainViewModel.class);
        btnSearch.setOnClickListener( v-> {
            String airport = autoCompleteAirports.getText().toString();
            Utils.debug(MainActivity.class, String.format("%s", airport));
            if(!TextUtils.isEmpty(airport)) {
                getDataFromServer(airport);
                if(metarLiveData != null && metarLiveData.hasObservers()) {
                    metarLiveData.removeObserver(metarObservable);
                }
                updateUI(airport);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.err_station), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(String airport) {
        metarLiveData =  model.getMetarLiveData(airport);
        metarObservable = metars -> {
            if(metars.length == 0) {
                Utils.debug(MainActivity.class, String.format("metar is null, airport = %s", metars.length, airport));
                Utils.debug(MainActivity.class, "value is null");
                switcher.setDisplayedChild(0);
                tvMessage.setText(Utils.isNetworkConnected(getApplicationContext()) ? getString(R.string.mesg_loading_data) : getString(R.string.mesg_connect_network));
            } else {
                Utils.debug(MainActivity.class, String.format("metar size = %d, airport = %s", metars.length, airport));
                switcher.setDisplayedChild(1);
                for (Metar metar : metars) {
                    if (metar.getMetarType() == Constants.METAR_TYPE_RAW) {
                        binding.setRawMetar(metar);
                    } else if (metar.getMetarType() == Constants.METAR_TYPE_DECODED) {
                        binding.setDecodedMetar(metar);
                    }
                }
            }
        };
        metarLiveData.observe(this, metarObservable);
    }

    private void getDataFromServer(String airport) {
        if (Utils.isNetworkConnected(this)) {
            try {
                String[] urls = {Constants.BASE_URL + "/stations/" + airport + ".TXT", Constants.BASE_URL + "/decoded/" + airport + ".TXT"};
                new NetworkCall(urls[0], airport, this).execute();
                new NetworkCall(urls[1], airport, this).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.err_internet_connection), Toast.LENGTH_LONG).show();
            Log.i(this.getClass().getSimpleName(), "No internet connected");
        }
    }


    private void setupAirportsAutoComplete() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, airportTags);
        autoCompleteAirports.setAdapter(adapter);
        autoCompleteAirports.addTextChangedListener(this);
        autoCompleteAirports.setValidator(this);
    }

    private void initViews() {
        autoCompleteAirports = findViewById(R.id.autocomplete_airport);
        tvRaw = findViewById(R.id.tv_metar_raw);
        tvDecoded = findViewById(R.id.tv_metar_decoded);
        btnSearch = findViewById(R.id.btn_search);
        switcher = findViewById(R.id.switcher);
        tvMessage = findViewById(R.id.tv_message);
    }

    @Override
    public void networkCallComplete(String data, String url, String airport) {
        if(!TextUtils.isEmpty(data)) {
            Metar metar = new Metar();
            if (url.contains("decoded")) {
                Utils.debug(MainActivity.class, "isDecoded");
                metar.setMetarType(Constants.METAR_TYPE_DECODED);
            } else {
                Utils.debug(MainActivity.class, "isRaw");
                metar.setMetarType(Constants.METAR_TYPE_RAW);
            }
            metar.setAirportTag(airport);
            metar.setData(data);
            metar.setLastSyncedTime(new Date());
            Utils.debug(this.getClass(), metar.toString());
            model.insert(metar);
        }
    }


    @Override
    public boolean isValid(CharSequence text) {
        Utils.debug(MainActivity.class, "Checking if valid: "+ text);
        Arrays.sort(airportTags);
        boolean isValidName =  Arrays.binarySearch(airportTags, text.toString()) > 0;
        Utils.debug(MainActivity.class, "is valid: "+ isValidName);
        return isValidName;
    }

    @Override
    public CharSequence fixText(CharSequence charSequence) {
        Utils.debug(MainActivity.class, "fix text called with : "+ charSequence.toString());
        return "";
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(TextUtils.isEmpty(editable)) {
            switcher.setDisplayedChild(0);
            tvMessage.setText(getString(R.string.mesg_airport));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(metarLiveData != null && metarLiveData.hasObservers()) {
            metarLiveData.removeObserver(metarObservable);
        }
    }
}
