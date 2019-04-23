package com.cuhk.travelligent.page.comments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuhk.travelligent.Configs
import com.cuhk.travelligent.R
import io.swagger.client.apis.CommentApi
import io.swagger.client.apis.FlightApi
import io.swagger.client.apis.HotelApi
import io.swagger.client.apis.SightApi
import io.swagger.client.models.*
import kotlinx.android.synthetic.main.fragment_comments.view.*
import kotlinx.android.synthetic.main.fragment_create_comment.view.*
import kotlin.concurrent.thread


/**
 * A fragment representing a list of Items.
 */
class CommentsFragment : Fragment() {

    private var flightId: Int? = null
    private var hotelId: Int? = null
    private var sightId: Int? = null
    private val comments = mutableListOf<CommentDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_FLIGHT_ID)) flightId = it.getInt(ARG_FLIGHT_ID)
            if (it.containsKey(ARG_HOTEL_ID)) hotelId = it.getInt(ARG_HOTEL_ID)
            if (it.containsKey(ARG_SIGHT_ID)) sightId = it.getInt(ARG_SIGHT_ID)

            val serializableArg = it.getSerializable(ARG_SERIALIZABLE_ARG) as CommentsFragmentSerializableArg
            comments.addAll(0, serializableArg.comments)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comments, container, false)
        val activity = activity!!

        // Set the adapter
        with(view.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = MyCommentRecyclerViewAdapter(comments)
        }

        val firstCommentView = view.first_comment
        val fab = view.fab

        if (comments.isEmpty()) {
            firstCommentView.visibility = View.VISIBLE
            fab.hide()
        } else {
            firstCommentView.visibility = View.INVISIBLE
            fab.show()
        }

        firstCommentView.setOnClickListener {
            showCommentDialog(activity, view)
        }
        fab.setOnClickListener {
            showCommentDialog(activity, view)
        }

        return view
    }

    private fun showCommentDialog(activity: FragmentActivity, view: View) {
        val viewInflated = LayoutInflater.from(context)
            .inflate(R.layout.fragment_create_comment, getView() as ViewGroup?, false)

        AlertDialog.Builder(context!!)
            .setTitle("New Comment")
            .setMessage("Type something to share!")
            .setView(viewInflated)
            .setPositiveButton("Create") { dialog, whichButton ->
                val content = viewInflated.content.text.toString()

                val prefs = activity.getSharedPreferences(Configs.PREFS, Context.MODE_PRIVATE)
                val accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null)

                val commentApi = CommentApi()
                val flightApi = FlightApi()
                val hotelApi = HotelApi()
                val sightApi = SightApi()

                thread {
                    val commentOutput = commentApi.apiCommentCreate(
                        CreateCommentInput(content, flightId, sightId, hotelId),
                        "Bearer " + accessToken!!
                    )

                    val comments = if (flightId != null) {
                        val getFlightOutput = flightApi.apiFlightGet(GetFlightInput(flightId), "Bearer " + accessToken)
                        val flight = getFlightOutput.flight!!

                        flight.comments
                    } else if (sightId != null) {
                        val getSightOutput = sightApi.apiSightGet(GetSightInput(sightId), "Bearer " + accessToken)
                        val sight = getSightOutput.sight!!

                        sight.comments
                    } else {
                        val getSightOutput = hotelApi.apiHotelGet(GetHotelInput(hotelId), "Bearer " + accessToken)
                        val hotel = getSightOutput.hotel!!

                        hotel.comments
                    }

                    this.comments.clear()
                    this.comments.addAll(0, comments!!.asList())

                    activity.runOnUiThread {
                        view.list.adapter!!.notifyDataSetChanged()

                        view.first_comment.visibility = View.INVISIBLE
                        view.fab.show()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, whichButton -> }
            .show()
    }

    companion object {

        const val ARG_FLIGHT_ID = "flight-id"
        const val ARG_HOTEL_ID = "hotel-id"
        const val ARG_SIGHT_ID = "sight-id"
        const val ARG_SERIALIZABLE_ARG = "column-count"

        @JvmStatic
        fun newInstance(
            sightId: Int? = null,
            flightId: Int? = null,
            hotelId: Int? = null,
            serializableArg: CommentsFragmentSerializableArg
        ) =
            CommentsFragment().apply {
                arguments = Bundle().apply {
                    if (sightId != null) putInt(ARG_SIGHT_ID, sightId)
                    if (flightId != null) putInt(ARG_FLIGHT_ID, flightId)
                    if (hotelId != null) putInt(ARG_FLIGHT_ID, hotelId)
                    putSerializable(ARG_SERIALIZABLE_ARG, serializableArg)
                }
            }
    }
}
