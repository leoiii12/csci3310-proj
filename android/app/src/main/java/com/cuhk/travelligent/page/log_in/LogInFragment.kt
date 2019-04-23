package com.cuhk.travelligent.page.log_in


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cuhk.travelligent.Configs
import com.cuhk.travelligent.HomeActivity

import com.cuhk.travelligent.R
import com.cuhk.travelligent.page.sign_up.SignUpFragment
import io.swagger.client.apis.AuthApi
import io.swagger.client.apis.UserApi
import io.swagger.client.models.AuthenticateInput
import io.swagger.client.models.MyUserDto
import kotlinx.android.synthetic.main.fragment_log_in.view.*
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass.
 *
 */
class LogInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_log_in, container, false)
        val context = context!!
        val activity = activity!!
        val fragmentManager = fragmentManager!!

        val emailAddressView = view.email_address
        val passwordView = view.password
        val logInButton = view.log_in_button
        val signUpButton = view.sign_up_button

        emailAddressView.setText("choimankin@outlook.com")
        passwordView.setText("12345678")
        logInButton.setOnClickListener {
            emailAddressView.isEnabled = false
            passwordView.isEnabled = false
            logInButton.isEnabled = false
            signUpButton.isEnabled = false

            val emailAddress = emailAddressView.text.toString()
            val password = passwordView.text.toString()

            val authApi = AuthApi()
            val userApi = UserApi()

            thread {
                val authenticateInput = AuthenticateInput(emailAddress, password)
                val authenticateOutput = authApi.apiAuthAuthenticate(authenticateInput, "")

                val myUser: MyUserDto

                try {
                    val getMyUserOutput = userApi.apiUserGetMyUser("Bearer " + authenticateOutput.accessToken!!)
                    myUser = getMyUserOutput.myUser!!
                } catch (ex: Exception) {
                    ex.printStackTrace()

                    return@thread
                }

                val editor = context.getSharedPreferences(Configs.PREFS, Context.MODE_PRIVATE).edit()
                editor.putInt(Configs.PREFS_USER_ID, myUser.id!!)
                editor.putString(Configs.PREFS_EMAIL_ADDRESS, myUser.emailAddress)
                editor.putString(Configs.PREFS_FIRST_NAME, myUser.firstName)
                editor.putString(Configs.PREFS_LAST_NAME, myUser.lastName)
                editor.putString(Configs.PREFS_ACCESS_TOKEN, authenticateOutput.accessToken)
                editor.apply()

                activity.runOnUiThread {
                    emailAddressView.isEnabled = true
                    passwordView.isEnabled = true
                    logInButton.isEnabled = true
                    signUpButton.isEnabled = true
                }

                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        signUpButton.setOnClickListener {
            val emailAddress = emailAddressView.text.toString()
            val password = passwordView.text.toString()

            val signUpFragment = SignUpFragment.newInstance(emailAddress, password)

            fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_fragment, signUpFragment)
                .commit()
        }

        if (context.getSharedPreferences(Configs.PREFS, Context.MODE_PRIVATE).contains(Configs.PREFS_ACCESS_TOKEN)) {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)

            return view
        }

        return view;
    }


}
