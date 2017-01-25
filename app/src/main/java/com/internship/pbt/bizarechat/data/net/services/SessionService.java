package com.internship.pbt.bizarechat.data.net.services;


import com.internship.pbt.bizarechat.data.datamodel.SessionModel;
import com.internship.pbt.bizarechat.data.datamodel.response.SignInResponseModel;
import com.internship.pbt.bizarechat.data.datamodel.response.SignUpResponseModel;
import com.internship.pbt.bizarechat.data.net.ApiConstants;
import com.internship.pbt.bizarechat.data.net.requests.SessionRequest;
import com.internship.pbt.bizarechat.data.net.requests.SessionWithAuthRequest;
import com.internship.pbt.bizarechat.data.net.requests.SignUpRequestModel;
import com.internship.pbt.bizarechat.data.net.requests.UserRequestModel;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public interface SessionService {
    @Headers({"Content-Type: application/json", "QuickBlox-REST-API-Version: 0.1.0"})
    @POST("/session.json")
    Observable<SessionModel> getSession(@Body SessionRequest body);

    @Headers({"Content-Type: application/json", "QuickBlox-REST-API-Version: 0.1.0"})
    @POST("/session.json")
    Observable<SessionModel> getSessionWithAuth(@Body SessionWithAuthRequest body);

    @Headers({"Content-Type: application/json", "QuickBlox-REST-API-Version: 0.1.0"})
    @POST("/login.json")
    Observable<SignInResponseModel> loginUser(@Header(ApiConstants.TOKEN_HEADER_NAME) String tokenHeader,
                                              @Body UserRequestModel requestModel);

    @Headers({"Content-Type: application/json", "QuickBlox-REST-API-Version: 0.1.0"})
    @POST("/user.json")
    Observable<SignUpResponseModel> signUpUser(@Header(ApiConstants.TOKEN_HEADER_NAME) String tokenHeader,
                                               @Body SignUpRequestModel signUpRequestModel);

}
