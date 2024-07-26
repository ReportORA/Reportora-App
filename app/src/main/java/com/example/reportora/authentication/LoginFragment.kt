package com.example.reportora.authentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.reportora.R
import com.example.reportora.ui.HomeFragmentDirections
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)
        view.findViewById<TextView>(R.id.signup_nav).setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment)}

        auth = Firebase.auth
        val emailEditText =view.findViewById<TextInputEditText>(R.id.login_username)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.login_password)
        val signInButton = view.findViewById<Button>(R.id.login_button)


        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            signIn(email, password)
        }

        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUserSignIn()
    }


    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword("$email@gmail.com", password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {

                        Log.i("LoginFragment", "Current User: ${user.email}")

                        if(user.uid == "gacmBE4JgqgRvPgGlu5783pUm7k1") {

                            Navigation.findNavController(requireView())
                                .navigate(R.id.action_loginFragment_to_uploadFragment)
                        }
                        else{
                            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment(email)
                            findNavController().navigate(action)
                        }

                    }
                    Toast.makeText(context, "Sign In Successful.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Sign In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun checkUserSignIn() {
        val currentUser = auth.currentUser

        if (currentUser != null) {

            if(currentUser.uid == "gacmBE4JgqgRvPgGlu5783pUm7k1") {

                Navigation.findNavController(requireView())
                    .navigate(R.id.action_loginFragment_to_uploadFragment)
            }
            else
            {
                val email = currentUser.email.toString()
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment(email.replace("@gmail.com", "").toString())
                findNavController().navigate(action)

            }

        }
    }



}