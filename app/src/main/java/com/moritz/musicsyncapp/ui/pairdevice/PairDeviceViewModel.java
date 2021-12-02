package com.moritz.musicsyncapp.ui.pairdevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.model.device.IDevice;

import java.util.List;

public class PairDeviceViewModel extends ViewModel {

        private LiveData<List<IDevice>> avalibleDevices = new LiveData<List<IDevice>>() {



                @Override
                protected void postValue(List<IDevice> value) {
                        super.postValue(value);
                }
        };

        public LiveData<List<IDevice>> getDevices ()
        {

                return avalibleDevices;
        }

}
