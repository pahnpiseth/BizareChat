package com.internship.pbt.bizarechat.data.repository;

import com.internship.pbt.bizarechat.data.datamodel.mappers.SessionModelMapper;
import com.internship.pbt.bizarechat.data.net.ApiConstants;
import com.internship.pbt.bizarechat.data.net.RetrofitApi;
import com.internship.pbt.bizarechat.data.net.requests.SessionRequest;
import com.internship.pbt.bizarechat.data.net.services.SessionService;
import com.internship.pbt.bizarechat.data.util.HmacSha1Signature;
import com.internship.pbt.bizarechat.domain.model.Session;
import com.internship.pbt.bizarechat.domain.repository.SessionRepository;

import java.util.Random;

import rx.Observable;



public class SessionDataRepository implements SessionRepository {
    private SessionService sessionService;
    private Random randomizer;

    public SessionDataRepository() {
        sessionService = RetrofitApi.getRetrofitApi().getSessionService();
        randomizer = new Random(27);
    }

    @Override public Observable<Session> getSession() {
        int nonce = randomizer.nextInt();
        long timestamp = System.currentTimeMillis();
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
}