package com.tobcross.gymmanagerreceipt.retrofit;

import com.tobcross.gymmanagerreceipt.BuildConfig;
import com.tobcross.gymmanagerreceipt.model.ResultData;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by Koo on 2016. 11. 24..
 */

public interface ApiStores {
    String BASE_URL = BuildConfig.BASE_URL;

    @Multipart
    @POST("api/0/0/temporary/sendsms")
    Observable<ResultData> postReceiptContent(
            @Part("centerName") RequestBody centerName,
            @Part("senderPhone") RequestBody senderPhoneNumber,
            @Part("receiverPhone") RequestBody receiverPhoneNumber,
            @Part MultipartBody.Part receiptImage
    );

    // Text 방식
    @FormUrlEncoded
    @POST("api//0/0/temporary/sendsms")
    Observable<ResultData> postReceiptContent(
            @FieldMap Map<String, String> params
    );

}
