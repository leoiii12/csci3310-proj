package com.cuhk.travelligent.page.flight_details


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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import io.swagger.client.apis.FlightApi
import io.swagger.client.apis.RatingApi
import io.swagger.client.models.CreateRatingInput
import io.swagger.client.models.FlightDto
import io.swagger.client.models.GetFlightInput
import kotlinx.android.synthetic.main.fragment_flight_details.view.*
import kotlin.concurrent.thread


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_SIGHT_ID = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [FlightDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FlightDetailsFragment : Fragment() {

    private val flightApi = FlightApi()
    private val ratingApi = RatingApi()

    private var flightId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            flightId = it.getInt(ARG_SIGHT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_flight_details, container, false)
        val activity = activity!!

        val prefs = activity.getSharedPreferences(Configs.PREFS, MODE_PRIVATE)
        val accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null)

        val coverImageView = view.cover_image
        val ratingBarView = view.rating_bar
        val ratingBarHintsView = view.rating_bar_hints
        val viewCommentsButton = view.view_comments_button
        val firstCommentBanner = view.no_comments_yet

        thread {
            val getFlightOutput = flightApi.apiFlightGet(GetFlightInput(flightId), "Bearer " + accessToken!!)
            val flight = getFlightOutput.flight!!

            activity.runOnUiThread {
                activity.title = flight.title
                
                initCoverImageSection(view, coverImageView, flight)
                initRatingSection(activity, ratingBarView, ratingBarHintsView, flight)
                initCommentSection(viewCommentsButton, firstCommentBanner, flight)
            }
        }

        return view
    }

    @UiThread
    private fun initCoverImageSection(
        view: View,
        coverImageView: ImageView,
        flight: FlightDto
    ) {
        if (flight.images!!.isNotEmpty()) {
            Glide
                .with(view.context)
                .load(flight.images!![0].blobUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(coverImageView)
        }
    }

    @UiThread
    private fun initRatingSection(
        activity: FragmentActivity,
        ratingBarView: RatingBar,
        ratingBarHintsView: TextView,
        flight: FlightDto
    ) {
        val avg = calculateAvgRating(flight)

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
                            CreateRatingInput(rating.toDouble(), flightId = flightId),
                            "Bearer " + accessToken!!
                        )

                        val getFlightOutput = flightApi.apiFlightGet(GetFlightInput(flightId), "Bearer " + accessToken)
                        val flight = getFlightOutput.flight!!

                        activity.runOnUiThread {
                            val avg = calculateAvgRating(flight)

                            ratingBarView.rating = avg.toFloat()
                            ratingBarHintsView.text = "This is rated by ${flight.ratings!!.size} people."
                        }
                    }
                }
                .setNegativeButton("Cancel") { dialog, whichButton -> }
                .show()
        }
        if (flight.ratings!!.isNotEmpty()) {
            ratingBarHintsView.text = "This is rated by ${flight.ratings!!.size} people."
        } else {
            ratingBarHintsView.text = "Be the first one to rate."
        }
    }

    private fun calculateAvgRating(flight: FlightDto): Double {
        var sum = 0.0
        flight.ratings!!.forEach {
            sum += it.value!!
        }
        val avg = sum / flight.ratings!!.size
        return avg
    }

    @UiThread
    private fun initCommentSection(
        viewCommentsButton: MaterialButton,
        firstCommentBanner: MaterialCardView,
        flight: FlightDto
    ) {
        // Add previews
        val fragments = mutableListOf<CommentFragment>()
        flight.comments!!.forEach {
            if (fragments.size < 3) {
                fragments.add(
                    CommentFragment.newInstance(
                        "${it.createUser!!.firstName} ${it.createUser!!.lastName}",
                        it.content!!
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
                flightId = flightId!!,
                serializableArg = CommentsFragmentSerializableArg(flight.comments!!.asList())
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
         * @param flightId Parameter 1.
         * @return A new instance of fragment FlightDetailsFragment.
         */
        @JvmStatic
        fun newInstance(flightId: Int) =
            FlightDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SIGHT_ID, flightId)
                }
            }
    }
}
