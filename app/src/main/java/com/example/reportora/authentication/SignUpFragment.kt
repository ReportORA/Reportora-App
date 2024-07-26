package com.example.reportora.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.reportora.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_sign_up, container, false)
        view.findViewById<TextView>(R.id.login_nav).setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment)}

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()


        val emailEditText =view.findViewById<TextInputEditText>(R.id.signup_username)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.signup_password)
        val heightEditText = view.findViewById<TextInputEditText>(R.id.signup_height)
        val weightEditText = view.findViewById<TextInputEditText>(R.id.signup_weight)
        val bloodGroupEditText = view.findViewById<TextInputEditText>(R.id.signup_blood)
        val phoneNumberEditText = view.findViewById<TextInputEditText>(R.id.signup_number)
        val signUpButton = view.findViewById<Button>(R.id.signup_button)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val height = heightEditText.text.toString()
            val weight = weightEditText.text.toString()
            val bloodGroup = bloodGroupEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            signUp(email, password, height,weight, bloodGroup, phoneNumber)
        }

        return  view
    }

    private fun signUp(email: String, password: String, height: String,weight: String, bloodGroup: String, phoneNumber: String) {
        auth.createUserWithEmailAndPassword("$email@gmail.com", password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    val userMap = hashMapOf(
                        "email" to email,
                        "height" to height,
                        "weight" to weight,
                        "bloodGroup" to bloodGroup,
                        "phoneNumber" to phoneNumber
                    )

                    userId?.let {
                        val database = Firebase.database
                        database.reference.child("users").child(it)
                            .setValue(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Sign Up Successful.", Toast.LENGTH_SHORT).show()
                                Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_loginFragment)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Error saving user info: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }



}