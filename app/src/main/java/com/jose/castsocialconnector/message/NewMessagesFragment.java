package com.jose.castsocialconnector.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.main.BaseFragment;
import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.main.MenuFragment;
import com.jose.castsocialconnector.message.receive.Email;

import java.util.ArrayList;

public class NewMessagesFragment extends BaseFragment {

    private final String TAG = "NewMessagesFragment";
    private ArrayList<Email> emails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emails = ((MainActivity) getActivity()).getGetEmailService().getEmails();
        sendNewMessages();
    }

    @Override
    protected void onBackPressed() {
        changeFragment(new MenuFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_messages, container, false);
        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
        view.findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prev();
            }
        });
        return view;
    }

    public void sendNewMessages() {
        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {

                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String json = gson.toJson(emails);

                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.new_messages), json).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
        }
    }

    public void next() {
        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {
                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.next_message_namespace), ".").setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
        }
    }

    public void prev() {
        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {
                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.prev_message_namespace), ".").setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
        }
    }
}
