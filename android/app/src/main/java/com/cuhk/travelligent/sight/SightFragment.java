package com.cuhk.travelligent.sight;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuhk.travelligent.Configs;
import com.cuhk.travelligent.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.swagger.client.apis.SightApi;
import io.swagger.client.models.ListSightsOutput;
import io.swagger.client.models.SightListDto;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SightFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private Activity activity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SightFragment() {
    }

    @SuppressWarnings("unused")
    public static SightFragment newInstance(int columnCount) {
        SightFragment fragment = new SightFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        activity.setTitle("Sights");

        View view = inflater.inflate(R.layout.fragment_sight_list, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.list);
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        // Load Sights
        final List<SightListDto> sights = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            SightApi sightApi = new SightApi();

            @Override
            public void run() {
                SharedPreferences prefs = activity.getSharedPreferences(Configs.PREFS, MODE_PRIVATE);
                String accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null);

                ListSightsOutput listSightsOutput = sightApi.apiSightList("Bearer " + accessToken);
                sights.addAll(Arrays.asList(listSightsOutput.getSights()));

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        });
        thread.start();

        // Set the adapter
        recyclerView.setAdapter(new MySightRecyclerViewAdapter(sights, mListener));

        // Set the fab
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateSightFragment createSightFragment = CreateSightFragment.newInstance("", null, null);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction
                        .addToBackStack(null)
                        .replace(R.id.homeFragment, createSightFragment).commit();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(SightListDto item);
    }
}
