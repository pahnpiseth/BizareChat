package com.internship.pbt.bizarechat.presentation.view.fragment.publicChat;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.internship.pbt.bizarechat.R;

public class PublicChatFragment extends Fragment implements PublicChatView {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats_list, container, false);
    }

    public static PublicChatFragment newInstance() {
        PublicChatFragment fragment = new PublicChatFragment();
        return fragment;
    }


}
