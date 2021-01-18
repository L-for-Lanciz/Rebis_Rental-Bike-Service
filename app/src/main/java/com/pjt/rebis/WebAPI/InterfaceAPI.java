package com.pjt.rebis.WebAPI;

import com.pjt.rebis.ui.history.RentalItem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

    /* Interface for the API and the application. */
public interface InterfaceAPI {

    @POST("/rentals")
    Call<RentalItem> createRental(@Body RentalItem obj);

    @POST("/ending")
    Call<RentalItem> endingRental(@Body RentalItem obj);

}
