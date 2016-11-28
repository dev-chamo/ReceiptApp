package com.tobcross.gymmanagerreceipt.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.tobcross.gymmanagerreceipt.Constans;
import com.tobcross.gymmanagerreceipt.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.register_center_name_et)
    EditText mCenterNameEt;

    @BindView(R.id.register_phone_digit_middle_et)
    EditText mPhoneDigitMiddleNumberEt;

    @BindView(R.id.register_phone_digit_last_et)
    EditText mPhoneDigitLastNumberEt;

    @BindView(R.id.register_phone_digit_first_sp)
    Spinner mPhoneDigistFirstNumberSp;

    @BindView(R.id.register_ok_btn)
    BootstrapButton mOkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initToolBarAsHome(getResources().getString(R.string.app_name), true);

        String centerName = getPreference().getString(Constans.CENTER_NAME, "");
        String centerPhoneNumber = getPreference().getString(Constans.CENTER_PHONE_NUMBER, "");

        if(!TextUtils.isEmpty(centerName)){
            mCenterNameEt.setText(centerName);
            mOkBtn.setText(R.string.modify);
        }

        if(!TextUtils.isEmpty(centerPhoneNumber)){
            String[] firstDigitArray = getResources().getStringArray(R.array.phone_first_digit_array);
            mPhoneDigistFirstNumberSp.setSelection(Arrays.asList(firstDigitArray).indexOf(centerPhoneNumber.split("-")[0]));
            mPhoneDigitMiddleNumberEt.setText(centerPhoneNumber.split("-")[1]);
            mPhoneDigitLastNumberEt.setText(centerPhoneNumber.split("-")[2]);
        }

        SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.phone_first_digit_array));
        mPhoneDigistFirstNumberSp.setAdapter(adapter);

        if (getDisplayDensity() > 240){
            findViewById(R.id.register_phone_tv).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            exitActivity();
        }

        return true;
    }

    @Override
    public void exitActivity() {
        setResult(RESULT_OK);
        super.exitActivity();
    }

    @OnClick(R.id.register_ok_btn)
    void onClickOkButton() {
        hideSoftKeyboard();
        final String centerName = mCenterNameEt.getText().toString();
        final String firstNumber = mPhoneDigistFirstNumberSp.getSelectedItem().toString();
        final String middleNumber = mPhoneDigitMiddleNumberEt.getText().toString();
        final String lastNumber = mPhoneDigitLastNumberEt.getText().toString();

        if (TextUtils.isEmpty(centerName)) {
            showToast(R.string.wrong_center_name);
            return;
        }

        if (TextUtils.isEmpty(middleNumber)
                || middleNumber.length() < 3
                || TextUtils.isEmpty(lastNumber)
                || lastNumber.length() < 4) {
            showToast(R.string.wrong_phone_number);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        AlertDialog mAlertDialog = builder.setMessage(getResources().getString(R.string.save_center_register_info))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String phoneNumber = String.format("%s-%s-%s", firstNumber, middleNumber, lastNumber);

                        SharedPreferences.Editor editor = getPreference().edit();
                        editor.putString(Constans.CENTER_NAME, centerName);
                        editor.putString(Constans.CENTER_PHONE_NUMBER, phoneNumber);
                        editor.putBoolean(Constans.HAS_REGISTERED, true);
                        editor.apply();

                        showToast(R.string.center_register_saved);

                        dialogInterface.dismiss();
                        exitActivity();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        mAlertDialog.show();
    }

    @OnClick(R.id.register_cancel_btn)
    void onClickCancelButton() {
        hideSoftKeyboard();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        AlertDialog mAlertDialog = builder.setMessage(getResources().getString(R.string.center_register_cancel))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        exitActivity();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        mAlertDialog.show();
    }
}
