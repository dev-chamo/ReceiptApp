package com.tobcross.gymmanagerreceipt.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tobcross.gymmanagerreceipt.Constans;
import com.tobcross.gymmanagerreceipt.R;
import com.tobcross.gymmanagerreceipt.model.ResultData;
import com.tobcross.gymmanagerreceipt.retrofit.ApiCallback;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.main_receipt_iv)
    ImageView mReceiptIv;

    @BindView(R.id.main_receipt_tv)
    TextView mReceiptTv;

    @BindView(R.id.main_phone_digit_middle_et)
    EditText mPhoneDigitMiddleNumberEt;

    @BindView(R.id.main_phone_digit_last_et)
    EditText mPhoneDigitLastNumberEt;

    @BindView(R.id.main_phone_digit_first_sp)
    Spinner mPhoneDigistFirstNumberSp;

    private boolean mIsSingleLaunch = false;
    private Uri mReceiptImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBarAsHome(getResources().getString(R.string.app_name), false);

        checkPermission();

        // 센터 등록 체크
        if (!getPreference().getBoolean(Constans.HAS_REGISTERED, false)) {
            mIsSingleLaunch = true;
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            intent.putExtra(Constans.IS_SINGLE_LAUNCH, true);
            startActivityForResult(intent, Constans.REQUEST_REGISTER);
        }

        // 이미지 수신 (MMS)
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if (intent.getType().startsWith("image/")) {
                handleSendImage(intent);
            }
        } else {
            showSingleLaunchWarningDialog();
        }

        SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.phone_first_digit_array));
        mPhoneDigistFirstNumberSp.setAdapter(adapter);

        if (getDisplayDensity() > 320) {
            findViewById(R.id.main_phone_tv).setVisibility(View.GONE);
        }

    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Log.d(TAG, imageUri.toString());
        if (imageUri != null) {
            mReceiptTv.setVisibility(View.GONE);
            Glide.with(this)
                    .load(imageUri)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mReceiptIv);
            mReceiptImageUri = imageUri;
        }
    }

    private void showSingleLaunchWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);

        AlertDialog dialog = builder.setMessage(getResources().getString(R.string.single_launch_warning))
                .setTitle(R.string.warning)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        exitActivity();
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }

    @OnClick(R.id.main_cancel_btn)
    void showReceiptIssueCancelDialog() {
        hideSoftKeyboard();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);

        final AlertDialog mAlertDialog = builder.setMessage(getResources().getString(R.string.cancel_receipt_send))
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        exitActivity();
                    }
                })
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();

        mAlertDialog.show();

    }

    @OnClick(R.id.main_ok_btn)
    void showReceiptIssueOkDialog() {
        hideSoftKeyboard();

        final String firstNumber = mPhoneDigistFirstNumberSp.getSelectedItem().toString();
        final String middleNumber = mPhoneDigitMiddleNumberEt.getText().toString();
        final String lastNumber = mPhoneDigitLastNumberEt.getText().toString();

        if (TextUtils.isEmpty(middleNumber)
                || middleNumber.length() < 3
                || TextUtils.isEmpty(lastNumber)
                || lastNumber.length() < 4) {
            showToast(R.string.wrong_phone_number);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);

        AlertDialog mAlertDialog = builder.setMessage(getResources().getString(R.string.ok_receipt_send))
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String phoneNumber = String.format("%s%s%s", firstNumber, middleNumber, lastNumber);
                        postReceiptContent(phoneNumber);

                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        mAlertDialog.show();
    }

    /*
    private void postReceiptContent(String receiverPhoneNumber) {
        String centerName = getPreference().getString(Constans.CENTER_NAME, "");
        String centerPhoneNumber = getPreference().getString(Constans.CENTER_PHONE_NUMBER, "");

        File file = new File(mReceiptImageUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        RequestBody $centerName = RequestBody.create(MediaType.parse("multipart/form-data"), centerName);
        RequestBody $centerPhoneNumber = RequestBody.create(MediaType.parse("multipart/form-data"), centerPhoneNumber);
        RequestBody $receiverPhoneNumber = RequestBody.create(MediaType.parse("multipart/form-data"), receiverPhoneNumber);

        showProgressDialog();
        addSubscription(mApiStore.postReceiptContent($centerName, $centerPhoneNumber, $receiverPhoneNumber, body),
                new ApiCallback<ResultData>() {
                    @Override
                    public void onSuccess(ResultData resultData) {
                        Log.d(TAG, "postReceiptContent onSuccess");
                        if (resultData.getResult().isRequest()) {
                            showAlertDialog(getResources().getString(R.string.receipt_send_success), true);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.d(TAG, String.format("postReceiptContent onFailure %s", msg));
                        showToast(String.format("%s (%s)", getResources().getString(R.string.error_occured), msg));
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "postReceiptContent onFinish");
                        dismissProgressDialog();
                    }
                });
    }
    */

    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if (src == null)
            return null;

        int width = src.getWidth();
        int height = src.getHeight();

        if (width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if (width > w)
            x = (width - w) / 2;

        if (height > h)
            y = (height - h) / 2;

        int cw = w; // crop width
        int ch = h; // crop height

        if (w > width)
            cw = width;

        if (h > height)
            ch = height;

        return Bitmap.createBitmap(src, x, y, cw, ch);
    }

    private void postReceiptContent(String receiverPhoneNumber) {
        String centerName = getPreference().getString(Constans.CENTER_NAME, "");
        String centerPhoneNumber = getPreference().getString(Constans.CENTER_PHONE_NUMBER, "");

        Bitmap bitmap = BitmapFactory.decodeFile(mReceiptImageUri.getPath());

        int width = 510;
        int height = 808;

        Bitmap resized;
        if (height > width) {
            resized = Bitmap.createScaledBitmap(bitmap, width, height, true);
        } else {
            resized = Bitmap.createScaledBitmap(bitmap, height, width, true);
        }
        bitmap = null;

        Bitmap _bitmap = Bitmap.createBitmap(resized,
                0,
                40,
                resized.getWidth(),
                (int) (resized.getHeight() * 0.75));
        resized = null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        _bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        String receiptContent = Base64.encodeToString(data, Base64.DEFAULT);

        Map<String, String> params = new HashMap<>();
        params.put("centerName", centerName);
        params.put("senderPhone", centerPhoneNumber);
        params.put("receiverPhone", receiverPhoneNumber);
        params.put("photo", receiptContent);

        showProgressDialog();
        addSubscription(mApiStore.postReceiptContent(params),
                new ApiCallback<ResultData>() {
                    @Override
                    public void onSuccess(ResultData resultData) {
                        Log.d(TAG, "postReceiptContent onSuccess");
                        showAlertDialog(getResources().getString(R.string.receipt_send_success), true);
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.d(TAG, String.format("postReceiptContent onFailure %s", msg));
                        showToast(String.format("%s (%s)", getResources().getString(R.string.error_occured), msg));
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "postReceiptContent onFinish");
                        dismissProgressDialog();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_modify_center_register) {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivityForResult(intent, Constans.REQUEST_REGISTER);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constans.REQUEST_REGISTER) {
                if (mIsSingleLaunch) {
                    showToast(R.string.finish_app);
                    exitActivity();
                }
            }
        }
    }
}
