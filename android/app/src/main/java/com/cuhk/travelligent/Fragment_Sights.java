package com.cuhk.travelligent;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.swagger.client.apis.SightApi;
import io.swagger.client.models.CreateSightInput;
import io.swagger.client.models.CreateSightOutput;
import io.swagger.client.models.ListSightsOutput;
import io.swagger.client.models.SightListDto;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Sights extends Fragment {

    SightApi sightApi = new SightApi();

    public Fragment_Sights() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Configs.PREFS, MODE_PRIVATE);

        final String accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, "");
        if ("".equals(accessToken)) {
            throw new UnsupportedOperationException();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ListSightsOutput listSightsOutput = sightApi.apiSightList("Bearer " + accessToken);
                    SightListDto[] sights = listSightsOutput.getSights();

                    if (sights.length == 0) {
                        CreateSightInput createSightInput = new CreateSightInput("天一合一", 22.4215355, 114.2076754);
                        CreateSightOutput createSightOutput = sightApi.apiSightCreate(createSightInput, "Bearer " + accessToken);

                        System.out.println(createSightOutput);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment__sights, container, false);
    }

}
