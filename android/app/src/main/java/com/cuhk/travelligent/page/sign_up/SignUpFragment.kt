package com.cuhk.travelligent.page.sign_up


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.cuhk.travelligent.Configs
import com.cuhk.travelligent.HomeActivity
import com.cuhk.travelligent.R
import io.swagger.client.apis.AuthApi
import io.swagger.client.apis.UserApi
import io.swagger.client.models.AuthenticateInput
import io.swagger.client.models.CheckAccountInput
import io.swagger.client.models.SignUpInput
import kotlinx.android.synthetic.main.fragment_log_in.view.*
import kotlin.concurrent.thread

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_EMAIL_ADDRESS = "param1"
private const val ARG_PASSWORD = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SignUpFragment : Fragment() {

    private var defaultEmailAddress: String? = null
    private var defaultPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            defaultEmailAddress = it.getString(ARG_EMAIL_ADDRESS)
            defaultPassword = it.getString(ARG_PASSWORD)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        val context = context!!
        val activity = activity!!
        val fragmentManager = fragmentManager!!

        val emailAddressView = view.email_address
        val passwordView = view.password
        val signUpButton = view.sign_up_button
        val logInButton = view.log_in_button

        emailAddressView.setText(defaultEmailAddress)
        passwordView.setText(defaultPassword)
        signUpButton.setOnClickListener {
            emailAddressView.isEnabled = false
            passwordView.isEnabled = false
            signUpButton.isEnabled = false
            logInButton.isEnabled = false

            val authApi = AuthApi()
            val userApi = UserApi()

            thread {
                val emailAddress = emailAddressView.text.toString()
                val password = passwordView.text.toString()

                val checkAccountInput = CheckAccountInput(emailAddress)
                val checkAccountOutput = authApi.apiAuthCheckAccount(checkAccountInput, "")

                if (checkAccountOutput.accountStatus == 1) {
                    activity.runOnUiThread {
                        AlertDialog.Builder(context)
                            .setTitle("Alert")
                            .setMessage("The emailAddress has been registered.")
                            .setPositiveButton("Dismiss") { dialog, whichButton ->
                                emailAddressView.isEnabled = true
                                passwordView.isEnabled = true
                                signUpButton.isEnabled = true
                                logInButton.isEnabled = true
                            }
                            .show()
                    }

                    return@thread
                }

                try {
                    val signUpInput = SignUpInput(emailAddress, password)
                    val signUpOutput = authApi.apiAuthSignUp(signUpInput, "")

                    val authenticateInput = AuthenticateInput(emailAddress, password)
                    val authenticateOutput = authApi.apiAuthAuthenticate(authenticateInput, "")


                    val getMyUserOutput = userApi.apiUserGetMyUser("Bearer " + authenticateOutput.accessToken!!)
                    val myUser = getMyUserOutput.myUser!!

                    val editor = context.getSharedPreferences(Configs.PREFS, Context.MODE_PRIVATE).edit()
                    editor.putInt(Configs.PREFS_USER_ID, myUser.id!!)
                    editor.putString(Configs.PREFS_EMAIL_ADDRESS, myUser.emailAddress)
                    editor.putString(Configs.PREFS_FIRST_NAME, myUser.firstName)
                    editor.putString(Configs.PREFS_LAST_NAME, myUser.lastName)
                    editor.putString(Configs.PREFS_ACCESS_TOKEN, authenticateOutput.accessToken)
                    editor.apply()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                activity.runOnUiThread {
                    emailAddressView.isEnabled = true
                    passwordView.isEnabled = true
                    signUpButton.isEnabled = true
                    logInButton.isEnabled = true
                }

                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        logInButton.setOnClickListener {
            fragmentManager.popBackStack()
        }

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param emailAddress Parameter 1.
         * @param password Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        @JvmStatic
        fun newInstance(emailAddress: String, password: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EMAIL_ADDRESS, emailAddress)
                    putString(ARG_PASSWORD, password)
                }
            }
    }
}
