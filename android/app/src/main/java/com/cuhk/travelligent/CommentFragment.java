package com.cuhk.travelligent;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import io.swagger.client.models.CommentDto;

class CommentFragmentSerializableArg implements Serializable {

    public List<CommentDto> comments;

    public CommentFragmentSerializableArg(List<CommentDto> comments) {
        this.comments = comments;
    }

}

/**
 * A fragment representing a list of Items.
 */
public class CommentFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_ARG = "arg";

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<CommentDto> comments;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommentFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CommentFragment newInstance(int columnCount, List<CommentDto> comments) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putSerializable(ARG_ARG, new CommentFragmentSerializableArg(comments));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            comments = ((CommentFragmentSerializableArg) getArguments().getSerializable(ARG_ARG)).comments;

            System.out.println(comments);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyCommentRecyclerViewAdapter(comments));
        }
        return view;
    }

}
