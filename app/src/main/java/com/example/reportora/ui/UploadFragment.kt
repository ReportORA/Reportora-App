package com.example.reportora.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reportora.R
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UploadFragment : Fragment() {

    private val storage = Firebase.storage
    private  lateinit var uploadUsername:TextInputEditText
    private  lateinit var downloadUsername:TextInputEditText
    private  lateinit var uploadYear:TextInputEditText
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_upload, container, false)
        auth = Firebase.auth


        uploadUsername =view.findViewById(R.id.report_upload)
        downloadUsername =view.findViewById(R.id.report_download)
        uploadYear =view.findViewById(R.id.upload_year)

        val buttonSelectFile = view.findViewById<Button>(R.id.hospital_upload)
        val userReportButton = view.findViewById<Button>(R.id.hospital_download)

        val logout = view.findViewById<Button>(R.id.upload_logout)

        logout.setOnClickListener {
            firebaseLogout()
        }

        buttonSelectFile.setOnClickListener {
            pickFileLauncher.launch(intent)
        }

        userReportButton.setOnClickListener {
            val action = UploadFragmentDirections.actionUploadFragmentToHomeFragment(downloadUsername.text.toString())
            findNavController().navigate(action)
        }

        return view
    }

    private fun firebaseLogout() {
        auth.signOut()
        findNavController().navigate(R.id.action_uploadFragment_to_loginFragment)
    }

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = result.data?.data
            //fileUri?.let { uploadFileToFirebase(it) }
            val fileName = fileUri?.let { getFileNameFromUri(requireContext().contentResolver, it) }
            Log.i("UploadFragment", "File Name: $fileName")
            Log.i("UploadFragment", "Uri  Name: $fileUri")
            fileUri?.let {fileName?.let { name ->
                uploadFile(it, name)
            }
            }
        }
    }

    // Trigger file selection (e.g., in a button click listener)
    private val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*" // Or specify a more specific MIME type
        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/*")) // Includes images and documents
    }


    private fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri): String? {
        var fileName: String? = null

        // Query the content resolver to get the file's metadata
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }

        return fileName
    }

    private fun uploadFile(fileUri: Uri,fileName:String) {
        val storageRef = storage.reference
        val fileRef = storageRef.child("${uploadUsername.text.toString()}/${uploadYear.text.toString()}/$fileName") // Customize the storage path

        val uploadTask = fileRef.putFile(fileUri)
        Toast.makeText(requireContext(), "File Uploading...", Toast.LENGTH_LONG).show()
        // Monitor upload progress and handle success/failure
        uploadTask.addOnSuccessListener {
            // File uploaded successfully
            Log.i("FirebaseStorage","File uploaded successfully")
            Toast.makeText(requireContext(), "File uploaded successfully", Toast.LENGTH_LONG).show()
            // Get download URL for the uploaded file
            fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                Log.i("FirebaseStorage", "Download URL: $downloadUri")
                // Use the downloadUri as needed
            }
        }.addOnFailureListener { exception ->
            // Handle unsuccessful uploads
            Log.i("FirebaseStorage", "Upload failed", exception)
            Toast.makeText(requireContext(), "Failed to download file: ${exception.message}", Toast.LENGTH_LONG).show()

        }
    }


}
