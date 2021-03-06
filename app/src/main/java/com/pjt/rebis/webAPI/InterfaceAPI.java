package com.pjt.rebis.webAPI;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

    /* Interface for the API and the application. */
public interface InterfaceAPI {

    @POST("/rentals")
    Call<Payload> createRental(@Body Payload obj);

    @POST("/ending")
    Call<Payload> endingRental(@Body Payload obj);

}
