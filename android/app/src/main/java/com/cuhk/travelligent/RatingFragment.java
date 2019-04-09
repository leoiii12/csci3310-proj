package com.cuhk.travelligent;


import android.content.Context;
import android.media.Rating;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import java.io.Serializable;
import java.util.List;

import io.swagger.client.models.CommentDto;
import io.swagger.client.models.RatingDto;


class RatingFragmentSerializableArg implements Serializable {

    public List<RatingDto> ratings;

    public RatingFragmentSerializableArg(List<RatingDto> comments) {
        this.ratings = comments;
    }

}

/**
 * A simple {@link Fragment} subclass.
 */
public class RatingFragment extends Fragment {

    private static final String ARG_ARG = "arg";

    private List<RatingDto> ratings;

    public RatingFragment() {
        // Required empty public constructor
    }

    public static RatingFragment newInstance(List<RatingDto> ratings) {
        RatingFragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ARG, new RatingFragmentSerializableArg(ratings));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ratings = ((RatingFragmentSerializableArg) getArguments().getSerializable(ARG_ARG)).ratings;

            System.out.println(ratings);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);

        RatingBar ratingBar = view.findViewById(R.id.rating_bar);

        double count = 0;
        double sum = 0;
        for (RatingDto rating : ratings) {
            count += 1;
            sum += rating.getValue();
        }

        float rating = (float) (sum / count);

        ratingBar.setRating(rating);

        return view;
    }

}
