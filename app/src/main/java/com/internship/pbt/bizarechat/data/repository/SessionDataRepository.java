package com.internship.pbt.bizarechat.data.repository;

import android.util.Log;

import com.internship.pbt.bizarechat.data.datamodel.mappers.SessionModelMapper;
import com.internship.pbt.bizarechat.data.net.ApiConstants;
import com.internship.pbt.bizarechat.data.net.requests.SessionRequest;
import com.internship.pbt.bizarechat.data.net.requests.SessionWithAuthRequest;
import com.internship.pbt.bizarechat.data.net.requests.UserRequestModel;
import com.internship.pbt.bizarechat.data.net.requests.signup.SignUpRequestM;
import com.internship.pbt.bizarechat.data.net.services.SessionService;
import com.internship.pbt.bizarechat.data.util.HmacSha1Signature;
import com.internship.pbt.bizarechat.domain.model.Session;
import com.internship.pbt.bizarechat.domain.model.UserLoginResponse;
import com.internship.pbt.bizarechat.domain.model.signup.ResponseSignUpModel;
import com.internship.pbt.bizarechat.domain.repository.SessionRepository;

import java.util.Random;

import retrofit2.Response;
import rx.Observable;


public class SessionDataRepository implements SessionRepository {
    private static final int NUMERIC = 27;
    private static final int NIL = 0;
    private static final long MILSECONDS = 1000;
    private SessionService sessionService;
    private Random randomizer;

    public SessionDataRepository(SessionService sessionService) {
        this.sessionService = sessionService;
        randomizer = new Random(NUMERIC);
    }

    @Override
    public Observable<Session> getSession() {
        int nonce = randomizer.nextInt();
        if (nonce < NIL) {
            nonce = -nonce;
        }
        long timestamp = System.currentTimeMillis() / MILSECONDS;
        String signature = HmacSha1Signature.calculateSignature(nonce, timestamp);

        SessionRequest request = new SessionRequest(
                ApiConstants.APP_ID,
                ApiConstants.AUTH_KEY,
                String.valueOf(timestamp),
                String.valueOf(nonce),
                signature
        );

        return sessionService.getSession(request).map(SessionModelMapper::transform);
    }


    @Override
    public Observable<Session> getSessionWithAuth(UserRequestModel requestModel) {
        Log.d("321", "get SessionWithAuth()");
        int nonce = randomizer.nextInt();
        if (nonce < NIL) {
            nonce = -nonce;
        }
        long timestamp = System.currentTimeMillis() / MILSECONDS;
        String signature = HmacSha1Signature.calculateSignatureWithAuth(
                requestModel.getEmail(), requestModel.getPassword(), nonce, timestamp);

        SessionWithAuthRequest request = new SessionWithAuthRequest(
                ApiConstants.APP_ID,
                ApiConstants.AUTH_KEY,
                String.valueOf(timestamp),
                String.valueOf(nonce),
                signature,
                requestModel
        );

        Log.d("321", "getSessionWithAuth " + request.toString());
        return sessionService.getSessionWithAuth(request).map(SessionModelMapper::transform);
    }

    @Override
    public Observable<UserLoginResponse> loginUser(UserRequestModel requestModel) {
        Log.d("321", "loginUser() requestModel " + requestModel.toString() + " TOKEN " + UserToken.getInstance().getToken());
        return sessionService.loginUser(UserToken.getInstance().getToken(), requestModel)
                .map(SessionModelMapper::transform);
    }

    @Override
    public Observable<ResponseSignUpModel> signUpUser(SignUpRequestM requestModel) {
        return sessionService.signUpUser(UserToken.getInstance().getToken(), requestModel);

    }

    @Override
    public Observable<Response<Void>> signOutUser() {
        return sessionService.signOut(UserToken.getInstance().getToken());
    }


}
