package com.cuhk.travelligent.sight;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuhk.travelligent.Configs;
import com.cuhk.travelligent.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.swagger.client.apis.ImageApi;
import io.swagger.client.apis.SightApi;
import io.swagger.client.models.CreateImageOutput;
import io.swagger.client.models.CreateSightInput;
import io.swagger.client.models.CreateSightOutput;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateSightFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateSightFragment extends Fragment implements OnMapReadyCallback {

    private static int IMAGE_PICKER_SELECT = 0;

    private static final String ARG_DEFAULT_TITLE = "default_title";
    private static final String ARG_DEFAULT_LATITUDE = "default_latitude";
    private static final String ARG_DEFAULT_LONGITUDE = "default_longitude";

    private String defaultTitle;
    private double defaultLatitude;
    private double defaultLongitude;

    private Button selectCoverImageButton;
    private ImageView coverImageView;
    private TextView titleView;
    private Button createButton;

    private CreateImageOutput createImageOutput = null;
    private LatLng selectedLatLng;

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

        selectCoverImageButton = (Button) view.findViewById(R.id.select_cover_image_button);
        selectCoverImageButton.setOnClickListener(new View.OnClickListener() {
            ImageApi imageApi = new ImageApi();

            @Override
            public void onClick(View v) {
                selectCoverImageButton.setEnabled(false);
                createButton.setEnabled(true);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = getActivity().getSharedPreferences(Configs.PREFS, MODE_PRIVATE);
                        String accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null);

                        createImageOutput = imageApi.apiImageCreate("Bearer " + accessToken);

                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, IMAGE_PICKER_SELECT);
                    }
                });

                thread.start();
            }
        });

        coverImageView = (ImageView) view.findViewById(R.id.cover_image_view);

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

                        CreateSightInput createSightInput = createImageOutput == null ?
                                new CreateSightInput(titleView.getText().toString(), selectedLatLng.latitude, selectedLatLng.longitude, new Integer[]{}) :
                                new CreateSightInput(titleView.getText().toString(), selectedLatLng.latitude, selectedLatLng.longitude, new Integer[]{createImageOutput.getImageId()});
                        CreateSightOutput createSightOutput = sightApi.apiSightCreate(createSightInput, "Bearer " + accessToken);

                        getFragmentManager().popBackStack();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            final ClipData.Item item = data.getClipData().getItemAt(0);
            final ContentResolver contentResolver = getContext().getContentResolver();
            final Uri uri = item.getUri();

            try {
                InputStream inputStream = contentResolver.openInputStream(uri);
                byte[] bytes = IOUtils.toByteArray(inputStream);

                RequestBody requestBody = RequestBody.create(MediaType.parse("image"), bytes);

                Request request = new Request.Builder()
                        .url(createImageOutput.getBlobUrl())
                        .addHeader("x-ms-version", "2015-02-21")
                        .addHeader("x-ms-date", "2019-04-21")
                        .addHeader("x-ms-blob-type", "BlockBlob")
                        .addHeader("x-ms-blob-content-disposition", "attachment; filename=\"image\"")
                        .addHeader("Content-Length", "" + bytes.length)
                        .addHeader("Content-Type", "image")
                        .put(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();

                client
                        .newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(final Call call, final IOException e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        selectCoverImageButton.setEnabled(true);
                                        createButton.setEnabled(false);

                                        createImageOutput = null;
                                    }
                                });
                            }

                            @Override
                            public void onResponse(final Call call, final Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            selectCoverImageButton.setVisibility(View.INVISIBLE);
                                            createButton.setEnabled(true);

                                            try {
                                                coverImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(contentResolver, uri));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
