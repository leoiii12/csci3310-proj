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
import io.swagger.client.apis.SightApi
import io.swagger.client.models.CommentDto
import io.swagger.client.models.CreateCommentInput
import io.swagger.client.models.GetSightInput
import kotlinx.android.synthetic.main.fragment_comments.view.*
import kotlinx.android.synthetic.main.fragment_create_comment.view.*
import kotlin.concurrent.thread


/**
 * A fragment representing a list of Items.
 */
class CommentsFragment : Fragment() {

    private var sightId: Int = -1
    private val comments = mutableListOf<CommentDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            sightId = it.getInt(ARG_SIGHT_ID)

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
                val sightApi = SightApi()

                thread {
                    val commentOutput = commentApi.apiCommentCreate(
                        CreateCommentInput(content, sightId = sightId),
                        "Bearer " + accessToken!!
                    )

                    val getSightOutput = sightApi.apiSightGet(GetSightInput(sightId), "Bearer " + accessToken)
                    val sight = getSightOutput.sight!!

                    comments.clear()
                    comments.addAll(0, sight.comments!!.asList())

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

        const val ARG_SIGHT_ID = "sight-id"
        const val ARG_SERIALIZABLE_ARG = "column-count"

        @JvmStatic
        fun newInstance(sightId: Int, serializableArg: CommentsFragmentSerializableArg) =
            CommentsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SIGHT_ID, sightId)
                    putSerializable(ARG_SERIALIZABLE_ARG, serializableArg)
                }
            }
    }
}
