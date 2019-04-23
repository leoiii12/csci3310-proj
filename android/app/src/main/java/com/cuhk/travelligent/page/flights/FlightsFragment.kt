package com.cuhk.travelligent.page.flights

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuhk.travelligent.Configs
import com.cuhk.travelligent.R
import com.cuhk.travelligent.page.create_flight.CreateFlightFragment
import com.cuhk.travelligent.page.flight_details.FlightDetailsFragment
import io.swagger.client.apis.FlightApi
import io.swagger.client.models.FlightListDto
import kotlinx.android.synthetic.main.fragment_flights.view.*
import kotlin.concurrent.thread


/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 *
 *
 * See the Android Training lesson
 * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
 * for more information.
 */
interface OnListFragmentInteractionListener {
    fun onListFragmentInteraction(flight: FlightListDto)
}

/**
 * A fragment representing a list of Items.
 */
class FlightsFragment : Fragment(), OnListFragmentInteractionListener {

    private val flightApi = FlightApi()
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                (recyclerView.adapter as MyFlightRecyclerViewAdapter).filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    (recyclerView.adapter as MyFlightRecyclerViewAdapter).filter("")
                }

                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_flights, container, false)
        val activity = activity!!

        activity.title = "Flights"

        recyclerView = view.list
        val fab = view.findViewById(R.id.fab) as FloatingActionButton

        // Set the adapter
        with(recyclerView) {
            layoutManager = LinearLayoutManager(view.context)
            adapter = MyFlightRecyclerViewAdapter(listener)
        }

        // Set the fab
        fab.setOnClickListener {
            val createFlightFragment = CreateFlightFragment()

            fragmentManager!!
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.home_fragment, createFlightFragment).commit()
        }

        // Load flights
        thread {
            val prefs = activity.getSharedPreferences(Configs.PREFS, MODE_PRIVATE)
            val accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null)

            val listFlightsOutput = flightApi.apiFlightList("Bearer $accessToken")

            activity.runOnUiThread {
                (recyclerView.adapter as MyFlightRecyclerViewAdapter).newFlights(listFlightsOutput.flights!!.asList())
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = this
    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    override fun onListFragmentInteraction(flight: FlightListDto) {
        val flightDetailsFragment = FlightDetailsFragment.newInstance(flight.id!!)

        fragmentManager!!
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.home_fragment, flightDetailsFragment).commit()
    }

}
