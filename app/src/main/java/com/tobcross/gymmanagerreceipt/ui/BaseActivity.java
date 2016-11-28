package com.tobcross.gymmanagerreceipt.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tobcross.gymmanagerreceipt.Constans;
import com.tobcross.gymmanagerreceipt.R;
import com.tobcross.gymmanagerreceipt.retrofit.ApiStores;
import com.tobcross.gymmanagerreceipt.retrofit.AppClient;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Koo on 2016. 11. 24..
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public final int PERMISSIONS_REQUEST_STORAGE = 1;

    public ApiStores mApiStore = AppClient.retrofit().create(ApiStores.class);

    private CompositeSubscription mCompositeSubscription;

    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        mActivity = this;
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
        mActivity = this;
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
        mActivity = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        onUnsubscribe();
        super.onDestroy();
    }

    public void addSubscription(Observable observable, Subscriber subscriber) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    public void onUnsubscribe() {
        Log.d(TAG, "onUnsubscribe");
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    public Toolbar initToolBarAsHome(String title, boolean shouldDisplayGoToHomeButton) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbarTitle.setText(title);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(shouldDisplayGoToHomeButton);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        return toolbar;
    }

    public void exitActivity(){
        hideSoftKeyboard();
        finish();
    }

    public SharedPreferences getPreference(){
        return getSharedPreferences(Constans.PREFERENCE_DEFAULT_NAME, MODE_PRIVATE);
    }

    public void showToast(int resId) {
        Toast.makeText(mActivity, resId, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    public AlertDialog showAlertDialog(CharSequence message, final boolean shouldFinish){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        mAlertDialog = builder.setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (shouldFinish){
                            exitActivity();
                        }
                    }
                })
                .setCancelable(false)
                .create();
        mAlertDialog.show();

        return mAlertDialog;
    }

    public ProgressDialog showProgressDialog() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
        return mProgressDialog;
    }

    public ProgressDialog showProgressDialog(CharSequence message) {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_STORAGE);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // pass
                } else {
                    Toast.makeText(getApplicationContext(), "권한 설정을 취소하였습니다. 앱 사용에 문제가 있을 수 있습니다.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public int getDisplayDensity(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }
}
