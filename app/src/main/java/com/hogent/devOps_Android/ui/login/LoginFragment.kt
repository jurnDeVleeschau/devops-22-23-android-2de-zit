package com.hogent.devOps_Android.ui.login

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.hogent.devOps_Android.R
import com.hogent.devOps_Android.databinding.FragmentLoginBinding
import timber.log.Timber

class LoginFragment : Fragment() {

    private lateinit var account: Auth0
    private lateinit var loggedInText: TextView
    private var loggedIn = false
    private var UserId = ""

    private lateinit var application: Application

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        application = requireNotNull(this.activity).application

        /*CredentialsManager.LoggedIn.observe(this.viewLifecycleOwner, Observer{
            navigateToVMLIST()
        })*/

        // OAUTH
        account = Auth0(
            getString(R.string.auth_client_id),
            getString(R.string.auth_domain)
        )

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val button = view.findViewById<Button>(R.id.login_button)
        button?.setOnClickListener {
            loginWithBrowser()
        }

        val logoutbutton = view.findViewById<Button>(R.id.logout_button)
        logoutbutton?.setOnClickListener {
            logout()
        }
        loggedInText = view.findViewById(R.id.logged_in_textview)

       /* binding.lifecycleOwner = this



        binding.loginButton.setOnClickListener {
            loginWithBrowser()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }

        binding.naarRegistreerButton.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.login_to_register)
        }

        loggedInText = binding.loggedInTextview*/

        checkIfToken()
        // setLoggedInText()
        // navigateToVMLIST()

        return view
    }

    private fun checkIfToken() {
        val token = CredentialsManager.getAccessToken(requireContext())
        if (token != null) {
            // checking if the token works...
            showUserProfile(token)
        } else {
            Toast.makeText(context, "Token doesn't exist", Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun setLoggedInText() {
        if(loggedIn) {loggedInText.text = "you're logged in"}
        else {loggedInText.text = "not logged in"}
    }*/

    private fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.

        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope("openid profile email")
            // Launch the authentication passing the callback where the results will be received
            .start(
                requireContext(),
                object : Callback<Credentials, AuthenticationException> {
                    // Called when there is an authentication failure
                    override fun onFailure(exception: AuthenticationException) {
                        loggedIn = false
                    }

                    // Called when authentication completed successfully
                    override fun onSuccess(credentials: Credentials) {
                        // Get the access token from the credentials object.
                        // This can be used to call APIs
                        val accessToken = credentials.accessToken
                        Toast.makeText(context, accessToken, Toast.LENGTH_SHORT).show()

                        CredentialsManager.saveCredentials(requireContext(), credentials)
                        checkIfToken()
                        // setLoggedInText()
                        navigateToVMLIST()
                    }
                }
            )
    }

    private fun navigateToVMLIST() {
        CredentialsManager.UserId = UserId
        if (loggedIn) {
            // var customerId = CredentialsManager.getAccessToken(requireContext())
            Timber.i(UserId)
            if (UserId != null) {
                /*NavHostFragment.findNavController(this)
                    .navigate(LoginFragmentDirections.loginToProfile())*/
                NavHostFragment.findNavController(this)
                    .navigate(LoginFragmentDirections.actionLoginFragmentToVMListFragment())
            }
        }
    }

    public fun logout() {
        WebAuthProvider.logout(account)
            .withScheme("demo")
            .start(
                requireContext(),
                object : Callback<Void?, AuthenticationException> {
                    override fun onSuccess(payload: Void?) {
                        Toast.makeText(context, "logout OK", Toast.LENGTH_SHORT).show()
                        loggedIn = false
                        CredentialsManager.LoggedIn.value = false
                        // setLoggedInText()
                    }

                    override fun onFailure(error: AuthenticationException) {
                        Toast.makeText(context, "logout NOK", Toast.LENGTH_SHORT).show()
                    }
                }
            )
    }

    private fun showUserProfile(accessToken: String) {
        var client = AuthenticationAPIClient(account)

        // With the access token, call `userInfo` and get the profile from Auth0.
        client.userInfo(accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(exception: AuthenticationException) {
                    Timber.i(exception.stackTraceToString())
                    loggedIn = false
                    // setLoggedInText()
                }

                override fun onSuccess(profile: UserProfile) {
                    // We have the user's profile!
                    Timber.i("SUCCESS! got the user profile")
                    UserId = profile.getId()!!
                    val email = profile.email
                    val name = profile.name
                    loggedIn = true
                    CredentialsManager.LoggedIn.value = true
                    // setLoggedInText()
                }
            })
    }
}
