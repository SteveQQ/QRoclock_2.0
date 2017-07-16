package com.steveq.qroclock_20.presentation.activities;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.scanner.ScannerCallback;

public class ScannerActivity extends AppCompatActivity implements ScannerView{
    private static final String TAG = ScannerActivity.class.getSimpleName();
    private DecoratedBarcodeView scannerBarcodeView;
    private ToggleButton configureToggleButton;
    private ScannerViewPresenter presenter;
    private LinearLayout rootView;
    private State state;
    public enum State {
        CONFIGURE,
        WAKE
    }

    public State getState() {
        return state;
    }

    private final CompoundButton.OnCheckedChangeListener toggleCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                configBarcodeView();
                state = State.CONFIGURE;
                scannerBarcodeView.setStatusText(getString(R.string.scan_to_configure));
            } else {
                configBarcodeView();
                state = State.WAKE;
                scannerBarcodeView.setStatusText(getString(R.string.scan_to_wake));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scannerBarcodeView = (DecoratedBarcodeView) findViewById(R.id.scannerBarcodeView);
        configureToggleButton = (ToggleButton) findViewById(R.id.configureToggleButton);
        presenter = ScannerViewPresenterImpl.getInstance(this);
        presenter.controlPermissionRequest();
        configureToggleButton.setOnCheckedChangeListener(toggleCheckListener);
        state = State.WAKE;
        rootView = (LinearLayout) findViewById(R.id.rootLinearLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerBarcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerBarcodeView.pause();
    }

    @Override
    public void configBarcodeView() {
        scannerBarcodeView.decodeSingle(new ScannerCallback(this));
        state = State.WAKE;
        scannerBarcodeView.setStatusText(getString(R.string.scan_to_wake));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ScannerViewPresenterImpl.CAMERA_PERMISSION_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                presenter.initView();
            }
        }
    }

    @Override
    public void showQrConfiguredsnackbar() {
        Snackbar snackbar = Snackbar.make(rootView, getResources().getString(R.string.qr_set), BaseTransientBottomBar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void showAlarmNotRingingSnackbar() {
        Snackbar snackbar = Snackbar.make(rootView, "Alarm Not Ringing", BaseTransientBottomBar.LENGTH_LONG);
        snackbar.show();
    }
}
