package com.cuhk.travelligent.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cuhk.travelligent.R
import kotlinx.android.synthetic.main.fragment_comment.view.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_CREATE_USER_NAME = "param1"
private const val ARG_CONTENT = "param2"
private const val ARG_CREATE_DATE = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [CommentFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CommentFragment : Fragment() {

    private var createUserName: String? = null
    private var content: String? = null
    private var createDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            createUserName = it.getString(ARG_CREATE_USER_NAME)
            content = it.getString(ARG_CONTENT)
            createDate = it.getString(ARG_CREATE_DATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_comment, container, false)

        view.create_user_name.text = createUserName
        view.content.text = content

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())

        view.create_date.text = formatter.format(Instant.parse(createDate!!))

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param createUserName Parameter 1.
         * @param content Parameter 2.
         * @return A new instance of fragment CommentFragment.
         */
        @JvmStatic
        fun newInstance(createUserName: String, content: String, createDate: String) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CREATE_USER_NAME, createUserName)
                    putString(ARG_CONTENT, content)
                    putString(ARG_CREATE_DATE, createDate)
                }
            }
    }
}
