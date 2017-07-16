package com.steveq.qroclock_20.scanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.steveq.qroclock_20.QRoclockApplication;
import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.presentation.activities.MainActivityPresenterImpl;
import com.steveq.qroclock_20.presentation.activities.ScannerActivity;
import com.steveq.qroclock_20.service.StartAlarmService;

import java.util.List;

/**
 * Created by Adam on 2017-07-14.
 */

public class ScannerCallback implements BarcodeCallback{
    private static final String TAG = ScannerCallback.class.getSimpleName();

    private Activity activity;

    public ScannerCallback(Activity activity){
        this.activity = activity;
    }

    @Override
    public void barcodeResult(BarcodeResult result) {
        if(((ScannerActivity)this.activity).getState() == ScannerActivity.State.CONFIGURE){
            SharedPreferences.Editor editor = MainActivityPresenterImpl.editor;
            Log.d(TAG, "configured qr : " + result.getText());
            editor.putString((this.activity).getString(R.string.QR), result.getText());
            editor.commit();
            ((ScannerActivity)this.activity).configBarcodeView();
        } else if (((ScannerActivity)this.activity).getState() == ScannerActivity.State.WAKE){
            Log.d(TAG, "wake qr : " + result.getText());
            if(MainActivityPresenterImpl.sharedPreferences.getString((this.activity).getString(R.string.QR), "").equals(result.getText()) &&
                    StartAlarmService.isWaking){
                Intent intent = new Intent();
                intent.setAction((this.activity).getString(R.string.stop_alarm_action));
                (this.activity).sendBroadcast(intent);
                (this.activity).finish();
            } else {
                ((ScannerActivity)this.activity).showAlarmNotRingingSnackbar();
            }

        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {

    }
}
