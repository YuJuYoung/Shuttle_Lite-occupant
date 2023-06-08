package com.shuttlelite.occupant;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

import java.nio.charset.StandardCharsets;

public class MyHostApduService extends HostApduService {

    public static final String MY_AID = BuildConfig.MY_AID;

    private MyAppInfo myAppInfo = MyAppInfo.getInstance();

    @Override
    public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
        Intent newIntent = new Intent(MyAction.DATA_SEND_COMPLETED);
        sendBroadcast(newIntent);

        String occupantNumber = myAppInfo.getOccupantNumber();
        return occupantNumber.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void onDeactivated(int i) {

    }
}
