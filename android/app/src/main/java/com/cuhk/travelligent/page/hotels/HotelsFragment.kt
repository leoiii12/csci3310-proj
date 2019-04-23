package com.cuhk.travelligent.page.hotels

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
import com.cuhk.travelligent.page.create_hotel.CreateHotelFragment
import com.cuhk.travelligent.page.hotel_details.HotelDetailsFragment
import io.swagger.client.apis.HotelApi
import io.swagger.client.models.HotelListDto
import kotlinx.android.synthetic.main.fragment_hotels.view.*
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
    fun onListFragmentInteraction(hotel: HotelListDto)
}

/**
 * A fragment representing a list of Items.
 */
class HotelsFragment : Fragment(), OnListFragmentInteractionListener {

    private val hotelApi = HotelApi()
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
                (recyclerView.adapter as MyHotelRecyclerViewAdapter).filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    (recyclerView.adapter as MyHotelRecyclerViewAdapter).filter("")
                }

                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hotels, container, false)
        val activity = activity!!

        activity.title = "Hotels"

        recyclerView = view.list
        val fab = view.findViewById(R.id.fab) as FloatingActionButton

        // Set the adapter
        with(recyclerView) {
            layoutManager = LinearLayoutManager(view.context)
            adapter = MyHotelRecyclerViewAdapter(listener)
        }

        // Set the fab
        fab.setOnClickListener {
            val createHotelFragment = CreateHotelFragment()

            fragmentManager!!
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.home_fragment, createHotelFragment).commit()
        }

        // Load hotels
        thread {
            val prefs = activity.getSharedPreferences(Configs.PREFS, MODE_PRIVATE)
            val accessToken = prefs.getString(Configs.PREFS_ACCESS_TOKEN, null)

            val listHotelsOutput = hotelApi.apiHotelList("Bearer $accessToken")

            activity.runOnUiThread {
                (recyclerView.adapter as MyHotelRecyclerViewAdapter).newHotels(listHotelsOutput.hotels!!.asList())
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

    override fun onListFragmentInteraction(hotel: HotelListDto) {
        val hotelDetailsFragment = HotelDetailsFragment.newInstance(hotel.id!!)

        fragmentManager!!
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.home_fragment, hotelDetailsFragment).commit()
    }

}
