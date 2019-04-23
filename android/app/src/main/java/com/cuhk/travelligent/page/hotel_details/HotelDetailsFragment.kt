package com.cuhk.travelligent.page.hotel_details


import android.content.Context.MODE_PRIVATE
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cuhk.travelligent.Configs
import com.cuhk.travelligent.R
import com.cuhk.travelligent.component.CommentFragment
import com.cuhk.travelligent.page.comments.CommentsFragment
import com.cuhk.travelligent.page.comments.CommentsFragmentSerializableArg
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import io.swagger.client.apis.RatingApi
import io.swagger.client.apis.HotelApi
import io.swagger.client.models.CreateRatingInput
import io.swagger.client.models.GetHotelInput
import io.swagger.client.models.HotelDto
import kotlinx.android.synthetic.main.fragment_hotel_details.view.*
import kotlin.concurrent.thread


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_SIGHT_ID = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [HotelDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HotelDetailsFragment : Fragment() {

    private val hotelApi = HotelApi()
    private val ratingApi = RatingApi()

    private var hotelId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            hotelId = it.getInt(ARG_SIGHT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_hotel_details, container, false)
        val activity = activity!!

        val prefs = activity.getSharedPreferences(Configs.PREFS, MODE_PRIVATE)
        val accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null)

        val coverImageView = view.cover_image
        val mapButton = view.map_button
        val ratingBarView = view.rating_bar
        val ratingBarHintsView = view.rating_bar_hints
        val viewCommentsButton = view.view_comments_button
        val firstCommentBanner = view.no_comments_yet

        thread {
            val getHotelOutput = hotelApi.apiHotelGet(GetHotelInput(hotelId), "Bearer " + accessToken!!)
            val hotel = getHotelOutput.hotel!!

            activity.runOnUiThread {
                activity.title = hotel.title

                initCoverImageSection(view, coverImageView, hotel)
                initMap(mapButton, hotel)
                initRatingSection(activity, ratingBarView, ratingBarHintsView, hotel)
                initCommentSection(viewCommentsButton, firstCommentBanner, hotel)
            }
        }

        return view
    }

    private fun initMap(mapButton: MaterialButton, hotel: HotelDto) {
        if (hotel.latLng != null && hotel.latLng!!.latitude != null && hotel.latLng!!.longitude != null) {
            mapButton.setOnClickListener {
                val latLng = LatLng(hotel.latLng!!.latitude!!, hotel.latLng!!.longitude!!)
                val mapFragment = SupportMapFragment.newInstance()

                mapFragment.getMapAsync { map ->
                    map.mapType = GoogleMap.MAP_TYPE_NORMAL

                    map.clear()

                    // Set default latLng
                    map.addMarker(MarkerOptions().position(latLng))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f))
                }

                fragmentManager!!.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.home_fragment, mapFragment)
                    .commit()
            }
        }
    }

    @UiThread
    private fun initCoverImageSection(
        view: View,
        coverImageView: ImageView,
        hotel: HotelDto
    ) {
        if (hotel.images!!.isNotEmpty()) {
            Glide
                .with(view.context)
                .load(hotel.images!![0].blobUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(coverImageView)
        }
    }

    @UiThread
    private fun initRatingSection(
        activity: FragmentActivity,
        ratingBarView: RatingBar,
        ratingBarHintsView: TextView,
        hotel: HotelDto
    ) {
        val avg = calculateAvgRating(hotel)

        ratingBarView.rating = avg.toFloat()
        ratingBarView.setIsIndicator(true)

        ratingBarHintsView.setOnClickListener {
            val viewInflated = LayoutInflater.from(context)
                .inflate(R.layout.fragment_create_rating, view as ViewGroup?, false)

            viewInflated.rating_bar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                if (fromUser) {
                    if (rating < 1f) {
                        ratingBar.rating = 1f
                    }
                }
            }

            AlertDialog.Builder(context!!)
                .setTitle("Rating")
                .setMessage("This is up to you.")
                .setView(viewInflated)
                .setPositiveButton("Create") { dialog, whichButton ->
                    val rating = viewInflated.rating_bar.rating

                    val prefs = activity.getSharedPreferences(Configs.PREFS, MODE_PRIVATE)
                    val accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null)

                    thread {
                        val createRatingOutput = ratingApi.apiRatingCreate(
                            CreateRatingInput(rating.toDouble(), hotelId = hotelId),
                            "Bearer " + accessToken!!
                        )

                        val getHotelOutput = hotelApi.apiHotelGet(GetHotelInput(hotelId), "Bearer " + accessToken)
                        val hotel = getHotelOutput.hotel!!

                        activity.runOnUiThread {
                            val avg = calculateAvgRating(hotel)

                            ratingBarView.rating = avg.toFloat()
                            ratingBarHintsView.text = "This is rated by ${hotel.ratings!!.size} people."
                        }
                    }
                }
                .setNegativeButton("Cancel") { dialog, whichButton -> }
                .show()
        }
        if (hotel.ratings!!.isNotEmpty()) {
            ratingBarHintsView.text = "This is rated by ${hotel.ratings!!.size} people."
        } else {
            ratingBarHintsView.text = "Be the first one to rate."
        }
    }

    private fun calculateAvgRating(hotel: HotelDto): Double {
        var sum = 0.0
        hotel.ratings!!.forEach {
            sum += it.value!!
        }
        val avg = sum / hotel.ratings!!.size
        return avg
    }

    @UiThread
    private fun initCommentSection(
        viewCommentsButton: MaterialButton,
        firstCommentBanner: MaterialCardView,
        hotel: HotelDto
    ) {
        // Add previews
        val fragments = mutableListOf<CommentFragment>()
        hotel.comments!!.forEach {
            if (fragments.size < 3) {
                fragments.add(
                    CommentFragment.newInstance(
                        "${it.createUser!!.firstName} ${it.createUser!!.lastName}",
                        it.content!!,
                        it.createDate!!
                    )
                )
            }
        }
        if (fragments.size > 0) {
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragments.forEach { fragmentTransaction.add(R.id.comments, it) }
            fragmentTransaction.commit()

            firstCommentBanner.visibility = View.GONE
        } else {
            firstCommentBanner.visibility = View.VISIBLE
        }

        // View Comments
        viewCommentsButton.setOnClickListener {
            val fragment = CommentsFragment.newInstance(
                hotelId = hotelId!!,
                serializableArg = CommentsFragmentSerializableArg(hotel.comments!!.asList())
            )
            fragmentManager!!.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.home_fragment, fragment)
                .commit()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param hotelId Parameter 1.
         * @return A new instance of fragment HotelDetailsFragment.
         */
        @JvmStatic
        fun newInstance(hotelId: Int) =
            HotelDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SIGHT_ID, hotelId)
                }
            }
    }
}
