package com.example.chatapp.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAwTDwx9M:APA91bF0P5KDFWVfN1jENzu8vPogPHVA0gleeq2BHzjFdizEnDw39Rkl4pcR7JzC9r34KVfQIapiKfDd2zfRC8f4iDQLJ2SK_l9NmFeMkTopkWwGhpZiEa2BfO8lJ5WrNj-BPD3PiWxj"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
