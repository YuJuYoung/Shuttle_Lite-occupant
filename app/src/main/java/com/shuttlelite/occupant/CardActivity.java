package com.shuttlelite.occupant;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class CardActivity extends AppCompatActivity {

    private class MyBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG_COMPLETE = "태그 완료";

        @Override
        public void onReceive(Context context, Intent intent) {
            notice.setText(TAG_COMPLETE);
            image.setImageResource(R.drawable.ic_baseline_done_24);
            image.setContentDescription(TAG_COMPLETE);
        }
    }

    // private MyAppInfo myAppInfo = MyAppInfo.getInstance();

    private NfcAdapter nfcAdapter;
    private CardEmulation cardEmulation;
    private MyBroadcastReceiver receiver;

    private ComponentName componentName;
    private List<String> AIDList;

    private TextView notice;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (!nfcAdapter.isEnabled()) {
            showNFCAlertDialog();
        }
        cardEmulation = CardEmulation.getInstance(nfcAdapter);
        receiver = new MyBroadcastReceiver();

        componentName = new ComponentName(this, MyHostApduService.class);
        AIDList = Arrays.asList(MyHostApduService.MY_AID);

        notice = findViewById(R.id.notice);
        image = findViewById(R.id.image);
    }

    @Override
    public void onResume() {
        super.onResume();
        cardEmulation.registerAidsForService(componentName, BuildConfig.MY_AID_CATEGORY, AIDList);

        IntentFilter filter = new IntentFilter();
        filter.addAction(MyAction.DATA_SEND_COMPLETED);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        cardEmulation.removeAidsForService(componentName, BuildConfig.MY_AID_CATEGORY);
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }

    private void showNFCAlertDialog() {
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(this);

        String title = "NFC가 비활성화 되어있음";
        String message = "NFC를 활성화 시켜야합니다.";
        String positiveBtnMsg = "예";

        DialogBuilder
                .setTitle(title).setMessage(message)
                .setPositiveButton(positiveBtnMsg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                        } else {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    }
                });
        AlertDialog dialog = DialogBuilder.create();
        dialog.show();
    }

}
