package com.shuttlelite.occupant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private MyAppInfo myAppInfo = MyAppInfo.getInstance();

    private TextView occupantNumText, checkNumFailedText;
    boolean isCheckBtnEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        occupantNumText = findViewById(R.id.occupant_number_text);
        checkNumFailedText = findViewById(R.id.check_number_failed_text);
        isCheckBtnEnable = true;

        findViewById(R.id.check_number_btn).setOnClickListener(view -> {
            if (!isCheckBtnEnable) {
                return;
            }
            isCheckBtnEnable = false;

            String occupantNumber = occupantNumText.getText().toString();

            if (occupantNumber.equals("")) {
                return;
            }
            String result = checkOccupantNumber(occupantNumber);

            if (result.equals(ResultMessage.SUCCESS)) {
                myAppInfo.setOccupantNumber();

                Intent newIntent = new Intent(this, CardActivity.class);
                startActivity(newIntent);
            } else {
                checkNumFailedText.setText(result);

            }
            isCheckBtnEnable = true;
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }

    private String checkOccupantNumber(String occupantNumber) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("occupantNumber", occupantNumber);

            MyHttpURLConnection myHttpURLConnection
                    = new MyHttpURLConnection(MyURL.CHECK_OCCUPANT_NUMBER, requestBody);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    myHttpURLConnection.request();
                }
            });

            thread.start();
            thread.join(8000);

            JSONObject responseBody = myHttpURLConnection.getResponseBody();

            if (responseBody != null) {
                boolean result = responseBody.getBoolean("result");

                if (result) {
                    if (myAppInfo.writeOccupantNumber(occupantNumber)) {
                        return ResultMessage.SUCCESS;
                    }
                } else {
                    return ResultMessage.WRONG_OCCUPANT_NUMBER;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResultMessage.ERROR;
    }
}
