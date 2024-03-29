package com.cuhk.travelligent.page.sight_details


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
import io.swagger.client.apis.SightApi
import io.swagger.client.models.CreateRatingInput
import io.swagger.client.models.GetSightInput
import io.swagger.client.models.SightDto
import kotlinx.android.synthetic.main.fragment_sight_details.view.*
import kotlin.concurrent.thread


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_SIGHT_ID = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [SightDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SightDetailsFragment : Fragment() {

    private val sightApi = SightApi()
    private val ratingApi = RatingApi()

    private var sightId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            sightId = it.getInt(ARG_SIGHT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sight_details, container, false)
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
            val getSightOutput = sightApi.apiSightGet(GetSightInput(sightId), "Bearer $accessToken")
            val sight = getSightOutput.sight!!

            activity.runOnUiThread {
                activity.title = sight.title

                initCoverImageSection(view, coverImageView, sight)
                initMap(mapButton, sight)
                initRatingSection(activity, ratingBarView, ratingBarHintsView, sight)
                initCommentSection(viewCommentsButton, firstCommentBanner, sight)
            }
        }

        return view
    }

    private fun initMap(mapButton: MaterialButton, sight: SightDto) {
        if (sight.latLng != null && sight.latLng!!.latitude != null && sight.latLng!!.longitude != null) {
            mapButton.setOnClickListener {
                val latLng = LatLng(sight.latLng!!.latitude!!, sight.latLng!!.longitude!!)
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
        sight: SightDto
    ) {
        if (sight.images!!.isNotEmpty()) {
            Glide
                .with(view.context)
                .load(sight.images!![0].blobUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(coverImageView)
        }
    }

    @UiThread
    private fun initRatingSection(
        activity: FragmentActivity,
        ratingBarView: RatingBar,
        ratingBarHintsView: TextView,
        sight: SightDto
    ) {
        val avg = calculateAvgRating(sight)

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
                            CreateRatingInput(rating.toDouble(), sightId = sightId),
                            "Bearer $accessToken"
                        )

                        val getSightOutput = sightApi.apiSightGet(GetSightInput(sightId), "Bearer $accessToken")
                        val sight = getSightOutput.sight!!

                        activity.runOnUiThread {
                            val avg = calculateAvgRating(sight)

                            ratingBarView.rating = avg.toFloat()
                            ratingBarHintsView.text = "This is rated by ${sight.ratings!!.size} people."
                        }
                    }
                }
                .setNegativeButton("Cancel") { dialog, whichButton -> }
                .show()
        }
        if (sight.ratings!!.isNotEmpty()) {
            ratingBarHintsView.text = "This is rated by ${sight.ratings!!.size} people."
        } else {
            ratingBarHintsView.text = "Be the first one to rate."
        }
    }

    private fun calculateAvgRating(sight: SightDto): Double {
        var sum = 0.0
        sight.ratings!!.forEach {
            sum += it.value!!
        }
        val avg = sum / sight.ratings!!.size
        return avg
    }

    @UiThread
    private fun initCommentSection(
        viewCommentsButton: MaterialButton,
        firstCommentBanner: MaterialCardView,
        sight: SightDto
    ) {
        // Add previews
        val fragments = mutableListOf<CommentFragment>()
        sight.comments!!.forEach {
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
                sightId = sightId!!,
                serializableArg = CommentsFragmentSerializableArg(sight.comments!!.asList())
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
         * @param sightId Parameter 1.
         * @return A new instance of fragment SightDetailsFragment.
         */
        @JvmStatic
        fun newInstance(sightId: Int) =
            SightDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SIGHT_ID, sightId)
                }
            }
    }
}
