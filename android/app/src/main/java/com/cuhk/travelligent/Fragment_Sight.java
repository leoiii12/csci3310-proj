package com.cuhk.travelligent;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.swagger.client.apis.CommentApi;
import io.swagger.client.apis.RatingApi;
import io.swagger.client.apis.SightApi;
import io.swagger.client.models.CreateCommentInput;
import io.swagger.client.models.CreateCommentOutput;
import io.swagger.client.models.CreateRatingInput;
import io.swagger.client.models.CreateRatingOutput;
import io.swagger.client.models.GetSightInput;
import io.swagger.client.models.GetSightOutput;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Sight extends Fragment {

    SightApi sightApi = new SightApi();

    public Fragment_Sight() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Configs.PREFS, MODE_PRIVATE);

        String accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, "");
        if ("".equals(accessToken)) {
            throw new UnsupportedOperationException();
        }

        GetSightInput getSightInput = new GetSightInput(1);
        GetSightOutput getSightOutput = sightApi.apiSightGet(getSightInput, "Bearer " + accessToken);

        System.out.println(getSightOutput);

        CommentApi commentApi = new CommentApi();
        CreateCommentInput createCommentInput = new CreateCommentInput("Hello World", null, 1, null);
        CreateCommentOutput createCommentOutput = commentApi.apiCommentCreate(createCommentInput, "Bearer " + accessToken);

        System.out.println(createCommentOutput);

        RatingApi ratingApi = new RatingApi();
        CreateRatingInput createRatingInput = new CreateRatingInput(4.9, null, 1, null);
        CreateRatingOutput createRatingOutput = ratingApi.apiRatingCreate(createRatingInput, "Bearer " + accessToken);

        System.out.println(createRatingOutput);

        getSightInput = new GetSightInput(1);
        getSightOutput = sightApi.apiSightGet(getSightInput, "Bearer " + accessToken);

        System.out.println(getSightOutput);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment__sight_details, container, false);
    }

}
