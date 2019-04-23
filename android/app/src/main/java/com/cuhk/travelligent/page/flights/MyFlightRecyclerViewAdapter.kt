package com.cuhk.travelligent.page.flights


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cuhk.travelligent.R
import io.swagger.client.models.FlightListDto
import kotlinx.android.synthetic.main.fragment_flight.view.*
import java.util.*


/**
 * [RecyclerView.Adapter] that can display a [FlightListDto] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyFlightRecyclerViewAdapter(
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyFlightRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    private var query: String = ""

    private var values: List<FlightListDto> = listOf()
    private val filteredValues: MutableList<FlightListDto> = mutableListOf()

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as FlightListDto
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            listener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_flight, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredValues[position]

        if (item.image != null) {
            Glide
                .with(holder.view.context)
                .load(item.image!!.blobUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.coverImageView);
        }

        holder.titleView.text = item.title

        with(holder.view) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = filteredValues.size

    @UiThread
    fun newFlights(flights: List<FlightListDto>) {
        this.values = flights

        filter(query)
    }

    @UiThread
    fun filter(q: String) {
        query = q.toLowerCase(Locale.getDefault())

        filteredValues.clear()

        if (query.isEmpty()) {
            filteredValues.addAll(values)
        } else {
            for (value in values) {
                if (value.title!!.toLowerCase(Locale.getDefault()).contains(query)) {
                    filteredValues.add(value)
                }
            }
        }

        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val coverImageView: ImageView = view.cover_image
        val titleView: TextView = view.title

        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'"
        }
    }
}
