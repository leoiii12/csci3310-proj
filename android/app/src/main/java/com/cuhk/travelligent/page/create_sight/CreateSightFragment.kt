package com.cuhk.travelligent.page.create_sight

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.cuhk.travelligent.Configs
import com.cuhk.travelligent.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.swagger.client.apis.ImageApi
import io.swagger.client.apis.SightApi
import io.swagger.client.models.CreateImageOutput
import io.swagger.client.models.CreateSightInput
import okhttp3.*
import org.apache.commons.io.IOUtils
import java.io.IOException
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass.
 * Use the [CreateHotelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateSightFragment : Fragment(), OnMapReadyCallback {

    private val IMAGE_PICKER_SELECT = 0

    private lateinit var selectCoverImageButton: Button
    private lateinit var coverImageView: ImageView
    private lateinit var titleView: TextView
    private lateinit var createButton: Button

    private var createImageOutput: CreateImageOutput? = null
    private var selectedLatLng: LatLng = LatLng(22.4162632, 114.2087378)

    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        geocoder = Geocoder(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_sight, container, false)
        val activity = activity!!

        activity.title = "Create Sight"

        selectCoverImageButton = view.findViewById(R.id.select_cover_image_button) as Button
        coverImageView = view.findViewById(R.id.cover_image) as ImageView
        titleView = view.findViewById(R.id.title_view) as TextView
        createButton = view.findViewById(R.id.create_button) as Button

        selectCoverImageButton.setOnClickListener {
            selectCoverImageButton.isEnabled = false
            createButton.isEnabled = true

            val prefs = activity.getSharedPreferences(Configs.PREFS, MODE_PRIVATE)
            val accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null)

            val imageApi = ImageApi()

            thread {
                createImageOutput = imageApi.apiImageCreate("Bearer $accessToken")

                activity.runOnUiThread {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, IMAGE_PICKER_SELECT)
                }
            }
        }

        titleView.text = ""

        createButton.setOnClickListener {
            createButton.isEnabled = false

            val prefs = activity.getSharedPreferences(Configs.PREFS, MODE_PRIVATE)
            val accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null)

            val createSightInput = if (createImageOutput == null)
                CreateSightInput(
                    titleView.text.toString(),
                    selectedLatLng.latitude,
                    selectedLatLng.longitude,
                    arrayOf()
                )
            else
                CreateSightInput(
                    titleView.text.toString(),
                    selectedLatLng.latitude,
                    selectedLatLng.longitude,
                    arrayOf(createImageOutput!!.imageId!!)
                )

            thread {
                val sightApi = SightApi()

                val createSightOutput = sightApi.apiSightCreate(createSightInput, "Bearer $accessToken")

                activity.runOnUiThread {
                    fragmentManager!!.popBackStack()
                }
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Set default latLng
        map.addMarker(MarkerOptions().position(selectedLatLng))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 17.0f))

        map.setOnMapClickListener { latLng ->
            map.clear()

            selectedLatLng = latLng

            map.addMarker(MarkerOptions().position(latLng))
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))

            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10)

                if (addresses.size >= 1) {
                    val address = addresses[0]

                    titleView.text = address.featureName
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            val item = data!!.clipData!!.getItemAt(0)
            val uri = item.uri

            uploadPickedImage(uri)
        }
    }

    private fun uploadPickedImage(uri: Uri) {
        val contentResolver = context!!.contentResolver

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = IOUtils.toByteArray(inputStream)

            val requestBody = RequestBody.create(MediaType.parse("image"), bytes)

            val request = Request.Builder()
                .url(createImageOutput!!.blobUrl!!)
                .addHeader("x-ms-version", "2015-02-21")
                .addHeader("x-ms-date", "2019-04-21")
                .addHeader("x-ms-blob-type", "BlockBlob")
                .addHeader("x-ms-blob-content-disposition", "attachment; filename=\"image\"")
                .addHeader("Content-Length", "" + bytes.size)
                .addHeader("Content-Type", "image")
                .put(requestBody)
                .build()

            val client = OkHttpClient()

            client
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity!!.runOnUiThread {
                            selectCoverImageButton.isEnabled = true
                            createButton.isEnabled = false
                            createImageOutput = null
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            activity!!.runOnUiThread {
                                selectCoverImageButton.visibility = View.INVISIBLE
                                createButton.isEnabled = true

                                try {
                                    coverImageView.setImageBitmap(
                                        MediaStore.Images.Media.getBitmap(
                                            contentResolver,
                                            uri
                                        )
                                    )
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()

            selectCoverImageButton.isEnabled = true
            createButton.isEnabled = false
            createImageOutput = null
        }
    }

}
