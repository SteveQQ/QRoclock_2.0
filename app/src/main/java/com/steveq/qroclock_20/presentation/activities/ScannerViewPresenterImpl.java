package com.steveq.qroclock_20.presentation.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Adam on 2017-07-14.
 */

public class ScannerViewPresenterImpl implements ScannerViewPresenter {
    private static final String TAG = ScannerViewPresenterImpl.class.getSimpleName();
    public static final int CAMERA_PERMISSION_REQUEST = 1234;
    private static ScannerViewPresenterImpl instance;
    private ScannerView scannerView;

    private ScannerViewPresenterImpl(ScannerView scannerView){
        this.scannerView = scannerView;
    }

    public static ScannerViewPresenterImpl getInstance(ScannerView scannerView){
        if(instance == null){
            instance = new ScannerViewPresenterImpl(scannerView);
        }
        return instance;
    }

    public static ScannerViewPresenterImpl getInstance(){
        if(instance == null){
            throw new IllegalStateException("Presenter need to be first instantiated with proper context, use getInstance( ScannerView );");
        }
        return instance;
    }

    @Override
    public void initView() {
        scannerView.configBarcodeView();
    }

    @Override
    public void controlPermissionRequest() {
        if(ContextCompat.checkSelfPermission((Context)scannerView, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Permission not granted");
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)scannerView, Manifest.permission.CAMERA)){
                Log.d(TAG, "Explanation should be prompted");
            } else {
                ActivityCompat.requestPermissions((Activity)scannerView,
                                                    new String[]{Manifest.permission.CAMERA},
                                                    CAMERA_PERMISSION_REQUEST);
            }
        } else {
            initView();
        }
    }
}
