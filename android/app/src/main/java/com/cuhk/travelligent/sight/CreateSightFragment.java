package com.cuhk.travelligent.sight;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cuhk.travelligent.Configs;
import com.cuhk.travelligent.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import io.swagger.client.apis.SightApi;
import io.swagger.client.models.CreateSightInput;
import io.swagger.client.models.CreateSightOutput;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateSightFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateSightFragment extends Fragment implements OnMapReadyCallback {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DEFAULT_TITLE = "default_title";
    private static final String ARG_DEFAULT_LATITUDE = "default_latitude";
    private static final String ARG_DEFAULT_LONGITUDE = "default_longitude";

    private String defaultTitle;
    private double defaultLatitude;
    private double defaultLongitude;

    private TextView titleView;
    private LatLng selectedLatLng;
    private Button createButton;

    private GoogleMap googleMap;
    private Geocoder geocoder;

    public CreateSightFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title     Parameter 1.
     * @param latitude  Parameter 2.
     * @param longitude Parameter 3.
     * @return A new instance of fragment CreateSightFragment.
     */
    public static CreateSightFragment newInstance(String title, Double latitude, Double longitude) {
        CreateSightFragment fragment = new CreateSightFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEFAULT_TITLE, title);

        // CUHK
        if (latitude == null) {
            args.putDouble(ARG_DEFAULT_LATITUDE, 22.4162632);
        }
        if (longitude == null) {
            args.putDouble(ARG_DEFAULT_LONGITUDE, 114.2087378);
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            defaultTitle = getArguments().getString(ARG_DEFAULT_TITLE);
            defaultLatitude = getArguments().getDouble(ARG_DEFAULT_LATITUDE);
            defaultLongitude = getArguments().getDouble(ARG_DEFAULT_LONGITUDE);
        }

        geocoder = new Geocoder(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_sight, container, false);

        titleView = (TextView) view.findViewById(R.id.title_view);
        titleView.setText(defaultTitle);

        createButton = (Button) view.findViewById(R.id.create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            SightApi sightApi = new SightApi();

            @Override
            public void onClick(View v) {
                createButton.setEnabled(false);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = getActivity().getSharedPreferences(Configs.PREFS, MODE_PRIVATE);
                        String accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null);

                        CreateSightInput createSightInput = new CreateSightInput(titleView.getText().toString(), selectedLatLng.latitude, selectedLatLng.longitude);
                        CreateSightOutput createSightOutput = sightApi.apiSightCreate(createSightInput, "Bearer " + accessToken);
                    }
                });

                thread.start();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Set default latLng
        selectedLatLng = new LatLng(defaultLatitude, defaultLongitude);
        googleMap.addMarker(new MarkerOptions().position(selectedLatLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 17.0f));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();

                selectedLatLng = latLng;

                googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);

                    if (addresses.size() >= 1) {
                        Address address = addresses.get(0);

                        titleView.setText(address.getFeatureName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
