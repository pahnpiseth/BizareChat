package com.internship.pbt.bizarechat;


import com.internship.pbt.bizarechat.presentation.model.InformationOnCheck;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RegistrationPresenterUnitTest {
    private String[] positiveTestPasswordLengthData = {"11111", "1111111111111", ""};
    private String[] negativeTestPasswordLengthData = {"111111", "1111111", "11111111111", "111111111111"};



    @Mock
    private RegistrationView mRegistrationFragment;

    private RegistrationPresenterImpl mRegistrationPresenter;

    private InformationOnCheck mInformationOnCheck;
    @Before
    public void prepareData() {
        mInformationOnCheck = new InformationOnCheck();
        mInformationOnCheck.setEmail("Example@gmail.com");
        mInformationOnCheck.setPassword("QA1we2");
        mInformationOnCheck.setPhone("0797878796");
        mRegistrationPresenter = new RegistrationPresenterImpl();
        mRegistrationPresenter.setRegistrationView(mRegistrationFragment);
    }

    @Test
    public void checkPasswordLengthNotMatches(){
        when(mInformationOnCheck.getEmail()).thenReturn("email");
        when(mInformationOnCheck.getPhone()).thenReturn("phone");

        for(String value : positiveTestPasswordLengthData){
            when(mInformationOnCheck.getPassword()).thenReturn(value);
            mRegistrationPresenter.validateInformation(mInformationOnCheck);
            verify(mRegistrationFragment).showErrorPasswordLength();
        }
    }

    @Test
    public void checkPasswordLengthMatches(){
        when(mInformationOnCheck.getEmail()).thenReturn("email");
        when(mInformationOnCheck.getPhone()).thenReturn("phone");

        for(String value : negativeTestPasswordLengthData){
            when(mInformationOnCheck.getPassword()).thenReturn(value);
            mRegistrationPresenter.validateInformation(mInformationOnCheck);
            verify(mRegistrationFragment, calls(0)).showErrorPasswordLength();
        }
    }

    @Test
    public void checkIfUserEnteredInvalidEmail() {
        mInformationOnCheck.setEmail("Invalid");
        mRegistrationPresenter.validateInformation(mInformationOnCheck);

        verify(mRegistrationFragment).showErrorInvalidEmail();
        verify(mRegistrationFragment, never()).showErrorInvalidPassword();
        verify(mRegistrationFragment, never()).showErrorInvalidPhone();
    }

    @Test
    public void checkIfUserEnteredInvalidPassword(){
        mInformationOnCheck.setPassword("Invalid");
        mRegistrationPresenter.validateInformation(mInformationOnCheck);

        verify(mRegistrationFragment).showErrorInvalidPassword();
        verify(mRegistrationFragment, never()).showErrorInvalidEmail();
        verify(mRegistrationFragment, never()).showErrorInvalidPhone();
    }

    @Test
    public void checkIfUserEnteredInvalidPhone(){
        mInformationOnCheck.setPhone("Invalid");
        mRegistrationPresenter.validateInformation(mInformationOnCheck);

        verify(mRegistrationFragment).showErrorInvalidPhone();
        verify(mRegistrationFragment, never()).showErrorInvalidPassword();
        verify(mRegistrationFragment, never()).showErrorInvalidEmail();
    }

    @Test
    public void passwordMatch(){}


}