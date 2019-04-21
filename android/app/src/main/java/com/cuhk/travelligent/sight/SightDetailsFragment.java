package com.cuhk.travelligent.sight;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cuhk.travelligent.CommentFragment;
import com.cuhk.travelligent.Configs;
import com.cuhk.travelligent.R;
import com.cuhk.travelligent.RatingFragment;

import org.w3c.dom.Comment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import io.swagger.client.apis.SightApi;
import io.swagger.client.models.CommentDto;
import io.swagger.client.models.GetSightInput;
import io.swagger.client.models.GetSightOutput;
import io.swagger.client.models.ImageDto;
import io.swagger.client.models.ListSightsOutput;
import io.swagger.client.models.RatingDto;
import io.swagger.client.models.SightDto;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SightDetailsFragment extends Fragment {

    public static String SightDetails_SightId = "SIGHT_ID";

    private FragmentActivity activity;

    public SightDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_sight_details, container, false);

        final ImageView coverImageView = (ImageView) view.findViewById(R.id.cover_image_view);

        final int sightId = getArguments().getInt(SightDetails_SightId, 0);
        if (sightId == 0) {
            throw new UnsupportedOperationException();
        }

        // Load Sight
        Thread thread = new Thread(new Runnable() {
            SightApi sightApi = new SightApi();

            @Override
            public void run() {
                SharedPreferences prefs = getActivity().getSharedPreferences(Configs.PREFS, MODE_PRIVATE);
                String accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null);

                GetSightOutput getSightOutput = sightApi.apiSightGet(new GetSightInput(sightId), "Bearer " + accessToken);
                SightDto sight = getSightOutput.getSight();

                System.out.println(sight);

                loadRatings(Arrays.asList(sight.getRatings()));
                loadComments(Arrays.asList(sight.getComments()));

                try {
                    ImageDto[] images = sight.getImages();
                    ImageDto coverImage = images[0];

                    URL imageBlobUrl = new URL(coverImage.getBlobUrl());
                    final Bitmap bitmap = BitmapFactory.decodeStream(imageBlobUrl.openConnection().getInputStream());

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            coverImageView.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();

        return view;
    }

    public void loadRatings(final List<RatingDto> ratings) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                RatingFragment ratingFragment = RatingFragment.newInstance(ratings);
                fragmentTransaction.add(R.id.rating_fragment_container, ratingFragment);
                fragmentTransaction.commit();
            }
        });
    }

    public void loadComments(final List<CommentDto> comments) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CommentFragment commentFragment = CommentFragment.newInstance(1, comments);
                fragmentTransaction.add(R.id.comment_fragment_container, commentFragment);
                fragmentTransaction.commit();
            }
        });
    }

}
